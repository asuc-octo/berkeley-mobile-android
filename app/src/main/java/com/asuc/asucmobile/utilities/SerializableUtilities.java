package com.asuc.asucmobile.utilities;


import android.content.Context;

import com.asuc.asucmobile.domain.main.ListOfFavorites;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializableUtilities {

    private static final String SAVE_FILE_NAME = "save_object.bin";

    private static ListOfFavorites favorites;

    public static Object loadSerializedObject(Context context)
    {
        if (favorites != null) {
            return favorites;
        }

        try {
            String[] files = context.fileList();
            for (String fileName : files) {
                if (fileName.equals(SAVE_FILE_NAME)) {
                    FileInputStream inputStream = context.openFileInput(SAVE_FILE_NAME);
                    InputStream buffer = new BufferedInputStream(inputStream);
                    ObjectInput input = new ObjectInputStream(buffer);

                    favorites = (ListOfFavorites) input.readObject();
                    return favorites;
                }
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static void saveObject(Context context, ListOfFavorites lof){
        favorites = lof;

        try
        {
            FileOutputStream outputStream = context.openFileOutput(SAVE_FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
            objectStream.writeObject(lof); // write the class as an 'object'
            objectStream.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
            objectStream.close(); // close the stream
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
