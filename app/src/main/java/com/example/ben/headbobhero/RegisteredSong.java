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
    public RegisteredSong(){}
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

    public void setSongPath(String path)
    {
        this.songPath = path;
    }
    public String getSongPath()
    {
        return this.songPath;
    }

    public String getSongName()
    {
        return this.songName;
    }
    public void setSongName(String name)
    {
        this.songName = name;
    }

    public int getDifficulty()
    {
        return this.difficulty;
    }

    public void setNewDifficulty(int diff) {
        this.difficulty = diff;
    }
}
