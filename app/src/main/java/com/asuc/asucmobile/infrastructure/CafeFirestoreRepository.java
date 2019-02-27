package com.asuc.asucmobile.infrastructure;

import android.support.annotation.NonNull;
import android.util.Log;

import com.asuc.asucmobile.domain.models.Cafe;
import com.asuc.asucmobile.domain.repository.Repository;
import com.asuc.asucmobile.domain.repository.RepositoryCallback;
import com.asuc.asucmobile.infrastructure.transformers.CafeTransformer;
import com.asuc.asucmobile.values.FirebaseCollectionNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class CafeFirestoreRepository implements Repository<Cafe> {

    public static final String TAG = "CafeFirebase";

    private CafeTransformer mTransformer;
    private CollectionReference mRef;

    public CafeFirestoreRepository(FirebaseFirestore firestore, CafeTransformer transformer) {
        Log.d(TAG, "Initialized");
        mTransformer = transformer;
        mRef = firestore.collection(FirebaseCollectionNames.CAFE);
    }


    /**
     * Scan all dining halls
     * @param cafes
     * @return
     */
    @Override
    public List<Cafe> scanAll(final List<Cafe> cafes, final RepositoryCallback<Cafe> callback) {
        Log.d(TAG, "Called scanAll");
        mRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    cafes.clear();
                    cafes.addAll(mTransformer.cafeQSDomainTransformer(queryDocumentSnapshots));
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }
        });
        mRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Unable to retrieve dining hall data, please try again");
                callback.onFailure();
            }
        });
        return cafes;
    }
}
