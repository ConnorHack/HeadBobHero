package com.example.ben.headbobhero;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity {

    static final int ACTIVITY_RESULT_DONE = 1;

    private SongAdapter songAdpt;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_import) {
            Intent playIntent = new Intent(MainActivity.this, ImportActivity.class);
            playIntent.setAction("import_song");
            startActivityForResult(playIntent, ACTIVITY_RESULT_DONE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initList();
        ListView lv = (ListView) findViewById(R.id.listView);
        songAdpt = new SongAdapter(this,android.R.id.text1, songList);


        lv.setAdapter(songAdpt);
        // React to user clicks on item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {
                RegisteredSong clickedSong = songList.get(position);

                Intent playIntent = new Intent(MainActivity.this, SongActivity.class);
                playIntent.putExtra("registered_song", JsonUtility.toJSON(clickedSong));
                playIntent.setAction("song_menu");
                startActivityForResult(playIntent, ACTIVITY_RESULT_DONE);

            }
        });
    }


    ArrayList<RegisteredSong> songList= new ArrayList<RegisteredSong>();
    private void initList() {
        songList.clear();
        songList.addAll(JsonUtility.getAllSongs(new File(getFilesDir().getPath())));

        Uri contentUris[] = {
                Uri.parse("android.resource://com.example.ben.headbobhero/" + R.raw.song1),
                Uri.parse("android.resource://com.example.ben.headbobhero/" + R.raw.song2),
                Uri.parse("android.resource://com.example.ben.headbobhero/" + R.raw.song3),
                Uri.parse("android.resource://com.example.ben.headbobhero/" + R.raw.song4)
        };

        boolean hasSong[] = {
                false, false, false, false
        };

        for(RegisteredSong song: songList) {
            for(int i = 0; i < contentUris.length; i++) {
                if(song.getSongPath().equals(contentUris[i].toString())) {
                    hasSong[i] = true;
                }
            }
        }

        if(!hasSong[0]) {
            songList.add(new RegisteredSong("September - Earth, Wind & Fire", contentUris[0].toString(), 0, new ArrayList<HeadBob>()));
        }

        if(!hasSong[1]) {
            songList.add(new RegisteredSong("Juke Box Hero - Foreigner", contentUris[1].toString(), 0, new ArrayList<HeadBob>()));
        }

        if(!hasSong[2]) {
            songList.add(new RegisteredSong("Danger Zone - Kenny Loggins", contentUris[2].toString(), 0, new ArrayList<HeadBob>()));
        }

        if(!hasSong[3]) {
            songList.add(new RegisteredSong("What is Love - Haddaway", contentUris[3].toString(), 0, new ArrayList<HeadBob>()));
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (ACTIVITY_RESULT_DONE) : {
                if (resultCode == Activity.RESULT_OK) {
                    initList();
                    songAdpt.notifyDataSetChanged();
                }
                break;
            }
        }
    }
}
