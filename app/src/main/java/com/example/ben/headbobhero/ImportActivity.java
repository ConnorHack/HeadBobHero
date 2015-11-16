package com.example.ben.headbobhero;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportActivity extends Activity {
    private ImportAdapter impAdpt;
    Map<Long, String> songMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        ListView lv = (ListView) findViewById(R.id.listView);

        songMap = getMusicMap();

        impAdpt = new ImportAdapter(songMap);
        lv.setAdapter(impAdpt);

        final Activity importActivity = this;

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
                Map.Entry<Long, String> songItem = (Map.Entry<Long, String>) parentAdapter.getItemAtPosition(position);

                Uri contentUri = ContentUris.withAppendedId(
                        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songItem.getKey());

                RegisteredSong song = new RegisteredSong(songItem.getValue(), contentUri.toString(), 0, new ArrayList<HeadBob>());

                String songJson = JsonUtility.toJSON(song);
                JsonUtility.writeJSONToFile(importActivity, songJson, song.getSongName());
                setResult(Activity.RESULT_OK);
                finish();
            }

        });
    }

    public Map<Long, String> findMusic() {

        String selection = (MediaStore.Audio.Media.IS_MUSIC + "!= 0");

        Map<Long, String> allSongs = new HashMap<Long, String>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, selection, null, null);
        if (cursor == null) {
            // query failed, handle error.
            //this should have a popup that there was an issue trying trying to get music
        } else if (!cursor.moveToFirst()) {
            // This should have a popup that no music was found on the device
        } else {
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            do {
                long thisId = cursor.getLong(idColumn);
                String thisTitle = cursor.getString(titleColumn);
                // below is processing the found audio
                //if (cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC) != 0) {
                allSongs.put(thisId, thisTitle);
            } while (cursor.moveToNext());
        }
        allSongs.put(Long.valueOf(R.raw.song1), "September - Earth, Wind & Fire");
        allSongs.put(Long.valueOf(R.raw.song2), "Juke Box Hero - Foreigner");
        allSongs.put(Long.valueOf(R.raw.song3), "Danger Zone - Kenny Loggins");
        allSongs.put(Long.valueOf(R.raw.song4), "What is Love - Haddaway");
        return allSongs;
    }

    public Map<Long, String> getMusicMap() {
        return findMusic();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_import, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
