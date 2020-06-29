package com.example.buildmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

public class WorksSection extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    private static final int ACTIVITY_CODE = 998;
    private RecyclerView worksList;
    private Toolbar toolbar;
    private FireStoreHandler fireStoreHandler;
    private RelativeLayout progressBack;
    private CardView progressLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_section_works);

        worksList = findViewById(R.id.worksSectionList);
        toolbar = findViewById(R.id.workSectionToolbar);
        progressBack = findViewById(R.id.progressBack);
        progressLogo = findViewById(R.id.progressLogo);

        progressLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_anim));
        progressBack.setVisibility(View.VISIBLE);

        fireStoreHandler = new FireStoreHandler(this);

        toolbar.setTitle("Works List");
        setSupportActionBar(toolbar);

        Intent currentIntent = getIntent();

        fireStoreHandler.getWorks(worksList, currentIntent.getExtras().getString("sectionDoc"), this, progressBack);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == ACTIVITY_CODE) {
            try {
                JSONObject response = new JSONObject(data.getStringExtra("response"));

                if (response.get("STATUS") != null && response.get("STATUS").equals("TXN_SUCCESS")) {
                    HashMap<String, Object> serviceData = getServiceData();
                    fireStoreHandler.addServiceCall(serviceData);
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

    HashMap<String, Object> getServiceData() {
        WorksSectionAdapter worksSectionAdapter = (WorksSectionAdapter) worksList.getAdapter();
        DocumentSnapshot currentWork = worksSectionAdapter.getCalledService();
        HashMap<String, Object> serviceData = new HashMap<>();
        serviceData.put("username", fireStoreHandler.getUser());
        serviceData.put("workName", (String) currentWork.get("workName"));
        serviceData.put("orderTime", new Date());

        return serviceData;
    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        HashMap<String, Object> serviceData = getServiceData();
        fireStoreHandler.addServiceCall(serviceData);
    }

    @Override
    public void networkNotAvailable() {
        showToast("Network not available");
    }

    @Override
    public void onErrorProceed(String s) {
        showToast("Something went wrong!");
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
        showToast("Error loading web page");
    }

    @Override
    public void onBackPressedCancelTransaction() {
        showToast("Order cancelled");
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        showToast("Transaction cancelled");
    }
    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
