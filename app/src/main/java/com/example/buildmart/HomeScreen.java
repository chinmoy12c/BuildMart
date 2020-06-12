package com.example.buildmart;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeScreen extends Fragment implements View.OnClickListener {

    private RecyclerView dealsOfDayList, recommendedList;
    private CarouselView carouselView;
    private TrendingRecyclerAdapter trendingRecyclerAdapter;
    private RecommendedRecyclerAdapter recommendedRecyclerAdapter;
    private ImageView buildingCategory, electricalCategory, tilesCategory, plumbingCategory, paintsCategory, sanitryCategory;
    private FireStoreHandler fireStoreHandler;


    int pic[] = {R.raw.construction1,R.raw.construction2,R.raw.construction3,R.raw.construction4};

    public HomeScreen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_home_screen, container, false);


        dealsOfDayList = rootView.findViewById(R.id.dealsOfDayList);
        carouselView  = rootView.findViewById(R.id.previewCarousel);
        recommendedList = rootView.findViewById(R.id.recommendedList);
        buildingCategory = rootView.findViewById(R.id.buildingWork);
        electricalCategory = rootView.findViewById(R.id.electricWork);
        tilesCategory = rootView.findViewById(R.id.tilesWork);
        plumbingCategory = rootView.findViewById(R.id.plumbingWork);
        paintsCategory = rootView.findViewById(R.id.paintsWork);
        sanitryCategory = rootView.findViewById(R.id.sanitaryWork);

        buildingCategory.setOnClickListener(this);
        electricalCategory.setOnClickListener(this);
        tilesCategory.setOnClickListener(this);
        plumbingCategory.setOnClickListener(this);
        paintsCategory.setOnClickListener(this);
        sanitryCategory.setOnClickListener(this);


        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(pic[position]);
            }
        };
        carouselView.setPageCount(pic.length);
        carouselView.setImageListener(imageListener);

        fireStoreHandler = new FireStoreHandler(getContext());

        trendingRecyclerAdapter = new TrendingRecyclerAdapter(getContext());
        dealsOfDayList.setLayoutManager(new GridLayoutManager(getContext(),3));
        dealsOfDayList.setAdapter(trendingRecyclerAdapter);

        fireStoreHandler.getRecommended(recommendedList);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Intent materialListIntent = new Intent(getContext(), CategoryDetails.class);

        switch (v.getId()){

            case R.id.buildingWork:
                materialListIntent.putExtra("category","building");
                startActivity(materialListIntent);
                break;
            case R.id.electricWork:
                materialListIntent.putExtra("category","electrical");
                startActivity(materialListIntent);
                break;
            case R.id.tilesWork:
                materialListIntent.putExtra("category","tiles");
                startActivity(materialListIntent);
                break;
            case R.id.plumbingWork:
                materialListIntent.putExtra("category","plumbing");
                startActivity(materialListIntent);
                break;
            case R.id.paintsWork:
                materialListIntent.putExtra("category","paints");
                startActivity(materialListIntent);
                break;
            case R.id.sanitaryWork:
                materialListIntent.putExtra("category","sanitary");
                startActivity(materialListIntent);
                break;
        }

    }
}
