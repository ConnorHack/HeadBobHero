package com.example.ben.headbobhero;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

    public static RegisteredSong ParseJSON(String filePath, String JSON)
    {
        Gson gson = new Gson();
        try
        {
            String json = "";
            BufferedReader buff = new BufferedReader(new FileReader(filePath));
            RegisteredSong obj = gson.fromJson(buff, RegisteredSong.class);
            return obj;

        }
        catch(IOException eee)
        {
            eee.printStackTrace();
        }
        return null;
    }


    //TODO: Temp array of the JSON strings

    public static void writeJSONToFile(String json)
    {
        try
        {
            FileWriter writer = new FileWriter("/songs/aSong.json");
            writer.write(json);
            writer.close();
        }
        catch(IOException ee)
        {
            ee.printStackTrace();
        }

    }
}
