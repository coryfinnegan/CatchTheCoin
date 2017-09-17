package com.example.cory.catchthecoin;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


/**
 * Created by Cory on 4/12/2016.
 */
class GameThreadView extends SurfaceView implements Runnable, SensorEventListener {
    Thread renderThread = null;
    volatile boolean running = false;
    SurfaceHolder holder;
    MainActivity gta;
    Paint bg;
    Paint fg;

    //Bitmaps
    Bitmap ground;
    Rect groundDest;

    //Prefs
    Boolean audio;
    String difficulty;

    //Sensors
    private SensorManager mSensorManager;
    private Sensor mSensor;
    double[] gravity = new double[3];

    //Game Variables go here
    int w;
    int h;
    ArrayList<Boulder> boulders;
    ArrayList<Coin> coins;
    Player player;
    ArrayList<Player>players;
    ArrayList<Shot>shots;
    ArrayList<Boulder> expiredBoulders;
    ArrayList<Shot> expiredShots;

    int boulderSpawnSpeed = 75;
    int coinSpawnSpeed = 100;

    boolean endGame = false;
    int score = 0;
    boolean checkCollision = false;

    Boolean makeShotsBool;


    public GameThreadView(Context context) {
        super(context);
        gta = (MainActivity)context;
        holder = getHolder();
        fg = new Paint(Paint.ANTI_ALIAS_FLAG);
        fg.setTextSize(36);
        fg.setTextAlign(Paint.Align.CENTER);
        fg.setColor(getResources().getColor(R.color.text));
        bg = new Paint();
        bg.setColor(getResources().getColor(R.color.background));
        Log.d("GAME", "GameThreadView");
        boulders = new ArrayList<Boulder>();
        coins = new ArrayList<Coin>();
        players = new ArrayList<>();
        shots = new ArrayList<>();
        expiredBoulders = new ArrayList<>();
        expiredShots = new ArrayList<>();
        makeShotsBool = false;


        //Set Prefs
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(gta);
        audio = prefs.getBoolean("key_audio", false);//Set Audio Choice
        difficulty = prefs.getString("key_diff", "e");//Set diff choice
        setDifficulty(difficulty);

        //Initialize bitmap for ground
        ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground, null);
        groundDest = new Rect();

        //Make the player
        makePlayer();

