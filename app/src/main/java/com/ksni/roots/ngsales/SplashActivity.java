package com.ksni.roots.ngsales;


import android.app.ActionBar;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.View;

import com.ksni.roots.ngsales.domain.Login;

import java.util.Random;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		MultiDex.install(this);

		Thread background = new Thread() {
			public void run() {

				try {
					sleep(3*1000);
					
				    Intent i=new Intent(getBaseContext(),Login.class);
					startActivity(i);
					finish();
					
				} catch (Exception e) {
				
				}
			}
		};
		
		// start thread
		background.start();
		
//METHOD 2	
		
		/*
		new Handler().postDelayed(new Runnable() {
			 
            // Using handler with postDelayed called runnable run method
 
            @Override
            public void run() {
                Intent i = new Intent(MainSplashScreen.this, FirstScreen.class);
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, 5*1000); // wait for 5 seconds
		*/
	}


	
	@Override
    protected void onDestroy() {
        super.onDestroy();
        
    }
}
