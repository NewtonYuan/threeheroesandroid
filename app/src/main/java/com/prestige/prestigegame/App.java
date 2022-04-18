package com.prestige.prestigegame;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;

public class App extends Application {
    private Activity activity = new MainActivity();
    public String playerID = "";
    @Override
    public void onCreate() {
        super.onCreate();
        PlayGamesSdk.initialize(this);

        GamesSignInClient gamesSignInClient = PlayGames.getGamesSignInClient(activity);
        gamesSignInClient.signIn();

        gamesSignInClient.isAuthenticated().addOnCompleteListener(isAuthenticatedTask -> {
            boolean isAuthenticated =
                    (isAuthenticatedTask.isSuccessful() &&
                            isAuthenticatedTask.getResult().isAuthenticated());

            if (isAuthenticated) {
                PlayGames.getPlayersClient(activity).getCurrentPlayer().addOnCompleteListener(mTask -> {
                    playerID = mTask.getResult().getPlayerId();
                    Log.i("Result", playerID);
                });
            }
        });
        Log.i("Result_UNAUTH", playerID);
    }
}