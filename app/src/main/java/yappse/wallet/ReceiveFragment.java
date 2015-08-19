package yappse.wallet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.*;
import com.google.zxing.common.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.util.concurrent.ExecutionException;


public class ReceiveFragment extends Fragment {

    public GetData gd = new GetData();
    public RequestResult requestResult = new RequestResult();
    public String secret;
    public User user;

    ImageView qrpic;
    TextView address;

    Intent intent;
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        intent = activity.getIntent();
        Bundle bundle = intent.getExtras();
        secret = bundle.getString("secret");
    }

    public ReceiveFragment()
    {
//        Intent intent = getActivity().getIntent();
//        Bundle bundle = intent.getExtras();
//        secret = bundle.getString("secret");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { //Receive
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_receive, container, false);

        qrpic = (ImageView)view.findViewById(R.id.imageViewReceiveByQRPic);
        address = (TextView)view.findViewById(R.id.textViewAddressText);

        try {
            new MyTaskUser().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return view;
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
            if(user != null && requestResult.success)
            {
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix bitMatrix = null;
                try
                {
                    bitMatrix = writer.encode(user.username, BarcodeFormat.QR_CODE, 500, 500);
                    Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
                    for(int i = 0; i < 500; i++)
                    {
                        for(int j = 0; j < 500; j++)
                        {
                            bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK: Color.WHITE);
                        }
                    }
                    if(bitmap != null)
                    {
                        qrpic.setImageBitmap(bitmap);
                        address.setText(user.username);
                    }
                }
                catch (Exception ex)
                {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
}
