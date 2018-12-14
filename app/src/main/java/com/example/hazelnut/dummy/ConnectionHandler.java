package com.example.hazelnut.dummy;

import android.os.AsyncTask;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionHandler extends AsyncTask<String , Void ,String> {

    @Override
    protected String doInBackground(String... strings) {

        String result = "";
        int code = 200;
        try {
            URL siteURL = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(100);
            connection.connect();

            code = connection.getResponseCode();
            if (code == 200) {
                result = "-> AVAILABLE <-\t" + "Code: " + code;
                ;
            } else {
                result = "-> REDIRECTED <-\t" + "Code: " + code;
            }
        } catch (Exception e) {
            result = "-> NOT AVAILABLE:  <-\t" + e.getMessage();

        }
        System.out.println(strings[0] + "\t\tStatus:" + result);
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}

