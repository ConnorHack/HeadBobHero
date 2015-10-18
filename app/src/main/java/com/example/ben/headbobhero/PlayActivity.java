package com.example.ben.headbobhero;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

public class PlayActivity extends Activity {

    //Divide the frame by 1000 to calculate how many times per second the screen will update.
    public static final int FRAME_RATE = 20; //50 frames per second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);


        Handler h1 = new Handler();
        Handler h2 = new Handler();
        h1.postDelayed(new Runnable() {
            @Override
            public void run() {
                initGfx();
            }
        }, 1000);

        h2.postDelayed(new Runnable() {
            @Override
            public void run() {
                playMusic();
            }
        }, 2850);
    }

    private Handler frame = new Handler();

    //the context supplied "getApplicationContext" may not be correct but works
    public void playMusic(){
        final MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.song1);
        mediaPlayer.seekTo(36000);
        mediaPlayer.start();
        ((GameBoard)findViewById(R.id.the_canvas)).setGameOverRunnable(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.release();
            }
        });
    }

    synchronized public void initGfx() {
        frame.removeCallbacks(frameUpdate);
        frame.postDelayed(frameUpdate, FRAME_RATE);
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
            ((GameBoard)findViewById(R.id.the_canvas)).invalidate();
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }
    };
}
