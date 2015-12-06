package com.example.ben.headbobhero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class SongActivity extends Activity {

    static final int ACTIVITY_RESULT_DONE = 1;

    RegisteredSong song;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_activiy);
        final Intent intent = getIntent();
        song = JsonUtility.ParseJSON(intent.getStringExtra("registered_song"));
        setTitle(song.getSongName());

        findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(SongActivity.this, PlayActivity.class);
                playIntent.putExtra("registered_song", JsonUtility.toJSON(song));
                playIntent.setAction("play_headbobs");
                startActivityForResult(playIntent, ACTIVITY_RESULT_DONE);
            }
        });

        findViewById(R.id.record_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recordingIntent = new Intent(SongActivity.this, RecordingActivity.class);
                recordingIntent.putExtra("registered_song", JsonUtility.toJSON(song));
                recordingIntent.setAction("record_headbobs");
                startActivityForResult(recordingIntent, ACTIVITY_RESULT_DONE);
            }
        });

        int score = song.getHighestScore();

        String scoreText = "No high score yet";

        if(score > 0) {
            scoreText = "High Score: " + score;
        }

       ((TextView) findViewById(R.id.highscore)).setText(scoreText);
    }

    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_song_activiy, menu);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (ACTIVITY_RESULT_DONE) : {
                if (resultCode == Activity.RESULT_OK) {
                    song = JsonUtility.ParseJSON(data.getStringExtra("updated_song"));
                }
                break;
            }
        }
    }
}
