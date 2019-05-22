package com.nkcs.yuhao.klotski;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.LinkedList;

public class ChooseLevel extends AppCompatActivity {
    // 关卡列表
    private final LinkedList<String> mWordList = new LinkedList<>();
    // recyclerView的引用
    private RecyclerView mRecyclerView;
    // 关卡适配器引用
    private LevelListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_level);

        // 初始化要显示的内容
        for(int i=0;i<14;i++)
        {
            mWordList.addLast("第"+(i+1)+"关：横刀立马");
        }

        mRecyclerView = findViewById(R.id.recyclerview);
        // 创建适配器并提供数据展示
        mAdapter = new LevelListAdapter(this, mWordList);
        // 将Adapter赋给RecyclerView
        mRecyclerView.setAdapter(mAdapter);
        // 给RecyclerView设置默认布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
