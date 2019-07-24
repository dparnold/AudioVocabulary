package com.dparnold.audiovocabulary;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dparnold.audiovocabulary.Helper.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;

public class FileDownload extends AppCompatActivity {
    private ImageView imageView;
    private Toast errorToast;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);
        // Requesting Permissions
        if (ContextCompat.checkSelfPermission(FileDownload.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(FileDownload.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1); // request code can be arbitrary
        }
        imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.ic_sleep);
        errorToast=new Toast(FileDownload.this);
        errorToast.makeText(FileDownload.this,"Nothing happened in the download function", Toast.LENGTH_LONG).show();
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        // https://medium.com/@crossphd/android-image-loading-from-a-string-url-6c8290b82c5e
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private class DownloadAudioTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urlParams) {
            int count;
            try {
                URL url = new URL(urlParams[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conexion.getContentLength();
                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                String filePath=Environment.getExternalStorageDirectory()+"/audio.mp3";
                OutputStream output = new FileOutputStream(filePath);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
            }
            return null;
        }
    }

    public void downloadImage(View view){
        new DownloadImageTask(imageView).execute("https://i.ytimg.com/vi/dGFSjKuJfrI/maxresdefault.jpg");
    }
    public void showImage(View view){
        new DownloadAudioTask().execute("https://audio1.spanishdict.com/audio?lang=es&text=exito");
        String filePath = Environment.getExternalStorageDirectory()+"/audio.mp3";
        mediaPlayer = new  MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


//    public void play(){
//
//        final int resource = FileDownload.this.getResources().getIdentifier("package1_en_001",
//                "raw", "com.dparnold.audiovocabulary");
//        MediaPlayer mediaPlayer = MediaPlayer.create(FileDownload.this, resource);
//        mediaPlayer.start(); // no need to call prepare(); create() does that for you
//
//        // This method uses handlers to play the audio files in the right order with the set delays
//        // playHandler.postDelayed gives the Runnable and the delay time
//        // First the runnables are defined
//        firstRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if(playing) {
//                    if (mediaPlayer != null) {
//                        mediaPlayer.stop();
//                        mediaPlayer.release();
//                        mediaPlayer = null;
//                    }
//                    textViewLang1.setText("");
//                    final int resource = MainActivity.this.getResources().getIdentifier("package1_en_"
//                                    + Util.int2StringDigits(vocables.get(vocablesIndex).getID(), 3),
//                            "raw", "com.dparnold.audiovocabulary");
//                    mediaPlayer = MediaPlayer.create(MainActivity.this, resource);
//                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mediaPlayer) {
//                            if (playing) {
//                                playHandler.postDelayed(secondRunnable, delay);
//                            }
//                        }
//                    });
//                    mediaPlayer.start();
//                    textViewLang0.setText(vocables.get(vocablesIndex).getLangKnown());
//                }
//            }
//        };
//        secondRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if(playing) {
//                    if (mediaPlayer != null) {
//                        mediaPlayer.stop();
//                        mediaPlayer.release();
//                        mediaPlayer = null;
//                    }
//                    final int resource = MainActivity.this.getResources().getIdentifier("package1_es_"
//                                    + Util.int2StringDigits(vocables.get(vocablesIndex).getID(), 3),
//                            "raw", "com.dparnold.audiovocabulary");
//                    mediaPlayer = MediaPlayer.create(MainActivity.this, resource);
//                    textViewLang1.setText(vocables.get(vocablesIndex).getLangForeign());
//                    // Next vocable in the list
//                    if (vocablesIndex == vocables.size() - 1) {
//                        vocablesIndex = 0;
//                        // Shuffling the list again
//                        // !!! can lead to having the same vocable twice in a row
//                        Collections.shuffle(vocables);
//                    } else {
//                        vocablesIndex += 1;
//                    }
//                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mediaPlayer) {
//
//                            // Starting with the word in the known language again
//                            textViewLang0.setText("");
//                            playHandler.postDelayed(firstRunnable, delay);
//
//                        }
//                    });
//                    mediaPlayer.start();
//                }
//            }
//        };
//        // Start with the first runnable right away (delay = 0)
//        playHandler.postDelayed(firstRunnable,0);
//    }

}
