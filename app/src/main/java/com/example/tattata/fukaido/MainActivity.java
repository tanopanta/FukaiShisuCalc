package com.example.tattata.fukaido;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView textResult;
    EditText editKion;
    EditText editShitsudo;
    TableRow row1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        textResult = (TextView) findViewById(R.id.textResult);
        editKion = (EditText) findViewById(R.id.editKion);
        editShitsudo = (EditText) findViewById(R.id.editShitsudo);
        row1 = (TableRow) findViewById(R.id.row1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double kion = 0.0;
                double shitsudo = 0.0;
                textResult.setText("");
                //計算
                try {
                    kion = Double.parseDouble(editKion.getText().toString());
                    shitsudo = Double.parseDouble(editShitsudo.getText().toString());
                } catch(NumberFormatException nfe) {
                    Toast.makeText(getApplicationContext(), "気温と湿度を入力してください。", Toast.LENGTH_SHORT).show();
                    return;
                }
                double shisu = 0.81 * kion + 0.01 * shitsudo * (0.99 * kion - 14.3) + 46.3;
                shisu = Math.round(shisu * 10) / 10.0;//こうしておかないと後の比較の部分で困る。
                textResult.setText(String.format("不快指数は %.1f です。", shisu));
                //色変更
                for(int i = 0; i < row1.getChildCount(); i++) {
                    row1.getChildAt(i).setBackgroundColor(Color.WHITE);
                }
                if (shisu < 70.0) {
                    row1.getChildAt(0).setBackgroundColor(Color.rgb(50, 255, 220));
                } else if (shisu < 75.0) {
                    row1.getChildAt(1).setBackgroundColor(Color.rgb(81, 255, 50));
                } else if (shisu < 80.0) {
                    row1.getChildAt(2).setBackgroundColor(Color.rgb(248, 255, 50));
                } else if (shisu < 85.0) {
                    row1.getChildAt(3).setBackgroundColor(Color.rgb(255, 139, 50));
                } else {
                    row1.getChildAt(4).setBackgroundColor(Color.rgb(255, 57, 50));
                }
            }
        });
    }
}
