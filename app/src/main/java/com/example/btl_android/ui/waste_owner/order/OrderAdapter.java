package com.example.btl_android.ui.waste_owner.order;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.R;
import com.example.btl_android.data.model.ScheduleRequest;
import com.example.btl_android.viewmodel.ScheduleViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<ScheduleRequest> orderList = new ArrayList<>();
    private final Context context;
    private final ScheduleViewModel viewModel;
    private final LifecycleOwner lifecycleOwner;

    private final OnOrderClickListener listener;

    public OrderAdapter(Context context, ScheduleViewModel viewModel, LifecycleOwner lifecycleOwner, OnOrderClickListener listener) {
        this.context = context;
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.listener = listener;
    }

    public void setData(List<ScheduleRequest> list) {
        this.orderList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        ScheduleRequest order = orderList.get(position);

        holder.tvTime.setText(formatDateTime(order.getCreatedAt()));
        holder.tvTitle.setText("Nhà riêng");
        holder.tvAddress.setText(order.getAddress());

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xoá đơn")
                    .setMessage("Bạn có chắc chắn muốn xoá đơn này không?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        viewModel.deleteScheduleById(order.getId())
                                .observe(lifecycleOwner, success -> {
                                    if (Boolean.TRUE.equals(success)) {
                                        Toast.makeText(context, "Đã xoá đơn", Toast.LENGTH_SHORT).show();
                                        orderList.remove(position);
                                        notifyItemRemoved(position);
                                    } else {
                                        Toast.makeText(context, "Xoá thất bại!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));

    }

    public static String formatDateTime(String isoDateTime) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            Date date = isoFormat.parse(isoDateTime);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
            return displayFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return isoDateTime;
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddress, tvTime, tvTitle;
        ImageButton btnDelete;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
    public interface OnOrderClickListener {
        void onOrderClick(ScheduleRequest order);
    }

}


