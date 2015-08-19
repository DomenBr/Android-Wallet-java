package yappse.wallet;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;


public class exchangeMco extends Activity {

    public String secret;
    public GetData gd;
    public RequestResult requestResult = new RequestResult();
    ExchangeResponse exchangeResponse = null;
    EditText amount;
    String amountStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setIcon(R.drawable.exchange_icon_white_small);
        setContentView(R.layout.activity_exchange_mco);

        Intent callingIntent = getIntent();
        if(callingIntent != null)
        {
            Bundle callBundle = callingIntent.getExtras();
            if(callBundle != null)
            {
                secret = callBundle.getString("secret");
            }
            else
            {
                secret = "empty";
            }
        }

        amount = (EditText)findViewById(R.id.inputValueMco);
        Button exchange = (Button)findViewById(R.id.buttonExchangeMco);

        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float amountF;

                if (tryParseFloat(amount.getText().toString())) {
                    amountStr = amount.getText().toString();
                    try {
                        new MyTaskExchange().execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Amount value", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void exchangeEUR()
    {
        if((exchangeResponse != null) && requestResult.success)
        {
            Toast.makeText(this, "Status: " + exchangeResponse.status, Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this, "Status: " + exchangeResponse.status, Toast.LENGTH_LONG).show();
        }
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

    private class MyTaskExchange extends AsyncTask<String, Void, Void> {

        String pageContent = "";


        @Override
        public Void doInBackground(String... params) {

            requestResult.setSuccess(false);
            DataOutputStream wr;
            HttpURLConnection connection;

            try {
                //Create connection
                URL url = new URL("https://mw.coinsrace.com/api/exchange");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                String postData = "";

                postData += URLEncoder.encode("secret") + "=" + URLEncoder.encode(secret) + "&"; //ko na novo iz nekje pokličem ta activity je secret prazen in zato NulllPointerException
                postData += URLEncoder.encode("address") + "=" + URLEncoder.encode("MCO") + "&";
                postData += URLEncoder.encode("amount") + "=" + URLEncoder.encode(amountStr);
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
            exchangeResponse = new Gson().fromJson(pageContent, ExchangeResponse.class);
            exchangeEUR();
        }
    }
}
