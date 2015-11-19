package com.example.ben.headbobhero;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniellegonzalez on 10/6/15.
 */
public class RegisteredSong {

    //TODO: use Song object for these things instead once it's created
    private String songName = "";
    private String songPath = "";
    private int highestScore = 0;
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

    public void addBobCollectionToPattern(List<HeadBob> bobCollection)
    {
        for(int i = 0; i < bobCollection.size(); i++)
        {
            addBobToPattern(bobCollection.get(i));
        }
    }

    public boolean hasRecording() {
        return this.bobPattern.isEmpty();
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

    public int getHighestScore() {return this.highestScore;}
    public void setHighestScore(int newHighest) {this.highestScore = newHighest;}

    public int getDifficulty()
    {
        return this.difficulty;
    }
    public void setNewDifficulty(int diff) {
        this.difficulty = diff;
    }
}

