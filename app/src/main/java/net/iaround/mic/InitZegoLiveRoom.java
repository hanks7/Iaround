package net.iaround.mic;

import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.constants.ZegoAvConfig;

import net.iaround.BaseApplication;
import net.iaround.conf.Config;
import net.iaround.tools.CommonFunction;

public class InitZegoLiveRoom {

    private static final String TAG = "InitZegoLiveRoom";
    private static ZegoLiveRoom sZegoLiveRoom = null;
    private static InitZegoLiveRoom sInstance;

    private InitZegoLiveRoom() {
    }

    public static InitZegoLiveRoom getInstance() {
        if (sInstance == null) {
            synchronized (InitZegoLiveRoom.class) {
                if (sInstance == null) {
                    sInstance = new InitZegoLiveRoom();
                }
            }
        }
        return sInstance;
    }

    public static synchronized void initZego() {
        CommonFunction.log(TAG, "initZego() into");
        InitZegoLiveRoom.unInitZego();
        if (sZegoLiveRoom == null) {
            sZegoLiveRoom = new ZegoLiveRoom();

            //业务类型
            ZegoLiveRoom.setBusinessType(0); //0 纯音频走CDN 2一对一视频+纯音频

            // 设置是否开启“测试环境”, true:开启 false:关闭
            ZegoLiveRoom.setTestEnv(false);

            // 设置设置调试模式
            if (Config.DEBUG == true) {
                ZegoLiveRoom.setVerbose(true);
            } else {
                ZegoLiveRoom.setVerbose(false);
            }

            // ！！！注意：这个Appid和signKey需要从server下发到App，避免在App中存储，防止盗用
            byte[] signKey = {(byte) 0x26, (byte) 0x40, (byte) 0x9e, (byte) 0xad, (byte) 0x23, (byte) 0xfa, (byte) 0x83, (byte) 0x03,
                    (byte) 0x34, (byte) 0xe8, (byte) 0xb8, (byte) 0x05, (byte) 0x4f, (byte) 0x83, (byte) 0xca, (byte) 0xf1,
                    (byte) 0x31, (byte) 0xf1, (byte) 0x5d, (byte) 0xa6, (byte) 0x5c, (byte) 0x18, (byte) 0x94, (byte) 0xf7,
                    (byte) 0x45, (byte) 0x4d, (byte) 0x87, (byte) 0x5b, (byte) 0x43, (byte) 0x8f, (byte) 0x68, (byte) 0x44};

            long appID = 2815417840L;
            ZegoLiveRoom.setAudioDeviceMode(2);

            // 初始化sdk
            boolean appId = sZegoLiveRoom.initSDK(appID, signKey, BaseApplication.appContext);

            // 初始化设置级别为"High"
            ZegoAvConfig zegoAvConfig = new ZegoAvConfig(ZegoAvConfig.Level.High);
            sZegoLiveRoom.setAVConfig(zegoAvConfig);
        }

        CommonFunction.log(TAG, "initZego() out");
    }


    public static synchronized void unInitZego() {
        CommonFunction.log(TAG, "unInitZego() into");
        if (sZegoLiveRoom != null) {
            sZegoLiveRoom.unInitSDK();
            sZegoLiveRoom = null;
        }
        CommonFunction.log(TAG, "unInitZego() out");
    }

    public static synchronized ZegoLiveRoom getZegoLiveRoom() {
        if (sZegoLiveRoom == null) {
            initZego();
        }
        return sZegoLiveRoom;
    }
}
