package com.example.workwiz;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.workwiz.R;
import com.example.workwiz.Skills;
import com.example.workwiz.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import static android.app.Activity.RESULT_OK;
import static java.lang.Thread.sleep;

public class MyProfileFragment extends Fragment {

    private static final int CHOOSE_IMAGE = 101;
    ImageView profile;
    TextView tvEmail;
    TextView tvPhone;
    TextView tvName;
    Uri profileUri;
    ProgressBar progressBar;
    StorageTask uploadTask;
    String uid;
    FirebaseUser user;
    Dialog myDialog;
    TextView addExperience, addSkills, tvExperience, tvSkills;
    public static String name, email, phoneNo;
    List<String>list = new ArrayList<String>();


    public static final String COLLECTION_NAME_KEY = "Users";
    String profileUrl;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private boolean executed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_my_profile,container,false);
    /*    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.fragment_profile,null,false);
        drawer.addView(contentView,0);*/
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhn);
        tvName = view.findViewById(R.id.tvName);
        profile = view.findViewById(R.id.profile);
        progressBar = view.findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        collectionReference = db.collection(COLLECTION_NAME_KEY);
        user = mAuth.getCurrentUser();
        myDialog = new Dialog(getActivity());
        addExperience = view.findViewById(R.id.addExperience);
        addSkills = view.findViewById(R.id.addSkills);
      /*  Toolbar toolbar = getView().findViewById(R.id.toolbar);

        DrawerLayout drawer =getView().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
*/
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showImageChooser();

            }
        });

        loadUserInfo();
       /* addExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Experience_form.class);
                startActivity(intent);

            }
        });
*/
        addSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), Skills.class);
                startActivity(i);

            }
        });
        return view;


    }


    @Override
    public void onStart() {
        super.onStart();

        DocumentReference docRef = db.collection(COLLECTION_NAME_KEY).document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Users users = new Users();
                    document.getData();
                    users.setName((String) document.get("name"));
                    users.setPhoneNo((String) document.get("phoneNo"));
                    users.setEmail((String) document.get("email"));


                    name = users.getName();
                    email = users.getEmail();
                    phoneNo = users.getPhoneNo();


                    tvName.setText(name);
                    tvEmail.setText(email);
                    tvPhone.setText(phoneNo);

                }
            }
        });

        getSkills();


    }

    private void loadUserInfo() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            if (user.getPhotoUrl() != null) {
                Glide.with(getActivity())
                        .load(user.getPhotoUrl())
                        .centerCrop()
                        .into(profile);
            }


        }
    }

    private void saveUserInformation() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && profileUrl != null) {
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profileUrl))
                    .build();

            user.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            profileUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), profileUri);
                profile.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();
                //  uploadTofirestore();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadImageToFirebaseStorage() {

        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        if (profileUri != null) {

            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading");
            progressDialog.show();


            profileImageRef.putFile(profileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            //   profileUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            profileImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    profileUrl = task.getResult().toString();
                                    Log.i("URL", profileUrl);
                                    saveUserInformation();
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }

    }

    private void showImageChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);

    }



    private void uploadTofirestore(){

        final Users users = new Users();
        users.setProfile(profileUrl);
        db.collection(COLLECTION_NAME_KEY).document().set(users);

        //  db.collection(COLLECTION_NAME_KEY).document(etEmail.getText().toString()).set(users);

        Map<String,Object> data = new HashMap<>();
        data.put("profile",profileUri);
        db.collection(COLLECTION_NAME_KEY).document(user.getUid())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getActivity(), "Error has occurred" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }

    private void getSkills() {
        DocumentReference docRef = db.collection(COLLECTION_NAME_KEY).document(user.getUid()).collection("Skills").document(user.getUid());

        db.collection(COLLECTION_NAME_KEY).document(user.getUid()).collection("Skills").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    list.add((documentSnapshot.get("skill")).toString());
                }
                ArrayAdapter<String>adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,list);
                ListView listView = getView().findViewById(R.id.listSkills);
                listView.setAdapter(adapter);
            }
        });

    }
}


