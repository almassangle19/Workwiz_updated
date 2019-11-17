package com.example.workwiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.myapplication.SignUpActivity.COLLECTION_NAME_KEY;

public class Skills extends AppCompatActivity {


    Spinner spinner;
    Button btnAdd;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String skill;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_skills);

        spinner = findViewById(R.id.spinner);
        btnAdd = findViewById(R.id.btnAdd);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        final List<String> skills = new ArrayList<String>();
        skills.add("Select Skills");
        skills.add("Android Development");
        skills.add("Data Scientist");
        skills.add("Web development");


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,skills);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner.setSelection(position);
                skill = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String,Object> data1 = new HashMap<>();
                data1.put("skill",skill);
                db.collection(COLLECTION_NAME_KEY).document(user.getUid()).collection("Skills").document()
                        .set(data1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                Toast.makeText(Skills.this, "Updated", Toast.LENGTH_SHORT).show();
                                Intent intent =  new Intent(Skills.this, com.example.myapplication.MyProfileFragment.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Skills.this, "Error has occurred" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }
}



