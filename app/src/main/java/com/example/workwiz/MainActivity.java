package com.example.workwiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.workwiz.util.JobUtil;
import com.example.workwiz.viewModel.MainActivityViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, FragmentActionListener{

    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;

    private static final int LIMIT = 50;

    private Toolbar mToolbar;
    private TextView mCurrentSearchView;
    private TextView mCurrentSortByView;
    private RecyclerView mJobsRecycler;
    private ViewGroup mEmptyView;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private FilterDialogFragment mFilterDialog;
    private com.example.workwiz.adapter.JobAdapter mAdapter;

    private MainActivityViewModel mViewModel;
    private FragmentManager fragmentManager;
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;
    Fragment fragment = null;
    ImageView profile;
    ActionBarDrawerToggle toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        fragmentManager = getSupportFragmentManager();
        setHomeFragment();
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        updateNavHeader();






    }

    private void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            profile = headerView.findViewById(R.id.nav_profile);
            final TextView name = headerView.findViewById(R.id.nav_name);
            final TextView email = headerView.findViewById(R.id.nav_email);
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }

        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .centerCrop()
                    .into(profile);
        }
    }

    private void setHomeFragment() {

       // getSupportActionBar().setTitle("Workwiz");
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setFragmentActionListener(this::actionPerformed);
        fragmentManager.popBackStack("home",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().replace(R.id.frame,homeFragment).commit();
    }


    @Override
    public void onStart() {
        super.onStart();



    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);

            if (resultCode != RESULT_OK && shouldStartSignIn()) {
                startSignIn();
            }
        }
    }



    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }


    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);
    }



    private void showTodoToast() {
        Toast.makeText(this, "TODO: Implement", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.

        int id = item.getItemId();
        //TODO: rewite FindJobFragment and MyProfileFragment
        if (id == R.id.nav_home){

            setHomeFragment();
            updateNavHeader();

        }
        if (id == R.id.nav_profile) {
            fragment = new MyProfileFragment();

        }
        if (id == R.id.nav_post_jobs){
            fragment = new JobPostFragment();
        }
        if (id == R.id.nav_logout){
            //TODO: fix these after the fragments have been added

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("Ok", (dialog, id1) -> {

                AuthUI.getInstance().signOut(this);
                startSignIn();
            });
            builder.setNegativeButton("Cancel", (dialog, id12) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if (fragment!=null)
        {
            //TODO:I dont know where in the UI do we have content_frame

            FrameLayout frame = findViewById(R.id.frame);
            frame.removeAllViews();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame,fragment);
            ft.commit();
        }
        /* else if (id == R.id.activeJobs) {

            Intent intent = new Intent(Dashboard.this, ActiveJobs.class);
            startActivity(intent);

        } else if (id == R.id.starredJobs) {

            Intent intent = new Intent(Dashboard.this, StarredJobs.class);
            startActivity(intent);


        }

        else if (id == R.id.statusNotification) {

            Intent intent = new Intent(Dashboard.this, StatusNotification.class);
            startActivity(intent);


        }*/


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public Void actionPerformed(Bundle bundle) {
        int action = bundle.getInt(FragmentActionListener.ACTION_KEY);
        switch (action){
            case FragmentActionListener.ACTION_VALUE_BACK_TO_HOME:
                setHomeFragment();
                break;
        }

        return null;
    }
}

