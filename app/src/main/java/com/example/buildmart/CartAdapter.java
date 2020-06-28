package com.example.buildmart;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<MaterialObject> cartItems;
    private ArrayList<Long> quantities;
    private String totalAmount;
    private String cartId;

    CartAdapter(Context context, ArrayList<MaterialObject> cartItems, ArrayList<Long> quantities, TextView totalAmountText, String cartId) {
        this.context = context;
        this.cartItems = cartItems;
        this.quantities = quantities;
        this.cartId = cartId;
        totalAmount = getAmount();
        totalAmountText.setText("Rs. " + totalAmount);
    }

    ArrayList<MaterialObject> getCartItems(){
        return cartItems;
    }

    ArrayList<Long> getQuantities() {
        return quantities;
    }

    String getTotalAmount() {
        return totalAmount;
    }

    String getCartId() {
        return cartId;
    }

    private String getAmount() {
        long totalAmount = 0;

        for (int x = 0; x<cartItems.size(); x++) {
            MaterialObject currentObject = cartItems.get(x);
            long quantity = quantities.get(0);
            long ratePerUnit;

            if (quantity <= currentObject.getQuantities().get(0))
                ratePerUnit = currentObject.getRates().get(0);
            else if (quantity <= currentObject.getQuantities().get(1))
                ratePerUnit = currentObject.getRates().get(1);
            else
                ratePerUnit = currentObject.getRates().get(2);

            totalAmount += quantity * ratePerUnit;
        }
        return String.valueOf(totalAmount);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView itemImage;
        private TextView itemName, itemQuantity, itemPriceTag;
        private CardView cartItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.cartItemImage);
            itemName = itemView.findViewById(R.id.cartItemName);
            itemQuantity = itemView.findViewById(R.id.cartItemQuantity);
            itemPriceTag = itemView.findViewById(R.id.cartPriceTag);
            cartItem = itemView.findViewById(R.id.cartItemCard);
        }

        public void bind(int position) {
            final MaterialObject currentObject = cartItems.get(position);

            Picasso.get().load(currentObject.getImgPath()).into(itemImage);
            itemName.setText(currentObject.getItemName());
            itemQuantity.setText(String.valueOf(quantities.get(position)));
            itemPriceTag.setText("Rs. " + currentObject.getBestPrice());
            cartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailsIntent = new Intent(context, ItemDetailsScreen.class);
                    detailsIntent.putExtra("itemData", currentObject);
                    context.startActivity(detailsIntent);
                }
            });
        }
    }
}
