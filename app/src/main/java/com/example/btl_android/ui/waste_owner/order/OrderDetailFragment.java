package com.example.btl_android.ui.waste_owner.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.btl_android.R;
import com.example.btl_android.data.model.ScheduleRequest;
import com.example.btl_android.databinding.FragmentOrderDetailBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetailFragment extends Fragment {

    private FragmentOrderDetailBinding binding;
    private ScheduleRequest schedule;

    public static OrderDetailFragment newInstance(ScheduleRequest request) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("order", request); // Model phải implements Serializable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            schedule = (ScheduleRequest) getArguments().getSerializable("order");

            if (schedule != null) {
                binding.tvPickupTime.setText(schedule.getTimeRange());
                binding.tvOrderStatus.setText(schedule.getStatus());
                binding.tvLocationName.setText("Nhà Riêng");
                binding.tvAddress.setText(schedule.getAddress());
                showMapWithLocation(schedule.getLatitude(), schedule.getLongitude());
            }
        }
    }
    private void showMapWithLocation(double lat, double lng) {
        SupportMapFragment mapFragment = new SupportMapFragment();

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.mapViewContainer, mapFragment)
                .commit();

        mapFragment.getMapAsync(googleMap -> {
            LatLng location = new LatLng(lat, lng);
            googleMap.addMarker(new MarkerOptions().position(location).title("Vị trí thu gom"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        });
    }

}