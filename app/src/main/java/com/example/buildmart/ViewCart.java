package com.example.buildmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class ViewCart extends AppCompatActivity {

    private RecyclerView cartRecycler;
    private FireStoreHandler fireStoreHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);

        cartRecycler = findViewById(R.id.cartRecycler);
        fireStoreHandler = new FireStoreHandler(this);

        fireStoreHandler.getCart(cartRecycler);
    }
}
