package ru.ger_dmitriy.virtualtourandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import static android.content.ContentValues.TAG;

public class sendFile extends AsyncTask<String, Void, String> {

    private Context mContext;
    private boolean flag = false;

    public sendFile (Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String charset = "UTF-8";
            String requestURL = params[0];
            if (!params[4].equals("")){ //if receive a signal (it's the last file)
                flag = true;
            }
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
            multipart.addFormField("login", params[1]);
            multipart.addFormField("name", params[2]);
            //multipart.addFormField("param_name_3", "param_value");
            multipart.addFilePart("uploads", new File(params[3]));
            Log.d(TAG, params[1] + ' ' + params[2] + ' ' + params[3]);
            String response = multipart.finish(); // response from server.
            Log.d(TAG, "onPostExecute: " + "\r\n" + response);
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
        if(!flag) {     //if didn't receive the signal
            return;
        }
        if(result.equals("")) {
            MainActivity.showProgress(false);
            toast = Toast.makeText(mContext, "files sent successfully", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            MainActivity.showProgress(false);
            toast = Toast.makeText(mContext, "Error: " + result, Toast.LENGTH_SHORT);
            toast.show();
/*
            AlertDialog.Builder ad;

            ad = new AlertDialog.Builder(mContext);
            ad.setTitle("Ошибка");  // заголовок
            ad.setMessage("Повторить отправку?"); // сообщение
            ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {

                }
            });
            ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                }
            });
            ad.setCancelable(true);
            ad.show();*/
        }

    }
}
