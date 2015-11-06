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
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameBoard extends View implements SensorEventListener {
    private Paint p;
    private Paint textPaint;

    boolean hasInitializedBobs = false;
    private boolean startedGame = false;
    private CountDownTimer countdownToStart = null;
    private int secondsLeftBeforeStart;

    public static List<HeadBob> headBobs = new ArrayList<HeadBob>();

    public static List<HeadBob> recordedHeadBobs = new ArrayList<HeadBob>();

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
    private HeadBob scanForHeadBob = null;

    private HeadBob scanForHeadBobOverlap = null;

    // Defines if we have received a correct head-bob
    private Boolean hasScannedCorrectBob = false;
    private boolean scanning = false;

    private final String[] bobMatchStrings = {"Nice!", "Cool!", "Great!", "Awesome!"};

    private final String[] bobFailStrings = {"Boo", "Miss", "Fail", "Nope"};

    private int bobsMatched = 0;
    private int bobsMissed = 0;
    private int bobsMissedSinceLastMatch = 0;
    private int multiplier = 1;
    private int bobsMatchedInARow = 0;
    private int bobsMatchedInARowForNextMultiplier = 10;
    private int score = 0;

    private Random random = new Random();

    private final int ALLOWED_MISSED = 10;

    private HashSet<HeadBob> currentMissedBobs = new HashSet<HeadBob>();


    private class GameFeedbackString {
        public String text = "";
        public int color = Color.RED;
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

    public void setHeadBobs(List<HeadBob> headBobs) {
        GameBoard.recordedHeadBobs = headBobs;
    }

    public void setGameOverRunnable(Runnable runnable) {
        gameOverRunnable = runnable;
    }


    private void initializeHeadBobs() {
        headBobs.clear();
        if (recordedHeadBobs.size() != 0) {
            for(HeadBob headBob: recordedHeadBobs) {
                headBobs.add(new HeadBob(headBob.offset, headBob.direction));
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

            hasInitializedBobs = true;
        }

        if (!startedGame) {

            if (countdownToStart == null) {
                countdownToStart = new CountDownTimer(5000, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        secondsLeftBeforeStart = (int)Math.ceil(millisUntilFinished / 1000) + 1;
                    }

                    @Override
                    public void onFinish() {
                        secondsLeftBeforeStart = 1;
                        startedGame = true;
                    }
                };
                countdownToStart.start();
            } else {
                textPaint.setColor(Color.WHITE);
                textPaint.setAlpha(255);
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setTypeface(Typeface.SANS_SERIF);
                textPaint.setTextSize(48);


                int xPos = (canvas.getWidth() / 2);
                int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
                canvas.drawText("Ready... " + Integer.toString(secondsLeftBeforeStart), xPos, yPos, textPaint);
            }
            return;
        }

        if (headBobs.size() == 0 || bobsMissedSinceLastMatch > ALLOWED_MISSED) {
            // Game over
            textPaint.setColor(Color.WHITE);
            textPaint.setAlpha(255);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTypeface(Typeface.SANS_SERIF);
            textPaint.setTextSize(48);

            int xPos = (canvas.getWidth() / 2);
            int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));

            if (bobsMissedSinceLastMatch > ALLOWED_MISSED) {
                canvas.drawText("Song failed: " + getBobPercentage() + "%", xPos, yPos, textPaint);
            } else {
                canvas.drawText("Game Over: " + getBobPercentage() + "%", xPos, yPos, textPaint);
            }


            // Unregister the sensor listener
            mSensorManager.unregisterListener(this);

            if(!calledGameOverRunnable && gameOverRunnable != null) {
                calledGameOverRunnable = true;
                gameOverRunnable.run();
            }
        } else {

            Iterator<HeadBob> headBobIterator = headBobs.iterator();
            int bobYPos = canvas.getHeight() / 2 - 45;

            long bobOffset = 0;
            while (headBobIterator.hasNext()) {
                HeadBob bob = headBobIterator.next();
                bobOffset = bob.offset + getWidth();

                if (bobOffset > -128 && bobOffset < getWidth()) {

                    if(currentMissedBobs.contains(bob)) {
                        p.setAlpha(127);
                    } else {
                        p.setAlpha(255);
                    }

                    switch (bob.direction) {
                        case DOWN:
                            canvas.drawBitmap(bm_bob_down, bobOffset, bobYPos, p);
                            break;
                        case LEFT:
                            canvas.drawBitmap(bm_bob_left, bobOffset, bobYPos, p);
                            break;
                        case RIGHT:
                            canvas.drawBitmap(bm_bob_right, bobOffset, bobYPos, p);
                            break;
                    }
                }
                // Have to split the if statements to allow for this one and the
                // one before it to both run (this one didn't get run because it
                // falls in between the value range of the previous if statement)
                if (bobOffset > 0 && bobOffset < 128) {

                    if(scanForHeadBob == null) {
                        // Set the head-bob direction to scan for and begin scanning
                        scanForHeadBob = bob;
                        scanning = true;
//                    } else if(scanForHeadBobOverlap == null && scanForHeadBob != bob) {
//                        scanForHeadBobOverlap = bob;
//                    }
                    }
                }

                if (!currentMissedBobs.contains(bob) &&
                        (
                                (bobOffset < 0 && !hasScannedCorrectBob) ||
                                (bobOffset < 128 && hasScannedCorrectBob && scanForHeadBob == bob)// ||
                                //(bobOffset > 0 && bobOffset < 128 && scanForHeadBob != null && bob == scanForHeadBobOverlap)
                        )
                    ) {
                    if (hasScannedCorrectBob) {
                        //Log.d("play", "got bob: " + scanForHeadBob);
                        gameFeedbackString.text = bobMatchStrings[random.nextInt(bobMatchStrings.length)];
                        gameFeedbackString.color = Color.GREEN;
                        bobsMatched++;
                        bobsMissedSinceLastMatch = 0;
                        bobsMatchedInARow++;
                        if (bobsMatchedInARow >= bobsMatchedInARowForNextMultiplier) {
                            multiplier++;
                        }
                        score = score + multiplier;
                        headBobIterator.remove();
                    } else {
                        //Log.d("play", "did not get bob " + bob.direction
                        //       + ": got " + scanForHeadBob + " instead");

                        gameFeedbackString.text = bobFailStrings[random.nextInt(bobFailStrings.length)];
                        gameFeedbackString.color = Color.RED;
                        bobsMissed++;
                        bobsMissedSinceLastMatch++;
                        bobsMatchedInARow = 0;
                        multiplier = 1;
                        currentMissedBobs.add(bob);
                    }
                    gameFeedbackRemoveHandler.removeCallbacks(gameFeedbackRemoveRunnable);
                    gameFeedbackRemoveHandler.postDelayed(gameFeedbackRemoveRunnable, 1000);
                    scanForHeadBob = null;


                    hasScannedCorrectBob = false;

                } else if(bobOffset < -128) {
                    if(currentMissedBobs.contains(bob)) {
                        currentMissedBobs.remove(bob);
                    }
                    headBobIterator.remove();
                }

                bob.offset -= 4;
            }

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
            textPaint.setTextAlign(Paint.Align.RIGHT);
            textPaint.setTypeface(Typeface.SANS_SERIF);
            textPaint.setTextSize(25);

            canvas.drawText("Life:", getWidth() - 103, getHeight() - 3, textPaint);


            p.setColor(Color.RED);
            canvas.drawRect(getWidth() - 100, getHeight() - 25, getWidth(), getHeight(), p);
            p.setColor(Color.BLACK);
            canvas.drawRect(getWidth() - 97, getHeight() - 22, getWidth() + 3, getHeight() + 3, p);
            p.setColor(Color.rgb(255,64,64));
            canvas.drawRect(getWidth(), getHeight() - 22, (getWidth() - 97) + ((int) Math.floor(94 * ((float) bobsMissedSinceLastMatch / ALLOWED_MISSED))) , getHeight() + 3, p);

            textPaint.setColor(gameFeedbackString.color);
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
                    hasScannedCorrectBob = (scanForHeadBob.direction == HeadBobDirection.RIGHT);
                    // Stop scanning since we received a head-bob
                    scanning = false;
                } else if (mGravity[0] > HeadBob.THRESHOLD_BOB_LEFT) {
                    hasScannedCorrectBob = (scanForHeadBob.direction == HeadBobDirection.LEFT);
                    scanning = false;
                } else if (mGravity[1] < HeadBob.THRESHOLD_BOB_DOWN) {
                    hasScannedCorrectBob = (scanForHeadBob.direction == HeadBobDirection.DOWN);
                    scanning = false;
                }
            }
        }
    }
}
