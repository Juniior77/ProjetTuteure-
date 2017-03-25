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
import android.widget.ImageButton;

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
    private Bitmap Gauche;
    private Bitmap mGauche;
    private Bitmap Droite;
    private Bitmap mDroite;
    private Bitmap FeuBleu;
    private Bitmap mFeuBleu;
    private Bitmap FeuVert;
    private Bitmap mFeuVert;
    private Bitmap FeuDetresse;
    private Bitmap mFeuDetresse;
    private Bitmap Parking;
    private Bitmap mParking;

    public Thread cv_thread;
    public int posY;
    private int posX;
    private boolean onMove = false;
    private int ptZero;
    public float repereAcceleration;
    public boolean ClignGauche = false;
    public boolean ClignDroite = false;
    public boolean Warning = false;
    public boolean Phare = false;
    public boolean PleinPhare = false;
    public boolean FeuStop = true;
    public boolean ActionTourner1 = false;
    public boolean ActionTourner2 = false;
    public boolean ActionParking = false;

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
        Gauche = BitmapFactory.decodeResource(mRes, R.drawable.gauche150);
        mGauche = Bitmap.createScaledBitmap(Gauche, 100, 100, true);
        Droite = BitmapFactory.decodeResource(mRes, R.drawable.droite150);
        mDroite = Bitmap.createScaledBitmap(Droite, 100, 100, true);
        FeuBleu = BitmapFactory.decodeResource(mRes, R.drawable.feuxbleu);
        mFeuBleu = Bitmap.createScaledBitmap(FeuBleu, 100, 100, true);
        FeuVert = BitmapFactory.decodeResource(mRes, R.drawable.feuvert);
        mFeuVert = Bitmap.createScaledBitmap(FeuVert, 100, 100, true);
        FeuDetresse = BitmapFactory.decodeResource(mRes, R.drawable.danger);
        mFeuDetresse = Bitmap.createScaledBitmap(FeuDetresse, 100, 100, true);
        Parking = BitmapFactory.decodeResource(mRes, R.drawable.parking);
        mParking = Bitmap.createScaledBitmap(Parking, 100, 100, true);
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
        posX = (int)event.getX();
        posY = (int)event.getY();

        if(posX > getWidth() - 300)
        {
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
        }
        else{
            onMove = false;
            repereAcceleration = 0;
        }

        if(event.getAction() == MotionEvent.ACTION_UP){
            if(posX > (getWidth()/2)-(getWidth()/4) && posY > 100 && posX < (getWidth()/2)-(getWidth()/4)+100 && posY < 100+100)
            {
                //BOUTON GAUCHE
                if(ClignGauche == false)
                {
                    ClignGauche = true;
                    ClignDroite = false;
                }
                else
                    ClignGauche = false;
                Log.i("REPERE_BOUTON_GAUCHE", "HELLO TU EST SUR LE BOUTON CLIGNOTANT GAUCHE :)");
            }
            else if(posX > (getWidth()/2)+(getWidth()/4) && posY > 100 && posX < (getWidth()/2)+(getWidth()/4)+100 && posY < 100+100)
            {
                //BOUTON DROITE
                if(ClignDroite == false){
                    ClignDroite = true;
                    ClignGauche = false;
                }
                else
                    ClignDroite = false;
                Log.i("REPERE_BOUTON_DROIT", "HELLO TU EST SUR LE BOUTON CLIGNOTANT DROIT :)");
            }
            else if(posX > (getWidth()/2)-(getWidth()/8) && posY > 100 && posX < (getWidth()/2)-(getWidth()/8)+100 && posY < 100+100){
                //BOUTON FEU VERT
                if(Phare == false)
                    Phare = true;
                else
                    Phare = false;
                Log.i("REPERE_BOUTON_VERT", "HELLO TU EST SUR LE BOUTON FEU VERT :)");
            }
            else if(posX > (getWidth()/2)+(getWidth()/8) && posY > 100 && posX < (getWidth()/2)+(getWidth()/8)+100 && posY < 100+100){
                //BOUTON FEU BLEU
                if(PleinPhare == false)
                    PleinPhare = true;
                else
                    PleinPhare = false;
                Log.i("REPERE_BOUTON_BLEU", "HELLO TU EST SUR LE BOUTON FEU BLEU :)");
            }
            else if(posX > (getWidth()/2) && posY > 100 && posX < (getWidth()/2)+100 && posY < 100+100){
                //BOUTON WARNING
                if(Warning == false)
                    Warning = true;
                else
                    Warning = false;
                Log.i("REPERE_BOUTON_WARNING", "HELLO TU EST SUR LE BOUTON CLIGNOTANT WARNING :)");
            }
            else if(posX > (getWidth()/2) && posY > (getHeight() - 150) && posX < (getWidth()/2)+100 && posY < (getHeight() - 150)+100){
                ActionParking = true;
            }
        }
        Log.i("ACC", "Acceleration: " + repereAcceleration);
        return true;
    }

    private  void nDraw(Canvas canvas){
        canvas.drawRGB(44, 44, 44);
        drawRepereAcc(canvas);
        drawGauche(canvas);
        drawDroite(canvas);
        drawFeuVert(canvas);
        drawFeuBleu(canvas);
        drawWarning(canvas);
        drawParking(canvas);
        if(onMove) {
            drawJoyAccMove(canvas);
            FeuStop = false;
        }
        else
        {
            drawJoyAcc(canvas);
            FeuStop = true;
        }
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

    public void drawGauche(Canvas canvas){
        canvas.drawBitmap(mGauche, (getWidth()/2) - (getWidth()/4), 100, null);
    }

    public void drawDroite(Canvas canvas){
        canvas.drawBitmap(mDroite, (getWidth()/2) + (getWidth()/4), 100, null);
    }

    public void drawFeuVert(Canvas canvas){
        canvas.drawBitmap(mFeuVert, (getWidth()/2) - (getWidth()/8), 100, null);
    }

    public void drawFeuBleu(Canvas canvas){
        canvas.drawBitmap(mFeuBleu, (getWidth()/2) + (getWidth()/8), 100, null);
    }

    public void drawWarning(Canvas canvas){
        canvas.drawBitmap(mFeuDetresse, (getWidth()/2), 100, null);
    }
    public void drawParking(Canvas canvas){
        canvas.drawBitmap(mParking, (getWidth()/2), (getHeight() - 150), null);
    }
}
