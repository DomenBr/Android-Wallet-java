package yappse.wallet;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity {


    static String Tag = "ActionBarTabsSupport";
    Fragment[] _fragments;

    //for accessing data from WS
    public GetData gd = new GetData();
    public User user;
    //request result and error messeging
    public RequestResult requestResult = new RequestResult();

    //menu
    DrawerLayout mDrawerLayout;
    ArrayList<String> mLeftItems = new ArrayList<>();

    ListView mLeftDrawer;
    ActionBarDrawerToggle myDrawerToggle;

    ListAdapter mLeftAdapter;

    //added to send secret to settings activity
    private String secret = "";

    EditText address;
    TextView textViewName, textViewEmail;

    ActionBar ab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ab = getActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setContentView(R.layout.activity_main);

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

        mDrawerLayout = (DrawerLayout) findViewById(R.id.myDrawer);
        mLeftDrawer = (ListView) findViewById(R.id.leftListView);

        textViewName = (TextView) findViewById(R.id.TextViewName);
        textViewEmail = (TextView) findViewById(R.id.TextViewEmail);


        //get user data
        //User user = gd.GetUser(secret, requestResult);
        try {
            new MyTaskUser().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        //menu items
        mLeftItems.add("ACCOUNT");
        mLeftItems.add("My Wallet");
        mLeftItems.add("Exchange");
        mLeftItems.add("Settings");
        mLeftItems.add("MCOIN");
        mLeftItems.add("Receive");
        mLeftItems.add("Send");
        mLeftItems.add("APP");
        mLeftItems.add("Logout");

        myDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.menu_icon, R.string.open_drawer, R.string.close_drawer);
        mLeftAdapter = new MenuAdapter(this, mLeftItems.toArray());
        mLeftDrawer.setAdapter(mLeftAdapter);
        mLeftDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int tab = ab.getSelectedNavigationIndex();
                int index = position;

                TextView text = (TextView) parent.findViewWithTag("1");
                text.setTextColor(Color.BLACK);
                text = (TextView) parent.findViewWithTag("2");
                text.setTextColor(Color.BLACK);
                text = (TextView) parent.findViewWithTag("3");
                text.setTextColor(Color.BLACK);
                text = (TextView) parent.findViewWithTag("4");
                text.setTextColor(Color.BLACK);
                text = (TextView) parent.findViewWithTag("5");
                text.setTextColor(Color.BLACK);

                if (position == 1) {
                    ab.setSelectedNavigationItem(0);
                    text = (TextView) parent.findViewWithTag("1");
                    text.setTextColor(Color.parseColor("#0099FF"));
                }
                if (position == 2) {
                    ab.setSelectedNavigationItem(3);
                    text = (TextView) parent.findViewWithTag("2");
                    text.setTextColor(Color.parseColor("#0099FF"));
                }
                if (position == 3) {
                    Bundle bun = new Bundle();
                    bun.putString("secret", secret);
                    Intent intent = new Intent(getApplication(), settingsActivity.class);
                    intent.putExtras(bun);
                    startActivity(intent);
                }
                if (index == 5) {
                    ab.setSelectedNavigationItem(2);
                    text = (TextView) parent.findViewWithTag("4");
                    text.setTextColor(Color.parseColor("#0099FF"));
                }
                if (index == 6) {
                    ab.setSelectedNavigationItem(1);
                    text = (TextView) parent.findViewWithTag("5");
                    text.setTextColor(Color.parseColor("#0099FF"));
                }
                if (index == 8) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.apply();

                    Intent intentLogin = new Intent(getApplicationContext(), login.class);
                    startActivity(intentLogin);

                }
            }
        });


        mDrawerLayout.setDrawerListener(myDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setIcon(R.drawable.co_icon4);



        //tabs setup
        Bundle bundle = new Bundle();
        bundle.putString("secret", secret);

        _fragments = new Fragment[] {
                new dashboardFragment(),
                new sendFragment(),
                new ReceiveFragment(),
                new ExchangeFragment(),
                new allTransactionFragment(),
                new SettingsFragment()
        };

        for(Fragment f : _fragments)
        {
            f.setArguments(bundle);
        }

        AddDashboardTabToActionBar(R.layout.dashboard_tab, R.string.dashboard_tab_label, R.drawable.tab_dashboard_pic, ab);
        AddSendTabToActionBar(R.layout.send_tab, R.string.send_tab_label, R.drawable.tab_send_pic, ab);
        AddReceiveTabToActionBar(R.layout.receive_tab, R.string.receive_tab_label, R.drawable.tab_receive_pic, ab);
        AddExchangeTabToActionBar(R.layout.exchange_tab, R.string.exchange_tab_label, R.drawable.tab_exchange_pic, ab);
        AddAllTransactionsTabToActionBar(R.layout.alltransaction_tab, R.string.allTransactions_tab_label, R.drawable.tab_alltransactions_pic, ab);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
                {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
                else
                {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        myDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        myDrawerToggle.onConfigurationChanged(newConfig);
    }

    void AddDashboardTabToActionBar(int view, int labelResourceId, int iconResourceId, ActionBar ab)
    {
        ActionBar.Tab tab = ab.newTab();
        tab.setText(labelResourceId);
        tab.setCustomView(view);
        tab.setIcon(iconResourceId);

        tab.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                System.out.println("Dashboard");
                TabOnTabSelected(tab);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        });
        ab.addTab(tab);

        String title = "Dashboard";
        TextView text = (TextView)findViewById(R.id.tab_dashboard_title);
        text.setText(title);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
    }
    void AddSendTabToActionBar(int view, int labelResourceId, int iconResourceId, ActionBar ab)
    {
        ActionBar.Tab tab = ab.newTab();
        tab.setText(labelResourceId);
        tab.setCustomView(view);
        tab.setIcon(iconResourceId);

        tab.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                System.out.println("Send");
                TabOnTabSelected(tab);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        });
        ab.addTab(tab);

        String title = "Send";
        TextView text = (TextView)findViewById(R.id.tab_send_title);
        text.setText(title);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
    }
    void AddReceiveTabToActionBar(int view, int labelResourceId, int iconResourceId, ActionBar ab)
    {
        ActionBar.Tab tab = ab.newTab();
        tab.setText(labelResourceId);
        tab.setCustomView(view);
        tab.setIcon(iconResourceId);

        tab.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                System.out.println("Receive");
                TabOnTabSelected(tab);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        });
        ab.addTab(tab);

        String title = "Receive";
        TextView text = (TextView)findViewById(R.id.tab_receive_title);
        text.setText(title);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
    }
    void AddExchangeTabToActionBar(int view, int labelResourceId, int iconResourceId, ActionBar ab)
    {
        ActionBar.Tab tab = ab.newTab();
        tab.setText(labelResourceId);
        tab.setCustomView(view);
        tab.setIcon(iconResourceId);

        tab.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                System.out.println("Exchange");
                TabOnTabSelected(tab);

            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        });
        ab.addTab(tab);

        String title = "Exchange";
        TextView text = (TextView)findViewById(R.id.tab_exchange_title);
        text.setText(title);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
    }
    void AddAllTransactionsTabToActionBar(int view, int labelResourceId, int iconResourceId, ActionBar ab)
    {
        ActionBar.Tab tab = ab.newTab();
        tab.setText(labelResourceId);
        tab.setCustomView(view);
        tab.setIcon(iconResourceId);

        tab.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                System.out.println("All transaction");
                TabOnTabSelected(tab);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        });
        ab.addTab(tab);

        String title = "All Transactions";
        TextView text = (TextView)findViewById(R.id.tab_allTransactions_title);
        text.setText(title);
        text.setTextColor(ColorStateList.valueOf(Color.WHITE));
    }

    public void TabOnTabSelected(ActionBar.Tab tab)
    {
        Log.d(Tag, "The tab " + tab.getPosition() + " has been selected+");
        if(tab.getPosition() == 1)
        {
            OnTabSelectedChangeMenuTextColor();
            TextView text = (TextView)mLeftDrawer.findViewWithTag("5");
            text.setTextColor(Color.parseColor("#0099FF"));
        }
        else if(tab.getPosition() == 2)
        {
            OnTabSelectedChangeMenuTextColor();
            TextView text = (TextView)mLeftDrawer.findViewWithTag("4");
            text.setTextColor(Color.parseColor("#0099FF"));
        }
        else if(tab.getPosition() == 3)
        {
            OnTabSelectedChangeMenuTextColor();
            TextView text = (TextView)mLeftDrawer.findViewWithTag("2");
            text.setTextColor(Color.parseColor("#0099FF"));
        }
        else if(tab.getPosition() == 4)
        {
            OnTabSelectedChangeMenuTextColor();
        }
        Fragment frag = _fragments[tab.getPosition()];
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.frameLayout1, frag).commit();
    }

    void OnTabSelectedChangeMenuTextColor()
    {
        TextView text = (TextView)mLeftDrawer.findViewWithTag("1");
        text.setTextColor(Color.BLACK);
        TextView text1 = (TextView)mLeftDrawer.findViewWithTag("2");
        text1.setTextColor(Color.BLACK);
        TextView text2 = (TextView)mLeftDrawer.findViewWithTag("3");
        text2.setTextColor(Color.BLACK);
        TextView text3 = (TextView)mLeftDrawer.findViewWithTag("4");
        text3.setTextColor(Color.BLACK);
        TextView text4 = (TextView)mLeftDrawer.findViewWithTag("5");
        text4.setTextColor(Color.BLACK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        System.out.println("Rezultati");
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            if(scanResult.getContents() != null) {
                Fragment f = new sendFragment();
                address = (EditText) findViewById(R.id.editTextAddress);
                address.setText(scanResult.getContents());
                Toast.makeText(this, "Found Address: " + scanResult.getContents(), Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this, "Scanning Canceled!", Toast.LENGTH_LONG).show();
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
                textViewName.setText(user.fullname);
                textViewEmail.setText(user.email);
            }
        }
    }

}

