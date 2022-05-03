package com.lamaq.midnight_walls.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;
import com.lamaq.midnight_walls.MainActivity;
import com.lamaq.midnight_walls.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FullScreenWallpaper extends AppCompatActivity {
    String originalUrl = "";
    ImageView photoView;
    Bitmap bitmap;
    Button buttonSetWallpaper, buttonSaveWallpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_wallpaper);

        Intent intent = getIntent();
        originalUrl = intent.getStringExtra("originalUrl");

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        final ProgressBar saveProgressBar = (ProgressBar) findViewById(R.id.SaveProgressBar);
        final ProgressBar setProgressBar = (ProgressBar) findViewById(R.id.SetProgressBar);

        photoView = (ImageView) findViewById(R.id.photoView);
        Glide.with(getApplicationContext())
                .load(originalUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        setProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(photoView);

        buttonSetWallpaper = (Button) findViewById(R.id.buttonSetWallpaper);
        buttonSetWallpaper.setOnClickListener(view -> {
 //           Toast.makeText(this, "Wallpaper set Button Clicked", Toast.LENGTH_SHORT).show();
            buttonSetWallpaper.setVisibility(View.INVISIBLE);
            setProgressBar.setVisibility(View.VISIBLE);
            @SuppressLint("StaticFieldLeak")
            AsyncTask<String, String, String> demoSetWallpaper = new AsyncTask<String, String, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "done";
                }

                @Override
                protected void onPostExecute(String s) {
                    if (s.equals("done")){
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(FullScreenWallpaper.this);
                        bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
                        try {
                            wallpaperManager.setBitmap(bitmap);
                            Toast.makeText(FullScreenWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                            buttonSetWallpaper.setVisibility(View.VISIBLE);
                            setProgressBar.setVisibility(View.INVISIBLE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            demoSetWallpaper.execute();
        });

        buttonSaveWallpaper = (Button) findViewById(R.id.buttonSaveWallpaper);
        buttonSaveWallpaper.setOnClickListener(view -> {
 //           Toast.makeText(this, "Wallpaper set Button Clicked", Toast.LENGTH_SHORT).show();
            buttonSaveWallpaper.setVisibility(View.INVISIBLE);
            saveProgressBar.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Wallpaper Saved", Toast.LENGTH_SHORT).show();
            saveProgressBar.setVisibility(View.GONE);
            try {
                saveImageToExternal("wallpaper", bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
    public void saveImageToExternal(String imgName, Bitmap bm) throws IOException {
        //Create Path to save Image
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"midnight walls"); //Creates app specific folder
        path.mkdirs();
        File imageFile = new File(path, imgName+".png"); // Imagename.png
        FileOutputStream out = new FileOutputStream(imageFile);
        try{
            bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
            out.flush();
            out.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this, new String[] { imageFile.getAbsolutePath() }, null,new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        } catch(Exception e) {
            throw new IOException();
        }
    }
}