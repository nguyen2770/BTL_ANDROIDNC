package com.example.btl_android.ui.waste_owner.Material;

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
import com.example.btl_android.data.model.Material;

import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>{
    private List<Material> materialList;
    private Context context;
    private OnMaterialClickListener listener;

    public interface OnMaterialClickListener {
        void onMaterialClick(Material material);
    }

    public MaterialAdapter(Context context, OnMaterialClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setMaterialList(List<Material> materialList) {
        this.materialList = materialList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.material_item, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        Material m = materialList.get(position);
        holder.txtName.setText(m.getName());
        holder.txtPrice.setText(String.format("%.0fđ", m.getPricePerKg()));
        Glide.with(context).load(m.getImageUrl()).into(holder.imgMaterial);

        holder.itemView.setOnClickListener(v -> listener.onMaterialClick(m));
    }

    @Override
    public int getItemCount() {
        return materialList == null ? 0 : materialList.size();
    }

    static class MaterialViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMaterial;
        TextView txtName, txtPrice;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMaterial = itemView.findViewById(R.id.imgMaterial);
            txtName = itemView.findViewById(R.id.txtMaterialName);
            txtPrice = itemView.findViewById(R.id.txtMaterialPrice);
        }
    }
}