class MenuAdapter extends BaseAdapter
{
    Object[] items;
    Context context;
    public MenuAdapter(Context context, Object[] items)
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
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    public int Count;

    public  View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(position == 0)
        {
            view = mInflater.inflate(R.layout.listview_menu_separator, null);
            TextView t = (TextView)view.findViewById(R.id.menu_separator);
            t.setText(items[position].toString());
        }
        else if(position == 1)
        {
            view = mInflater.inflate(R.layout.listview_menu, null);
            ImageView im = (ImageView)view.findViewById(R.id.imageViewIconList);
            im.setImageResource(R.drawable.menu_1);

            TextView t = (TextView)view.findViewById(R.id.textViewAmountList);
            t.setText(items[position].toString());
            t.setTextColor(Color.parseColor("#0099FF"));
            t.setTag("1");
        }
        else if(position == 2)
        {
            view = mInflater.inflate(R.layout.listview_menu, null);
            ImageView im = (ImageView)view.findViewById(R.id.imageViewIconList);
            im.setImageResource(R.drawable.menu_2);

            TextView t = (TextView)view.findViewById(R.id.textViewAmountList);
            t.setText(items[position].toString());
            t.setTag("2");
        }
        else if(position == 3)
        {
            view = mInflater.inflate(R.layout.listview_menu, null);
            ImageView im = (ImageView)view.findViewById(R.id.imageViewIconList);
            im.setImageResource(R.drawable.menu_3);

            TextView t = (TextView)view.findViewById(R.id.textViewAmountList);
            t.setText(items[position].toString());
            t.setTag("3");
        }
        else if(position == 4)
        {
            view = mInflater.inflate(R.layout.listview_menu_separator, null);
            TextView t = (TextView) view.findViewById(R.id.menu_separator);
            t.setText(items[position].toString());
        }
        else if(position == 5)
        {
            view = mInflater.inflate(R.layout.listview_menu, null);
            ImageView im = (ImageView)view.findViewById(R.id.imageViewIconList);
            im.setImageResource(R.drawable.menu_4);

            TextView t = (TextView)view.findViewById(R.id.textViewAmountList);
            t.setText(items[position].toString());
            t.setTag("4");
        }
        else if(position == 6)
        {
            view = mInflater.inflate(R.layout.listview_menu, null);
            ImageView im = (ImageView)view.findViewById(R.id.imageViewIconList);
            im.setImageResource(R.drawable.menu_5);

            TextView t = (TextView)view.findViewById(R.id.textViewAmountList);
            t.setText(items[position].toString());
            t.setTag("5");
        }
        else if(position == 7)
        {
            view = mInflater.inflate(R.layout.listview_menu_separator, null);
            TextView t = (TextView) view.findViewById(R.id.menu_separator);
            t.setText(items[position].toString());
        }
        else if(position == 8)
        {
            view = mInflater.inflate(R.layout.listview_menu, null);
            ImageView im = (ImageView)view.findViewById(R.id.imageViewIconList);
            im.setImageResource(R.drawable.menu_6);

            TextView t = (TextView)view.findViewById(R.id.textViewAmountList);
            t.setText(items[position].toString());
        }
        return view;
    }

}
