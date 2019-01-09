package com.example.hazelnut.dummy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

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

public class BranchActivity extends AppCompatActivity {

    //Set REST API media format to JSON
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    OkHttpClient client;

    //variable declaraton
    ImageView imageView, titleBranch;

    TextView nameFirst, nameLast, ktp, email, dob, address, nationality, accountNumber, photo, ipInput;
    Button edit;

    //Rest API endpoint
    String ip;
    String api = "/tempBlock/";
    String api2 = "/getUserDetail/";
    String api3 = "/fetchData/";
    String api6 = "/submitUser/";
    String api7 = "/getUpdateStatus/";
    String statusjson = "";
    String fetchjson;
    Block fetchedBlock = new Block();

    String globalUser;
    String globalPass;
    String globalKtp;
    String foto64;

    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);

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
        titleBranch = findViewById(R.id.titleBranch);

        ipInput = findViewById(R.id.ipInput);
        imageView  = findViewById(R.id.texture);
        edit = findViewById(R.id.updateBtn);


       //database handler part

        //DatabaseHandler db = new DatabaseHandler(this);
        //ArrayList<HashMap<String, String>> userData = db.GetBlocks();

        //fetch data

        Intent i = getIntent();
        String tempuser = i.getStringExtra("username");
        final String unit = i.getStringExtra("unit");
        String tempktp = i.getStringExtra("ktp");
        ip = i.getStringExtra("ip");

        globalUser = tempuser;
        globalKtp = tempktp;

        if (unit.equals("bcabank"))
        {
            titleBranch.setImageDrawable(getResources().getDrawable(R.drawable.bcabank));
        }
        if (unit.equals("bcainsurance"))
        {
            titleBranch.setImageDrawable(getResources().getDrawable(R.drawable.bcainsurance));
        }
        if (unit.equals("bcafinancial"))
        {
            titleBranch.setImageDrawable(getResources().getDrawable(R.drawable.bcafinance));
        }
        if (unit.equals("bcasyariah"))
        {
            titleBranch.setImageDrawable(getResources().getDrawable(R.drawable.bcasyariah));
        }
        if (unit.equals("bcasekuritas"))
        {
            titleBranch.setImageDrawable(getResources().getDrawable(R.drawable.bcasekuritas));
        }

        sendpost(globalUser, "a", ip);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to register to "+unit+" ?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseHandler dbHandler = new DatabaseHandler(BranchActivity.this);
                        dbHandler.insertUserDetails(nameFirst.getText().toString(),
                                nameLast.getText().toString(),
                                ktp.getText().toString(),
                                email.getText().toString(),
                                dob.getText().toString(),
                                address.getText().toString(),
                                nationality.getText().toString(),
                                accountNumber.getText().toString(),
                                photo.getText().toString());
                        Toast.makeText(getApplicationContext(), "Details Inserted Successfully",Toast.LENGTH_SHORT).show();
                        sendpost(nameFirst.getText().toString(),
                                nameLast.getText().toString(),
                                globalKtp,
                                email.getText().toString(),
                                dob.getText().toString(),
                                address.getText().toString(),
                                nationality.getText().toString(),
                                accountNumber.getText().toString(),
                                photo.getText().toString(),
                                ip,
                                unit);
                        try {
                            Thread.sleep(100);
                        }catch(Exception e){}
                        //sendpost(globalUser,globalPass,globalKtp,"dummy");
                        Intent pindah = new Intent(getApplicationContext(), MainActivity.class);
                        pindah.putExtra("username",globalUser);
                        pindah.putExtra("password", globalPass);
                        pindah.putExtra("ktp",globalKtp);
                        pindah.putExtra("mode","2"); //submit new registration
                        pindah.putExtra("ip", ip);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(BranchActivity.this, "Check data Success!",Toast.LENGTH_LONG).show();
                            }
                        });
                        startActivity(pindah);
                        finish();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                final AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
    ////////////////////////

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
                            Toast.makeText(BranchActivity.this, "Submit Success!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
