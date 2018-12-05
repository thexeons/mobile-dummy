package com.example.hazelnut.dummy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {

    //Set REST API media format to JSON
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    OkHttpClient client;
    String api5 = ":8095/verifyRegister/";

    //button
    Button checkButton;
    EditText usernameText, passwordText,ktpText;

    public static final String key = "TeddyGembelGante";
    public static final String initVector = "GembelTeddyGante";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        checkButton = findViewById(R.id.check);
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        ktpText = findViewById(R.id.ktpText);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String encryptedpassword = AESEncrypt(passwordText.getText().toString());
                sendpost(usernameText.getText().toString(),encryptedpassword,ktpText.getText().toString(),"192.168.43.219");
                //response 1 : KTP EXISTED
                //response 2 : USERNAME EXISTED
                //response 3 : KTP USERNAME EXISTED
                //response 4 :
            }
        });
    }

    protected void sendpost(String username, String password, String ktp, String ipnumber)
    {
        client = new OkHttpClient.Builder()
                .connectTimeout(50,TimeUnit.SECONDS)
                .readTimeout(50,TimeUnit.SECONDS)
                .build();

        JSONObject postdata = new JSONObject();
        String sendpassword = password.replaceAll("\n","");
        try {
            postdata.put("username",username);
            postdata.put("password",sendpassword);
            postdata.put("ktp",ktp);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        final Request request = new Request.Builder()
                .url("http://"+ "192.168.43.219" + api5)
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
                    final String result = response.body().string(); //jangan panggil response body lebih dari sekali
                    Log.w("SUCCESS: ", result);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this,result,Toast.LENGTH_LONG).show();
                        }
                    });
                }
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
}