        //Initialize Sensor
        mSensorManager = (SensorManager)gta.getSystemService(gta.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, 100000);



    }//End Constructor()

    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }//end resume()

    public void pause() {
        running = false;

        while(true) {
            try {
                renderThread.join();
                break;
            } catch (InterruptedException e) {
                // retry
            }
        }
        renderThread = null;
    }//end Pause()
    public void stop(){
        mSensorManager.unregisterListener(this);
    }

    public void run() {
        long startTime = System.nanoTime();

        while(running) {
            if(!holder.getSurface().isValid())
                continue;

            float deltaTime = (System.nanoTime()-startTime)/1000000000.0f;
            startTime = System.nanoTime();
            updateGame(deltaTime);

            Canvas canvas = holder.lockCanvas();
            drawSurface(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }//end run()

    private void updateGame(float deltaTime) {
        //tiltCharacter(deltaTime);
        if (!endGame){
            makeBoulders(deltaTime);
            makeCoins(deltaTime);

            for (Player d : players){
                d.setXPos((float) gravity[0], deltaTime);
            }

            detectCollision();
            killExpiredObject();
        }


        detectCollision();
    }//end updateGame

    private void drawSurface(Canvas canvas) {
        canvas.drawPaint(bg);
        fg.setTextSize(80f);

        w = canvas.getWidth();
        h = canvas.getHeight();

        groundDest.set(0, h - ground.getHeight(), w, h);
        canvas.drawBitmap(ground, null, groundDest, null);
        //draw the Score

      //Draw the boulders coins and player
        for (Player d : players){
            d.setRect(w, h, ground.getHeight());
            canvas.drawBitmap(d.getBitmap(), null, d.getRect(), null);
        }
        for (Boulder p  : boulders){
            p.setRect(w, h);
            canvas.drawBitmap(p.getBitmap(), null, p.getRect(), null);

        }

        for (Coin c : coins){
            c.setRect(w, h);
            canvas.drawBitmap(c.getBitmap(), null, c.getRect(), null);
        }

        for (Shot shot : shots){
            shot.setRect(w, h);
            canvas.drawBitmap(shot.getBitmap(), null, shot.getRect(), null);
        }

        //Iter refactor
        /*
        for (Iterator<Boulder> boulderIter = boulders.iterator(); boulderIter.hasNext();) {
            Boulder currentBoulder = boulderIter.next();
            currentBoulder.setRect(w, h);
            canvas.drawBitmap(currentBoulder.getBitmap(), null, currentBoulder.getRect(), null);
        }
        for (Iterator<Coin> coinIter = coins.iterator(); coinIter.hasNext();) {
            Coin coin = coinIter.next();
            coin.setRect(w, h);
            canvas.drawBitmap(coin.getBitmap(), null, coin.getRect(), null);
        }
        for (Iterator<Shot> shotIter = shots.iterator(); shotIter.hasNext();) {
            Shot shot = shotIter.next();
            shot.setRect(w, h);
            canvas.drawBitmap(shot.getBitmap(), null, shot.getRect(), null);
        }
        */


        canvas.drawText("Score: " + Integer.toString(score), w / 2, h * 0.1f, fg);
        if (endGame){
            canvas.drawText("Tap to Restart", w/2, h/2, fg);
        }
        //Game Draw Code goes here

    }//end drawSurface()

    public boolean onTouchEvent(MotionEvent event) {
        synchronized (this) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    makeShotsBool = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                   if (endGame){
                       endGame = false;
                       score  = 0;
                   }
                    if (!endGame){
                        if (!makeShotsBool) {
                            makeShot();
                            makeShotsBool = true;
                        }

                    }
                    break;
            }//end switch
            return true;
        }//end synchronized
    }//end onTouchEvent()

    public void onSensorChanged(SensorEvent event){
        final float alpha = 0.8f;
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
    }
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }//end onAccuracyChanged

    public void playSound(int soundValue){

        //turn on when audio preference is set
        if (audio == true){
            gta.soundPool.play(soundValue, 1, 1, 0, 0, 1);
        }
        else if(audio == false){
            //Do nothing
        }
    }//end playSound()


    public void makeBoulders(float deltaTime){
        Random random = new Random();
        int randomInt =  random.nextInt(boulderSpawnSpeed);
        if (randomInt == 1){
            float spawnXPos = random.nextFloat();
            Log.d("SPAWNXPOS_RANDOMFLOAT", Float.toString(spawnXPos));
            Boulder boulder = new Boulder(gta, spawnXPos, -1, h, w);
            boulders.add(boulder);
        }//end If

        for (Iterator<Boulder> boulderIter = boulders.iterator(); boulderIter.hasNext();) {
            Boulder currentBoulder = boulderIter.next();
            if (currentBoulder.getYpos() > h + 30) {
                boulderIter.remove();
            }
        }
    }//end makeBoulders

    public void makeCoins(float deltaTime){
        Random random = new Random();
        int randomInt =  random.nextInt(coinSpawnSpeed);
        if (randomInt == 1){
            float spawnXPos = random.nextFloat();
            Coin coin = new Coin(gta, spawnXPos, -1, h, w);
            coins.add(coin);
        }//end If

        //Destroy if they get too far south
        for (Iterator<Coin> coinIter = coins.iterator(); coinIter.hasNext();) {
            Coin currentCoin = coinIter.next();
            if (currentCoin.getYpos() > h+20) {
                coinIter.remove();
            }
        }


    }//end makeCoins

    public void makePlayer(){
        player = new Player(gta);
        players.add(player);
    }

    public void detectCollision(){
        for (Iterator<Boulder> boulderIter = boulders.iterator(); boulderIter.hasNext();){
            Boulder currentBoulder = boulderIter.next();
            for (Iterator<Player>playerIter = players.iterator(); playerIter.hasNext();){
                Player currentPlayer = playerIter.next();
                if (currentPlayer.getRect().intersects(currentPlayer.getRect(), currentBoulder.getRect())) {
                    if (endGame == false){
                        endGame(currentPlayer);
                    }
                    Log.d("PLAYERDEAD", "Player is dead");

                }

            }
        }
        for (Player d : players){
            for (Iterator<Coin> c = coins.iterator(); c.hasNext();) {
                Coin currentCoin = c.next();
                if (d.getRect().intersects(d.getRect(), currentCoin.getRect())){
                    c.remove();
                    score += 10;
                    playSound(gta.coinSound);
                    Log.d("COIN", "coin is hit");
                }
            }
        }
        /*
        for (Iterator<Boulder> boulderIter = boulders.iterator(); boulderIter.hasNext();) {
            Boulder boulder = boulderIter.next();
            for (Iterator<Shot>shotIter = shots.iterator(); shotIter.hasNext();){
                Shot shot = shotIter.next();
                if (shot.getRect().intersects(shot.getRect(), boulder.getRect())){
                    boulderIter.remove();
                    playSound(gta.explosionSound);
                    break;
                }
            }
        }
        */
        for (Boulder boulder : boulders){
            for (Shot shot : shots){
                if (shot.getRect().intersects(shot.getRect(), boulder.getRect())){
                    expiredBoulders.add(boulder);
                    expiredShots.add(shot);
                    playSound(gta.explosionSound);
                    score += 10;
                }
            }
        }

    }
    public void killExpiredObject(){
        for (Boulder boulder : expiredBoulders){
            boulders.remove(boulder);
        }
        for (Shot shot: expiredShots){
            shots.remove(shot);
        }
    }
    public void endGame(Player player){
        endGame = true;
        playSound(gta.deathSound);
        player.setDead();
    }
    public void makeShot(){
        Shot shot = new Shot(gta, player.getX(), player.getRect().top, h, w);
        shots.add(shot);
        playSound(gta.shootSound);
    }
    public void setDifficulty(String keyValue){
        if (keyValue.equals("e")){
            boulderSpawnSpeed = 100;
            Log.d("PADDLE", "Easy Set");
        }
        if (keyValue.equals("m")){
            Log.d("PADDLE", "Medium Set");
            boulderSpawnSpeed = 50;
        }
        if (keyValue.equals("h")){
            Log.d("PADDLE", "Hard Set");
            boulderSpawnSpeed = 25;
        }
    }

}//end GameThreadView
