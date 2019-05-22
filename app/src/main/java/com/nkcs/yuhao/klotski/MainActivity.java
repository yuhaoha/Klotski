package com.nkcs.yuhao.klotski;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    static String LevelExtra = "Level";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void enterGame(View view) {
        // 进入游戏界面
        Intent intent = new Intent(this,PlayGame.class);
        intent.putExtra(LevelExtra,1);
        this.startActivity(intent);
    }

    public void chooseLevel(View view){
        // 进入选择关卡
        Intent intent = new Intent(this,ChooseLevel.class);
        this.startActivity(intent);
    }

}
