package com.example.btl_android.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.btl_android.data.model.ScheduleRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ScheduleRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    // Tạo mới một đơn thu gom
    public LiveData<Boolean> createSchedule(ScheduleRequest request) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        firestore.collection("schedules")
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    result.setValue(true);
                })
                .addOnFailureListener(e -> {
                    result.setValue(false);
                });
        return result;
    }

    // Lấy danh sách đơn theo trạng thái và userId
    public LiveData<List<ScheduleRequest>> getSchedulesByStatusAndUserId(String status, String userId) {
        MutableLiveData<List<ScheduleRequest>> schedulesLiveData = new MutableLiveData<>();

        firestore.collection("schedules")
                .whereEqualTo("status", status)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ScheduleRequest> schedules = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ScheduleRequest schedule = doc.toObject(ScheduleRequest.class);
                        schedule.setId(doc.getId()); // set lại ID lấy từ document
                        schedules.add(schedule);
                    }
                    schedulesLiveData.setValue(schedules);
                })
                .addOnFailureListener(e -> schedulesLiveData.setValue(null));

        return schedulesLiveData;
    }

    public LiveData<Boolean> deleteScheduleById(String scheduleId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        firestore.collection("schedules")
                .document(scheduleId)
                .delete()
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));
        return result;
    }

}
