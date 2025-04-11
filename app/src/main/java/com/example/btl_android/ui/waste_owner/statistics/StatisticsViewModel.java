package com.example.btl_android.ui.waste_owner.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.btl_android.data.model.CollectionStatistics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class StatisticsViewModel extends ViewModel {
    private final MutableLiveData<List<CollectionStatistics>> statistics;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public StatisticsViewModel() {
        statistics = new MutableLiveData<>(new ArrayList<>());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        
        // Lắng nghe sự thay đổi trạng thái đăng nhập
        auth.addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                loadStatistics(); // Load thống kê khi đã đăng nhập
            } else {
                statistics.setValue(new ArrayList<>()); // Xóa thống kê khi đăng xuất
            }
        });
    }

    public LiveData<List<CollectionStatistics>> getStatistics() {
        return statistics;
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

                    // Tính tổng khối lượng từ các đơn hàng
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Double weight = doc.getDouble("weight");
                        if (weight != null) {
                            totalWeight += weight;
                        }
                    }

                    // Tạo đối tượng thống kê
                    CollectionStatistics stats = new CollectionStatistics(currentUser.getUid());
                    stats.setTotalWeight(totalWeight);
                    stats.setTotalCollections(totalCollections);
                    
                    List<CollectionStatistics> statsList = new ArrayList<>();
                    statsList.add(stats);
                    statistics.setValue(statsList);
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Hủy đăng ký AuthStateListener khi ViewModel bị hủy
        auth.removeAuthStateListener(firebaseAuth -> {});
    }

    // Cập nhật thống kê khi có đơn hàng mới
    public void updateStatistics(double weight) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        // Lấy thống kê hiện tại
        List<CollectionStatistics> currentStats = statistics.getValue();
        if (currentStats == null || currentStats.isEmpty()) {
            CollectionStatistics stats = new CollectionStatistics(currentUser.getUid());
            stats.setTotalWeight(weight);
            stats.setTotalCollections(1);
            List<CollectionStatistics> newStats = new ArrayList<>();
            newStats.add(stats);
            statistics.setValue(newStats);
        } else {
            CollectionStatistics stats = currentStats.get(0);
            stats.setTotalWeight(stats.getTotalWeight() + weight);
            stats.setTotalCollections(stats.getTotalCollections() + 1);
            statistics.setValue(currentStats);
        }
    }
} 