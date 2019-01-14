package com.example.hazelnut.dummy;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
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
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateActivity extends AppCompatActivity {

    //Set REST API media format to JSON
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    OkHttpClient client;

    //variable declaraton
    Button takeButton, importButton, updateButton;

    ImageView imageView;

    EditText nameFirst, nameLast, ktp, email, dob, address, nationality, accountNumber, photo, ipInput;

    TextView bankStatus, insuranceStatus, financeStatus, syariahStatus, sekuritasStatus;

    RadioGroup radioUnitGroup;
    RadioButton radioUnitButton;
    RadioButton bank, insurance, finance, syariah, sekuritas;

    Bitmap img;
    //Rest API endpoint
    String ip;
    //String ip = "192.168.43.219";
    String api = "/tempBlock/";
    String api2 = "/getRegDetail/";
    String api3 = "/fetchData/";
    String api4 = "/updateBlock/";
    String statusjson = "";
    String fetchjson;
    Block fetchedBlock = new Block();

    String globalUser;
    String globalPass;
    String globalKtp;
    String errorLbl = "";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    final Context context = this;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

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

        takeButton = findViewById(R.id.btn_takepicture);
        updateButton = findViewById(R.id.btn_update);
        bankStatus = findViewById(R.id.bankStatus);
        insuranceStatus = findViewById(R.id.insuranceStatus);
        financeStatus = findViewById(R.id.financeStatus);
        syariahStatus = findViewById(R.id.syariahStatus);
        sekuritasStatus = findViewById(R.id.sekuritasStatus);
        imageView  = findViewById(R.id.texture);
        importButton = findViewById(R.id.btn_importpicture);
        radioUnitGroup = findViewById(R.id.radioUnit);
        bank = findViewById(R.id.radioBank);
        insurance = findViewById(R.id.radioInsurance);
        finance = findViewById(R.id.radioFinance);
        syariah = findViewById(R.id.radioSyariah);
        sekuritas = findViewById(R.id.radioSekuritas);



        //Request camera permission to avoid crash

        if (ContextCompat.checkSelfPermission(UpdateActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(UpdateActivity.this, new String[] {Manifest.permission.CAMERA}, 100);
        }

        //database handler part

        //DatabaseHandler db = new DatabaseHandler(this);
        //ArrayList<HashMap<String, String>> userData = db.GetBlocks();

        //fetch data

        Intent i = getIntent();
        String tempuser = i.getStringExtra("username");
        String mode = i.getStringExtra("mode");
        String tempktp = i.getStringExtra("ktp");
        String temppassword = i.getStringExtra("password");
        String bankStatus = i.getStringExtra("bcabank");
        String insuranceStatus = i.getStringExtra("bcainsurance");
        String financeStatus = i.getStringExtra("bcafinance");
        String syariahStatus = i.getStringExtra("bcasyariah");
        String sekuritasStatus = i.getStringExtra("bcasekuritas");
        ip = i.getStringExtra("ip");

        globalKtp = tempktp;
        globalPass = temppassword;
        globalUser = tempuser;

        bank.setEnabled(false);
        insurance.setEnabled(false);
        finance.setEnabled(false);
        syariah.setEnabled(false);
        sekuritas.setEnabled(false);

        if(bankStatus.equals("Accepted"))
            bank.setEnabled(true);
        if(insuranceStatus.equals("Accepted"))
            insurance.setEnabled(true);
        if(financeStatus.equals("Accepted"))
            finance.setEnabled(true);
        if(syariahStatus.equals("Accepted"))
            syariah.setEnabled(true);
        if(sekuritasStatus.equals("Accepted"))
            sekuritas.setEnabled(true);

        sendpost(tempuser, "a", ip);

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

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(UpdateActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLbl = "";
                if (nameFirst.getText().toString().matches("[A-Za-z]+") == false || nameFirst.getText().toString().length() < 1 || nameFirst.getText().toString().length()>20){
                    errorLbl = "First name invalid";
                }
                else if (nameLast.getText().toString().matches("[A-Za-z]+") == false || nameLast.getText().toString().length() < 1 || nameLast.getText().toString().length()>20){
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
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to update?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int selectedId = radioUnitGroup.getCheckedRadioButtonId();
                            String unit = "";
                            radioUnitButton = findViewById(selectedId);
                            if (radioUnitButton.getText().equals("1")) {
                                unit = "bcabank";
                            } else if (radioUnitButton.getText().equals("2")) {
                                unit = "bcainsurance";
                            } else if (radioUnitButton.getText().equals("3")) {
                                unit = "bcafinancial";
                            } else if (radioUnitButton.getText().equals("4")) {
                                unit = "bcasyariah";
                            } else if (radioUnitButton.getText().equals("5")) {
                                unit = "bcasekuritas";
                            }
                            updatepost(nameFirst.getText().toString().toUpperCase(),
                                    nameLast.getText().toString().toUpperCase(),
                                    ktp.getText().toString(),
                                    email.getText().toString(),
                                    dob.getText().toString(),
                                    address.getText().toString(),
                                    nationality.getText().toString().toUpperCase(),
                                    accountNumber.getText().toString(),
                                    photo.getText().toString(),
                                    ip,
                                    unit);
                            Intent pindah = new Intent(getApplicationContext(), MainActivity.class);
                            pindah.putExtra("username", globalUser);
                            pindah.putExtra("ip",ip);
                            dialog.dismiss();
                            startActivity(pindah);
                            finish();
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
                if (errorLbl.equals("") == false) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UpdateActivity.this, errorLbl, Toast.LENGTH_LONG).show();
                        }
                    });
                }
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

    protected void updatepost(String firstname, String lastname, String ktp, String email, String dob, String address, String nationality, String accountnum, String photo,  String ipnumber, String unit)
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
            postdata.put("verified","2");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        final Request request = new Request.Builder()
                .url("http://"+ ipnumber + api4)
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
                            nameFirst.setText(fetchedBlock.getFirstname());
                            nameLast.setText(fetchedBlock.getLastname());
                            ktp.setText(fetchedBlock.getKtp());
                            email.setText(fetchedBlock.getEmail());
                            dob.setText(fetchedBlock.getDob());
                            address.setText(fetchedBlock.getAddress());
                            nationality.setText(fetchedBlock.getNationality());
                            accountNumber.setText(fetchedBlock.getAccountnum());
                            photo.setText(fetchedBlock.getPhoto());
                        }
                    });

                }
            }
        });
    }
    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dob.setText(sdf.format(myCalendar.getTime()));

    }
}
