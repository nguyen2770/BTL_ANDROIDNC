package com.example.btl_android.utils;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DataSeeder {
    private static final String TAG = "DataSeeder";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final Random random = new Random();

    // Mảng các loại rác
    private static final String[] WASTE_TYPES = {
            "Nhựa",
            "Giấy",
            "Kim loại",
            "Thủy tinh",
            "Rác hữu cơ",
            "Rác điện tử"
    };

    /**
     * Tạo dữ liệu mẫu cho thống kê
     * @param numOrders Số lượng đơn hàng cần tạo
     * @param daysBack Số ngày trong quá khứ để bắt đầu tạo dữ liệu
     * @param listener Callback khi hoàn tất
     */
    public static void seedOrderData(int numOrders, int daysBack, OnSeederCompleteListener listener) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "Không thể tạo dữ liệu: Người dùng chưa đăng nhập");
            if (listener != null) {
                listener.onComplete(false);
            }
            return;
        }

        String userId = currentUser.getUid();
        
        // Tạo danh sách các đơn hàng
        List<Map<String, Object>> orders = new ArrayList<>();
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -daysBack); // Lùi về quá khứ
        
        for (int i = 0; i < numOrders; i++) {
            Map<String, Object> order = new HashMap<>();
            
            // Tạo thời gian ngẫu nhiên trong khoảng từ ngày bắt đầu đến hiện tại
            Calendar orderTime = (Calendar) calendar.clone();
            orderTime.add(Calendar.DAY_OF_YEAR, random.nextInt(daysBack));
            orderTime.add(Calendar.HOUR_OF_DAY, random.nextInt(24));
            orderTime.add(Calendar.MINUTE, random.nextInt(60));
            
            // Tạo khối lượng ngẫu nhiên từ 0.5 đến 20.0 kg
            double weight = 0.5 + random.nextDouble() * 19.5;
            weight = Math.round(weight * 10.0) / 10.0; // Làm tròn 1 chữ số thập phân
            
            // Chọn loại rác ngẫu nhiên
            String wasteType = WASTE_TYPES[random.nextInt(WASTE_TYPES.length)];
            
            // Tạo đơn hàng
            order.put("userId", userId);
            order.put("timestamp", orderTime.getTimeInMillis());
            order.put("weight", weight);
            order.put("wasteType", wasteType);
            order.put("status", "Completed");
            order.put("createdAt", orderTime.getTimeInMillis());
            
            orders.add(order);
        }
        
        // Thêm các đơn hàng vào Firestore
        List<Task<Void>> tasks = new ArrayList<>();
        for (Map<String, Object> order : orders) {
            tasks.add(db.collection("orders").document().set(order));
        }
        
        // Đợi tất cả các tác vụ hoàn thành
        Tasks.whenAllComplete(tasks)
                .addOnSuccessListener(taskSnapshots -> {
                    Log.d(TAG, "Đã tạo thành công " + numOrders + " đơn hàng mẫu");
                    if (listener != null) {
                        listener.onComplete(true);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi tạo dữ liệu mẫu: " + e.getMessage());
                    if (listener != null) {
                        listener.onComplete(false);
                    }
                });
    }
    
    // Interface callback
    public interface OnSeederCompleteListener {
        void onComplete(boolean success);
    }
} 