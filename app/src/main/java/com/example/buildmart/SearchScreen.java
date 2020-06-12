package com.example.buildmart;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchScreen extends Fragment {

    private TextView searchBar;
    private RecyclerView searchListRecycler;
    private FireStoreHandler fireStoreHandler;

    public SearchScreen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_screen, container, false);

        fireStoreHandler = new FireStoreHandler(getContext());

        searchBar = rootView.findViewById(R.id.searchBar);
        searchListRecycler = rootView.findViewById(R.id.searchListRecycler);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = searchBar.getText().toString().toUpperCase();

                if (searchText.length() < 3) {
                    searchListRecycler.setAdapter(null);
                    return;
                }

                fireStoreHandler.searchItem(searchListRecycler, searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return rootView;
    }

}
