package net.iaround.tools;


import android.os.Environment;

import net.iaround.conf.Common;

import java.io.File;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-6-3 下午10:01:38
 * @ClassName PathUtil.java
 * @Description: 遇见中关于路径、前缀、后缀的工具类
 */

public class PathUtil {

    private static final String APKDir = "apkcache/";
    private static final String HTTPPrefix = "http://";
    private static final String PicDir = "proPics/";
    private static final String GIFPostfix = ".gif";
    private static final String DynamicFacePostfix = ".dynamic";// 动态表情贴图后缀
    private static final String StaticFacePostfix = ".static";// 静态表情贴图后缀
    private static final String PNGPostfix = ".png";
    private static final String FILEPrefix = "file://";
    private static final String JPGPostfix = ".jpg";
    private static final String FaceDir = "face/";//清除缓存的时候不删除
    private static final String TMPPostfix = ".tmp";// 临时文件
    private static final String ImageLoaderDir = "cacheimage_new/";
    private static final String VideoDir = "cacheVideo/";
    private static final String VideoPostfix = ".3gp";
    private static final String KeepDir = "keepcache/";//保留缓存文件夹，在清除缓存的时候不删除
    private static final String BufferDir = "cachebuffer/";
    // 6.0
    // 保存各个圈子的消息接收状态和圈助手开关状态，uid+文件名 做md5
    private static final String GROUP_MSG_STATUS_PROFIX = "groupMsgStatus";
    private static final String UserDir = "cacheuser/";
    // 用于圈子列表保存时的文件名字后缀，(（loginUserID + GROUP_LIST_PROFIX）做md5
    private static final String GROUP_LIST_PROFIX = "groupList";
    // 用于联系人保存时的文件名字后缀，（loginUserID + CONTACTS_PROFIX）做md5
    private static final String CONTACTS_PROFIX = "contacts";
    private static final String AudioDir = "cacheAudio/";
    private static final String MP3Postfix = ".mp3";
    // 用于保存长备注信息时的文件名字后缀，（loginUserID + LONG_NOTE_PROFIX）做md5
    private static final String LONG_NOTE_PROFIX = "longNote";
    // 用于保存[最新的一条我的动态]的文件名字后缀,（loginUserID + DYNAMIC_MY_NEW_PROFIX）做md5
    private static final String DYNAMIC_MY_NEW_PROFIX = "dynamicMyNew";
    // 用于保存动态中心的前十条动态的文件名字后缀,（loginUserID + DYNAMIC_CENTER_PROFIX）做md5
    private static final String DYNAMIC_CENTER_PROFIX = "dynamicCenter";
    // 用于保存我的动态的前十条动态的文件名字后缀,（loginUserID + DYNAMIC_MINE_PROFIX）做md5
    private static final String DYNAMIC_MINE_PROFIX = "dynamicMine";

    //6.0
    //保存标签化数据统计的缓存文件，无需分辨用户，直接文件名MD5
    private static final String DATA_STATISTICS_CACHE = "dataStatistics";
    //目录，用于保存标签化数据统计的上报失败的数据文件，无需辨别用户，直接文件名MD5
    private static final String DATA_STATISTICS_FAILED_CACHE = "dataFailedCache";

    /**
     * 返回临时文件后缀
     *
     * @return .tmp
     */
    public static String getTMPPostfix() {
        return TMPPostfix;
    }

    /**
     * 返回表情的存放目录
     *
     * @return /iaround/face/
     */
    public static String getFaceDir() {
        return createDir(getSDPath() + FaceDir);
    }

    /**
     * 返回照片后缀
     *
     * @return .jpg
     */
    public static String getJPGPostfix() {
        return JPGPostfix;
    }

    /**
     * 获取图片的旋转后的路径
     *
     * @return
     */
    public static String getImageRotatePath(String path) {
        return PathUtil.getPictureDir() + CryptoUtil.generate(path);
    }

    /**
     * 返回文件类型前缀
     *
     * @return file://
     */
    public static String getFILEPrefix() {
        return FILEPrefix;
    }

    /**
     * 返回照片后缀
     *
     * @return .png
     */
    public static String getPNGPostfix() {
        return PNGPostfix;
    }

    /**
     * 返回修改文件后缀名后静态表情贴图后缀
     *
     * @return .static
     */
    public static String getStaticFacePostfix() {
        return StaticFacePostfix;
    }

    /**
     * 返回动态表情后缀
     *
     * @return .gif
     */
    public static String getGifPostfix() {
        return GIFPostfix;
    }

    /**
     * 返回修改文件后缀名后动态表情贴图后缀
     *
     * @return .dynamic
     */
    public static String getDynamicFacePostfix() {
        return DynamicFacePostfix;
    }

    /**
     * 返回图片存放目录
     *
     * @return /iaround/proPics/
     */
    public static String getPictureDir() {
        return createDir(getSDPath() + PicDir);
    }

    /**
     * 返回Http前缀
     *
     * @return http://
     */
    public static String getHTTPPrefix() {
        return HTTPPrefix;
    }

    /**
     * 返回APK的存放目录
     *
     * @return /iaround/apkcache/
     */
    public static String getAPKCacheDir() {

        return createDir(getSDPath() + APKDir);
    }

