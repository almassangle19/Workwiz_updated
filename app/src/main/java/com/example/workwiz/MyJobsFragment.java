package com.example.workwiz;



import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workwiz.util.JobUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyJobsFragment extends Fragment {


    FirebaseUser user;
    String name, category, city, price, avgRating ,rating;

    //firebase firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mRecyclerView;
    private CustomAdapter adapter;
    private ArrayList<Job> jobsArrayList = new ArrayList<>();
    //vars



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.my_jobs,container,false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mRecyclerView = view.findViewById(R.id.recycler_view);

        DocumentReference docRef = db.collection("jobs").document(user.getUid());

     docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    document.getData();
                    name = document.get("name").toString();
                    category = document.get("category").toString();
                    city= document.get("city").toString();
                    price = document.get("price").toString();
                    avgRating = document.get("avgRating").toString();
                    rating = document.get("numRating").toString();

                    jobsArrayList.add(new Job(name,city,category,"",Integer.parseInt(price),Integer.parseInt(rating),Integer.parseInt(avgRating)));


                }
            }
        });

        adapter = new CustomAdapter(getActivity(),jobsArrayList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);



        return view;

    }



    @Override
    public void onStart() {
        super.onStart();

    }


}
