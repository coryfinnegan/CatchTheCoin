package com.example.cory.catchthecoin;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;

public class AnimateSprite {

    private Bitmap mAnimation;
    private int mXPos;
    private int mYPos;
    private Rect mSRectangle;
    private int mFPS;
    private int mXNoOfFrames;
    private int mYNoOfFrames;
    private int mCurrentFrame;
    private long mFrameTimer;
    private int mSpriteHeight;
    private int mSpriteWidth;
    Rect dest;
    Matrix tiltM;

    public AnimateSprite() {
        mSRectangle = new Rect();


        dest = new Rect();
        tiltM = new Matrix();

        mFrameTimer = 0;
        mCurrentFrame = 0;
        setmXPos(80);
        setmYPos(80);
    }

    public void Initalise(Bitmap theBitmap, int Height, int Width, int theFPS, int xcount, int ycount) {
        mAnimation = theBitmap;
        mXNoOfFrames = xcount;
        mYNoOfFrames = ycount;
        if (mXNoOfFrames == 0) mXNoOfFrames = 1;
        if (mYNoOfFrames == 0) mYNoOfFrames = 1;
        mSpriteHeight = Height / mYNoOfFrames;
        mSpriteWidth = Width / mXNoOfFrames;
        mSRectangle.set(0, 0, mSpriteWidth,  mSpriteHeight);
        mFPS = 1000 /theFPS;
    }

    public void Update(long GameTime) {
        if(GameTime > mFrameTimer + mFPS ) {
            mFrameTimer = GameTime;
            mCurrentFrame +=1;
            tiltM.preRotate(30);

            if(mCurrentFrame >= mXNoOfFrames * mYNoOfFrames) {
                mCurrentFrame = 0;
            }

            mSRectangle.left = (mCurrentFrame % mXNoOfFrames)* mSpriteWidth;
            mSRectangle.right = mSRectangle.left + mSpriteWidth;
            mSRectangle.top = (mCurrentFrame % mYNoOfFrames)* mSpriteHeight;
            mSRectangle.bottom = mSRectangle.top + mSpriteHeight;
        }

    }

    public void draw(Canvas canvas) {

        // last updated location
        dest.set(getmXPos() - mSpriteWidth/2,
                getmYPos() - mSpriteHeight/2,
                getmXPos() + mSpriteWidth/2,
                getmYPos() + mSpriteHeight/2);
        // applies matrix tiltM to get rotated sprite image
        Log.d("AS", String.format("%d %d %d %d", mSRectangle.left,mSRectangle.top, mSpriteWidth, mSpriteHeight));
        Bitmap tiltB = Bitmap.createBitmap(mAnimation,
                mSRectangle.left,mSRectangle.top, mSpriteWidth, mSpriteHeight,
                tiltM, false);
        canvas.drawBitmap(tiltB, null, dest, null);
        // clean up
        tiltB.recycle();

    }

    int getmXPos() {
        return mXPos;
    }

    void setmXPos(int mXPos) {
        this.mXPos = mXPos;
    }

    int getmYPos() {
        return mYPos;
    }

    void setmYPos(int mYPos) {
        this.mYPos = mYPos;
    }

}
