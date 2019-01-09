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
                result = "1";
            } else {
                result = "2";
            }
        } catch (Exception e) {
            result = "0";

        }
        System.out.println(strings[0] + "\t\tStatus:" + result);
        if (result == "1"){
            return strings[0];
        }
        else
            return null;
    }

    protected void onPostExecute(String s) {
    }
}

