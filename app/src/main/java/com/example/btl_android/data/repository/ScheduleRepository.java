package com.example.btl_android.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.btl_android.data.model.RecyclableMaterial;
import com.example.btl_android.data.model.ScheduleRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference requestsRef = firestore.collection("schedules");
    // Tạo mới một đơn thu gom
    public LiveData<Boolean> createSchedule(ScheduleRequest request) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        requestsRef
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    String generatedId = documentReference.getId();
                    // Cập nhật trường id sau khi thêm document
                    documentReference.update("id", generatedId)
                            .addOnSuccessListener(aVoid -> result.setValue(true))
                            .addOnFailureListener(e -> result.setValue(false));
                })
                .addOnFailureListener(e -> {
                    result.setValue(false);
                });
        return result;
    }

    // Lấy danh sách đơn theo trạng thái và userId
    public LiveData<List<ScheduleRequest>> getSchedulesByStatusAndUserId(String status, String userId) {
        MutableLiveData<List<ScheduleRequest>> schedulesLiveData = new MutableLiveData<>();

        requestsRef
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
        requestsRef
                .document(scheduleId)
                .delete()
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));
        return result;
    }

    public LiveData<List<ScheduleRequest>> getPendingRequests() {
        MutableLiveData<List<ScheduleRequest>> data = new MutableLiveData<>();

        requestsRef
                .whereEqualTo("status", "Pending")
                .whereEqualTo("collectorId", null)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    List<ScheduleRequest> list = new ArrayList<>();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            ScheduleRequest request = doc.toObject(ScheduleRequest.class);
                            if (request != null) {
                                list.add(request);
                            }
                        }
                    }
                    data.setValue(list);
                });

        return data;
    }

    public void acceptRequest(String requestId, String collectorId) {

        requestsRef
                .document(requestId)
                .update(
                        "collectorId", collectorId,
                        "status", "InProgress"
                );
    }

    public LiveData<List<ScheduleRequest>> getAcceptedRequests(String collectorId) {
        MutableLiveData<List<ScheduleRequest>> liveData = new MutableLiveData<>();

        requestsRef
                .whereEqualTo("collectorId", collectorId)
                .whereEqualTo("status", "InProgress") // chỉ lấy các đơn đã được nhận
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    List<ScheduleRequest> list = new ArrayList<>();
                    for (DocumentSnapshot doc : value) {
                        ScheduleRequest request = doc.toObject(ScheduleRequest.class);
                        request.setId(doc.getId());
                        list.add(request);
                    }
                    liveData.setValue(list);
                });

        return liveData;
    }

    public void markRequestAsCompleted(String requestId, List<RecyclableMaterial> materials, OnCompleteListener<Void> listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "completed");
        updates.put("materials", materials); // materials: List<RecyclableMaterial>
        updates.put("completedAt", new Timestamp(new Date()));

        requestsRef
                .document(requestId)
                .update(updates)
                .addOnCompleteListener(listener);
    }




}
