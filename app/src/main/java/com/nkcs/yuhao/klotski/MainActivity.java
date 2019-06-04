package com.nkcs.yuhao.klotski;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eminayar.panter.PanterDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.idescout.sql.SqlScoutServer;

import java.util.List;


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
        intent.putExtra("activityName","MainActivity");
        intent.putExtra("levelId",1);
        intent.putExtra("levelTitle","横刀立马");
        this.startActivity(intent);
    }

    // 进入选择关卡
    public void chooseLevel(View view){
        Intent intent = new Intent(this,ChooseLevel.class);
        this.startActivity(intent);
    }

    // 读取存档
    public void loadHistory(View view) {
        Intent intent = new Intent(this,ChooseHistory.class);
        this.startActivity(intent);
    }


    public void aboutGame(View view) {
        new PanterDialog(this)
                .setHeaderBackground(R.drawable.pattern_bg_blue)
                .setTitle("关于游戏")
                .setPositive("朕了解了")
                .setMessage(R.string.about_game)
                .isCancelable(false)
                .show();
    }

    public void exitGame(View view) {
        new PanterDialog(this)
                .setHeaderBackground(R.drawable.pattern_bg_blue)
                .setTitle("退出游戏")
                .setPositive("去意已决", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 结束主界面activity
                       // finish();
                        // 彻底关闭app
                        exitAPP();
                    }
                })
                .setNegative("我再想想")
                .setMessage(R.string.exit_game)
                .isCancelable(false)
                .show();
    }

    // 退出程序
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void exitAPP() {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
        for (ActivityManager.AppTask appTask : appTaskList) {
            appTask.finishAndRemoveTask();
        }
        System.exit(0);
    }

}
