package com.prime.studio.apps.gpsrouteguide.maps.directions.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.prime.studio.apps.gpsrouteguide.maps.directions.R;

/**
 * Created by Admin11 on 11/25/2016.
 */
public class Splash extends AppCompatActivity {

    Handler mHandler;
	Runnable mRunnable;
	InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash1);

		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
		requestNewInterstitial();


		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				requestNewInterstitial();
				startActivity(new Intent(getBaseContext(), FirstActivity.class));
				Splash.this.finish();
			}
		});

		mHandler = new Handler();
		mRunnable = new Runnable() {
			@Override
			public void run() {
				if (mInterstitialAd.isLoaded()) {
					mInterstitialAd.show();
				}else {
					startActivity(new Intent(getBaseContext(), FirstActivity.class));
					Splash.this.finish();
				}
			}
		};

		mHandler.postDelayed(mRunnable,4000);

    }

	private void requestNewInterstitial() {
		AdRequest adRequest = new AdRequest.Builder()
				.build();

		mInterstitialAd.loadAd(adRequest);
		// mInterstitialAd2.loadAd(adRequest);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mHandler.removeCallbacks(mRunnable);
	}
}
