package ru.ger_dmitriy.virtualtourandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import static android.content.ContentValues.TAG;

public class compressFile extends AsyncTask<String, Void, String> {

    private Context mContext;
    private boolean flag = false;

    public compressFile (Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            if (!params[1].equals("")){ //if receive a signal (it's the last file)
                flag = true;
            }
            Bitmap bitmap = ImageUtils.getInstant().getCompressedBitmap(params[0]);
            //ByteArrayOutputStream out = new ByteArrayOutputStream();
            FileOutputStream fileOuputStream =
                    new FileOutputStream(params[0]);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOuputStream);

            fileOuputStream.flush();
            fileOuputStream.close();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute: ");
        Toast toast;
        if (!flag)
            return;
        if(result.equals("")) {
            toast = Toast.makeText(mContext, "files compress successfully", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            toast = Toast.makeText(mContext, "Error: " + result, Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
