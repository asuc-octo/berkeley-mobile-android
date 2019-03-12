package com.asuc.asucmobile.domain.fragments;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class MapsFragmentTest {

    @Test
    public void markers_are_false() throws Exception {
        MapsFragment mp = new MapsFragment();
        assertEquals(mp.isBottlesShown(), false);
        assertEquals(mp.isSleepShown(), false);
        assertEquals(mp.isMicrowavesShown(), false);
    }
}
