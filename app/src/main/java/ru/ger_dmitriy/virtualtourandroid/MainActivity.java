package ru.ger_dmitriy.virtualtourandroid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.provider.Telephony.Mms.Part.FILENAME;

public class MainActivity extends AppCompatActivity {

    private final int CAMERA_RESULT = 0;
    private final String JPEG_FILE_PREFIX = "photo_";
    private final String JPEG_FILE_SUFFIX = ".jpg";

    private String TAG = "VT";

    public static final String APP_PREFERENCES = "settings";
    public static String APP_PREFERENCES_ADDRESS = "";

    private TextView addressTxtView;

    static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath;
    private String directoryTimeStamp;
    private File storageDir;
    private EditText editText;
    private static final int PERMISSION_REQUEST = 1;

    private Context mContext;

    private static final String login = LoginActivity.login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        editText = (EditText) findViewById(R.id.editText);
        setSupportActionBar(toolbar);

        addressTxtView = findViewById(R.id.addressTextView);

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        // читаем установленное значение из
        APP_PREFERENCES_ADDRESS = prefs.getString("upload_address", "0.0.0.0");
        Log.d(TAG, "onCreate: " + APP_PREFERENCES_ADDRESS);

        addressTxtView.setText(APP_PREFERENCES_ADDRESS);

        //login = LoginActivity.login;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST);//выводит диалог, где пользователю предоставляется выбор
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, PERMISSION_REQUEST);//выводит диалог, где пользователю предоставляется выбор
        }

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        // читаем установленное значение из
        APP_PREFERENCES_ADDRESS = prefs.getString("upload_address", "0.0.0.0");
        addressTxtView.setText(APP_PREFERENCES_ADDRESS);
        // Log.d(TAG, "onResume: " + APP_PREFERENCES_ADDRESS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_sendAgain) {
            sendFiles(storageDir);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCameraClick(View view) {
        //String login = editText.getText().toString();

        if (login.equals("")) {
            Toast toast = Toast
                    .makeText(this, "Enter a login!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        directoryTimeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES.concat("/"
                + directoryTimeStamp));
        dispatchTakePictureIntent();

    }

    private void dispatchTakePictureIntent() {
        Toast toast;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);//выводит диалог, где пользователю предоставляется выбор
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);//выводит диалог, где пользователю предоставляется выбор
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);//выводит диалог, где пользователю предоставляется выбор
        } else {
            if (!isIntentAvailable(this, MediaStore.ACTION_IMAGE_CAPTURE)) {
                toast = Toast
                        .makeText(this, "Error! Can't get access to a camera",
                                Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) == null) {
                toast = Toast
                        .makeText(this, "Error! Can't get access to a camera activity",
                                Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "dispatchTakePictureIntent: Can't create file", ex);
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile == null) {
                toast = Toast
                        .makeText(this, "Error! Can't create temp file",
                                Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            try {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ru.ger_dmitriy.virtualtourservice",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            } catch (Exception ex) {
                Log.e(TAG, "dispatchTakePictureIntent: Can't start activity", ex);
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_TAKE_PHOTO) {

                    Toast toast = Toast
                            .makeText(this, "Photo is created", Toast.LENGTH_SHORT);
                    toast.show();
                    dispatchTakePictureIntent();
                }
            } else {
                sendFiles(storageDir);
            }
        } catch (Exception ex) {
            Log.e(TAG, "onActivityResult: ", ex);
        }
    }

    /*
        private void saveFullImage() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);//выводит диалог, где пользователю предоставляется выбор
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);//выводит диалог, где пользователю предоставляется выбор
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);//выводит диалог, где пользователю предоставляется выбор
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST);//выводит диалог, где пользователю предоставляется выбор
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory(),
                        "test.jpg");
                //mOutputFileUri = FileProvider.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoPath);
                startActivityForResult(intent, TAKE_PICTURE);
            }
        }
    */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
/*
    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        mImageBitmap = (Bitmap) extras.get("data");
        //mImageView.setImageBitmap(mImageBitmap);
    }*/

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES.concat(directoryTimeStamp));
        File image = File.createTempFile(
                imageFileName,
                JPEG_FILE_SUFFIX,
                storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
/*
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }*/

    private void sendFiles(File path) {
        Toast toast;
        if (path == null) {
            toast = Toast
                    .makeText(this, "Error! No files", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        //String login = editText.getText().toString();
        if (login.equals("")) {
            toast = Toast
                    .makeText(this, "Error! Enter a login", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        Log.d(TAG, "Login: " + login);
        mContext = this;
        int count = path.listFiles().length;
       /* toast = Toast
                .makeText(this, "Count " + count, Toast.LENGTH_SHORT);
        toast.show();*/
        for (File file : path.listFiles()) {
            if (file.isFile() && file.length() != 0) {
                clientRun client = new clientRun(mContext);
                client.execute(APP_PREFERENCES_ADDRESS, login, directoryTimeStamp, file.getAbsolutePath());
            }
        }
    }

    public void onGalleryClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
    }

    public void onSendClick(View view) {

    }
}
