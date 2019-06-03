package com.nkcs.yuhao.klotski;

import java.util.Stack;

public class GameHistory {
    // DatabaseHelper.insertGameHistory(time,level,level_title,states);
    int id;
    String time;
    int level;
    String levelTitle;
    int moveTimes;
    Stack<PlayBoard> states;

    // 插入时的构造函数
    GameHistory(String time,int level,String levelTitle,int moveTimes,Stack<PlayBoard> states)
    {
        this.time = time;
        this.level = level;
        this.levelTitle = levelTitle;
        this.moveTimes = moveTimes;
        this.states = states;
    }

    // 从数据库取出后，存储在gameHistoryList中的数据格式
    GameHistory(int id,String time,int level,String levelTitle,int moveTimes)
    {
        this.id = id;
        this.time = time;
        this.level = level;
        this.levelTitle = levelTitle;
        this.moveTimes = moveTimes;
        this.states = null;
    }
}
