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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;


public class settingsActivity extends Activity {

    public User user;
    public RequestResult requestResult = new RequestResult();

    public String secret;

    EditText fullName, address, city, zip, phone;
    Button editButton, changePassbutton;

//    public settingsActivity(String n_secret)
//    {
//        secret = n_secret;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setIcon(R.drawable.settings_icon);
        setContentView(R.layout.activity_settings);

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

        fullName = (EditText)findViewById(R.id.inputFullName);
        address = (EditText)findViewById(R.id.inputAddress);
        city = (EditText)findViewById(R.id.inputCity);
        zip = (EditText)findViewById(R.id.inputZip);
        phone = (EditText)findViewById(R.id.inputPhone);
        editButton = (Button)findViewById(R.id.buttonEdit);
        changePassbutton = (Button)findViewById(R.id.buttonChange);

        try {
            new MyTaskUser().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((fullName.getText()==null)||fullName.getText().toString().equals(""))
                    Toast.makeText(getApplication(), "No full name", Toast.LENGTH_SHORT).show();
                else if ((address.getText() == null) || address.getText().toString().equals(""))
                    Toast.makeText(getApplication(), "No address", Toast.LENGTH_SHORT).show();
                else if ((city.getText() == null) || city.getText().toString().equals(""))
                    Toast.makeText(getApplication(), "No city", Toast.LENGTH_SHORT).show();
                else if ((zip.getText() == null) || zip.getText().toString().equals(""))
                    Toast.makeText(getApplication(), "No zip", Toast.LENGTH_SHORT).show();
                else if ((phone.getText() == null) || phone.getText().toString().equals(""))
                    Toast.makeText(getApplication(), "No phone", Toast.LENGTH_SHORT).show();
                else
                {
                    user.fullname=fullName.getText().toString();
                    user.address=address.getText().toString();
                    user.city=city.getText().toString();
                    user.zip=zip.getText().toString();
                    user.phone=phone.getText().toString();
                    try {
                        new MyTaskSettings().execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        changePassbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), chagePassword.class);
                Bundle bundle = new Bundle();
                bundle.putString("secret", secret);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
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
            if ((user != null) && requestResult.success)
            {
                if (user.fullname != null)
                    fullName.setText(user.fullname);
                if (user.address != null)
                    address.setText(user.address);
                if (user.city != null)
                    city.setText(user.city);
                if (user.zip != null)
                    zip.setText(user.zip);
                if (user.phone != null)
                    phone.setText(user.phone);
                //email.Text = user.email;
                //phone.Text = user.phone;
            }
        }
    }

    private class MyTaskSettings extends AsyncTask<String, Void, Void> {

        String pageContent = "";

        @Override
        public Void doInBackground(String... params) {

            requestResult.setSuccess(false);
            DataOutputStream wr;
            HttpURLConnection connection;

            try {
                //Create connection
                URL url = new URL("https://mw.coinsrace.com/api/settings");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                String postData = "";

                postData += URLEncoder.encode("secret") + "=" + URLEncoder.encode(secret) + "&";
                postData += URLEncoder.encode("accountId") + "=" + URLEncoder.encode(user.accountId) + "&";
                postData += URLEncoder.encode("active") + "=" + URLEncoder.encode(user.active + "") + "&";
                postData += URLEncoder.encode("address") + "=" + URLEncoder.encode(user.address) + "&";
                postData += URLEncoder.encode("avatar") + "=" + URLEncoder.encode(user.avatar + "") + "&";
                postData += URLEncoder.encode("balanceCoins") + "=" + URLEncoder.encode(user.balanceCoins) + "&";
                postData += URLEncoder.encode("balanceEur") + "=" + URLEncoder.encode(user.balanceEur) + "&";
                postData += URLEncoder.encode("balanceTokens") + "=" + URLEncoder.encode(user.balanceTokens) + "&";
                postData += URLEncoder.encode("bio") + "=" + URLEncoder.encode(user.bio + "") + "&";
                postData += URLEncoder.encode("city") + "=" + URLEncoder.encode(user.city) + "&";
                postData += URLEncoder.encode("coinsEscrow") + "=" + URLEncoder.encode(user.coinsEscrow) + "&";
                postData += URLEncoder.encode("country") + "=" + URLEncoder.encode(user.country) + "&";
                postData += URLEncoder.encode("createdAt") + "=" + URLEncoder.encode(user.createdAt + "") + "&";
                postData += URLEncoder.encode("crxunits") + "=" + URLEncoder.encode(user.crxunits + "") + "&";
                postData += URLEncoder.encode("dob") + "=" + URLEncoder.encode(user.dob + "") + "&";
                postData += URLEncoder.encode("email") + "=" + URLEncoder.encode(user.email) + "&";
                postData += URLEncoder.encode("fullname") + "=" + URLEncoder.encode(user.fullname) + "&";
                postData += URLEncoder.encode("gender") + "=" + URLEncoder.encode(user.gender + "") + "&";
                postData += URLEncoder.encode("identify") + "=" + URLEncoder.encode(user.identify + "") + "&";
                postData += URLEncoder.encode("level") + "=" + URLEncoder.encode(user.level + "") + "&";
                postData += URLEncoder.encode("package") + "=" + URLEncoder.encode(user._package + "");
                postData += URLEncoder.encode("phone") + "=" + URLEncoder.encode(user.phone) + "&";
                postData += URLEncoder.encode("refererId") + "=" + URLEncoder.encode(user.refererId + "") + "&";
                postData += URLEncoder.encode("status") + "=" + URLEncoder.encode(user.status  + "") + "&";
                postData += URLEncoder.encode("tokensAvailable") + "=" + URLEncoder.encode(user.tokensAvailable + "") + "&";
                postData += URLEncoder.encode("tokensEscrow") + "=" + URLEncoder.encode(user.tokensEscrow + "") + "&";
                postData += URLEncoder.encode("updatedAt") + "=" + URLEncoder.encode(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())) + "&";
                postData += URLEncoder.encode("username") + "=" + URLEncoder.encode(user.username) + "&";
                postData += URLEncoder.encode("zip") + "=" + URLEncoder.encode(user.zip) + "&";
                postData += URLEncoder.encode("_id") + "=" + URLEncoder.encode(user._id + "");
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

                UpdateProfileResponse updated = new Gson().fromJson(pageContent, UpdateProfileResponse.class);
                if(updated.updated)
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

            if(requestResult.success==true)
                Toast.makeText(getApplication(), "Profile updated", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplication(), "Error while updating profile", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplication(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("secret", secret);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
