package com.example.workwiz;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class JobPost extends AppCompatActivity {

    Button btnLogout;
        FirebaseAuth mFirebaseAuth;
        private FirebaseAuth.AuthStateListener mAuthStateListener;
        private static final String TAG = "MainActivity";
        private static final String KEY_NAME = "Name";
        private static final String KEY_LOCATION = "Location";
        private static final String KEY_SALARY = "Salary";
        private static final String KEY_DESCRIPTION = "Description";

        private EditText editTextName;
        private EditText editTextLocation;
        private EditText editTextSalary;
        private EditText editTextDescription;
        private TextView textViewData;


        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private CollectionReference notebookRef = db.collection("JOBS");
        private DocumentReference noteRef = db.document("JOBS/My First job");

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.job_post);
            btnLogout = findViewById(R.id.logout);
            editTextName = findViewById(R.id.edit_text_Name);
            editTextLocation = findViewById(R.id.edit_text_location);
            editTextSalary = findViewById(R.id.edit_text_salary);
            editTextDescription = findViewById(R.id.edit_text_description);
            textViewData = findViewById(R.id.text_view_data);
           /* btnLogout .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intSignUp = new Intent(JobPost.this,MainActivity.class);
                    startActivity(intSignUp);
                }
            });*/
        }

        protected void onStart ()
        {
            super.onStart();
            notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                    if(e!= null){
                        return;
                    }

                    String data ="";
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    {
                        Note note = documentSnapshot.toObject(Note.class);
                        note.setDocumentId(documentSnapshot.getId());
                        String documentId = note.getDocumentId();
                        String name = note.getName();
                        String location = note.getLocation();
                        String salary = note.getSalary();
                        String description = note.getDescription();

                        data +="ID : "+ documentId + "\nName :" +name +"\nDescription: "+description +"\nSalary :"+salary +"\n Location :"+location +"\n\n";



                    }


                    textViewData.setText(data);
                }
            });
        }



        public void addNote (View v)
        {
            String name = editTextName.getText().toString();
            String location = editTextLocation.getText().toString();
            String salary = editTextSalary.getText().toString();
            String description = editTextDescription.getText().toString();

            Note note =new Note (name,location,salary,description);

            notebookRef.add(note);
        }

        public void loadNotes(View v){
            notebookRef.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            String data ="";
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                Note note = documentSnapshot.toObject(Note.class );
                                note.setDocumentId(documentSnapshot.getId());

                                String documentId = note.getDocumentId();
                                String name = note.getName();
                                String location = note.getLocation();
                                String salary = note.getSalary();
                                String description = note.getDescription();

                                data += "ID : "+ documentId +"\nName :" +name +"\nDescription: "+description +"\nSalary :"+salary +"\n Location :"+location +"\n\n";
                            }
                            textViewData.setText(data);
                        }
                    });
        }




}
