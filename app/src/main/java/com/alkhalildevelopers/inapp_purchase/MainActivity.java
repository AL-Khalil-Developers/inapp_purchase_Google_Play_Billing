package com.alkhalildevelopers.inapp_purchase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button buyBtn;
    Activity activity = MainActivity.this;
    BillingClient billingClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView itemNameTv = findViewById(R.id.itemName);
        Button itemPriceBtn = findViewById(R.id.itemPrice);
        itemPriceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToGooglePlayBilling();
                Toast.makeText(activity, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });


        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

            }
        }).build();
        try {
            connectToGooglePlayBilling();
        }catch (Exception e){
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void connectToGooglePlayBilling() {
        try {
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingServiceDisconnected() {
                    Toast.makeText(activity, "Billing Service Disconnected", Toast.LENGTH_SHORT).show();
                    connectToGooglePlayBilling();
                }

                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    Toast.makeText(activity, "Billing Setup Finished", Toast.LENGTH_SHORT).show();
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        getProductDetails();
                    }
                }

            });
        }catch (Exception e){
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    private void getProductDetails() {
        List<String> productIDs = new ArrayList<String>();
        productIDs.add("deposit_coins");

        SkuDetailsParams getProductDetailsQuery = SkuDetailsParams.newBuilder().setSkusList(productIDs).setType(BillingClient.SkuType.INAPP).build();
        billingClient.querySkuDetailsAsync(getProductDetailsQuery, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                    TextView itemNameTv = findViewById(R.id.itemName);
                    Button itemPriceBtn = findViewById(R.id.itemPrice);
                    SkuDetails itemInfo = list.get(0);
                    itemNameTv.setText(itemInfo.getTitle());
                    itemPriceBtn.setText(itemInfo.getPrice());
                    itemPriceBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            billingClient.launchBillingFlow(activity, BillingFlowParams.newBuilder().setSkuDetails(itemInfo).build());
                            Toast.makeText(activity, "Clicked", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}