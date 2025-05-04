package com.example.btl_android.data.repository;

import android.util.Log;

import com.example.btl_android.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

//    public void addPointsToUser(String uid, int additionalPoints, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
//        db.collection("users").document(uid).get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    User user = documentSnapshot.toObject(User.class);
//                    if (user != null) {
//                        int updatedPoints = user.getPoints() + additionalPoints;
//                        db.collection("users").document(uid)
//                                .update("points", updatedPoints)
//                                .addOnSuccessListener(onSuccess)
//                                .addOnFailureListener(onFailure);
//                    } else {
//                        onFailure.onFailure(new Exception("User not found"));
//                    }
//                })
//                .addOnFailureListener(onFailure);
//    }

    public void addPointsToUser(String userId, double earnedPoints, OnCompleteListener<Void> listener) {
        DocumentReference userRef = db.collection("users").document(userId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(userRef);
            Double currentPoints = snapshot.getDouble("points");
            if (currentPoints == null) currentPoints = 0.0;
            transaction.update(userRef, "points", currentPoints + earnedPoints);
            return(Void) null;
        }).addOnCompleteListener(listener);
    }



}
