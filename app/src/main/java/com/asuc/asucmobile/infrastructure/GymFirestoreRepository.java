package com.asuc.asucmobile.infrastructure;

import android.support.annotation.NonNull;
import android.util.Log;

import com.asuc.asucmobile.domain.models.Gym;
import com.asuc.asucmobile.domain.repository.Repository;
import com.asuc.asucmobile.domain.repository.RepositoryCallback;
import com.asuc.asucmobile.infrastructure.transformers.GymTransformer;
import com.asuc.asucmobile.values.FirebaseCollectionNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class GymFirestoreRepository implements Repository<Gym> {

    public static final String TAG = "GymFirestore";

    private GymTransformer mTransformer;
    private CollectionReference mRef;

    public GymFirestoreRepository(FirebaseFirestore firestore) {
        Log.d(TAG, "Initialized");
        mTransformer = new GymTransformer();
        mRef = firestore.collection(FirebaseCollectionNames.GYM);
    }

    @Override
    public List<Gym> scanAll(final List<Gym> list, final RepositoryCallback<Gym> callback) {
        Log.d(TAG, "Called scanAll");
        mRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, queryDocumentSnapshots.toString());
                if (queryDocumentSnapshots != null) {
                    list.clear();
                    list.addAll(mTransformer.gymQSDomainTransformer(queryDocumentSnapshots));
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }
        });
        mRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Unable to retrieve gym data");
                callback.onFailure();
            }
        });
        return list;
    }
}
