package com.prestige.prestigegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.prestige.prestigegame.graphics.DamageAnimator;
import com.prestige.prestigegame.graphics.HealerAnimator;
import com.prestige.prestigegame.graphics.TankAnimator;

public class HeroesActivity extends AppCompatActivity {
    MediaPlayer bgmPlayer;
    private DamageAnimator damage;
    private HealerAnimator healer;
    private TankAnimator tank;
    private boolean toPauseMusic = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heroes);

        ImageView background = (ImageView) findViewById(R.id.background);
        background.setColorFilter(ContextCompat.getColor(this, R.color.blackTint), PorterDuff.Mode.SRC_OVER);
        MainActivity.bgmPlayer.start();

    }
    @Override
    protected void onPause() {
        super.onPause();
        if (MainActivity.bgmPlayer != null) {
            if (MainActivity.bgmPlayer.isPlaying() && toPauseMusic) {
                MainActivity.bgmPlayer.pause();
            }
        }
        toPauseMusic = true;
    }

    public void character1ButtonClick (View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "About: An archer that possesses a large arsenal of arrows. Deals massive amounts of damage when leveled up.\n\n" +
                "Passive: Fires regular arrow at closest enemy. \n(1 Damage, 2s Cool-down)\n\n" +
                "First Ability: Fires explosive arrow at closest enemy, dealing AOE damage. \n(2 Damage, 10s Cool-down)\n\n" +
                "Second Ability: Fires piercing arrow that penetrates all enemies. \n(3 Damage, 7s Cool-down)\n\n" +
                "Level-Up: Increases damage of abilities, Decreases cool-down of abilities.\n\n")
                .setTitle("Damage Dealer");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void character2ButtonClick (View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "About: A priest that heals heroes who also possesses several other magical abilities.\n\n" +
                        "Passive: Heals the party at intervals until max health\n(3 Power, 12s Cool-down)\n\n" +
                        "First Ability: Casts a freeze spell on a random location on screen, grounding enemies\n(3s Duration, 9s Cool-down)\n\n" +
                        "Second Ability: Instantly heals half of the party's max health\n(60s Cool-down)\n\n" +
                        "Level-Up: Increases power of abilities, Decreases cool-down of abilities, Freeze ability deals damage at Level 10.\n\n")
                .setTitle("Healer");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void character3ButtonClick (View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "About: A durable warrior that helps protect the party with its abilities.\n\n" +
                        "Passive: Increases max HP of party.\n\n" +
                        "First Ability: Summons protective shield, which wears away as enemies attack it\n(2-4 Charges, 15s Cool-down)\n\n" +
                        "Second Ability: Knocks back enemies within a certain range of the party.\n(10s Cool-down)\n\n" +
                        "Level-Up: Increases max HP of party, Increases max number of shield charges, Decreases cool-down of abilities, Knock-back ability deals damage at Level 10.\n\n")
                .setTitle("Tank");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void characterLockedClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "Coming soon!")
                .setTitle("Hero in Development");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void backButtonClick(View view){
        super.onBackPressed();
        toPauseMusic = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toPauseMusic = false;
    }
}