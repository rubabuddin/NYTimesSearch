package com.rubabuddin.nytimessearch.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by rubab.uddin on 10/16/2016.
 */
@Parcel
public class Article{

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
    public int getPhotoHeight() { return photoHeight; }
    public int getPhotoWidth() { return photoWidth; }


    String webUrl;
    String headline;

    //image parameters
    String photo;
    String subtype;
    int photoHeight;
    int photoWidth;

    //empty constructor needed for Parceler
    public Article(){
    }

    public Article(JSONObject jsonObject){
        try{
            this.webUrl = jsonObject.getString("web_url");
            if(this.webUrl == null || this.webUrl.isEmpty()){
                this.webUrl = jsonObject.getString("url");
                Log.d("Values", this.webUrl.toString());
            }

            this.headline = jsonObject.getJSONObject("headline").getString("main");
            if(this.headline == null || this.headline.isEmpty()){
                this.headline = jsonObject.getString("title");
                Log.d("Values", this.headline.toString());
            }

            this.photoHeight = 0;
            this.photoWidth = 0;

            int breakTrigger = 0;

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            int objectNum = 0;
            //find photos that aren't thumbnails
            if(multimedia.length() > 0){
                for(int i =0; i<multimedia.length(); i++){
                    subtype = multimedia.getJSONObject(i).getString("subtype");
                    if(subtype != "thumbnail" && subtype != null){
                        objectNum = i;
                        this.photoHeight = multimedia.getJSONObject(i).getInt("height");
                        this.photoWidth = multimedia.getJSONObject(i).getInt("width");
                        breakTrigger = 1;
                        break;
                    }
                    if(breakTrigger == 1)
                        break;
                }
                if(this.photoHeight == 0)
                    this.photoHeight = 800;
                if(this.photoWidth == 0)
                    this.photoWidth = 600;

                JSONObject multimediaJson = multimedia.getJSONObject(objectNum);
                this.photo = "http://www.nytimes.com/" + multimediaJson.getString("url");
            } else {
                this.photo = "";
                this.photoHeight = 800;
                this.photoWidth = 600;
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
