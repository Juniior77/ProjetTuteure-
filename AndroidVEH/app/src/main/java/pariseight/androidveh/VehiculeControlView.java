package pariseight.androidveh;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Guillaume on 28/02/2017.
 */

public class VehiculeControlView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    SurfaceHolder holder;
    private Resources mRes;
    private Context mContext;
    private Bitmap repereAcc;
    private Bitmap mRepereAcc;
    private Bitmap joyAcc;
    private Bitmap mJoyAcc;
    public Thread cv_thread;
    public int posY;
    private boolean onMove = false;
    private int ptZero;
    public float repereAcceleration;

    public VehiculeControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);

        mContext = context;
        mRes = mContext.getResources();
        cv_thread = new Thread(this);

        if ((cv_thread != null) && (!cv_thread.isAlive())) {
            cv_thread.start();
            if(BuildConfig.DEBUG) {
                Log.e("-FCT-", "cv_thread.start()");
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        repereAcc = BitmapFactory.decodeResource(mRes, R.drawable.accelerationrect);
        mRepereAcc = Bitmap.createScaledBitmap(repereAcc, 250, (getHeight() - 70), true);
        joyAcc = BitmapFactory.decodeResource(mRes, R.drawable.accelerationpoint);
        mJoyAcc = Bitmap.createScaledBitmap(joyAcc, 270, 80, true);
        ptZero = getHeight() / 2;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void run() {
        Canvas c = null;
        while (true)
        {
            try {
                cv_thread.sleep(50);
                try {
                    c = holder.lockCanvas(null);
                    nDraw(c);
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
            } catch (Exception e) {
                if(BuildConfig.DEBUG) {
                    Log.e("-> RUN <-", "PB DANS RUN" + e.getMessage());
                }
            }

        }
    }

    public boolean onTouchEvent(MotionEvent event){
        posY = (int)event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                onMove = true;
                break;
            case MotionEvent.ACTION_MOVE:
                repereAcceleration = (ptZero - posY) / 2;
                break;
            case MotionEvent.ACTION_UP:
                onMove = false;
                repereAcceleration = 0;
                break;
        }
Log.i("ACC", "Acceleration: " + repereAcceleration);
        return true;
    }

    private  void nDraw(Canvas canvas){
        canvas.drawRGB(44, 44, 44);
        drawRepereAcc(canvas);
        if(!onMove)
            drawJoyAcc(canvas);
        if(onMove)
            drawJoyAccMove(canvas);

    }

    public void drawRepereAcc(Canvas canvas){
        canvas.drawBitmap(mRepereAcc, (getWidth() - 150) - (mRepereAcc.getWidth()/2), (getHeight()/2) - (mRepereAcc.getHeight()/2), null);
    }

    public void drawJoyAcc(Canvas canvas){
        canvas.drawBitmap(mJoyAcc, (getWidth() - 150) - (mJoyAcc.getWidth()/2), (getHeight()/2) - (mJoyAcc.getHeight()/2), null);
    }

    public void drawJoyAccMove(Canvas canvas){
        canvas.drawBitmap(mJoyAcc, (getWidth() - 150) - (mJoyAcc.getWidth()/2), posY , null);
    }
}
