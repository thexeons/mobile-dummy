package com.example.hazelnut.dummy;

import android.os.AsyncTask;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionHandler extends AsyncTask<String , Void ,Integer> {

    @Override
    protected Integer doInBackground(String... strings) {

        int result;
        int code = 200;
        try {
            URL siteURL = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(100);
            connection.connect();

            code = connection.getResponseCode();
            if (code == 200) {
                result = 1;

            } else {
                result = 2;
            }
        } catch (Exception e) {
            result = 0;

        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer s) {
        if (s == 1)
            super.onPostExecute(s);
        else
            System.out.println("------------------"+s);
    }
}

