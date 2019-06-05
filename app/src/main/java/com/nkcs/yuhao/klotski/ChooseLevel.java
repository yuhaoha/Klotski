package com.nkcs.yuhao.klotski;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.LinkedList;

public class ChooseLevel extends AppCompatActivity {
    // 关卡列表
    private  LinkedList<Level> mLevelList;
    // recyclerView的引用
    private RecyclerView mRecyclerView;
    // 关卡适配器引用
    private LevelListAdapter mAdapter;
    private static ChooseLevel context = null; //中介变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_level);
        // 从数据库获取关卡列表
        mLevelList = DatabaseHelper.getLevelListObject();
        mRecyclerView = findViewById(R.id.recyclerview);
        // 创建适配器并提供数据展示
        mAdapter = new LevelListAdapter(this, mLevelList);
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

class Level
{
    private int levelId;
    private String title;
    private String description;
    private int bestScore;

    Level(int levelId,String title,String description,int bestScore)
    {
        this.title = title;
        this.description = description;
        this.levelId = levelId;
        this.bestScore = bestScore;
    }

    Level(){}

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Level{" +
                "levelId=" + levelId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", bestScore=" + bestScore +
                '}';
    }
}
