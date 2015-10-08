package com.example.ben.headbobhero;

/**
 * Created by Ben on 10/2/2015.
 */
class HeadBob {
    public HeadBobDirection direction;

    public long offset;

    public static final double THRESHOLD_BOB_LEFT = 0.5 ;
    public static final double THRESHOLD_BOB_RIGHT = -2.0 ;
    public static final double THRESHOLD_BOB_DOWN = 9.3 ;

    public HeadBob(long offset, HeadBobDirection direction) {
        this.direction = direction;
        this.offset = offset;
    }
}
