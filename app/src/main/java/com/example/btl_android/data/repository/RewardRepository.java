package com.example.btl_android.data.repository;

import com.example.btl_android.data.model.Reward;
import com.example.btl_android.data.model.UserReward;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getAvailableRewards(Callback<List<Reward>> callback) {
        db.collection("Reward")
                .whereEqualTo("status", "Available")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Reward> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Reward r = doc.toObject(Reward.class);
                        if (r != null && r.getNumber() > 0 && !isExpired(r.getExpiryDate())) {
                            list.add(r);
                        }
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getUserRewards(String userId, Callback<List<UserReward>> callback) {
        db.collection("user_rewards")
                .whereEqualTo("userId", userId) // 🔍 lọc theo ID người dùng
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<UserReward> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        UserReward reward = doc.toObject(UserReward.class);
                        list.add(reward);
                    }
                    callback.onSuccess(list); // ✅ trả về danh sách quà đã đổi
                })
                .addOnFailureListener(callback::onFailure);
    }


    private boolean isExpired(Timestamp expiryDate) {
        if (expiryDate == null) return false;
        Date expiry = expiryDate.toDate();
        return expiry.before(new Date());
    }

    public void getRewardById(String rewardId, Callback<Reward> callback) {
        db.collection("Reward")
                .document(rewardId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Reward reward = doc.toObject(Reward.class);
                        callback.onSuccess(reward);
                    } else {
                        callback.onFailure(new Exception("Reward not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void exchangeReward(String userId, Reward reward, int userPoints, Callback<Void> callback) {
        DocumentReference userDoc = db.collection("users").document(userId);
        DocumentReference rewardDoc = db.collection("Reward").document(reward.getRewardID());
        CollectionReference userRewardsRef = db.collection("user_rewards"); // ✅ cấp cao hơn, không nằm trong users

        db.runTransaction(transaction -> {
                    DocumentSnapshot userSnap = transaction.get(userDoc);
                    DocumentSnapshot rewardSnap = transaction.get(rewardDoc);

                    int currentPoints = userSnap.getLong("points").intValue();
                    int rewardCount = rewardSnap.getLong("number").intValue();

                    if (currentPoints < reward.getPointsRequired()) {
                        throw new FirebaseFirestoreException("Không đủ điểm", FirebaseFirestoreException.Code.ABORTED);
                    }

                    if (rewardCount <= 0) {
                        throw new FirebaseFirestoreException("Phần quà đã hết", FirebaseFirestoreException.Code.ABORTED);
                    }

                    // Cập nhật điểm và số lượng
                    transaction.update(userDoc, "points", currentPoints - reward.getPointsRequired());
                    transaction.update(rewardDoc, "number", rewardCount - 1);

                    // Dữ liệu lưu vào user_rewards
                    Map<String, Object> rewardData = new HashMap<>();
                    rewardData.put("userId", userId); // ✅ để lọc sau này
                    rewardData.put("rewardID", reward.getRewardID());
                    rewardData.put("title", reward.getTitle());
                    rewardData.put("description", reward.getDescription());
                    rewardData.put("imageUrl", reward.getImageUrl());
                    rewardData.put("pointsRequired", reward.getPointsRequired());
                    rewardData.put("expiryDate", reward.getExpiryDate());
                    rewardData.put("timestamp", new Timestamp(new Date()));

                    transaction.set(userRewardsRef.document(), rewardData);
                    return null;
                }).addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    public interface Callback<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }
}
