package com.nkcs.yuhao.klotski;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

// 适配器类LevelListAdapter
public class HistoryListAdapter extends
        RecyclerView.Adapter<HistoryListAdapter.HistoryViewHolder> {

    // 要展示的history内容
    private final LinkedList<GameHistory> mHistoryList;
    private final LayoutInflater mInflater;
    private Context mContext;

    // 内部实现holder类
    class HistoryViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener,View.OnLongClickListener {

        // 获取history_item中的 TextView,即显示关卡名称，当前移动次数，存储时间
        private final TextView historyTitle;
        private final TextView historyMovetimes;
        private final TextView historyTime;
        // Adapter对象
        final HistoryListAdapter mAdapter;

        // holder构造函数 ，itemView对应level_item.xml布局，以便在此布局中找到R.id.levelTitle
        // 同时传入adapter
        public HistoryViewHolder(View itemView, HistoryListAdapter adapter) {
            super(itemView);

            // level_ite.xml中找到对应的textView
            historyTitle = itemView.findViewById(R.id.historyTitle);
            historyMovetimes = itemView.findViewById(R.id.historyMovetimes);
            historyTime = itemView.findViewById(R.id.historyTime);
            this.mAdapter = adapter;
            // 为itemview监听点击事件
            itemView.setOnClickListener(this);
        }

        //监听点击事件，进入点击的关卡
        @Override
        public void onClick(View view) {
            // 获取点击item的位置，这个是API提供的函数 0为下标的开始
            int mPosition = getLayoutPosition();
            // 跳转到游戏界面，传递level值，关卡名，最佳成绩
            GameHistory gh = mHistoryList.get(mPosition);
            Intent intent = new Intent(mContext,PlayGame.class);
            intent.putExtra("activityName","ChooseHistory");
            intent.putExtra("historyId",gh.id);
            intent.putExtra("levelId",gh.level);
            intent.putExtra("levelTitle",gh.levelTitle);
            intent.putExtra("moveTimes",gh.moveTimes);
            mContext.startActivity(intent);
        }

        // 长按删除存档
        @Override
        public boolean onLongClick(View v) {
            int mPosition = getLayoutPosition();
            GameHistory gh = mHistoryList.get(mPosition);
            DatabaseHelper.deleteGameHistory(gh.id);
            Toast.makeText(MyApplication.getContext(),
                    "长按+gh.id",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // Adapter构造函数，传递过来上下文和关卡列表
    public HistoryListAdapter(Context context, LinkedList<GameHistory> historyList) {
        // 根据传过来的上下文给mInflater赋值
        mInflater = LayoutInflater.from(context);
        this.mHistoryList = historyList;
        //设置上下文环境
        this.mContext = context;
    }

    // 创建ViewHolder
    @Override
    public HistoryListAdapter.HistoryViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {

        View mItemView = mInflater.inflate(
                R.layout.history_item, parent, false);
        return new HistoryViewHolder(mItemView, this);
    }

    // 取出 LinkedList中的值，逐个显示在level_item上
    @Override
    public void onBindViewHolder(HistoryListAdapter.HistoryViewHolder holder,
                                 int position) {
        // 逐个获取level标题，已走步数，保存时间
        GameHistory gh = mHistoryList.get(position);
        String levelTitle = "第"+gh.level+"关："+gh.levelTitle;
        String myMoveTimes = "已走步数:"+gh.moveTimes;
        String historyTime = "存档时间： "+gh.time;
        // 通过holder修改值
        holder.historyTitle.setText(levelTitle);
        holder.historyMovetimes.setText(myMoveTimes);
        holder.historyTime.setText(historyTime);
    }

    // 返回关卡链表的大小
    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }
}

