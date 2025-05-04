package com.example.btl_android.ui.collector.thugom;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.btl_android.R;
import com.example.btl_android.data.model.ScheduleRequest;
import com.example.btl_android.ui.collector.PendingRequestAdapter;
import com.example.btl_android.viewmodel.ScheduleViewModel;

import java.util.ArrayList;
import java.util.List;


public class AcceptedRequestsFragment extends Fragment {

    private ScheduleViewModel viewModel;
    private PendingRequestAdapter adapter;
    private Button btnStartCollecting;
    private List<ScheduleRequest> acceptedList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accepted_requests, container, false);

        btnStartCollecting = view.findViewById(R.id.btnStartCollecting);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerAcceptedRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PendingRequestAdapter(request -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("schedule", request);
            Navigation.findNavController(view).navigate(R.id.action_accepted_to_collectDetail, bundle);
        });
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

        // Lấy danh sách các đơn đã được collector hiện tại nhận

        viewModel.getAcceptedRequests().observe(getViewLifecycleOwner(), requests -> {
            acceptedList = requests;
            adapter.setRequestList(requests);
        });

        // Khi bấm "Bắt đầu thu gom" -> sang màn bản đồ
        btnStartCollecting.setOnClickListener(v -> {
            if (acceptedList.isEmpty()) {
                Toast.makeText(getContext(), "Chưa có đơn nào được nhận!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Truyền danh sách qua Fragment bản đồ
            Bundle bundle = new Bundle();
            bundle.putSerializable("accepted_requests", new ArrayList<>(acceptedList));
            Navigation.findNavController(view).navigate(R.id.action_accepted_to_map, bundle);
        });



        return view;
    }
}