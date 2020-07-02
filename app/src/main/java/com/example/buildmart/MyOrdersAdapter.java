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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder> {

    private Context context;
    private QuerySnapshot orders;

    MyOrdersAdapter(Context context, QuerySnapshot queryDocumentSnapshots) {
        this.context = context;
        this.orders = queryDocumentSnapshots;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_order_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView orderTime, orderAmount;
        private ImageView status;
        private CardView orderItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            orderTime = itemView.findViewById(R.id.orderTime);
            orderAmount = itemView.findViewById(R.id.orderAmount);
            status = itemView.findViewById(R.id.orderStatus);
            orderItem = itemView.findViewById(R.id.orderItem);
        }

        public void bind(int position) {
            final DocumentSnapshot currentOrder = orders.getDocuments().get(position);
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            String strDate = dateFormat.format(currentOrder.getDate("orderTimestamp").getTime());
            orderTime.setText(strDate);
            orderAmount.setText("Rs. " + currentOrder.get("totalAmount"));

            if (currentOrder.get("status").toString().equals("done"))
                status.setColorFilter(ContextCompat.getColor(context, R.color.green));

            orderItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderObject orderObject = new OrderObject(currentOrder);
                    Intent orderDetails = new Intent(context, OrderDetails.class);
                    orderDetails.putExtra("orderDoc", orderObject);
                    orderDetails.putExtra("totalAmount", String.valueOf(currentOrder.get("totalAmount")));
                    context.startActivity(orderDetails);
                }
            });
        }
    }
}
