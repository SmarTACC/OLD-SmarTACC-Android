package com.ort.smartacc;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class RequestTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... uri) {
        String responseString=null;
        HttpURLConnection connection=null;
        try {
            connection = (HttpURLConnection) new URL(uri[0]).openConnection();
            if(connection.getResponseCode()==200){
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
                String line;
                StringBuilder result = new StringBuilder();
                while((line = bufferedReader.readLine()) != null){
                    result.append(line);
                }
                responseString = result.toString();
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
    }
}