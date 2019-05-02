package com.asuc.asucmobile.domain.repository;

import android.util.Log;

import com.asuc.asucmobile.domain.models.CategoryLoc;
import com.asuc.asucmobile.infrastructure.FordGoBikeFirestoreRepository;
import com.asuc.asucmobile.infrastructure.MentalHealthFirestoreRepository;
import com.asuc.asucmobile.infrastructure.MicrowaveFirestoreRepository;
import com.asuc.asucmobile.infrastructure.NapPodFirestoreRepository;
import com.asuc.asucmobile.infrastructure.PrinterFirestoreRepository;
import com.asuc.asucmobile.infrastructure.WaterFountainFirestoreRepository;
import com.asuc.asucmobile.values.MapIconCategories;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

/**
 * Aggregates all CategoryLocs into custom repository for hashmap as MapsFragment expects
 */
public class CategoryLocDomainRepository implements MultiRepository<String, CategoryLoc> {

    public static final String TAG = "CategoryLocDomain";
    private Repository<CategoryLoc> mMentalHealthRepository;
    private Repository<CategoryLoc> mMicrowaveRepository;
    private Repository<CategoryLoc> mNapPodRepository;
    private Repository<CategoryLoc> mPrinterRepository;
    private Repository<CategoryLoc> mWaterFountainRepository;
    private Repository<CategoryLoc> mFordGoBikeRepository;

    public CategoryLocDomainRepository(FirebaseFirestore firestore) {
        Log.d(TAG, "Initialized");
        mMentalHealthRepository = new MentalHealthFirestoreRepository(firestore);
        mMicrowaveRepository = new MicrowaveFirestoreRepository(firestore);
        mNapPodRepository = new NapPodFirestoreRepository(firestore);
        mPrinterRepository = new PrinterFirestoreRepository(firestore);
        mWaterFountainRepository = new WaterFountainFirestoreRepository(firestore);
        mFordGoBikeRepository = new FordGoBikeFirestoreRepository(firestore);
    }

    /**
     * Calls all scanalls of repositories. TODO: figure out a way/flow to have aggregate failure tracking
     * @param map
     * @param indivcallback
     * @return
     */
    public Map<String, List<CategoryLoc>> scanAll(final Map<String, List<CategoryLoc>> map, RepositoryCallback<CategoryLoc> indivcallback) {

        // empty previous
        for (List<CategoryLoc> list : map.values()) {
            list.clear();
        }

        mMentalHealthRepository.scanAll(map.get(MapIconCategories.MENTAL_HEALTH), indivcallback);
        mMicrowaveRepository.scanAll(map.get(MapIconCategories.MICROWAVE), indivcallback);
        mNapPodRepository.scanAll(map.get(MapIconCategories.NAP_POD), indivcallback);
        mPrinterRepository.scanAll(map.get(MapIconCategories.PRINTER), indivcallback);
        mWaterFountainRepository.scanAll(map.get(MapIconCategories.WATER_FOUNTAIN), indivcallback);
        mFordGoBikeRepository.scanAll(map.get(MapIconCategories.FORD_GO_BIKE), indivcallback);

        return map;
    }
}
