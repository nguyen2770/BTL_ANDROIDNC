package com.example.btl_android.ui.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_android.R;
import com.example.btl_android.data.model.Reward;
import com.example.btl_android.data.model.UserReward;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyRewardAdapter extends RecyclerView.Adapter<MyRewardAdapter.ViewHolder>{
    private final Context context;
    private List<UserReward> rewardList = new ArrayList<>();

    public MyRewardAdapter(Context context) {
        this.context = context;
    }

    public void setRewardList(List<UserReward> list) {
        this.rewardList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyRewardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_reward, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRewardAdapter.ViewHolder holder, int position) {

        UserReward reward = rewardList.get(position);
        holder.title.setText(reward.getTitle());
        if (reward.getExpiryDate() != null) {
            Date date = reward.getExpiryDate().toDate(); // Chuyển từ Timestamp -> Date
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = sdf.format(date);
            holder.expiryDate.setText("HSD: " + formattedDate);
        } else {
            holder.expiryDate.setText("HSD: Không xác định");
        }


        Glide.with(context)
                .load(reward.getImageUrl())
                .placeholder(R.drawable.loading) // ảnh hiển thị trong lúc loading (tùy chọn)
                .error(R.drawable.voucher)       // ảnh mặc định khi load thất bại
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, expiryDate;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_reward_title);
            expiryDate = itemView.findViewById(R.id.tv_reward_expiry);
            image = itemView.findViewById(R.id.img_reward);
        }
    }
}
