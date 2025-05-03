package com.example.btl_android.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.btl_android.R;
import com.example.btl_android.ui.auth.LoginActivity;
import com.example.btl_android.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        BottomNavigationView bottomNavigationView = binding.appBarMain.bottomNavigation;

        // Nhận role từ LoginActivity
        String role = getIntent().getStringExtra("role");

        if ("collector".equals(role)) {
            // Nếu là người thu gom, đổi menu navigation
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.nav_collector_drawer);

            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_collector);

            // Cập nhật AppBarConfiguration tương ứng
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_setting, R.id.nav_notification)
                    .setOpenableLayout(drawer)
                    .build();
        } else {
            // Nếu là donor thì giữ nguyên
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_history, R.id.nav_setting, R.id.nav_notification, R.id.nav_statistics, R.id.Schedule)
                    .setOpenableLayout(drawer)
                    .build();
        }
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.logout) {
                    // Xử lý đăng xuất riêng
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return true;
                } else {
                    // Mặc định điều hướng qua navGraph
                    boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                    drawer.closeDrawer(GravityCompat.START);
                    return handled;
                }
            }
        });

        setupHeader();
    }


    private void setupHeader() {
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.fetchCurrentUser();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView txtName = headerView.findViewById(R.id.nav_header_name);
        TextView txtEmail = headerView.findViewById(R.id.nav_header_email);

        userViewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                txtName.setText(user.getName());
                Log.i( "setupHeader: ", user.getName());
                Log.i( "setupHeader: ", user.getRole());

                txtEmail.setText(user.getEmail());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}