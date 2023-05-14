package com.example.skillswap.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.widget.Toolbar;

import com.example.skillswap.models.MySkillViewModel;
import com.example.skillswap.R;

public class MySkillActivity extends BaseActivity {

    private AppBarConfiguration appBarConfiguration;
    private Toolbar toolbar;
    private MySkillViewModel mMySkillViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_skill);
        mMySkillViewModel = new ViewModelProvider(this).get(MySkillViewModel.class);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_my_skill);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(toolbar, navController);

        }

@Override
public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_my_skill);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
        || super.onSupportNavigateUp();
        }
        }