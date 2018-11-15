package com.asuc.asucmobile.infrastructure;

import com.asuc.asucmobile.domain.models.DiningHall;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class DiningHallRepositoryTest {

    private Repository<DiningHall> repository;

    @Before
    public void setUp() {
        repository = new DiningHallTestRepository();
    }

    /**
     * Try serializing JSON for 10s
     */
    @Test(timeout=10000)
    public void testScanAll() {
        List<DiningHall> diningHallList = new ArrayList<>();
        repository.scanAll(diningHallList, new RepositoryCallback<DiningHall>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {

            }
        });
        while (diningHallList.isEmpty());
    }
}
