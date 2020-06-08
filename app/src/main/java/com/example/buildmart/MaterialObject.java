package com.example.buildmart;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class MaterialObject implements Serializable {

    private String itemName;
    private String bestPrice;
    private String imgPath;
    private String description;
    private String materialId;
    private ArrayList<Long> quantities;
    private ArrayList<Long> rates;

    MaterialObject(DocumentSnapshot itemSnapshot) {
        setDetails(itemSnapshot);
    }

    private void setDetails(DocumentSnapshot itemSnapshot) {
        itemName = (String) itemSnapshot.get("name");
        bestPrice = (String) itemSnapshot.get("bestRate");
        description = (String) itemSnapshot.get("description");
        imgPath = (String) itemSnapshot.get("imgPath");
        materialId = (String) itemSnapshot.get("matId");
        quantities = (ArrayList<Long>) itemSnapshot.get("quantities");
        rates = (ArrayList<Long>) itemSnapshot.get("rates");
    }

    public String getItemName() {
        return itemName;
    }

    public ArrayList<Long> getQuantities() {
        return quantities;
    }

    public ArrayList<Long> getRates() {
        return rates;
    }

    public String getBestPrice() {
        return bestPrice;
    }

    public String getDescription() {
        return description;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getMaterialId() {
        return materialId;
    }
}
