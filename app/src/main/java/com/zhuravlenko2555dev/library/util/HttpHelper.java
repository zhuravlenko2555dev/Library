package com.zhuravlenko2555dev.library.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by zhura on 15.03.2018.
 */

public class HttpHelper {
    String response = "";
    URL url;
    HttpURLConnection conn = null;
    int responseCode = 0;

    public String sendRequest(String path, HashMap<String, String> params) {
        try {
            Log.d("HttpConnectionService", "Starting process to connect path: " + path);
            url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            /*conn.setRequestProperty("connection", "close");//Jellybean is having an issue on "Keep-Alive" connections*/
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            /*conn.setRequestProperty("Content-Type", "application/text; charset=UTF-8");*/
        } catch (IOException ioe) {
            Log.d("HttpConnectionService", "Problem in getting connection.");
            ioe.printStackTrace();
        } catch (Exception e) {
            Log.d("HttpConnectionService", "Problem in getting connection. Safegaurd catch.");
            e.printStackTrace();
        }

        OutputStream os = null;
        try {
            if (null != conn) {
                os = conn.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(params));
                writer.flush();
                writer.close();
                os.close();

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            responseCode = conn.getResponseCode();
                        } catch (Exception e) { //IOException
                            e.printStackTrace();
                        }
                    }
                });
                t.run();
            }
        } catch (Exception e) { //IOException
            Log.d("HttpConnectionService", "Problem in getting outputstream and passing parameter.");
            e.printStackTrace();
        }

        if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            Log.d("HttpConnectionService", "Connection success to path: " + path);
            String line;
            BufferedReader br = null;

            //getting the reader instance from connection
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                try {
                    if (null != conn) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    }
                } catch (IOException e) {
                    Log.d("HttpConnectionService", "Problem with opening reader.");
                    e.printStackTrace();
                }
            }

            if (responseCode == HttpsURLConnection.HTTP_NOT_FOUND) {
                if (null != conn) {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
            }

            //reading the responseJson from stream
            try {
                if (null != br) {
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("HttpConnectionService", "output: " + line);
                    }
                }
            } catch (IOException e) {
                response = "";
                Log.d("HttpConnectionService", "Problem in extracting the result. " + Integer.toString(responseCode));
                e.printStackTrace();
            }
        } else {
            response = "";
        }

        /*conn.disconnect();*/

        if (conn != null) {
            conn.disconnect();
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                Log.d("HttpConnectionService", "entry.Key: " + entry.getKey());
                Log.d("HttpConnectionService", "entry.Value: " + entry.getValue());
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (Exception e) {
            Log.d("HttpConnectionService", "Problem in getPostDataString while handling params.");
            e.printStackTrace();
            return "";
        }

        Log.d("-----", result.toString());

        return result.toString();
    }
}
