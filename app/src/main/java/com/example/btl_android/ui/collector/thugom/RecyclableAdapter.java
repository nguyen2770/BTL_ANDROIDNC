package com.example.btl_android.ui.collector.thugom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.R;
import com.example.btl_android.data.model.Material;
import com.example.btl_android.data.model.RecyclableMaterial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclableAdapter extends RecyclerView.Adapter<RecyclableAdapter.ViewHolder> {

    private List<RecyclableMaterial> recyclableMaterials;
    private Map<String, Material> materialMap;

    public RecyclableAdapter(List<RecyclableMaterial> recyclableMaterials) {
        this.recyclableMaterials = recyclableMaterials;
        this.materialMap = new HashMap<>();
    }

    // Gọi từ Fragment sau khi đã load danh sách vật liệu đầy đủ
    public void setMaterialList(List<Material> materials) {
        for (Material m : materials) {
            materialMap.put(String.valueOf(m.getMaterialID()), m);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclable_material, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclableMaterial rm = recyclableMaterials.get(position);
        Material material = materialMap.get(rm.getId());

        if (material != null) {
            double point = rm.getQuantity() * material.getPricePerKg();
            holder.txtName.setText(material.getName());
            holder.txtQuantity.setText("Số lượng: " + rm.getQuantity() + " kg");
            holder.txtPoint.setText("Điểm: " + point);
        }

        holder.btnDelete.setOnClickListener(v -> {
            recyclableMaterials.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, recyclableMaterials.size());
        });
    }

    @Override
    public int getItemCount() {
        return recyclableMaterials.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtQuantity, txtPoint;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtMaterialName);
            txtQuantity = itemView.findViewById(R.id.txtMaterialQuantity);
            txtPoint = itemView.findViewById(R.id.txtMaterialPoint);
            btnDelete = itemView.findViewById(R.id.btnDeleteMaterial);
        }
    }
}
