package com.example.cory.catchthecoin;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    GameThreadView gameView;
    SoundPool soundPool;
    int coinSound = -1;
    int deathSound = -1;
    int shootSound = -1;
    int explosionSound = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameThreadView(this);
        setContentView(gameView);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


    }

    protected void onResume() {
        super.onResume();
        gameView.resume();
        soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);

        try {
            AssetManager assetManager = getAssets(); // if called in Activity
            AssetFileDescriptor descriptor = assetManager.openFd("coin.wav");
            coinSound = soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("death.wav");
            deathSound = soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("shot.wav");
            shootSound = soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("explosion.wav");
            explosionSound = soundPool.load(descriptor, 1);
        } catch (IOException e) {
            Log.w("TAG", "Can't find sound file");
        }
    }

    protected void onPause() {

        soundPool.unload(coinSound);
        soundPool.unload(deathSound);
        soundPool.unload(shootSound);
        soundPool.unload(explosionSound);
        soundPool.release();

        super.onPause();

        gameView.pause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

}
