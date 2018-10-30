package com.asuc.asucmobile.infrastructure;

import com.asuc.asucmobile.GlobalApplication;
import com.asuc.asucmobile.infrastructure.transformers.LibraryTransformer;
import com.asuc.asucmobile.models.Library;
import com.asuc.asucmobile.values.FirebaseCollectionNames;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

public class LibraryRepository {

    @Inject
    FirebaseFirestore firestore;

    private LibraryTransformer mTransformer;


    private CollectionReference mRef;

    @Inject
    @Singleton
    public LibraryRepository() {
        GlobalApplication.getFirebaseComponent().inject(this);
        mTransformer = new LibraryTransformer();
        mRef = firestore.collection(FirebaseCollectionNames.LIBRARY);
    }

    public List<Library> scanAllLibraries(final List<Library> libraries) {
        mRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    libraries.clear();
                    libraries.addAll(mTransformer.libraryQSDomainTransformer(queryDocumentSnapshots));
                }
            }
        });
        return libraries;
    }
}
