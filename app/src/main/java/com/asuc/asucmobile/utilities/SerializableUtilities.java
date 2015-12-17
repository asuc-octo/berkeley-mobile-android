package com.asuc.asucmobile.utilities;


import com.asuc.asucmobile.main.ListOfFavorites;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Victor on 12/4/15.
 */
public class SerializableUtilities {

    public static Object loadSerializedObject()
    {
        try
        {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("/sdcard/save_object.bin"));
            Object o = ois.readObject();
            return o;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static void saveObject(ListOfFavorites lof){
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("/sdcard/save_object.bin"))); //Select where you wish to save <span id="IL_AD6" class="IL_AD">the file</span>...
            oos.writeObject(lof); // write the class as an 'object'
            oos.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
            oos.close();// close the stream
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
