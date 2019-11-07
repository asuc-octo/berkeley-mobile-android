package com.asuc.asucmobile.infrastructure;

import androidx.annotation.NonNull;
import android.util.Log;

import com.asuc.asucmobile.domain.models.DiningHall;
import com.asuc.asucmobile.domain.repository.Repository;
import com.asuc.asucmobile.domain.repository.RepositoryCallback;
import com.asuc.asucmobile.infrastructure.models.DiningHallWeek;
import com.asuc.asucmobile.infrastructure.transformers.DiningHallTransformer;
import com.asuc.asucmobile.values.FirebaseCollectionNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * Repository wrapping CRUD operations for DiningHalls around Firebase
 * TODO: probably make an interface around this later
 */
public class DiningHallFirestoreRepository implements Repository<DiningHall> {

    public static final String TAG = "DiningHallFirestore";

    private DiningHallTransformer mTransformer;
    private CollectionReference mRef;
    private DocumentReference mDocRef;
    private ArrayList<CollectionReference> mDateCollectionRefs;

    public DiningHallFirestoreRepository(FirebaseFirestore firestore) {
        Log.d(TAG, "Initialized");
        mTransformer = new DiningHallTransformer();
        mRef = firestore.collection(FirebaseCollectionNames.DINING_HALL);
        mDocRef = firestore.collection(FirebaseCollectionNames.DINING_HALL).document(FirebaseCollectionNames.DINING_HALL_DOCUMENT);
        mDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Log.d(TAG, documentSnapshot.toString());
                DiningHallWeek week = documentSnapshot.toObject(DiningHallWeek.class);
            }
        });
    }

    /**
     * Scan all dining halls
     * @param diningHalls
     * @return
     */
    @Override
    public List<DiningHall> scanAll(final List<DiningHall> diningHalls, final RepositoryCallback<DiningHall> callback) {
        Log.d(TAG, "Called scanAll");
        mRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    diningHalls.clear();
                    diningHalls.addAll(mTransformer.dingingHallQSDomainTransformer(queryDocumentSnapshots));
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }
        });
        mRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Unable to retrieve dining hall data");
                callback.onFailure();
            }
        });
        return diningHalls;
    }
}
