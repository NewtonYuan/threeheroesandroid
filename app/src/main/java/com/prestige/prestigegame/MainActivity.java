package com.prestige.prestigegame;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

/**
 * MainActivity is the entry point to our application.
 */
public class MainActivity extends Activity {
    static MediaPlayer bgmPlayer;
    AudioManager bgmManager;
    int maxVolume = 50;
    float log1=(float)(Math.log(maxVolume-40)/Math.log(maxVolume));
    private boolean toPauseMusic = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        bgmPlayer = MediaPlayer.create(MainActivity.this, R.raw.gbgm);
        bgmPlayer.setLooping(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bgmPlayer != null) {
            if (bgmPlayer.isPlaying() && toPauseMusic) {
                bgmPlayer.pause();
            }
        }
        toPauseMusic = true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        bgmPlayer.start();
    }

    public void playButtonClick (View view) {
        startActivity(new Intent(MainActivity.this, ReadyActivity.class));
        toPauseMusic = false;
    }

    public void heroesButtonClick (View view) {
        startActivity(new Intent(MainActivity.this, HeroesActivity.class));
        toPauseMusic = false;
    }

    public void leaderboardsButtonClick (View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This area is still in development!")
                .setTitle("Under Construction");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void recordsButtonClick (View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This area is still in development!")
                .setTitle("Under Construction");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
