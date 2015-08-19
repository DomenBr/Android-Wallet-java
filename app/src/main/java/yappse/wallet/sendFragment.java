package yappse.wallet;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class sendFragment extends Fragment {

    public GetData gd = new GetData();
    User user;
    public RequestResult requestResult = new RequestResult();
    public String secret;

    ImageButton scanQr;
    TextView textViewEUR;
    TextView textViewMCO;
    EditText address;
    EditText amount;
    Button sendButton;

    private ScrollView scroll;
    Timer t;
    SendMCOResponse sendMCOResponse;

    Intent intent;
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        intent = activity.getIntent();
        Bundle bundle = intent.getExtras();
        secret = bundle.getString("secret");
    }

    public sendFragment()
    {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_send, container, false);
        scroll = (ScrollView)view.findViewById(R.id.scroll_view_send);

        Activity act = this.getActivity();


        textViewEUR = (TextView) view.findViewById(R.id.textViewEUR);
        textViewMCO = (TextView) view.findViewById(R.id.textViewMCO);
        scanQr = (ImageButton) view.findViewById(R.id.imageButtonScanQR);
        address = (EditText) view.findViewById(R.id.editTextAddress);
        amount = (EditText) view.findViewById(R.id.editTextAmount);
        sendButton = (Button) view.findViewById(R.id.buttonSend);

        new MyTaskUser().execute();

        scanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IntentIntegrator intent = new IntentIntegrator(getActivity());
                    intent.setPrompt("Hold the camera up to the barcode\nAbout 6 inches away\n\nWait for the barcode to automatically scan");
                    intent.initiateScan();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((address.getText()) == null || address.getText().toString() == "")
                {
                    Toast.makeText(getActivity(), "No address", Toast.LENGTH_SHORT).show();
                }
                else if ((amount.getText() == null) || amount.getText().toString() == "")
                {
                    Toast.makeText(getActivity(), "No amount", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if((user != null) && requestResult.success)
                    {
                        float amountF;

                        if(tryParseFloat(amount.getText().toString()))
                        {
                            amountF = Float.parseFloat(amount.getText().toString());
                            try {
                                new MyTaskSendMCO().execute(secret, address.getText().toString(), amountF+"").get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Wrong amount value", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        final CountDownTimer timer;
        timer = new CountDownTimer(300, 300) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                scroll.scrollBy(0, 5000);

            }
        }.start();
        final TimerTask myTask = new TimerTask()
        {
            @Override
            public void run() {
                t.cancel();
                scroll.scrollBy(0, 5000);
            }
        };

        address.setOnFocusChangeListener(new View.OnFocusChangeListener() { // preveri delovanje
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                t = new Timer();
//                t.schedule(myTask, 0);
                timer.start();
                //scroll.scrollTo(0, 3000);
            }
        });

        amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                t = new Timer();
//                t.schedule(myTask, 0);
                timer.start();
               // scroll.scrollTo(0, 3000);
            }
        });

        return view;
    }

    public boolean tryParseFloat(String value)
    {
        try
        {
            Float.parseFloat(value);
            return true;
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
    }

    public void fillTexts()
    {
        if((user != null) && requestResult.success) {
            DecimalFormat dfC = new DecimalFormat("#.##");
            DecimalFormat dfE = new DecimalFormat("#.###");
            textViewMCO.setText(dfC.format(Double.parseDouble(user.balanceCoins)) + " MCO", TextView.BufferType.NORMAL);
            textViewEUR.setText(dfE.format(Double.parseDouble(user.balanceEur)) + " €", TextView.BufferType.NORMAL);
        }
    }

    public void sendMco()
    {
        if((sendMCOResponse != null) && requestResult.success)
        {
            if(sendMCOResponse.status.equals("1"))
            {
                Toast.makeText(getActivity(), "Transaction processed", Toast.LENGTH_LONG).show();
            }
            else if (sendMCOResponse.status.equals("-1"))
            {
                Toast.makeText(getActivity(), "Transaction canceled", Toast.LENGTH_LONG).show();
            }
            else if (sendMCOResponse.status.equals("0"))
            {
                Toast.makeText(getActivity(), "Transaction pending", Toast.LENGTH_LONG).show();
            }
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

            user = new Gson().fromJson(pageContent, User.class);
            fillTexts();
        }
    }

    private class MyTaskSendMCO extends AsyncTask<String, Void, Void> {

        String pageContent = "";
        DataOutputStream wr;

        @Override
        public Void doInBackground(String... params) {

            requestResult.setSuccess(false);
            HttpURLConnection connection;

            try {
                //Create connection
                URL url = new URL("https://mw.coinsrace.com/api/send");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                String postData = "";
                String amountStr = params[2].toString();

                postData += URLEncoder.encode("secret") + "=" + URLEncoder.encode(params[0]) + "&";
                postData += URLEncoder.encode("address") + "=" + URLEncoder.encode(params[1]) + "&";//ko na novo iz nekje pokličem ta activity je secret prazen in zato NulllPointerException
                postData += URLEncoder.encode("amount") + "=" + URLEncoder.encode(params[2]);
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
                    System.out.println(line);
                    i++;
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
            sendMCOResponse = new Gson().fromJson(pageContent, SendMCOResponse.class);
            sendMco();
        }
    }

}
