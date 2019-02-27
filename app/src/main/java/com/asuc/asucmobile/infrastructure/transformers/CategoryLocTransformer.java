package com.asuc.asucmobile.infrastructure.transformers;

import android.util.Log;

import com.asuc.asucmobile.infrastructure.models.CategoryLoc;
import com.asuc.asucmobile.infrastructure.models.OpenClose;
import com.asuc.asucmobile.infrastructure.models.MultiOpenClose;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CategoryLocTransformer {

    public static final String TAG = "CategoryLocTransformer";

    /**
     * Transform Firebase QuerySnapshot to domain model
     * @param qs
     * @return
     */
    public List<com.asuc.asucmobile.domain.models.CategoryLoc> categoryLocQSDomainTransformer(QuerySnapshot qs) {
        List<com.asuc.asucmobile.domain.models.CategoryLoc> categoryLocs = new ArrayList<>();
        com.asuc.asucmobile.infrastructure.models.CategoryLoc infraCategoryLoc = null;
        for (DocumentSnapshot documentSnapshot : qs.getDocuments()) {
            try {
                infraCategoryLoc = documentSnapshot.toObject(com.asuc.asucmobile.infrastructure.models.CategoryLoc.class);
                if (infraCategoryLoc != null) {
                    categoryLocs.add(categoryLocInfraDomainTransformer(infraCategoryLoc));
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }

        }
        return categoryLocs;
    }

    public com.asuc.asucmobile.domain.models.CategoryLoc categoryLocInfraDomainTransformer(CategoryLoc categoryLoc) {
        ArrayList<Date> weeklyOpen = new ArrayList<>();
        ArrayList<Date> weeklyClose = new ArrayList<>();

        if (categoryLoc.getOpenCloses() != null) {
            for (MultiOpenClose openClose : categoryLoc.getOpenCloses()) {
                if (openClose.getOpenTimes() != null) {
                    for (Long l : openClose.getOpenTimes()) {
                        weeklyOpen.add(new Date(l));
                    }
                }
                if (openClose.getCloseTimes() != null) {
                    for (Long l : openClose.getCloseTimes()) {
                        weeklyClose.add(new Date(l));
                    }
                }
            }
        }

        return com.asuc.asucmobile.domain.models.CategoryLoc.builder()
                .category(categoryLoc.getCategory())
                .description1(categoryLoc.getName()) //TODO: not sure where to put this
                .description2(categoryLoc.getDetails())
                .imagelink(categoryLoc.getImageLink())
                .lat(categoryLoc.getLatitude())
                .lon(categoryLoc.getLongitude()) // TODO: use the open close
                .build();
    }
    
}
