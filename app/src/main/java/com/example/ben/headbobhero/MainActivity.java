package com.example.ben.headbobhero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends Activity {

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
        SongAdapter songAdpt = new SongAdapter(this,android.R.id.text1, songList);

        lv.setAdapter(songAdpt);
        // React to user clicks on item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {

                Intent playIntent = new Intent(MainActivity.this, SongActivity.class);
                playIntent.setAction("song_menu");
                startActivity(playIntent);

            }
        });

    }


    ArrayList<RegisteredSong> songList= new ArrayList<RegisteredSong>();
    private void initList() {
        songList.add(new RegisteredSong("All Star", null, 1, null));
        songList.add(new RegisteredSong("Get Swifty", null, 1, null));
    }
}
