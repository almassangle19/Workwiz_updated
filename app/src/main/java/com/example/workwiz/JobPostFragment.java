package com.example.workwiz;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workwiz.adapter.JobAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.workwiz.MyProfileFragment.COLLECTION_NAME_KEY;

public class JobPostFragment extends Fragment {

    private EditText mName, mCity, mCategory, mPrice;
    private Button mAddBtn;
    FirebaseUser user;
    String name, category, city, price;

    //firebase firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mRecyclerView;
    private CustomAdapter adapter;
    private ArrayList<Job> jobsArrayList = new ArrayList<>();
    //vars

    private static final String TAG = "MainActivity";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.job_post,container,false);


        mAddBtn = view.findViewById(R.id.add_button);
        mName = view.findViewById(R.id.etname);
        mCity = view.findViewById(R.id.etcity);
        mCategory = view.findViewById(R.id.etcategory);
        mPrice = view.findViewById(R.id.etprice);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToDatabase();
            }
        });

//        setupRecyclerView();


        return view;

    }







    private void setupRecyclerView()
    {


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
                    jobsArrayList.add(new Job(name,city,category,"",Integer.parseInt(price),0,0));


                }
            }
        });

        adapter = new CustomAdapter(jobsArrayList , getContext());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


    }


    private void addToDatabase() {
        Log.d(TAG, "addToDatabase: started");
        String name = mName.getText().toString().trim();
        String category = mCategory.getText().toString().trim();
        String city = mCity.getText().toString().trim();
        int price =Integer.parseInt( mPrice.getText().toString().trim());

        Map<String,Object> data1 = new HashMap<>();
        data1.put("name",name);
        data1.put("category",category);
        data1.put("city",city);
        data1.put("price",price);
        data1.put("avgRating",0);
        data1.put("numRating",0);
        data1.put("photo",null);

        db.collection("jobs").document(user.getUid())
                .set(data1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        Toast.makeText(getContext(), "Posted", Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(), "Error has occurred" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();

    }






}
