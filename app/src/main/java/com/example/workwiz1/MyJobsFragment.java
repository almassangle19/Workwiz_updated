package com.example.workwiz1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.opencensus.tags.Tag;

public class MyJobsFragment extends Fragment implements com.example.workwiz1.adapter.CustomAdapter.OnClickListener {

    FirebaseUser user;
    String name, category, city, price, avgRating, rating;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private com.example.workwiz1.adapter.JobAdapter mAdapter;


    //firebase firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mRecyclerView;
    private CustomAdapter adapter;
    private List<com.example.workwiz1.Job> jobsArrayList;
    //vars


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.my_jobs, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        jobsArrayList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mFirestore = FirebaseFirestore.getInstance();
        // initFirestore();
        myJobsRecyclerView();

        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
        myJobsRecyclerView();
    }

    public static MyJobsFragment newInstance() {
        return new MyJobsFragment();
    }

    /*private void initFirestore() {
        // TODO(developer): Implement
        mFirestore = FirebaseFirestore.getInstance();

        //Get the 50 highest rated jobs
        mQuery = mFirestore.collection("Users").document(user.getUid()).collection("My Jobs");                 //CHANGE NAME TO JOBS
    }*/

    public void myJobsRecyclerView() {

        db.collection("Users").document(user.getUid()).collection("My Jobs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        com.example.workwiz1.Job job = queryDocumentSnapshot.toObject(com.example.workwiz1.Job.class);
                        jobsArrayList.add(job);

                        adapter = new CustomAdapter(getActivity(), jobsArrayList);
                        mRecyclerView.setAdapter(adapter);

                    }
                } else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }

            }
        });
        // mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

      /*  docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    document.getData();
                    name = document.get("title").toString();
                    category = document.get("category").toString(); Log.d(T,"Error",task.getException());
                    city = document.get("city").toString();
                    price = document.get("price").toString();
                    avgRating = document.get("avgRating").toString();
                    rating = document.get("numRating").toString();
                    jobsArrayList.add(new com.example.workwiz1.Job(name, city, category, "",
                            Integer.parseInt(price), Integer.parseInt(rating), Integer.parseInt(avgRating)));
                    adapter = new CustomAdapter(getActivity(),jobsArrayList);
                    mRecyclerView.setAdapter(adapter);

                }
            }
        });*/


      /* if (mQuery == null) {
            Log.w("Fragment", "No query, not initializing RecyclerView");
        }

        mAdapter = new com.example.workwiz1.adapter.JobAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mRecyclerView.setVisibility(View.GONE);
                    //mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    //mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(getView().findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();

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


    @Override
    public void onJobSelected(DocumentSnapshot restaurant) {

    }*/

    }

        @Override
        public void onJobSelected (DocumentSnapshot restaurant){
            // Go to the details page for the selected restaurant
            Intent intent = new Intent(getContext(), JobDetail.class);
            intent.putExtra(JobDetail.KEY_JOB_ID, restaurant.getId());
            startActivity(intent);
        }



}