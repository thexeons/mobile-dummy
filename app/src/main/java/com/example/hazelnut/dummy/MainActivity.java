package com.example.hazelnut.dummy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
    Button sendButton, takeButton, importButton, logout,refresh;

    ImageView imageView;

    EditText nameFirst, nameLast, ktp, email, dob, address, nationality, accountNumber, photo, ipInput;

    TextView bankStatus, insuranceStatus, financeStatus, syariahStatus, sekuritasStatus;

    RadioGroup radioUnitGroup;
    RadioButton radioUnitButton;

    Bitmap img;
    //Rest API endpoint
    String api = ":8095/newBlock/";
    String api2 = ":8095/getUserDetail/";
    String statusjson;
    Status statusfinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        sendButton = findViewById(R.id.btn_kirim);
        takeButton = findViewById(R.id.btn_takepicture);
        bankStatus = findViewById(R.id.bankStatus);
        insuranceStatus = findViewById(R.id.insuranceStatus);
        financeStatus = findViewById(R.id.financeStatus);
        syariahStatus = findViewById(R.id.syariahStatus);
        sekuritasStatus = findViewById(R.id.sekuritasStatus);
        imageView  = findViewById(R.id.texture);
        importButton = findViewById(R.id.btn_importpicture);
        radioUnitGroup = findViewById(R.id.radioUnit);
        logout = findViewById(R.id.logout);
        refresh = findViewById(R.id.refresh);

        //Request camera permission to avoid crash

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, 100);
        }

        //database handler part

        //DatabaseHandler db = new DatabaseHandler(this);
        //ArrayList<HashMap<String, String>> userData = db.GetBlocks();


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //capture radio button selection from radiogroup
                int selectedId = radioUnitGroup.getCheckedRadioButtonId();
                String unit = "";
                radioUnitButton = findViewById(selectedId);
                if(radioUnitButton.getText().equals("BCA Bank"))
                {
                    unit = "bcabank";
                }
                else if(radioUnitButton.getText().equals("BCA Insurance"))
                {
                    unit = "bcainsurance";
                }
                else if(radioUnitButton.getText().equals("BCA Finance"))
                {
                    unit = "bcafinancial";
                }
                else if(radioUnitButton.getText().equals("BCA Syariah"))
                {
                    unit = "bcasyariah";
                }
                else if(radioUnitButton.getText().equals("BCA Sekuritas"))
                {
                    unit = "bcasekuritas";
                }
                api = ":8090/newBlock/";
                DatabaseHandler dbHandler = new DatabaseHandler(MainActivity.this);
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
                        ktp.getText().toString(),
                        email.getText().toString(),
                        dob.getText().toString(),
                        address.getText().toString(),
                        nationality.getText().toString(),
                        accountNumber.getText().toString(),
                        photo.getText().toString(),
                        ipInput.getText().toString(),
                        unit);
            }
        });

        takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // initialize intent to capture image from Camera
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //send it to Activity Result (case 1 request code 100)
                startActivityForResult(intent,100);
                //kita lempar ke onActivityResult.
            }
        });

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                // invoke the image gallery using an implict intent.
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

                // where do we want to find the data?
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();
                // finally, get a URI representation
                Uri data = Uri.parse(pictureDirectoryPath);

                // set the data and type.  Get all image types.
                photoPickerIntent.setDataAndType(data, "image/*");

                // we will invoke this activity, and get something back from it.
                startActivityForResult(photoPickerIntent, 200);
                */
                Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, 200);

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendpost(ktp.getText().toString(),ipInput.getText().toString());
                ObjectMapper mapper = new ObjectMapper();
                Status status = null;
                try {
                    status = mapper.readValue(statusjson, Status.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                statusfinal = status;
                bankStatus.setText(statusfinal.getBankStatus());
                insuranceStatus.setText(statusfinal.getInsuranceStatus());
                syariahStatus.setText(statusfinal.getSyariahStatus());
                financeStatus.setText(statusfinal.getFinanceStatus());
                sekuritasStatus.setText(statusfinal.getSekuritasStatus());
            }
        });
    }
    ////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //flip matrix
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);//flip horizontal

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            //save image to a temporary Bitmap
            Bitmap oriImage = (Bitmap) data.getExtras().get("data");
            //edit the image
            img = Bitmap.createBitmap(oriImage, 0, 0, oriImage.getWidth(), oriImage.getHeight(), matrix, false);
            //display in imageview
            img = Bitmap.createScaledBitmap(img, oriImage.getWidth() + 100, oriImage.getHeight() + 100, false);
            imageView.setImageBitmap(img);

            // masukkin deh ke img.
            photo.setText(base64encode(img));
        } else {
            if (requestCode == 200) {
                Uri uri=data.getData();
                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    img = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
                    img = Bitmap.createScaledBitmap(img, image.getWidth()/5, image.getHeight()/5, false);

                }catch (Exception e)
                {
                    //handle exception
                }
                imageView.setImageURI(uri);
                photo.setText(base64encode(img));
                /* THIS CODE IS SLOW
                // the address of the image on the SD Card.
                Uri imageUri = data.getData();

                // declare a stream to read the image data from the SD Card.
                InputStream inputStream;

                // we are getting an input stream, based on the URI of the image.
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    // get a bitmap from the stream.
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    img = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
                    img = Bitmap.createScaledBitmap(img, image.getWidth() + 100, image.getHeight() + 100, false);


                    // show the image to the user
                    imageView.setImageBitmap(img);
                    photo.setText(base64encode(img));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // show a message to the user indictating that the image is unavailable.
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }*/
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected String base64encode(Bitmap img)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT); //dapet nih stringnya bois
        return encoded;
    }

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
                Log.w("failure Response", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful())
                {
                    String result = response.body().string(); //jangan panggil response body lebih dari sekali
                    Log.w("SUCCESS", result);
                }
            }
        });
    }

    protected void sendpost(String ktp,  String ipnumber)
    {
        client = new OkHttpClient.Builder()
                .connectTimeout(50,TimeUnit.SECONDS)
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
                .url("http://"+ ipnumber + api2)
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
                    statusjson = result;
                }
            }
        });
    }
}
