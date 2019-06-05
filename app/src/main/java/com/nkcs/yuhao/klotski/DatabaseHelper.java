package com.nkcs.yuhao.klotski;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Stack;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String name ="klotski";
    private static final int version = 1;

    // 每个点的类
    class Point
    {
        int x; //横坐标
        int y; //纵坐标
        int heigth;
        int width;
        Point(int x,int y,int width,int heigth)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.heigth = heigth;
        }
    }

    // 构造函数
    DatabaseHelper(Context context) {
        super(context, name, null, version);
    }

    // 程序安卓的时候执行！
    // 初次使用软件时生成数据库表，初次生成数据库时调用，添加使用到的初始数据
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        String sql = "CREATE TABLE IF NOT EXISTS level (level_id integer primary key , title varchar(100), description varchar(500),best_score INTEGER,level_layout text)";
        db.execSQL(sql);
        // 创建游戏存档表
        sql = "CREATE TABLE IF NOT EXISTS game_history (id INTEGER PRIMARY KEY AUTOINCREMENT ,time text, level_id Integer, level_title varchar(100),movetimes Integer,states text)";
        db.execSQL(sql);
        // 插入关卡数据
        Point [] points1 = {new Point(1,0,2,2),new Point(0,0,1,2),new Point(3,0,1,2),new Point(0,2,1,2),new Point(3,2,1,2),new Point(1,2,2,1),new Point(0,4,1,1),new Point(3,4,1,1),new Point(1,3,1,1),new Point(2,3,1,1)};
        db.execSQL("insert into level(level_id,title,description,best_score,level_layout) values(1,'横刀立马','曹操能否逃脱，请拭目以待',9999,?)",new Object[]{getLayoutJson(points1)});
        Point [] points2 = {new Point(1,0,2,2),new Point(0,0,1,2),new Point(3,0,1,2),new Point(0,3,1,2),new Point(3,3,1,2),new Point(1,2,2,1),new Point(0,2,1,1),new Point(3,2,1,1),new Point(1,3,1,1),new Point(2,3,1,1)};
        db.execSQL("insert into level(level_id,title,description,best_score,level_layout) values(2,'小试牛刀','曹操能否逃脱，请拭目以待',9999,?)",new Object[]{getLayoutJson(points2)});
        Point [] points3 = {new Point(1,0,2,2),new Point(0,0,1,2),new Point(3,0,1,2),new Point(0,3,1,2),new Point(3,3,1,2),new Point(1,2,2,1),new Point(0,2,1,1),new Point(3,2,1,1),new Point(1,3,1,1),new Point(2,3,1,1)};
        db.execSQL("insert into level(level_id,title,description,best_score,level_layout) values(3,'齐头并前','曹操能否逃脱，请拭目以待',9999,?)",new Object[]{getLayoutJson(points3)});
        db.execSQL("insert into level(level_id,title,description,best_score) values(4,'兵分三路','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(5,'屯兵东路','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(6,'左右布兵','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(7,'前挡后阻','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(8,'插翅难飞','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(9,'近在咫尺','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(10,'层层设防','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(11,'水泄不通','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(12,'小燕出巢','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(13,'兵挡将阻','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(14,'过五关','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(15,'一夫当关','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(16,'一字长蛇','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(17,'四面楚歌','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(18,'比翼横空','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(19,'兵临曹营','曹操能否逃脱，请拭目以待',9999)");
        db.execSQL("insert into level(level_id,title,description,best_score) values(20,'逃之夭夭','曹操能否逃脱，请拭目以待',9999)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // 向游戏板中添加人物块
    //人物顺序固定，1曹操、2张飞、3赵云、4马超、5黄忠、6关羽、7-10小兵
    // name,value,width,heigth,mpicture不变，仅xPos,yPos改变
    private String getLayoutJson(Point[] points)
    {
        // 向游戏板加入10个人物块
        Hashtable<Integer,Fragment> fragmentHashtable = new Hashtable<>();
        fragmentHashtable.put(1,new Fragment("Cao Cao", 1, 2, 2, points[0].x, points[0].y));
        fragmentHashtable.put(2,new Fragment("Zhang Fei", 2, 1, 2, points[1].x, points[1].y));
        fragmentHashtable.put(3,new Fragment("Zhao Yun", 3 , 1, 2,points[2].x, points[2].y));
        fragmentHashtable.put(4,new Fragment("Ma Chao", 4, 1, 2,points[3].x, points[3].y));
        fragmentHashtable.put(5,new Fragment("Huang Zhong", 5, 1, 2, points[4].x, points[4].y));
        fragmentHashtable.put(6,new Fragment("Guan Yu", 6, 2, 1,points[5].x, points[5].y));
        fragmentHashtable.put(7,new Fragment("Soldier1", 7, 1, 1, points[6].x, points[6].y));
        fragmentHashtable.put(8,new Fragment("Soldier2", 8, 1, 1, points[7].x, points[7].y));
        fragmentHashtable.put(9,new Fragment("Soldier3", 9, 1, 1, points[8].x, points[8].y));
        fragmentHashtable.put(10,new Fragment("Soldier4", 10, 1, 1, points[9].x, points[9].y));
        Gson gson = new Gson();
        String data = gson.toJson(fragmentHashtable);
        return data;
    }

    // 根据传入的等级返回对应的布局
    static Hashtable<Integer,Fragment> getLayoutObject(int level)
    {
        String data="";
        DatabaseHelper dbHelper = new DatabaseHelper(PlayGame.getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 查询level对应的布局HashTable对象
        Cursor cursor = db.rawQuery("select * from level where level_id=?",new String[]{level+""});
        while (cursor.moveToNext())
            data = cursor.getString(4);
        Log.d("hello","data="+data);
        cursor.close();
        db.close();
        // 解析数据
        Gson gson = new Gson();
        Hashtable<Integer,Fragment> newFragmentHashTable;
        newFragmentHashTable = gson.fromJson(data,  new TypeToken<Hashtable<Integer,Fragment>>() {}.getType());
        return newFragmentHashTable;
    }

    // 获取关卡列表
    static LinkedList<Level> getLevelListObject()
    {
        // 初始化要显示的内容 从数据库取数据
        LinkedList<Level> levelList = new LinkedList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from level",null);
        while (cursor.moveToNext())
        {
            Level level = new Level(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3));
            levelList.addLast(level);
        }
        cursor.close();
        db.close();
        return levelList;

    }

    // 插入游戏存档
    static void insertGameHistory(GameHistory gh)
    {
        String time = gh.time;
        int level_id = gh.level;
        String level_title = gh.levelTitle;
        int movetimes = gh.moveTimes;
        Stack<PlayBoard> states = gh.states;
        Gson gson = new Gson();
        String states_data = gson.toJson(states);
        DatabaseHelper dbHelper = new DatabaseHelper(PlayGame.getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into game_history(time,level_id,level_title,movetimes,states) values(?,?,?,?,?)",new Object[]{time,level_id,level_title,movetimes,states_data});
    }

    // 获取历史记录列表 包含 id time level levelTitle moveTimes属性
    static LinkedList<GameHistory> getGameHistoryList()
    {
        // 初始化要显示的内容 从数据库取数据
        LinkedList<GameHistory> historyList = new LinkedList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from game_history order by id desc",null);
        while (cursor.moveToNext())
        {
            GameHistory gh = new GameHistory(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getString(3),cursor.getInt(4));
            historyList.addLast(gh);
        }
        cursor.close();
        db.close();
        return historyList;
    }

    // 根据历史记录id获取保存的状态栈
    static Stack<PlayBoard> getGameHistory(int id)
    {
        String data="";
        DatabaseHelper dbHelper = new DatabaseHelper(PlayGame.getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 查询id对应的Stack<PlayBoard>对象
        Cursor cursor = db.rawQuery("select * from game_history where id=?",new String[]{id+""});
        while (cursor.moveToNext())
            data = cursor.getString(5);
        Log.d("hello","data="+data);
        cursor.close();
        db.close();
        // 解析数据
        Gson gson = new Gson();
        Stack<PlayBoard> states;
        states = gson.fromJson(data,  new TypeToken<Stack<PlayBoard>>() {}.getType());
        return states;
    }

    // 删除游戏存档记录
    static void deleteGameHistory(int id)
    {
        DatabaseHelper dbHelper = new DatabaseHelper(ChooseHistory.getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM game_history WHERE id =?",new String[]{id+""});
    }

    // 游戏胜利后插入新的成绩（如果比最佳成绩好）
    static int updateToLevel(int level,int score)
    {
        DatabaseHelper dbHelper = new DatabaseHelper(PlayGame.getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int oldBestScore = DatabaseHelper.getLevel(level).getBestScore();
        if(score<oldBestScore)
        {
            db.execSQL("UPDATE level SET best_score = ? WHERE level_id = ?;",new String[]{score+"",level+""});
            return score;
        }
        else
            return oldBestScore;
    }

    // 获得level id对应的level对象
    static Level getLevel(int id)
    {
        Level level = new Level();
        DatabaseHelper dbHelper = new DatabaseHelper(PlayGame.getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 查询id对应的Stack<PlayBoard>对象
        Cursor cursor = db.rawQuery("select * from level where level_id=?",new String[]{id+""});
        while (cursor.moveToNext())
            level = new Level(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3));
        cursor.close();
        db.close();
        return level;
    }

}
