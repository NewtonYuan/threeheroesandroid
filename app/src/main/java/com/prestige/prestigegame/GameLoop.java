package com.prestige.prestigegame;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameLoop extends Thread{
    public static final double MAX_UPS = 120.0;
    private static final double UPS_PERIOD = 1E+3/MAX_UPS;

    private Game game;
    private SurfaceHolder surfaceHolder;

    private boolean isRunning = false;
    public boolean isPaused = false;
    public boolean firstPause = false;

    public GameLoop(Game game, SurfaceHolder surfaceHolder) {
        this.game = game;
        this.surfaceHolder = surfaceHolder;
    }

    public void startLoop() {
        if (!isRunning){
            Log.d("GameLoop.java", "startLoop()");
            isRunning = true;
            start();
        }
    }

    @Override
    public void run() {
        Log.d("GameLoop.java", "run()");
        super.run();

        // Declare time and cycle count variables
        int updateCount = 0;
        int frameCount = 0;

        long startTime;
        long elapsedTime;
        long sleepTime;

        // Game loop
        Canvas canvas = null;
        startTime = System.currentTimeMillis();
        while(isRunning) {
            if (!isPaused) {
                // Try to update and render game
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        game.update();
                        updateCount++;

                        game.draw(canvas);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } finally {
                    if(canvas != null) {
                        try {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                            frameCount++;
                            game.checkStopped();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Pause game loop to not exceed target UPS
                elapsedTime = System.currentTimeMillis() - startTime;
                sleepTime = (long) (updateCount*UPS_PERIOD - elapsedTime);
                if(sleepTime > 0) {
                    try {
                        sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Skip frames to keep up with target UPS
                while(sleepTime < 0 && updateCount < MAX_UPS-1) {
                    game.update();
                    updateCount++;
                    elapsedTime = System.currentTimeMillis() - startTime;
                    sleepTime = (long) (updateCount*UPS_PERIOD - elapsedTime);
                }

                // Calculate average UPS and FPS
                elapsedTime = System.currentTimeMillis() - startTime;
                if(elapsedTime >= 1000) {
                    updateCount = 0;
                    frameCount = 0;
                    startTime = System.currentTimeMillis();
                }
            } if (isPaused) {
                game.checkResume();
            }
        }
    }

    public void stopLoop() {
        Log.d("GameLoop.java", "stopLoop()");
        isRunning = false;
        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pauseLoop(){
        isPaused = true;
    }

    public void resumeLoop(){
        isPaused = false;
        Log.i("Paused", "Yes");
    }

    public void restartLoop(){
        stopLoop();
        startLoop();
    }
}