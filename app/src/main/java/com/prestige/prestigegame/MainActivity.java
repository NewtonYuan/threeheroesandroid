package com.prestige.prestigegame;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

/**
 * MainActivity is the entry point to our application.
 */
public class MainActivity extends Activity {
    static MediaPlayer bgmPlayer;
    private boolean toPauseMusic = true;
    private static final int RC_LEADERBOARD_UI = 9004;
    public boolean tutorialActivated = false;
    private boolean musicMuted = false;
    private SharedPreferences settings;

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

        settings = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (settings.getBoolean("menuMusicMuted", false)) {
            ImageView musicSoundIMG = (ImageView) findViewById(R.id.musicSound);
            musicSoundIMG.setBackgroundResource(R.drawable.musicsoundmute);
            bgmPlayer.setVolume(0, 0);
        }

        if(settings.getBoolean("firstLaunch", true)){

        } else {

        }
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
        showLeaderboard();
    }

    public void recordsButtonClick (View view) {
        startActivity(new Intent(MainActivity.this, RecordsActivity.class));
        toPauseMusic = false;
    }

    public void tutorialClick (View view) {
        startActivity(new Intent(MainActivity.this, RecordsActivity.class));
        toPauseMusic = false;
    }

    public void musicSoundClick (View view) {
        ImageView musicSoundIMG = (ImageView) findViewById(R.id.musicSound);
        SharedPreferences.Editor settingsEditor = settings.edit();
        if (!musicMuted){
            musicMuted = true;
            settingsEditor.putBoolean("menuMusicMuted", true);
            bgmPlayer.setVolume(0,0);
            musicSoundIMG.setBackgroundResource(R.drawable.musicsoundmute);
        } else {
            musicMuted = false;
            settingsEditor.putBoolean("menuMusicMuted", false);
            bgmPlayer.setVolume(1, 1);
            musicSoundIMG.setBackgroundResource(R.drawable.musicsound);
        }
        settingsEditor.apply();
    }

    private void showLeaderboard() {
        PlayGames.getLeaderboardsClient(this)
                .getLeaderboardIntent(getString(R.string.leaderboard_id))
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });
    }
}
