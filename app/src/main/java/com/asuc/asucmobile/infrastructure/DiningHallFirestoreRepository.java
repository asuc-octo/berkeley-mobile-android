package com.asuc.asucmobile.infrastructure;

import android.support.annotation.NonNull;
import android.util.Log;

import com.asuc.asucmobile.domain.models.DiningHall;
import com.asuc.asucmobile.infrastructure.transformers.DiningHallTransformer;
import com.asuc.asucmobile.values.FirebaseCollectionNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.inject.Inject;

import dagger.Provides;


/**
 * Repository wrapping CRUD operations for DiningHalls around Firebase
 * TODO: probably make an interface around this later
 */
public class DiningHallFirestoreRepository implements Repository<DiningHall> {

    public static final String TAG = "DiningHallFirestore";

    private DiningHallTransformer mTransformer;
    private CollectionReference mRef;

    public DiningHallFirestoreRepository(FirebaseFirestore firestore, DiningHallTransformer transformer) {
        Log.d(TAG, "Initialized");
        mTransformer = transformer;
        mRef = firestore.collection(FirebaseCollectionNames.DINING_HALL);
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
