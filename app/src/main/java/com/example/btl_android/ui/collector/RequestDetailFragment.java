package com.example.btl_android.ui.collector;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl_android.R;
import com.example.btl_android.data.model.ScheduleRequest;
import com.example.btl_android.viewmodel.ScheduleViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestDetailFragment extends Fragment {

    private static final String ARG_REQUEST = "request";
    private ScheduleRequest request;
    private ScheduleViewModel viewModel;

    public static RequestDetailFragment newInstance(ScheduleRequest request) {
        RequestDetailFragment fragment = new RequestDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_REQUEST, request);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            request = (ScheduleRequest) getArguments().getSerializable(ARG_REQUEST);
        }
        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_detail, container, false);

        TextView txtAddress = view.findViewById(R.id.txtDetailAddress);
        TextView txtTimeRange = view.findViewById(R.id.txtDetailTimeRange);
        Button btnAccept = view.findViewById(R.id.btnAcceptRequest);

        txtAddress.setText(request.getAddress());
        txtTimeRange.setText(request.getTimeRange());

        btnAccept.setOnClickListener(v -> {


            viewModel.acceptRequest(request.getId());

            Toast.makeText(getContext(), "Đã nhận đơn!", Toast.LENGTH_SHORT).show();

            // Quay về màn hình trước
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}