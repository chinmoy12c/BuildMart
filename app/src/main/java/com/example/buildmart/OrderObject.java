package com.example.buildmart;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;

public class OrderObject implements Serializable {

    private HashMap<String, Long> materialList;
    private long totalAmount;

    OrderObject(DocumentSnapshot orderDoc) {
        materialList = (HashMap<String, Long>) orderDoc.get("orderItems");
        totalAmount = orderDoc.getLong("totalAmount");
    }

    long getTotalAmount() {
        return totalAmount;
    }

    HashMap<String, Long> getMaterialList() {
        return materialList;
    }
}
