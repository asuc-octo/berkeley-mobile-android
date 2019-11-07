package com.asuc.asucmobile.infrastructure;

import androidx.annotation.NonNull;
import android.util.Log;

import com.asuc.asucmobile.domain.repository.Repository;
import com.asuc.asucmobile.domain.repository.RepositoryCallback;
import com.asuc.asucmobile.infrastructure.transformers.LibraryTransformer;
import com.asuc.asucmobile.domain.models.Library;
import com.asuc.asucmobile.values.FirebaseCollectionNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class LibraryFirestoreRepository implements Repository<Library> {

    public static final String TAG = "LibraryFirestore";

    private LibraryTransformer mTransformer;
    private CollectionReference mRef;

    public LibraryFirestoreRepository(FirebaseFirestore firestore) {
        Log.d(TAG, "Initialized");
        mTransformer = new LibraryTransformer();
        mRef = firestore.collection(FirebaseCollectionNames.LIBRARY);
    }

    @Override
    public List<Library> scanAll(final List<Library> list, final RepositoryCallback<Library> callback) {
        Log.d(TAG, "Called scanAll");
        mRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, queryDocumentSnapshots.toString());
                if (queryDocumentSnapshots != null) {
                    list.clear();
                    list.addAll(mTransformer.libraryQSDomainTransformer(queryDocumentSnapshots));
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }
        });
        mRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Unable to retrieve library data");
                callback.onFailure();
            }
        });
        return list;
    }
}
