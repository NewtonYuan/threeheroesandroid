package com.prestige.prestigegame;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.prestige.prestigegame.gameobject.Circle;
import com.prestige.prestigegame.gameobject.BronzeCoin;
import com.prestige.prestigegame.gameobject.DamageExplosion;
import com.prestige.prestigegame.gameobject.DamageExplosive;
import com.prestige.prestigegame.gameobject.DamageGodArrow;
import com.prestige.prestigegame.gameobject.Enemy;
import com.prestige.prestigegame.gameobject.GoldCoin;
import com.prestige.prestigegame.gameobject.HealerFreeze;
import com.prestige.prestigegame.gameobject.Player;
import com.prestige.prestigegame.gameobject.SilverCoin;
import com.prestige.prestigegame.gameobject.Spell;
import com.prestige.prestigegame.gameobject.TankKnockBack;
import com.prestige.prestigegame.gameobject.TankShield;
import com.prestige.prestigegame.gamepanel.GameOver;
import com.prestige.prestigegame.gamepanel.Joystick;
import com.prestige.prestigegame.gamepanel.LevelUp;
import com.prestige.prestigegame.gamepanel.PauseButton;
import com.prestige.prestigegame.gamepanel.Paused;
import com.prestige.prestigegame.gamepanel.ProgressBar;
import com.prestige.prestigegame.gamepanel.TimeAlive;
import com.prestige.prestigegame.graphics.CharacterList;
import com.prestige.prestigegame.graphics.DamageAnimator;
import com.prestige.prestigegame.graphics.EnemyAnimator;
import com.prestige.prestigegame.graphics.HealerAnimator;
import com.prestige.prestigegame.graphics.SpriteSheet;
import com.prestige.prestigegame.graphics.TankAnimator;
import com.prestige.prestigegame.map.Map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
/**
 * Game manages all objects in the game and is responsible for updating all states and render all
 * objects to the screen
 */
class Game extends SurfaceView implements SurfaceHolder.Callback {
    MediaPlayer bronzeCoinSoundPlayer = MediaPlayer.create(getContext(), R.raw.bronzecoin);
    MediaPlayer silverCoinSoundPlayer = MediaPlayer.create(getContext(), R.raw.silvercoin);
    MediaPlayer goldCoinSoundPlayer = MediaPlayer.create(getContext(), R.raw.goldcoin);
    MediaPlayer arrowSoundPlayer = MediaPlayer.create(getContext(), R.raw.arrow);
    MediaPlayer arrow2SoundPlayer = MediaPlayer.create(getContext(), R.raw.arrow2);
    MediaPlayer arrow3SoundPlayer = MediaPlayer.create(getContext(), R.raw.arrow3);
    MediaPlayer[] arrowSoundList = new MediaPlayer[]{arrowSoundPlayer, arrow2SoundPlayer, arrow3SoundPlayer};
    MediaPlayer healSoundPlayer = MediaPlayer.create(getContext(), R.raw.heal);
    MediaPlayer bigHealSoundPlayer = MediaPlayer.create(getContext(), R.raw.bigheal);
    MediaPlayer explosionSoundPlayer = MediaPlayer.create(getContext(), R.raw.explosion);
    private int joystickPointerId = 0;
    private final Joystick joystick;
    private final Player player;
    private GameLoop gameLoop;
    private List<Spell> spellList = new ArrayList<Spell>();
    private List<DamageGodArrow> godArrowList = new ArrayList<DamageGodArrow>();
    private List<DamageExplosive> explosiveList = new ArrayList<DamageExplosive>();
    private List<BronzeCoin> bronzeCoinList = new ArrayList<BronzeCoin>();
    private List<SilverCoin> silverCoinList = new ArrayList<SilverCoin>();
    private List<GoldCoin> goldCoinList = new ArrayList<GoldCoin>();
    private int numberOfSpellsToCast = 0;
    private GameOver gameOver;
    private Paused paused;
    private LevelUp levelUp;
    private GameDisplay gameDisplay;
    private int JSX = 0;
    private int JSY = 0;
    private Map map;
    private PauseButton pauseButton;
    private int touchX = 0;
    private int touchY = 0;
    private ProgressBar progressBar;
    private BronzeCoin bronzeCoin;
    private SilverCoin silverCoin;
    private GoldCoin goldCoin;
    private Enemy enemy;
    private TankShield tankShield;
    private TankKnockBack tankKnockBack;
    private HealerFreeze healerFreeze;
    private int screenWidth;
    private int screenHeight;
    private final int initialEnemyDamage = 5;
    private int enemyDamage = initialEnemyDamage;
    public boolean isPaused = false;
    private EnemyAnimator enemyAnimator;
    private DamageAnimator damageAnimator;
    private HealerAnimator healerAnimator;
    private DamageExplosion damageExplosion;
    private int multiShotCount = 0;
    private int multiShotTimer = 0;
    private final double UPS = GameLoop.MAX_UPS;
    private double damageDelay = UPS*1;
    private int damageTimeCounter = 0;
    private int godArrowTimeCounter = 0;
    private int healTimeCounter = 0;
    private int bigHealTimeCounter = 0;
    private int explosiveTimeCounter = 0;
    private int enemyNumberTimeCounter = 0;
    private int enemyLevelUpCounter = 0;
    private final double multiShotDelay = UPS/3;
    private TimeAlive timeAlive;
    private Activity gameActivity;
    public int playerHP;
    public boolean adShown = false;

