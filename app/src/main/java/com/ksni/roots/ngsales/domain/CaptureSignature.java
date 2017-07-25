package com.ksni.roots.ngsales.domain;

/**
 * Created by #roots on 04/11/2015.
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.util.Helper;

public class CaptureSignature extends AppCompatActivity{
    signature mSignature;
    Paint paint;
    LinearLayout mContent;
    MenuItem clear, save;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        clear = menu.findItem(R.id.action_refresh);
        save = menu.findItem(R.id.action_save_signature);
        save.setEnabled(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signature, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)         {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_refresh:
                mSignature.clear();
                break;
            case R.id.action_save_signature:
                mSignature.save();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capturesignature);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();

        ab.setTitle(Helper.getStrResource(this, R.string.call_plan_pre_title_signature));




        //save = (Button) findViewById(R.id.save);
        //save.setEnabled(false);
        //clear = (Button) findViewById(R.id.clear);
        mContent = (LinearLayout) findViewById(R.id.mysignature);

        mSignature = new signature(this, null);
        mContent.addView(mSignature);

//        save.setOnClickListener(onButtonClick);
  //      clear.setOnClickListener(onButtonClick);
    }

/*    Button.OnClickListener onButtonClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == clear) {
                mSignature.clear();
            } else if (v == save) {
                mSignature.save();
            }
        }
    };
*/

    public class signature extends View {
        static final float STROKE_WIDTH = 10f;
        static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        Paint paint = new Paint();
        Path path = new Path();

        float lastTouchX;
        float lastTouchY;
        final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void clear() {
            path.reset();
            invalidate();
            save.setEnabled(false);
        }

        public void save() {
            Bitmap returnedBitmap = Bitmap.createBitmap(mContent.getWidth(),
                    mContent.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            Drawable bgDrawable = mContent.getBackground();
            if (bgDrawable != null)
                bgDrawable.draw(canvas);
            else
                canvas.drawColor(Color.WHITE);
            mContent.draw(canvas);

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            returnedBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);

            File photo=new File(Environment.getExternalStorageDirectory(), "signature.png");

            if (photo.exists()) {
                photo.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(photo.getPath());

                fos.write(bs.toByteArray());
                fos.close();
            }
                catch (IOException e) {
                }
            Intent intent = new Intent();
            intent.putExtra("byteArray", bs.toByteArray());
            setResult(1, intent);
            finish();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            save.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

}
