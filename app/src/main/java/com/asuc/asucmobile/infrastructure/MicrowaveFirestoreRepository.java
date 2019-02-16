package com.asuc.asucmobile.infrastructure;

import android.support.annotation.NonNull;
import android.util.Log;

import com.asuc.asucmobile.domain.models.CategoryLoc;
import com.asuc.asucmobile.infrastructure.transformers.CategoryLocTransformer;
import com.asuc.asucmobile.values.FirebaseCollectionNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MicrowaveFirestoreRepository implements Repository<CategoryLoc> {

    public static final String TAG = "MicrowaveFirestore";

    private CategoryLocTransformer mTransformer;
    private CollectionReference mRef;

    public MicrowaveFirestoreRepository(FirebaseFirestore firestore) {
        Log.d(TAG, "Initialized");
        mTransformer = new CategoryLocTransformer();
        mRef = firestore.collection(FirebaseCollectionNames.MICROWAVE);
    }

    @Override
    public List<CategoryLoc> scanAll(final List<CategoryLoc> list, final RepositoryCallback<CategoryLoc> callback) {
        Log.d(TAG, "Called scanAll");
        mRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, queryDocumentSnapshots.toString());
                if (queryDocumentSnapshots != null) {
                    list.clear();
                    list.addAll(mTransformer.categoryLocQSDomainTransformer(queryDocumentSnapshots));
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }
        });
        mRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Unable to retrieve microwave data");
                callback.onFailure();
            }
        });
        return list;
    }
}
