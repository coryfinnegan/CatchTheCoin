package com.example.cory.catchthecoin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Log;


public class Coin {
    float spawnPoint;
    Bitmap coinBitmap;
    public RectF boulderRectDest;
    float speed = 6f;
    public float xPosBoulder;
    public float yPosBoulder;

    public Coin(Context context, float xPos, float yPos, int h, int w) {
        xPosBoulder = xPos;
        yPosBoulder = yPos;
        coinBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.coin, null);
        boulderRectDest = new RectF(xPosBoulder * w, yPosBoulder, (xPosBoulder * w) + (coinBitmap.getWidth()), yPosBoulder + coinBitmap.getHeight());
        Log.d("SPAWNLOCATION", Float.toString(spawnPoint));
        Log.d("COINYPOS", Float.toString(yPosBoulder));
    }//End Constructor

    public RectF getRect(){
        return boulderRectDest;
    }
    public Bitmap getBitmap(){
        return coinBitmap;
    }
    public void setRect(int w, int h){
        boulderRectDest.set(xPosBoulder * w, yPosBoulder, (xPosBoulder * w) + (coinBitmap.getWidth()), yPosBoulder + coinBitmap.getHeight());
        yPosBoulder += speed;
    }

    public void printStuff(){
        Log.d("BoulderObjYPOS", Float.toString(yPosBoulder));
        Log.d("BoulderObjXPOS", Float.toString(spawnPoint));
    }
    public float getYpos(){
        return yPosBoulder;
    }

}//end coin

