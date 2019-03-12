package com.asuc.asucmobile.infrastructure;

import android.support.annotation.NonNull;
import android.util.Log;

import com.asuc.asucmobile.domain.models.Resource;
import com.asuc.asucmobile.domain.repository.Repository;
import com.asuc.asucmobile.domain.repository.RepositoryCallback;
import com.asuc.asucmobile.infrastructure.transformers.ResourceTransformer;
import com.asuc.asucmobile.values.FirebaseCollectionNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ResourceFirestoreRepository implements Repository<Resource> {


    public static final String TAG = " ResourceFirestore";

    private ResourceTransformer mTransformer;
    private CollectionReference mRef;

    public ResourceFirestoreRepository(FirebaseFirestore firestore) {
        Log.d(TAG, "Initialized");
        mTransformer = new ResourceTransformer();
        mRef = firestore.collection(FirebaseCollectionNames.RESOURCE);
    }

    @Override
    public List<Resource> scanAll(final List<Resource> list, final RepositoryCallback<Resource> callback) {
        Log.d(TAG, "Called scanAll");
        mRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    list.clear();
                    list.addAll(mTransformer.resourceQSDomainTransformer(queryDocumentSnapshots));
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }

            }
        });
        mRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Unable to retrieve data");
                callback.onFailure();
            }
        });
        return list;
    }
}
