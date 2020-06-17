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

public class ServiceSectionAdapter extends RecyclerView.Adapter<ServiceSectionAdapter.MyViewHolder> {

    private Context context;
    private QuerySnapshot serviceSections;
    private FireStoreHandler fireStoreHandler;

    ServiceSectionAdapter(Context context, QuerySnapshot queryDocumentSnapshots) {
        this.context = context;
        serviceSections = queryDocumentSnapshots;
        fireStoreHandler = new FireStoreHandler(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.services_section_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return serviceSections.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView serviceName;
        private ImageView serviceImage;
        private CardView serviceItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceName = itemView.findViewById(R.id.serviceName);
            serviceImage = itemView.findViewById(R.id.serviceImage);
            serviceItem = itemView.findViewById(R.id.serviceItem);
        }

        public void bind(int position) {

            final DocumentSnapshot currentService = serviceSections.getDocuments().get(position);

            serviceName.setText((String) currentService.get("serviceName"));
            fireStoreHandler.setImageFromPath((String)currentService.get("imgName"), serviceImage);

            serviceItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent workListIntent = new Intent(context, WorksSection.class);
                    workListIntent.putExtra("sectionDoc", currentService.getId());
                    context.startActivity(workListIntent);
                }
            });
        }
    }
}
