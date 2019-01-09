package com.example.hazelnut.dummy;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MainActivity extends AppCompatActivity {

    //Set REST API media format to JSON
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    OkHttpClient client;


    //variable declaraton
    Button logout, profileBtn;
    LinearLayout cv1, cv2, cv3, cv4, cv5;
    ImageView cv1iv, cv2iv, cv3iv, cv4iv, cv5iv;
    TextView cv1tv, cv2tv, cv3tv, cv4tv, cv5tv;
    String bankStatus, insuranceStatus, financeStatus, syariahStatus, sekuritasStatus;
    String nameFirst,nameLast,ktp,email,dob,address,nationality,accountNumber,photo;
    FloatingActionButton refresh;

    final Context context = this;
    Bitmap img;
    //Rest API endpoint
    String ip = "192.168.43.219";
    String api = "/tempBlock/";
    String api2 = "/getRegDetail/";
    String api3 = "/fetchData/";
    String api4 = "/updateBlock/";
    String api6 = "/submitUser/";
    String api2a = "/getRegDetail2/";
    String api7 = "/getUpdateStatus/";
    String statusjson = "";
    String fetchjson;
    Block fetchedBlock = new Block();

    String globalUser;
    String globalPass;
    public String globalKtp = "xxx";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //component declaration
        logout = findViewById(R.id.logout);
        profileBtn = findViewById(R.id.profileBtn);
        refresh = findViewById(R.id.refresh);

        cv1 = findViewById(R.id.cv1);
        cv2 = findViewById(R.id.cv2);
        cv3 = findViewById(R.id.cv3);
        cv4 = findViewById(R.id.cv4);
        cv5 = findViewById(R.id.cv5);

        cv1iv = findViewById(R.id.cv1iv);
        cv1tv = findViewById(R.id.cv1tv);
        cv2iv = findViewById(R.id.cv2iv);
        cv2tv = findViewById(R.id.cv2tv);
        cv3iv = findViewById(R.id.cv3iv);
        cv3tv = findViewById(R.id.cv3tv);
        cv4iv = findViewById(R.id.cv4iv);
        cv4tv = findViewById(R.id.cv4tv);
        cv5iv = findViewById(R.id.cv5iv);
        cv5tv = findViewById(R.id.cv5tv);

        //Request camera permission to avoid crash

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, 100);
        }

        //database handler part

        //DatabaseHandler db = new DatabaseHandler(this);
        //ArrayList<HashMap<String, String>> userData = db.GetBlocks();

        //fetch data

        Intent i = getIntent();
        final String tempuser = i.getStringExtra("username");
        String mode = i.getStringExtra("mode");
        String tempktp = i.getStringExtra("ktp");
        String temppassword = i.getStringExtra("password");
        String ipkampret = i.getStringExtra("ip");
        ip = ipkampret;

        globalKtp = tempktp;
        globalPass = temppassword;
        globalUser = tempuser;

        sendpost(ktp,"a","b","c");
        //sendpost(tempuser, "a", ipInput.getText().toString());



        for(int queue = 1; queue<3 ; queue++) {
            if (queue==1) {
                sendpost(tempuser, "a", ip);
            }
            if (queue==2) {
                sendpost(tempuser, ip);
            }
            try {
                SystemClock.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(
                    cv1tv.getText().toString()=="Accepted" ||
                    cv2tv.getText().toString()=="Accepted" ||
                    cv3tv.getText().toString()=="Accepted" ||
                    cv4tv.getText().toString()=="Accepted" ||
                    cv5tv.getText().toString()=="Accepted")
                {
                    Intent pindah = new Intent(getApplicationContext(), ViewActivity.class);
                    pindah.putExtra("username", globalUser);
                    pindah.putExtra("bcabank", bankStatus);
                    pindah.putExtra("bcainsurance", insuranceStatus);
                    pindah.putExtra("bcafinance", financeStatus);
                    pindah.putExtra("bcasyariah", syariahStatus);
                    pindah.putExtra("bcasekuritas", sekuritasStatus);
                    pindah.putExtra("ip", ip);
                    startActivity(pindah);
                }
                else
                {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "ACCOUNT STILL PENDING!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int queue = 1; queue<3 ; queue++) {
                    if (queue==1) {
                        sendpost(tempuser, "a", ip);
                    }
                    if (queue==2) {
                        sendpost(tempuser, ip);
                    }
                    try {
                        SystemClock.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cv1tv.getText().toString().equals("Not Registered") && (cv1tv.getText().toString()=="Accepted" ||
                        cv2tv.getText().toString()=="Accepted" ||
                        cv3tv.getText().toString()=="Accepted" ||
                        cv4tv.getText().toString()=="Accepted" ||
                        cv5tv.getText().toString()=="Accepted")) {
                    Intent pindah = new Intent(getApplicationContext(), BranchActivity.class);
                    pindah.putExtra("username", globalUser);
                    pindah.putExtra("ktp", globalKtp);
                    pindah.putExtra("unit", "bcabank");
                    pindah.putExtra("ip", ip);
                    startActivity(pindah);
                }
            }
        });

        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cv2tv.getText().toString().equals("Not Registered") && (cv1tv.getText().toString()=="Accepted" ||
                        cv2tv.getText().toString()=="Accepted" ||
                        cv3tv.getText().toString()=="Accepted" ||
                        cv4tv.getText().toString()=="Accepted" ||
                        cv5tv.getText().toString()=="Accepted")) {
                    Intent pindah = new Intent(getApplicationContext(), BranchActivity.class);
                    pindah.putExtra("username", globalUser);
                    pindah.putExtra("ktp", globalKtp);
                    pindah.putExtra("unit", "bcainsurance");
                    pindah.putExtra("ip", ip);
                    startActivity(pindah);
                }
            }
        });
        cv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cv3tv.getText().toString().equals("Not Registered") && (cv1tv.getText().toString()=="Accepted" ||
                        cv2tv.getText().toString()=="Accepted" ||
                        cv3tv.getText().toString()=="Accepted" ||
                        cv4tv.getText().toString()=="Accepted" ||
                        cv5tv.getText().toString()=="Accepted")) {
                    Intent pindah = new Intent(getApplicationContext(), BranchActivity.class);
                    pindah.putExtra("username", globalUser);
                    pindah.putExtra("ktp", globalKtp);
                    pindah.putExtra("unit", "bcafinancial");
                    pindah.putExtra("ip", ip);
                    startActivity(pindah);
                }
            }
        });
        cv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cv4tv.getText().toString().equals("Not Registered") && (cv1tv.getText().toString()=="Accepted" ||
                        cv2tv.getText().toString()=="Accepted" ||
                        cv3tv.getText().toString()=="Accepted" ||
                        cv4tv.getText().toString()=="Accepted" ||
                        cv5tv.getText().toString()=="Accepted")) {
                    Intent pindah = new Intent(getApplicationContext(), BranchActivity.class);
                    pindah.putExtra("username", globalUser);
                    pindah.putExtra("ktp", globalKtp);
                    pindah.putExtra("unit", "bcasyariah");
                    pindah.putExtra("ip", ip);
                    startActivity(pindah);
                }
            }
        });
        cv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cv5tv.getText().toString().equals("Not Registered") && (cv1tv.getText().toString()=="Accepted" ||
                        cv2tv.getText().toString()=="Accepted" ||
                        cv3tv.getText().toString()=="Accepted" ||
                        cv4tv.getText().toString()=="Accepted" ||
                        cv5tv.getText().toString()=="Accepted")) {
                    Intent pindah = new Intent(getApplicationContext(), BranchActivity.class);
                    pindah.putExtra("username", globalUser);
                    pindah.putExtra("ktp", globalKtp);
                    pindah.putExtra("unit", "bcasekuritas");
                    pindah.putExtra("ip", ip);
                    startActivity(pindah);
                }
            }
        });
    }
    ////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
    }

    //refresh
    protected void sendpost(String username,  String ipnumber)
    {
        ipnumber = ip;
        client = new OkHttpClient.Builder()
                .connectTimeout(50,TimeUnit.SECONDS)
                .readTimeout(50,TimeUnit.SECONDS)
                .build();

        JSONObject postdata = new JSONObject();
        try {

            postdata.put("username",username);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        final Request request = new Request.Builder()
                .url("http://"+ ipnumber + api2a)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.w("FAILED: ", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful())
                {
                    String result = response.body().string(); //jangan panggil response body lebih dari sekali
                    Log.w("SUCCESS: ", result);
                    statusjson = result;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Status status = new Status();
                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                status = mapper.readValue(statusjson, Status.class);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            bankStatus=(status.getBankStatus());
                            verifstatus(status.getBankStatus(),cv1iv,cv1tv);
                            insuranceStatus=(status.getInsuranceStatus());
                            verifstatus(insuranceStatus,cv2iv,cv2tv);
                            financeStatus=(status.getFinanceStatus());
                            verifstatus(financeStatus,cv3iv,cv3tv);
                            syariahStatus=(status.getSyariahStatus());
                            verifstatus(syariahStatus,cv4iv,cv4tv);
                            sekuritasStatus=(status.getSekuritasStatus());
                            verifstatus(sekuritasStatus,cv5iv,cv5tv);
                        }
                        public void verifstatus (String check, ImageView iv, TextView tv){
                            if(check.equals("Accepted"))
                            {
                                iv.setImageDrawable(getResources().getDrawable(R.drawable.statusverified));
                                tv.setText("Accepted");
                                tv.setTextColor(Color.parseColor("#0fdd0f"));
                            }
                            else if (check.equals("not Registered")){
                                iv.setImageDrawable(getResources().getDrawable(R.drawable.statusnotverified));
                                tv.setText("Not Registered");
                                tv.setTextColor(Color.parseColor("#dd0f0f"));
                            }
                            else if (check.equals("Pending")){
                                iv.setImageDrawable(getResources().getDrawable(R.drawable.statuspartverified));
                                tv.setText("Pending");
                                tv.setTextColor(Color.parseColor("#efd510"));
                            }
                        }
                    });
                }
            }
        });
    }

    //fetchdata
    protected void sendpost(String username, String dummy,String ipnumber)
    {
        client = new OkHttpClient.Builder()
                .connectTimeout(50,TimeUnit.SECONDS)
                .readTimeout(50,TimeUnit.SECONDS)
                .build();

        JSONObject postdata = new JSONObject();
        try {

            postdata.put("username",username);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        final Request request = new Request.Builder()
                .url("http://"+ ipnumber + api3)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.w("FAILED: ", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful())
                {
                    String result = response.body().string(); //jangan panggil response body lebih dari sekali
                    Log.w("SUCCESS: ", result);
                    ObjectMapper mapper = new ObjectMapper();
                    Block fetched = new Block();
                    try {
                        fetched = mapper.readValue(result, Block.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fetchedBlock = fetched;
                            //insert fetched data
                    runOnUiThread(new Runnable() {
                        public void run() {
                            nameFirst=(fetchedBlock.getFirstname());
                            nameLast=(fetchedBlock.getLastname());
                            ktp=(fetchedBlock.getKtp());
                            globalKtp=fetchedBlock.getKtp();
                            email=(fetchedBlock.getEmail());
                            dob=(fetchedBlock.getDob());
                            address=(fetchedBlock.getAddress());
                            nationality=(fetchedBlock.getNationality());
                            accountNumber=(fetchedBlock.getAccountnum());
                            photo=(fetchedBlock.getPhoto());
                        }
                    });

                }
            }
        });
    }

    //checkupdatestatus
    protected void sendpost(final String ktp, String a, String b, String c)
    {
        client = new OkHttpClient.Builder()
                .connectTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50,TimeUnit.SECONDS)
                .build();
        JSONObject postdata = new JSONObject();
        try {
            postdata.put("ktp",ktp);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        final Request request = new Request.Builder()
                .url("http://"+ ip + api7)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.w("failure Response", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {

                    String result = response.body().string(); //jangan panggil response body lebih dari sekali
                    Log.w("SUCCESS", result);
                    if (result.equals("0")) //1 berhasil, 0 angka
                    {
                        final AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirm");
                        builder.setMessage("You still have pending update!");
                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
                        cv1.setClickable(false);
                        cv2.setClickable(false);
                        cv3.setClickable(false);
                        cv4.setClickable(false);
                        cv5.setClickable(false);
                    } else {
                        cv1.setClickable(true);
                        cv2.setClickable(true);
                        cv3.setClickable(true);
                        cv4.setClickable(true);
                        cv5.setClickable(true);

                    }
                }

            }
        });
    }
}
