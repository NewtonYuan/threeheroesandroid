package com.prestige.prestigegame;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.prestige.prestigegame.Game;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.prestige.prestigegame.gameobject.Player;

/**
 * MainActivity is the entry point to our application.
 */
public class GameActivity extends Activity {
    private static final String AD_UNIT_ID = "ca-app-pub-7351857308954450/6944150865";
    private static final String FORCED_AD_UNIT_ID = "ca-app-pub-7351857308954450/6217478272";
    private static final long COUNTER_TIME = 10;
    private static final int GAME_OVER_REWARD = 1;
    private static final String TAG = "Game";

    private InterstitialAd interstitialAd;
    private static RewardedAd rewardedAd;
    private Button retryButton;
    private Button showVideoButton;
    private long timeRemaining;
    private boolean finishedAd = false;
    boolean isLoading;
    static MediaPlayer gameMusicPlayer;
    private com.prestige.prestigegame.Game game;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("GameActivity.java", "onCreate()");
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        loadRewardedAd();
        loadInterstitialAd();

        // Set content view to game, so that objects in the Game class can be rendered to the screen
        game = new Game(this, GameActivity.this);
        setContentView(game);

        gameMusicPlayer = MediaPlayer.create(GameActivity.this, R.raw.bgm);
        gameMusicPlayer.setLooping(true);
        gameMusicPlayer.setVolume(0.2f, 0.2f);
    }

    @Override
    protected void onStart() {
        Log.d("GameActivity.java", "onStart()");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("GameActivity.java", "onResume()");
        game.resume();
        super.onResume();
        gameMusicPlayer.start();
    }

    @Override
    protected void onPause() {
        Log.d("GameActivity.java", "onPause()");
        game.stopLoop();
        super.onPause();
        gameMusicPlayer.pause();
    }

    @Override
    protected void onStop() {
        Log.d("GameActivity.java", "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("GameActivity.java", "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public void loadRewardedAd() {
        if (rewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    this,
                    AD_UNIT_ID,
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d(TAG, loadAdError.getMessage());
                            rewardedAd = null;
                            GameActivity.this.isLoading = false;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            GameActivity.this.rewardedAd = rewardedAd;
                            Log.d(TAG, "onAdLoaded");
                            GameActivity.this.isLoading = false;
                        }
                    });
        }
    }

    public void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
            this,
            FORCED_AD_UNIT_ID,
            adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    GameActivity.this.interstitialAd = interstitialAd;
                    Log.i(TAG, "onAdLoaded");
                    interstitialAd.setFullScreenContentCallback(
                            new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Called when fullscreen content is dismissed.
                                    // Make sure to set your reference to null so you don't
                                    // show it a second time.
                                    startActivity(new Intent(GameActivity.this, ReadyActivity.class));
                                    GameActivity.this.interstitialAd = null;
                                    Log.d("TAG", "The ad was dismissed.");
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    // Called when fullscreen content failed to show.
                                    // Make sure to set your reference to null so you don't
                                    // show it a second time.
                                    GameActivity.this.interstitialAd = null;
                                    Log.d("TAG", "The ad failed to show.");
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    // Called when fullscreen content is shown.
                                    Log.d("TAG", "The ad was shown.");
                                }
                            });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.i(TAG, loadAdError.getMessage());
                    interstitialAd = null;
                }
            });
    }

    public void returnHome(){
        super.onBackPressed();
        gameMusicPlayer.pause();
        gameMusicPlayer.release();
    }

    public void showRewardedVideo() {
        if (rewardedAd == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            Toast.makeText(GameActivity.this, "Ad Unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        rewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "onAdShowedFullScreenContent");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.d(TAG, "onAdFailedToShowFullScreenContent");
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        if (finishedAd){
                            game.revivePlayer();
                            game.resume();
                            setContentView(game);
                            game.adShown = true;
                            finishedAd = false;
                        }
                        rewardedAd = null;
                        Log.d(TAG, "onAdDismissedFullScreenContent");
                        // Preload the next rewarded ad.
                        GameActivity.this.loadRewardedAd();
                    }
                });
        Activity activityContext = GameActivity.this;
        rewardedAd.show(
                activityContext,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("TAG", "The user earned the reward.");
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                        finishedAd = true;
                    }
                });
    }

    public void showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            startActivity(new Intent(GameActivity.this, ReadyActivity.class));
        }
    }
}
