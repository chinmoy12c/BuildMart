package com.example.buildmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemDetailsScreen extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialObject itemObject;
    private ImageView itemImage;
    private TextView itemDescription, quantity1, quantity2, quantity3, rate1, rate2, rate3;
    private ArrayList<Long> quantities;
    private ArrayList<Long> rates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details_screen);

        toolbar = findViewById(R.id.detailsToolbar);
        setSupportActionBar(toolbar);

        itemImage = findViewById(R.id.materialImage);
        itemDescription = findViewById(R.id.materialDescription);
        quantity1 = findViewById(R.id.quantity1);
        quantity2 = findViewById(R.id.quantity2);
        quantity3 = findViewById(R.id.quantity3);
        rate1 = findViewById(R.id.rate1);
        rate2 = findViewById(R.id.rate2);
        rate3 = findViewById(R.id.rate3);


        Intent intent = getIntent();
        itemObject = (MaterialObject) intent.getExtras().getSerializable("itemData");
        quantities = itemObject.getQuantities();
        rates = itemObject.getRates();

        Picasso.get().load(itemObject.getImgPath()).into(itemImage);
        itemDescription.setText(itemObject.getDescription());
        quantity1.setText("Upto " + quantities.get(0));
        quantity2.setText("Upto " + quantities.get(1));
        quantity3.setText("Upto " + quantities.get(2));
        rate1.setText(String.valueOf(rates.get(0)));
        rate2.setText(String.valueOf(rates.get(1)));
        rate3.setText(String.valueOf(rates.get(2)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}
