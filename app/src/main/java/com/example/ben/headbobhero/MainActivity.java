package com.example.ben.headbobhero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
            startActivity(playIntent);
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
        //songList.add(new RegisteredSong("All Star", null, 0, new ArrayList<HeadBob>()));
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
