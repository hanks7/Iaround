package net.iaround.connector;

import android.media.AudioRecord;

import net.iaround.tools.CommonFunction;
import net.iaround.tools.PathUtil;
import net.iaround.tools.im.FFT;
import net.iaround.ui.chat.ChatSendAudioTouchListener;
import net.iaround.ui.interfaces.AudioRecordCallBack;
import net.iaround.utils.Mp3Lame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-5-28 上午10:10:16
 * @ClassName AudioRecordThread.java
 * @Description: 音频转码的线程
 */

public class AudioRecordThread extends Thread {
    // 文件完整完整路径
    private String filePath = "";
    // 录音开始时间
    private long recordStartTime = 0;
    // 录音回调接口
    private AudioRecordCallBack callback = null;
    // 录音器
    private AudioRecord audioRecord = null;
    // 线程控制开关
    public boolean bActive = true;
    // 录音缓冲区大小
    public static int bufferSizeInBytes = ChatSendAudioTouchListener.bufferSizeInBytes;
    // 快速傅里叶变换对象（计算音量）构造快速傅里叶变换对象，用以计算录音音量
    private static FFT fft = new FFT((int) (Math.log((double) bufferSizeInBytes / 2) / Math.log((double) 2)));

    public AudioRecordThread(AudioRecord audioRecord, long recordStartTime, AudioRecordCallBack callback) {
        this.audioRecord = audioRecord;
        this.filePath = PathUtil.getAudioDir() + recordStartTime + PathUtil.getMP3Postfix();
        this.recordStartTime = recordStartTime;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            // 创建文件
            FileOutputStream fos = null;
            File file = new File(filePath);
            fos = new FileOutputStream(file);// 建立一个可存取字节的文件

            // 开始发送一条新的语音消息
            CommonFunction.log("AudioRecordThread", "callback.AudioRecordStart()");
            callback.AudioRecordStart(recordStartTime, filePath);

            // 准备缓冲区
            byte[] pcmData = new byte[bufferSizeInBytes];
            byte[] mp3Data = new byte[bufferSizeInBytes];
            // 这个用计算读取数据失败的情况,如果次数大于10次那么认为这个录音没获得权限,小米手机就算开了权限,开始的时候也会读不到数据,所以加个次数限制
            int roundTimes = 0;
            // 保存数据
            while (bActive == true) {
                int readsize = audioRecord.read(pcmData, 0, bufferSizeInBytes);
                CommonFunction.log("AudioRecordThread", "audioRecord.read=" + readsize);
                if (readsize > 0) {
                    try {

                        if (readsize / 2 >= fft.FFT_N) {
                            float realIO[] = new float[fft.FFT_N];
                            int i, j;
                            for (i = j = 0; i < fft.FFT_N; i++, j += 2) {
                                realIO[i] = (pcmData[j + 1] << 8 | pcmData[j] & 0xff) / 32768.0f;
                            }
                            double dVolume = 0.0f;
                            for (i = 0; i < realIO.length; i++) {
                                dVolume += Math.abs(realIO[i]);
                            }
                            dVolume = 60 * Math.log10(1 + dVolume); // 分贝值

                            if (dVolume <= 0) {
                                roundTimes++;
                                if (roundTimes >= 10) {
                                    bActive = false;
                                    callback.AudioRecordError(recordStartTime);
                                    break;
                                }
                            } else {
                                callback.AudioVolumeFeedback(dVolume);
                            }
                        }

                        // 压缩成mp3格式
                        int mp3len = Mp3Lame.encodeBuffer(pcmData, readsize, mp3Data, bufferSizeInBytes);
                        if (mp3len > 0) {
                            // 写文件
                            fos.write(mp3Data, 0, mp3len);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (readsize == AudioRecord.ERROR_INVALID_OPERATION)// 录音权限未获取
                {
                    roundTimes++;
                    if (roundTimes >= 10) {
                        bActive = false;
                        callback.AudioRecordError(recordStartTime);
                        break;
                    }
                }
            }
            CommonFunction.log("AudioRecordThread", "out of");
            fos.close();// 关闭写入流
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            callback.AudioRecordEnd(recordStartTime, filePath);
            audioRecord = null;
            callback = null;
        }
    }

}
