package com.example.buildmart;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ServicesScreen extends Fragment implements View.OnClickListener {


    private CardView plumber, electrician, painter, carpenter;

    public ServicesScreen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_services_screen, container, false);

        plumber = rootView.findViewById(R.id.plumbingService);
        electrician = rootView.findViewById(R.id.electricalService);
        painter = rootView.findViewById(R.id.painterService);
        carpenter = rootView.findViewById(R.id.carpenterService);

        plumber.setOnClickListener(this);
        electrician.setOnClickListener(this);
        painter.setOnClickListener(this);
        carpenter.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Intent serviceSectionIntent = new Intent(getContext(), ServiceDetailsScreen.class);

        switch (v.getId()) {

            case R.id.plumbingService:
                serviceSectionIntent.putExtra("serviceSection", "plumber");
                break;

            case R.id.carpenterService:
                serviceSectionIntent.putExtra("serviceSection", "carpenter");
                break;

            case R.id.painterService:
                serviceSectionIntent.putExtra("serviceSection", "painter");
                break;

            case R.id.electricalService:
                serviceSectionIntent.putExtra("serviceSection", "electrician");
                break;
        }

        startActivity(serviceSectionIntent);
    }
}
