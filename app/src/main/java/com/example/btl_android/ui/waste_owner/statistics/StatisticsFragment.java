package com.example.btl_android.ui.waste_owner.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.data.model.CollectionStatistics;
import com.example.btl_android.databinding.FragmentStatisticsBinding;

import java.text.DecimalFormat;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private StatisticsViewModel statisticsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        
        setupToolbar();
        observeViewModel();
        
        return binding.getRoot();
    }

    private void setupToolbar() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void observeViewModel() {
        statisticsViewModel.getStatistics().observe(getViewLifecycleOwner(), statistics -> {
            if (!statistics.isEmpty()) {
                CollectionStatistics latestStats = statistics.get(0);
                updateStatisticsDisplay(latestStats);
            } else {
                // Hiển thị giá trị mặc định khi không có dữ liệu
                binding.textTotalWeight.setText("0 kg");
                binding.textTotalCollections.setText("0");
                binding.textEfficiency.setText("0%");
            }
        });
    }

    private void updateStatisticsDisplay(CollectionStatistics stats) {
        DecimalFormat df = new DecimalFormat("#,##0.##");
        binding.textTotalWeight.setText(df.format(stats.getTotalWeight()) + " kg");
        binding.textTotalCollections.setText(String.valueOf(stats.getTotalCollections()));
        binding.textEfficiency.setText(df.format(stats.getEfficiency()) + "%");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 