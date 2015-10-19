package com.example.ben.headbobhero;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
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
        try
        {
            JSONObject obj = new JSONObject();
            obj.put("SongName", song.getSongName());
            obj.put("SongPath", song.getSongPath());
            obj.put("Difficulty", song.getSongPath());

            JSONArray bobArray = new JSONArray();
            for(HeadBob bob : song.getBobPattern())
            {
                JSONObject bobObj = new JSONObject();
                bobObj.put("Direction", bob.direction);
                bobObj.put("Offset", bob.offset);
                bobArray.put(bobObj);
            }
            obj.put("BobPattern", bobArray);

            return obj.toString();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        return null;
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


    public static List<RegisteredSong> getAllSongs(String directoryPath)
    {
        List<RegisteredSong> songs = new ArrayList<RegisteredSong>();

        File f = new File(directoryPath);
        File[] files = f.listFiles();
        int fileCount = files.length;
        if (files != null)
        {
            for (int i = 0; i < files.length; i++) {
                String filePath = files[i].getAbsolutePath();
                RegisteredSong aSong = loadAndParseJSON(filePath);
                songs.add(aSong);
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


    public static void writeJSONToFile(String json, String songName)
    {
        try
        {
            FileWriter writer = new FileWriter("/songs/" + songName + ".json");
            writer.write(json);
            writer.close();
        }
        catch(IOException ee)
        {
            ee.printStackTrace();
        }

    }
}
