package com.example.hazelnut.dummy;

import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginDevActivity extends AppCompatActivity {

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    OkHttpClient client;
    Button loginButton, signupButton, devButton;
    EditText username, password, ipkampret;
    TextView registerText, resultText;
    //String[] m = new String[5];
    String ip,m1,m2,m3,m4,m5;
    String api = "/verifyLogin/";
    int notificationId = 1;

    final Context context = this;
    public static final String key = "TeddyGembelGante";
    public static final String initVector = "GembelTeddyGante";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.loginButton);
        devButton = findViewById(R.id.devBtn);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signupButton = findViewById(R.id.registerText);
        //registerText = findViewById(R.id.registerText);
        resultText= findViewById(R.id.result);
        //skipButton = findViewById(R.id.skip);

        Intent i = getIntent();
        String[] m = {i.getStringExtra("m1"),
                i.getStringExtra("m2"),
                i.getStringExtra("m3"),
                i.getStringExtra("m4"),
                i.getStringExtra("m5")};
        /*
        m[1] = i.getStringExtra("m1");
        m[2] = i.getStringExtra("m2");
        m[3] = i.getStringExtra("m3");
        m[4] = i.getStringExtra("m4");
        m[5] = i.getStringExtra("m5");
        */
        ip = i.getStringExtra("ip");
        new ConnectionHandler().execute("http://www.google.com");
        new ConnectionHandler().onPostExecute("test");


        resultText.setText(m[0]+m[1]+m[2]+m[3]+m[4]);
        String result;
        int code;
        int j;
        for (j=0 ; j<1 ; j++) {
            try {
                URL siteURL = new URL("http://" + m[j]);
                HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(100);
                connection.connect();

                code = connection.getResponseCode();
                if (code == 200) {
                    result = "Success";
                } else {
                    result = "Fail";
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = "Error";
            }
            System.out.println("http://" + m[j] + "-----" +result);
            if (result.equals("Success")) {
                ip = m[j];
                break;
            }

        }
        if(new ConnectionHandler().execute("Http://www.google.com").toString().endsWith("1")){
            System.out.println("-------------------------");
        }
        pinger("http://www.google.com");



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("------ini ip yang dipake"+ip);
                final String encryptedpassword = AESEncrypt(password.getText().toString());
                sendpost(username.getText().toString(),encryptedpassword);
            }});

        devButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pindah = new Intent(getApplicationContext(), DevActivity.class);
                pindah.putExtra("ip1", m1);
                pindah.putExtra("ip1", m2);
                pindah.putExtra("ip1", m3);
                pindah.putExtra("ip1", m4);
                pindah.putExtra("ip1", m5);
                pindah.putExtra("ip", ip);
                startActivity(pindah);
                finish();
            }
        });
        /*
        signupButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        password.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                    case MotionEvent.ACTION_UP: {
                        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        // Button is not pressed
                    }
                }
                return true;
            }
        });
        */


        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                System.out.println("------ini ip yang dipake"+ip);
                Intent pindah = new Intent(getApplicationContext(), RegisterActivity.class);
                pindah.putExtra("ip", ip);
                startActivity(pindah);
            }
        });


    }
    public static String AESEncrypt(String password){
        try{
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE,sKeySpec,iv);

            byte[] encrypted = cipher.doFinal(password.getBytes());
            String encoded = Base64.encodeToString(encrypted,Base64.DEFAULT);
            return encoded;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String pinger(String url){
        String result = "";
        int code = 200;

        try {
            URL siteURL = new URL(url);
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
            e.printStackTrace();
        }
        System.out.println(url+ "\t\tStatus:" + result);
        if (result == "1"){
            return url;
        }
        else
            return null;
    }

    public void addNotif(){
        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(LoginDevActivity.this);
        mbuilder.setSmallIcon(R.drawable.innerchainv);
        mbuilder.setContentTitle("Innerchain");
        mbuilder.setContentText("You have been accepted");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(LoginDevActivity.this);
        stackBuilder.addParentStack(MainActivity.class);


        NotificationManager notifmanager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notifmanager.notify(notificationId, mbuilder.build());
        System.out.println("--------------------------------testnotif");
    }
    protected void sendpost(final String usernama, String password)
    {
        client = new OkHttpClient.Builder()
                .connectTimeout(50,TimeUnit.SECONDS)
                .readTimeout(50,TimeUnit.SECONDS)
                .build();
       JSONObject postdata = new JSONObject();
       String sendpassword = password.replaceAll("\n","");
        try {
            postdata.put("username",usernama);
            postdata.put("password",sendpassword);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());
        resultText.setText(ip);

        final Request request = new Request.Builder()
                .url("http://"+ ip + api)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.w("failure Response", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful())
                {
                    String result = response.body().string(); //jangan panggil response body lebih dari sekali
                    Log.w("SUCCESS", result);
                    if(result.equals("1")) //1 berhasil, 0 angka
                    {
                        Intent pindah = new Intent(getApplicationContext(), MainActivity.class);
                        pindah.putExtra("username",usernama);
                        pindah.putExtra("mode","1"); //login registered user
                        pindah.putExtra("ip", ip);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(LoginDevActivity.this, "Login Success!",Toast.LENGTH_LONG).show();
                            }
                        });
                        startActivity(pindah);
                    }
                    else if(result.equals("2"))
                    {
                        Intent intent = new Intent(LoginDevActivity.this, BlacklistActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginDevActivity.this, "Login Failed!",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });
    }

}
