package net.iaround.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Description Model层用于处理业务成与数据层（数据库、文件）之间的处理
 * @Note 在5.2版本之前，model层有时用来解析服务端下发的json串，5.2以后对于下发的json串统一用Gson解析。
 */

public class Model {

    /**
     * 把文件转成对象
     */
    protected Object getBufferFromFile(String path) {
        File bufferFile = new File(path);
        if (bufferFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(bufferFile);
                GZIPInputStream gzis = new GZIPInputStream(fis);
                ObjectInputStream ois = new ObjectInputStream(gzis);
                Object obj = ois.readObject();

                ois.close();
                gzis.close();
                fis.close();

                return obj;
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                bufferFile.delete();
                return null;
            }
        }
        return null;
    }

    /**
     * 把对象保存成文件
     */
    protected boolean saveBufferToFile(String path, Object obj) {
        File bufferFile = new File(path);

        if (bufferFile.exists()) {
            bufferFile.delete();
        }

        FileOutputStream fos;
        GZIPOutputStream gzos;
        ObjectOutputStream oos;
        try {
            fos = new FileOutputStream(bufferFile);
            gzos = new GZIPOutputStream(fos);
            oos = new ObjectOutputStream(gzos);
            oos.writeObject(obj);

            oos.flush();
            oos.close();
            gzos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            bufferFile.delete();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            bufferFile.delete();
            return false;
        }

        return true;
    }
}
