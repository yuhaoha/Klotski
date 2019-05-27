package com.nkcs.yuhao.klotski;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.idescout.sql.SqlScoutServer;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {
    static String LevelExtra = "Level";
    static MainActivity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SqlScoutServer.create(this, getPackageName());
        setContentView(R.layout.activity_main);
        context = this;
    }

    public static MainActivity getActivity()
    {
        return context;
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

    public void loadHistory(View view) {

    }
}
