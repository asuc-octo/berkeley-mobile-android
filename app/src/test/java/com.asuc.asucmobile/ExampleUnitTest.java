package com.asuc.asucmobile;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;

/**
 * Created by Courtney on 9/18/18.
 */

public class ExampleUnitTest {
    @Test
    public void add_isCorrect() throws Exception {
        assertEquals(4, Utils.addNumbers(2 , 2));
    }
    @Test
    public void add_twodigits_isCorrect() throws Exception {
        assertEquals(44, Utils.addNumbers(22 , 22));
    }
    @Test
    public void add_big_isCorrect() throws Exception {
        assertEquals(4444, Utils.addNumbers(2222 , 2222));
    }

}
