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
            String requestURL = "http://192.168.0.2:8080/";

            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
         /*   multipart.addFormField("param_name_1", "param_value");
            multipart.addFormField("param_name_2", "param_value");
            multipart.addFormField("param_name_3", "param_value");*/
            for (String param : params) {
                multipart.addFilePart("uploads[]", new File(param));
                Log.d(TAG, param);
            }
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