    private static String createDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dirPath;
    }

    /**
     * 获取遇见的根目录
     *
     * @return 如果SD卡存在返回/iaround/目录，否者返回/data/data/net.iaround/iaround/
     */
    public static String getSDPath() {
        String sdDir = "";
        if (isSDcardExist()) {
            sdDir = Environment.getExternalStorageDirectory() + "/iaround/";// 获取跟目录

        } else {
            sdDir = "/data/data/net.iaround/";
            // sdDir = Environment.getDataDirectory() + "/iAround/";
        }

        File file = new File(sdDir);
        if (!file.exists()) {
            file.mkdirs();
            if (!file.exists()) {
                sdDir = "/data/data/net.iaround/";
                File tryfile = new File(sdDir);
                if (!tryfile.exists()) {
                    tryfile.mkdirs();
                }
            }
        }
        return sdDir;
    }

    /**
     * 判断sd卡是否存在
     */
    public static boolean isSDcardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 返回ImageLoader的存放目录
     *
     * @return /iaround/cacheimage_new/
     */
    public static String getImageLoaderDir() {
        return createDir(getSDPath() + ImageLoaderDir);
    }

    /**
     * 返回视频存放目录
     *
     * @return /iaround/cacheVideo/
     */
    public static String getVideoDir() {
        return createDir(getSDPath() + VideoDir);
    }

    /**
     * 返回视频后缀
     *
     * @return .3gp
     */
    public static String get3GPPostfix() {
        return VideoPostfix;
    }

    public static String getGroupMsgStatus() {
        return getMD5UserCacheDir(GROUP_MSG_STATUS_PROFIX);
    }

    private static String getMD5UserCacheDir(String fileProfix) {
        String fileName = Common.getInstance().loginUser.getUid() + fileProfix;
        String md5FileName = CryptoUtil.md5(fileName);
        String absolutePath = PathUtil.getUserCacheDir() + md5FileName;
        return absolutePath;
    }

    /**
     * 返回用户信息的存放目录
     *
     * @return /iaround/cacheuser/
     */
    public static String getUserCacheDir() {
        return createDir(getSDPath() + UserDir);
    }

    /**************************
     * 获取用户缓存文件路径
     *********************************/
    public static String getGroupListFilePath() {
        return getMD5UserCacheDir(GROUP_LIST_PROFIX);
    }

    /**************************
     * 获取用户缓存文件路径
     *********************************/

    public static String getContactsFilePath() {
        return getMD5UserCacheDir(CONTACTS_PROFIX);
    }

    /**
     * 返回语音存放目录
     *
     * @return /iaround/cacheAudio/
     */
    public static String getAudioDir() {
        return createDir(getSDPath() + AudioDir);
    }


    /**
     * 返回音频后缀
     *
     * @return .mp3
     */
    public static String getMP3Postfix() {
        return MP3Postfix;
    }

    public static String getLongNoteFilePath() {
        return getMD5UserCacheDir(LONG_NOTE_PROFIX);
    }

    public static String getDynamicMyNewFilePath() {
        return getMD5UserCacheDir(DYNAMIC_MY_NEW_PROFIX);
    }

    public static String getDynamicCenterFilePath() {
        return getMD5UserCacheDir(DYNAMIC_CENTER_PROFIX);
    }

    public static String getDynamicMineFilePath() {
        return getMD5UserCacheDir(DYNAMIC_MINE_PROFIX);
    }

    public static boolean isExistMD5UserCacheFile(String sourceUrlPath) {

        // 把缩略图，保存到imageLoader的文件夹中去
        // String targetThuPath = CommonFunction.thumPicture( sourceUrlPath );
        String pathDir = PathUtil.getImageLoaderDir();
        String sha1Name = CryptoUtil.generate(sourceUrlPath);

        String layer1 = sha1Name.substring(0, 2);
        String layer2 = sha1Name.substring(2, 4);
        String parentDir = pathDir + layer1 + "/" + layer2 + "/";

        File file = new File(parentDir + sha1Name);

        return file.exists();

    }

    /**
     * 返回保留缓存信息的存放目录
     */
    public static String getKeepCacheDir() {
        return createDir(getSDPath() + KeepDir);
    }

    public static String getDataStatisticsFilePath() {
        String md5FileName = CryptoUtil.md5(DATA_STATISTICS_CACHE);
        String absolutePath = PathUtil.getKeepCacheDir() + md5FileName;
        return absolutePath;
    }

    public static String getDataStatisticsFailedDirectory() {
        String md5Name = CryptoUtil.md5(DATA_STATISTICS_FAILED_CACHE);
        String directoryPath = getKeepCacheDir() + md5Name;
        File file = new File(directoryPath);
        if (!file.exists())
            file.mkdir();
        return directoryPath;
    }


    /**
     * 返回数据缓存目录
     */
    public static String getBufferCacheDir() {
        return createDir(getSDPath() + BufferDir);
    }

    public static String getExistMD5UserCacheFile(String sourceUrlPath) {

        // 把缩略图，保存到imageLoader的文件夹中去
        // String targetThuPath = CommonFunction.thumPicture( sourceUrlPath );
        String pathDir = PathUtil.getImageLoaderDir();
        String sha1Name = CryptoUtil.generate(sourceUrlPath);

        String layer1 = sha1Name.substring(0, 2);
        String layer2 = sha1Name.substring(2, 4);
        String parentDir = pathDir + layer1 + "/" + layer2 + "/";

        File file = new File(parentDir + sha1Name);

        return file.getPath();
    }
}
