package com.nkcs.yuhao.klotski;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eminayar.panter.PanterDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

public class KlotskiView extends View implements View.OnTouchListener,GestureDetector.OnGestureListener
{
    private  int SIZE ;
    private final int MARGIN = 10;
    private int level =1;
    private PlayBoard playBoard ; //当前的游戏板
    public int moveTimes = 0; //移动次数
    public Stack<PlayBoard> states = new Stack<PlayBoard>(); //保存历史状态
    // 构建手势探测器为gesture对象赋值,监听自定义view
    GestureDetector mygesture = new GestureDetector(this);

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        // 改变level
        this.level = level;
        Log.d("hello","level="+level);
        // 根据level调整布局
        // 将默认（第一关）的游戏板弹出
        if(!states.empty())
            states.clear();
        // 创建新的游戏板
        PlayBoard newPlayBoard =  new PlayBoard(4,5);
        // 根据关卡设置不同布局
        newPlayBoard.fragmentHashtable=DatabaseHelper.getLayoutObject(level);
        // 呈现在游戏板上
        Enumeration<Fragment> enumeration =  newPlayBoard.fragmentHashtable.elements();
        while(enumeration.hasMoreElements())
        {
            Fragment fragment = enumeration.nextElement();
            // 给游戏板数组赋值
            newPlayBoard.setFragmentValue(fragment);
        }
        // 赋值给当前游戏板
        playBoard = newPlayBoard;
        states.push(playBoard);
        // 绘制游戏板
        invalidate();
    }

    // 游戏读档
    public void loadGameHistory(int id){
        // 把 时间 level level_title 步数 展示在选择读档界面
        // 点击后传递id给游戏界面  注意区分得到的intent是choose_level 还是 choose_history
        // 游戏界面根据setStates 给游戏面板赋值并初始化

        // 在数据库中根据id读取历史记录
        states = DatabaseHelper.getGameHistory(id);
        // 设置步长
        moveTimes = states.size()-1;
        setSteps();
        // 设置level
        this.level = PlayGame.getLevel();
        // 设置游戏板
        playBoard = states.peek();
        // 绘制游戏板
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    public KlotskiView(Context context,AttributeSet paramAttributeSet)
    {
        // 在XML中使用构造函数需要AttributeSet
        super(context,paramAttributeSet);
        // 防止空引用
        playBoard = new PlayBoard(4,5);
        //设置Touch监听
        this.setOnTouchListener(this);

    }

    // 判断胜利条件
    private void judgeVictory()
    {
        // 获取人物块 曹操是1
        Fragment f = playBoard.fragmentHashtable.get(8); //选一个小兵来测试 8
        if(f!=null)
        {
            if(f.getxPos()==1&&f.getyPos()==4)
            {
                // 获取关卡值对应的对象
                Level mylevel = DatabaseHelper.getLevel(level);
                // 返回值为最新的最佳成绩
                int result = DatabaseHelper.updateToLevel(level,moveTimes);
                String message = "";
                if(result==moveTimes)
                    message+="创造了新的记录！\n";
                message += "恭喜您通过第"+level+"关："+mylevel.getTitle()+"\n您的步长是为："+moveTimes;
//                Intent intent = new Intent(pg,ChooseLevel.class);  //跳转到游戏胜利页面
//                pg.startActivity(intent);
                PlayGame pg = (PlayGame)PlayGame.getActivity(); //获取Activity引用
                new PanterDialog(pg)
                        .setHeaderBackground(R.drawable.pattern_bg_blue)
                        .setTitle("游戏胜利")
                        .setPositive("下一关", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 开始下一关
                                nextLevel();
                            }
                        })
                        .setNegative("返回首页", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toHomePage();
                            }
                        })
                        .setMessage(message)
                        .isCancelable(false)
                        .show();
            }
        }
    }


    private void toHomePage()
    {
        // 回到首页
        PlayGame pg = (PlayGame)PlayGame.getActivity(); //获取Activity引用
        Intent intent = new Intent(pg,MainActivity.class);  //跳转到游戏胜利页面
        pg.startActivity(intent);
        pg.finish();
    }


    // 下一关
    void nextLevel()
    {
        // 检查是否到了最后一关再执行查询
        if(DatabaseHelper.getLevelListObject().size()<=level+1)
        {
            toHomePage();
        }
        else
        {
            PlayGame pg = (PlayGame)PlayGame.getActivity(); //获取Activity引用
            Intent intent = new Intent(pg,PlayGame.class);  //跳转到游戏页面
            Level mylevel = DatabaseHelper.getLevel(level+1);
            intent.putExtra("activityName","PlayGame");
            intent.putExtra("levelId",mylevel.getLevelId());
            intent.putExtra("levelTitle",mylevel.getTitle());
            intent.putExtra("bestScore",mylevel.getBestScore());
            pg.startActivity(intent);
            pg.finish();
        }
    }

    // 设置步长
    private void setSteps()
    {
        PlayGame pg = (PlayGame)PlayGame.getActivity(); //获取Activity引用
        Log.d("hello","in setSteps moveTimes="+moveTimes);
        pg.setMoveTimes(moveTimes);
    }

    // 上一步
    public void lastState()
    {
        // 已经到了最初状态
        if(states.size()<=1)
            return;
        // 弹出最顶部状态
        states.pop();
        // 设置新状态
        playBoard = states.peek();
        // 重新绘制
        invalidate();
        moveTimes--;//移动次数-1
        setSteps(); //改变移动步数
    }


    // 游戏存档
    public void saveGameHistory() {
        String time = Util.getCurrentTime();
        PlayGame pg = (PlayGame)PlayGame.getActivity(); //获取Activity引用
        TextView levelTitle = pg.findViewById(R.id.levelTitleInGame);
        String level_title = (String)levelTitle.getText();
        // 参数分别为 时间 关卡id 关卡标题 移动次数 状态栈
        DatabaseHelper.insertGameHistory(new GameHistory(time,level,level_title,moveTimes,states));
    }

    /**
     * 182,180,194 背景
     * 209,209,209 胜利
     * 170,201,206 曹操
     * 229,193,205 else
     *
     * 橙色 255,221,148
     * 紫色 182,227,206
     */

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        // 为自定义View设置背景
        // this.setBackgroundResource(R.drawable.gameboard_background);
        // 画游戏区域背景
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE); //描边且填充
        RectF rect = new RectF(0,0,SIZE*4,SIZE*5);
        paint.setARGB(255,204,171,219);
        canvas.drawRoundRect(rect,50,50,paint);
        // 画胜利区域
        rect = new RectF(SIZE*1,SIZE*3,SIZE*3,SIZE*5);
        paint.setARGB(150,255,221,148);
