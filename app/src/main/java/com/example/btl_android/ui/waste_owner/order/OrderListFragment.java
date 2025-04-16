package com.example.btl_android.ui.waste_owner.order;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.btl_android.R;
import com.example.btl_android.data.model.ScheduleRequest;
import com.example.btl_android.viewmodel.ScheduleViewModel;

import java.util.List;

public class OrderListFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private String status;

    private ScheduleViewModel scheduleViewModel;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;

    public static OrderListFragment newInstance(String status) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        // ViewModel
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

        // RecyclerView setup
        recyclerView = view.findViewById(R.id.recyclerOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter khởi tạo một lần và gán vào RecyclerView
        orderAdapter = new OrderAdapter(requireContext(), scheduleViewModel, getViewLifecycleOwner(), order -> {
            // Mở fragment chi tiết khi click vào item
            Bundle bundle = new Bundle();
            bundle.putSerializable("order", order);
            NavHostFragment.findNavController(OrderListFragment.this)
                    .navigate(R.id.detailSchedule, bundle);

        });
        recyclerView.setAdapter(orderAdapter);

        // Observe danh sách đơn theo trạng thái
        scheduleViewModel.getSchedulesByStatus(status).observe(getViewLifecycleOwner(), this::updateUI);

        return view;
    }

    private void updateUI(List<ScheduleRequest> schedules) {
        orderAdapter.setData(schedules);
    }
}
