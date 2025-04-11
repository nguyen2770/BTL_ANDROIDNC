package com.example.btl_android.ui.waste_owner.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.btl_android.data.model.Notification;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Notification>> notifications;
    private final FirebaseFirestore db;

    public HomeViewModel() {
        notifications = new MutableLiveData<>(new ArrayList<>());
        db = FirebaseFirestore.getInstance();
        loadNotifications();
        getFCMToken();
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    // Save token to Firestore or your server
                    saveFCMToken(token);
                });
    }

    private void saveFCMToken(String token) {
        // Save token to Firestore under user's document
        db.collection("users")
                .document("current_user_id") // Replace with actual user ID
                .update("fcmToken", token);
    }

    public LiveData<List<Notification>> getNotifications() {
        return notifications;
    }

    private void loadNotifications() {
        db.collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        List<Notification> notificationList = new ArrayList<>();
                        value.forEach(doc -> {
                            Notification notification = doc.toObject(Notification.class);
                            notification.setId(doc.getId());
                            notificationList.add(notification);
                        });
                        notifications.setValue(notificationList);
                    }
                });
    }

    public void addNotification(Notification notification) {
        db.collection("notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    notification.setId(documentReference.getId());
                });
    }

    public void updateNotification(Notification notification) {
        if (notification.getId() != null) {
            db.collection("notifications")
                    .document(notification.getId())
                    .set(notification);
        }
    }
}