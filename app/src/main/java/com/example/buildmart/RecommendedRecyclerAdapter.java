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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class RecommendedRecyclerAdapter extends RecyclerView.Adapter<RecommendedRecyclerAdapter.MyViewHolder> {

    private Context context;
    private QuerySnapshot materials;

    RecommendedRecyclerAdapter(Context context, QuerySnapshot queryDocumentSnapshots){
        this.context = context;
        this.materials = queryDocumentSnapshots;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recommended_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView materialCard;
        private TextView itemPrice, itemName;
        private ImageView itemImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            materialCard = itemView.findViewById(R.id.recommendedCard);
            itemPrice = itemView.findViewById(R.id.priceTagRecommended);
            itemName = itemView.findViewById(R.id.itemName);
            itemImage = itemView.findViewById(R.id.itemImage);
        }

        public void bind(int position) {

            DocumentSnapshot singleItem = materials.getDocuments().get(position);
            final MaterialObject materialObject = new MaterialObject(singleItem);

            itemName.setText(materialObject.getItemName());
            itemPrice.setText("Rs. " + materialObject.getBestPrice() + "/kg");
            Picasso.get().load(materialObject.getImgPath()).into(itemImage);

            materialCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ItemDetailsScreen.class);
                    intent.putExtra("itemData", materialObject);
                    context.startActivity(intent);
                }
            });
        }
    }
}
