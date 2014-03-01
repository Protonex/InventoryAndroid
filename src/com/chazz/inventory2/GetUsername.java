package com.chazz.inventory2;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Created by dilkov on 2/15/14.
 */
public class GetUsername extends AsyncTask<Object,Void,String> {

    JSONObject obj = new JSONObject();

    @Override
    protected String doInBackground(Object[] params) {
        String url = (String) params[0];


        String sacsid = (String) params[1];

        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object
        HttpGet httpget = new HttpGet(url);




        httpget.setHeader("Content-Type","application/json;charset=UTF-8");
        httpget.setHeader("User-Agent","Nemesis (gzip)");
        httpget.setHeader("Host","m-dot-betaspike.appspot.com");
        httpget.setHeader("Connection","Keep-Alive");
        httpget.setHeader("Cookie","SACSID=" + sacsid+ ";path=/");
        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            // Examine the response status
            Log.i("Praeda", response.getStatusLine().toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream);

                instream.close();

                try {
                    obj = new JSONObject(result.replace("while(1);",""));
                       obj = obj.getJSONObject("result");

                    String username = obj.getString("nickname");

                    return (username);
                } catch (Exception ex){
                    return null;
                }



            }


        } catch (Exception e) {}


        return null;
    }
    private  String convertStreamToString(InputStream is) {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
