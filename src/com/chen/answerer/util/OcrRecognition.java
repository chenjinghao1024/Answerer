package com.chen.answerer.util;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class OcrRecognition {
    //设置APPID/AK/SK
    private static final String APP_ID = "11307860";
    private static final String API_KEY = "uytf55LSP7A12H8FKBU7cweK";
    private static final String SECRET_KEY = "QWXHwG3MVplsYpLg5xX64GyHBXUYI9Ba";
    private static AipOcr client= new AipOcr(APP_ID, API_KEY, SECRET_KEY);



    public static String imageToText(String path){
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
       try {
           JSONObject res = client.basicGeneral(path, new HashMap<>());
           JSONArray jsonArray = (JSONArray) res.get("words_result");

           StringBuilder result = new StringBuilder();
           for (int i = 0; i < jsonArray.length(); i++) {
               JSONObject json = (JSONObject) jsonArray.get(i);
               result.append(json.get("words"));
           }
           return result.toString();
       }catch (Exception e){
           return null;
       }
    }
}