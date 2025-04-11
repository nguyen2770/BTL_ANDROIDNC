package com.example.btl_android.ui.waste_owner.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.btl_android.data.model.CollectionStatistics;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatisticsViewModel extends ViewModel {
    private final MutableLiveData<List<CollectionStatistics>> statistics;
    private final MutableLiveData<List<Entry>> timeSeriesData;
    private final MutableLiveData<List<PieEntry>> wasteDistributionData;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public StatisticsViewModel() {
        statistics = new MutableLiveData<>(new ArrayList<>());
        timeSeriesData = new MutableLiveData<>(new ArrayList<>());
        wasteDistributionData = new MutableLiveData<>(new ArrayList<>());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        
        // Lắng nghe sự thay đổi trạng thái đăng nhập
        auth.addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                loadStatistics(); // Load thống kê khi đã đăng nhập
            } else {
                statistics.setValue(new ArrayList<>()); // Xóa thống kê khi đăng xuất
                timeSeriesData.setValue(new ArrayList<>());
                wasteDistributionData.setValue(new ArrayList<>());
            }
        });
    }

    public LiveData<List<CollectionStatistics>> getStatistics() {
        return statistics;
    }

    public LiveData<List<Entry>> getTimeSeriesData() {
        return timeSeriesData;
    }

    public LiveData<List<PieEntry>> getWasteDistributionData() {
        return wasteDistributionData;
    }

    /**
     * Tải lại dữ liệu thống kê từ Firestore
     */
    public void refreshData() {
        loadStatistics();
    }

    private void loadStatistics() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            statistics.setValue(new ArrayList<>());
            return;
        }

        // Lấy thống kê từ collection orders của người dùng hiện tại
        db.collection("orders")
                .whereEqualTo("userId", currentUser.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        statistics.setValue(new ArrayList<>());
                        return;
                    }

                    double totalWeight = 0;
                    int totalCollections = value.size();
                    Map<String, Double> wasteTypeMap = new HashMap<>();
                    Map<Long, Double> timeSeriesMap = new TreeMap<>();

                    // Tính tổng khối lượng từ các đơn hàng
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Double weight = doc.getDouble("weight");
                        String wasteType = doc.getString("wasteType");
                        Long timestamp = doc.getLong("timestamp");
                        
                        if (weight != null) {
                            totalWeight += weight;
                            
                            // Cập nhật phân bố loại rác
                            if (wasteType != null) {
                                wasteTypeMap.merge(wasteType, weight, Double::sum);
                            }

                            // Cập nhật dữ liệu theo thời gian
                            if (timestamp != null) {
                                timeSeriesMap.merge(timestamp, weight, Double::sum);
                            }
                        }
                    }

                    // Tạo đối tượng thống kê
                    CollectionStatistics stats = new CollectionStatistics(currentUser.getUid());
                    stats.setTotalWeight(totalWeight);
                    stats.setTotalCollections(totalCollections);
                    stats.setWasteTypeDistribution(wasteTypeMap);
                    stats.setTimeSeriesData(timeSeriesMap);
                    
                    List<CollectionStatistics> statsList = new ArrayList<>();
                    statsList.add(stats);
                    statistics.setValue(statsList);

                    // Cập nhật dữ liệu biểu đồ theo thời gian
                    List<Entry> timeEntries = new ArrayList<>();
                    int index = 0;
                    for (Map.Entry<Long, Double> entry : timeSeriesMap.entrySet()) {
                        timeEntries.add(new Entry(index++, entry.getValue().floatValue()));
                    }
                    timeSeriesData.setValue(timeEntries);

                    // Cập nhật dữ liệu biểu đồ phân bố loại rác
                    List<PieEntry> pieEntries = new ArrayList<>();
                    for (Map.Entry<String, Double> entry : wasteTypeMap.entrySet()) {
                        pieEntries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
                    }
                    wasteDistributionData.setValue(pieEntries);
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Hủy đăng ký AuthStateListener khi ViewModel bị hủy
        auth.removeAuthStateListener(firebaseAuth -> {});
    }

    // Cập nhật thống kê khi có đơn hàng mới
    public void updateStatistics(double weight, String wasteType) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        // Lấy thống kê hiện tại
        List<CollectionStatistics> currentStats = statistics.getValue();
        if (currentStats == null || currentStats.isEmpty()) {
            CollectionStatistics stats = new CollectionStatistics(currentUser.getUid());
            stats.setTotalWeight(weight);
            stats.setTotalCollections(1);
            stats.addWasteType(wasteType, weight);
            stats.addTimeSeriesData(System.currentTimeMillis(), weight);
            List<CollectionStatistics> newStats = new ArrayList<>();
            newStats.add(stats);
            statistics.setValue(newStats);
        } else {
            CollectionStatistics stats = currentStats.get(0);
            stats.setTotalWeight(stats.getTotalWeight() + weight);
            stats.setTotalCollections(stats.getTotalCollections() + 1);
            stats.addWasteType(wasteType, weight);
            stats.addTimeSeriesData(System.currentTimeMillis(), weight);
            statistics.setValue(currentStats);
        }
    }
} 