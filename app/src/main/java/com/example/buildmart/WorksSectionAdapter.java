package com.example.buildmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

public class WorksSectionAdapter extends RecyclerView.Adapter<WorksSectionAdapter.MyViewHolder> {

    private static final int ACITIVITY_CODE = 998;
    private Context context;
    private QuerySnapshot worksList;
    private WorksSectionAdapter currentAdapter;
    private WorksSection activity;
    private DocumentSnapshot calledService;
    private RelativeLayout progressBack;

    WorksSectionAdapter(Context context, QuerySnapshot queryDocumentSnapshots, WorksSection activity, RelativeLayout progressBack) {
        this.context = context;
        this.activity = activity;
        this.progressBack = progressBack;
        currentAdapter = this;
        worksList = queryDocumentSnapshots;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.service_work_item, parent, false);
        return new MyViewHolder(view);
    }

    DocumentSnapshot getCalledService() {
        return calledService;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return worksList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void getServiceToken(long advance, int position) {
        final String orderId = getOrderId();
        final String amount = getAmount(position, advance);
        RequestQueue queue = Volley.newRequestQueue(context);

        HashMap<String, String> data = new HashMap<>();
        data.put("MID", Constants.M_ID);
        data.put("ORDER_ID", orderId);
        data.put("AMOUNT", amount);

        JsonObjectRequest txnIdRequest = new JsonObjectRequest(Request.Method.POST, Constants.txnTokenUrl,
                new JSONObject(data)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject body = (JSONObject) response.get("body");
                    System.out.println(body);
                    startPayment(String.valueOf(body.get("txnToken")), orderId, amount);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(txnIdRequest);
    }

    private String getAmount(int position, long advance) {
        long workPrice = worksList.getDocuments().get(position).getLong("price");
        return String.valueOf(Math.floor(workPrice * advance/100.0));
    }

    private String getOrderId() {
        return UUID.randomUUID().toString();
    }

    private void startPayment(String txnToken, String orderId, String amount) {
        System.out.println(txnToken);

        String callbackurl = Constants.callbackUrl + orderId;
        PaytmOrder paytmOrder = new PaytmOrder(orderId, Constants.M_ID, txnToken, amount, callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, activity);
        transactionManager.setShowPaymentUrl(Constants.paymentUrl);
        transactionManager.startTransaction(activity, ACITIVITY_CODE);
        progressBack.setVisibility(View.INVISIBLE);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView workName, workPrice;
        private Button callService;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            workName = itemView.findViewById(R.id.workName);
            workPrice = itemView.findViewById(R.id.workPriceTag);
            callService = itemView.findViewById(R.id.callService);
        }

        public void bind(final int position) {
            final DocumentSnapshot currentWork = worksList.getDocuments().get(position);

            workName.setText((String) currentWork.get("workName"));
            workPrice.setText("Rs. " + currentWork.get("price"));

            callService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBack.setVisibility(View.VISIBLE);
                    calledService = currentWork;
                    new FireStoreHandler(context).getServiceAdvance(position, currentAdapter);
                }
            });
        }
    }
}
