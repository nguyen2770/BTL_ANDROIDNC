package com.example.btl_android.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.btl_android.data.model.Material;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MaterialViewModel extends ViewModel {

    private MutableLiveData<List<Material>> materialList = new MutableLiveData<>();

    public LiveData<List<Material>> getMaterials() {
        if (materialList.getValue() == null) {
            loadMaterials();
        }
        return materialList;
    }

    private void loadMaterials() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("materials")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Material> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Material m = doc.toObject(Material.class);
                        Log.d("MaterialDebug", "Name: " + m.getName() + ", Image URL: " + m.getImageUrl());

                        list.add(m);
                    }
                    materialList.setValue(list);
                });
    }
}
