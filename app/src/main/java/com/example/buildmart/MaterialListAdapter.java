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

public class MaterialListAdapter extends RecyclerView.Adapter<MaterialListAdapter.MyViewHolder> {

    private Context context;
    private QuerySnapshot materials;

    MaterialListAdapter(Context context, QuerySnapshot queryDocumentSnapshots){
        this.context = context;
        this.materials = queryDocumentSnapshots;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.material_object, parent, false);
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

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView singleMaterialObject;
        private ImageView itemImage;
        private TextView itemName, itemPrice;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            singleMaterialObject = itemView.findViewById(R.id.singleMaterialObject);
            itemImage = itemView.findViewById(R.id.materialObjectPic);
            itemName = itemView.findViewById(R.id.materialObjectName);
            itemPrice = itemView.findViewById(R.id.materialObjectRate);
        }

        public void bind(int position) {

            final DocumentSnapshot itemData = materials.getDocuments().get(position);
            final MaterialObject itemObject = new MaterialObject(itemData);

            new FireStoreHandler(context).setImageFromPath(itemObject.getImgPath(), itemImage);
            itemName.setText(itemObject.getItemName());
            itemPrice.setText("Rs."+itemObject.getBestPrice());
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