    public void checkResumeTimer(){
        new java.util.Timer().schedule(
                new java.util.TimerTask(){
                    @Override
                    public void run(){
                        checkResume();
                    }
                },
                100
        );
    }

    public Game(Context context, Activity activity) {
        super(context);
        this.gameActivity = activity;

        DisplayMetrics displayMetrics = new DisplayMetrics();

        // Get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        gameLoop = new GameLoop(this, surfaceHolder);

        // Initialize game panels
        gameOver = new GameOver(context);
        paused = new Paused(context);
        joystick = new Joystick(JSX, JSY, 200, 100);
        progressBar = new ProgressBar(context);

        // Initialize game objects
        SpriteSheet spriteSheet = new SpriteSheet(context);
        CharacterList characterList = new CharacterList(context);
        TankAnimator tankAnimator = new TankAnimator(context);
        healerAnimator = new HealerAnimator(context);
        enemyAnimator = new EnemyAnimator(context);
        damageAnimator = new DamageAnimator(context);
        player = new Player(context, joystick, 0, 0, 130, tankAnimator, damageAnimator, healerAnimator, progressBar);
        tankShield = new TankShield(context, player, 0, 0, 200);
        tankKnockBack = new TankKnockBack(context, player, 0, 0, 400);
        healerFreeze = new HealerFreeze(context, player, 0, 0, 100);
        levelUp = new LevelUp(context, tankAnimator, damageAnimator, healerAnimator, player);
        bronzeCoin = new BronzeCoin(getContext(), player, 0, 0, 25);
        silverCoin = new SilverCoin(getContext(), player, 0, 0, 25);
        goldCoin = new GoldCoin(getContext(), player, 0, 0, 25);
        damageExplosion = new DamageExplosion(context, player, 0,0, 125);
        enemy = new Enemy(getContext(), player, 0, 0, 50, enemyAnimator, 1);

        // Initialize display and center it around the player
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        gameDisplay = new GameDisplay(displayMetrics.widthPixels, displayMetrics.heightPixels, player);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        map = new Map(context);
        pauseButton = new PauseButton(context, player);
        timeAlive = new TimeAlive(context);

        setFocusable(true);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Handle user input touch event actions
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchX = (int) event.getX();
                touchY = (int) event.getY();
                checkReturnHome();
            case MotionEvent.ACTION_POINTER_DOWN:
                JSX = (int) event.getX();
                JSY = (int) event.getY();
                if (JSY >= levelUp.getLowestPosition()){
                    if (JSY >= player.getPositionY()){
                        if (joystick.isPressed((double) event.getX(), (double) event.getY())) {
                            // Joystick is pressed in this event -> setIsPressed(true) and store pointer id
                            joystickPointerId = event.getPointerId(event.getActionIndex());
                            joystick.setIsPressed(true);
                        }
                    }
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (JSY >= screenHeight/5*4-200){
                    if (!isPaused){
                        // Joystick was pressed previously and is now moved
                        joystick.setActuator((double) event.getX(), (double) event.getY());
                    }
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (JSY >= screenHeight/5*4-200) {
                    if (!isPaused) {
                        if (joystickPointerId == event.getPointerId(event.getActionIndex())) {
                            // joystick pointer was let go off -> setIsPressed(false) and resetActuator()
                            joystick.setIsPressed(false);
                            joystick.resetActuator();
                        }
                    }
                }
                return true;

        }

        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("Game.java", "surfaceCreated()");
        if (gameLoop.getState().equals(Thread.State.TERMINATED)) {
            SurfaceHolder surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            gameLoop = new GameLoop(this, surfaceHolder);
        }
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("Game.java", "surfaceChanged()");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("Game.java", "surfaceDestroyed()");
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        map.draw(canvas, player);
        tankKnockBack.drawTankKnockBack(canvas);
        healerFreeze.drawHealerFreeze(canvas, gameDisplay);

        // Draw game objects
        player.draw(canvas, gameDisplay);

        for (Enemy enemy : enemyAnimator.enemyList) {
            enemy.drawEnemy(canvas, gameDisplay);
        }

        for (Spell spell : spellList) {
            spell.drawArrow(canvas, gameDisplay);
        }

        for (DamageGodArrow godArrow : godArrowList){
            godArrow.drawArrow(canvas, gameDisplay);
        }

        for (DamageExplosive explosive : explosiveList){
            explosive.drawArrow(canvas, gameDisplay);
        }

        for (BronzeCoin coin : bronzeCoinList){
            coin.drawCoin(canvas, gameDisplay);
        }

        for (SilverCoin coin : silverCoinList){
            coin.drawCoin(canvas, gameDisplay);
        }

        for (GoldCoin coin : goldCoinList){
            coin.drawCoin(canvas, gameDisplay);
        }

        damageExplosion.drawExplosion(canvas);

        // Draw game panels
        if (JSY >= screenHeight/5*4-200){
            joystick.draw(canvas);
        }

        progressBar.draw(canvas);
        pauseButton.draw(canvas, touchX, touchY);

        tankShield.drawTankShield(canvas);
        timeAlive.drawTime(canvas);

        // Draw Game over if the player is dead
        if (player.getHealthPoint() <= 0) {
            gameOver.draw(canvas);
        }

        if (pauseButton.isClicked(touchX, touchY) && player.getHealthPoint() > 0){
            paused.draw(canvas);
            isPaused = true;
            pause();
        }

        if (progressBar.levelUpped){
            Log.i("WAITAMINUTE", "");
            levelUp.draw(canvas);
            isPaused = true;
            pause();
        }
    }

    public void update() {
        // Stop updating the game if the player is dead
        if (player.getHealthPoint() <= 0 || pauseButton.isClicked(touchX, touchY)) {
            return;
        }

        int positionX = (int)(player.getPositionX()%1500);
        if (Math.pow(positionX, 2) <= 10){
            map.updateX(player);
        }

        int positionY = (int)(player.getPositionY()%500);
        if (Math.pow(positionY, 2) <= 10){
            map.updateY(player);
        }

        // Update game state
        if (JSY >= screenHeight/5*4-200){
            joystick.update(JSX, JSY);
        }
        player.update();
        tankShield.update();
        tankKnockBack.update();
        healerFreeze.update();
        damageExplosion.update();

        enemyNumberTimeCounter++;
        if (enemyNumberTimeCounter >= enemy.delay){
            enemy.maxEnemies += 1;
            enemyNumberTimeCounter = 0;
        }

        enemyLevelUpCounter++;
        if (enemyLevelUpCounter >= UPS*1){
            int levelIndex = (int)(Math.random()*100);
            int randomLevel = enemy.levelProbability.get(levelIndex);
            randomLevel++;
            enemy.levelProbability.set(levelIndex, randomLevel);
            enemyLevelUpCounter = 0;
        }
        // Spawn enemy
        if(enemy.readyToSpawn()) {
            if(enemyAnimator.enemyList.toArray().length <= enemy.maxEnemies){
                enemyAnimator.enemyList.add(new Enemy(getContext(), player, enemy.getNewPositionX(), enemy.getNewPositionY(), 50, enemyAnimator, enemy.getEnemyLevel()));
            }
            if (enemy.updatesUntilNextSpawn >= 5)
            enemy.updatesUntilNextSpawn -= 1;
        }

        // Update states of all enemies
        for (Enemy enemy : enemyAnimator.enemyList) {
            enemy.update();
        }

        enemy.getNewPosition();

        // Spawn coin
        if(BronzeCoin.readyToSpawn()) {
            if(bronzeCoinList.toArray().length <= 20) {
                bronzeCoinList.add(new BronzeCoin(getContext(), player, bronzeCoin.getNewPositionX(), bronzeCoin.getNewPositionY(), 25));
            }
        }
        if(SilverCoin.readyToSpawn()) {
            if(silverCoinList.toArray().length <= 5) {
                silverCoinList.add(new SilverCoin(getContext(), player, silverCoin.getNewPositionX(), silverCoin.getNewPositionY(), 25));
            }
        }
        if(GoldCoin.readyToSpawn()) {
            if(goldCoinList.toArray().length <= 1) {
                goldCoinList.add(new GoldCoin(getContext(), player, silverCoin.getNewPositionX(), silverCoin.getNewPositionY(), 25));
            }
        }

        bronzeCoin.update();
        silverCoin.update();
        goldCoin.update();

        damageTimeCounter++;
        if (damageTimeCounter >= damageAnimator.arrowReloadTime*UPS){
            spellList.add(new Spell(getContext(), player));
            arrowSoundList[(int) (Math.random()*3)].start();
            damageTimeCounter = 0;
        }
        godArrowTimeCounter++;
        if (damageAnimator.godArrowActivated){
            if (godArrowTimeCounter >= UPS*7){
                godArrowList.add(new DamageGodArrow(getContext(), player));
                arrowSoundList[(int) (Math.random()*3)].start();
                godArrowTimeCounter = 0;
            }
        }
        explosiveTimeCounter++;
        if (explosiveTimeCounter >= damageAnimator.explosiveArrowReloadTime*UPS && damageAnimator.explosiveArrowActivated){
            explosiveList.add(new DamageExplosive(getContext(), player));
            arrowSoundList[(int) (Math.random()*3)].start();
            explosiveTimeCounter = 0;
        }

        healTimeCounter++;
        if (healTimeCounter >= healerAnimator.healCoolDown*UPS && player.getHealthPoint() < player.MAX_HEALTH_POINTS) {
            player.setHealthPoint(player.getHealthPoint()+healerAnimator.healAmount);
            if (player.getHealthPoint() > player.MAX_HEALTH_POINTS){
                player.setHealthPoint(player.MAX_HEALTH_POINTS);
            } else {
                healSoundPlayer.start();
            }
            healTimeCounter = 0;
        }
        bigHealTimeCounter++;
        if (bigHealTimeCounter >= healerAnimator.bigHealCoolDown*UPS && player.getHealthPoint() < player.MAX_HEALTH_POINTS && healerAnimator.bigHealActivated) {
            player.setHealthPoint(player.getHealthPoint()+player.MAX_HEALTH_POINTS/2);
            if (player.getHealthPoint() > player.MAX_HEALTH_POINTS){
                player.setHealthPoint(player.MAX_HEALTH_POINTS);
            } else {
                bigHealSoundPlayer.start();
            }
            bigHealTimeCounter = 0;
        }

        for (Spell spell : spellList) {
            if (!spell.foundEnemy){
                spell.getClosestEnemy(enemyAnimator.enemyList, player);
            }
            spell.update();
            damageAnimator.bowAngle = spell.getArrowAngle();
        }
        if (damageAnimator.godArrowActivated){
            for (DamageGodArrow godArrow : godArrowList) {
                if(!godArrow.foundEnemy){
                    godArrow.getClosestEnemy(enemyAnimator.enemyList, player);
                }
                godArrow.update();
                damageAnimator.bowAngle = godArrow.getArrowAngle();
            }
        }
        for (DamageExplosive explosive : explosiveList) {
            if(!explosive.foundEnemy){
                explosive.getClosestEnemy(enemyAnimator.enemyList, player);
            }
            explosive.update();
            damageAnimator.bowAngle = explosive.getArrowAngle();
        }

        // Iterate through enemyList and Check for collision between each enemy and the player and
        // spells in spellList.
        Iterator<Enemy> iteratorEnemy = enemyAnimator.enemyList.iterator();
        while (iteratorEnemy.hasNext()) {
            Enemy enemy = iteratorEnemy.next();
            if (enemy.finishedDying){
                iteratorEnemy.remove();
                enemy.finishedDying = false;
                continue;
            }
            if (tankShield.activated){
                if (Circle.isColliding(enemy, tankShield)) {
                    // Remove enemy if it collides with the player
                    tankShield.checkShield();
                    iteratorEnemy.remove();
                    continue;
                }
            }
            if (Circle.isColliding(enemy, player)) {
                // Remove enemy if it collides with the player
                iteratorEnemy.remove();
                if (player.toggleInvincible){
                } else {
                    player.setHealthPoint(player.getHealthPoint() - enemy.damage);
                }
                continue;
            }
            if (tankKnockBack.activated && Circle.isColliding(enemy, tankKnockBack)) {
                enemy.knockedBack = true;
                if (tankKnockBack.improvedKnockBack){
                    enemy.hitPoints--;
                }
                continue;
            }
            if (healerFreeze.activated && Circle.isColliding(enemy, healerFreeze) && !enemy.frozen){
                enemy.frozen = true;
                if (healerFreeze.improvedFreeze){
                    enemy.hitPoints--;
                }
                continue;
            }
            if (enemy.getPositionX() >= player.getPositionX()+screenWidth ||
                    enemy.getPositionX() <= player.getPositionX()-screenWidth ||
                    enemy.getPositionY() >= player.getPositionY()+screenHeight ||
                    enemy.getPositionY() <= player.getPositionY()-screenHeight){
                enemy.hitPoints -= enemy.hitPoints;
                continue;
            }

            Iterator<Spell> iteratorSpell = spellList.iterator();
            while (iteratorSpell.hasNext()) {
                Circle spell = iteratorSpell.next();
                // Remove enemy if it collides with a spell
                if (Circle.isColliding(spell, enemy)) {
                    iteratorSpell.remove();
                    enemy.hitPoints -= damageAnimator.arrowDamage;
                    break;
                }
                if (spell.getPositionX() >= player.getPositionX()+screenWidth ||
                        spell.getPositionX() <= player.getPositionX()-screenWidth ||
                        spell.getPositionY() >= player.getPositionY()+screenHeight ||
                        spell.getPositionY() <= player.getPositionY()-screenHeight){
                    iteratorSpell.remove();
                    break;
                }
            }

            Iterator<DamageGodArrow> iteratorGodArrow = godArrowList.iterator();
            while (iteratorGodArrow.hasNext()) {
                Circle godArrow = iteratorGodArrow.next();
                // Remove enemy if it collides with a spell
                if (Circle.isColliding(godArrow, enemy)) {
                    enemy.hitPoints -= damageAnimator.arrowDamage*3;
                    break;
                }
                if (godArrow.getPositionX() >= player.getPositionX()+screenWidth ||
                        godArrow.getPositionX() <= player.getPositionX()-screenWidth ||
                        godArrow.getPositionY() >= player.getPositionY()+screenHeight ||
                        godArrow.getPositionY() <= player.getPositionY()-screenHeight){
                    iteratorGodArrow.remove();
                    break;
                }
            }

            Iterator<DamageExplosive> iteratorExplosive = explosiveList.iterator();
            while (iteratorExplosive.hasNext()) {
                Circle explosive = iteratorExplosive.next();
                // Remove enemy if it collides with a spell
                if (Circle.isColliding(explosive, enemy)) {
                    explosionSoundPlayer.start();
                    damageExplosion.getExplosionPosition((int)enemy.getPositionX(), (int)enemy.getPositionY());
                    iteratorExplosive.remove();
                    break;
                }
                if (explosive.getPositionX() >= player.getPositionX()+screenWidth ||
                        explosive.getPositionX() <= player.getPositionX()-screenWidth ||
                        explosive.getPositionY() >= player.getPositionY()+screenHeight ||
                        explosive.getPositionY() <= player.getPositionY()-screenHeight){
                    iteratorExplosive.remove();
                    break;
                }
            }
            if (damageExplosion.activated){
                if (Circle.isColliding(enemy, damageExplosion)){
                    enemy.hitPoints -= damageAnimator.arrowDamage*2;
                }
            }
        }


        Iterator<BronzeCoin> iteratorBronzeCoin = bronzeCoinList.iterator();
        while (iteratorBronzeCoin.hasNext()) {
            Circle coin = iteratorBronzeCoin.next();
            if (Circle.isColliding(coin, player)) {
                // Remove enemy if it collides with the player
                iteratorBronzeCoin.remove();
                progressBar.setProgressPoints(progressBar.getProgressPoints() + 1);
                bronzeCoinSoundPlayer.start();
                continue;
            }
            if (coin.getPositionX() >= player.getPositionX()+screenWidth*3 ||
                coin.getPositionX() <= player.getPositionX()-screenWidth*3 ||
                coin.getPositionY() >= player.getPositionY()+screenHeight*1.5 ||
                coin.getPositionY() <= player.getPositionY()-screenHeight*1.5){
                iteratorBronzeCoin.remove();
                break;
            }
        }
        Iterator<SilverCoin> iteratorSilverCoin = silverCoinList.iterator();
        while (iteratorSilverCoin.hasNext()) {
            Circle coin = iteratorSilverCoin.next();
            if (Circle.isColliding(coin, player)) {
                // Remove enemy if it collides with the player
                iteratorSilverCoin.remove();
                progressBar.setProgressPoints(progressBar.getProgressPoints() + 3);
                silverCoinSoundPlayer.start();
                continue;
            }
            if (coin.getPositionX() >= player.getPositionX()+screenWidth*3 ||
                    coin.getPositionX() <= player.getPositionX()-screenWidth*3 ||
                    coin.getPositionY() >= player.getPositionY()+screenHeight*1.5 ||
                    coin.getPositionY() <= player.getPositionY()-screenHeight*1.5){
                iteratorSilverCoin.remove();
                break;
            }
        }
        Iterator<GoldCoin> iteratorGoldCoin = goldCoinList.iterator();
        while (iteratorGoldCoin.hasNext()) {
            Circle coin = iteratorGoldCoin.next();
            if (Circle.isColliding(coin, player)) {
                // Remove enemy if it collides with the player
                iteratorGoldCoin.remove();
                progressBar.setProgressPoints(progressBar.getProgressPoints() + 10);
                goldCoinSoundPlayer.start();
                continue;
            }
            if (coin.getPositionX() >= player.getPositionX()+screenWidth*3 ||
                    coin.getPositionX() <= player.getPositionX()-screenWidth*3 ||
                    coin.getPositionY() >= player.getPositionY()+screenHeight*1.5 ||
                    coin.getPositionY() <= player.getPositionY()-screenHeight*1.5){
                iteratorGoldCoin.remove();
                break;
            }
        }

        // Update gameDisplay so that it's center is set to the new center of the player's
        // game coordinates
        timeAlive.update();
        gameDisplay.update();
    }

    public void pause() {
        gameLoop.pauseLoop();
    }

    public void resume(){
        gameLoop.resumeLoop();
        isPaused = false;
    }

    public void checkResume() {
        if (isPaused) {
            if (paused.resumeButtonClicked(touchX, touchY)){
                resume();
            }
            if (progressBar.levelUpped) {
                if (levelUp.optionOneClicked(touchX, touchY)){
                    resume();
                    isPaused = false;
                    damageAnimator.checkLevelPerks(player.damageLevel);
                    player.damageLevel++;
                    progressBar.levelUpped = false;
                    player.toggleInvincible = true;
                }
                if (levelUp.optionTwoClicked(touchX, touchY)){
                    resume();
                    isPaused = false;
                    if (player.tankLevel >= 10){
                        player.MAX_HEALTH_POINTS += 3;
                        player.setHealthPoint(player.getHealthPoint()+3);
                    } else {
                        player.MAX_HEALTH_POINTS += 5;
                        player.setHealthPoint(player.getHealthPoint()+5);
                    }
                    Log.i("HP", String.valueOf(player.MAX_HEALTH_POINTS));
                    player.tankLevel++;
                    progressBar.levelUpped = false;
                    player.toggleInvincible = true;
                }
                if (levelUp.optionThreeClicked(touchX, touchY)){
                    resume();
                    isPaused = false;
                    healerAnimator.checkLevelPerks(player.healerLevel);
                    player.healerLevel++;
                    progressBar.levelUpped = false;
                    player.toggleInvincible = true;
                }
            }
        }
    }

    public void checkReturnHome() {
        if (player.getHealthPoint() <= 0){
            if (gameOver.returnHomeButtonClicked(touchX, touchY) && !adShown){
                Handler mainHandler = new Handler(getContext().getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        ((GameActivity)getContext()).showRewardedVideo();
                    }
                };
                mainHandler.post(myRunnable);
                gameOver.adShown = true;
            }
            if (gameOver.returnHomeButtonClicked(touchX, touchY) && adShown){
                Handler mainHandler = new Handler(getContext().getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        ((GameActivity)getContext()).showInterstitial();
                    }
                };
                mainHandler.post(myRunnable);
            }
            if (gameOver.noThanksButtonClicked(touchX, touchY)){
                Handler mainHandler = new Handler(getContext().getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        ((GameActivity)getContext()).showInterstitial();
                    }
                };
                mainHandler.post(myRunnable);
            }
        } else {
            if (paused.quitButtonClicked(touchX, touchY) && isPaused){
                Handler mainHandler = new Handler(getContext().getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        ((GameActivity)getContext()).showInterstitial();
                    }
                };
                mainHandler.post(myRunnable);
            }
        }
    }

    public void revivePlayer(){
        player.setHealthPoint(player.MAX_HEALTH_POINTS);
    }

    public void checkStopped(){
        if (player.getHealthPoint() <= 0) {
            gameLoop.pauseLoop();
        }
        if (isPaused) {
            if (paused.quitButtonClicked(touchX, touchY)){
                Handler mainHandler = new Handler(getContext().getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        ((GameActivity)getContext()).showInterstitial();
                    }
                };
                mainHandler.post(myRunnable);
            }
        }
    }

    public void stopLoop(){
        gameLoop.stopLoop();
    }
}
