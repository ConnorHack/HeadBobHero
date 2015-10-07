package com.example.ben.headbobhero;

import java.net.URI;

/**
 * Created by Joe on 10/6/2015.
 */
public class Song {
    private String songName;
    private URI songUri;

    public Song(String songName, URI songUri){
        this.songName = songName;
        this.songUri = songUri;
    }

    public String getSongName() {
        return songName;
    }

    public URI getSongUri() {
        return songUri;
    }
}
