package com.example.btl_android.ui.waste_owner.notification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.btl_android.data.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class NotificationViewModel extends ViewModel {
    private final MutableLiveData<List<Notification>> notifications;
    private final MutableLiveData<Boolean> isLoading;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public NotificationViewModel() {
        notifications = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        
        loadNotifications();
    }

    public LiveData<List<Notification>> getNotifications() {
        return notifications;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void refreshNotifications() {
        loadNotifications();
    }

    private void loadNotifications() {
        isLoading.setValue(true);
        
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            notifications.setValue(new ArrayList<>());
            isLoading.setValue(false);
            return;
        }
        
        db.collection("notifications")
                .whereEqualTo("userId", currentUser.getUid()) // Filter by current user
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    isLoading.setValue(false);
                    
                    if (error != null) {
                        // Handle error
                        return;
                    }
                    
                    if (value != null) {
                        List<Notification> notificationList = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Notification notification = doc.toObject(Notification.class);
                            if (notification != null) {
                                notification.setId(doc.getId());
                                notificationList.add(notification);
                            }
                        }
                        notifications.setValue(notificationList);
                    }
                });
    }

    public void markAsRead(String notificationId) {
        if (notificationId == null || notificationId.isEmpty()) {
            return;
        }
        
        db.collection("notifications")
                .document(notificationId)
                .update("read", true)
                .addOnSuccessListener(aVoid -> {
                    // Update successful, load notifications again
                    refreshNotifications();
                });
    }

    public void markAllAsRead() {
        List<Notification> currentNotifications = notifications.getValue();
        if (currentNotifications == null || currentNotifications.isEmpty()) {
            return;
        }
        
        for (Notification notification : currentNotifications) {
            if (!notification.isRead() && notification.getId() != null) {
                db.collection("notifications")
                        .document(notification.getId())
                        .update("read", true);
            }
        }
        
        // Refresh after a delay to ensure all writes have completed
        new android.os.Handler().postDelayed(this::refreshNotifications, 500);
    }

    public void deleteNotification(String notificationId) {
        if (notificationId == null || notificationId.isEmpty()) {
            return;
        }
        
        db.collection("notifications")
                .document(notificationId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Delete successful, refresh notifications
                    refreshNotifications();
                });
    }
} 