package pariseight.androidveh;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
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
    private Bitmap joyAcc;
    public Thread cv_thread;

    public VehiculeControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);

        mContext = context;
        mRes = mContext.getResources();
        cv_thread = new Thread(this);

        repereAcc = BitmapFactory.decodeResource(mRes, R.drawable.accelerationrect);
        joyAcc = BitmapFactory.decodeResource(mRes, R.drawable.accelerationpoint);

        if ((cv_thread != null) && (!cv_thread.isAlive())) {
            cv_thread.start();
            if(BuildConfig.DEBUG) {
                Log.e("-FCT-", "cv_thread.start()");
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

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

    private  void nDraw(Canvas canvas){
        canvas.drawRGB(44, 44, 44);
        drawRepereAcc(canvas);
    }

    public void drawRepereAcc(Canvas canvas){
        canvas.drawBitmap(repereAcc, (getWidth()/2) - (repereAcc.getWidth()/2), (getHeight()/2) - (repereAcc.getHeight()/2), null);
    }
}
