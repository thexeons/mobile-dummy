package com.example.hazelnut.dummy;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register2Activity extends AppCompatActivity {

    //Set REST API media format to JSON
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    OkHttpClient client;

    //variable declaraton
    TextView signin;
    EditText nameFirst, nameLast, ktp, email, dob, address, nationality, accountNumber, photo, ipInput;
    Button register2nextBtn;
    String ip;

    String globalUser;
    String globalPass;
    String globalKtp;
    String errorLbl = "";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

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
        ipInput.setText("192.168.43.219");
        register2nextBtn = findViewById(R.id.register2Btn);
        signin = findViewById(R.id.signin);
        //fetch data

        Intent i = getIntent();
        String tempuser = i.getStringExtra("username");
        final String mode = i.getStringExtra("mode");
        String tempktp = i.getStringExtra("ktp");
        String temppassword = i.getStringExtra("password");
        ip = i.getStringExtra("ip");
        globalKtp = tempktp;
        globalPass = temppassword;
        globalUser = tempuser;
        ktp.setText(tempktp);
        ktp.setEnabled(false);


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register2Activity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Register2Activity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        register2nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLbl = "";
                if (nameFirst.getText().toString().matches("[A-Za-z ]+") == false || nameFirst.getText().toString().length() < 1 || nameFirst.getText().toString().length()>20){
                    errorLbl = "First name invalid";
                }
                else if (nameLast.getText().toString().matches("[A-Za-z ]+") == false || nameLast.getText().toString().length() < 1 || nameLast.getText().toString().length()>20){
                    errorLbl = "Last name invalid";
                }
                else if (address.getText().toString().length() < 1 || address.getText().toString().matches("[A-Za-z0-9]+")){
                    errorLbl = "Address invalid";
                }
                else if (email.getText().toString().matches(emailPattern) == false || email.getText().toString().length() < 1)
                {
                    errorLbl = "Email invalid";
                }
                else if (accountNumber.getText().toString().matches("[0-9]+") == false || accountNumber.length() != 10)
                {
                    errorLbl = "Account number invalid";
                }
                else if (nationality.getText().toString().length() < 1)
                {
                    errorLbl = "Nationality invalid";
                }
                else {
                    Intent pindah = new Intent(getApplicationContext(), Register3Activity.class);
                    pindah.putExtra("username", globalUser);
                    pindah.putExtra("password", globalPass);
                    pindah.putExtra("ktp", ktp.getText().toString());
                    pindah.putExtra("mode", mode);
                    pindah.putExtra("first", nameFirst.getText().toString().toUpperCase());
                    pindah.putExtra("last", nameLast.getText().toString().toUpperCase());
                    pindah.putExtra("address", address.getText().toString());
                    pindah.putExtra("dob", dob.getText().toString());
                    pindah.putExtra("email", email.getText().toString());
                    pindah.putExtra("accnumber", accountNumber.getText().toString());
                    pindah.putExtra("nationality", nationality.getText().toString().toUpperCase());//submit new registration
                    pindah.putExtra("ip", ip);
                    System.out.println(ip);
                    startActivity(pindah);
                    finish();
                }
                if (errorLbl.equals("") == false) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Register2Activity.this, errorLbl, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                /*
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String errorLbl;
                if(nameFirst.getText().toString().matches("[a-zA-Z]")==false || nameFirst.length() < 1)
                {
                    errorLbl = "First name must be alphabet";
                }
                else if (nameLast.getText().toString().matches("[a-zA-Z]")==false || nameLast.length() <1)
                {
                    errorLbl = "Last name must be alphabet";
                }
                else if (email.getText().toString().matches(emailPattern)==false || email.length() <1)
                {
                    errorLbl = "Email wrong";
                }
                else {
                    Intent pindah = new Intent(getApplicationContext(), Register3Activity.class);
                    pindah.putExtra("username", globalUser);
                    pindah.putExtra("password", globalPass);
                    pindah.putExtra("ktp", ktp.getText().toString());
                    pindah.putExtra("mode", mode);
                    pindah.putExtra("first", nameFirst.getText().toString().toUpperCase());
                    pindah.putExtra("last", nameLast.getText().toString().toUpperCase());
                    pindah.putExtra("address", address.getText().toString());
                    pindah.putExtra("dob", dob.getText().toString());
                    pindah.putExtra("email", email.getText().toString());
                    pindah.putExtra("accnumber", accountNumber.getText().toString());
                    pindah.putExtra("nationality", nationality.getText().toString().toUpperCase());//submit new registration
                    startActivity(pindah);
                }
                */
            }
        });
    }
    ////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
    }
    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dob.setText(sdf.format(myCalendar.getTime()));

    }
}
