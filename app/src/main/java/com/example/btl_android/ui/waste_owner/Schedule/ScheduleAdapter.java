package com.example.btl_android.ui.waste_owner.Schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.R;
import com.example.btl_android.data.model.SavedAddress;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.AddressViewHolder> {

    public interface OnAddressActionListener {
        void onEdit(SavedAddress address);
        void onDelete(SavedAddress address);
        void onSelect(SavedAddress address);
    }

    private final List<SavedAddress> addressList;
    private final OnAddressActionListener listener;
    private int selectedPosition = -1;

    public ScheduleAdapter(List<SavedAddress> addressList, OnAddressActionListener listener) {
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address_card, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        SavedAddress address = addressList.get(position);

        holder.tvAddressType.setText(address.getLabel());
        holder.tvAddressDetail.setText(address.getAddress());

        boolean isSelected = selectedPosition == position;

        // Highlight khi chọn
        holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(
                isSelected ? R.color.teal_700 : android.R.color.white));

        holder.tvAddressType.setTextColor(holder.itemView.getResources().getColor(
                isSelected ? android.R.color.white : R.color.teal_700));
        holder.tvAddressDetail.setTextColor(holder.itemView.getResources().getColor(
                isSelected ? android.R.color.white : android.R.color.black));

        holder.btnEdit.setColorFilter(holder.itemView.getResources().getColor(
                isSelected ? android.R.color.white : R.color.blue));
        holder.btnDelete.setColorFilter(holder.itemView.getResources().getColor(
                isSelected ? android.R.color.white : R.color.red));

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            listener.onSelect(address);
            notifyDataSetChanged();
        });

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(address));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(address));
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddressType, tvAddressDetail;
        ImageView btnEdit, btnDelete;
        CardView cardView;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddressType = itemView.findViewById(R.id.tvAddressType);
            tvAddressDetail = itemView.findViewById(R.id.tvAddressDetail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            cardView = itemView.findViewById(R.id.cardViewAddress);
        }
    }
}
