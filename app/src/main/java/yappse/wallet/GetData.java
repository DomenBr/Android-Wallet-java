package yappse.wallet;


import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.List;

public class GetData extends AsyncTask<String, String, String>{

    RequestResult request;
    @Override
    public String doInBackground(String... params)
    {
        String pageContent = "";
        request.setSuccess(false);
        HttpURLConnection connection;
        DataOutputStream wr;
        try {
            //Create connection
            URL url = new URL("https://mw.coinsrace.com/api/auth");
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            String postData = "";

            postData += URLEncoder.encode("username") + "=" + URLEncoder.encode(params[0]) + "&";
            postData += URLEncoder.encode("password") + "=" + URLEncoder.encode(params[1]);
            byte[] data = postData.getBytes(); // StandardCharsets.US_ASCII

            connection.setRequestProperty("Content-Length", data.length+"");
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            wr = new DataOutputStream (connection.getOutputStream());
            wr.writeBytes(postData);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
            String s = "";
            String line;
            int i = 0;
            while((line = rd.readLine()) != null)
            {
                response.append(line);
                System.out.println(line);
                i++;
            }
            System.out.println(i);
            rd.close();
            pageContent = response.toString();

            request.setSuccess(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            request.setSuccess(false);
            request.setMessage(e.getMessage());
        }
        return pageContent;
    }



    public Authentication authentication(String username, String password, RequestResult result)
    {
        result.setSuccess(false);
        request = result;
        String page = doInBackground(username, password);

        Authentication auth = new Gson().fromJson(page, Authentication.class);
        return null;
    }



//    public Authentication authentication(String username, String password, RequestResult result)
//    {
//        Authentication auth = null;
//        try {
//            //string url = "http://46.101.20.34:1337/api/auth";
//            String url = "https://mw.coinsrace.com/api/auth";
//            // Create an HTTP web request using the URL:
//            URL obj = new URL(url);
//            HttpURLConnection request = (HttpURLConnection)obj.openConnection();
//            request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            request.setRequestMethod("POST");
//
//            String postData = "";
//
//            postData += URLEncoder.encode("username") + "=" + URLEncoder.encode(username) + "&";
//            postData += URLEncoder.encode("password") + "=" + URLEncoder.encode(password);
//
//            HttpURLConnection myHttpWebRequest = (HttpURLConnection)obj.openConnection();
//            myHttpWebRequest.setRequestMethod("POST");
//
//            byte[] data = postData.getBytes(); // StandardCharsets.US_ASCII
//            myHttpWebRequest.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            myHttpWebRequest.setRequestProperty("Content-Length", data.length + "");
//
//            request.setRequestProperty("Content-Length", data.length + "");
//            request.setUseCaches(false);
//            request.setDoOutput(true);
//
//            System.out.println("V metodi");
//            DataOutputStream wr = new DataOutputStream(request.getOutputStream());
//            wr.writeBytes(postData);
//            wr.close();
//
//            System.out.println("V metodi1");
//            InputStream is = request.getInputStream();
//            System.out.println("V metodi2");
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//            System.out.println("V metodi3");
//            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
//            String line;
//            while((line = rd.readLine()) != null) {
//                response.append(line);
//                response.append('\r');
//            }
//            rd.close();
//
//            String pageContent = response.toString();
//            System.out.println("V metodi4");
//
////            System.out.println("V metodi");
////            InputStream responseStream = (InputStream)myHttpWebRequest.getContent();
////            System.out.println("V metodi 2");
////            String encoding = myHttpWebRequest.getContentEncoding();
////            encoding = encoding == null ? "UTF-8" : encoding;
////            InputStreamReader myStreamReader = new InputStreamReader(responseStream, encoding);
////
////
////            BufferedReader br = new BufferedReader(myStreamReader);
//////            String pageContent = "";
//////            while((pageContent += br.readLine()) != null)  //Preveri delovanje kao read to end
//////            {
//////            }
////            StringBuilder response = new StringBuilder();
////            String line;
////            while((line = br.readLine()) != null)
////            {
////                response.append(line);
////            }
////
////
////            String pageContent = response.toString();
////            System.out.println("Pride do sem in pageContent = " + pageContent);
////
////            br.close();
////            responseStream.close();
////            myStreamReader.close();
//
////            GsonBuilder gsonb = new GsonBuilder();
////            Gson gson = gsonb.create();
////            auth = gson.fromJson(pageContent, Authentication.class);
////            result.setSuccess(true);
//            auth = new Gson().fromJson(pageContent, Authentication.class);
//        }
//        catch(Exception ex)
//        {
//            result.setSuccess(false);
//            result.setMessage(ex.getMessage());
//        }
//        return auth;
//    }



//    public Authentication authentication(String username, String password, RequestResult result)
//    {
//        Authentication auth = null;
//        HttpURLConnection connection;
//        connection = null;
//        try {
//            //Create connection
//            URL url = new URL("https://mw.coinsrace.com/api/auth");
//            connection = (HttpURLConnection)url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type",
//                    "application/x-www-form-urlencoded");
//
//            String postData = "";
//
//            postData += URLEncoder.encode("username") + "=" + URLEncoder.encode(username) + "&";
//            postData += URLEncoder.encode("password") + "=" + URLEncoder.encode(password);
//            byte[] data = postData.getBytes(); // StandardCharsets.US_ASCII
//
//            connection.setRequestProperty("Content-Length", data.length+"");
//            connection.setRequestProperty("Content-Language", "en-US");
//
//            connection.setUseCaches(false);
//            connection.setDoOutput(true);
//
//            //Send request
//            DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
//            wr.writeBytes(postData);
//            wr.close();
//
//            //Get Response
//            InputStream is = connection.getInputStream();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
//            String s = "";
//            String line;
//            int i = 0;
//            while((line = rd.readLine()) != null)
//            {
//                response.append(line);
//                System.out.println(line);
//                i++;
//            }
//            System.out.println(i);
//            rd.close();
//            String pageContent = response.toString();
//            auth = new Gson().fromJson(pageContent, Authentication.class);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        } finally {
//            if(connection != null) {
//                connection.disconnect();
//            }
//        }
//        return auth;
//    }

    public User GetUser(String secret, RequestResult result)
    {
        result.success = false;
        User user = null;
//        String resp;
//
//        try
//        {
//            //String url = "http://46.101.20.34:1337/api/user?secret=" + secret;
//            String url = "https://mw.coinsrace.com/api/user?secret=" + secret;
//
//            URL obj = new URL(url);
//            HttpURLConnection request = (HttpURLConnection)obj.openConnection();
//            request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            request.setRequestMethod("GET");
//
//            InputStream is = request.getInputStream();
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            StringBuilder response = new StringBuilder();
//            String line;
//            while((line = br.readLine()) != null)
//            {
//                response.append(line);
//                response.append('\r');
//            }
//
//            resp = response.toString();
//            user = new Gson().fromJson(resp, User.class); //preveri
//
//            br.close();
//
//        }
//        catch(Exception ex)
//        {
//            result.success = false;
//            result.message = ex.getMessage();
//        }

        return user;
    }

    public List<Transaction> getTransactionsHistory(String secret, RequestResult result)
    {
        result.success = false;
        List<Transaction> transactionsHistory = null;
        try
        {
            String url = "https://mw.coinsrace.com/api/transactions";

        }
        catch (Exception e)
        {

        }

        return transactionsHistory;
    }

    public List<Rate> GetRates(RequestResult result)
    {
        String rate = "";
        result.setSuccess(false);
        List<Rate> rates = null;

        return rates;
    }

}

class RequestResult
{
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean success;
    //error message
    public String message;
}


