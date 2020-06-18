package com.example.buildmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.security.Permission;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, Toolbar.OnMenuItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView drawerNavigation;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FireStoreHandler fireStoreHandler;

    int pic[] = {R.raw.construction1,R.raw.construction2,R.raw.construction3,R.raw.construction4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        drawerLayout = findViewById(R.id.mainNavDrawer);
        drawerNavigation = findViewById(R.id.navDrawer);
        toolbar = findViewById(R.id.mainToolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        setSupportActionBar(toolbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        loadScreen(new HomeScreen());

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_drawer,R.string.open_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        drawerNavigation.setNavigationItemSelectedListener(this);


        fireStoreHandler = new FireStoreHandler(this);

        toolbar.setOnMenuItemClickListener(this);

        new PermissionHandler(this, new String[]{Manifest.permission.CALL_PHONE}).requestPermissions();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.homeScreen:
                loadScreen(new HomeScreen());
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                break;

            case R.id.servicesScreen:
                loadScreen(new ServicesScreen());
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                break;

            case R.id.searchScreen:
                loadScreen(new SearchScreen());
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                break;

            case R.id.rateCalculatorScreen:
                loadScreen(new RateCalculatorScreen());
                bottomNavigationView.getMenu().getItem(3).setChecked(true);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginScreen.class));
                break;
        }
        return false;
    }

    private void loadScreen(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.screenContainer,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.viewCart:
                startActivity(new Intent(this, ViewCart.class));
                break;

            case R.id.callShop:
                fireStoreHandler.placeShopCall();
                break;

            case R.id.whatsappMessage:
                fireStoreHandler.sendWhatsappMessage();
                break;
        }
        return false;
    }
}
