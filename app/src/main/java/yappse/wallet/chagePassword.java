package yappse.wallet;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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


public class chagePassword extends Activity {

    public GetData gd = new GetData();

    public RequestResult requestResult = new RequestResult();

    public String secret = "";

    EditText password, repeatPassword;
    Button changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chage_password);

        ActionBar ab = this.getActionBar();

        ab.setDisplayHomeAsUpEnabled(false);
        ab.setHomeButtonEnabled(true);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setIcon(R.drawable.settings_icon);

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

        password = (EditText)findViewById(R.id.inputPassword);
        repeatPassword = (EditText)findViewById(R.id.inputRepeatPassword);
        changePassword = (Button)findViewById(R.id.buttonChangePassword);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString() == null || password.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Password field is empty", Toast.LENGTH_SHORT).show();
                }
                else if(repeatPassword.getText().toString() == null || repeatPassword.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Confirm password field is empty", Toast.LENGTH_SHORT).show();
                }
                else if(!(password.getText().toString().equals(repeatPassword.getText().toString())))
                {
                    Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try {
                        new MyTaskChangePassword().execute(password.getText().toString(), repeatPassword.getText().toString()).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private class MyTaskChangePassword extends AsyncTask<String, Void, Void> {

        String pageContent = "";
        DataOutputStream wr;

        @Override
        public Void doInBackground(String... params) {

            requestResult.setSuccess(false);
            HttpURLConnection connection;

            try {
                //Create connection
                URL url = new URL("https://mw.coinsrace.com/api/changePass");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                String postData = "";

                postData += URLEncoder.encode("current") + "=" + URLEncoder.encode(params[0]) + "&";
                postData += URLEncoder.encode("new") + "=" + URLEncoder.encode(params[1]) + "&";//ko na novo iz nekje pokličem ta activity je secret prazen in zato NulllPointerException
                postData += URLEncoder.encode("confirm") + "=" + URLEncoder.encode(params[1]);
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

                if(pageContent.equals("OK"))
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
            if(requestResult.success == true)
            {
                Toast.makeText(getApplicationContext(), "Password changed", Toast.LENGTH_SHORT).show();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Error while updating password", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
