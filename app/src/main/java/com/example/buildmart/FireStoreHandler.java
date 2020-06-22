package com.example.buildmart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

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

    private FirebaseAuth firebaseAuth;

    FireStoreHandler(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        imageRef = FirebaseStorage.getInstance().getReference().child(IMAGE_PATH);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private String getUser() {
        return firebaseAuth.getCurrentUser().getEmail();
    }

    private void showError(Exception e) {
        Toast.makeText(context, "Oops! Something went wrong.", Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }

    public void setImageFromPath(String imgName, final ImageView serviceName) {

        Log.d("imagePath", imageRef.getPath());
        imageRef.child(imgName + ".png").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(serviceName);
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

    public void getCart(final RecyclerView cartRecycler) {
        db.collection(CART_COLLECTION).whereEqualTo("userIdOwner", getUser())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                HashMap<String, Long> matQuantities = (HashMap<String, Long>) queryDocumentSnapshots.getDocuments()
                        .get(0)
                        .get("materialList");

                getCartMaterials(matQuantities, cartRecycler);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showError(e);
            }
        });
    }

    private void getCartMaterials(final HashMap<String, Long> matQuantities, final RecyclerView cartRecycler) {
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
                        CartAdapter cartAdapter = new CartAdapter(context, cartMaterials, quantities);
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
    }

    public void addItemToCart(final String materialId, final int value) {

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
                        Toast.makeText(context, "Added to cart", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
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

    public void getWorks(final RecyclerView worksList, String sectionDoc) {
        db.collection(SERVICE_COLLECTION).document(sectionDoc)
                .collection(WORK_COLLECTION)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() != 0) {
                            WorksSectionAdapter worksSectionAdapter = new WorksSectionAdapter(context, queryDocumentSnapshots);
                            worksList.setAdapter(worksSectionAdapter);
                            worksList.setLayoutManager(new LinearLayoutManager(context));
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
}
