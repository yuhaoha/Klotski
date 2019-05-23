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
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
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

public class KlotskiView extends View
{
    private final int SIZE = 200;
    private int level =1;
    private PlayBoard playBoard ; //当前的游戏板
    public int moveTimes = 0; //移动次数
    public Stack<PlayBoard> states = new Stack<>(); //保存历史状态

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
                newPlayBoard.fragmentHashtable.put(1,new Fragment("Cao Cao", 1, 2, 2, 1, 0, R.drawable.role_caocao));
                newPlayBoard.fragmentHashtable.put(2,new Fragment("Zhang Fei", 2, 1, 2, 0, 0, R.drawable.role_zhangfei));
                newPlayBoard.fragmentHashtable.put(3,new Fragment("Zhao Yun", 3 , 1, 2, 3, 2, R.drawable.role_zhaoyun));
                newPlayBoard.fragmentHashtable.put(4,new Fragment("Ma Chao", 4, 1, 2, 0, 2, R.drawable.role_machao));
                newPlayBoard.fragmentHashtable.put(5,new Fragment("Huang Zhong", 5, 1, 2, 3, 0, R.drawable.role_huangzhong));
                newPlayBoard.fragmentHashtable.put(6,new Fragment("Guan Yu", 6, 2, 1, 1, 2, R.drawable.role_guanyu));
                newPlayBoard.fragmentHashtable.put(7,new Fragment("Soldier1", 7, 1, 1, 0, 4, R.drawable.role_soldier));
                newPlayBoard.fragmentHashtable.put(8,new Fragment("Soldier2", 8, 1, 1, 3, 4, R.drawable.role_soldier));
                newPlayBoard.fragmentHashtable.put(9,new Fragment("Soldier3", 9, 1, 1, 1, 3, R.drawable.role_soldier));
                newPlayBoard.fragmentHashtable.put(10,new Fragment("Soldier4", 10, 1, 1, 2, 3, R.drawable.role_soldier));
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
        //下面是加载监听器
        this.setOnTouchListener(new OnTouchListener()
        {
            private int selectedValue = 0;//记录被选中的人物的value
            private int oritation = 0;//记录移动的方向，0代表不移动
            int beginX=0,beginY=0,endX=0,endY=0;  //滑动开始，滑动结束的x,y值
            //onTouch表示单击
            public boolean onTouch(View view, MotionEvent motion)
            {
                // 获取触摸点相对于父View的x,y值
                switch (motion.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        beginX = (int)motion.getX()/SIZE;
                        beginY = (int)motion.getY()/SIZE;
                        break;
                    case MotionEvent.ACTION_UP:
                        endX = (int)motion.getX()/SIZE;
                        endY = (int)motion.getY()/SIZE;
                        // 处理边界异常
                        if(endX<0)
                            endX = 0;
                        else if(endX>3)
                            endX = 3;
                        if(endY < 0)
                            endY = 0;
                        else if(endY > 4)
                            endY = 4;
                        // 确定移动方向
                        if(endX<beginX)  //对应左移
                            oritation = 3;
                        else if(endX>beginX) //对应右移
                            oritation = 4;
                        if(endY<beginY) //对应下移
                            oritation = 1;
                        else if(endY>beginY) //对应上移
                            oritation = 2;
                        Log.d("hello","beginX="+beginX+" beginY="+beginY);
                        Log.d("hello","endX="+endX+" endY="+endY);
                        break;
                }

                Log.d("hello","当前hashcode"+playBoard.hashCode());
                selectedValue = playBoard.getBoardValue(beginX, beginY); //获取点击的人物块的value
                Log.d("hello","selectedValue"+selectedValue);
                if(selectedValue != 0) //selectedValue == 0代表新选择的点是空的，无人物块
                {
                    // 获取对应的人物块引用
                    Fragment selectedFragment = playBoard.fragmentHashtable.get(selectedValue);
                    boolean result = playBoard.checkMove(selectedFragment,oritation); //检查是否可以移动
                    if(result) //进行移动操作
                    {
                        Log.d("hello","可以移动");
                        // 移动人物块
                        try {
                            // 获取移动后的人物板
                            playBoard = playBoard.moveFragment(selectedFragment,oritation);
                            Log.d("hello","成功改变PlayBoard");
                            // 加入到状态栈
                            states.push(playBoard);
                            Log.d("hello","改变后栈大小"+states.size()+"");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // 视图重新绘制，onDraw()被调用
                        view.invalidate();
                        moveTimes++;//移动次数+1
                        setSteps(); //改变移动步数
                        judgeVictory(); //判断移动后是否胜利
                    }
                    else
                        Log.d("hello",oritation+"不能移动");
                }
                return true;
            }
        });

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

    @Override
    //涂色
    //因为贴图，所以无意义
    public void onDraw(Canvas canvas)
    {
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
        Paint paint = new Paint();

        Rect rect = new Rect();//rect表示矩形
        rect.left = fragment.getxPos() * SIZE;//矩形左侧边界坐标等于位置（0,1,2,3,4）*80，上侧同理
        rect.top = fragment.getyPos() * SIZE;
        rect.right = (fragment.getxPos() + fragment.getWidth()) * SIZE;
        rect.bottom = (fragment.getyPos() + fragment.getHeight()) * SIZE;

        // 获取人物对应的图片
        InputStream is = this.getContext().getResources().openRawResource(fragment.getPicture());
        @SuppressWarnings("deprecation")
        BitmapDrawable bmpDraw = new BitmapDrawable(is);//下划线在腰上，疑似旧的API可以被新的代替，但是也能用
        Bitmap mPic = bmpDraw.getBitmap();
        canvas.drawBitmap(mPic, null, rect, paint);
    }

    // 设置组件大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(SIZE*4, SIZE*5);
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