package ru.ger_dmitriy.virtualtourandroid;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import static android.content.ContentValues.TAG;

public class clientRun extends AsyncTask<String, Void, Void> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            String charset = "UTF-8";
            String requestURL = "http://192.168.0.2/upload";

            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
            multipart.addFormField("login", params[0]);
            multipart.addFormField("date", params[1]);
            //multipart.addFormField("param_name_3", "param_value");
            multipart.addFilePart("uploads[]", new File(params[2]));
            Log.d(TAG, params[0] + ' ' + params[1] + ' ' + params[2]);
            String response = multipart.finish(); // response from server.
            Log.d(TAG, "onPostExecute: " + params.toString() + "\r\n" + response);
           /* Toast toast = Toast
                    .makeText( response, Toast.LENGTH_SHORT);
            toast.show();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute: ");
      /*  if(resultString != null) {
            Toast toast = Toast.makeText(, resultString, Toast.LENGTH_SHORT);
            toast.show();
        }
*/
    }
}
