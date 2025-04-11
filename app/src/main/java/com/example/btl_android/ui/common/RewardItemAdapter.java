package com.example.btl_android.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_android.R;
import com.example.btl_android.data.model.Reward;

import java.util.ArrayList;
import java.util.List;

public class RewardItemAdapter extends RecyclerView.Adapter<RewardItemAdapter.ViewHolder> {

    private final Context context;
    private final OnRewardExchangeListener exchangeListener;
    private List<Reward> rewardList = new ArrayList<>();

    // Constructor có listener
    public RewardItemAdapter(Context context, OnRewardExchangeListener listener) {
        this.context = context;
        this.exchangeListener = listener;
    }

    public void setRewardList(List<Reward> list) {
        this.rewardList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RewardItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reward, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardItemAdapter.ViewHolder holder, int position) {
        Reward reward = rewardList.get(position);
        holder.title.setText(reward.getTitle());
        holder.points.setText(reward.getPointsRequired() + " điểm");

        Glide.with(context)
                .load(reward.getImageUrl())
                .placeholder(R.drawable.loading)
                .error(R.drawable.voucher)
                .into(holder.image);

        // Gọi callback khi nhấn nút "Đổi quà"
        holder.btnExchange.setOnClickListener(v -> {
            if (exchangeListener != null) {
                exchangeListener.onExchangeClick(reward);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, points;
        ImageView image;
        Button btnExchange;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_reward_title);
            points = itemView.findViewById(R.id.tv_reward_points);
            image = itemView.findViewById(R.id.img_reward);
            btnExchange = itemView.findViewById(R.id.btn_exchange);
        }
    }

    // Interface callback để xử lý sự kiện "Đổi quà"
    public interface OnRewardExchangeListener {
        void onExchangeClick(Reward reward);
    }
}
