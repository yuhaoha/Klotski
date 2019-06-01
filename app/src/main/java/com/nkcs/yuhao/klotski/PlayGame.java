package com.nkcs.yuhao.klotski;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PlayGame extends AppCompatActivity {
    private static PlayGame context = null; //中介变量
    KlotskiView kv; //获取Klotski组件引用
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取关卡数值 最佳成绩 关卡名
        Intent intent = getIntent();
        int level = intent.getIntExtra("levelId",1);
        int bestScore = intent.getIntExtra("bestScore",9999);
        String levelTitle = intent.getStringExtra("levelTitle");
        // 设置关卡
//        setGameLevel(level);
        // 设置布局，要在设置关卡之后执行
        setContentView(R.layout.activity_play_game);
        context = this;
        kv = findViewById(R.id.gameBoard);
        kv.setLevel(level);
        // 设置关卡名
        TextView titleView = findViewById(R.id.levelTitleInGame);
        titleView.setText(levelTitle);
    }

    public static Activity getActivity()
    {
        return context;
    }

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
        tv.setText("总计:"+moveTimes);
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
        if(kv!=null)
        {
            // 调用KlotskiView的方法，重新开始游戏
            kv.replay();
        }
        else
            Log.d("hello","kv==null");
    }

    // 游戏存档
    public void saveGameHistory(View view) {
        if(kv!=null)
        {
            // 调用KlotskiView的方法，重新开始游戏
            kv.saveGameHistory();
            Toast.makeText(MyApplication.getContext(),
                    "存档成功",
                    Toast.LENGTH_SHORT).show();
        }
        else
            Log.d("hello","kv==null");
    }
}
