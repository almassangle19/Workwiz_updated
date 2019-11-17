package com.example.workwiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.workwiz.util.JobUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public abstract class JobDetailFragment extends Fragment implements
        View.OnClickListener,
        EventListener<DocumentSnapshot>,
        RatingDialogFragment.RatingListener {

    private static final String TAG = "JobDetail";

    public static final String KEY_JOB_ID = "key_job_id";

    private ImageView mImageView;
    private TextView mNameView;
    private MaterialRatingBar mRatingIndicator;
    private TextView mNumRatingsView;
    private TextView mCityView;
    private TextView mCategoryView;
    private TextView mPriceView;
    private ViewGroup mEmptyView;
    private RecyclerView mRatingsRecycler;

    private RatingDialogFragment mRatingDialog;

    private FirebaseFirestore mFirestore;
    private DocumentReference mJobRef;
    private ListenerRegistration mJobRegistration;

    private RatingAdapter mRatingAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_job_detail, container, false);

        mImageView = view.findViewById(R.id.job_image);
        mNameView = view.findViewById(R.id.restaurant_name);
        mRatingIndicator = view.findViewById(R.id.job_rating);
        mNumRatingsView = view.findViewById(R.id.job_num_ratings);
        mCityView = view.findViewById(R.id.job_city);
        mCategoryView = view.findViewById(R.id.job_category);
        mPriceView = view.findViewById(R.id.job_salary);
        mEmptyView = view.findViewById(R.id.view_empty_ratings);
        mRatingsRecycler = view.findViewById(R.id.recycler_ratings);

        view.findViewById(R.id.job_button_back).setOnClickListener(this);
        view.findViewById(R.id.fab_show_rating_dialog).setOnClickListener(this);

        // Get restaurant ID from extras
        String restaurantId = getActivity().getIntent().getExtras().getString(KEY_JOB_ID);
        if (restaurantId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_JOB_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mJobRef = mFirestore.collection("jobs").document(restaurantId);  //CHANGE NAME TO JOBS

        // Get ratings
        Query ratingsQuery = mJobRef
                .collection("ratings")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);

        // RecyclerView
        mRatingAdapter = new RatingAdapter(ratingsQuery) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mRatingsRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRatingsRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        };

        mRatingsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRatingsRecycler.setAdapter(mRatingAdapter);

        mRatingDialog = new RatingDialogFragment();
        return view;
    }




    @Override
    public void onStart() {
        super.onStart();

        mRatingAdapter.startListening();
        mJobRegistration = mJobRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mRatingAdapter.stopListening();

        if (mJobRegistration != null) {
            mJobRegistration.remove();
            mJobRegistration = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.job_button_back:
                onBackArrowClicked(v);
                break;
            case R.id.fab_show_rating_dialog:
                onAddRatingClicked(v);
                break;
        }
    }

    private Task<Void> addRating(final DocumentReference jobRef, final com.example.workwiz.Rating rating) {
        // TODO(developer): Implement
        //return Tasks.forException(new Exception("not yet implemented"));
        // Create reference for new rating, for use inside the transaction
        final DocumentReference ratingRef = jobRef.collection("ratings")
                .document();

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction)
                    throws FirebaseFirestoreException {

                Job job = transaction.get(jobRef)
                        .toObject(Job.class);

                // Compute new number of ratings
                int newNumRatings = job.getNumRatings() + 1;

                // Compute new average rating
                double oldRatingTotal = job.getAvgRating() *
                        job.getNumRatings();
                double newAvgRating = (oldRatingTotal + rating.getRating()) /
                        newNumRatings;

                // Set new job info
                job.setNumRatings(newNumRatings);
                job.setAvgRating(newAvgRating);

                // Commit to Firestore
                transaction.set(jobRef, job);
                transaction.set(ratingRef, rating);

                return null;
            }
        });
    }

    /**
     * Listener for the Job document ({@link #mJobRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }

        onJobLoaded(snapshot.toObject(Job.class));
    }

    private void onJobLoaded(Job job) {
        mNameView.setText(job.getName());
        mRatingIndicator.setRating((float) job.getAvgRating());
        mNumRatingsView.setText(getString(R.string.fmt_num_ratings, job.getNumRatings()));
        mCityView.setText(job.getCity());
        mCategoryView.setText(job.getCategory());
        mPriceView.setText(JobUtil.getPriceString(job));

        // Background image
        Glide.with(mImageView.getContext())
                .load(job.getPhoto())
                .into(mImageView);
    }

    public void onBackArrowClicked(View view) {
        getActivity().onBackPressed();
    }

    public void onAddRatingClicked(View view) {
        mRatingDialog.show(getActivity().getSupportFragmentManager(), RatingDialogFragment.TAG);
    }

    //@Override
    public void onRating(Rating rating) {
        // In a transaction, add the new rating and update the aggregate totals
        addRating(mJobRef, rating)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Rating added");

                        // Hide keyboard and scroll to top
                        hideKeyboard();
                        mRatingsRecycler.smoothScrollToPosition(0);
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Add rating failed", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(getView().findViewById(android.R.id.content), "Failed to add rating",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
