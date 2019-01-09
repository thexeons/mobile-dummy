package com.example.hazelnut.dummy;

import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
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

public class DevActivity extends AppCompatActivity {


    Button devButton;
    EditText ip1,ip2,ip3,ip4,ip5;
    String m1,m2,m3,m4,m5;
    String ip;


    final Context context = this;
    public static final String key = "TeddyGembelGante";
    public static final String initVector = "GembelTeddyGante";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);
        devButton = findViewById(R.id.devBtn);
        ip1 = findViewById(R.id.ip1);
        ip2 = findViewById(R.id.ip2);
        ip3 = findViewById(R.id.ip3);
        ip4 = findViewById(R.id.ip4);
        ip5 = findViewById(R.id.ip5);


        Intent i = getIntent();
        ip = i.getStringExtra("ip");



            devButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m1 = ip1.getText().toString()+":8095";
                    m2 = ip2.getText().toString()+":8096";
                    m3 = ip3.getText().toString()+":8097";
                    m4 = ip4.getText().toString()+":8098";
                    m5 = ip5.getText().toString()+":8099";
                    Intent pindah = new Intent(getApplicationContext(), LoginDevActivity.class);
                    pindah.putExtra("m1", m1);
                    pindah.putExtra("m2", m2);
                    pindah.putExtra("m3", m3);
                    pindah.putExtra("m4", m4);
                    pindah.putExtra("m5", m5);
                    pindah.putExtra("ip", ip);
                    startActivity(pindah);
                    finish();
                }
            });

    }


}
