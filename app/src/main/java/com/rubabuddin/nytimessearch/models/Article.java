package com.rubabuddin.nytimessearch.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rubab.uddin on 10/16/2016.
 */

public class Article {

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getPhoto() {
        return photo;
    }

    public String getSubtype() {
        return subtype;
    }

    String webUrl;
    String headline;
    String photo;
    String subtype;

    public Article(JSONObject jsonObject){
        try{
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            int objectNum = 0;
            //find photos that aren't thumbnails
            if(multimedia.length() > 0){
                for(int i =0; i<multimedia.length(); i++){
                    subtype = multimedia.getJSONObject(i).getString("subtype");
                    if(subtype != "thumbnail"){
                        objectNum = i;
                        break;
                    }
                }

                JSONObject multimediaJson = multimedia.getJSONObject(objectNum);
                this.photo = "http://www.nytimes.com/" + multimediaJson.getString("url");
                Log.d("DEBUG", photo.toString());
            } else {
                this.photo = "";
            }
        } catch (JSONException e){

        }

    }

    public static ArrayList<Article> fromJSONArray(JSONArray array){
        ArrayList<Article> results = new ArrayList<>();

        for(int x = 0; x < array.length(); x++){
            try{
                results.add(new Article(array.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

}
