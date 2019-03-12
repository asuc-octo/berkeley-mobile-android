package com.asuc.asucmobile.utilities;

import java.util.Collections;
import java.util.List;

public class ListUtils {
    public static List safe( List other ) {
        return other == null ? Collections.emptyList() : other;
    }
}
