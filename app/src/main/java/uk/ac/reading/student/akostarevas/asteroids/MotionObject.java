package uk.ac.reading.student.akostarevas.asteroids;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Extends GameObject.
 * Allows movement.
 */
abstract class MotionObject extends GameObject {

    /* Physics variables */
    private final static int speedMultiplier = 100;
    private final static int dropOffDivisor = 100;

    /* Angle in degrees */
    Vector motion;

    /* Special flags */
    private boolean warp;
    private boolean enteringBounds;
    boolean exitedBounds = false;

    /* Bitmap */
    private final Paint noAA;
    Bitmap bitmap;

    MotionObject(float x, float y, float size, int canvasWidth, int canvasHeight, Bitmap bitmap) {
        this(x, y, size, canvasWidth, canvasHeight, new Vector(), false, bitmap);
    }

    MotionObject(float x, float y, float size,
                 int canvasWidth, int canvasHeight,
                 Vector motion, boolean enteringBounds,
                 Bitmap bitmap) {
        super(x, y, size, canvasWidth, canvasHeight);

        /* Tracking flags */
        this.motion = motion;
        this.enteringBounds = enteringBounds;
        this.warp = !enteringBounds;

        /* Scale bitmap */
        this.bitmap = Bitmap.createScaledBitmap(bitmap, (int) this.size, (int) this.size, false);

        /* No anti-alias scaling */
        noAA = new Paint();
        noAA.setAntiAlias(false);
        noAA.setFilterBitmap(false);
        noAA.setDither(false);
    }

    /**
     * Updates drop-off, moves object and checks bounds.
     * @param secondsElapsed Seconds elapsed.
     */
    void move(float secondsElapsed) {
        /* Update velocity with drop off */
        updateDropOff(secondsElapsed);

        /* Apply displacement */
        x += motion.getX() * secondsElapsed * speedMultiplier;
        y += motion.getY() * secondsElapsed * speedMultiplier;

        /* Check bounds and/or warp */
        checkBounds();
    }

    /**
     * Updates velocity with drop-off.
     * @param secondsElapsed Seconds elapsed.
     */
    private void updateDropOff(float secondsElapsed) {
        double dropOff = Math.pow(motion.getVelocity(), 2) * secondsElapsed / dropOffDivisor;
        motion.updateVelocity(0 - dropOff);
    }

    /**
     * Checks bounds and/or warps depending on flags.
     */
    private void checkBounds() {
        /* If out of bounds */
        if (!inBounds(x, y)) {
            /* If wasn't placed out of bounds */
            if (!enteringBounds) {
                exitedBounds = true;
            }
        }

        /* If within bounds */
        if (inBounds(x, y)) {
            /* If started out of bounds */
            if (enteringBounds) {
                enteringBounds = false;
            }
        }

        if (warp) {
            /* Warp bottom and right */
            x %= canvasWidth;
            y %= canvasHeight;

            /* Warp top and left */
            x = (x < 0) ? x + canvasWidth : x;
            y = (y < 0) ? y + canvasHeight : y;
        }
    }

    void draw(Canvas canvas) {
        draw(canvas, 180);
    }

    void draw(Canvas canvas, float rotate) {
        /* Rotate bullet */
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);
        matrix.postRotate(180 - rotate);
        matrix.postTranslate(x + (float) (size / 2.0), y + (float) (size / 2.0));

        /* Draw main player */
        canvas.drawBitmap(bitmap, matrix, noAA);
    }

}
