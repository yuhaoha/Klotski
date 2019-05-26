package com.nkcs.yuhao.klotski;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    public Stack<PlayBoard> states = new Stack<>(); //保存历史状态
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
        initPlayBoard();
    }

    // 在设置游戏关卡后要改变游戏板
    private void initPlayBoard()
    {
        // 将默认（第一关）的游戏板弹出
        if(!states.empty())
            states.clear();
        // 创建新的游戏板
        PlayBoard newPlayBoard =  new PlayBoard(4,5);
        // 根据关卡设置不同布局
        switch (level)
        {
            case 1:
                // 第1关 添加人物
                newPlayBoard.fragmentHashtable.put(1,new Fragment("曹操", 1, 2, 2, 1, 0, R.drawable.role_caocao));
                newPlayBoard.fragmentHashtable.put(2,new Fragment("张飞", 2, 1, 2, 0, 0, R.drawable.role_zhangfei));
                newPlayBoard.fragmentHashtable.put(3,new Fragment("赵云", 3 , 1, 2, 3, 2, R.drawable.role_zhaoyun));
                newPlayBoard.fragmentHashtable.put(4,new Fragment("马超", 4, 1, 2, 0, 2, R.drawable.role_machao));
                newPlayBoard.fragmentHashtable.put(5,new Fragment("黄忠", 5, 1, 2, 3, 0, R.drawable.role_huangzhong));
                newPlayBoard.fragmentHashtable.put(6,new Fragment("关羽", 6, 2, 1, 1, 2, R.drawable.role_guanyu));
                newPlayBoard.fragmentHashtable.put(7,new Fragment("兵", 7, 1, 1, 0, 4, R.drawable.role_soldier));
                newPlayBoard.fragmentHashtable.put(8,new Fragment("兵", 8, 1, 1, 3, 4, R.drawable.role_soldier));
                newPlayBoard.fragmentHashtable.put(9,new Fragment("兵", 9, 1, 1, 1, 3, R.drawable.role_soldier));
                newPlayBoard.fragmentHashtable.put(10,new Fragment("兵", 10, 1, 1, 2, 3, R.drawable.role_soldier));
                break;
            case 2:
                // 设置布局，代表10个人物的(x,y)坐标
                Point [] points = {new Point(1,0),new Point(0,0),new Point(3,0),new Point(0,3),new Point(3,3),new Point(1,2),new Point(0,2),new Point(3,2),new Point(1,3),new Point(2,3)};
                // 加入到playBoard
                newPlayBoard.addFragmentToPlayBoard(points);
        }
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
        Fragment f = playBoard.fragmentHashtable.get(8); //选一个小兵来测试
        if(f!=null)
        {
            if(f.getxPos()==1&&f.getyPos()==4)
            {
                PlayGame pg = (PlayGame)PlayGame.getActivity(); //获取Activity引用
                Intent intent = new Intent(pg,ChooseLevel.class);  //跳转到游戏胜利页面
                pg.startActivity(intent);
            }
        }
    }

    // 设置步长
    private void setSteps()
    {
        PlayGame pg = (PlayGame)PlayGame.getActivity(); //获取Activity引用
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

    //  重新游戏
    public void replay()
    {
        // 弹出栈内除栈低外所有状态
        while(states.size()>1)
        {
            states.pop();
        }
        playBoard = states.peek();
        invalidate();
        moveTimes = 0;
        setSteps();
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


//        String name = fragment.getName();
//        paint.setColor(Color.BLACK);
//        canvas.drawText(name,fragment.getxPos(),fragment.getyPos(),paint);

//        // 获取人物对应的图片
//        InputStream is = this.getContext().getResources().openRawResource(fragment.getPicture());
//        BitmapDrawable bmpDraw = new BitmapDrawable(is);//下划线在腰上，疑似旧的API可以被新的代替，但是也能用
//        // 生成位图
//        Bitmap mPic = bmpDraw.getBitmap();
//        // 绘图
//        canvas.drawBitmap(mPic, null, rect, paint);
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
        SIZE = widthSize/5;
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

    // 向游戏板中添加人物块
    //人物顺序固定，1曹操、2张飞、3赵云、4马超、5黄忠、6关羽、7-10小兵
    // name,value,width,heigth,mpicture不变，仅xPos,yPos改变
    void addFragmentToPlayBoard(Point[] points)
    {
        // 向游戏板加入10个人物块
        this.fragmentHashtable.put(1,new Fragment("Cao Cao", 1, 2, 2, points[0].x, points[0].y, R.drawable.role_caocao));
        this.fragmentHashtable.put(2,new Fragment("Zhang Fei", 2, 1, 2, points[1].x, points[1].y, R.drawable.role_zhangfei));
        this.fragmentHashtable.put(3,new Fragment("Zhao Yun", 3 , 1, 2,points[2].x, points[2].y, R.drawable.role_zhaoyun));
        this.fragmentHashtable.put(4,new Fragment("Ma Chao", 4, 1, 2,points[3].x, points[3].y, R.drawable.role_machao));
        this.fragmentHashtable.put(5,new Fragment("Huang Zhong", 5, 1, 2, points[4].x, points[4].y, R.drawable.role_huangzhong));
        this.fragmentHashtable.put(6,new Fragment("Guan Yu", 6, 2, 1,points[5].x, points[5].y, R.drawable.role_guanyu));
        this.fragmentHashtable.put(7,new Fragment("Soldier1", 7, 1, 1, points[6].x, points[6].y, R.drawable.role_soldier));
        this.fragmentHashtable.put(8,new Fragment("Soldier2", 8, 1, 1, points[7].x, points[7].y, R.drawable.role_soldier));
        this.fragmentHashtable.put(9,new Fragment("Soldier3", 9, 1, 1, points[8].x, points[8].y, R.drawable.role_soldier));
        this.fragmentHashtable.put(10,new Fragment("Soldier4", 10, 1, 1, points[9].x, points[9].y, R.drawable.role_soldier));
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
    private int mPicture;

    public Fragment(String name, int value, int width, int height, int xPos, int yPos, int mPicture)
    {
        this.name = name;
        this.width = width;
        this.height = height;
        this.value = value;
        this.xPos = xPos;
        this.yPos = yPos;
        this.mPicture = mPicture;
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

    public int getPicture(){
        return mPicture;
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

// 每个点的类
class Point
{
    int x; //横坐标
    int y; //纵坐标
    Point(int x,int y)
    {
        this.x = x;
        this.y = y;
    }
}