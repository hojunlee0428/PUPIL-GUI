package com.example.alessiamorin.myapplication;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

public class PhotoChoice extends AppCompatActivity {

    ImageView imageView;
    TextView tv;
    apiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_choice);

        imageView = (ImageView) findViewById(R.id.photo);

        tv = (TextView) findViewById(R.id.textbox);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.widthPixels;


        Intent intent = getIntent();
        final Uri imageUri = intent.getData();
        imageView.setImageURI(imageUri);

        Button yes = (Button) findViewById(R.id.Yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Run the API code on the image!
                try {
                    sendNetworkRequest(imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button cancel = (Button) findViewById(R.id.Cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhotoChoice.this, MainActivity.class));
            }
        });

    }

    public void sendNetworkRequest(final Uri photo) throws IOException {
        InputStream iStream =   getContentResolver().openInputStream(photo);
        byte[] inputData = getBytes(iStream);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getBaseContext(), "http://192.168.0.23:8080/upload", new ByteArrayEntity(inputData), "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onStart(){
                //do a spinner UI element
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                try {
                    JSONObject output = new JSONObject(new String(binaryData));
                    imageView.setVisibility(View.GONE);
                    tv.setText(output.getString("message"));
                    tv.setVisibility(View.VISIBLE);
//                    File file
//                            =getApplicationContext().getExternalFilesDir(null);
//                    File filePath = new File(file , "pupil");
//                    if (!filePath.exists()) {
//                        filePath.mkdir();
//                    }
//                    File imageFile = new File(filePath.getPath(), "test.txt");
//                    FileOutputStream fos = new FileOutputStream(imageFile);
//                    fos.write(output.getString("message").getBytes());
//                    fos.close();
//                    Intent intent = new Intent();
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    String authority = "com.example.android.fileprovider";
//                    Uri uri = FileProvider.getUriForFile(getApplicationContext(), authority, imageFile);
//                    intent.setDataAndType(uri, "text/*");
//                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
                Toast.makeText(getBaseContext(), "unsuccess", Toast.LENGTH_LONG).show();
            }
        });
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public File getPrivateAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(null), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }
}
