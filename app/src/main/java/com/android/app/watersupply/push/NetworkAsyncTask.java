package com.android.app.watersupply.push;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Beryl on 03-Oct-18.
 */

public class NetworkAsyncTask extends AsyncTask<Void, Void, String> {


    private String to;
    private String body;
    private String title;

    public NetworkAsyncTask(String to, String body, String title) {

        this.to = to;
        this.body = body;
        this.title=title;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            final String apiKey = "AIzaSyDTAq1yked6vjj04DSTEB_ulIuRNaOevp8";
            URL url = new URL("https://fcm.googleapis.com/fcm/send24");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);
            conn.setDoOutput(true);
            JSONObject message = new JSONObject();
            message.put("to", to);
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", body);
            notification.put("click_action","com.example.berylsystems.watersupply.activities.SplashActivity");
            message.put("data", notification);
            message.put("notification",notification);
            OutputStream os = conn.getOutputStream();
            os.write(message.toString().getBytes());

            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + message.toString());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Code : " + conn.getResponseMessage());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

}
