package com.example.btl_android.ui.collector;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.R;
import com.example.btl_android.data.model.ScheduleRequest;

import java.util.ArrayList;
import java.util.List;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestAdapter.RequestViewHolder> {

    private List<ScheduleRequest> requestList = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ScheduleRequest request);
    }

    public PendingRequestAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setRequestList(List<ScheduleRequest> list) {
        requestList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        ScheduleRequest request = requestList.get(position);
        holder.bind(request, listener);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView txtAddress, txtTime;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtTime = itemView.findViewById(R.id.txtTimeRange);
        }

        void bind(ScheduleRequest request, OnItemClickListener listener) {
            txtAddress.setText(request.getAddress());
            txtTime.setText(request.getTimeRange());

            itemView.setOnClickListener(v -> listener.onItemClick(request));
        }
    }
}

