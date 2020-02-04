package com.example.workwiz1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class JobPost extends AppCompatActivity {

    private EditText mName, mCity, mCategory, mPrice, mStartDate,mDeadline,mDescription;
    private Button mAddBtn;
    FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String KEY_JOB_ID = "key_job_id";
    String restaurantId;
    String documentId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jobs_post);


        mAddBtn = findViewById(R.id.add_button);
        mName = findViewById(R.id.etname);
        mCity = findViewById(R.id.etcity);
        mCategory = findViewById(R.id.etcategory);
        mStartDate = findViewById(R.id.etStartDate);
        mDeadline = findViewById(R.id.etDeadline);
        mPrice = findViewById(R.id.etprice);
        mDescription = findViewById(R.id.etDescription);
        user = FirebaseAuth.getInstance().getCurrentUser();
//        restaurantId = getIntent().getExtras().getString(KEY_JOB_ID);
     /*   if (restaurantId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_JOB_ID);
        }*/
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToDatabase();
            }
        });

    }

    private void addToDatabase() {
        Log.d("Job Post", "addToDatabase: started");
        String name = mName.getText().toString().trim();
        String category = mCategory.getText().toString().trim();
        String city = mCity.getText().toString().trim();
        int price =Integer.parseInt( mPrice.getText().toString().trim());

        Map<String,Object> data1 = new HashMap<>();
        data1.put("title",name);
        data1.put("category",category);
        data1.put("city",city);
        data1.put("price",price);
        data1.put("avgRating",0);
        data1.put("numRating",0);
        data1.put("photo",null);

        documentId = db.collection("Jobs").document().getId();
        db.collection("Jobs").document(documentId)
                .set(data1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        Toast.makeText(JobPost.this, "Posted", Toast.LENGTH_SHORT).show();
                     /*   db.collection("Users").
                                document(user.getUid()).collection("My Jobs").document(user.getUid());*/


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(JobPost.this, "Error has occurred" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        db.collection("Users").document(user.getUid()).collection("My Jobs").document(documentId).set(data1);


    }
}
