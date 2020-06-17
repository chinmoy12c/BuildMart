package com.example.buildmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

public class WorksSection extends AppCompatActivity {

    private RecyclerView worksList;
    private Toolbar toolbar;
    private FireStoreHandler fireStoreHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_section_works);

        worksList = findViewById(R.id.worksSectionList);
        toolbar = findViewById(R.id.workSectionToolbar);

        fireStoreHandler = new FireStoreHandler(this);

        toolbar.setTitle("Works List");
        setSupportActionBar(toolbar);

        Intent currentIntent = getIntent();

        fireStoreHandler.getWorks(worksList, currentIntent.getExtras().getString("sectionDoc"));
    }
}