//        canvas.drawRoundRect(rect,50,50,paint);
        // 依次画每个人物
        Enumeration<Fragment> enumeration = playBoard.fragmentHashtable.elements();
        while(enumeration.hasMoreElements())
        {
            Fragment fragment = enumeration.nextElement();
            // 每次绘制一个人物块
            drawFragment(canvas, fragment);
        }
    }

    //绘制矩形形状和尺寸
    private void drawFragment(Canvas canvas, Fragment fragment)
    {
        // 定义一个paint
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE); //描边且填充
        // 每个人物块是一个曲边矩形  坐标是相对画布canvas而言的
        // 绘制人物块
        // 外层透明矩形
        RectF rect = new RectF();
        rect.left = fragment.getxPos() * SIZE;//矩形左侧边界坐标等于位置（0,1,2,3,4）*200，上侧同理
        rect.top = fragment.getyPos() * SIZE;
        rect.right = (fragment.getxPos() + fragment.getWidth()) * SIZE;
        rect.bottom = (fragment.getyPos() + fragment.getHeight()) * SIZE;
        paint.setColor(Color.TRANSPARENT);  // 颜色设置为透明
        canvas.drawRoundRect(rect,50,50,paint);
        // 内层矩形
        if(fragment.getValue()==1) //曹操设置成绿色
            paint.setARGB(255,250,137,123);
        else
            paint.setARGB(255,182,227,206); // 透明度，RGB  //
        rect.left += MARGIN;
        rect.right -= MARGIN;
        rect.top += MARGIN;
        rect.bottom -= MARGIN;
        canvas.drawRoundRect(rect,50,50,paint);

    }

     // 设置组件大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);   //获取宽的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec); //获取高的模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
        int heightSize = MeasureSpec.getSize(heightMeasureSpec); //获取高的尺寸
        Log.v("hello", "宽的模式:"+widthMode);
        Log.v("hello", "高的模式:"+heightMode);
        Log.v("hello", "宽的尺寸:"+widthSize);
        Log.v("hello", "高的尺寸:"+heightSize);
        // 根据测量值（父控件最多能为子控件提供的）为SIZE赋值，留20%空白
        SIZE = widthSize/4;
        int width;
        int height ;
        if (widthMode == MeasureSpec.EXACTLY) {
            //如果match_parent或者具体的值，直接赋值
            width = widthSize;
        } else {
            //如果是wrap_content，我们要得到控件需要多大的尺寸
            width = SIZE*4;   //控件的宽度
            Log.v("hello",  "控件的宽度："+width);
        }
        //高度跟宽度处理方式一样
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = SIZE*5;
            Log.v("hello", "控件的高度："+height);
        }
        //保存测量宽度和测量高度
        setMeasuredDimension(width, height);
        // setMeasuredDimension(SIZE*4, SIZE*5);
    }

    // 实现OnGestureListener接口
    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("hello","onDown触发");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    // 监听滑动事件
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // e1：第1个ACTION_DOWN MotionEvent 手指摁下的event
        // e2：最后一个ACTION_MOVE MotionEvent 手指抬起的event
        // velocityX：X轴上的移动速度（像素/秒）
        // velocityY：Y轴上的移动速度（像素/秒）
        Log.d("hello", "onFlying触发");

        int beginX=0,beginY=0;
        int selectedValue =0; // 选择的人物块的value
        int oritation = 0; //滑动的方向，不滑动为0
        beginX = ((int) e1.getX()) / SIZE;
        beginY = ((int) e1.getY()) / SIZE;
        selectedValue = playBoard.getBoardValue(beginX, beginY); //获取点击的人物块的value
        Log.d("hello", "selectedValue" + selectedValue);

        // 确定移动方向
        if(Math.abs(velocityX) <= Math.abs(velocityY))  //上下移动
        {
            if(velocityY<0)
                oritation = 1; //上
            else
                oritation = 2; //下
        }
        else
        {
            if(velocityX<0)
                oritation = 3; //左
            else
                oritation = 4; //右
        }

        // 进行移动相关操作
        if (selectedValue != 0) //selectedValue == 0代表新选择的点是空的，无人物块
        {
            // 获取对应的人物块引用
            Fragment selectedFragment = playBoard.fragmentHashtable.get(selectedValue);
            boolean result = playBoard.checkMove(selectedFragment, oritation); //检查是否可以移动
            if (result) //进行移动操作
            {
                // 移动人物块
                try {
                    // 获取移动后的人物板
                    playBoard = playBoard.moveFragment(selectedFragment, oritation);
                    // 加入到状态栈
                    states.push(playBoard);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 视图重新绘制，onDraw()被调用
                invalidate();
                moveTimes++;//移动次数+1
                setSteps(); //改变移动步数
                judgeVictory(); //判断移动后是否胜利
            } else
                Log.d("hello", oritation + "不能移动");
        }
        return false;
    }

    // 实现OnTouchListener接口
    @Override
    public boolean onTouch(View v, MotionEvent motion) {
        // 点击事件，返回给mygesture处理
        return mygesture.onTouchEvent(motion);
    }

}


