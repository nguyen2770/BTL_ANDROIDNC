package com.example.btl_android.utils;

import android.util.Log;

import com.example.btl_android.data.model.Material;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    
    /**
     * Tạo dữ liệu mẫu cho vật liệu tái chế
     * @param listener Callback khi hoàn tất
     */
    public static void seedMaterialData(OnSeederCompleteListener listener) {
        // Kiểm tra xem đã có dữ liệu vật liệu chưa
        db.collection("materials").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Log.d(TAG, "Đã có dữ liệu vật liệu, không cần tạo lại");
                        if (listener != null) {
                            listener.onComplete(true);
                        }
                        return;
                    }
                    
                    // Tạo danh sách các vật liệu mẫu
                    List<Material> materials = new ArrayList<>();
                    
                    // Nhựa
                    materials.add(new Material(
                            null,
                            "Chai nhựa PET",
                            "Chai nhựa trong suốt dùng đựng nước, nước ngọt, dầu ăn...",
                            "Nhựa",
                            4000,
                            "https://cdn.dariu.vn/wp-content/uploads/2023/10/ve-sinh-chai-nhua-1.jpg"
                    ));
                    
                    materials.add(new Material(
                            null,
                            "Nhựa HDPE",
                            "Nhựa đục, cứng, thường dùng làm chai sữa, chai dầu gội, nước rửa chén...",
                            "Nhựa",
                            5000,
                            "https://cdn.tgdd.vn/Files/2022/06/10/1438609/phan-biet-cac-loai-nhua-co-the-tai-che-va-khong-the-tai-che-202206101429111103.jpg"
                    ));
                    
                    // Giấy
                    materials.add(new Material(
                            null,
                            "Giấy báo, tạp chí",
                            "Giấy báo, tạp chí cũ, sách vở không dùng...",
                            "Giấy",
                            3000,
                            "https://cdn.tgdd.vn/Files/2021/06/01/1356264/10-meo-tai-che-giay-bao-cu-thanh-do-dung-huu-ich-202106011526309633.jpg"
                    ));
                    
                    materials.add(new Material(
                            null,
                            "Thùng carton",
                            "Thùng carton đựng hàng hóa, sản phẩm...",
                            "Giấy",
                            2500,
                            "https://phongthuyhongphuc.vn/pic/News/images/tin-tuc-1/mo-thay-thung-carton-1.jpg"
                    ));
                    
                    // Kim loại
                    materials.add(new Material(
                            null,
                            "Lon nhôm",
                            "Lon bia, nước ngọt, đồ uống có ga...",
                            "Kim loại",
                            20000,
                            "https://cdn.tgdd.vn/Files/2022/04/01/1423683/ve-sinh-va-bao-quan-lon-bia-sao-cho-dung-202204011531422141.jpg"
                    ));
                    
                    materials.add(new Material(
                            null,
                            "Sắt vụn",
                            "Các loại sắt vụn, thép không gỉ, kim loại tái chế...",
                            "Kim loại",
                            7000,
                            "https://thephuc.com/wp-content/uploads/2020/06/sat-thep-phe-lieu.jpg"
                    ));
                    
                    // Thủy tinh
                    materials.add(new Material(
                            null,
                            "Chai thủy tinh",
                            "Chai rượu, nước ngọt, lọ thủy tinh đựng gia vị...",
                            "Thủy tinh",
                            2000,
                            "https://cdn.tgdd.vn/Files/2021/07/30/1372078/huong-dan-tai-che-chai-thuy-tinh-thong-dung-ma-ban-nhat-dinh-phai-biet-202107302344534050.jpg"
                    ));
                    
                    // Rác điện tử
                    materials.add(new Material(
                            null,
                            "Pin, ắc quy",
                            "Pin các loại đã qua sử dụng...",
                            "Rác điện tử",
                            15000,
                            "https://i-vnexpress.vnecdn.net/2021/09/22/pin-dien-thoai-8587-1632296824.jpg"
                    ));
                    
                    materials.add(new Material(
                            null,
                            "Linh kiện điện tử",
                            "Bo mạch, dây điện, linh kiện từ thiết bị điện tử...",
                            "Rác điện tử",
                            30000,
                            "https://image.thanhnien.vn/w2048/Uploaded/2022/puqgfdmzs-co/2021_11_10/rac-thai-dien-tu-3194.jpeg"
                    ));
                    
                    // Thêm các vật liệu vào Firestore
                    List<Task<Void>> tasks = new ArrayList<>();
                    for (Material material : materials) {
                        DocumentReference docRef = db.collection("materials").document(); // tạo docRef
                        material.setMaterialID(docRef.getId()); // gán ID tự sinh vào model
                        tasks.add(docRef.set(material)); // lưu vào Firestore
                    }

                    // Đợi tất cả các tác vụ hoàn thành
                    Tasks.whenAllComplete(tasks)
                            .addOnSuccessListener(taskSnapshots -> {
                                Log.d(TAG, "Đã tạo thành công " + materials.size() + " vật liệu mẫu");
                                if (listener != null) {
                                    listener.onComplete(true);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Lỗi khi tạo dữ liệu vật liệu mẫu: " + e.getMessage());
                                if (listener != null) {
                                    listener.onComplete(false);
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi kiểm tra collection materials: " + e.getMessage());
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