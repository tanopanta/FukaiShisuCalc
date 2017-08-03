package com.example.tattata.fukaido;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private static final int TYO = 40;

    ImageView playerImage;
    TextView enemy1;
    TextView timeView;
    ConstraintLayout layout;
    Button modoruButton;

    int sceneTop;
    int sceneX;
    int sceneY;
    double result;
    Handler handler;
    Runnable r;
    List<Enemy> enemyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        enemyList = new ArrayList<>();

        Display display = this.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        sceneX = point.x;
        sceneY = point.y;

        timeView = (TextView)findViewById(R.id.timeView);
        playerImage = (ImageView)findViewById(R.id.playerImage);
        enemy1 = (TextView)findViewById(R.id.enemy1);
        layout = (ConstraintLayout)findViewById(R.id.layout);
        modoruButton = (Button)findViewById(R.id.modoruButton);

        modoruButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("RESULT", result);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        enemyList.add(new Enemy(enemy1));

        handler = new Handler();
        r = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                // UIスレッド
                timeView.setText(String.format("%.1f", count / 12.5));
                if(collisionDetect()) {
                    endProcessing(count);
                    return;
                }
                moveEnemy();
                count++;
                if(count % 14 == 0) {
                    emergeEnemy();
                }
                handler.removeCallbacks(this);
                handler.postDelayed(this, 80);
            }
        };
        handler.post(r);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //Y座標の調整
        Rect rect = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top; // ステータスバーの高さ
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        sceneTop = statusBarHeight + contentViewTop;
        sceneY = sceneY - sceneTop;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String action = "";
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                setImageX(event.getX());
                setImageY(event.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                break;
        }

        /*
        Log.v("MotionEvent",
                "action = " + action + ", " +
                        "x = " + String.valueOf(event.getX()) + ", " +
                        "y = " + String.valueOf(event.getY()));
                        */
        return super.onTouchEvent(event);
    }
    public void setImageX(float x) {
        playerImage.setX(x - playerImage.getWidth() / 2);
    }
    public void setImageY(float y) {
        playerImage.setY(y - sceneTop - playerImage.getHeight() / 2);
    }
    public boolean collisionDetect() {
        float px = playerImage.getX();
        float py = playerImage.getY();
        int i = 0;
        for(Enemy e:enemyList) {
            float ey = e.getY();
            float ex = e.getX();
            if(ex < px + TYO && ex + e.getWidth()  > px + TYO
                    || ex  < px + playerImage.getWidth()  - TYO && ex + e.getWidth()  > px + playerImage.getWidth() - TYO ) {
                if(ey  < py + TYO && ey + e.getHeight()  > py + TYO
                        || ey  < py + playerImage.getHeight() - TYO && ey + e.getHeight()  > py + playerImage.getHeight() - TYO ) {
                    return true;
                }
            }
            i++;
        }
        return false;
    }
    public void emergeEnemy() {
        TextView tv = new TextView(this);
        Enemy e = new Enemy(tv, enemyList.get(0).getX(), enemyList.get(0).getY());
        enemyList.add(e);
        layout.addView(tv);
    }
    public void moveEnemy() {
        int i = 0;
        for(Enemy e:enemyList) {
            e.setX(e.getX() + e.getMoveX());
            e.setY(e.getY() + e.getMoveY());
            if(e.getX() <= 0 || e.getX() + e.getWidth() > sceneX) {
                e.returnMoveX();
            }
            if(e.getY() <= 0 || e.getY() + e.getHeight() > sceneY) {
                e.returnMoveY();
            }
            i++;
        }
    }
    public void endProcessing(int count) {
        Toast.makeText(getApplicationContext(), "GAME OVER", Toast.LENGTH_LONG).show();
        this.result = 100 - count / 12.5;
        modoruButton.setVisibility(View.VISIBLE);
    }
}
