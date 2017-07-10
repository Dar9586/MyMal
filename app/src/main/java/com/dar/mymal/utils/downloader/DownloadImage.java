package com.dar.mymal.utils.downloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by stopp on 30/06/2017.
 */

public class DownloadImage extends AsyncTask<String, Void, Void> {
    ImageView img;
    Drawable kkk;
    String path="",name="";
    public DownloadImage(ImageView x){
        img=x;
    }
    public DownloadImage(ImageView x,String savePath,String name){
        img=x;
        path=savePath;
        this.name=name+".jpg";
        Log.i("Pathgenerator",path+"     "+name);
    }
    @Override
    protected Void doInBackground(String... arg0) {
        kkk= downloadImage(arg0[0]);
        return null;
    }
    @Override
    protected void onPostExecute(Void image)
    {
        img.setImageDrawable(kkk);
    }
    private Drawable downloadImage(String _url)
    {
        URL url;
        BufferedOutputStream out;
        InputStream in;
        BufferedInputStream buf;
        try {
            url = new URL(_url);
            in = url.openStream();
            buf = new BufferedInputStream(in);
            Bitmap bMap = BitmapFactory.decodeStream(buf);
            if (in != null) {
                in.close();
            }
            if (buf != null) {
                buf.close();
            }
            if(path!="")saveImage(bMap);
            return new BitmapDrawable(bMap);

        } catch (Exception e) {
            Log.e("Error reading file", e.toString());
        }

        return null;
    }

        private void saveImage(Bitmap finalBitmap) {
            File file = new File(path,name);
            if (!file.exists ()){
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }}
}
