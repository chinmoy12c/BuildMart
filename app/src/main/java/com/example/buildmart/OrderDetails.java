package com.example.buildmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class OrderDetails extends AppCompatActivity {

    private RecyclerView materialList;
    private CardView progressLogo;
    private RelativeLayout progressBack;
    private FireStoreHandler fireStoreHandler;
    private Toolbar toolbar;
    private TextView totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        materialList = findViewById(R.id.materialList);
        progressLogo = findViewById(R.id.progressLogo);
        progressBack = findViewById(R.id.progressBack);
        toolbar = findViewById(R.id.orderDetailsToolbar);
        totalAmount = findViewById(R.id.totalAmountText);

        toolbar.setTitle("Order details");
        setSupportActionBar(toolbar);

        progressLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_anim));
        progressBack.setVisibility(View.VISIBLE);

        Intent currentIntent = getIntent();
        OrderObject orderObject = (OrderObject) currentIntent.getSerializableExtra("orderDoc");
        String totalAmountText = "Rs. " + currentIntent.getStringExtra("totalAmount");

        totalAmount.setText(totalAmountText);

        fireStoreHandler = new FireStoreHandler(this);
        fireStoreHandler.getOrderItems(materialList, orderObject, progressBack);
    }
}