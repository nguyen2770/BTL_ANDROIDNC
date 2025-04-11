package com.example.btl_android.ui.waste_owner.statistics;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.data.model.CollectionStatistics;
import com.example.btl_android.databinding.FragmentStatisticsBinding;
import com.example.btl_android.utils.DataSeeder;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private StatisticsViewModel statisticsViewModel;
    private LineChart lineChart;
    private PieChart pieChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        
        setupToolbar();
        setupCharts();
        observeViewModel();
        setupSeedDataButton();
        
        return binding.getRoot();
    }

    private void setupToolbar() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setupCharts() {
        // Cấu hình biểu đồ đường
        lineChart = binding.chartTimeSeries;
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        
        // Cấu hình biểu đồ tròn
        pieChart = binding.chartWasteDistribution;
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
    }

    private void setupSeedDataButton() {
        binding.btnSeedData.setOnClickListener(v -> {
            showSeedDataDialog();
        });
    }

    private void showSeedDataDialog() {
        final String[] options = {"10 đơn hàng - 1 tuần", "30 đơn hàng - 1 tháng", "100 đơn hàng - 3 tháng"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn loại dữ liệu mẫu");
        builder.setItems(options, (dialog, which) -> {
            int numOrders;
            int daysBack;
            
            switch (which) {
                case 0:
                    numOrders = 10;
                    daysBack = 7;
                    break;
                case 1:
                    numOrders = 30;
                    daysBack = 30;
                    break;
                case 2:
                    numOrders = 100;
                    daysBack = 90;
                    break;
                default:
                    numOrders = 10;
                    daysBack = 7;
            }
            
            seedData(numOrders, daysBack);
        });
        
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void seedData(int numOrders, int daysBack) {
        // Hiển thị dialog tiến trình
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Đang tạo dữ liệu mẫu...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        DataSeeder.seedOrderData(numOrders, daysBack, success -> {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            
            if (success) {
                Toast.makeText(requireContext(), 
                        "Đã tạo thành công " + numOrders + " đơn hàng mẫu", 
                        Toast.LENGTH_SHORT).show();
                
                // Tải lại dữ liệu
                statisticsViewModel.refreshData();
            } else {
                Toast.makeText(requireContext(), 
                        "Không thể tạo dữ liệu mẫu. Vui lòng thử lại.", 
                        Toast.LENGTH_SHORT).show();
            }
        });
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

        // Cập nhật biểu đồ theo thời gian
        statisticsViewModel.getTimeSeriesData().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null && !entries.isEmpty()) {
                LineDataSet dataSet = new LineDataSet(entries, "Khối lượng rác (kg)");
                dataSet.setColor(Color.BLUE);
                dataSet.setCircleColor(Color.BLUE);
                dataSet.setLineWidth(2f);
                dataSet.setCircleRadius(4f);
                dataSet.setDrawCircleHole(false);
                dataSet.setValueTextSize(10f);
                dataSet.setDrawFilled(true);
                dataSet.setFillColor(Color.BLUE);
                dataSet.setFillAlpha(30);

                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);
                lineChart.invalidate();
            } else {
                lineChart.clear();
                lineChart.invalidate();
            }
        });

        // Cập nhật biểu đồ phân bố loại rác
        statisticsViewModel.getWasteDistributionData().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null && !entries.isEmpty()) {
                PieDataSet dataSet = new PieDataSet(entries, "Loại rác");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                dataSet.setValueTextSize(12f);
                dataSet.setValueTextColor(Color.WHITE);

                PieData pieData = new PieData(dataSet);
                pieChart.setData(pieData);
                pieChart.invalidate();
            } else {
                pieChart.clear();
                pieChart.invalidate();
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