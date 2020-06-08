package com.example.buildmart;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class FireStoreHandler {

    private Context context;
    private FirebaseFirestore db;
    private final String MATERIAL_COLLECTION = "materials";
    private final String CART_COLLECTION = "userCarts";

    FireStoreHandler(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    void getCategoryMaterials(String category, final RecyclerView materialList) {
        db.collection(MATERIAL_COLLECTION).whereEqualTo("category",category)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                CategoryMaterialListAdapter categoryMaterialListAdapter = new CategoryMaterialListAdapter(context, queryDocumentSnapshots);
                materialList.setAdapter(categoryMaterialListAdapter);
                materialList.setLayoutManager(new LinearLayoutManager(context));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getRecommended(final RecyclerView recommendedList) {
        db.collection(MATERIAL_COLLECTION).whereEqualTo("recommended", "true")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                RecommendedRecyclerAdapter recommendedRecyclerAdapter = new RecommendedRecyclerAdapter(context, queryDocumentSnapshots);
                recommendedList.setAdapter(recommendedRecyclerAdapter);
                recommendedList.setLayoutManager(new GridLayoutManager(context, 2));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getCart(final RecyclerView cartRecycler) {
        db.collection(CART_COLLECTION).whereEqualTo("userIdOwner", "chinmoy12c@gmail.com")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                HashMap<String,Long> matQuantities = (HashMap<String, Long>) queryDocumentSnapshots.getDocuments()
                        .get(0)
                        .get("materialList");

                getCartMaterials(matQuantities, cartRecycler);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Oops! Something went wrong.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getCartMaterials(final HashMap<String, Long> matQuantities, final RecyclerView cartRecycler) {
        final ArrayList<MaterialObject> cartMaterials = new ArrayList<>();
        final ArrayList<Long> quantities = new ArrayList<>();

        for (final String matId : matQuantities.keySet()) {
            db.collection(MATERIAL_COLLECTION).whereEqualTo("matId", matId)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    DocumentSnapshot material = queryDocumentSnapshots.getDocuments().get(0);
                    cartMaterials.add(new MaterialObject(material));
                    quantities.add(matQuantities.get(matId));

                    if (cartMaterials.size() == matQuantities.size()) {
                        CartAdapter cartAdapter = new CartAdapter(context, cartMaterials, quantities);
                        cartRecycler.setLayoutManager(new LinearLayoutManager(context));
                        cartRecycler.setAdapter(cartAdapter);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Oops! Something went wrong.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
