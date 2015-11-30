package com.example.ben.headbobhero;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

public class PlayActivity extends Activity {

    //Divide the frame by 1000 to calculate how many times per second the screen will update.
    public static final int FRAME_RATE = 20; //50 frames per second

    private RegisteredSong song;
    private Handler playMusicHandler;

    private GameBoard playingGameBoard ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        final Intent intent = getIntent();
        song = JsonUtility.ParseJSON(intent.getStringExtra("registered_song"));

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //setting the specific URI as the media. getApplicationContext() returns the app's context which
        //is required to setDataSource
        System.out.println(Uri.parse(song.getSongPath()));
        try {
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(song.getSongPath()));
        } catch (IOException e) {
            System.out.println("------------------------ TESTING ---------------");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        //this should be done in its own thread according to android documentation, but this works
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            System.out.println("------------------------ HERERERHEJFHGHJDSHGFHJIJ ---------------");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        playMusicHandler = new Handler();

        playMusicHandler.postDelayed(playMusicDelay, 6850);
    }

    private Runnable playMusicDelay = new Runnable() {
        @Override
        public void run() {
            playMusic();
        }
    };

    @Override
    public void onBackPressed() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }

        playingGameBoard.pauseSong();
        createDialogPausePlaying().show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        playingGameBoard = (GameBoard)findViewById(R.id.the_canvas);
        playingGameBoard.setHeadBobs(song.getBobPattern());
        frame.removeCallbacks(frameUpdate);
        frame.postDelayed(frameUpdate, FRAME_RATE);

    }


    private Handler frame = new Handler();
    private MediaPlayer mediaPlayer;

    //the context supplied "getApplicationContext" may not be correct but works
    public void playMusic(){
        mediaPlayer.start();
        playingGameBoard.setGameOverRunnable(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.release();
                mediaPlayer = null;
                //get score from game & set as song's high score
               int newScore = playingGameBoard.score;
                if(newScore > song.getHighestScore())
                {
                    song.setHighestScore(newScore);
                }
                //update the song
                String writeSong = JsonUtility.toJSON(song);
                JsonUtility.writeJSONToFile(getApplicationContext(), writeSong, song.getSongName());
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updated_song", writeSong);
                setResult(Activity.RESULT_OK, resultIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play, menu);
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

    private Runnable frameUpdate = new Runnable() {
        @Override
        synchronized public void run() {
            frame.removeCallbacks(frameUpdate);
            //make any updates to on screen objects here
            //then invoke the on draw by invalidating the canvas
            playingGameBoard.invalidate();
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }
    };


    private void continuePlaying() {
        playingGameBoard.resetStartedRecording();
        playMusicHandler.postDelayed(playMusicDelay, 5000);
    }

    private void quitPlaying() {

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        playMusicHandler.removeCallbacks(playMusicDelay);
        super.onBackPressed();
    }

    /**
     * <h2>Create an alert dialog when the user presses the back button when
     * playing for a song</h2>
     *
     * Action 1: Quit the song
     * Action 2: Continue the song
     *
     * @return -  An alert dialog
     */
    private AlertDialog createDialogPausePlaying() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        return
                dialogBuilder
                        .setTitle("Song paused")
                        .setPositiveButton("Play", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                continuePlaying();
                            }
                        })
                        .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                quitPlaying();
                            }
                        }).create();
    }
}
