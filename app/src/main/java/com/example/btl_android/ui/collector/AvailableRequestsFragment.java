package com.example.btl_android.ui.collector;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.btl_android.R;
import com.example.btl_android.viewmodel.ScheduleViewModel;


public class AvailableRequestsFragment extends Fragment {

    private ScheduleViewModel scheduleViewModel;
    private PendingRequestAdapter adapter;

    public AvailableRequestsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_requests, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerPendingRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PendingRequestAdapter(request -> {
            NavController navController = Navigation.findNavController(view);
            Bundle bundle = new Bundle();
            bundle.putSerializable("request", request);
            navController.navigate(R.id.collector_detail_request, bundle);


        });

        recyclerView.setAdapter(adapter);

        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        scheduleViewModel.getPendingRequests().observe(getViewLifecycleOwner(), adapter::setRequestList);

        return view;
    }
}