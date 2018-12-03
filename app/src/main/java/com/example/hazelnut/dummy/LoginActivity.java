package com.example.hazelnut.dummy;

import android.content.Intent;
import android.os.Bundle;
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

import static com.example.hazelnut.dummy.MainActivity.MEDIA_TYPE;

public class LoginActivity extends AppCompatActivity {

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    OkHttpClient client;
    Button loginButton;
    EditText username, password;
    TextView registerText, resultText;
    String api = ":8095/verifyLogin/";
    String answer ="";

    public static final String key = "TeddyGembelGante";
    public static final String initVector = "GembelTeddyGante";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.loginButton);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        registerText = findViewById(R.id.registerText);
        resultText= findViewById(R.id.result);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String encryptedpassword = AESEncrypt(password.getText().toString());
                if(sendpost(username.getText().toString(),encryptedpassword).equals("1")) //1 berhasil, 0 angka
                {
                    Toast.makeText(LoginActivity.this, "Login Success!",Toast.LENGTH_LONG).show();
                    Intent pindah = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(pindah);
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Login Failed!",Toast.LENGTH_LONG).show();
                }
            }});

        registerText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent pindah = new Intent(getApplicationContext(), RegisterActivity.class);
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

    protected String sendpost(String username, String password)
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
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());
        resultText.setText(postdata.toString());

        final Request request = new Request.Builder()
                .url("http://"+ "192.168.43.219" + api)
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

                    if (result.equals("1"))
                    {
                        answer = "1";
                    }
                    else if (result.equals("0"))
                    {
                        answer = "0";
                    }

                }
            }
        });
        return answer;
    }

}
