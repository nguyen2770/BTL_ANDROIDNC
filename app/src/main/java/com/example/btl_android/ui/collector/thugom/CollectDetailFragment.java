package com.example.btl_android.ui.collector.thugom;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.R;
import com.example.btl_android.data.model.Material;
import com.example.btl_android.data.model.RecyclableMaterial;
import com.example.btl_android.data.model.ScheduleRequest;
import com.example.btl_android.viewmodel.CollectDetailViewModel;

import java.util.ArrayList;
import java.util.List;

public class CollectDetailFragment extends Fragment {

    private Spinner spinner;
    private EditText edtQuantity;
    private Button btnAdd, btnComplete;
    private RecyclerView recyclerView;

    private CollectDetailViewModel viewModel;
    private MaterialAdapter spinnerAdapter;
    private RecyclableAdapter recyclerAdapter;
    private List<RecyclableMaterial> selectedMaterials = new ArrayList<>();
    private ScheduleRequest scheduleRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect_detail, container, false);

        // Ánh xạ
        spinner = view.findViewById(R.id.spinnerMaterials);
        edtQuantity = view.findViewById(R.id.edtQuantity);
        btnAdd = view.findViewById(R.id.btnAddMaterial);
        btnComplete = view.findViewById(R.id.btnComplete);
        recyclerView = view.findViewById(R.id.recyclerMaterials);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(CollectDetailViewModel.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAdapter = new RecyclableAdapter(selectedMaterials);
        recyclerView.setAdapter(recyclerAdapter);

        if (getArguments() != null) {
            Object obj = getArguments().getSerializable("schedule");
            if (obj != null && obj instanceof ScheduleRequest) {
                scheduleRequest = (ScheduleRequest) obj;
                Log.d("CollectDetailFragment", "ScheduleRequest received: " + scheduleRequest.getId());
            } else {
                Log.e("CollectDetailFragment", "ScheduleRequest is null or wrong type");
            }
        } else {
            Log.e("CollectDetailFragment", "getArguments() is null");
        }


        // Gọi tải dữ liệu vật liệu
        viewModel.loadAllMaterials();

        // Quan sát vật liệu
        viewModel.getMaterialsLiveData().observe(getViewLifecycleOwner(), materials -> {
            spinnerAdapter = new MaterialAdapter(getContext(), materials);
            spinner.setAdapter(spinnerAdapter);
            recyclerAdapter.setMaterialList(materials);
        });

        // Quan sát lỗi
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // Quan sát hoàn thành đơn
        viewModel.getCompletionSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Hoàn thành đơn thành công!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).popBackStack();
            }
        });

        // Xử lý nút thêm vật liệu
        btnAdd.setOnClickListener(v -> {
            Material material = (Material) spinner.getSelectedItem();
            String quantityStr = edtQuantity.getText().toString().trim();
            if (quantityStr.isEmpty()) {
                edtQuantity.setError("Nhập số lượng");
                return;
            }

            double quantity = Double.parseDouble(quantityStr);
            selectedMaterials.add(new RecyclableMaterial(String.valueOf(material.getMaterialID()), quantity));
            recyclerAdapter.notifyDataSetChanged();
            edtQuantity.setText("");
        });

        // Xử lý nút hoàn thành
        btnComplete.setOnClickListener(v -> {
            if (selectedMaterials.isEmpty()) {
                Toast.makeText(getContext(), "Chưa chọn vật liệu nào", Toast.LENGTH_SHORT).show();
                return;
            }


            Log.d( "completeSchedule: ", scheduleRequest.getId());
            Log.d( "completeSchedule: ", scheduleRequest.getUserId() );


            viewModel.completeSchedule(
                    scheduleRequest.getId(),
                    selectedMaterials,
                    scheduleRequest.getUserId()  // lấy userId để cộng điểm
            );
        });

        return view;
    }
}

