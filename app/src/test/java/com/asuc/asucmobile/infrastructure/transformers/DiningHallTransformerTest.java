package com.asuc.asucmobile.infrastructure.transformers;

import com.asuc.asucmobile.infrastructure.models.DiningHall;

import org.junit.Before;
import org.junit.Test;

public class DiningHallTransformerTest {

    DiningHallTransformer transformer;
    DiningHall infraDiningHall;
    com.asuc.asucmobile.domain.models.DiningHall domainDiningHall;

    @Before
    public void setUp() {

        transformer = new DiningHallTransformer();

        String id = "test_id";
        String name = "tet_name";

        infraDiningHall = DiningHall.builder()
                .id(id)
                .name(name)
                .build();

        domainDiningHall = com.asuc.asucmobile.domain.models.DiningHall.builder()
                .id(id)
                .name(name)
                .build();
    }

    @Test
    public void transformSingleSanityTest() {
        com.asuc.asucmobile.domain.models.DiningHall sanity = transformer.diningHallInfraDomainTransformer(infraDiningHall);
        assert sanity.isOpen() == domainDiningHall.isOpen();
        assert sanity.getName().equals(domainDiningHall.getName());
        assert sanity.getId().equals(domainDiningHall.getId());
    }

}
