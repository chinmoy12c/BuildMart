package com.example.buildmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class WorksSectionAdapter extends RecyclerView.Adapter<WorksSectionAdapter.MyViewHolder> {

    private Context context;
    private QuerySnapshot worksList;

    WorksSectionAdapter(Context context, QuerySnapshot queryDocumentSnapshots) {
        this.context = context;
        worksList = queryDocumentSnapshots;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.service_work_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return worksList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView workName, workPrice;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            workName = itemView.findViewById(R.id.workName);
            workPrice = itemView.findViewById(R.id.workPriceTag);
        }

        public void bind(int position) {
            DocumentSnapshot currentWork = worksList.getDocuments().get(position);

            workName.setText((String) currentWork.get("workName"));
            workPrice.setText("Rs. " + currentWork.get("price"));
        }
    }
}
