package com.prestige.prestigegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HeroesActivity extends AppCompatActivity {
    MediaPlayer bgmPlayer;
    private boolean toPauseMusic = true;
    private boolean perlaHeroUnlocked;
    private boolean alfredHeroUnlocked;
    private boolean erinaHeroUnlocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heroes);

        SharedPreferences characters = this.getSharedPreferences("characters", Context.MODE_PRIVATE);
        perlaHeroUnlocked = characters.getBoolean("perla", false);
        alfredHeroUnlocked = characters.getBoolean("alfred",  false);
        erinaHeroUnlocked = characters.getBoolean("erina",  false);

        ImageView background = (ImageView) findViewById(R.id.background);
        background.setColorFilter(ContextCompat.getColor(this, R.color.blackTint), PorterDuff.Mode.SRC_OVER);
        MainActivity.bgmPlayer.start();

        if (!perlaHeroUnlocked){
            ImageView perla = (ImageView) findViewById(R.id.perla);
            perla.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        }
        if (!alfredHeroUnlocked){
            ImageView alfred = (ImageView) findViewById(R.id.alfred);
            alfred.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        }
        if (!erinaHeroUnlocked){
            ImageView alfred = (ImageView) findViewById(R.id.erina);
            alfred.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        }
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
                .setTitle("Damage Dealer (DPS)")
                .setNeutralButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
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
                .setTitle("Healer (Support)")
                .setNeutralButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
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
                .setTitle("Tank (Tank)")
                .setNeutralButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void perlaButtonClick (View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (!perlaHeroUnlocked){
            builder.setMessage(
                    "Reach a high score survival time of 8 minutes to unlock this hero!")
                    .setTitle("Perla (Support)")
                    .setNeutralButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
        } else {
            builder.setMessage(
                    "About: A powerful magician that supports their party members while possessing the ability to control enemy speed.\n\n" +
                            "First Passive: Decrease base DPS reload time.\n\n" +
                            "Second Passive: Increase base DPS damage.\n\n" +
                            "First Ability: Heal the party at intervals until max health\n(3 Power, 11s Cool-down)\n\n" +
                            "Second Ability: Decrease speed of all existing enemies by 50% for 4 seconds, remove ALL enemy speed buffs.\n(12s Cool-down)\n\n" +
                            "Level-Up: Increase effect of passive abilities, decrease ALL ability cool-downs, increase healing power, Slow-Mo deals damage at Level 10.\n\n")
                    .setTitle("Perla (Support)")
                    .setNeutralButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void alfredButtonClick (View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (!alfredHeroUnlocked){
            builder.setMessage(
                    "Gain a total of 500 EXP to unlock this hero!")
                    .setTitle("Alfred (Tank)")
                    .setNeutralButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
        } else {
            builder.setMessage(
                    "About: A loyal knight that protects the party with it's defensive abilities and offensive attacks.\n\n" +
                            "First Passive: Increase max HP of party\n\n" +
                            "Second Passive: Decrease damage taken from enemies\n\n" +
                            "First Ability: Slash the air, creating a force that decreases enemy speed and halves enemy hit-points\n(14s Cool-down)\n\n" +
                            "Second Ability: Double the max HP of party temporarily, all damage taken will be effectively halved\n(10s Duration, 40s Cool-down)\n\n" +
                            "Level-Up: Increase effect of passive abilities, decrease Slash ability cool-downs.\n\n")
                    .setTitle("Alfred (Tank)")
                    .setNeutralButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void erinaButtonClick (View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (!erinaHeroUnlocked){
            builder.setMessage(
                    "Defeat a total of 15,000 enemies to unlock this hero!")
                    .setTitle("Erina (DPS)")
                    .setNeutralButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
        } else {
            builder.setMessage(
                    "About: A commanding pirate with a fearful arsenal of weapons.\n\n" +
                            "Passive: Fire bullets out of the guns on each hand\n\n" +
                            "Ability: Summon a rain of fire that descends upon the battlefield, leaving behind a scorching hot mark that burns enemies\n(15s Cool-down, 3 Charges)\n\n" +
                            "Level-Up: Decrease gun reload time, increase bullet damage, decrease Fire-Rain Cool-down, increase Fire-Rain charges.\n\n")
                    .setTitle("Erina (DPS)")
                    .setNeutralButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
        }
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

    public void recheckHeroesClick(View view){
        SharedPreferences HS = this.getSharedPreferences("highScores", Context.MODE_PRIVATE);
        SharedPreferences characters = this.getSharedPreferences("characters", Context.MODE_PRIVATE);
        SharedPreferences.Editor charEditor = characters.edit();
        if (HS.getInt("highScoreMin", 0) >= 8){
            charEditor.putBoolean("perla", true);
        }
        if (HS.getInt("totalEXPGained", 0) >= 500){
            charEditor.putBoolean("alfred", true);
        }
        if (HS.getInt("totalEnemiesDefeated", 0) >= 15000){
            charEditor.putBoolean("erina", true);
        }
        charEditor.apply();
        super.recreate();
        toPauseMusic = false;
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