package com.example.buildmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

public class CategoryDetails extends AppCompatActivity {

    private RecyclerView materialList;
    private Toolbar toolbar;
    private FireStoreHandler fireStoreHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        fireStoreHandler = new FireStoreHandler(this);

        materialList = findViewById(R.id.materialList);
        toolbar = findViewById(R.id.categoryToolbar);

        Intent intent = getIntent();
        String category  = intent.getStringExtra("category");
        toolbar.setTitle(category.toUpperCase());
        setSupportActionBar(toolbar);
        fireStoreHandler.getCategoryMaterials(category, materialList);
    }
}
