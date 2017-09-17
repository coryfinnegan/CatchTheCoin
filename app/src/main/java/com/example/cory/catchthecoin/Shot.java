package com.example.cory.catchthecoin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

/**
 * Created by Cory on 4/30/2016.
 */
public class Shot {
    float speed = 6f;
    Bitmap shot;
    float yPos;
    float xPos;
    float spawnPoint;
    RectF rect;



    public Shot(Context context, float xPos, float yPos, int h, int w){
        this.xPos = xPos;
        this.yPos = yPos;
        shot = BitmapFactory.decodeResource(context.getResources(), R.drawable.shoot, null);
        rect = new RectF(xPos * w, yPos, (xPos *  w) + shot.getWidth(), yPos + shot.getHeight());
    }
    public RectF getRect(){
        return rect;
    }
    public Bitmap getBitmap(){
        return shot;
    }
    public void setRect(int w, int h){
        rect.set(xPos * w, yPos, (xPos *  w) + shot.getWidth(), yPos + shot.getHeight());
        yPos -= speed;
    }
}//end Shot
