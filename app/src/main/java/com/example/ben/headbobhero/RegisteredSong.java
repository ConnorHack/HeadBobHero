package com.example.ben.headbobhero;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by daniellegonzalez on 10/6/15.
 */
public class RegisteredSong {

    //TODO: use Song object for these things instead once it's created
    private String songName = "";
    private String songPath = "";

    private int difficulty = 0;
    private ArrayList<HeadBob> bobPattern = new ArrayList<HeadBob>();

    public RegisteredSong(String name, String path, int difficulty, ArrayList<HeadBob> bobs)
    {
        this.songName = name;
        this.songPath= path;
        this.difficulty = difficulty;
        this.bobPattern = bobs;
    }

    public void addBobToPattern(HeadBob bob)
    {
        this.bobPattern.add(bob);
    }

    public ArrayList<HeadBob> getBobPattern()
    {
        return this.bobPattern;
    }

    public void resetBobPattern()
    {
        this.bobPattern.clear();
    }

    public String getSongPath()
    {
        return this.songPath;
    }

    public String getSongName()
    {
        return this.songName;
    }

    public int getDifficulty()
    {
        return this.difficulty;
    }

    public void setNewDifficulty(int diff) {
        this.difficulty = diff;
    }

    // Serialize the Song data to JSON
    public static String toJSon(RegisteredSong song)
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
}

