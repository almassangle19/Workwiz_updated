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
import java.util.Map;

import static com.example.workwiz.MyProfileFragment.COLLECTION_NAME_KEY;

public class JobPostFragment extends Fragment {

    private EditText mName, mCity, mCategory, mPrice;
    private Button mAddBtn;
    FirebaseUser user;
    String jobs;

    //firebase firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToDatabase();
            }
        });




        return view;

    }



    private void addToDatabase() {
        Log.d(TAG, "addToDatabase: started");
        String name = mName.getText().toString().trim();
        String category = mCategory.getText().toString().trim();
        String city = mCity.getText().toString().trim();
        String price = mPrice.getText().toString().trim();

        //check that fields are not empty
        if (!name.equals("") && !category.equals("") && !city.equals("") && !price.equals("")) {
            Job jobs = new Job(name, city, category, "", Integer.parseInt(price), 0, 0);
            db.collection("jobs").add(jobs).addOnSuccessListener(
                    new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "onSuccess: Added successfully");
                            Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: ", e);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }






}
