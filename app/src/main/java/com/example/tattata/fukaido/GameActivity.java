package com.example.tattata.fukaido;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import java.util.LinkedList;
import java.util.List;
/*
Intentの参考https://akira-watson.com/android/activity-2.html
 */
public class GameActivity extends AppCompatActivity {
    private static final int TYO = 40;

    ImageView playerImage;
    TextView enemy1;
    TextView timeView;
    ConstraintLayout layout;
    Button modoruButton;
    Button rankButton;

    SharedPreferences pref;
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
        rankButton = (Button)findViewById(R.id.rankButton);


        pref = getSharedPreferences("gamePref", MODE_PRIVATE);

        modoruButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("RESULT", 100 - result);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        rankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(GameActivity.this);
                dialog.setTitle("ハイスコアランキング");
                LinkedList<Float> list = new LinkedList<>();
                for(int i = 0; i < 5; i++) {
                    list.add(pref.getFloat("rank" + String.valueOf(i + 1), 0.0f));
                }
                TextView tv = new TextView(getApplicationContext());
                int i = 1;
                String msg = "";
                for(Float f: list) {
                    msg += String.format("%d位           %.1f\n", i, f);
                    i++;
                }
                tv.setText(msg);
                tv.setTextSize(24f);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                dialog.setView(tv);
                //dialog.setMessage(msg);
                dialog.setPositiveButton("OK", null);
                dialog.create().show();
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
                    e.setColor(Color.YELLOW);
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
        this.result = count / 12.5;
        playerImage.setRotation(-90.0f);
        updateRanking();
        modoruButton.setVisibility(View.VISIBLE);
        rankButton.setVisibility(View.VISIBLE);
    }
    public void updateRanking() {
        LinkedList<Float> list = new LinkedList<>();
        for(int i = 0; i < 5; i++) {
           list.add(pref.getFloat("rank" + String.valueOf(i + 1), 0.0f));
        }

        int i = 0;
        for(Float f:list) {
            if(f <= this.result) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putFloat("rank" + String.valueOf(i), (float)this.result);
                Toast.makeText(getApplicationContext(), "ハイスコア！！" + String.valueOf(i+1) + "位にランクイン！！", Toast.LENGTH_SHORT).show();

                list.add(i, (float)this.result);
                i = 0;
                for(Float ff:list) {
                    editor.putFloat("rank" + String.valueOf(i + 1), ff);
                    i++;
                }
                editor.commit();
                return;
            }
            i++;
        }
        Toast.makeText(getApplicationContext(), "げーむおーばー", Toast.LENGTH_SHORT).show();

    }
    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(r);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        handler.postDelayed(r, 80);
    }
}
