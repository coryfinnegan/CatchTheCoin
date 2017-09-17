package com.example.cory.catchthecoin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Cory on 4/24/2016.
 */
public class Player {
    float playerSpeed = 0.2f;
    Bitmap player;
    Bitmap player_left;
    Bitmap player_right;
    RectF playerRectDest;
    float playerXpos;
    Bitmap dead;
    boolean amAlive = true;

    public Player(Context context){
        player = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_right, null);
        player_left = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_left, null);
        player_right = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_right, null);
        playerRectDest = new RectF();
        dead = BitmapFactory.decodeResource(context.getResources(), R.drawable.dead, null);
        playerXpos = 0.5f;

    }

    public void setRect(int w, int h, float groundHeight){
        playerRectDest.set(playerXpos * w, h - player.getHeight() - groundHeight, playerXpos * w + player.getWidth(), h - groundHeight);
    }
    public void setXPos(float gravity, float deltaTime) {
        if (gravity > 0 && playerXpos > 0) {
            player = player_left;
            playerXpos += gravity * -playerSpeed * deltaTime;

        }//end if statement
        //right
        if (gravity < 0 && playerXpos < 0.9f) {
            player = player_right;
            playerXpos += gravity * -playerSpeed * deltaTime;

            Log.d("PLAYERXPOS", Float.toString(playerXpos));
        }//end if statement
        if (playerXpos > 1) {
            playerXpos = 0.9f;
        }
    }
    public Bitmap getBitmap(){
        return player;
    }
    public RectF getRect(){
        return playerRectDest;
    }
    public float getX(){
        return playerXpos;
    }
    public void setDead(){
        amAlive = false;
        player = dead;
    }
    public void setAlive(){
        amAlive = true;
        player = player_left;
    }


}
