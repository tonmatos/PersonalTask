package com.example.waniltonfilho.personaltasks.model.http;

import android.util.Base64;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Wanilton on 08/04/2016.
 */
public class LoginAuthenticationService {
    private LoginAuthenticationService() {
    }

    ;

    //private static final String URL = "http://inovacoes.cast.com.br/api";
    private static final String URL = "http://10.0.3.2:3000/api/v1/users";


    public static HttpURLConnection getAuthentication(String user, String password) {
        try{
            java.net.URL url = new URL(URL);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String sign = user + ":" + password;
            conn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(sign.getBytes(), Base64.NO_WRAP).replace("\n", ""));
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);


            int responseCode = conn.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                return conn;
            }

        }catch (Exception e){
            Log.e(LoginAuthenticationService.class.getName(), e.getMessage());
        }
        return null;
    }
}