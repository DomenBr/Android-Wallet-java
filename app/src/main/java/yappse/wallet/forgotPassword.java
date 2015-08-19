package yappse.wallet;

import android.app.Activity;
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


public class forgotPassword extends Activity {

    public GetData gd = new GetData();
    public RequestResult requestResult = new RequestResult();
    EditText username;
    Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        buttonSend = (Button)findViewById(R.id.buttonSend);
        username = (EditText)findViewById(R.id.inputUserName);
    }

    public void buttonClick(View v)
    {
        try {
            new MyTask().execute(username.getText().toString()).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void message()
    {
        if(requestResult.success == true)
        {
            Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Error while sendig email", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyTask extends AsyncTask<String, Void, Void> {

        String pageContent = "";

        @Override
        public Void doInBackground(String... params) {

            requestResult.setSuccess(false);
            HttpURLConnection connection;
            DataOutputStream wr;
            try {
                //Create connection
                URL url = new URL("https://mw.coinsrace.com/api/forgotten");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                String postData = "";

                postData += URLEncoder.encode("email") + "=" + URLEncoder.encode(params[0]);
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
                while ((line = rd.readLine()) != null) { //read to end
                    response.append(line);
                    System.out.println(line);
                    i++;
                }
                System.out.println(i);
                rd.close();
                pageContent = response.toString();
                if(pageContent == "OK")
                {
                    requestResult.setSuccess(true);
                }
                else
                {
                    requestResult.setSuccess(false);
                }

            } catch (Exception e) {
                e.printStackTrace();
                requestResult.setSuccess(false);
                requestResult.setMessage(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            message();
        }
    }
}
