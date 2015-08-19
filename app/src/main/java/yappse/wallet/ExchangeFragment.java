package yappse.wallet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ExchangeFragment extends Fragment {
    public GetData gd = new GetData();
    public RequestResult requestResult = new RequestResult();
    public String secret;

    List<Rate> rates = null;

    GraphView chart;
    FrameLayout frame;

    Intent intent;
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        intent = activity.getIntent();
        Bundle bundle = intent.getExtras();
        secret = bundle.getString("secret");
    }
    public  ExchangeFragment()
    {
//        Intent intent = getActivity().getIntent();
//        Bundle bundle = intent.getExtras();
//        secret = bundle.getString("secret");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);
        Activity act = this.getActivity();
        frame = (FrameLayout)view.findViewById(R.id.graph);
        ImageButton excEUR = (ImageButton)view.findViewById(R.id.imageButtonEurExchange);
        ImageButton excMCO = (ImageButton)view.findViewById(R.id.imageButtonMcoExchange);

        excEUR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), exchangeEur.class);
                Bundle bundle = new Bundle();
                bundle.putString("secret", secret);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        excMCO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), exchangeMco.class);
                Bundle bundle = new Bundle();
                bundle.putString("secret", secret);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //List<Rate> rates = gd.GetRates(requestResult);
        try {
            new MyTaskUser().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return view;
    }

    public void drawChart()
    {
        if((rates != null) && requestResult.success)
        {
            List<Float> dataF = new ArrayList<>();
            for(int i = 0; i < rates.size(); i++)
            {
                dataF.add(Float.parseFloat(rates.get(i).rate));
            }
            Object[] objects = dataF.toArray(); //to string array
            String[] dataString = new String[objects.length];
            for(int j = 0; j < objects.length; j++)
            {
                dataString[j] = objects[j].toString();
            }

            DataPoint[] dataPoints = new DataPoint[rates.size()];
            for(int i = 0; i < dataPoints.length; i++)
            {
                dataPoints[i] = new DataPoint(i, Double.parseDouble(rates.get(i).rate));
            }

            BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(dataPoints);
            series.setSpacing(25);
            series.setValuesOnTopSize(20.0f);
            chart = new GraphView(this.getActivity());
            chart.addSeries(series);

            series.setDrawValuesOnTop(true);
            series.setValuesOnTopColor(Color.WHITE);

            chart.getViewport().setScrollable(true);
            chart.getViewport().setScalable(true);

            chart.getViewport().setXAxisBoundsManual(true);
            chart.getViewport().setMinX(5.0);
            chart.getViewport().setMaxX(15.0);
            frame.addView(chart);
        }
    }

    private class MyTaskUser extends AsyncTask<String, Void, Void> {

        String pageContent = "";

        @Override
        public Void doInBackground(String... params) {

            requestResult.setSuccess(false);
            HttpURLConnection connection;

            try {
                //Create connection
                URL url = new URL("https://mw.coinsrace.com/api/rates");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type",
                        "application/json");

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
                String s = "";
                String line;
                int i = 0;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    //System.out.println(line);
                    //i++;
                }
                //System.out.println(i);
                rd.close();
                pageContent = response.toString();

                requestResult.setSuccess(true);
            } catch (Exception e) {
                e.printStackTrace();
                requestResult.setSuccess(false);
                requestResult.setMessage(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            rates = new Gson().fromJson(pageContent, new TypeToken<List<Rate>>() {
            }.getType());
            drawChart();
        }
    }

}
