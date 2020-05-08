package com.tika.app2;

import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class Photosave extends AppCompatActivity {


    Button save;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photosave);




        Drawable drawable = getResources().getDrawable(R.drawable.background);
        bitmap = ((BitmapDrawable) drawable).getBitmap();
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);

                File file = new File(directory, "UniqueFileName" + ".png");
                if (!file.exists() || file.exists()) {
                    Log.i("TAG", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+directory.getAbsolutePath());
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

    }







}
