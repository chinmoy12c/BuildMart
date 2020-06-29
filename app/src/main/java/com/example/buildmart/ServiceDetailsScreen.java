package com.example.buildmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

public class ServiceDetailsScreen extends AppCompatActivity {

    private RecyclerView serviceSectionList;
    private Toolbar toolbar;
    private FireStoreHandler fireStoreHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details_screen);

        toolbar = findViewById(R.id.servicesToolbar);
        toolbar.setTitle("Services Section");
        setSupportActionBar(toolbar);

        serviceSectionList = findViewById(R.id.servicesSectionList);

        fireStoreHandler = new FireStoreHandler(this);

        Intent currentIntent = getIntent();
        String serviceSection = currentIntent.getExtras().getString("serviceSection");

        fireStoreHandler.getServiceSections(serviceSectionList, serviceSection);
    }

    public void postServiceRequirement(View view) {
        startActivity(new Intent(this, PostUnknownService.class));
    }
}
