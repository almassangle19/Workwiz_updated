package com.example.workwiz1;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.workwiz1.MyProfileFragment.COLLECTION_NAME_KEY;


public class Skills extends AppCompatActivity {


    Spinner spinner;
    Button btnAdd;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String skill;
    Fragment fragment;
    FragmentManager fragmentManager;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_skills);

        spinner = findViewById(R.id.spinner);
        btnAdd = findViewById(R.id.btnAdd);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fragmentManager = getSupportFragmentManager();
       // fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
         /*   @Override
            public void onBackStackChanged() {
                if (getFragmentManager().getBackStackEntryCount() == 0)
                {
                    finish();
                }
            }
        });*/
        user = mAuth.getCurrentUser();

        final List<String> skills = new ArrayList<String>();
        skills.add("Select Skills");
        skills.add("Machine Learning");
        skills.add("Android Development");
        skills.add("Web Development");
        skills.add("Data Scientist");




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



       /* skills.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String value = (String) adapterView.getItemAtPosition(i);
                arrayAdapter.remove(value);
                arrayAdapter.notifyDataSetChanged();
            }
        });*/

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

                            //    FrameLayout frame = findViewById(R.id.frame1);
                              //  frame.removeAllViews();
                               // fragment = new MyProfileFragment();
                               // fragmentManager.popBackStack("skills",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            //  FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.frame1,fragment);
                                //  ft.replace(R.id.frame1,fragment);
                              //    ft.addToBackStack(null);
                               // ft.commit();



                                //getFragmentManager().popBackStack();

                              /*  if (getFragmentManager().getBackStackEntryCount()>0)
                                {
                                    getFragmentManager().popBackStack();
                                    return;
                                } if (getFragmentManager().getBackStackEntryCount()>0)
                                {
                                    getFragmentManager().popBackStack();
                                    return;
                                }
                               /* Intent intent =  new Intent(Skills.this, MyProfileFragment.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                               /* Intent intent =  new Intent(Skills.this, MyProfileFragment.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();*/


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



