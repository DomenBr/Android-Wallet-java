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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


public class dashboardFragment extends Fragment {

    public GetData gd = new GetData();
    public RequestResult requestResult = new RequestResult();
    public String secret;
    User user = null;
    TextView textViewMyWallet, textViewMCO, textViewEUR;
    ListView transactions;
    List<Transaction> lastestTransacrions;

    Intent intent;
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        intent = activity.getIntent();
        Bundle bundle = intent.getExtras();
        secret = bundle.getString("secret");
    }

    public dashboardFragment()
    {
//        Intent intent =this.getActivity().getIntent();
//        Bundle bundle = intent.getExtras();
//        secret = bundle.getString("secret");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);

        final Activity activity = getActivity();

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        textViewMyWallet = (TextView)view.findViewById(R.id.textViewMyWallet);
        textViewMCO = (TextView)view.findViewById(R.id.textViewMCO);
        textViewEUR = (TextView)view.findViewById(R.id.textViewEUR);
        transactions = (ListView)view.findViewById(R.id.listViewDashboard);

        textViewMyWallet.setTextColor(Color.parseColor("#ffffff"));

        textViewMCO.setTextColor(Color.parseColor("#ffffff"));

        textViewEUR.setTextColor(Color.parseColor("#ffffff"));

        try {
            new MyTaskUser().execute(secret).get();
            new MyTaskTransactions().execute(secret).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //auto update
        Timer timer = new Timer();
        TimerTask update = new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new MyTaskUser().execute(secret).get();
                            new MyTaskTransactions().execute(secret).get();
                            System.out.println(secret);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        timer.schedule(update, 10000, 10000); //update.run()
        return view;
    }

    public void fillTexts()
    {
        if((user != null) && requestResult.success)
        {
            DecimalFormat dfC = new DecimalFormat("#.##");
            DecimalFormat dfE = new DecimalFormat("#.###");
            textViewMyWallet.setText(R.string.ApplicationName);
            textViewMCO.setText(dfC.format(Double.parseDouble(user.balanceCoins)) + " MCO", TextView.BufferType.NORMAL);
            textViewEUR.setText(dfE.format(Double.parseDouble(user.balanceEur)) + " €", TextView.BufferType.NORMAL);
        }
    }

    public void fillTrasactions()
    {
        if((lastestTransacrions != null) && requestResult.success)
        {
            transactions.setAdapter(new TransactionAdapter(getActivity(), lastestTransacrions));
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
                URL url = new URL("https://mw.coinsrace.com/api/user?secret=" + secret);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

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

            user = new Gson().fromJson(pageContent, User.class);
            fillTexts();
        }
    }

    private class MyTaskTransactions extends AsyncTask<String, Void, Void> {

        String pageContent = "";
        DataOutputStream wr;

        @Override
        public Void doInBackground(String... params) {

            requestResult.setSuccess(false);
            HttpURLConnection connection;

            try {
                //Create connection
                URL url = new URL("https://mw.coinsrace.com/api/transactions");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                String postData = "";

                postData += URLEncoder.encode("secret") + "=" + URLEncoder.encode(secret); //ko na novo iz nekje pokličem ta activity je secret prazen in zato NulllPointerException
                byte[] data = postData.getBytes(); // StandardCharsets.US_ASCII

                connection.setRequestProperty("Content-Length", data.length + "");
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoOutput(true);

                //Send request
                wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(postData);
                wr.close();

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
                    i++;
                }
                System.out.println(i);
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

            lastestTransacrions = new Gson().fromJson(pageContent, new TypeToken<List<Transaction>>(){}.getType());
            fillTrasactions();
        }
    }

}



class TransactionAdapter extends BaseAdapter
{
    List<Transaction> items;
    Activity context;
    public TransactionAdapter(Activity context, List<Transaction> items)
    {
        this.context = context;
        this.items = items;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public Object getItem(int position)
    {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if(view == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.listview_row, null);
        }
        TextView text = (TextView)view.findViewById(R.id.textViewAmountList);
        text.setText(items.get(position).amountMCO + " MCO");
        TextView text1 = (TextView)view.findViewById(R.id.textViewAddressList);
        text1.setText(items.get(position).transactionId);
        TextView text2 = (TextView)view.findViewById(R.id.textViewDateList);
        text2.setText(items.get(position).createdAt);

        if(items.get(position).type == "transfer")
        {
            ImageView imgV = (ImageView)view.findViewById(R.id.imageViewIconList);
            imgV.setImageResource(R.drawable.transaction_green);
        }
        else
        {
            ImageView imgV = (ImageView)view.findViewById(R.id.imageViewIconList);
            imgV.setImageResource(R.drawable.transaction_blue);
        }
        return view;

    }

}