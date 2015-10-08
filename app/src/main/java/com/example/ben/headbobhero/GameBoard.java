package com.example.ben.headbobhero;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameBoard extends View implements SensorEventListener {
    private Paint p;
    private Paint textPaint;

    boolean hasInitializedBobs = false;
    public static List<HeadBob> headBobs = new ArrayList<HeadBob>();

    private Runnable gameOverRunnable = null;
    private Boolean calledGameOverRunnable = false;

    private final static String DEBUG = "GameBoard";

    private final Bitmap bm_bob_down;
    private final Bitmap bm_bob_left;
    private final Bitmap bm_bob_right;

    // Rate at which to collect values from sensors
    private static int RATE = SensorManager.SENSOR_DELAY_FASTEST;

    // Globals needed to collect values from device's sensors
    private SensorManager mSensorManager;
    private Sensor mSensorGravity;
    private float mGravity[];

    // Defines the headbob the system is waiting to receive
    private HeadBobDirection scanForHeadBob = null;
    // Defines if we have received a correct head-bob
    private Boolean hasScannedCorrectBob = false;
    private boolean scanning = false;

    private final String[] bobMatchStrings = {"Nice!", "Cool!", "Great!", "Awesome!"};

    private final String[] bobFailStrings = {"Boo", "Miss", "Fail", "Nope"};

    private int bobsMatched = 0;
    private int bobsMissed = 0;

    private Random random = new Random();


    private class GameFeedbackString {
        public String text = "";
    }
    private final GameFeedbackString gameFeedbackString = new GameFeedbackString();

    private Handler gameFeedbackRemoveHandler = new Handler();
    private Runnable gameFeedbackRemoveRunnable = new Runnable() {
        @Override
        public void run() {
            gameFeedbackString.text = "";
        }
    };

    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);

        p = new Paint();
        textPaint = new Paint();

        bm_bob_down = BitmapFactory.decodeResource(getResources(), R.drawable.bob_down);
        bm_bob_left = BitmapFactory.decodeResource(getResources(), R.drawable.bob_left);
        bm_bob_right = BitmapFactory.decodeResource(getResources(), R.drawable.bob_right);

        // Register the sensor listener with the specified rate
        // Initialize the sensor manager, gravity sensor, and gravity vector
        mGravity = new float[3];
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        // Register the listener to begin receiving updates form the gravity sensor
        mSensorManager.registerListener(this, mSensorGravity, RATE);
        // Check to ensure the gravity sensor is functional on the device
        if (mSensorGravity == null) {
            Log.e(DEBUG, "Device does not possess a gravity sensor. "
                    + "This application will not function properly.");
        }
    }

    public void setGameOverRunnable(Runnable runnable) {
        gameOverRunnable = runnable;
    }


    private void initializeHeadBobs() {

        if (headBobs.size() == 0) {
            headBobs = new ArrayList<HeadBob>();
            for (int i = 0; i < 10; i++) {
                Random r = new Random();

                int directionInt = r.nextInt(3);
                HeadBobDirection direction = null;

                switch (directionInt) {
                    case 0:
                        direction = HeadBobDirection.DOWN;
                        break;
                    case 1:
                        direction = HeadBobDirection.LEFT;
                        break;
                    case 2:
                        direction = HeadBobDirection.RIGHT;
                        break;
                }

                headBobs.add(new HeadBob(i * 128, direction));
            }
        }
    }

    private int getBobPercentage() {
        return Math.round(((float) bobsMatched / (float) (bobsMatched + bobsMissed)) * 100);
    }

    @Override
    synchronized public void onDraw(Canvas canvas) {
        //create a black canvas
        p.setColor(Color.BLACK);
        p.setAlpha(255);
        p.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);
        //initialize the starfield if needed
        boolean shouldDrawLine = hasInitializedBobs;
        if (!hasInitializedBobs) {
            initializeHeadBobs();

            textPaint.setColor(Color.WHITE);
            textPaint.setAlpha(255);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTypeface(Typeface.SANS_SERIF);
            textPaint.setTextSize(48);


            int xPos = (canvas.getWidth() / 2);
            int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
            canvas.drawText("Ready...", xPos, yPos, textPaint);

            hasInitializedBobs = true;
        }
        int bobYPos = canvas.getHeight() / 2 - 50;

        Iterator<HeadBob> headBobIterator = headBobs.iterator();
        while (headBobIterator.hasNext()) {
            HeadBob bob = headBobIterator.next();
            long bobOffset = bob.offset + getWidth();

            if (bobOffset > -128 && bobOffset < getWidth()) {
                switch (bob.direction) {
                    case DOWN:
                        canvas.drawBitmap(bm_bob_down, bobOffset, bobYPos, null);
                        break;
                    case LEFT:
                        canvas.drawBitmap(bm_bob_left, bobOffset, bobYPos, null);
                        break;
                    case RIGHT:
                        canvas.drawBitmap(bm_bob_right, bobOffset, bobYPos, null);
                        break;
                }
            }
            // Have to split the if statements to allow for this one and the
            // one before it to both run (this one didn't get run because it
            // falls in between the value range of the previous if statement)
            if (bobOffset > 0 && bobOffset < 128 && scanForHeadBob == null) {
                // Set the head-bob direction to scan for and begin scanning
                scanForHeadBob = bob.direction;
                scanning = true;
            }

            if ((bobOffset < -128 && !hasScannedCorrectBob) || (bobOffset < 128 && hasScannedCorrectBob && scanForHeadBob == bob.direction)) {
                if (hasScannedCorrectBob) {
                    Log.d("play", "got bob: " + scanForHeadBob);
                    gameFeedbackString.text = bobMatchStrings[random.nextInt(bobMatchStrings.length)];
                    bobsMatched++;
                } else {
                    Log.d("play", "did not get bob " + bob.direction
                            + ": got " + scanForHeadBob + " instead");

                    gameFeedbackString.text = bobFailStrings[random.nextInt(bobFailStrings.length)];
                    bobsMissed++;
                }
                gameFeedbackRemoveHandler.removeCallbacks(gameFeedbackRemoveRunnable);
                gameFeedbackRemoveHandler.postDelayed(gameFeedbackRemoveRunnable, 1000);

                scanForHeadBob = null;
                hasScannedCorrectBob = false;
                headBobIterator.remove();
            }

            bob.offset -= 4;
        }

        if (headBobs.size() == 0) {
            // Game over
            textPaint.setColor(Color.WHITE);
            textPaint.setAlpha(255);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTypeface(Typeface.SANS_SERIF);
            textPaint.setTextSize(48);


            int xPos = (canvas.getWidth() / 2);
            int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
            canvas.drawText("Game Over: " + getBobPercentage() + "%", xPos, yPos, textPaint);


            // Unregister the sensor listener
            mSensorManager.unregisterListener(this);

            if(!calledGameOverRunnable && gameOverRunnable != null) {
                calledGameOverRunnable = true;
                gameOverRunnable.run();
            }
        } else {

            textPaint.setColor(Color.WHITE);
            textPaint.setAlpha(255);
            textPaint.setTextAlign(Paint.Align.RIGHT);
            textPaint.setTypeface(Typeface.SANS_SERIF);
            textPaint.setTextSize(22);

            if(bobsMissed == 0) {
                canvas.drawText("100%", getWidth() - 20, 20, textPaint);
            } else {
                canvas.drawText("" + getBobPercentage() + "%", getWidth() - 20, 20, textPaint);
            }

            textPaint.setColor(Color.RED);
            textPaint.setAlpha(255);
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setTypeface(Typeface.SANS_SERIF);
            textPaint.setTextSize(30);
            canvas.drawText(gameFeedbackString.text, 145 , 20, textPaint);

            if(shouldDrawLine) {
                p.setStrokeWidth(10);
                p.setColor(Color.RED);
                canvas.drawLine(128, 0, 128, getHeight(), p);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Don't need to account for anything here
        Log.w(DEBUG, "Unaccounted action with: " + sensor.getName());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            mGravity = RecordingActivity.lowPassFilter(event.values, mGravity);
        }

        // Confirm we are attempting to scan for a head-bob
        if (scanning) {
            // Confirm that we haven't already scanned a head-bob for the current scan
            if (scanForHeadBob != null) {
                if (mGravity[0] < HeadBob.THRESHOLD_BOB_RIGHT) {
                    // Determine if we scanned for the correct head bob
                    hasScannedCorrectBob = (scanForHeadBob == HeadBobDirection.RIGHT);
                    // Stop scanning since we received a head-bob
                    scanning = false;
                } else if (mGravity[0] > HeadBob.THRESHOLD_BOB_LEFT) {
                    hasScannedCorrectBob = (scanForHeadBob == HeadBobDirection.LEFT);
                    scanning = false;
                } else if (mGravity[1] < HeadBob.THRESHOLD_BOB_DOWN) {
                    hasScannedCorrectBob = (scanForHeadBob == HeadBobDirection.DOWN);
                    scanning = false;
                }
            }
        }
    }
}
