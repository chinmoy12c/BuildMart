package com.example.buildmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class ViewCart extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    private RecyclerView cartRecycler;
    private FireStoreHandler fireStoreHandler;
    private TextView totalAmountText;

    private Integer ActivityRequestCode = 2;
    private String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);

        cartRecycler = findViewById(R.id.cartRecycler);
        fireStoreHandler = new FireStoreHandler(this);
        totalAmountText = findViewById(R.id.totalAmountText);

        fireStoreHandler.getCart(cartRecycler, totalAmountText);
    }

    public void checkoutCart(View view) {
        if (cartRecycler.getAdapter() != null)
            getToken();
    }

    private void getToken() {
        final String orderId = getOrderId();
        final String amount = getAmount();
        RequestQueue queue = Volley.newRequestQueue(this);

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

    private String getAmount() {
        CartAdapter cartAdapter = (CartAdapter) cartRecycler.getAdapter();
        return cartAdapter.getTotalAmount();
    }

    private String getOrderId() {
        return UUID.randomUUID().toString();
    }

    private void startPayment(String txnToken, String orderId, String amount) {
        System.out.println(txnToken);

        String callbackurl = Constants.callbackUrl + orderId;
        PaytmOrder paytmOrder = new PaytmOrder(orderId, Constants.M_ID, txnToken, amount, callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, this);
        transactionManager.setShowPaymentUrl(Constants.paymentUrl);
        transactionManager.startTransaction(this, ActivityRequestCode);
    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        HashMap<String, Object> orderData = getOrderData();
        fireStoreHandler.addOrder(orderData, this);
    }

    @Override
    public void networkNotAvailable() {
        showToast("Network not available");
    }

    @Override
    public void onErrorProceed(String s) {
        showToast("Some error occurred");
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        showToast("Authentication failed");
    }

    @Override
    public void someUIErrorOccurred(String s) {
        showToast("Some UI error occurred");
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        showToast("Error loading page");
    }

    @Override
    public void onBackPressedCancelTransaction() {
        showToast("Order not placed");
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        showToast("Transaction cancelled");
    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityRequestCode && data != null) {
            try {
                JSONObject response = new JSONObject(data.getStringExtra("response"));

                if (response.get("STATUS") != null && response.get("STATUS").equals("TXN_SUCCESS")) {
                    HashMap<String, Object> orderData = getOrderData();
                    fireStoreHandler.addOrder(orderData, this);
                }
                else {
                    showToast("Transaction failed!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showToast("Something went wrong! Try again.");
            }
        }
    }

    private HashMap<String, Object> getOrderData() {
        CartAdapter cartAdapter  = (CartAdapter) cartRecycler.getAdapter();
        ArrayList<MaterialObject> cartItems = cartAdapter.getCartItems();
        ArrayList<Long> quantities = cartAdapter.getQuantities();

        HashMap<String, Object> orderData = new HashMap<>();
        HashMap<String, Long> orderItems = new HashMap<>();

        for (int x = 0; x < cartItems.size(); x++) {
            orderItems.put(cartItems.get(x).getMaterialId(), quantities.get(0));
        }

        orderData.put("orderItems", orderItems);
        orderData.put("username", fireStoreHandler.getUser());
        orderData.put("orderTimestamp", new Date());
        orderData.put("cartId", cartAdapter.getCartId());
        orderData.put("totalAmount", getAmount());

        return orderData;
    }
}
