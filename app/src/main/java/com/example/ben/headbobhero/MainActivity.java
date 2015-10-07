package com.example.ben.headbobhero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
        ArrayAdapter<String> simpleAdpt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, songList);

        lv.setAdapter(simpleAdpt);
        // React to user clicks on item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {

                Intent playIntent = new Intent(MainActivity.this, PlayActivity.class);
                playIntent.setAction("play_headbobs");
                startActivity(playIntent);

            }
        });

    }

        /*findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(MainActivity.this, PlayActivity.class);
                playIntent.setAction("play_headbobs");
                startActivity(playIntent);
            }
        });

        findViewById(R.id.record_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recordingIntent = new Intent(MainActivity.this, RecordingActivity.class);
                recordingIntent.setAction("record_headbobs");
                startActivity(recordingIntent);
            }
        });*/


    ArrayList<String> songList= new ArrayList<String>();
    private void initList()
    {
        songList.add("song1");
        songList.add("song2");
    }

}
