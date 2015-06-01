package com.darincritchlow.assignment8.cs3270a8;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetCanvasCourses extends AsyncTask<String, Integer, String>{

    String AUTH_TOKEN = Authorization.AUTH_TOKEN;
    String rawJson = "";

    @Override
    protected String doInBackground(String... params) {

        Log.d("test", "In AsyncTask GetCanvasCourses");

        try {
            URL url = new URL("https://weber.instructure.com/api/v1/courses");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + AUTH_TOKEN);
            conn.connect();
            int status = conn.getResponseCode();
            if (status == 200 || status == 201){
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                rawJson = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);


    }
}
