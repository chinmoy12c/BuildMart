package com.example.buildmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryDetails extends AppCompatActivity {

    private RecyclerView materialList;
    private Toolbar toolbar;
    private FireStoreHandler fireStoreHandler;
    private RelativeLayout progressBack;
    private CardView progressLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        progressBack = findViewById(R.id.progressBack);
        progressLogo = findViewById(R.id.progressLogo);
        progressLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_anim));
        progressBack.setVisibility(View.VISIBLE);

        fireStoreHandler = new FireStoreHandler(this);

        materialList = findViewById(R.id.materialList);
        toolbar = findViewById(R.id.categoryToolbar);

        Intent intent = getIntent();
        String category  = intent.getStringExtra("category");
        toolbar.setTitle(category.toUpperCase());
        setSupportActionBar(toolbar);
        fireStoreHandler.getCategoryMaterials(category, materialList, progressBack);
    }
}
