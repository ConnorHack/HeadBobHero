package com.example.ben.headbobhero;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

/**
 * Created by Ben on 10/2/2015.
 */
class HeadBob {
    public HeadBobDirection direction;

    public long offset;

    public HeadBob(long offset, HeadBobDirection direction) {
        this.direction = direction;
        this.offset = offset;
    }
}
