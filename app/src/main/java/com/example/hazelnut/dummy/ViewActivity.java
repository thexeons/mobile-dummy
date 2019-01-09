package com.example.hazelnut.dummy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewActivity extends AppCompatActivity {

    //Set REST API media format to JSON
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    OkHttpClient client;

    //variable declaraton
    ImageView imageView;

    TextView nameFirst, nameLast, ktp, email, dob, address, nationality, accountNumber, photo, ipInput;
    Button edit;

    //Rest API endpoint
    String ip;
    String api = "/tempBlock/";
    String api3 = "/fetchData/";
    String api7 = "/getUpdateStatus/";
    String api8 = "/cancelUpdate/"; //kasih ktp
    String statusjson = "";
    String fetchjson;
    Block fetchedBlock = new Block();

    String bankStatus, insuranceStatus, financeStatus, syariahStatus, sekuritasStatus;
    String globalUser;
    String globalPass;
    String globalKtp;

    String foto64;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //component declaration
        ipInput   = findViewById(R.id.ipInput);
        nameFirst   = findViewById(R.id.nameFirst);
        nameLast   = findViewById(R.id.nameLast);
        ktp   = findViewById(R.id.ktp);
        email   = findViewById(R.id.email);
        dob   = findViewById(R.id.dob);
        address   = findViewById(R.id.address);
        nationality   = findViewById(R.id.nationality);
        accountNumber   = findViewById(R.id.accountNumber);
        photo   = findViewById(R.id.photo);
        ipInput = findViewById(R.id.ipInput);
        imageView  = findViewById(R.id.texture);
        edit = findViewById(R.id.updateBtn);

       //database handler part

        //DatabaseHandler db = new DatabaseHandler(this);
        //ArrayList<HashMap<String, String>> userData = db.GetBlocks();

        //fetch data
        Intent i = getIntent();
        String tempuser = i.getStringExtra("username");
        String mode = i.getStringExtra("mode");
        String tempktp = i.getStringExtra("ktp");
        String temppassword = i.getStringExtra("password");
        ip = i.getStringExtra("ip");
        bankStatus = i.getStringExtra("bcabank");
        insuranceStatus = i.getStringExtra("bcainsurance");
        financeStatus = i.getStringExtra("bcafinance");
        syariahStatus = i.getStringExtra("bcasyariah");
        sekuritasStatus = i.getStringExtra("bcasekuritas");



        globalKtp = tempktp;
        globalPass = temppassword;
        globalUser = tempuser;

        sendpost(tempuser, "a", ip);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendpost(ktp.getText().toString(),"a");
            }
        });
    }
    ////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
    }

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
                .url("http://"+ ip + api3)
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
                            nameFirst.setText(fetchedBlock.getFirstname());
                            nameLast.setText(fetchedBlock.getLastname());
                            ktp.setText(fetchedBlock.getKtp());
                            email.setText(fetchedBlock.getEmail());
                            dob.setText(fetchedBlock.getDob());
                            address.setText(fetchedBlock.getAddress());
                            nationality.setText(fetchedBlock.getNationality());
                            accountNumber.setText(fetchedBlock.getAccountnum());
                            foto64 = fetchedBlock.getPhoto();
                            byte[] decodedString = Base64.decode(foto64, Base64.DEFAULT);
                            Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imageView.setImageBitmap(bmp);
                            photo.setText(fetchedBlock.getPhoto());
                        }
                    });

                }
            }
        });
    }

    //checkupdatestatus
    protected void sendpost(final String ktp, String dummy)
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
                    if (result.equals("1")) //1 berhasil, 0 angka
                    {
                        Intent pindah = new Intent(getApplicationContext(), UpdateActivity.class);
                        pindah.putExtra("username", globalUser);
                        pindah.putExtra("bcabank", bankStatus);
                        pindah.putExtra("bcainsurance", insuranceStatus);
                        pindah.putExtra("bcafinance", financeStatus);
                        pindah.putExtra("bcasyariah", syariahStatus);
                        pindah.putExtra("bcasekuritas", sekuritasStatus);
                        pindah.putExtra("ip", ip);
                        startActivity(pindah);
                    } else {
                        final AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirm");
                        builder.setMessage("You still have pending update\nAre you sure you want to cancel update?");
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                sendpost(ktp, "a", "b", "c");
                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
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


                    }
                }

            }
            protected void sendpost (String ktp, String a, String b, String c){
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
                        .url("http://"+ ip + api8)
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
                        }
                    }
                });
            }
        });
    }

}