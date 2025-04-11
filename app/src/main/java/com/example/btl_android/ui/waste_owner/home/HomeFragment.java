package com.example.btl_android.ui.waste_owner.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.btl_android.data.model.Notification;
import com.example.btl_android.databinding.FragmentHomeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class HomeFragment extends Fragment implements NotificationAdapter.OnNotificationClickListener {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private NotificationAdapter notificationAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        setupRecyclerView();
        setupClickListeners();
        observeViewModel();

        return root;
    }

    private void setupRecyclerView() {
        notificationAdapter = new NotificationAdapter();
        notificationAdapter.setOnNotificationClickListener(this);
        binding.recyclerNotifications.setAdapter(notificationAdapter);
        binding.recyclerNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupClickListeners() {
        binding.fabAddNotification.setOnClickListener(v -> showAddNotificationDialog());
    }

    private void observeViewModel() {
        homeViewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            notificationAdapter.setNotifications(notifications);
        });
    }

    private void showAddNotificationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Thêm thông báo nhắc nhở")
                .setItems(new String[]{"Lịch thu gom", "Đơn hàng mới", "Cập nhật trạng thái"}, (dialog, which) -> {
                    String type;
                    String title;
                    String message;
                    switch (which) {
                        case 0:
                            type = "SCHEDULE";
                            title = "Lịch thu gom";
                            message = "Có lịch thu gom mới";
                            break;
                        case 1:
                            type = "NEW_ORDER";
                            title = "Đơn hàng mới";
                            message = "Bạn có đơn hàng mới";
                            break;
                        case 2:
                            type = "ORDER_STATUS";
                            title = "Cập nhật trạng thái";
                            message = "Trạng thái đơn hàng đã được cập nhật";
                            break;
                        default:
                            return;
                    }
                    homeViewModel.addNotification(new Notification(title, message, type));
                })
                .show();
    }

    @Override
    public void onNotificationClick(Notification notification) {
        notification.setRead(true);
        homeViewModel.updateNotification(notification);
        // TODO: Navigate to appropriate screen based on notification type
        Toast.makeText(requireContext(), notification.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}