package com.asuc.asucmobile.infrastructure;

import com.asuc.asucmobile.GlobalApplication;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.values.FirebaseCollectionNames;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.inject.Inject;


/**
 * Repository wrapping CRUD operations for DiningHalls around Firebase
 * TODO: probably make an interface around this later
 */
public class DiningHallRepository {

    @Inject
    FirebaseFirestore firestore;

    private CollectionReference mRef;

    public DiningHallRepository() {
        GlobalApplication.getFirebaseComponent().inject(this);
        mRef = firestore.collection(FirebaseCollectionNames.DINING_HALL);

    }

    public List<DiningHall> scanAllDiningHalls() {
        mRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.getDocuments();
            }
        });
        return null;
    }




}
