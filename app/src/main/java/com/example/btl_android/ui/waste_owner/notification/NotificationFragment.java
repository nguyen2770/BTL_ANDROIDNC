package com.example.btl_android.ui.waste_owner.notification;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.btl_android.R;
import com.example.btl_android.data.model.Notification;
import com.example.btl_android.databinding.FragmentNotificationBinding;
import com.example.btl_android.ui.waste_owner.home.NotificationAdapter;

public class NotificationFragment extends Fragment implements NotificationAdapter.OnNotificationClickListener {

    private FragmentNotificationBinding binding;
    private NotificationViewModel viewModel;
    private NotificationAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup toolbar
        setupToolbar();
        
        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup SwipeRefreshLayout
        setupSwipeRefresh();
        
        // Observe ViewModel
        observeViewModel();

        return root;
    }
    
    private void setupToolbar() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        // Add menu provider
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.notification_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_mark_all_read) {
                    viewModel.markAllAsRead();
                    Toast.makeText(requireContext(), "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());
    }
    
    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerNotifications;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        // Add divider between items
        DividerItemDecoration divider = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
        
        // Setup adapter
        adapter = new NotificationAdapter();
        adapter.setOnNotificationClickListener(this);
        recyclerView.setAdapter(adapter);
    }
    
    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.refreshNotifications();
        });
        
        binding.swipeRefresh.setColorSchemeResources(
            R.color.purple_500,
            R.color.purple_700,
            R.color.teal_200
        );
    }
    
    private void observeViewModel() {
        // Observe notifications
        viewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            adapter.setNotifications(notifications);
            
            // Show empty view if no notifications
            if (notifications.isEmpty()) {
                binding.emptyView.setVisibility(View.VISIBLE);
            } else {
                binding.emptyView.setVisibility(View.GONE);
            }
        });
        
        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.swipeRefresh.setRefreshing(isLoading);
        });
    }

    @Override
    public void onNotificationClick(Notification notification) {
        // Mark notification as read
        if (!notification.isRead()) {
            viewModel.markAsRead(notification.getId());
        }
        
        // Show notification details
        showNotificationDetails(notification);
    }
    
    @Override
    public void onNotificationLongClick(Notification notification) {
        // Show context menu
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        String[] options = {"Đánh dấu là đã đọc", "Xóa thông báo"};
        
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Mark as read
                    viewModel.markAsRead(notification.getId());
                    break;
                case 1: // Delete
                    showDeleteConfirmation(notification);
                    break;
            }
        });
        
        builder.show();
    }
    
    private void showNotificationDetails(Notification notification) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(notification.getTitle());
        builder.setMessage(notification.getMessage());
        builder.setPositiveButton("Đóng", null);
        
        // Add action button based on notification type
        if (notification.getType() != null) {
            switch (notification.getType()) {
                case "SCHEDULE":
                    builder.setNeutralButton("Xem lịch", (dialog, which) -> {
                        // Navigate to schedule screen
                        // TODO: Navigate to schedule screen
                    });
                    break;
                case "NEW_ORDER":
                    builder.setNeutralButton("Xem đơn hàng", (dialog, which) -> {
                        // Navigate to order details
                        // TODO: Navigate to order details
                    });
                    break;
                case "ORDER_STATUS":
                    builder.setNeutralButton("Xem chi tiết", (dialog, which) -> {
                        // Navigate to order status
                        // TODO: Navigate to order status
                    });
                    break;
            }
        }
        
        builder.show();
    }
    
    private void showDeleteConfirmation(Notification notification) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xóa thông báo");
        builder.setMessage("Bạn có chắc chắn muốn xóa thông báo này?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            viewModel.deleteNotification(notification.getId());
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 