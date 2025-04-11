package com.example.btl_android.ui.waste_owner;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.btl_android.R;
import com.example.btl_android.data.model.Material;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MaterialDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MaterialDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView imvLogo;
    private TextView tvName, tvDescription;

    public MaterialDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MaterialDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MaterialDetailFragment newInstance(String param1, String param2) {
        MaterialDetailFragment fragment = new MaterialDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_material_detail, container, false);
        imvLogo = view.findViewById(R.id.imgv_logo);
        tvName = view.findViewById(R.id.tv_name);
        tvDescription = view.findViewById(R.id.tv_description);

        Bundle args = getArguments();
        if (args != null) {
            String materialJson = args.getString("material");
            Material material = new Gson().fromJson(materialJson, Material.class);

            if (material != null) {
                tvName.setText(material.getName());
                tvDescription.setText(material.getDescription());
                Glide.with(this)
                        .load(material.getImageUrl())
                        .placeholder(R.drawable.baseline_account_circle_24)
                        .into(imvLogo);
            }
        }

        return view;


    }
}