class PlayBoard implements Serializable {

    private int[][] playArea;
    public  Hashtable<Integer,Fragment> fragmentHashtable = new Hashtable<>();
    public PlayBoard(int width, int heigth)
    {
        playArea = new int[heigth][width];
        for(int i = 0; i < playArea.length; i++)
            for(int j = 0; j < playArea[i].length; j++)
                playArea[i][j] = 0;
    }

    public int getBoardValue(int x, int y)
    {
        return playArea[y][x];
    }

    // 对当前游戏板进行拷贝
    public PlayBoard clonePlayBoard(PlayBoard pb)throws Exception
    {
        return MyUtil.clone(pb);
    }

    // 为游戏板设置人物块的数值
    void setFragmentValue(Fragment fragment)
    {
        // 对playBoard中的每个格子赋值
        for(int i = fragment.getyPos();i<fragment.getyPos()+fragment.getHeight();i++) //每行
        {
            for(int j=fragment.getxPos();j<fragment.getxPos()+fragment.getWidth();j++)  //每列
            {
                playArea[i][j] = fragment.getValue();
            }
        }
    }

    // 清除人物块
    void clearFragment(Fragment fragment)
    {
        // 对playBoard中的每个格子赋值为0
        for(int i = fragment.getyPos();i<fragment.getyPos()+fragment.getHeight();i++) //每行
        {
            for(int j=fragment.getxPos();j<fragment.getxPos()+fragment.getWidth();j++)  //每列
            {
                playArea[i][j] = 0;
            }
        }
    }


