package com.example.buildmart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FireStoreHandler {

    private Context context;
    private FirebaseFirestore db;
    private StorageReference imageRef;
    private static final String MATERIAL_COLLECTION = "materials";
    private static final String CART_COLLECTION = "userCarts";
    private static final String SERVICE_COLLECTION = "services";
    private static final String IMAGE_PATH = "imagePack";
    private static final String WORK_COLLECTION = "works";
    private static final String SHOP_DETAILS = "shopDetails";
    private static final String DEALS_OF_DAY = "dealsOfTheDay";
    private static final String SHOP_ORDERS = "shopOrders";
    private static final String SHOP_SERVICE_ORDERS = "shopServiceOrders";

    private FirebaseAuth firebaseAuth;

    FireStoreHandler(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        imageRef = FirebaseStorage.getInstance().getReference().child(IMAGE_PATH);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    String getUser() {
        return firebaseAuth.getCurrentUser().getEmail();
    }

    private void showError(Exception e) {
        Toast.makeText(context, "Oops! Something went wrong.", Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }

    public void setImageFromPath(String imgName, final ImageView imageView) {

        Log.d("imagePath", imageRef.getPath());
        imageRef.child(imgName + ".png").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imageView);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e);
                    }
                });
    }

    void getCategoryMaterials(String category, final RecyclerView materialList) {
        db.collection(MATERIAL_COLLECTION).whereEqualTo("category", category)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                MaterialListAdapter categoryMaterialListAdapter = new MaterialListAdapter(context, queryDocumentSnapshots);
                materialList.setAdapter(categoryMaterialListAdapter);
                materialList.setLayoutManager(new LinearLayoutManager(context));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showError(e);
            }
        });
    }

    public void getRecommended(final RecyclerView recommendedList) {
        db.collection(MATERIAL_COLLECTION).whereEqualTo("recommended", "true")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                MaterialObjectAdapter recommendedRecyclerAdapter = new MaterialObjectAdapter(context, queryDocumentSnapshots);
                recommendedList.setAdapter(recommendedRecyclerAdapter);
                recommendedList.setLayoutManager(new GridLayoutManager(context, 2));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showError(e);
            }
        });
    }

    public void getCart(final RecyclerView cartRecycler, final TextView totalAmountText, final RelativeLayout progressBack,
                        final CardView quantitySelectorCard) {
        db.collection(CART_COLLECTION).whereEqualTo("userIdOwner", getUser())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                HashMap<String, Long> matQuantities = (HashMap<String, Long>) queryDocumentSnapshots.getDocuments()
                        .get(0)
                        .get("materialList");

                String cartId = queryDocumentSnapshots.getDocuments().get(0).getId();

                getCartMaterials(matQuantities, cartRecycler, totalAmountText, cartId, progressBack, quantitySelectorCard);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showError(e);
            }
        });
    }

    private void getCartMaterials(final HashMap<String, Long> matQuantities, final RecyclerView cartRecycler,
                                  final TextView totalAmountText, final String cartId,
                                  RelativeLayout progressBack, final CardView quantitySelectorCard) {
        final ArrayList<MaterialObject> cartMaterials = new ArrayList<>();
        final ArrayList<Long> quantities = new ArrayList<>();

        for (final String matId : matQuantities.keySet()) {
            db.collection(MATERIAL_COLLECTION).whereEqualTo("matId", matId)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    DocumentSnapshot material = queryDocumentSnapshots.getDocuments().get(0);
                    cartMaterials.add(new MaterialObject(material));
                    quantities.add(matQuantities.get(matId));

                    if (cartMaterials.size() == matQuantities.size()) {
                        CartAdapter cartAdapter = new CartAdapter(context, cartMaterials, quantities,
                                totalAmountText, cartId, quantitySelectorCard);
                        cartRecycler.setLayoutManager(new LinearLayoutManager(context));
                        cartRecycler.setAdapter(cartAdapter);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showError(e);
                }
            });
        }

        progressBack.setVisibility(View.INVISIBLE);
    }

    public void addItemToCart(final String materialId, final int value, final RelativeLayout progressBack, final CardView itemQuantityCard) {

        db.collection(CART_COLLECTION).whereEqualTo("userIdOwner", getUser())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot thisDoc = queryDocumentSnapshots.getDocuments().get(0);
                HashMap<String, Long> materialList = (HashMap<String, Long>) thisDoc.get("materialList");

                long updatedValue = value;
                String fieldKey = "materialList." + materialId;

                if (materialList.get(materialId) != null)
                    updatedValue = materialList.get(materialId) + value;

                db.collection(CART_COLLECTION).document(thisDoc.getId())
                        .update(
                                fieldKey, updatedValue
                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBack.setVisibility(View.INVISIBLE);
                        itemQuantityCard.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Added to cart", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBack.setVisibility(View.INVISIBLE);
                        showError(e);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showError(e);
            }
        });
    }

    public void searchItem(final RecyclerView searchListRecycler, String searchText) {
        db.collection(MATERIAL_COLLECTION)
                .orderBy("name")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff")
                .limit(10)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        MaterialListAdapter searchListAdapter = new MaterialListAdapter(context, queryDocumentSnapshots);
                        searchListRecycler.setAdapter(searchListAdapter);
                        searchListRecycler.setLayoutManager(new LinearLayoutManager(context));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e);
                    }
                });
    }

    public void getServiceSections(final RecyclerView serviceSectionList, String serviceSection) {
        db.collection(SERVICE_COLLECTION)
                .whereEqualTo("serviceSection", serviceSection)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ServiceSectionAdapter serviceSectionAdapter = new ServiceSectionAdapter(context, queryDocumentSnapshots);
                        serviceSectionList.setAdapter(serviceSectionAdapter);
                        serviceSectionList.setLayoutManager(new LinearLayoutManager(context));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e);
                    }
                });
    }

    public void getWorks(final RecyclerView worksList, String sectionDoc, final WorksSection activity, final RelativeLayout progressBack) {
        db.collection(SERVICE_COLLECTION).document(sectionDoc)
                .collection(WORK_COLLECTION)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() != 0) {
                            WorksSectionAdapter worksSectionAdapter = new WorksSectionAdapter(context, queryDocumentSnapshots, activity, progressBack);
                            worksList.setAdapter(worksSectionAdapter);
                            worksList.setLayoutManager(new LinearLayoutManager(context));
                            progressBack.setVisibility(View.INVISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBack.setVisibility(View.INVISIBLE);
                        showError(e);
                    }
                });
    }

    public void placeShopCall() {
        db.collection(SHOP_DETAILS).document(SHOP_DETAILS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                documentSnapshot.get("phoneNumber")));
                        context.startActivity(callIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e);
                    }
                });
    }

    public void sendWhatsappMessage() {
        db.collection(SHOP_DETAILS).document(SHOP_DETAILS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String contact = (String) documentSnapshot.get("phoneNumber"); // use country code with your phone number
                        String url = "https://api.whatsapp.com/send?phone=" + contact;
                        try {
                            PackageManager pm = context.getPackageManager();
                            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                            Intent messageIntent = new Intent(Intent.ACTION_VIEW);
                            messageIntent.setData(Uri.parse(url));
                            context.startActivity(messageIntent);
                        } catch (PackageManager.NameNotFoundException e) {
                            Toast.makeText(context, "Whatsapp app is not installed in your phone", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e);
                    }
                });
    }

    public void getDealsOfDay(final RecyclerView dealsOfDayList) {
        db.collection(DEALS_OF_DAY).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        MaterialObjectAdapter dealsOfDayAdapter = new MaterialObjectAdapter(context, queryDocumentSnapshots);
                        dealsOfDayList.setAdapter(dealsOfDayAdapter);
                        dealsOfDayList.setLayoutManager(new GridLayoutManager(context, 2));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e);
                    }
                });
    }

    public void addUserCart(String email) {
        HashMap<String, Object> userCart = new HashMap<>();
        userCart.put("userIdOwner", email);
        userCart.put("materialList", new HashMap<String, Long>());

        db.collection(CART_COLLECTION).add(userCart);
    }

    public void postRequirement(String postText, final Activity requirementActivity) {
        HashMap<String, String> postDetails = new HashMap<>();
        postDetails.put("username", getUser());
        postDetails.put("postText", postText);

        db.collection("requirements").add(postDetails)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context, "Requirement posted", Toast.LENGTH_LONG).show();
                        requirementActivity.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e);
                    }
                });
    }

    public void postUnknownRequirement(String postText, final PostUnknownService postUnknownService) {
        HashMap<String, String> postDetails = new HashMap<>();
        postDetails.put("username", getUser());
        postDetails.put("postText", postText);

        db.collection("serviceRequirement").add(postDetails)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context, "Requirement posted", Toast.LENGTH_LONG).show();
                        postUnknownService.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e);
                    }
                });
    }

    public void addOrder(final HashMap<String, Object> orderData, final Activity cartActivity) {
        db.collection(SHOP_ORDERS).add(orderData)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                HashMap<String, Object> userCart = new HashMap<>();
                userCart.put("userIdOwner", getUser());
                userCart.put("materialList", new HashMap<String, Long>());

                db.collection(CART_COLLECTION).document((String) orderData.get("cartId"))
                        .set(userCart);

                Toast.makeText(context, "Successful", Toast.LENGTH_LONG).show();
                cartActivity.finish();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showError(e);
            }
        });
    }

    public void getServiceAdvance(final int position, final WorksSectionAdapter adapter) {
        db.collection(SHOP_DETAILS).document(SHOP_DETAILS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        long advance = documentSnapshot.getLong("serviceAdvance");
                        adapter.getServiceToken(advance, position);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e);
                    }
                });
    }

    public void addServiceCall(HashMap<String, Object> serviceData) {
        db.collection(SHOP_SERVICE_ORDERS).add(serviceData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context, "Service call successful", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e);
                    }
                });
    }

    public void updateMaterialInCart(HashMap<String, Object> selectedMat, final RelativeLayout progressBack) {

        String fieldKey = "materialList." + selectedMat.get("matId");

        db.collection(CART_COLLECTION).document((String) selectedMat.get("cartId"))
                .update(
                        fieldKey,
                        selectedMat.get("quantity")
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBack.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e);
                        progressBack.setVisibility(View.INVISIBLE);
                    }
                });
    }

    public void deleteItemFromCart(HashMap<String, Object> selectedMat, final RelativeLayout progressBack) {
        String fieldKey = "materialList." + selectedMat.get("matId");

        Map<String,Object> updates = new HashMap<>();
        updates.put(fieldKey, FieldValue.delete());

        db.collection(CART_COLLECTION).document((String) selectedMat.get("cartId"))
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBack.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBack.setVisibility(View.INVISIBLE);
                        showError(e);
                    }
                });
    }
}
