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

public class Register3Activity extends AppCompatActivity {

    //Set REST API media format to JSON
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    OkHttpClient client;

    //variable declaraton
    Button register3nextBtn, takeButton, importButton;
    ImageView imageView;
    EditText photo;
    TextView signin;

    Bitmap img;
    //Rest API endpoint
    String ip;
    String api = "/tempBlock/";
    String api2 = "/getUserDetail/";
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
        setContentView(R.layout.activity_register3);

        //component declaration
        photo   = findViewById(R.id.photo);
        takeButton = findViewById(R.id.btn_takepicture);
        imageView  = findViewById(R.id.texture);
        importButton = findViewById(R.id.btn_importpicture);
        register3nextBtn = findViewById(R.id.register3nextBtn);
        signin = findViewById(R.id.signin);

        //Request camera permission to avoid crash

        if (ContextCompat.checkSelfPermission(Register3Activity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(Register3Activity.this, new String[] {Manifest.permission.CAMERA}, 100);
        }

        //database handler part

        //DatabaseHandler db = new DatabaseHandler(this);
        //ArrayList<HashMap<String, String>> userData = db.GetBlocks();

        //fetch data

        Intent i = getIntent();
        String tempuser = i.getStringExtra("username");
        final String mode = i.getStringExtra("mode");
        String tempktp = i.getStringExtra("ktp");
        String temppassword = i.getStringExtra("password");
        final String tempfirst = i.getStringExtra("first");
        final String templast = i.getStringExtra("last");
        final String tempaddress = i.getStringExtra("address");
        final String tempdob = i.getStringExtra("dob");
        final String tempemail = i.getStringExtra("email");
        final String tempaccnumber = i.getStringExtra("accnumber");
        final String tempnationality = i.getStringExtra("nationality");
        ip = i.getStringExtra("ip");
        globalKtp = tempktp;
        globalPass = temppassword;
        globalUser = tempuser;

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
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register3Activity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        register3nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pindah = new Intent(getApplicationContext(), Register4Activity.class);
                pindah.putExtra("username",globalUser);
                pindah.putExtra("password", globalPass);
                pindah.putExtra("ktp",globalKtp);
                pindah.putExtra("mode",mode);
                pindah.putExtra("first",tempfirst);
                pindah.putExtra("last",templast);
                pindah.putExtra("address",tempaddress);
                pindah.putExtra("dob",tempdob);
                pindah.putExtra("email",tempemail);
                pindah.putExtra("accnumber",tempaccnumber);
                pindah.putExtra("nationality",tempnationality);
                pindah.putExtra("photo",photo.getText().toString());//submit new registration
                pindah.putExtra("ip", ip);
                startActivity(pindah);
                System.out.println(ip);
                finish();
            }
        });
    }
    ////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //flip matrix
        Matrix matrix = new Matrix();
        //matrix.preScale(-1.0f, 1.0f);//flip horizontal

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            //save image to a temporary Bitmap
            Bitmap oriImage = (Bitmap) data.getExtras().get("data");
            //edit the image
            img = Bitmap.createBitmap(oriImage, 0, 0, oriImage.getWidth(), oriImage.getHeight(), matrix, false);
            //display in imageview
            if(img.getWidth()>img.getHeight())
            {
                img = Bitmap.createScaledBitmap(img, 320, 160, false);
            }
            else if(img.getHeight()>img.getWidth())
            {
                img = Bitmap.createScaledBitmap(img, 160, 320, false);
            }
            imageView.setImageBitmap(img);
            // insert to img
            photo.setText(base64encode(img));
        } else {
            if (requestCode == 200) {
                Uri uri=data.getData();
                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    img = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
                    imageView.setImageURI(uri);
                    if(img.getWidth()>img.getHeight())
                    {
                        img = Bitmap.createScaledBitmap(img, 320, 160, false);
                    }
                    else if(img.getHeight()>img.getWidth())
                    {
                        img = Bitmap.createScaledBitmap(img, 160, 320, false);
                    }
                }catch (Exception e)
                {
                    //handle exception
                }
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

}