    // 移动人物块 返回移动后新的游戏板（改变Fragment的x或y，且playArea改变）
    PlayBoard moveFragment(Fragment f,int oritation) throws Exception
    {
        Log.d("hello","进入moveFragment");
        PlayBoard newPlayBoard = this.clonePlayBoard(this);
        // 找到clone的PlayBoard中对应的Fragment
        Fragment newFragment = newPlayBoard.fragmentHashtable.get(f.getValue());
        if(newFragment!=null)
            Log.d("hello",newFragment.getValue()+"");
        else
            Log.d("hello","没有获取到新Fragment");
        int x=newFragment.getxPos(),y=newFragment.getyPos();
        switch (oritation)
        {
            case 1:     //上移
                newPlayBoard.clearFragment(newFragment);
                newFragment.setyPos(y-1); //改变y值
                newPlayBoard.setFragmentValue(newFragment);
                break;
            case 2:
                newPlayBoard.clearFragment(newFragment);
                newFragment.setyPos(y+1);
                newPlayBoard.setFragmentValue(newFragment);
                break;
            case 3:
                newPlayBoard.clearFragment(newFragment);
                newFragment.setxPos(x-1);
                newPlayBoard.setFragmentValue(newFragment);
                break;
            case 4:
                newPlayBoard.clearFragment(newFragment);
                newFragment.setxPos(x+1);
                newPlayBoard.setFragmentValue(newFragment);
                break;
        }
        Log.d("hello","成功返回PlayBoard");
        return newPlayBoard;
    }

    // 检查是否可以移动
    boolean checkMove(Fragment f,int oritation)
    {
        int x=f.getxPos(),y=f.getyPos(),width=f.getWidth(),heigth=f.getHeight();
        switch (oritation)
        {
            case 0: //不移动
                return false;
            case 1:  //上
                if(y-1<0)
                    return false;
                for(int i=x;i<x+width;i++)
                {
                    if(playArea[y-1][i]!=0)
                        return false;
                }
                return true;
            case 2:   //下
                if(y+1>4||y+heigth>4)
                    return false;
                for(int i=x;i<x+width;i++)
                {
                    if(playArea[y+heigth][i]!=0)
                        return false;
                }
                return true;
            case 3:     //左
                if(x-1<0)
                    return false;
                for(int i=y;i<y+heigth;i++)
                {
                    if(playArea[i][x-1]!=0) //左边有别的人物块，不能移动
                        return false;
                }
                return true;
            case 4:        //右
                if(x+1>3||x+width>3)
                    return false;
                for(int i=y;i<y+heigth;i++)
                {
                    if(playArea[i][x+width]!=0)
                        return false;
                }
                return true;
        }
        return false;
    }

    private void gameVictory()
    {
        Toast.makeText(MyApplication.getContext(),
                "游戏胜利",
                Toast.LENGTH_SHORT).show();
    }
}

class Fragment implements Serializable{
    private String name;
    private int width;
    private int height;
    private int xPos = 0;
    private int yPos = 0;
    private int value;  //标识每一个块

    @Override
    public String toString() {
        return "Fragment{" +
                "name='" + name + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", xPos=" + xPos +
                ", yPos=" + yPos +
                ", value=" + value +
                '}';
    }

    public Fragment(String name, int value, int width, int height, int xPos, int yPos)
    {
        this.name = name;
        this.width = width;
        this.height = height;
        this.value = value;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public String getName(){
        return this.name;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public int getValue(){
        return value;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

}

class MyUtil {
    private MyUtil() {
        throw new AssertionError();
    }
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T clone(T obj) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bout);
        oos.writeObject(obj);
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bin);
        return (T) ois.readObject();
    }
}