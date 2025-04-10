package com.example.btl_android.data.repository;

import android.util.Log;

import com.example.btl_android.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getUserData(String uid, OnSuccessListener<User> onSuccess, OnFailureListener onFailure) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    Log.d( "getUserData: ", user.getName());
                    onSuccess.onSuccess(user);
                })
                .addOnFailureListener(onFailure);
    }
}
