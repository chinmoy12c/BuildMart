package com.example.buildmart;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ViewOrders extends AppCompatActivity {

    private RecyclerView ordersView;
    private RelativeLayout progressBack;
    private CardView progressLogo;
    private FireStoreHandler fireStoreHandler;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        ordersView = findViewById(R.id.myOrdersRecycler);
        progressBack = findViewById(R.id.progressBack);
        progressLogo = findViewById(R.id.progressLogo);
        toolbar = findViewById(R.id.myOrdersToolbar);

        toolbar.setTitle("Your orders");
        setSupportActionBar(toolbar);

        progressLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_anim));
        progressBack.setVisibility(View.VISIBLE);

        fireStoreHandler = new FireStoreHandler(this);
        fireStoreHandler.getMyOrders(ordersView, progressBack);
    }
}