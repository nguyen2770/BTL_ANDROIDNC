package com.example.btl_android.ui.collector.thugom;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.btl_android.data.model.Material;

import java.util.List;

public class MaterialAdapter extends ArrayAdapter<Material> {
    public MaterialAdapter(Context context, List<Material> materials) {
        super(context, android.R.layout.simple_spinner_item, materials);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(getContext());
        Material m = getItem(position);
        textView.setText(m.getName() + " - " + m.getPricePerKg() + "đ/kg");
        textView.setPadding(16, 16, 16, 16);
        return textView;
    }
}
