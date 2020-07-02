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

import java.util.ArrayList;

public class SingleOrderAdapter extends RecyclerView.Adapter<SingleOrderAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<MaterialObject> orderItems;
    private ArrayList<Long> quantities;

    SingleOrderAdapter(Context context, ArrayList<MaterialObject> orderItems, ArrayList<Long> quantities) {
        this.context = context;
        this.orderItems = orderItems;
        this.quantities = quantities;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_order_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView singleMaterialObject;
        private ImageView itemImage;
        private TextView itemName, itemPrice, itemQuantity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            singleMaterialObject = itemView.findViewById(R.id.singleMaterialObject);
            itemImage = itemView.findViewById(R.id.materialObjectPic);
            itemName = itemView.findViewById(R.id.materialObjectName);
            itemPrice = itemView.findViewById(R.id.materialObjectRate);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
        }

        public void bind(int position) {
            final MaterialObject itemObject = orderItems.get(position);

            new FireStoreHandler(context).setImageFromPath(itemObject.getImgPath(), itemImage);
            itemName.setText(itemObject.getItemName());
            itemPrice.setText("Rs. "+itemObject.getBestPrice());
            itemQuantity.setText(String.valueOf(quantities.get(position)));
            singleMaterialObject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent itemDetailsIntent = new Intent(context, ItemDetailsScreen.class);
                    itemDetailsIntent.putExtra("itemData", itemObject);
                    context.startActivity(itemDetailsIntent);
                }
            });
        }
    }
}
