package com.example.cory.catchthecoin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Cory on 4/23/2016.
 */
public class Boulder {
    float spawnPoint;
    Bitmap boulderBitmap;
    //AnimateSprite boulderAnim;
    public RectF boulderRectDest;
    Rect boulderRectSource;
    float speed = 6f;
    GameThreadView gta;
    public float xPosBoulder;
    public float yPosBoulder;


    public Boulder(Context context, float xPos, float yPos, int h, int w) {
        //boulderAnim = new AnimateSprite();
        xPosBoulder = xPos;
        yPosBoulder = yPos;
        //yPosBoulder += deltaTime * speed;
        //BitmapFactory.Options myOpts = new BitmapFactory.Options();
        boulderBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.rock, null);
        //boulderAnim.Initalise(boulderBitmap, boulderBitmap.getHeight(), boulderBitmap.getWidth(), 12, 5, 5);
        boulderRectDest = new RectF(xPosBoulder * w, yPosBoulder, (xPosBoulder * w) + (boulderBitmap.getWidth()), yPosBoulder + boulderBitmap.getHeight());
        Log.d("SPAWNLOCATION", Float.toString(spawnPoint));
        Log.d("yPosBoulder", Float.toString(yPosBoulder));



    }//End Constructor
    public void fall(float deltaTime){


    }
    public RectF getRect(){
        return boulderRectDest;
    }
    public Bitmap getBitmap(){
        return boulderBitmap;
    }
    public void setRect(int w, int h){
        boulderRectDest.set(xPosBoulder * w, yPosBoulder, (xPosBoulder * w) + (boulderBitmap.getWidth()), yPosBoulder + boulderBitmap.getHeight());
        yPosBoulder += speed;

    }
    public void printStuff(){
        Log.d("BoulderObjYPOS", Float.toString(yPosBoulder));
        Log.d("BoulderObjXPOS", Float.toString(spawnPoint));

    }
    public float getYpos(){
        return yPosBoulder;
    }

}//end boulder

