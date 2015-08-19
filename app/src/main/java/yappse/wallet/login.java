package yappse.wallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;


public class login extends Activity {

    public GetData gd = new GetData();
    public RequestResult requestResult = new RequestResult();
    EditText username;
    EditText password;

    SharedPreferences prefs;
    boolean remeberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
        LinearLayout keepLogin = (LinearLayout) findViewById(R.id.keep_logged_in);
        LinearLayout recover = (LinearLayout) findViewById(R.id.forgot_password);
        username = (EditText) findViewById(R.id.inputUserName);
        password = (EditText) findViewById(R.id.inputPassword);

        //check if credentials saved (keep me logged in)
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        if(isLoggedIn)
        {
            String usr = prefs.getString("usr", "");
            String pass = prefs.getString("pass", "");
            new MyTask().execute(usr, pass);
//            if((auth != null) && requestResult.success)
//            {
//                //if Authentication is successful, move to the next screen
//                if(auth.authenticated)
//                {
//                    Intent intent = new Intent(this, MainActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("secret", auth.secret);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                }
//            }
        }
    }

    public void recover(View v)
    {
        Intent intent = new Intent(this,forgotPassword.class);
        startActivity(intent);
    }

    public void remeberMeClick(View v)
    {
        remeberMe = true;
    }

    //ProgressDialog dialog;
    Authentication auth = null;
    public void clickLogin(View v)
    {

        //Authentication auth = gd.authentication(username.getText().toString(), password.getText().toString(), requestResult);

        try {
            //dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
            new MyTask().execute(username.getText().toString(), password.getText().toString()).get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    public void login()
    {
        //dialog.dismiss();
        if((auth != null) && requestResult.success)
        {
            //if Authentication is successful, move to the next screen
            if(auth.authenticated)
            {
                if(remeberMe)
                {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("usr", username.getText().toString());
                    editor.putString("pass", password.getText().toString());
                    editor.apply();
                }

                Intent intent = new Intent(this, MainActivity.class);
                Bundle bundle = new Bundle();
                System.out.println(auth.authenticated + " --  " + auth.secret);
                bundle.putString("secret", auth.secret); //desni string popravi
                intent.putExtras(bundle);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(this, "Error while signing in", Toast.LENGTH_LONG).show();
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
                URL url = new URL("https://mw.coinsrace.com/api/auth");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                String postData = "";

                postData += URLEncoder.encode("username") + "=" + URLEncoder.encode(params[0]) + "&";
                postData += URLEncoder.encode("password") + "=" + URLEncoder.encode(params[1]);
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

            auth = new Gson().fromJson(pageContent, Authentication.class);
            login();
        }
    }
}


