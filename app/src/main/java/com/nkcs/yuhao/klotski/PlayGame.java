package com.nkcs.yuhao.klotski;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eminayar.panter.PanterDialog;

import java.util.List;

import es.dmoral.toasty.Toasty;


public class PlayGame extends AppCompatActivity {
    private static PlayGame context = null; //中介变量
    private
    KlotskiView kv; //获取Klotski组件引用
    static int level;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取关卡数值 最佳成绩 关卡名
        Intent intent = getIntent();
        String activityName = intent.getStringExtra("activityName");
        // 从选择关卡页面进入游戏
        if(activityName.equals("ChooseLevel"))
        {
            level = intent.getIntExtra("levelId",1);
            String levelTitle = intent.getStringExtra("levelTitle");
            // 设置布局，要在设置关卡之后执行
            setContentView(R.layout.activity_play_game);
            context = this;
            kv = findViewById(R.id.gameBoard);
            kv.setLevel(level);
            // 设置关卡名
            TextView titleView = findViewById(R.id.levelTitleInGame);
            titleView.setText(levelTitle);
            // 设置最佳成绩
            setBestScore();
        }
        // 从读取存档页面进入游戏
        else if(activityName.equals("ChooseHistory"))
        {
            level = intent.getIntExtra("levelId",1);
            String levelTitle = intent.getStringExtra("levelTitle");
            int historyId = intent.getIntExtra("historyId",1);
            setContentView(R.layout.activity_play_game);
            context = this;
            kv = findViewById(R.id.gameBoard);
            // 设置传过来的游戏记录id
            kv.loadGameHistory(historyId);
            // 设置关卡名
            TextView titleView = findViewById(R.id.levelTitleInGame);
            titleView.setText(levelTitle);
            // 设置最佳成绩
            setBestScore();
        }
        // 点击游戏成功后再次游戏
        else
        {
            level = intent.getIntExtra("levelId",1);
            String levelTitle = intent.getStringExtra("levelTitle");
            // 设置布局，要在设置关卡之后执行
            setContentView(R.layout.activity_play_game);
            context = this;
            kv = findViewById(R.id.gameBoard);
            kv.setLevel(level);
            // 设置关卡名
            TextView titleView = findViewById(R.id.levelTitleInGame);
            titleView.setText(levelTitle);
            // 设置最佳成绩
            setBestScore();
        }

    }

    // 为游戏界面设置最佳成绩
    private void setBestScore()
    {
        Level myLevel = DatabaseHelper.getLevel(level);
        int bestScore = myLevel.getBestScore();
        TextView myBestScore = findViewById(R.id.bestScoreInGame);
        if(bestScore!=9999)
            myBestScore.setText("最佳成绩:"+bestScore);
    }

    public static Activity getActivity()
    {
        return context;
    }
    public static int getLevel(){return level;}
    // 获取自定义组件的引用
    private void getKlotskiView()
    {
        View view = getLayoutInflater().inflate(R.layout.activity_play_game,
                (ViewGroup) findViewById(R.id.gameBoard));
        kv =  view.findViewById(R.id.gameBoard);
    }

    // 设置关卡值
    private void setGameLevel(int level)
    {
        getKlotskiView();
        if(kv!=null)
        {
            Log.d("hello","设置前的level:"+kv.getLevel());
            kv.setLevel(level);
            Log.d("hello","设置后的level:"+kv.getLevel());
        }
        else
            Log.d("hello","引用为空");
    }

    public  void setMoveTimes(int  moveTimes)
    {
        TextView tv = findViewById(R.id.moveTimes);
        tv.setText("当前步数:"+moveTimes);
    }

    // 点击上一步按钮
    public void lastState(View view) {
        if(kv!=null)
        {
            // 调用KlotskiView的方法，返回上一步
            kv.lastState();
        }
        else
            Log.d("hello","kv==null");
    }

    // 点击重新游戏按钮
    public void replay(View view) {
        new PanterDialog(PlayGame.getActivity())
                .setHeaderBackground(R.drawable.pattern_bg_blue)
                .setTitle("退出游戏")
                .setPositive("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,PlayGame.class);
                        intent.putExtra("activityName","PlayGame");
                        intent.putExtra("levelId",level);
                        intent.putExtra("levelTitle",DatabaseHelper.getLevel(level).getTitle());
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegative("我再想想")
                .setMessage("您的游戏记录可能还未保存,是否确定重新开始?")
                .isCancelable(false)
                .show();
    }

    // 游戏存档
    public void saveGameHistory(View view) {
        if(kv!=null)
        {
            kv.saveGameHistory();
            // 提示存档成功
            Toasty.success(MyApplication.getContext(), "存档成功!", Toast.LENGTH_SHORT, true).show();
        }
        else
            Log.d("hello","kv==null");
    }

    // 进入上一关
    public void enterPreviousLevel(View view) {

        new PanterDialog(this)
                .setHeaderBackground(R.drawable.pattern_bg_blue)
                .setTitle("上一关")
                .setPositive("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 检查是否到了第一关
                        if(level-1<=0)
                        {
                            Intent intent = new Intent(context,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Intent intent = new Intent(context,PlayGame.class);  //跳转到游戏页面
                            Level mylevel = DatabaseHelper.getLevel(level-1);
                            intent.putExtra("activityName","PlayGame");
                            intent.putExtra("levelId",mylevel.getLevelId());
                            intent.putExtra("levelTitle",mylevel.getTitle());
                            intent.putExtra("bestScore",mylevel.getBestScore());
                            context.startActivity(intent);
                            context.finish();
                        }
                    }
                })
                .setNegative("我再想想")
                .setMessage("您的游戏记录可能还未保存,是否确定前往上一关?")
                .isCancelable(false)
                .show();

    }

    // 进入下一关
    public void enterNextLevel(View view) {
        new PanterDialog(this)
                .setHeaderBackground(R.drawable.pattern_bg_blue)
                .setTitle("下一关")
                .setPositive("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        kv.nextLevel();
                    }
                })
                .setNegative("我再想想")
                .setMessage("您的游戏记录可能还未保存,是否确定前往下一关?")
                .isCancelable(false)
                .show();
    }

    // 回到首页
    public void toHomePage(View view) {
        new PanterDialog(this)
                .setHeaderBackground(R.drawable.pattern_bg_blue)
                .setTitle("前往首页")
                .setPositive("前往首页", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegative("我再想想")
                .setMessage("您的游戏记录可能还未保存,是否确定前往首页?")
                .isCancelable(false)
                .show();
    }

    // 关于游戏
    public void aboutGame(View view) {
        new PanterDialog(this)
                .setHeaderBackground(R.drawable.pattern_bg_blue)
                .setTitle("关于游戏")
                .setPositive("朕了解了")
                .setMessage(R.string.about_game)
                .isCancelable(false)
                .show();
    }

    // 退出游戏
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

    // 在点击某个具体的选项后，关闭浮动按钮
    private void closeFloatingButton()
    {
        FloatingActionButton mButton = findViewById(R.id.my_floating_button);
        
    }


}
