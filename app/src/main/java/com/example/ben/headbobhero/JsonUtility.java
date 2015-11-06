package com.example.ben.headbobhero;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

/**
 * Created by daniellegonzalez on 10/7/15.
 */
public class JsonUtility {

    public JsonUtility(){}

    // Serialize the Song data to JSON
    public static String toJSON(RegisteredSong song)
    {
        Gson gson = new Gson();
        return gson.toJson(song);
    }
    // load .json file & return string
    public static String loadJSON(String filePath)
    {
        String result = null;
        try
        {
            BufferedReader buff = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line = buff.readLine();
            while (line != null) {
                sb.append(line);
                line = buff.readLine();
            }
            result = sb.toString();

        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // parse JSON into RegisteredSong
    public static RegisteredSong ParseJSON(String JSON)
    {
        Gson gson = new Gson();
        RegisteredSong obj = gson.fromJson(JSON, RegisteredSong.class);
        return obj;
    }


    public static ArrayList<RegisteredSong> getAllSongs(File directoryPath)
    {
        ArrayList<RegisteredSong> songs = new ArrayList<RegisteredSong>();

        File[] files = directoryPath.listFiles();
        if(files != null) {
            for (File file : files) {
                String filePath = file.getAbsolutePath();
                RegisteredSong aSong = loadAndParseJSON(filePath);
                if(aSong != null) {
                    songs.add(aSong);
                }
            }
        }
        return songs;
    }


    public static RegisteredSong loadAndParseJSON(String filePath)
    {
        try
        {
            Gson gson = new Gson();
            BufferedReader buff = new BufferedReader(new FileReader(filePath));
            RegisteredSong obj = gson.fromJson(buff, RegisteredSong.class);
            return obj;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


    public static void writeJSONToFile(Context context, String json, String songName)
    {
        try
        {
            FileOutputStream writer = context.openFileOutput(songName + ".json", 0);
            writer.write(json.getBytes());
            writer.close();
        }
        catch(IOException ee)
        {
            ee.printStackTrace();
        }

    }
}
