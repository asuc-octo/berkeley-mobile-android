package com.asuc.asucmobile.infrastructure;

import com.asuc.asucmobile.GlobalApplication;
import com.asuc.asucmobile.infrastructure.transformers.DiningHallTransformer;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.values.FirebaseCollectionNames;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * Repository wrapping CRUD operations for DiningHalls around Firebase
 * TODO: probably make an interface around this later
 */
public class DiningHallRepository {

    @Inject
    FirebaseFirestore firestore;

    @Inject
    DiningHallTransformer transformer;


    private CollectionReference mRef;

    @Inject
    public DiningHallRepository() {
        GlobalApplication.getFirebaseComponent().inject(this);
        mRef = firestore.collection(FirebaseCollectionNames.DINING_HALL);
    }

    public List<DiningHall> scanAllDiningHalls() {
        QuerySnapshot snapshot = mRef.get().getResult();
        if (snapshot == null) {
            return new ArrayList<>();
        }
        return transformer.dingingHallQSDomainTransformer(snapshot);
    }
}
