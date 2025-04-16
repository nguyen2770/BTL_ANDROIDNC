package com.example.btl_android.data.repository;

import androidx.annotation.NonNull;

import com.example.btl_android.data.model.SavedAddress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddressRepository {
    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;

    public interface OnAddressesFetchedListener {
        void onFetched(List<SavedAddress> addresses);
    }

    public AddressRepository() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public void saveAddress(SavedAddress address, OnCompleteListener<Void> listener) {
        String uid = auth.getCurrentUser().getUid();
        String docId = firestore.collection("users").document(uid)
                .collection("addresses").document().getId();

        address.setDocumentId(docId);
        address.setCreatedAt(new Timestamp(new Date()));

        firestore.collection("users")
                .document(uid)
                .collection("addresses")
                .document(docId)
                .set(address)
                .addOnCompleteListener(listener);
    }

    public void deleteAddress(String documentId, OnCompleteListener<Void> listener) {
        String uid = auth.getCurrentUser().getUid();
        firestore.collection("users")
                .document(uid)
                .collection("addresses")
                .document(documentId)
                .delete()
                .addOnCompleteListener(listener);
    }

    public void updateAddress(SavedAddress address, OnCompleteListener<Void> listener) {
        String uid = auth.getCurrentUser().getUid();
        firestore.collection("users")
                .document(uid)
                .collection("addresses")
                .document(address.getDocumentId())
                .set(address)
                .addOnCompleteListener(listener);
    }

    public void getSavedAddresses(OnAddressesFetchedListener listener) {
        String uid = auth.getCurrentUser().getUid();
        CollectionReference addressRef = firestore.collection("users")
                .document(uid)
                .collection("addresses");

        addressRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<SavedAddress> addressList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    SavedAddress address = doc.toObject(SavedAddress.class);
                    addressList.add(address);
                }
                listener.onFetched(addressList);
            } else {
                listener.onFetched(new ArrayList<>());
            }
        });
    }
}
