package com.example.btl_android.data.repository;

import com.example.btl_android.data.model.Material;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MaterialRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getAllMaterials(OnSuccessListener<List<Material>> listener, OnFailureListener failureListener) {
        db.collection("materials")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Material> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Material material = doc.toObject(Material.class);
                        material.setMaterialID(doc.getId());
                        list.add(material);
                    }
                    listener.onSuccess(list);
                })
                .addOnFailureListener(failureListener);
    }
}
