package com.example.anaworld.mifclucknowid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public final static int QRcodeWidth = 500;
    private static final String IMAGE_DIRECTORY = "/QRcodeDemonuts";
    Bitmap bitmap;
    private EditText etqr, etqr2, etqr3;
    private ImageView iv;
    private Button btn;
    int key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.iv);
        etqr = (EditText) findViewById(R.id.etqr);
        etqr2 = (EditText) findViewById(R.id.etqr2);
        etqr3 = (EditText) findViewById(R.id.etqr3);
        btn = (Button) findViewById(R.id.btn);

       try {
            FileInputStream fin = openFileInput("det.txt");
            int c;
            String temp="";
            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            if(temp.length()>1)
            {
                etqr.setText(temp.split(":")[0]);
                etqr2.setText(temp.split(":")[1]);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        File file = new File( "det.txt");




        btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (etqr.getText().toString().trim().length() == 0) {
                    Toast.makeText(MainActivity.this, "Enter Full Name!", Toast.LENGTH_SHORT).show();
                } else if (etqr2.getText().toString().trim().length() == 0) {
                    Toast.makeText(MainActivity.this, "Enter UserName!", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        String name = etqr.getText().toString();
                        String un = etqr2.getText().toString();
                        try {
                            key = Integer.parseInt(etqr3.getText().toString());
                        }catch (Exception e)
                        {
                            Toast.makeText(MainActivity.this, "Key Should be Number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String enc = "";
                        for (int i = 0; i < name.length(); i++) {
                            if (name.charAt(i) == 32)
                                enc += " ";
                            else
                                enc += (char) (name.charAt(i) + key);
                        }
                        enc += ":";
                        for (int i = 0; i < un.length(); i++) {
                            if (un.charAt(i) == 32)
                                enc += " ";
                            else
                                enc += (char) (un.charAt(i) + key);
                        }


                        enc = enc + ":" + Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                        try {
                            FileOutputStream fOut = openFileOutput("det.txt", Context.MODE_PRIVATE);
                            String str = etqr.getText().toString()+":"+etqr2.getText().toString();
                            fOut.write(str.getBytes());
                            fOut.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        bitmap = TextToImageEncode(enc);
                        iv.setImageBitmap(bitmap);
                       // String path = saveImage(bitmap);  //give read write permission
                     //   Toast.makeText(MainActivity.this, "QRCode saved to -> " + path, Toast.LENGTH_SHORT).show();
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d("dirrrrrr", "" + wallpaperDirectory.mkdirs());
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();   //give read write permission
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";

    }

    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
