package com.example.workwiz1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MyJobsFragment extends Fragment {


    FirebaseUser user;
    String name, category, city, price, avgRating ,rating;

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
        View view = inflater.inflate(R.layout.my_jobs,container,false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        jobsArrayList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
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

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    public static MyJobsFragment newInstance()
    {
        return  new MyJobsFragment();
    }

    public void myJobsRecyclerView()
    {

        DocumentReference docRef = db.collection("Jobs").document("xyz");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    document.getData();
                    name = document.get("title").toString();
                    category = document.get("category").toString();
                    city = document.get("city").toString();
                    price = document.get("price").toString();
                    avgRating = document.get("avgRating").toString();
                    rating = document.get("numRating").toString();

                    jobsArrayList.add(new com.example.workwiz1.Job(name, city, category, "", Integer.parseInt(price), Integer.parseInt(rating), Integer.parseInt(avgRating)));
                    adapter = new CustomAdapter(getActivity(),jobsArrayList);
                    mRecyclerView.setAdapter(adapter);

                }
            }
        });

    }




}
