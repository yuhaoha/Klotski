package com.nkcs.yuhao.klotski;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.LinkedList;

public class ChooseHistory extends AppCompatActivity {
    // 游戏存档列表
    private  LinkedList<GameHistory> mHistoryList;
    // recyclerView的引用
    private RecyclerView mRecyclerView;
    // 游戏记录适配器引用
    private HistoryListAdapter mAdapter;
    private static ChooseHistory context = null; //中介变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_history);
        // 从数据库获取关卡列表
        mHistoryList = DatabaseHelper.getGameHistoryList();
        mRecyclerView = findViewById(R.id.historyRecyclerview);
        // 创建适配器并提供数据展示
        mAdapter = new HistoryListAdapter(this, mHistoryList);
        // 将Adapter赋给RecyclerView
        mRecyclerView.setAdapter(mAdapter);
        // 给RecyclerView设置默认布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        context = this;
    }

    public static Activity getActivity()
    {
        return context;
    }
}