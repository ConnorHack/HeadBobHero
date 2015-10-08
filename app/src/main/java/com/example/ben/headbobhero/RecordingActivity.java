package com.example.ben.headbobhero;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;

/**
 * Created by Connor on 10/1/2015.
 * <p/>
 * <h1>Activity to record head-bobs overlaying a currently playing song</h1>
 * <p/>
 * This activity will house the recognition of specific head-bobs & storing the
 * head-bobs while relating them to the playing song.
 *
 * @author Connor
 * @version 1.0
 * @since 10/1/2015
 */
public class RecordingActivity extends Activity implements SensorEventListener {

    private static final String DEBUG = "RecordingActivity";

    /* Constants created for ease of use within code
     * DELAY_NORMAL:    7.14 records per second
     * DELAY_UI:        17.14 records per second
     * DELAY_GAME:      75.00 records per second
     * DELAY_FASTEST:   285.71 records per second

    public static final int DELAY_NORMAL = SensorManager.SENSOR_DELAY_NORMAL;
    public static final int DELAY_UI = SensorManager.SENSOR_DELAY_UI;
    public static final int DELAY_GAME = SensorManager.SENSOR_DELAY_GAME;
    public static final int DELAY_FASTEST = SensorManager.SENSOR_DELAY_FASTEST;
    */

    // Rate at which to collect values from sensors
    private static int RATE = SensorManager.SENSOR_DELAY_FASTEST;

    // Globals needed to collect values from device's sensors
    private SensorManager mSensorManager;
    private Sensor mSensorGravity;
    private float mGravity[];

    private boolean mIsRecording;
    private HeadBobDirection mCurrentBob;

    private long timeStart;

    private long lastBobTime;

    private long lastOffset;

    private class BobRecording {
        public long currentOffset;
    }

    private final BobRecording bobRecordingState = new BobRecording();

    private boolean mRegisteringBob;

    /////////////////////////////////////////////////////
    // Below methods are for the extension of Activity //
    /////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize any globals that need to be initialized
        initGlobals();
        frame.removeCallbacks(frameUpdate);
        frame.postDelayed(frameUpdate, FRAME_RATE);

        //TODO setContentView(R.layout.*****)
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the sensor listener
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register the sensor listener with the specified rate
        mSensorManager.registerListener(this, mSensorGravity, RATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //TODO Take any appropriate actions on the Options Menu

        return super.onCreateOptionsMenu(menu);
    }

    /////////////////////////////////////////////////////////////////////
    // Below methods are for the implementation of SensorEventListener //
    /////////////////////////////////////////////////////////////////////

    @Override
    public void onAccuracyChanged(Sensor sensor, int arg1) {
        // Don't need to account for anything here
        Log.w(DEBUG, "Unaccounted action with: " + sensor.getName());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Check first to make sure we are supposed to be collecting results
        if (mIsRecording) {
            if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                mGravity = lowPassFilter(event.values, mGravity);
            }

            if (mGravity != null) {
                //DEBUG STATEMENT: Testing certain head-bob thresholds
                //System.out.println(mGravity[0] + " " + mGravity[1] + " " + mGravity[2]);
/*                long now = System.currentTimeMillis();
                if(now < lastBobTime + 500) {
                    return;
                }
                lastBobTime = now;*/

                long offset = bobRecordingState.currentOffset;

                //if(offset + 500 < lastOffset) {
                //    return;
                //}
                //lastOffset = offset;
                if (!mRegisteringBob) {
                    if (mGravity[0] < HeadBob.THRESHOLD_BOB_RIGHT) {
                        GameBoard.headBobs.add(new HeadBob(offset, HeadBobDirection.RIGHT));
                        System.out.println(offset + " RIGHT");
                        mRegisteringBob = true;
                        mCurrentBob = HeadBobDirection.RIGHT;
                    } else if (mGravity[0] > HeadBob.THRESHOLD_BOB_LEFT) {
                        GameBoard.headBobs.add(new HeadBob(offset, HeadBobDirection.LEFT));
                        System.out.println(offset + " LEFT");
                        mRegisteringBob = true;
                        mCurrentBob = HeadBobDirection.LEFT;
                    } else if (mGravity[1] < HeadBob.THRESHOLD_BOB_DOWN) {
                        GameBoard.headBobs.add(new HeadBob(offset, HeadBobDirection.DOWN));
                        System.out.println(offset + " DOWN");
                        mRegisteringBob = true;
                        mCurrentBob = HeadBobDirection.DOWN;
                    }
                } else {
                    switch (mCurrentBob) {
                        case RIGHT:
                            if (mGravity[0] > HeadBob.THRESHOLD_BOB_RIGHT) {
                                mRegisteringBob = false;
                                mCurrentBob = null;
                                System.out.println(offset + " REST from RIGHT");
                            }
                            break;
                        case LEFT:
                            if (mGravity[0] < HeadBob.THRESHOLD_BOB_LEFT) {
                                mRegisteringBob = false;
                                mCurrentBob = null;
                                System.out.println(offset + " REST from LEFT");
                            }
                            break;
                        case DOWN:
                            if (mGravity[1] > HeadBob.THRESHOLD_BOB_DOWN) {
                                mRegisteringBob = false;
                                mCurrentBob = null;
                                System.out.println(offset + " REST from DOWN");
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /////////////////////////////////////////////////////
    // Below methods are specific to RecordingActivity //
    /////////////////////////////////////////////////////

    /**
     * @author Connor Hack
     * <h2>Function to initialize any needed variables</h2>
     */
    public void initGlobals() {

        // Initialize the sensor manager, gravity sensor, and gravity vector
        mGravity = new float[3];
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        timeStart = System.currentTimeMillis();

        // Check to ensure the gravity sensor is functional on the device
        if (mSensorGravity == null) {
            Log.e(DEBUG, "Device does not possess a gravity sensor. "
                    + "This application will not function properly.");
        }

        // Initially do not begin collecting results
        mIsRecording = true;

        // Initially do not register a head-bob
        mRegisteringBob = false;
    }

    /**
     * @param input  The data-set to modify
     * @param output The data-set to store the filtered values
     * @return Filtered Values
     * @author Connor Hack
     * <h2>Function to apply a Low Pass Filter on a data-set</h2>
     * <p/>
     * Allows low-frequency signals to pass and will reduce the amplitude of
     * high frequency signals.
     */
    public static float[] lowPassFilter(float[] input, float[] output) {

        // Return if the size of the two lists do not match
        if (output.length != input.length) {
            return input;
        }

        // Appply a low pass filter to the input
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + 0.1f * (input[i] - output[i]);
        }

        return output;
    }

    private static final int FRAME_RATE = 20; //50 frames per second

    private Handler frame = new Handler();


    private Runnable frameUpdate = new Runnable() {
        @Override
        synchronized public void run() {
            frame.removeCallbacks(frameUpdate);
            //make any updates to on screen objects here
            //then invoke the on draw by invalidating the canvas
            bobRecordingState.currentOffset += 4;
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }
    };
}
