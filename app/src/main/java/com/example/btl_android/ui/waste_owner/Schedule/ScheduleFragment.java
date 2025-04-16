package com.example.btl_android.ui.waste_owner.Schedule;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;


import com.example.btl_android.R;
import com.example.btl_android.data.model.SavedAddress;
import com.example.btl_android.data.model.ScheduleRequest;
import com.example.btl_android.viewmodel.AddressViewModel;
import com.example.btl_android.viewmodel.ScheduleViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private ScheduleViewModel scheduleViewModel;
    private AddressViewModel addressViewModel;

    private RecyclerView recyclerViewAddresses;
    private MaterialButton btnConfirm;
    private MaterialButton btnAddAddress;
    private TextView tvTimeRange;
    private MaterialButtonToggleGroup toggleDayGroup;

    private String selectedAddress = "";
    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;
    private boolean isWeekend = false;

    private ScheduleAdapter adapter;
    private final List<SavedAddress> savedAddresses = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        recyclerViewAddresses = view.findViewById(R.id.recyclerViewAddresses);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        tvTimeRange = view.findViewById(R.id.tvTimeRange);
        toggleDayGroup = view.findViewById(R.id.toggleDayGroup);
        btnAddAddress = view.findViewById(R.id.btnAddAddress);

        // Thêm địa chỉ mới
        btnAddAddress.setOnClickListener(v -> {
            // Chuyển sang màn hình chọn địa chỉ
            NavHostFragment.findNavController(ScheduleFragment.this)
                    .navigate(R.id.action_scheduleFragment_to_selectLocationFragment);
        });

        // Chọn ngày (weekend/weekday)
        toggleDayGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            MaterialButton btnWeekdays = group.findViewById(R.id.btnWeekdays);
            MaterialButton btnWeekend = group.findViewById(R.id.btnWeekend);

            if (isChecked) {
                if (checkedId == R.id.btnWeekend) {
                    isWeekend = true;

                    // Weekend selected
                    btnWeekend.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00C853"))); // xanh lá
                    btnWeekend.setTextColor(Color.WHITE);

                    btnWeekdays.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0"))); // xám
                    btnWeekdays.setTextColor(Color.parseColor("#333333"));
                } else if (checkedId == R.id.btnWeekdays) {
                    isWeekend = false;

                    // Weekdays selected
                    btnWeekdays.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00C853"))); // xanh lá
                    btnWeekdays.setTextColor(Color.WHITE);

                    btnWeekend.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0"))); // xám
                    btnWeekend.setTextColor(Color.parseColor("#333333"));
                }
            }
        });


        // Xác nhận lịch thu gom
        btnConfirm.setOnClickListener(v -> {
            if (selectedAddress.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn địa chỉ!", Toast.LENGTH_SHORT).show();
                return;
            }

            String timeRange = tvTimeRange.getText().toString();
            if (timeRange.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn thời gian thu gom!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng ScheduleRequest
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            ScheduleRequest request = new ScheduleRequest(
                    null, // ID sẽ được Firestore tự động tạo
                    currentUserId, // Thay bằng ID người dùng thực tế (lấy từ hệ thống xác thực)
                    selectedAddress,
                    selectedLatitude,
                    selectedLongitude,
                    timeRange,
                    "Pending", // Trạng thái mặc định ban đầu là "Pending"
                    isWeekend
            );


            // Gửi đơn thu gom
            scheduleViewModel.createSchedule(request).observe(getViewLifecycleOwner(), success -> {
                if (success) {
                    Toast.makeText(getContext(), "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Có lỗi xảy ra, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Cài đặt adapter cho RecyclerView
        adapter = new ScheduleAdapter(savedAddresses, new ScheduleAdapter.OnAddressActionListener() {
            @Override
            public void onEdit(SavedAddress address) {
                // Chuyển sang màn hình chọn địa chỉ để sửa
                addressViewModel.deleteAddress(address.getDocumentId(), task -> {
                    if (task.isSuccessful()) {
                        NavHostFragment.findNavController(ScheduleFragment.this)
                                .navigate(R.id.action_scheduleFragment_to_selectLocationFragment);
                    } else {
                        Toast.makeText(getContext(), "Lỗi không sửa được", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDelete(SavedAddress address) {
                addressViewModel.deleteAddress(address.getDocumentId(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                        addressViewModel.fetchSavedAddresses(); // Refresh danh sách sau khi xóa
                    } else {
                        Toast.makeText(getContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSelect(SavedAddress address) {
                // Lấy thông tin địa chỉ đã chọn
                selectedAddress = address.getAddress();
                selectedLatitude = address.getLatitude();
                selectedLongitude = address.getLongitude();
            }
        });

        recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAddresses.setAdapter(adapter);

        observeViewModel();
    }

    private void observeViewModel() {

        addressViewModel.getSavedAddresses().observe(getViewLifecycleOwner(), addresses -> {
            savedAddresses.clear();
            savedAddresses.addAll(addresses);
            adapter.notifyDataSetChanged();
        });

        addressViewModel.fetchSavedAddresses(); // Fetch dữ liệu khi mở fragment
    }
}