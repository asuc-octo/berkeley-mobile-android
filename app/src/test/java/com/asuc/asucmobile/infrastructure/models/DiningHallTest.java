package com.asuc.asucmobile.infrastructure.models;

import org.junit.Before;
import org.junit.Test;

public class DiningHallTest {

    private DiningHall diningHall;

    @Before
    public void setUp() {
        diningHall = DiningHall.builder()
                .id("Foothill")
                .build();
    }

    @Test
    public void diningHallBuilderTest() {
        assert diningHall.id != null && diningHall.getId().equals("Foothill");
    }
}
