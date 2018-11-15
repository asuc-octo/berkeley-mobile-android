package com.asuc.asucmobile.infrastructure;

import android.support.annotation.NonNull;
import android.util.Log;

import com.asuc.asucmobile.GlobalApplication;
import com.asuc.asucmobile.infrastructure.transformers.LibraryTransformer;
import com.asuc.asucmobile.domain.models.Library;
import com.asuc.asucmobile.values.FirebaseCollectionNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

public class LibraryFirestoreRepository implements Repository<Library> {

    public static final String TAG = "LibraryFirestore";

    private LibraryTransformer mTransformer;
    private CollectionReference mRef;

    public LibraryFirestoreRepository(FirebaseFirestore firestore) {
        mTransformer = new LibraryTransformer();
        mRef = firestore.collection(FirebaseCollectionNames.LIBRARY);
    }

    @Override
    public List<Library> scanAll(final List<Library> libraries, final RepositoryCallback<Library> callback) {
        mRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    libraries.clear();
                    libraries.addAll(mTransformer.libraryQSDomainTransformer(queryDocumentSnapshots));
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
        return libraries;
    }
}
