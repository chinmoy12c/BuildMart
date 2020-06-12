package com.example.buildmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemDetailsScreen extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private Toolbar toolbar;
    private CardView itemQuantityCard;
    private NumberPicker itemQuantitySelector;
    private MaterialObject itemObject;
    private ImageView itemImage;
    private TextView itemDescription, quantity1, quantity2, quantity3, rate1, rate2, rate3;
    private ArrayList<Long> quantities;
    private ArrayList<Long> rates;
    private FireStoreHandler fireStoreHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details_screen);

        toolbar = findViewById(R.id.detailsToolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(this);

        itemImage = findViewById(R.id.materialImage);
        itemDescription = findViewById(R.id.materialDescription);
        quantity1 = findViewById(R.id.quantity1);
        quantity2 = findViewById(R.id.quantity2);
        quantity3 = findViewById(R.id.quantity3);
        rate1 = findViewById(R.id.rate1);
        rate2 = findViewById(R.id.rate2);
        rate3 = findViewById(R.id.rate3);
        itemQuantityCard = findViewById(R.id.quantitySelectorCard);
        itemQuantitySelector = findViewById(R.id.itemQuantitySelector);

        itemQuantitySelector.setMaxValue(10000);
        itemQuantitySelector.setMinValue(1);


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

        fireStoreHandler = new FireStoreHandler(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public void addItemToCart(View view) {
        itemQuantitySelector.clearFocus();
        fireStoreHandler.addItemToCart(itemObject.getMaterialId(), itemQuantitySelector.getValue());
    }

    public void showSelector(View view) {
        itemQuantityCard.setVisibility(View.VISIBLE);
    }

    public void hideSelector(View view) {
        itemQuantityCard.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.viewCart:
                startActivity(new Intent(this, ViewCart.class));
                break;
        }
        return false;
    }
}
