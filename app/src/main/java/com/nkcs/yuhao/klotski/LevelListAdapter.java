package com.nkcs.yuhao.klotski;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

// 适配器类LevelListAdapter
public class LevelListAdapter extends
        RecyclerView.Adapter<LevelListAdapter.LevelViewHolder> {

    // 要展示的level内容
    private final LinkedList<Level> mLevelList;
    private final LayoutInflater mInflater;
    private Context mContext;

    // 内部实现holder类
    class LevelViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // 获取level_item中的 TextView,即显示关卡名称，最佳成绩，描述
        private final TextView levelItemView;
        private final TextView levelBestScore;
        private final TextView levelDescription;
        // Adapter对象
        final LevelListAdapter mAdapter;

        // holder构造函数 ，itemView对应level_item.xml布局，以便在此布局中找到R.id.levelTitle
        // 同时传入adapter
        public LevelViewHolder(View itemView, LevelListAdapter adapter) {
            super(itemView);

            // level_ite.xml中找到对应的textView
            levelItemView = itemView.findViewById(R.id.levelTitle);
            levelBestScore = itemView.findViewById(R.id.bestScore);
            levelDescription = itemView.findViewById(R.id.levelDescription);
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
            String levelTitle = mLevelList.get(mPosition).getTitle();
            int bestScore = mLevelList.get(mPosition).getBestScore();
            Intent intent = new Intent(mContext,PlayGame.class);
            intent.putExtra("activityName","ChooseLevel");
            intent.putExtra("levelId",mPosition+1);
            intent.putExtra("levelTitle",levelTitle);
            intent.putExtra("bestScore",bestScore);
            mContext.startActivity(intent);
        }
    }

    // Adapter构造函数，传递过来上下文和关卡列表
    public LevelListAdapter(Context context, LinkedList<Level> levelList) {
        // 根据传过来的上下文给mInflater赋值
        mInflater = LayoutInflater.from(context);
        this.mLevelList = levelList;
        //设置上下文环境
        this.mContext = context;
    }

    // 创建ViewHolder
    @Override
    public LevelListAdapter.LevelViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {

        View mItemView = mInflater.inflate(
                R.layout.level_item, parent, false);
        return new LevelViewHolder(mItemView, this);
    }

    // 取出 LinkedList中的值，逐个显示在level_item上
    @Override
    public void onBindViewHolder(LevelListAdapter.LevelViewHolder holder,
                                 int position) {
        // 逐个获取level标题，最佳成绩，描述
        int levelId = mLevelList.get(position).getLevelId();
        String levelTitle = mLevelList.get(position).getTitle();
        int bestScore = mLevelList.get(position).getBestScore();
        String levelDescription = mLevelList.get(position).getDescription();
        // 通过holder修改值
        holder.levelItemView.setText("第"+levelId+"关:"+levelTitle);
        if(bestScore==9999)
            holder.levelBestScore.setText("最佳成绩:暂无");
        else
            holder.levelBestScore.setText("最佳成绩："+bestScore);
        holder.levelDescription.setText(levelDescription);
    }

    // 返回关卡链表的大小
    @Override
    public int getItemCount() {
        return mLevelList.size();
    }
}