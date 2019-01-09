package com.example.hazelnut.dummy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

public class Register4Activity extends AppCompatActivity {

    //Set REST API media format to JSON
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    OkHttpClient client;

    //variable declaraton
    Button sendButton;
    RadioGroup radioUnitGroup;
    RadioButton radioUnitButton;
    TextView signin;

    Bitmap img;
    //Rest API endpoint
    String ip;
    String api = "/tempBlock/";
    String api2 = "/getRegDetail/";
    String api3 = "/fetchData/";
    String api4 = "/updateBlock/";
    String api6 = "/submitUser/";
    String statusjson = "";
    String fetchjson;
    Block fetchedBlock = new Block();

    String globalUser;
    String globalPass;
    String globalKtp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register4);

        //component declaration

        sendButton = findViewById(R.id.btn_kirim);
        radioUnitGroup = findViewById(R.id.radioUnit);
        signin = findViewById(R.id.signin);


        //Request camera permission to avoid crash

        if (ContextCompat.checkSelfPermission(Register4Activity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(Register4Activity.this, new String[] {Manifest.permission.CAMERA}, 100);
        }

        //database handler part

        //DatabaseHandler db = new DatabaseHandler(this);
        //ArrayList<HashMap<String, String>> userData = db.GetBlocks();

        //fetch data

        Intent i = getIntent();
        final String tempuser = i.getStringExtra("username");
        final String mode = i.getStringExtra("mode");
        final String tempktp = i.getStringExtra("ktp");
        final String temppassword = i.getStringExtra("password");
        final String tempfirst = i.getStringExtra("first");
        final String templast = i.getStringExtra("last");
        final String tempaddress = i.getStringExtra("address");
        final String tempdob = i.getStringExtra("dob");
        final String tempemail = i.getStringExtra("email");
        final String tempaccnumber = i.getStringExtra("accnumber");
        final String tempnationality = i.getStringExtra("nationality");
        final String tempphoto = i.getStringExtra("photo");
        ip = i.getStringExtra("ip");
        globalKtp = tempktp;
        globalPass = temppassword;
        globalUser = tempuser;

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register4Activity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //capture radio button selection from radiogroup
                int selectedId = radioUnitGroup.getCheckedRadioButtonId();
                String unit="";
                radioUnitButton = findViewById(selectedId);
                if(radioUnitButton.getText().equals("1"))
                {
                    unit = "bcabank";
                }
                else if(radioUnitButton.getText().equals("2"))
                {
                    unit = "bcainsurance";
                }
                else if(radioUnitButton.getText().equals("3"))
                {
                    unit = "bcafinancial";
                }
                else if(radioUnitButton.getText().equals("4"))
                {
                    unit = "bcasyariah";
                }
                else if(radioUnitButton.getText().equals("5"))
                {
                    unit = "bcasekuritas";
                }
                DatabaseHandler dbHandler = new DatabaseHandler(Register4Activity.this);
                dbHandler.insertUserDetails(tempfirst,
                        templast,
                        tempktp,
                        tempemail,
                        tempdob,
                        tempaddress,
                        tempnationality,
                        tempaccnumber,
                        tempphoto);
                Toast.makeText(getApplicationContext(), "Details Inserted Successfully",Toast.LENGTH_SHORT).show();
                sendpost(tempfirst,
                        templast,
                        tempktp,
                        tempemail,
                        tempdob,
                        tempaddress,
                        tempnationality,
                        tempaccnumber,
                        tempphoto,
                        ip,
                        unit);
                try {
                    Thread.sleep(100);
                }catch(Exception e){}
                sendpost(globalUser,globalPass,tempktp,"dummy");
                Intent pindah = new Intent(getApplicationContext(), MainActivity.class);
                pindah.putExtra("username",tempuser);
                pindah.putExtra("password", temppassword);
                pindah.putExtra("ktp",globalKtp);
                pindah.putExtra("mode","2");
                pindah.putExtra("ip",ip);//submit new registration
                System.out.println(ip);
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Register4Activity.this, "Check data Success!",Toast.LENGTH_LONG).show();
                    }
                });
                startActivity(pindah);
                finish();

            }
        });
    }
    ////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
    }

    //register
    protected void sendpost(String firstname, String lastname, String ktp, String email, String dob, String address, String nationality, String accountnum, String photo,  String ipnumber, String unit)
    {
        client = new OkHttpClient.Builder()
                .connectTimeout(50,TimeUnit.SECONDS)
                .readTimeout(50,TimeUnit.SECONDS)
                .build();

        String bcabank, bcainsurance, bcafinancial, bcasekuritas,bcasyariah;
        bcabank = bcainsurance = bcafinancial = bcasekuritas = bcasyariah = "0";
        if(unit.equals("bcabank"))
        {
            bcabank = "1";
        }
        else if(unit.equals("bcainsurance"))
        {
            bcainsurance = "1";
        }
        else if(unit.equals("bcafinancial"))
        {
            bcafinancial = "1";
        }
        else if(unit.equals("bcasekuritas"))
        {
            bcasekuritas = "1";
        }
        else if(unit.equals("bcasyariah"))
        {
            bcasyariah = "1";
        }

        JSONObject postdata = new JSONObject();
        try {
            postdata.put("firstname",firstname);
            postdata.put("lastname",lastname);
            postdata.put("ktp",ktp);
            postdata.put("email",email);
            postdata.put("dob",dob);
            postdata.put("address",address);
            postdata.put("nationality",nationality);
            postdata.put("accountnum",accountnum);
            postdata.put("photo",photo);
            postdata.put("bcabank",bcabank);
            postdata.put("bcainsurance",bcainsurance);
            postdata.put("bcafinancial",bcafinancial);
            postdata.put("bcasekuritas",bcasekuritas);
            postdata.put("bcasyariah",bcasyariah);
            postdata.put("verified","0");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        final Request request = new Request.Builder()
                .url("http://"+ ipnumber + api)
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
                }
            }
        });
    }

    //register
    protected void sendpost(String username, String password, String ktp, String ipnumber)
    {
        client = new OkHttpClient.Builder()
                .connectTimeout(50,TimeUnit.SECONDS)
                .readTimeout(50,TimeUnit.SECONDS)
                .build();

        JSONObject postdata = new JSONObject();
        try {
            postdata.put("username",username);
            postdata.put("password", password);
            postdata.put("ktp", ktp);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        final Request request = new Request.Builder()
                .url("http://"+ ip + api6)
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
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(Register4Activity.this, "Submit Success!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}