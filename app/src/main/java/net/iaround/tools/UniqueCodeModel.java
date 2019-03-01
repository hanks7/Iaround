package net.iaround.tools;

import android.os.Environment;

public class UniqueCodeModel extends Model {
    private static UniqueCodeModel instant;

    private String sdDir = Environment.getExternalStorageDirectory() + "/.2e24f5228cc47de8";

    public static UniqueCodeModel getInstant() {


        if (instant == null) {
            instant = new UniqueCodeModel();
        }
        return instant;
    }

    public void putUniqueCode(String code) {
        saveBufferToFile(sdDir, code);
    }

    public String getUniqueCode() {
        return (String) getBufferFromFile(sdDir);
    }

}
