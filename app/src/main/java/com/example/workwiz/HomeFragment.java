package com.example.workwiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class HomeFragment extends Fragment implements  View.OnClickListener,
        FilterDialogFragment.FilterListener,
        com.example.workwiz.adapter.JobAdapter.OnRestaurantSelectedListener {
    private static final int LIMIT = 50;

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
    FirebaseUser user;
    ImageView profile;
    private FragmentActionListener fragmentActionListener;

    public HomeFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main,container,false);

        mCurrentSearchView = view.findViewById(R.id.text_current_search);
        mCurrentSortByView = view.findViewById(R.id.text_current_sort_by);
        mJobsRecycler = view.findViewById(R.id.recycler_jobs);
        mEmptyView = view.findViewById(R.id.view_empty);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        view.findViewById(R.id.filter_bar).setOnClickListener(this);
        view.findViewById(R.id.button_clear_filter).setOnClickListener(this);

        // View model
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        FirebaseFirestore.setLoggingEnabled(true);

        // Initialize Firestore and the main RecyclerView
        initFirestore();
        initRecyclerView();
        setHasOptionsMenu(true);

        // Filter Dialog
        mFilterDialog = new FilterDialogFragment();

        return view;

    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener){
        this.fragmentActionListener = fragmentActionListener;
    }


    private void initFirestore() {
        // TODO(developer): Implement
        mFirestore = FirebaseFirestore.getInstance();

        //Get the 50 highest rated jobs
        mQuery = mFirestore.collection("jobs")                     //CHANGE NAME TO JOBS
                .orderBy("avgRating", Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w("Fragment", "No query, not initializing RecyclerView");
        }

        mAdapter = new com.example.workwiz.adapter.JobAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mJobsRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mJobsRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(getView().findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mJobsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mJobsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        onFilter(mViewModel.getFilters());

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    private void onAddItemsClicked() {
        // TODO(developer): Add random jobs
        //showTodoToast();
        //Get a reference to the jobs collection
        CollectionReference jobs = mFirestore.collection("jobs");   //CHANGE NAME TO JOBS

        for(int i = 0; i < 10; i++){
            //Get a random job POJO
            Job job = JobUtil.getRandom(getContext());

            //Add a new document to the jobs collection
            jobs.add(job);
        }//for
    }

    @Override
    public void onFilter(Filters filters) {
        // TODO(developer): Construct new query
        //showTodoToast();
        // Construct query basic query
        Query query = mFirestore.collection("jobs");             //CHANGE NAME TO JOBS

        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereEqualTo("category", filters.getCategory());
        }

        // City (equality filter)
        if (filters.hasCity()) {
            query = query.whereEqualTo("city", filters.getCity());
        }

        // Price (equality filter)
        if (filters.hasPrice()) {
            query = query.whereEqualTo("price", filters.getPrice());
        }

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        // Limit items
        query = query.limit(LIMIT);

        // Update the query
        mQuery = query;
        mAdapter.setQuery(query);

        // Set header
        mCurrentSearchView.setText(Html.fromHtml(filters.getSearchDescription(getContext())));
        mCurrentSortByView.setText(filters.getOrderDescription(getContext()));

        // Save filters
        mViewModel.setFilters(filters);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_items:
                onAddItemsClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter_bar:
                onFilterClicked();
                break;
            case R.id.button_clear_filter:
                onClearFilterClicked();
        }
    }

    public void onFilterClicked() {
        // Show the dialog containing filter options
        mFilterDialog.show(getActivity().getSupportFragmentManager(), FilterDialogFragment.TAG);
    }

    public void onClearFilterClicked() {
        mFilterDialog.resetFilters();

        onFilter(Filters.getDefault());
    }

    @Override
    public void onJobSelected(DocumentSnapshot restaurant) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(getActivity(), JobDetail.class);
        intent.putExtra(JobDetail.KEY_JOB_ID, restaurant.getId());
        startActivity(intent);
    }
}
