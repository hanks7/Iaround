package net.iaround.connector;

import net.iaround.interfaces.AudioSendDataCallback;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-5-28 上午10:11:00
 * @ClassName SendDataThread.java
 * @Description: 音频数据的发送线程
 * 其实真正发送数据不是通过该类,而是把读取的数据反馈给实现了接口的类
 */

public class SendDataThread extends Thread {
    //录音文件的完整路径
    private String audioFileName = "";
    //录音录制的时间/或者重新发送的时间
    private long flag = 0;
    //线程的控制开关
    private boolean bActive = true;

    //读取数据的回调接口
    private AudioSendDataCallback readDataCallback;

    /**
     * 发送数据的线程
     *
     * @param fileName      文件名字
     * @param bRecordActive 文件录制是否在进行，true为是，false为否
     */
    public SendDataThread(String fileName, boolean bRecordActive, long recordFlag, AudioSendDataCallback callback) {
        audioFileName = fileName;
        flag = recordFlag;
        bActive = bRecordActive;
        readDataCallback = callback;
    }


    @Override
    public void run() {
        try {
            File file = new File(audioFileName);
            file.mkdirs();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 5];
            int readlen = 0;
            int rank = 0;
            readDataCallback.DataSendStart(flag);
            while (bActive || readlen > -1) {
                // 从文件流中读取数据
                readlen = fis.read(buffer, 0, buffer.length);
                // 发送数据
                if (readlen > 0) {
                    //传入文件的真实长度，而不是默认的2048长度
                    rank++;
                    readDataCallback.DataSendProgress(flag, rank, buffer, readlen);
                }
            }
            readDataCallback.DataSendFinish(flag, rank);
            // 关闭文件流
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
            readDataCallback.DataSendError(flag);
        }
    }

    /**
     * 停止线程
     */
    public void stopThread() {
        bActive = false;
    }
}
