package com.example.tattata.fukaido;

import android.widget.TextView;

/**
 * Created by tattata on 2017/08/03.
 */

public class Enemy {
    public static final int MOVE = 20;
    private TextView tv;
    private int moveX;
    private int moveY;
    public Enemy(TextView tv) {
        this.tv = tv;
        moveX = MOVE * plusOrMinus();
        moveY = MOVE * plusOrMinus();
    }
    public Enemy(TextView tv, float x, float y) {
        this.tv = tv;
        tv.setText("敵");
        tv.setTextSize(28);
        moveX = (int)(MOVE * plusOrMinus() * Math.random());
        moveY = (int)(MOVE * plusOrMinus() * Math.random());
        setX(x);
        setY(y);
    }
    public int plusOrMinus() {
        if(Math.random() > 0.5) {
            return 1;
        } else {
            return -1;
        }
    }


    public int getMoveX() {
        return moveX;
    }

    public int getMoveY() {
        return moveY;
    }

    public void setMoveX(int moveX) {
        this.moveX = moveX;
    }

    public void setMoveY(int moveY) {
        this.moveY = moveY;
    }
    public float getX() {
        return tv.getX();
    }
    public  float getY() {
        return tv.getY();
    }
    public void setX(float x) {
        tv.setX(x);
    }
    public void setY(float y) {
        tv.setY(y);
    }
    public float getHeight() {
        return tv.getHeight();
    }
    public float getWidth() {
        return tv.getWidth();
    }
    public void returnMoveX() {
        moveX *= -1;
    }
    public void returnMoveY() {
        moveY *= -1;
    }

}
