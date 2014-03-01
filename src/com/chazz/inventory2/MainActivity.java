package com.chazz.inventory2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.webkit.*;
import android.widget.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MainActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private WebView web;
    private ProgressBar progressBar,loading;

    private String inventoryUrl = "http://localhost/ingress/index.php";
    private String getAppVersionUri = "getappversion.php";
    private String handshakeUrl = "https://m-dot-betaspike.appspot.com/handshake?json=";
    private String rpcUrl = "https://m-dot-betaspike.appspot.com/rpc/";
    private String betaspikeDomain = "m-dot-betaspike.appspot.com";
    private ArrayList<ListItem> menuItems;
    private String sacsid;
    private String version;
    private Map<String,String> addHeaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isDebuggable =  ( 0 != ( getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );

//        if(isDebuggable){
//            inventoryUrl = "http://localhost/ingress/";
//        }

        addHeaders  = new HashMap<String, String>();
        addHeaders.put("Connection","keep-alive");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
              setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_gb);

            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            loading = (ProgressBar) findViewById(R.id.loadingBar);



            web = (WebView) findViewById(R.id.webView);
            web.setWebChromeClient(new MyChromeClient());
            web.setWebViewClient(new MyWebViewClient());
            web.getSettings().setJavaScriptEnabled(true);
            web.loadUrl(inventoryUrl,addHeaders);
        }





        if (android.os.Build.VERSION.SDK_INT >= 11) {
            initializeDrawer();
        }

    }
    @SuppressLint("NewApi")
    protected void initializeDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        MyCustomAdapter ad = new MyCustomAdapter();
        ad.addItem(new ListItem(getString(R.string.menu_home)));
        ad.addItem(new ListItem(getString(R.string.menu_home),"index.php"));
        ad.addItem(new ListItem(getString(R.string.menu_badge),"index.php?page=badges"));
        ad.addItem(new ListItem(getString(R.string.menu_res),"index.php?page=resonators"));
        ad.addItem(new ListItem(getString(R.string.menu_burster),"index.php?page=weapons"));
        ad.addItem(new ListItem(getString(R.string.menu_mods),"index.php?page=mods"));
        ad.addItem(new ListItem(getString(R.string.menu_cubes),"index.php?page=cubes"));
        ad.addItem(new ListItem(getString(R.string.menu_media),"index.php?page=media"));
        ad.addItem(new ListItem(getString(R.string.menu_keys)));
        ad.addItem(new ListItem(getString(R.string.menu_keys_list),"index.php?page=keys&view=list"));
        ad.addItem(new ListItem(getString(R.string.menu_keys_map),"index.php?page=keys&view=map"));
        ad.addItem(new ListItem(getString(R.string.menu_tools)));
        ad.addItem(new ListItem(getString(R.string.menu_passcode),"index.php?page=tools&tool=redeem"));
        ad.addItem(new ListItem(getString(R.string.menu_recycle),"index.php?page=tools&tool=recycle"));
        ad.addItem(new ListItem(getString(R.string.menu_agent),"index.php?page=tools&tool=agent"));
        ad.addItem(new ListItem(getString(R.string.menu_score),"index.php?page=tools&tool=score"));
        ad.addItem(new ListItem(getString(R.string.menu_other)));
        ad.addItem(new ListItem(getString(R.string.menu_auth),getAppVersionUri));
        ad.addItem(new ListItem(getString(R.string.menu_logout),"logout"));


        menuItems = ad.getMenuItems();


        mDrawerList.setAdapter(ad);

        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View view, int position,  long id) {

                    selectItem(position);

            }
        });


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (android.os.Build.VERSION.SDK_INT >= 11)
                    invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (android.os.Build.VERSION.SDK_INT >= 11)
                    invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);


        Fragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(WebFragment.ARG_MENU_TITLE, getString(R.string.app_name));
        args.putString(WebFragment.ARG_MENU_URI, "index.php");
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();



            getActionBar().setDisplayHomeAsUpEnabled(true);

        if (android.os.Build.VERSION.SDK_INT >= 14)
            getActionBar().setHomeButtonEnabled(true);
    }


    @Override
    public void onBackPressed() {
        if (web.isFocused() && web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 11)
            mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        if (android.os.Build.VERSION.SDK_INT >= 11)
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            if (mDrawerToggle.onOptionsItemSelected(item))
                return true;
        } else  {
            switch (item.getItemId()) {
                case R.id.action_home:
                    web.loadUrl(inventoryUrl,addHeaders);
                    break;
                case R.id.action_auth:
                    login();
                    break;
                case R.id.action_logout:
                    CookieManager.getInstance().removeAllCookie();

                    web.loadUrl(inventoryUrl,addHeaders);
                    break;
            }

        }
        return super.onOptionsItemSelected(item);
    }


    private void selectItem(int position) {
        mDrawerList.setItemChecked(position, true);
        if(getAppVersionUri.equals(menuItems.get(position).getUri())) {
            login();
        } else if(menuItems.get(position).getUri().equals("logout")){
            CookieManager.getInstance().removeAllCookie();
            setTitle(getString(R.string.app_name));
            web.loadUrl(inventoryUrl,addHeaders);
        } else {
            setTitle(menuItems.get(position).getTitle());
            web.loadUrl(inventoryUrl+menuItems.get(position).getUri(),addHeaders);
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @SuppressLint("NewApi")
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (android.os.Build.VERSION.SDK_INT >= 11)
            getActionBar().setTitle(mTitle);
    }

    public String getCookie(String siteName,String CookieName){
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        String[] temp=cookies.split("[;]");
        for (String ar1 : temp ){
            if(ar1.contains(CookieName)){
                String[] temp1=ar1.split("[=]");
                CookieValue = temp1[1];
            }
        }
        return CookieValue;
    }
    public class WebFragment extends Fragment {
        public static final String ARG_MENU_TITLE = "menu_title";
        public static final String ARG_MENU_URI = "menu_uri";

        public WebFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_web, container, false);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            loading = (ProgressBar) rootView.findViewById(R.id.loadingBar);
            String title, uri;

            web = (WebView) rootView.findViewById(R.id.webView);
            web.setWebChromeClient(new MyChromeClient());
            web.setWebViewClient(new MyWebViewClient());
            web.getSettings().setJavaScriptEnabled(true);
            web.getSettings().setGeolocationEnabled(true);
            web.getSettings().setBuiltInZoomControls(true);

            title = getArguments().getString(ARG_MENU_TITLE);
            uri = getArguments().getString(ARG_MENU_URI);
            web.loadUrl(inventoryUrl+uri,addHeaders);
            getActivity().setTitle(title);
            return rootView;
        }
    }

    private void login(){
        AsyncTask as =  new GetVersion().execute(inventoryUrl+getAppVersionUri);
        try {
            version = String.valueOf(as.get());
            web.loadUrl(handshakeUrl+version);
        } catch (InterruptedException ex){
            System.out.println(ex.getMessage());
        } catch (ExecutionException ex){
            System.out.println(  ex.getMessage());

        }
    }
    public class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);

