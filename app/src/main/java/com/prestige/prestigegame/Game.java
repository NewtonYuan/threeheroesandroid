package com.prestige.prestigegame;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.games.PlayGames;
import com.prestige.prestigegame.gameobject.Circle;
import com.prestige.prestigegame.gameobject.BronzeCoin;
import com.prestige.prestigegame.gameobject.Enemy;
import com.prestige.prestigegame.gameobject.GoldCoin;
import com.prestige.prestigegame.gameobject.Player;
import com.prestige.prestigegame.gameobject.SilverCoin;
import com.prestige.prestigegame.gameobject.Spike;
import com.prestige.prestigegame.gamepanel.GameOver;
import com.prestige.prestigegame.gamepanel.Joystick;
import com.prestige.prestigegame.gamepanel.LevelUp;
import com.prestige.prestigegame.gamepanel.PauseButton;
import com.prestige.prestigegame.gamepanel.Paused;
import com.prestige.prestigegame.gamepanel.ProgressBar;
import com.prestige.prestigegame.gamepanel.TimeAlive;
import com.prestige.prestigegame.graphics.CharacterList;
import com.prestige.prestigegame.graphics.EnemyAnimator;
import com.prestige.prestigegame.graphics.SpriteSheet;
import com.prestige.prestigegame.heroes.alfred.Alfred;
import com.prestige.prestigegame.heroes.damage.Damage;
import com.prestige.prestigegame.heroes.erina.Erina;
import com.prestige.prestigegame.heroes.healer.Healer;
import com.prestige.prestigegame.heroes.perla.Perla;
import com.prestige.prestigegame.heroes.tank.Tank;
import com.prestige.prestigegame.map.Map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
/**
 * Game manages all objects in the game and is responsible for updating all states and render all
 * objects to the screen
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback {
    //Heroes
    public Damage damageHero;
    public Healer healerHero;
    public Tank tankHero;
    public Perla perlaHero;
    public Alfred alfredHero;
    public Erina erinaHero;

    //Selections
    public boolean damageHeroSelected;
    public boolean healerHeroSelected;
    public boolean tankHeroSelected;
    public boolean perlaHeroSelected;
    public boolean alfredHeroSelected;
    public boolean erinaHeroSelected;

    //Values
    public double dpsDamage;
    public double dpsReloadTime;

    //Draw
    private Paint selectedPaint = new Paint();
    private Paint levelPaint = new Paint();
    private Paint strokePaint = new Paint();
    private Paint invincibilityPaint = new Paint();

    //Hero Text
    String dpsText;
    String healerText;
    String tankText;

    MediaPlayer bronzeCoinSoundPlayer = MediaPlayer.create(getContext(), R.raw.bronzecoin);
    MediaPlayer silverCoinSoundPlayer = MediaPlayer.create(getContext(), R.raw.silvercoin);
    MediaPlayer goldCoinSoundPlayer = MediaPlayer.create(getContext(), R.raw.goldcoin);
    private int joystickPointerId = 0;
    private final Joystick joystick;
    private final Player player;
    private GameLoop gameLoop;
    private List<BronzeCoin> bronzeCoinList = new ArrayList<BronzeCoin>();
    private List<SilverCoin> silverCoinList = new ArrayList<SilverCoin>();
    private List<GoldCoin> goldCoinList = new ArrayList<GoldCoin>();
    private List<Spike> spikeList = new ArrayList<Spike>();
    private GameOver gameOver;
    public Paused paused;
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
    private Spike spike;
    private Enemy enemy;
    public   int screenWidth;
    public int screenHeight;
    private final int initialEnemyDamage = 5;
    private int enemyDamage = initialEnemyDamage;
    public boolean isPaused = false;
    public boolean isPausedButton = false;
    private EnemyAnimator enemyAnimator;
    private int multiShotCount = 0;
    private int multiShotTimer = 0;
    private final double UPS = GameLoop.MAX_UPS;
    private int enemyNumberTimeCounter = 0;
    private int enemyLevelUpCounter = 0;
    private int totalEnemiesDefeated = 0;
    private int totalCoinsCollected = 0;
    private int totalEXPGained = 0;
    private final double multiShotDelay = UPS/3;
    private TimeAlive timeAlive;
    private Activity gameActivity;
    public int playerHP;
    public boolean adShown = false;
    private Canvas pausedCanvas;
    public boolean sfxMuted = false;
    public float reductionRatio = 1;

    public Game(Context context, Activity activity) {
        super(context);
        this.gameActivity = activity;

        SharedPreferences settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        sfxMuted = settings.getBoolean("sfxMuted", false);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();

        // Get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        paused = new Paused(context);
        gameLoop = new GameLoop(this, surfaceHolder);

        // Initialize game panels
        gameOver = new GameOver(context);
        joystick = new Joystick(JSX, JSY, 160, 80);
        progressBar = new ProgressBar(context);

        //Draw
        final Typeface font = ResourcesCompat.getFont(context, R.font.customfont);
        levelPaint.setColor(Color.WHITE);
        levelPaint.setTextSize(40);
        levelPaint.setTextAlign(Paint.Align.CENTER);
        levelPaint.setTypeface(font);

        strokePaint.setColor(Color.BLACK);
        strokePaint.setTextSize(40);
        strokePaint.setTextAlign(Paint.Align.CENTER);
        strokePaint.setTypeface(font);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(5);

        ColorFilter invincibilityFilter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        invincibilityPaint.setColorFilter(invincibilityFilter);

        // Initialize game objects
        SpriteSheet spriteSheet = new SpriteSheet(context);
        CharacterList characterList = new CharacterList(context);
        enemyAnimator = new EnemyAnimator(context);
        player = new Player(context, joystick, 0, 0, 75, progressBar);
        levelUp = new LevelUp(context, player);
        bronzeCoin = new BronzeCoin(getContext(), player, 0, 0, 20);
        spike = new Spike(getContext(), player, 0, 0, 25);
        silverCoin = new SilverCoin(getContext(), player, 0, 0, 20);
        goldCoin = new GoldCoin(getContext(), player, 0, 0, 20);
        enemy = new Enemy(getContext(), player, 0, 0, 30, enemyAnimator, 1);

        // Initialize display and center it around the player
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        gameDisplay = new GameDisplay(displayMetrics.widthPixels, displayMetrics.heightPixels, player);

        map = new Map(context);
        pauseButton = new PauseButton(context, player);
        timeAlive = new TimeAlive(context);

        //Selection Booleans
        damageHeroSelected = ((GameActivity)getContext()).damageHeroSelected;
        healerHeroSelected = ((GameActivity)getContext()).healerHeroSelected;
        tankHeroSelected = ((GameActivity)getContext()).tankHeroSelected;
        perlaHeroSelected = ((GameActivity)getContext()).perlaHeroSelected;
        alfredHeroSelected = ((GameActivity)getContext()).alfredHeroSelected;
        erinaHeroSelected = ((GameActivity)getContext()).erinaHeroSelected;

        //Selected Heroes
        if (healerHeroSelected){ healerHero = new Healer(context, screenWidth, screenHeight, player, this); }
        if (tankHeroSelected){ tankHero = new Tank(context, screenWidth, screenHeight, player, this); }
        if (perlaHeroSelected){ perlaHero = new Perla(context, screenWidth, screenHeight, player); }
        if (alfredHeroSelected){ alfredHero = new Alfred(context, screenWidth, screenHeight, player); }

        if (damageHeroSelected){
            damageHero = new Damage(context, screenWidth, screenHeight, player);
        }
        if (erinaHeroSelected){
            erinaHero = new Erina(context, screenWidth, screenHeight, player, this);
        }

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
                if (joystick.isPressed((double) event.getX(), (double) event.getY())) {
                    // Joystick is pressed in this event -> setIsPressed(true) and store pointer id
                    joystickPointerId = event.getPointerId(event.getActionIndex());
                    joystick.setIsPressed(true);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!isPaused){
                    // Joystick was pressed previously and is now moved
                    joystick.setActuator((double) event.getX(), (double) event.getY());
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (!isPaused) {
                    if (joystickPointerId == event.getPointerId(event.getActionIndex())) {
                        // joystick pointer was let go off -> setIsPressed(false) and resetActuator()
                        joystick.setIsPressed(false);
                        joystick.resetActuator();
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

        if (player.invincibilityMode){ selectedPaint = invincibilityPaint; }
        if (!player.invincibilityMode){ selectedPaint = null; }

        map.draw(canvas, player);

        // Draw game objects
        player.draw(canvas, gameDisplay);

        //Draw Selected Heroes
        if (damageHeroSelected){ damageHero.draw(canvas, player, selectedPaint, levelPaint, strokePaint, gameDisplay); }
        if (healerHeroSelected){ healerHero.draw(canvas, player, selectedPaint, levelPaint, strokePaint, gameDisplay); }
        if (tankHeroSelected){ tankHero.draw(canvas, player, selectedPaint, levelPaint, strokePaint, gameDisplay); }
        if (perlaHeroSelected){ perlaHero.draw(canvas, player, selectedPaint, levelPaint, strokePaint, gameDisplay); }
        if (alfredHeroSelected){ alfredHero.draw(canvas, player, selectedPaint, levelPaint, strokePaint, gameDisplay); }
        if (erinaHeroSelected){ erinaHero.draw(canvas, player, selectedPaint, levelPaint, strokePaint, gameDisplay); }

        for (Enemy enemy : enemyAnimator.enemyList) {
            enemy.drawEnemy(canvas, gameDisplay);
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

        for (Spike spike : spikeList){
            spike.drawSpike(canvas, gameDisplay);
        }

        // Draw game panels
        joystick.draw(canvas);

        progressBar.draw(canvas);
        pauseButton.draw(canvas, touchX, touchY);

        timeAlive.drawTime(canvas);
        player.healthBar.draw(canvas, gameDisplay);

        // Draw Game over if the player is dead
        if (player.getHealthPoint() <= 0) {
            gameOver.draw(canvas);
        }

        if (pauseButton.isClicked(touchX, touchY) && player.getHealthPoint() > 0){
            paused.draw(canvas);
            pausedCanvas = canvas;
            isPaused = true;
            isPausedButton = true;
            pause();
        }

        if (progressBar.levelUpped){
            touchX = 0;
            touchY = 0;

            if (damageHeroSelected) {
                damageHero.getLevelUpText(player.damageLevel);
                dpsText = damageHero.levelUpMsg;
            }
            if (erinaHeroSelected) {
                erinaHero.getLevelUpText(player.damageLevel);
                dpsText = erinaHero.levelUpMsg;
            }

            if (healerHeroSelected) {
                healerHero.getLevelUpText(player.healerLevel);
                healerText = healerHero.levelUpMsg;
            }
            if (perlaHeroSelected) {
                perlaHero.getLevelUpText(player.healerLevel);
                healerText = perlaHero.levelUpMsg;
            }

            if (tankHeroSelected) {
                tankHero.getLevelUpText(player.tankLevel);
                tankText = tankHero.levelUpMsg;
            }
            if (alfredHeroSelected) {
                alfredHero.getLevelUpText(player.tankLevel);
                tankText = alfredHero.levelUpMsg;
            }

            levelUp.draw(canvas, dpsText, healerText, tankText);
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
        joystick.update(JSX, JSY);
        player.update();

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
                enemyAnimator.enemyList.add(new Enemy(getContext(), player, enemy.getNewPositionX(), enemy.getNewPositionY(), 30, enemyAnimator, enemy.getEnemyLevel()));
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
                bronzeCoinList.add(new BronzeCoin(getContext(), player, bronzeCoin.getNewPositionX(), bronzeCoin.getNewPositionY(), 20));
            }
        }
        if(SilverCoin.readyToSpawn()) {
            if(silverCoinList.toArray().length <= 5) {
                silverCoinList.add(new SilverCoin(getContext(), player, silverCoin.getNewPositionX(), silverCoin.getNewPositionY(), 20));
            }
        }
        if(GoldCoin.readyToSpawn()) {
            if(goldCoinList.toArray().length <= 1) {
                goldCoinList.add(new GoldCoin(getContext(), player, goldCoin.getNewPositionX(), goldCoin.getNewPositionY(), 20));
            }
        }
        if(Spike.readyToSpawn()) {
            if(spikeList.toArray().length <= 16) {
                spikeList.add(new Spike(getContext(), player, spike.getNewPositionX(), spike.getNewPositionY(), 25));
            }
        }

        bronzeCoin.update();
        silverCoin.update();
        goldCoin.update();
        spike.update();

        if (healerHeroSelected) { healerHero.update(player, enemyAnimator); }
        if (tankHeroSelected) { tankHero.update(player, enemyAnimator); }
        if (perlaHeroSelected) { perlaHero.update(player, enemyAnimator, this); }
        if (alfredHeroSelected) { alfredHero.update(player, enemyAnimator, this); }

        if (damageHeroSelected) {
            damageHero.update(player, enemyAnimator, this);
            dpsDamage = damageHero.baseArrowDamage;
            dpsReloadTime = damageHero.baseArrowReloadTime;
        }
        if (erinaHeroSelected) {
            erinaHero.update(player, enemyAnimator);
            dpsDamage = erinaHero.baseBulletDamage;
            dpsReloadTime = erinaHero.baseBulletCD;
        }

        // Iterate through enemyList and Check for collision between each enemy and the player and
        // spells in spellList.
        Iterator<Enemy> iteratorEnemy = enemyAnimator.enemyList.iterator();
        while (iteratorEnemy.hasNext()) {
            Enemy enemy = iteratorEnemy.next();
            if (enemy.finishedDying){
                iteratorEnemy.remove();
                totalEnemiesDefeated++;
                enemy.finishedDying = false;
                continue;
            }
            if (Circle.isColliding(enemy, player)) {
                // Remove enemy if it collides with the player
                iteratorEnemy.remove();
                totalEnemiesDefeated++;
                if (player.toggleInvincible){
                } else {
                    player.setHealthPoint(player.getHealthPoint() - enemy.damage*reductionRatio);
                }
                continue;
            }
            if (enemy.getPositionX() >= player.getPositionX()+screenWidth ||
                    enemy.getPositionX() <= player.getPositionX()-screenWidth ||
                    enemy.getPositionY() >= player.getPositionY()+screenHeight ||
                    enemy.getPositionY() <= player.getPositionY()-screenHeight){
                enemy.hitPoints -= enemy.hitPoints;
            }
        }


        Iterator<BronzeCoin> iteratorBronzeCoin = bronzeCoinList.iterator();
        while (iteratorBronzeCoin.hasNext()) {
            Circle coin = iteratorBronzeCoin.next();
            if (Circle.isColliding(coin, player)) {
                // Remove enemy if it collides with the player
                iteratorBronzeCoin.remove();
                progressBar.setProgressPoints(progressBar.getProgressPoints() + 1);
                if (!sfxMuted) { bronzeCoinSoundPlayer.start(); }
                totalCoinsCollected++;
                totalEXPGained += 1;
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
                if (!sfxMuted) { silverCoinSoundPlayer.start(); }
                totalCoinsCollected++;
                totalEXPGained += 2;
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
                if (!sfxMuted) { goldCoinSoundPlayer.start(); }
                totalCoinsCollected++;
                totalEXPGained += 3;
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
        Iterator<Spike> spikeIterator = spikeList.iterator();
        while (spikeIterator.hasNext()) {
            Circle spike = spikeIterator.next();
            if (Circle.isColliding(spike, player)) {
                // Remove enemy if it collides with the player
                spikeIterator.remove();
                if (!sfxMuted) { goldCoinSoundPlayer.start(); }
                player.setHealthPoint(player.getHealthPoint()-20);
                continue;
            }
            if (spike.getPositionX() >= player.getPositionX()+screenWidth*4 ||
                    spike.getPositionX() <= player.getPositionX()-screenWidth*4 ||
                    spike.getPositionY() >= player.getPositionY()+screenHeight*2 ||
                    spike.getPositionY() <= player.getPositionY()-screenHeight*2){
                spikeIterator.remove();
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
        if (isPausedButton) {
            if (paused.resumeButtonClicked(touchX, touchY)) {
                resume();
                isPausedButton = false;
            }
            if (paused.musicSoundClicked(touchX, touchY)) {
                paused.musicMuted = !paused.musicMuted;
                ((GameActivity)getContext()).muteMusic();
                touchX = 0;
                touchY = 0;
                gameLoop.pausedDrawRequest = true;
                SharedPreferences settings = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("musicMuted", paused.musicMuted);
                editor.apply();
            }
            if (paused.sfxSoundClicked(touchX, touchY)) {
                paused.sfxMuted = !paused.sfxMuted;
                this.sfxMuted = paused.sfxMuted;
                touchX = 0;
                touchY = 0;
                gameLoop.pausedDrawRequest = true;
                SharedPreferences settings = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("sfxMuted", paused.sfxMuted);
                editor.apply();
            }
        }
        if (isPaused){
            if (progressBar.levelUpped) {
                if (levelUp.optionOneClicked(touchX, touchY)){
                    resume();
                    isPaused = false;
                    if (damageHeroSelected){ damageHero.checkLevelPerks(player.damageLevel); }
                    if (erinaHeroSelected){ erinaHero.checkLevelPerks(player.damageLevel); }
                    player.damageLevel++;
                    progressBar.levelUpped = false;
                    player.toggleInvincible = true;
                }
                if (levelUp.optionTwoClicked(touchX, touchY)){
                    resume();
                    isPaused = false;
                    if (tankHeroSelected) { tankHero.checkLevelPerks(player.tankLevel); }
                    if (alfredHeroSelected) { alfredHero.checkLevelPerks(player.tankLevel, player, this); }
                    player.tankLevel++;
                    progressBar.levelUpped = false;
                    player.toggleInvincible = true;
                }
                if (levelUp.optionThreeClicked(touchX, touchY)){
                    resume();
                    isPaused = false;
                    if (healerHeroSelected){ healerHero.checkLevelPerks(player.healerLevel); }
                    if (perlaHeroSelected){ perlaHero.checkLevelPerks(player.healerLevel, this); }
                    if (damageHeroSelected){
                        damageHero.arrowDamage = dpsDamage;
                        damageHero.arrowReloadTime = dpsReloadTime;
                    }
                    if (erinaHeroSelected){
                        erinaHero.bulletDamage = dpsDamage;
                        erinaHero.bulletCD = dpsReloadTime;
                    }
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
                        ((GameActivity)getContext()).returnToSelectionScreen();
                    }
                };
                mainHandler.post(myRunnable);
            }
        }
        if (paused.quitButtonClicked(touchX, touchY) && isPausedButton){
            isPausedButton = false;
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

    public void revivePlayer(){
        player.setHealthPoint(player.MAX_HEALTH_POINTS);
    }

    public void checkStopped(){
        if (player.getHealthPoint() <= 0) {
            gameLoop.pauseLoop();
            SharedPreferences prefs = getContext().getSharedPreferences("highScores", Context.MODE_PRIVATE);
            SharedPreferences characters = getContext().getSharedPreferences("characters", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            SharedPreferences.Editor charEditor = characters.edit();

            int highestMin = prefs.getInt("highScoreMin", 0);
            int highestSec = prefs.getInt("highScoreSec", 0);
            if (timeAlive.minutes*60 + timeAlive.finalSeconds > highestMin*60 + highestSec){
                editor.putInt("highScoreMin", timeAlive.minutes);
                editor.putInt("highScoreSec", timeAlive.finalSeconds);
                PlayGames.getLeaderboardsClient(gameActivity)
                        .submitScore(getResources().getString(R.string.leaderboard_id), (timeAlive.minutes*60 + timeAlive.finalSeconds)*1000);
                if (timeAlive.minutes >= 7){
                    charEditor.putBoolean("perla", true);
                }
            }

            int totalEnemies = prefs.getInt("totalEnemiesDefeated", 0);
            editor.putInt("totalEnemiesDefeated", totalEnemies+totalEnemiesDefeated);

            int highestDPS = prefs.getInt("highestDPSLevel", 0);
            if (player.damageLevel > highestDPS) {
                editor.putInt("highestDPSLevel", player.damageLevel);
            }

            int highestHealer = prefs.getInt("highestHealerLevel", 0);
            if (player.healerLevel > highestHealer) {
                editor.putInt("highestHealerLevel", player.healerLevel);
            }

            int highestTank = prefs.getInt("highestTankLevel", 0);
            if (player.tankLevel > highestTank) {
                editor.putInt("highestTankLevel", player.tankLevel);
            }

            int totalCoins = prefs.getInt("totalCoinsCollected", 0);
            editor.putInt("totalCoinsCollected", totalCoins+totalCoinsCollected);

            int totalEXP = prefs.getInt("totalEXPGained", 0);
            editor.putInt("totalEXPGained", totalEXP+totalEXPGained);

            int highestCombined = prefs.getInt("highestCombinedLevel", 0);
            if (player.damageLevel+player.healerLevel+player.tankLevel > highestCombined){
                editor.putInt("highestCombinedLevel", player.damageLevel+player.healerLevel+player.tankLevel);
            }
            editor.apply();

            if (prefs.getInt("totalEXPGained", 0) >= 500){
                charEditor.putBoolean("alfred", true);
            }
            if (prefs.getInt("totalEnemiesDefeated", 0) >= 15000){
                charEditor.putBoolean("erina", true);
            }
            charEditor.apply();
        }
        if (isPausedButton) {
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
