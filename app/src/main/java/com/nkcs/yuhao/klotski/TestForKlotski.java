package com.nkcs.yuhao.klotski;

import android.provider.ContactsContract;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TestForKlotski {
    private Stack<PlayBoard> playBoards = new Stack<PlayBoard>();
    private String data;
    private PlayBoard newPlayBoard = new PlayBoard(4,5);
    public static void main(String[] args) {
        TestForKlotski mytest = new TestForKlotski();
//        mytest.toJson();
//        mytest.getJson();
        System.out.print(Util.getCurrentTime());
    }

    private void toJson()
    {
        Gson gson = new Gson();
        newPlayBoard.fragmentHashtable.put(1,new Fragment("曹操", 1, 2, 2, 1, 0));
        newPlayBoard.fragmentHashtable.put(2,new Fragment("张飞", 2, 1, 2, 0, 0));
        newPlayBoard.fragmentHashtable.put(3,new Fragment("赵云", 3 , 1, 2, 3, 2));
        newPlayBoard.fragmentHashtable.put(4,new Fragment("马超", 4, 1, 2, 0, 2));
        newPlayBoard.fragmentHashtable.put(5,new Fragment("黄忠", 5, 1, 2, 3, 0));
        newPlayBoard.fragmentHashtable.put(6,new Fragment("关羽", 6, 2, 1, 1, 2));
        newPlayBoard.fragmentHashtable.put(7,new Fragment("兵", 7, 1, 1, 0, 4));
        newPlayBoard.fragmentHashtable.put(8,new Fragment("兵", 8, 1, 1, 3, 4));
        newPlayBoard.fragmentHashtable.put(9,new Fragment("兵", 9, 1, 1, 1, 3));
        newPlayBoard.fragmentHashtable.put(10,new Fragment("兵", 10, 1, 1, 2, 3));
        playBoards.push(newPlayBoard);
        playBoards.push(newPlayBoard);
        playBoards.push(newPlayBoard);
        // 将栈写成json String
        data = gson.toJson(playBoards);
        System.out.println(data);
    }

    private void getJson()
    {
        Gson gson = new Gson();
        Stack<PlayBoard> states = gson.fromJson(data,  new TypeToken<Stack<PlayBoard>>() {}.getType());
        System.out.println(states.size());
        for(int i =0;i<states.size();i++)
        {
            System.out.println(states.get(i).fragmentHashtable);
        }
    }

}

