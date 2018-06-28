package com.example.gxy.intel.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gxy.intel.MonitorActivity;
import com.example.gxy.intel.MonitorDetailActivity;
import com.example.gxy.intel.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by gxy on 18-3-7.
 */

public class MonitorRVAdapter extends RecyclerView.Adapter<MonitorRVAdapter.ViewHolder> {

    private ArrayList<JSONObject> mData;
    private Context context;

    public MonitorRVAdapter(Context context, ArrayList<JSONObject> mData) {
        this.mData = mData;
        this.context = context;
    }

    public void updateData(ArrayList<JSONObject> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }


    @Override
    public MonitorRVAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MonitorRVAdapter.ViewHolder holder, int position) {
        try {
            holder.tv_item_title.setText(mData.get(position).getString("monitor_name"));
            holder.tv_item_description.setText(mData.get(position).getString("monitor_description"));
            holder.tv_item_time.setText(mData.get(position).getString("time"));
            holder.trend = mData.get(position).getString("trend");
            if (mData.get(position).getString("trend").equals("up")){
                holder.im_trend.setBackgroundResource(R.drawable.ic_trending_up);
            }
            else if(mData.get(position).getString("trend").equals("down")) {
                holder.im_trend.setBackgroundResource(R.drawable.ic_trending_down);
            }
            else {
                holder.im_trend.setBackgroundResource(R.drawable.ic_question);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.btn_monitor_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MonitorDetailActivity.class);
                intent.putExtra("monitor_name", holder.tv_item_title.getText());
                intent.putExtra("monitor_description", holder.tv_item_description.getText());
                intent.putExtra("trend", holder.trend);
                intent.putExtra("time", holder.tv_item_time.getText());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_item_title;
        private TextView tv_item_description;
        private TextView tv_item_time;
        private ImageView im_trend;
        private String trend;
        private Button btn_monitor_detail;

        public ViewHolder(final View itemView) {
            super(itemView);
            tv_item_title = itemView.findViewById(R.id.tv_item_title);
            tv_item_description = itemView.findViewById(R.id.tv_item_description);
            tv_item_time = itemView.findViewById(R.id.tv_item_time);
            im_trend = itemView.findViewById(R.id.im_trend);
            btn_monitor_detail = itemView.findViewById(R.id.btn_monitor_detail);
        }
    }
}
