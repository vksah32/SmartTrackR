package com.vivek.snapshary;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PhotoService extends Service {
    public static FileObserver observer;
    public final String TAG = "DEBUXX";

    public PhotoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        final String pathToWatch = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera/";
        Toast.makeText(this, "Session Started  watching" + pathToWatch, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "LALALA");
        observer = new FileObserver(pathToWatch) { // set up a file observer to watch this directory on sd card

            @Override
            public void onEvent(int event, String file) {
                //if(event == FileObserver.CREATE && !file.equals(".probe")){ // check if its a "create" and not equal to .probe because thats created every time camera is launched
                switch(event) {
                    case FileObserver.CREATE:
                        Log.d(TAG, "CREATE:" + pathToWatch+ file);
                        uploadPic(pathToWatch+ file);
                        break;
                    case FileObserver.DELETE:
                        Log.d(TAG, "DELETE:" + pathToWatch);
                        break;
                }

                //}
            }
        };
        observer.startWatching(); //START OBSERVING
        return super.onStartCommand(intent, flags, startId);
    }

    public void uploadPic(String file){
        byte[] data = null;
        try {
            data = readInFile(file);
        } catch (Exception e){
            Log.d(TAG, e.getMessage());
        }

        if (data != null){
            ParseFile newPic = new ParseFile("l.jpg", data);
            newPic.saveInBackground();
            MainActivity.mSession.put("Pictures2", newPic);
            MainActivity.mSession.saveInBackground();
        }







    }

    private byte[] readInFile(String path) throws IOException {
        // TODO Auto-generated method stub
        byte[] data = null;
        File file = new File(path);
        InputStream input_stream = new BufferedInputStream(new FileInputStream(
                file));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        data = new byte[16384]; // 16K
        int bytes_read;
        while ((bytes_read = input_stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytes_read);
        }
        input_stream.close();
        return buffer.toByteArray();

    }
}