//            if(url.indexOf(betaspikeDomain) != -1){
//                try{
//                sacsid = getCookie("https://"+betaspikeDomain,"SACSID");
//
//                    if(sacsid.equals(null)){
//                       login();
//                    }
//                } catch (NullPointerException ex){
//                   login();
//                }
//            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            if(web.getVisibility() == View.GONE) {
                web.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
            }
            if(url.indexOf(betaspikeDomain) != -1){
                web.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                try {
                    sacsid = getCookie("https://"+betaspikeDomain,"SACSID");
                    web.loadUrl(inventoryUrl+"auth.php?SACSID="+sacsid,addHeaders);

                } catch (NullPointerException ex){
                    web.loadUrl(inventoryUrl,addHeaders);
                }
            }
            if(url.equals(inventoryUrl+"index.php?page=reauth")){
                login();

            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            if(errorCode == ERROR_TIMEOUT){

            }
            if(errorCode == ERROR_FILE_NOT_FOUND){

            }
        }
    }

    public class MyChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);


        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin,true,false);
        }



    }


    private class MyCustomAdapter extends BaseAdapter {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

        private ArrayList mData = new ArrayList<ListItem>();
        private LayoutInflater mInflater;




        public MyCustomAdapter() {
            mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        public void addItem(final ListItem item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        public ArrayList<ListItem> getMenuItems(){
            return mData;
        }


        @Override
        public int getItemViewType(int position) {
            ListItem itm = (ListItem) (mData.get(position));
            return itm.isSeparator() ? TYPE_SEPARATOR : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            ListItem itm = (ListItem) (mData.get(position));
            return itm.getTitle();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public boolean isEnabled(int position) {
            ListItem itm = (ListItem) (mData.get(position));
            return  itm.isEnabled();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (android.os.Build.VERSION.SDK_INT < 11) return  convertView;

            ViewHolder holder = null;
            int type = getItemViewType(position);
            ListItem itm = (ListItem) mData.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                switch (type) {
                    case TYPE_ITEM:
                        convertView = mInflater.inflate(R.layout.drawer_list_item, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.list_item);
                        break;
                    case TYPE_SEPARATOR:
                        convertView = mInflater.inflate(R.layout.drawer_header, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.header_item);
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.textView.setText(itm.getTitle());
            return convertView;
        }

    }
    public static class ViewHolder {
        public TextView textView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (android.os.Build.VERSION.SDK_INT < 11)
            getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
