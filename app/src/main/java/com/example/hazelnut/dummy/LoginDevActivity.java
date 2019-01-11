package com.example.hazelnut.dummy;

import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    EditText username, password;
    TextView registerText, resultText;
    ProgressBar pb;
    String ip,m1,m2,m3,m4,m5;
    String api = "/verifyLogin/";
    int notificationId = 1;
    int flag=-1;
    String globalip;
    ArrayList<String> al = new ArrayList<String>();
    ArrayList<String> pbal = new ArrayList<String>();
    ArrayList<String> ipal = new ArrayList<String>();

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
        pb = findViewById(R.id.pb);

        Intent i = getIntent();
        ipal.add(i.getStringExtra("m1"));
        ipal.add(i.getStringExtra("m2"));
        ipal.add(i.getStringExtra("m3"));
        ipal.add(i.getStringExtra("m4"));
        ipal.add(i.getStringExtra("m5"));

        ip = i.getStringExtra("ip");
        loginButton.setEnabled(false);
        signupButton.setEnabled(false);

        //resultText.setText(ipal.get(j));

        pb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConnectPinger().execute("http://"+ipal.get(0),
                        "http://"+ipal.get(1),
                        "http://"+ipal.get(2),
                        "http://"+ipal.get(3),
                        "http://"+ipal.get(4));
                loginButton.setEnabled(true);
                signupButton.setEnabled(true);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println(al.size());
                if (al.size()!=0)
                {
                    ip = al.get(0).replaceAll("http://","");
                    System.out.println("THIS IS YOOUR IP----------------"+ip);
                }
                if(al.size()!=0) {
                    System.out.println("------ini ip yang dipake" + ip);
                    final String encryptedpassword = AESEncrypt(password.getText().toString());
                    sendpost(username.getText().toString(), encryptedpassword);
                }
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


        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                System.out.println(al.size());
                if (al.size()!=0)
                {
                    ip = al.get(0).replaceAll("http://","");
                    System.out.println("THIS IS YOOUR IP----------------"+ip);
                }
                if (al.size()!=0) {
                    System.out.println("------ini ip yang dipake" + ip);
                    Intent pindah = new Intent(getApplicationContext(), RegisterActivity.class);
                    pindah.putExtra("ip", ip);
                    startActivity(pindah);
                }
            }
        });
    }
    public class ConnectPinger extends AsyncTask<String , Integer ,List<String>> {

        @Override
        protected List<String> doInBackground(String... tasks) {

            // Get the number of task
            int count = tasks.length;
            // Initialize a new list
            List<String> taskList= new ArrayList<>(count);
            for(int i =0;i<count;i++){
                // Do the current task here
                String currentTask = tasks[i];
                taskList.add(currentTask);

                String result = "";
                int code = 200;
                // Sleep the thread for 1 second
                try {
                    URL siteURL = new URL(currentTask);
                    HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(750);
                    connection.connect();

                    code = connection.getResponseCode();
                    if (code == 200) {
                        result = "1";
                        al.add(currentTask);
                    } else {
                        result = "2";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Publish the async task progress
                // Added 1, because index start from 0
                publishProgress((int) (((i+1) / (float) count) * 100));

                // If the AsyncTask cancelled
                if(isCancelled()){
                    break;
                }
            }

            /*
            String result;
            int code = 200;
            try {
                URL siteURL = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(750);
                connection.connect();

                code = connection.getResponseCode();
                if (code == 200) {
                    result = "1";
                    System.out.println("----------------SUCCESS");
                    al.add(strings[0]);
                } else {
                    result = "2";
                }
            } catch (Exception e) {
                result = "0";
            }

            int count = 5;
            pbal.add("progress");
            publishProgress(pbal.size()*20);
            System.out.println(strings[0]+"-------result> "+result);
            return "Success";
            */
            return taskList;
        }

        // After each task done
        protected void onProgressUpdate(Integer... progress){
            // Update the progress bar
            pb.setProgress(progress[0]);
        }
        @Override
        protected void onPostExecute(List<String> result) {
            pb.incrementProgressBy(20);
        }
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
