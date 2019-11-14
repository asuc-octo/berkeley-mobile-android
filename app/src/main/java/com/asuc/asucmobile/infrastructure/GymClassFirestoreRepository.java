package com.asuc.asucmobile.infrastructure;

import androidx.annotation.NonNull;
import android.util.Log;

import com.asuc.asucmobile.domain.models.GymClass;
import com.asuc.asucmobile.domain.repository.Repository;
import com.asuc.asucmobile.domain.repository.RepositoryCallback;
import com.asuc.asucmobile.infrastructure.transformers.GymClassTransformer;
import com.asuc.asucmobile.values.FirebaseCollectionNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class GymClassFirestoreRepository implements Repository<GymClass> {

    public static final String TAG = "GymClassFirestore";

    private GymClassTransformer mTransformer;
    private CollectionReference mRef;

    public GymClassFirestoreRepository(FirebaseFirestore firestore) {
        Log.d(TAG, "Initialized");
        mTransformer = new GymClassTransformer();
        mRef = firestore.collection(FirebaseCollectionNames.GYM_CLASS);
    }

    @Override
    public List<GymClass> scanAll(final List<GymClass> list, final RepositoryCallback<GymClass> callback) {
        Log.d(TAG, "Called scanAll");
        mRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, queryDocumentSnapshots.toString());
                if (queryDocumentSnapshots != null) {
                    list.clear();
                    list.addAll(mTransformer.gymClassQSDomainTransformer(queryDocumentSnapshots));
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }
        });
        mRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Unable to retrieve gym class data");
                callback.onFailure();
            }
        });
        return list;
    }

}
