package net.iaround.tools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.ClipboardManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.BuildConfig;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.conf.KeyWord;
import net.iaround.connector.BaseHttp;
import net.iaround.connector.ConnectLogin;
import net.iaround.service.APKDownloadService;
import net.iaround.service.BackService;
import net.iaround.ui.activity.GuideActivity;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.activity.PhotoCropActivity;
import net.iaround.ui.activity.RegisterNewActivity;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.StartModel;
import net.iaround.ui.datamodel.WeiboState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * 公用的函数类
 *
 * @author linyg
 */
@SuppressWarnings("deprecation")
public class CommonFunction {

    public static enum NetType {
        WIFI,//wifi
        TYPE_MOBILE,//手机网络
        //        CMNET,//手机网络
//        CMWAP,
        NONE// 无网络
    }

    public static final int PICK_PHOTO_REQ = 0xff11;// 选择照片码
    public static final int TAKE_PHOTO_REQ = 0xff12;// 拍摄照片的请求码

    private static float density;
    private static FileWriter fw;
    public static long toastTime = 0;
    public static String lastString = "";
    public static Toast lastToast = null;
    // 渠道包id
    private static String packageID;

    public static boolean uiRunning = false; // 标记程序是否已经进入UI运行模式（false表示处于后台service）
    private static Process logcatProcess;

    private static MediaPlayer mediaPlayer; //只用于播放来消息提示
    private static Vibrator vibrator;

    public static boolean isActive;

    private static MediaPlayer mVideoChatMediaPlayer; //用来播放视频会话相关声音文件
    private static MediaPlayer mAudioChatMediaPlayer; //用来播放语音会话相关声音文件

    /**
     * @param context
     * @param bitmap
     * @return
     */
    public static Bitmap decomBackGroundPression(Context context, Bitmap bitmap) {
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        float srceenWidth = getScreenPixWidth(context);
        Matrix matrix = new Matrix();
        if (width > srceenWidth) {
            matrix.postScale(srceenWidth / width, srceenWidth / width);
        } else if (width < srceenWidth) {
            matrix.postScale(srceenWidth / width, srceenWidth / width);
        }
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) width,
                (int) height, matrix, true);
        int width2 = newBitmap.getWidth();
        int height2 = newBitmap.getHeight();
        System.out.println(width2 + "w" + height2);
        if (newBitmap != bitmap) {
            bitmap.recycle();
        }

        return newBitmap;

    }

    /**
     * 计算个性化距离显示
     *
     * @param distance 距离
     * @return String 显示内容
     */
    public static String covertSelfDistance(double distance) {
        String str = null;
        if (distance > 1000) {
            double d_distance = distance / 1000d;
            DecimalFormat df = new DecimalFormat("0.00");
            String result = df.format(d_distance);
            str = result + "km";
        } else {
            str = distance + "m";
        }
        return str;
    }

    /**
     * 转换魅力值符号数组
     */
    public static int[] getCharismaSymbol(int charisma) {
        int[] symbol = new int[6];

        symbol = new int[]
                {0, 0, 0, 0, 0, 0};
        if (charisma < 5) {
            symbol = new int[]
                    {0, 0, 0, 0, 0, 0};
        } else if (5 <= charisma && charisma <= 68) {
            symbol = new int[]
                    {1, 0, 0, 0, 0, 1};
        } else if (69 <= charisma && charisma <= 155) {
            symbol = new int[]
                    {1, 1, 0, 0, 0, 2};
        } else if (156 <= charisma && charisma <= 279) {
            symbol = new int[]
                    {1, 1, 1, 0, 0, 3};
        } else if (280 <= charisma && charisma <= 464) {
            symbol = new int[]
                    {1, 1, 1, 1, 0, 4};
        } else if (465 <= charisma && charisma <= 740) {
            symbol = new int[]
                    {1, 1, 1, 1, 1, 5};
        } else if (741 <= charisma && charisma <= 1143) {
            symbol = new int[]
                    {2, 0, 0, 0, 0, 6};
        } else if (1144 <= charisma && charisma <= 1715) {
            symbol = new int[]
                    {2, 1, 0, 0, 0, 7};
        } else if (1716 <= charisma && charisma <= 2504) {
            symbol = new int[]
                    {2, 1, 1, 0, 0, 8};
        } else if (2505 <= charisma && charisma <= 3564) {
            symbol = new int[]
                    {2, 1, 1, 1, 0, 9};
        } else if (3565 <= charisma && charisma <= 4955) {
            symbol = new int[]
                    {2, 1, 1, 1, 1, 10};
        } else if (4956 <= charisma && charisma <= 6743) {
            symbol = new int[]
                    {2, 2, 0, 0, 0, 11};
        } else if (6744 <= charisma && charisma <= 9000) {
            symbol = new int[]
                    {2, 2, 1, 0, 0, 12};
        } else if (9001 <= charisma && charisma <= 11804) {
            symbol = new int[]
                    {2, 2, 1, 1, 0, 13};
        } else if (11805 <= charisma && charisma <= 15239) {
            symbol = new int[]
                    {2, 2, 1, 1, 1, 14};
        } else if (15240 <= charisma && charisma <= 19395) {
            symbol = new int[]
                    {2, 2, 2, 0, 0, 15};
        } else if (19396 <= charisma && charisma <= 24368) {
            symbol = new int[]
                    {2, 2, 2, 1, 0, 16};
        } else if (24369 <= charisma && charisma <= 30260) {
            symbol = new int[]
                    {2, 2, 2, 1, 1, 17};
        } else if (30261 <= charisma && charisma <= 37179) {
            symbol = new int[]
                    {2, 2, 2, 2, 0, 18};
        } else if (37180 <= charisma && charisma <= 45239) {
            symbol = new int[]
                    {2, 2, 2, 2, 1, 19};
        } else if (45240 <= charisma && charisma <= 54560) {
            symbol = new int[]
                    {2, 2, 2, 2, 2, 20};
        } else if (54561 <= charisma && charisma <= 65268) {
            symbol = new int[]
                    {3, 0, 0, 0, 0, 21};
        } else if (65269 <= charisma && charisma <= 77495) {
            symbol = new int[]
                    {3, 1, 0, 0, 0, 22};
        } else if (77496 <= charisma && charisma <= 91379) {
            symbol = new int[]
                    {3, 1, 1, 0, 0, 23};
        } else if (91380 <= charisma && charisma <= 107064) {
            symbol = new int[]
                    {3, 1, 1, 1, 0, 24};
        } else if (107065 <= charisma && charisma <= 124700) {
            symbol = new int[]
                    {3, 1, 1, 1, 1, 25};
        } else if (124701 <= charisma && charisma <= 144443) {
            symbol = new int[]
                    {3, 2, 0, 0, 0, 26};
        } else if (144444 <= charisma && charisma <= 166455) {
            symbol = new int[]
                    {3, 2, 2, 0, 0, 27};
        } else if (166456 <= charisma && charisma <= 190904) {
            symbol = new int[]
                    {3, 2, 2, 2, 0, 28};
        } else if (190905 <= charisma && charisma <= 217964) {
            symbol = new int[]
                    {3, 2, 2, 2, 2, 29};
        } else if (217965 <= charisma && charisma <= 247815) {
            symbol = new int[]
                    {3, 3, 0, 0, 0, 30};
        } else if (247816 <= charisma && charisma <= 280643) {
            symbol = new int[]
                    {3, 3, 1, 0, 0, 31};
        } else if (280644 <= charisma && charisma <= 316640) {
            symbol = new int[]
                    {3, 3, 1, 1, 0, 32};
        } else if (316641 <= charisma && charisma <= 356004) {
            symbol = new int[]
                    {3, 3, 1, 1, 1, 33};
        } else if (356005 <= charisma && charisma <= 398939) {
            symbol = new int[]
                    {3, 3, 2, 0, 0, 34};
        } else if (398940 <= charisma && charisma <= 445655) {
            symbol = new int[]
                    {3, 3, 2, 2, 0, 35};
        } else if (445656 <= charisma && charisma <= 496368) {
            symbol = new int[]
                    {3, 3, 2, 2, 2, 36};
        } else if (496369 <= charisma && charisma <= 551300) {
            symbol = new int[]
                    {3, 3, 3, 0, 0, 37};
        } else if (551301 <= charisma && charisma <= 610679) {
            symbol = new int[]
                    {3, 3, 3, 1, 0, 38};
        } else if (610680 <= charisma && charisma <= 674739) {
            symbol = new int[]
                    {3, 3, 3, 1, 1, 39};
        } else if (674740 <= charisma && charisma <= 743720) {
            symbol = new int[]
                    {3, 3, 3, 2, 0, 40};
        } else if (743721 <= charisma && charisma <= 817868) {
            symbol = new int[]
                    {3, 3, 3, 2, 2, 41};
        } else if (817869 <= charisma && charisma <= 897435) {
            symbol = new int[]
                    {3, 3, 3, 3, 0, 42};
        } else if (897436 <= charisma && charisma <= 982679) {
            symbol = new int[]
                    {3, 3, 3, 3, 1, 43};
        } else if (982680 <= charisma && charisma <= 1073864) {
            symbol = new int[]
                    {3, 3, 3, 3, 2, 44};
        } else if (1073865 <= charisma && charisma <= 1171260) {
            symbol = new int[]
                    {3, 3, 3, 3, 3, 45};
        } else if (1171261 <= charisma && charisma <= 1275143) {
            symbol = new int[]
                    {4, 0, 0, 0, 0, 46};
        } else if (1275143 <= charisma && charisma <= 1385795) {
            symbol = new int[]
                    {4, 1, 0, 0, 0, 47};
        } else if (1385796 <= charisma && charisma <= 1503504) {
            symbol = new int[]
                    {4, 1, 1, 0, 0, 48};
        } else if (1503505 <= charisma && charisma <= 1628564) {
            symbol = new int[]
                    {4, 1, 1, 1, 0, 49};
        } else if (1628565 <= charisma && charisma <= 1761275) {
            symbol = new int[]
                    {4, 1, 1, 1, 1, 50};
        } else if (1761276 <= charisma && charisma <= 1901943) {
            symbol = new int[]
                    {4, 2, 0, 0, 0, 51};
        } else if (1901944 <= charisma && charisma <= 2050880) {
            symbol = new int[]
                    {4, 2, 2, 0, 0, 52};
        } else if (2050881 <= charisma && charisma <= 2208404) {
            symbol = new int[]
                    {4, 2, 2, 2, 0, 53};
        } else if (2208405 <= charisma && charisma <= 2374839) {
            symbol = new int[]
                    {4, 2, 2, 2, 2, 54};
        } else if (2374840 <= charisma && charisma <= 2550515) {
            symbol = new int[]
                    {4, 3, 0, 0, 0, 55};
        } else if (2550516 <= charisma && charisma <= 2735768) {
            symbol = new int[]
                    {4, 3, 3, 0, 0, 56};
        } else if (2735769 <= charisma && charisma <= 2930940) {
            symbol = new int[]
                    {4, 3, 3, 3, 0, 57};
        } else if (2930941 <= charisma && charisma <= 3136379) {
            symbol = new int[]
                    {4, 3, 3, 3, 3, 58};
        } else if (3136380 <= charisma && charisma <= 3352439) {
            symbol = new int[]
                    {4, 4, 0, 0, 0, 59};
        } else if (3352440 <= charisma && charisma <= 3579480) {
            symbol = new int[]
                    {4, 4, 1, 0, 0, 60};
        } else if (3579481 <= charisma && charisma <= 14545328) {
            symbol = new int[]
                    {4, 4, 1, 1, 0, 61};
        } else if (14545329 <= charisma && charisma <= 26300357) {
            symbol = new int[]
                    {4, 4, 1, 1, 1, 62};
        } else if (26300358 <= charisma && charisma <= 38886149) {
            symbol = new int[]
                    {4, 4, 2, 0, 0, 63};
        } else if (38886150 <= charisma && charisma <= 52345714) {
            symbol = new int[]
                    {4, 4, 2, 2, 0, 64};
        } else if (52345715 <= charisma && charisma <= 66723514) {
            symbol = new int[]
                    {4, 4, 2, 2, 2, 65};
        } else if (66723515 <= charisma && charisma <= 82065487) {
            symbol = new int[]
                    {4, 4, 3, 0, 0, 66};
        } else if (82065488 <= charisma && charisma <= 98419071) {
            symbol = new int[]
                    {4, 4, 3, 3, 0, 67};
        } else if (98419072 <= charisma && charisma <= 115833228) {
            symbol = new int[]
                    {4, 4, 3, 3, 3, 68};
        } else if (115833229 <= charisma && charisma <= 134358468) {
            symbol = new int[]
                    {4, 4, 4, 0, 0, 69};
        } else if (134358469 <= charisma && charisma <= 154046873) {
            symbol = new int[]
                    {4, 4, 4, 1, 0, 70};
        } else if (154046874 <= charisma && charisma <= 174952121) {
            symbol = new int[]
                    {4, 4, 4, 1, 1, 71};
        } else if (174952122 <= charisma && charisma <= 197129510) {
            symbol = new int[]
                    {4, 4, 4, 2, 0, 72};
        } else if (197129511 <= charisma && charisma <= 220635982) {
            symbol = new int[]
                    {4, 4, 4, 2, 2, 73};
        } else if (220635983 <= charisma && charisma <= 245530147) {
            symbol = new int[]
                    {4, 4, 4, 3, 0, 74};
        } else if (245530148 <= charisma && charisma <= 271872307) {
            symbol = new int[]
                    {4, 4, 4, 3, 3, 75};
        } else if (271872308 <= charisma && charisma <= 299724480) {
            symbol = new int[]
                    {4, 4, 4, 4, 0, 76};
        } else if (299724481 <= charisma && charisma <= 329150424) {
            symbol = new int[]
                    {4, 4, 4, 4, 1, 77};
        } else if (329150425 <= charisma && charisma <= 360215661) {
            symbol = new int[]
                    {4, 4, 4, 4, 2, 78};
        } else if (360215662 <= charisma && charisma <= 392987501) {
            symbol = new int[]
                    {4, 4, 4, 4, 3, 79};
        } else if (392987502 <= charisma && charisma <= 427535066) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 80};
        } else if (427535067 <= charisma && charisma <= 463929314) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 81};
        } else if (463929315 <= charisma && charisma <= 502243063) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 82};
        } else if (502243064 <= charisma && charisma <= 542551015) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 83};
        } else if (542551016 <= charisma && charisma <= 584929780) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 84};
        } else if (584929781 <= charisma && charisma <= 629457900) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 85};
        } else if (629457901 <= charisma && charisma <= 676215873) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 86};
        } else if (676215874 <= charisma && charisma <= 725286177) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 87};
        } else if (725286178 <= charisma && charisma <= 776753294) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 88};
        } else if (776753295 <= charisma && charisma <= 830703734) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 89};
        } else if (830703735 <= charisma && charisma <= 887226059) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 90};
        } else if (887226060 <= charisma && charisma <= 946410907) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 91};
        } else if (946410908 <= charisma && charisma <= 1008351016) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 92};
        } else if (1008351017 <= charisma && charisma <= 1073141248) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 93};
        } else if (1073141249 <= charisma && charisma <= 1140878613) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 94};
        } else if (1140878614 <= charisma && charisma <= 1211662293) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 95};
        } else if (1211662294 <= charisma && charisma <= 1285593666) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 96};
        } else if (1285593667 <= charisma && charisma <= 1362776330) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 97};
        } else if (1362776331 <= charisma && charisma <= 1443316127) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 98};
        } else if (1443316128 <= charisma && charisma <= 1527321167) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 99};
        } else if (1527321168 <= charisma && charisma <= 1614901852) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 100};
        } else if (charisma >= 1614901852) {
            symbol = new int[]
                    {4, 4, 4, 4, 4, 101};
        }

        return symbol;
    }

    /**
     * 发送邮件
     *
     * @param emails  邮箱列表
     * @param subject 主题
     * @param text    内容
     * @param context
     * @return
     * @time 2011-7-5 上午10:54:01
     * @author:linyg
     */
    public static boolean sendEmail(String emails, String subject, String text,
                                    Context context) {
        try {
            Uri mailToUri = Uri.parse("mailto:" + emails);
            Intent inte = new Intent(Intent.ACTION_SENDTO, mailToUri);
            inte.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            // inte.putExtra( android.content.Intent.EXTRA_TEXT , text );
            context.startActivity(inte);
            return true;
        } catch (Throwable t) {
            CommonFunction.log(t);
            Toast.makeText(context, R.string.launch_email_client_failed, Toast.LENGTH_SHORT)
                    .show();
        }
        return false;
    }

    public static RelativeLayout.LayoutParams setRelativeLayoutAttribute(View view,
                                                                         float width, float height) {
        int screenPixWidth = getScreenPixWidth(view.getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        if (width != 0) {
            params.width = (int) (screenPixWidth * width);
        }
        if (height != 0) {
            params.height = (int) (screenPixWidth * height);
        }
        view.setLayoutParams(params);
        return params;
    }

    /**
     * 打电话
     *
     * @param context
     * @param phone   电话号码
     * @time 2011-7-11 上午10:01:02
     * @author:linyg
     */
    public static void doPhone(Context context, String phone) {
        // Intent.ACTION_CALL 直接拨打
        Intent myIntentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        context.startActivity(myIntentDial);
    }

    /**
     * 将文本设置到剪切板中
     *
     * @param context
     * @param str
     * @time 2011-6-27 下午02:53:41
     * @author:linyg
     */
    public static boolean setClipboard(Context context, String str) {
        try {
            ClipboardManager clip = (ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);

            // 将包含有自定义表情的文本转换
            String text = FaceManager.catFace2Text(context, str);
            clip.setText(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int isFaceInstalled(String faceId) {
        if (faceId != null) {
            String facePath = getFacePath(faceId);
            File faceFileCache = new File(facePath);
            if (faceFileCache != null && faceFileCache.exists()
                    && faceFileCache.isDirectory() && faceFileCache.list().length > 0) {
                return 1;
            } else {
                if (faceFileCache != null) {
                    faceFileCache.delete();
                }
            }
        }
        return -1;
    }

    public static String getFacePath(String faceId) {
        //Common.getInstance().getUid( )
        return getSDPath() + "/face/" + Common.getInstance().loginUser.getUid() + "/"
                + faceId;
    }

    /**
     * 更改表情文件后缀名，下载后安装后立即更改 目的是为了不让表情图片出现在手机相册里
     *
     * @param path 路径
     */
    @SuppressWarnings("unused")
    public static void reFaceFileName(String path) {
        File f = new File(path);
        File[] fs = f.listFiles();
        if (fs.length > 0) {
            for (int i = 0; i < fs.length; ++i) {
                File f2 = fs[i];
                if (f2.isDirectory()) {
                    reFaceFileName(f2.getPath());
                } else {
                    String from = "";
                    String name = f2.getName();
                    if (name.contains(".")) {
                        String[] subStr = name.split("\\.");
                        if (subStr.length == 2) {
                            for (String string : subStr) {
                                from = "." + subStr[1];
                            }
                        }
                    }
                    if (from.equals(".txt")) {
                        continue;
                    } else if (from.equals(".gif")) {
                        String to = PathUtil.getDynamicFacePostfix();
                        if (name.endsWith(from)) {
                            f2.renameTo(new File(f2.getParent() + "/"
                                    + name.substring(0, name.indexOf(from)) + to));
                        }
                    } else if (from.equals(".png")) {
                        String to = PathUtil.getStaticFacePostfix();
                        if (name.endsWith(from)) {
                            f2.renameTo(new File(f2.getParent() + "/"
                                    + name.substring(0, name.indexOf(from)) + to));
                        }
                    } else {
                        if (!from.equals(".static") && !from.equals(".dynamic")) {
                            String to = PathUtil.getDynamicFacePostfix();
                            if (name.endsWith(from)) {
                                f2.renameTo(new File(f2.getParent() + "/"
                                        + name.substring(0, name.indexOf(from)) + to));
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * 给编辑框设置表情
     *
     * @param context
     * @param editContent 编辑框
     * @param faceTitle   表情的标识
     * @param faceImg     表情的资源ID
     */
    public static void setFace(Context context, EditText editContent, String faceTitle,
                               int faceImg, int max) {
        int start = editContent.getSelectionStart();
        Resources res = context.getResources();
        float faceSize = res.getDimension(R.dimen.face_height);

        int length = StringUtil.getLengthCN1(editContent.getText().toString());
        if (length + StringUtil.FACE_TAG_NUM <= max) {
            String strUnicode = faceStr2Unicode(faceTitle);
            Spannable ss = editContent.getText().insert(start, strUnicode);

            Drawable d = FaceManager.getInstance(context).getDrawableWithFaceKey(context,
                    faceTitle);
            if (d != null) {
                d.setBounds(0, 0, (int) faceSize, (int) faceSize);
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);

                ss.setSpan(span, start, start + strUnicode.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // 保存最近使用的表情
                CommonFunction.saveLastUsedFace(context, faceTitle);
            }
        }
    }

    /**
     * 保存最近使用的表情(最多存放100个表情)
     *
     * @param faceKey 表情的标识
     */
    public static void saveLastUsedFace(Context context, String faceKey) {
        SharedPreferenceUtil pre = SharedPreferenceUtil.getInstance(context);
        String faceStr = pre.getString(SharedPreferenceUtil.LAST_USED_FACE);

        if (faceStr != null && !faceStr.equals("")) {
            String[] faceList = faceStr.split(",");

            for (int i = 0; i < faceList.length; i++) {
                if (faceKey.equals(faceList[i])) { // 以保存过此表情，则删除
                    faceList[i] = "";

                    break;
                }
            }

            String newFaceStr = faceKey;
            int num = 1; // 表情的个数
            for (int j = 0; j < faceList.length; j++) { // 保存新的表情
                if (!faceList[j].equals("")) {
                    newFaceStr += ",";
                    newFaceStr += faceList[j];

                    num++;
                    if (100 == num) { // 最多存放100个表情
                        break;
                    }
                }
            }
            pre.putString(SharedPreferenceUtil.LAST_USED_FACE, newFaceStr);
        } else {
            pre.putString(SharedPreferenceUtil.LAST_USED_FACE, faceKey);
        }
    }

    public static String faceStr2Unicode(String str) {
        String result = "";

        if (str.startsWith(FaceManager.catFlag) || str.contains("back")) {

            result = str;

            return result;
        } else {

            if (str.length() % 4 == 0) {
                int i = 0;
                while (i < str.length() - 1) {

                    String hexstr = str.substring(i, i + 4);
                    int v = Integer.parseInt(hexstr, 16);
                    result += (char) v;
                    i = i + 4;
                }

                return result;
            } else if ((str.length() - 2) % 4 == 0) {
                int i = 0;
                String ascHexStr = str.substring(i, i + 2);
                int v = Integer.parseInt(ascHexStr, 16);
                result += (char) v;
                i = i + 2;
                while (i < str.length() - 1) {

                    String hexstr = str.substring(i, i + 4);
                    v = Integer.parseInt(hexstr, 16);
                    result += (char) v;
                    i = i + 4;
                }

                return result;
            } else {
                return str;
            }
        }
    }

    /**
     * @param bitmap     原图
     * @param edgeLength 希望得到的正方形部分的边长
     * @return (与服务端的截图方式一致)
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0) {
            return null;
        }

        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        int ret = widthOrg > heightOrg ? heightOrg : widthOrg;

        Bitmap cutedBitmap = null;
        if (widthOrg != heightOrg) {
            int left = 0;
            int top = 0;
            if (heightOrg - widthOrg > 150) {
                top = (int) (0.23 * heightOrg);
                if (heightOrg - top < ret)
                    top = 0;
            }
            if (widthOrg - heightOrg > 150) {
                left = (int) (0.23 * widthOrg);
                if (widthOrg - left < ret)
                    left = 0;
            }
            cutedBitmap = Bitmap.createBitmap(bitmap, left, top, ret, ret);
        }

        if (cutedBitmap != null) {
            return Bitmap.createScaledBitmap(cutedBitmap, edgeLength, edgeLength, true);
        } else {
            return Bitmap.createScaledBitmap(bitmap, edgeLength, edgeLength, true);
        }
    }

    public static String thumPicture(String url) {
        if (!TextUtils.isEmpty(url)) {
            int last = url.lastIndexOf("_s");
            if (last > 0) {
                return url;
            }

            if (url.contains(".")) {
                try {
                    String lastStr = url.substring(url.lastIndexOf("."), url.length());
                    String startStr = url.substring(0, url.lastIndexOf("."));
                    return startStr + "_s" + lastStr;
                } catch (Exception e) {

                }
            } else {
                return url + "_s";
            }

        }
        return "";
    }

    /**
     * 旋转图片,并保存
     */
    public static String rotaingImage(String path) {
        String outputPath = PathUtil.getImageRotatePath(path);

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            int degree = PhotoCropActivity.readPictureDegree(path);
            // if (degree == 0) {
            // outputPath = path;
            // } else {
            //
            // }

            File outputFile = new File(outputPath);
            if (!outputFile.exists()) {
                outputFile.createNewFile();

                bitmap = PhotoCropActivity.rotaingImageView(degree, bitmap);

                FileOutputStream os = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            outputPath = path;
        }

        return outputPath;
    }

    /**
     * 计算缩放比例
     *
     * @param srcBitmap srcBitmap 原图
     * @param maxWidth  目标宽度
     * @param maxHeight 目标高度
     */
    public static Bitmap scalePicture(Bitmap srcBitmap, int maxWidth, int maxHeight) {
        try {
            Bitmap bitmap = null;
            int srcWidth = srcBitmap.getWidth();
            int srcHeight = srcBitmap.getHeight();
            // 计算缩放率，新尺寸除原始尺寸
            float scaleWidth = ((float) maxWidth) / srcWidth;
            float scaleHeight = ((float) maxHeight) / srcHeight;

            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            // 缩放图片动作
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建新的图片
            // bitmap = Bitmap.createBitmap( srcBitmap , 0 , 0 , srcWidth ,
            // srcHeight , matrix ,
            // true );
            bitmap = CommonFunction.createBitmap(srcBitmap, 0, 0, srcWidth, srcHeight,
                    matrix, true);
            return bitmap;
        } catch (Throwable t) {
            t.printStackTrace();
            System.gc();
        }
        return srcBitmap;
    }

    /**
     * 计算缩放比例
     *
     * @param filename  filename 图片路径
     * @param maxWidth  目标宽度
     * @param maxHeight 目标高度
     */
    public static Bitmap scalePicture(String filename, int maxWidth, int maxHeight) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, opts);
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;
            int desWidth = 0;
            int desHeight = 0;

            // 缩放比例
            double ratio = 0.0;
            if (srcWidth > srcHeight) {
                ratio = srcWidth / maxWidth;
                desWidth = maxWidth;
                desHeight = (int) (srcHeight / ratio);
            } else {
                ratio = srcHeight / maxHeight;
                desHeight = maxHeight;
                desWidth = (int) (srcWidth / ratio);
            }
            // 设置输出宽度、高度
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inSampleSize = (int) (ratio) + 1;
            newOpts.inJustDecodeBounds = false;
            newOpts.outWidth = desWidth;
            newOpts.outHeight = desHeight;
            bitmap = BitmapFactory.decodeFile(filename, newOpts);

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 获取导航栏的高度-针对虚拟按键
     */
    public static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        return navigationBarHeight;
    }

    /**
     * 获取状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 获取系统的总内存（单位Byte）
     *
     * @return
     */
    public static long getTotalMemory() {
        try {
            FileReader localFileReader = new FileReader("/proc/meminfo");
            BufferedReader localBufferedReader = new BufferedReader(localFileReader);
            String memTotal = localBufferedReader.readLine();
            localFileReader.close();

            String regEx = "\\d{1,}";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(memTotal);
            if (m.find()) {
                return (Long.parseLong(m.group(0)) * 1024);
            }
        } catch (Throwable e) {
            log(e);
        }
        return -1;
    }

    /**
     * 使用c层代码早图片
     */
    public static Bitmap createBitmap(String path) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(path);
        return createBitmap(fis);
    }

    public static Bitmap createBitmap(InputStream is) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = 2;
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap createBitmap(String bmPath, BitmapFactory.Options opt) {
        try {
            opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            FileInputStream fis = new FileInputStream(bmPath);
            return BitmapFactory.decodeStream(fis, null, opt);
        } catch (Throwable t) {
            log(t);
        }
        return null;
    }

    public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height,
                                      Matrix m, boolean filter) {
        try {
            return Bitmap.createBitmap(source, x, y, width, height, m, filter);
        } catch (OutOfMemoryError e) {
            System.gc();
            log(e);
            try {
                log("System.out", "try to creat the bitmap again");
                return Bitmap.createBitmap(source, x, y, width, height, m, filter);
            } catch (Throwable t) {
                log(t);
            }
        } catch (Throwable t) {
            log(t);
        } finally {
            System.gc();
        }
        return null;
    }

    /**
     * 获取指定View的截图
     */
    public static Bitmap captureView(View view) throws Throwable {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmShare = view.getDrawingCache();
        if (bmShare == null) {
            // 使用反射，强制将mViewFlags设置为正确的数值
            Field mViewFlagsField = View.class.getDeclaredField("mViewFlags");
            mViewFlagsField.setAccessible(true);
            mViewFlagsField.set(view, Integer.valueOf(402685952));
            bmShare = view.getDrawingCache();
        }
        return bmShare;
    }

    public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height) {
        try {
            return Bitmap.createBitmap(source, x, y, width, height);
        } catch (OutOfMemoryError e) {
            System.gc();
            log(e);
            try {
                log("System.out", "try to creat the bitmap again");
                return Bitmap.createBitmap(source, x, y, width, height);
            } catch (Throwable t) {
                log(t);
            }
        } catch (Throwable t) {
            log(t);
        } finally {
            System.gc();
        }
        return null;
    }

    /**
     * 取代多余的换行
     *
     * @param str
     * @return
     */
    public static String replaceLineFeed(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\n{2,}");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("\n");
        }
        return dest;
    }

    /**
     * 过滤关键字并将关键字替换为 Emoji表情
     *
     * @param keyword
     * @return 返回的字符已经经过过滤处理，如果返回null或空字符串表示用户被禁言
     * @author huyunfeng E -mail:my519820363@gmail.com
     * @version CreateTime：2013- 3-27 上午11:39:13
     */
    public static String filterKeyWordAndReplaceEmoji(Context context, String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return null;// 空的内容
        }

        // 替换非法字符
        return KeyWord.getInstance(context).filterKeyWordAndReplaceEmoji(context, keyword);
    }

    /**
     * 过滤掉 \r 换行 \n回车
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\r+|\n+");
            Matcher m = p.matcher(str);
            dest = m.replaceAll(" ");// .replaceAll(" +", " ");
        }
        return dest;
    }

    public static boolean isFileExist(String path) {
        File file = new File(path);

        return file.exists();
    }


    /**
     * 获取屏幕分辨率：宽
     *
     * @param context
     * @return
     */
    public static int getScreenPixWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        if (!(context instanceof Activity)) {
            dm = context.getResources().getDisplayMetrics();
            return dm.widthPixels;
        }

        WindowManager wm = ((Activity) context).getWindowManager();
        if (wm == null) {
            dm = context.getResources().getDisplayMetrics();
            return dm.widthPixels;
        }

        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕分辨率：高
     *
     * @param context
     * @return
     */
    public static int getScreenPixHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        if (!(context instanceof Activity)) {
            dm = context.getResources().getDisplayMetrics();
            return dm.heightPixels;
        }

        WindowManager wm = ((Activity) context).getWindowManager();
        if (wm == null) {
            dm = context.getResources().getDisplayMetrics();
            return dm.heightPixels;
        }

        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;

    }

    // 压缩输出文件
    public static String compressImage(String sourcePath) throws Exception {
        File uploadFile = new File(sourcePath);
        String tmpFilePath = "";

        if (uploadFile.exists()) {
            tmpFilePath = PathUtil.getPictureDir() + CryptoUtil.md5(sourcePath);
            File tmpFile = new File(tmpFilePath);
            tmpFile.createNewFile();

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(uploadFile.getAbsolutePath(), opts);
//            opts.inSampleSize = 1;
//            if (opts.outHeight > 750 || opts.outWidth > 750) {
//                if (opts.outHeight < 750) {
//                    opts.inSampleSize = opts.outHeight / 750;
//                    Log.d("hanggao","outHeight==   "+opts.inSampleSize);
//                } else {
//                    opts.inSampleSize = opts.outWidth / 750;
//                    Log.d("hanggao","outWidth==  "+opts.inSampleSize);
//                }
//            }
            opts.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeFile(uploadFile.getAbsolutePath(), opts);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int options = 100;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);

            while ((baos.toByteArray().length / 1024) > 500) {
                CommonFunction.log("hanggao", "压缩  =" + (baos.toByteArray().length / 1024));
                baos.reset();
                options -= 10;
                if (options <= 0) {
                    break;
                }
                bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
            try {
                FileOutputStream fos = new FileOutputStream(tmpFile);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
                CommonFunction.log("hanggao", "AbsolutePath = " + tmpFile.getAbsolutePath() + "压缩  ===" + (getFileSize(tmpFile) / 1024));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            return tmpFilePath;
        }

        return tmpFilePath;
    }

//    // 压缩输出文件
//    public static String compressImage(String sourcePath) throws Exception {
//        File uploadFile = new File(sourcePath);
//        String tmpFilePath = "";
//
//        if (uploadFile.exists()) {
//            tmpFilePath = PathUtil.getPictureDir() + CryptoUtil.md5(sourcePath);
//            File tmpFile = new File(tmpFilePath);
//            tmpFile.createNewFile();
//
//            BitmapFactory.Options opts = new BitmapFactory.Options();
//            opts.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(uploadFile.getAbsolutePath(), opts);
//            int h = opts.outHeight;
//            int w = opts.outWidth;
//            boolean isCompress = false;
//            opts.inSampleSize = 1;
//            if (opts.outHeight > 1280 || opts.outWidth > 720) {
//                isCompress = true;
//                w = opts.outWidth * 1280 / opts.outHeight;
//                h = opts.outHeight * 720 / opts.outWidth;
//                if (h < 1280) {
//                    w = 720;
//                    opts.inSampleSize = opts.outHeight / 1280;
//                } else {
//                    h = 1280;
//                    opts.inSampleSize = opts.outWidth / 720;
//                }
//            }
//
//            opts.inJustDecodeBounds = false;
//            Bitmap bitmap = BitmapFactory.decodeFile(uploadFile.getAbsolutePath(), opts);
//            FileOutputStream fos = new FileOutputStream(tmpFile);
//            BufferedOutputStream bos = new BufferedOutputStream(fos);
//            long pictureSize = FormetFileSize(getFileSize(uploadFile));
//            //判断网络状态
//            ConnectivityManager manager = (ConnectivityManager) BaseApplication.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//            State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
//            State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//
////            if (gprs == State.CONNECTED || gprs == State.CONNECTING) {
//                if (isCompress && (w != opts.outWidth || h != opts.outHeight)) {
//                    Bitmap outBitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
//                    outBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);
//                    outBitmap.recycle();
//                } else {
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);
//                }
////            }
//            //判断为wifi状态下才加载广告，如果是GPRS手机网络则不加载！
////            if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
////                if (pictureSize > 500) {
////                    float picSize = (float) 500 / pictureSize;
////                    int quailty = (int) (picSize * 100);
////                    bitmap.compress(Bitmap.CompressFormat.JPEG, quailty, bos);
////
////                }
////            }
//
//            bitmap.recycle();
//            bos.flush();
//            fos.close();
//            bos.close();
//        } else {
//            return tmpFilePath;
//        }
//
//        return tmpFilePath;
//    }


    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private static long FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        long fileSizeString = 0;
        if (fileS == 0) {
            return fileSizeString;
        }
        fileSizeString = fileS / 1024;
        return fileSizeString;
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 显示软键盘
     *
     * @param context
     * @param view
     */
    public static void showInputMethodForQuery(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * 判断邮箱地址是否有效限
     */
    public static boolean isEmailAddValid(String address) {
        if (address != null && address.length() > 0) {
            char[] cAddress = address.toCharArray();
            for (char c : cAddress) {
                if (c > 127) {
                    return false;
                }
            }

            Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
            Matcher m = p.matcher(address);
            return m.matches();
        }
        return false;
    }

    /**
     * 将map 转为 string
     *
     * @param map
     * @return
     */
    public static String getUrlParamsMap(Map<String, String> map,
                                         boolean isSort) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        List<String> keys = new ArrayList<String>(map.keySet());
        if (isSort) {
            Collections.sort(keys);
        }
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if (map.get(key) != null) {
                String value = map.get(key).toString();
                sb.append(key + "=" + value);
                sb.append("&");
            }
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.lastIndexOf("&"));
        }
        return s;
    }

    /**
     * 将map 转为 string
     *
     * @param map
     * @return
     */
    public static String getUrlParamsByMap(Map<String, Object> map,
                                           boolean isSort) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        List<String> keys = new ArrayList<String>(map.keySet());
        if (isSort) {
            Collections.sort(keys);
        }
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if (map.get(key) != null) {
                String value = map.get(key).toString();
                sb.append(key + "=" + value);
                sb.append("&");
            }
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.lastIndexOf("&"));
        }
        return s;
    }

    /**
     * 返回当前的语言索引：<br>
     * 0:英文 ，1：中文简体，2：中文繁体（其他语言默认为0：英文）
     */
    public static int getLanguageIndex(Context context) {
        String lang = PhoneInfoUtil.getInstance(context).getLanguage() + "_";
        lang += PhoneInfoUtil.getInstance(context).getSettingLang().toLowerCase();
        int langIndex = 0;
        if (lang.indexOf("zh") > -1) {
            if (lang.indexOf("cn") > -1) {
                langIndex = 1;
            } else {
                langIndex = 2;
            }
        }
        return langIndex;
    }

    /**
     * 获取对应属性的文本格式（英|简|繁） 如 ： you | 你 |  妳
     */
    public static String getLangText(Context mContext, String str) {
        if (str == null) {
            return "";
        }
        int Lang = getLanguageIndex(mContext);
        if (str != null && !str.equals("")) {
            if (str.contains("|")) {
                String[] split = str.split("\\|");
                if (split.length == 3) {
                    return split[Lang];
                }
            }
        }
        return str;
    }

    /**
     * 获取对应属性的文本格式（英|简|繁） 如 ： you | 你 |  妳
     */
    public static String getLangText(String str) {
        if (str == null) {
            return "";
        }
        int Lang = getLanguageIndex(BaseApplication.appContext);
        if (str != null && !str.equals("")) {
            if (str.contains("|")) {
                String[] split = str.split("\\|");
                if (split.length == 3) {
                    return split[Lang];
                }
            }
        }
        return str;
    }

    /**
     * 1:英文 ，2：中文简体，3：中文繁体（其他语言默认为1：英文）
     * ( 在某些机型[ 如HTC 9088 ]上判断有误，推荐使用getLanguageIndex方法 )
     *
     * @param context
     * @return
     */
    public static int getLang(Context context) {
        int lang = 1;
        String language = PhoneInfoUtil.getInstance(context).getSettingLang().toLowerCase();
        if (language != null) {
            if (language.contains("cn")) {
                lang = 2;
            } else if (language.contains("tw") || language.contains("hk")) {
                lang = 3;
            }
        }
        return lang;
    }

    /**
     * 显示toast
     *
     * @param context
     * @param text
     * @param duration
     * @time 2011-6-28 上午10:11:22
     * @author:linyg
     */
    public static void showToast(Context context, String text, int duration) {
        Toast.makeText(context, text, duration).show();
    }

    /**
     * @Description: Toast一条消息
     */
    public static void toastMsg(Context context, String sMsg) {
        if (isEmptyOrNullStr(sMsg))
            return;
        context = BaseApplication.appContext;
        Toast tmpToast = Toast.makeText(context, sMsg, Toast.LENGTH_SHORT);
        //log("sherlock", tmpToast.getDuration());

        boolean isShowing = false;
        if (System.currentTimeMillis() - toastTime < 2000)
            isShowing = true;


        if (isShowing) {
            if (!lastString.equals(sMsg)) {
                if (lastToast != null)
                    lastToast.cancel();
                lastToast = tmpToast;
                toastTime = System.currentTimeMillis();
                lastString = sMsg;
                lastToast.show();
            }
        } else {
            if (lastToast != null)
                lastToast.cancel();
            lastToast = tmpToast;
            toastTime = System.currentTimeMillis();
            lastString = sMsg;
            lastToast.show();
        }
    }

    /**
     * @Description: Toast一条消息
     */
    public static void toastMsg(Context context, int rMsg) {
//		Toast.makeText( context , context.getString( rMsg ) , Toast.LENGTH_SHORT ).show();
        toastMsg(context, context.getString(rMsg));
    }

    /**
     * @Description: Toast一条消息 显示时间长一点
     */
    public static void toastMsgLong(Context context, String sMsg) {
        if (isEmptyOrNullStr(sMsg))
            return;
        context = BaseApplication.appContext;
        Toast tmpToast = Toast.makeText(context, sMsg, Toast.LENGTH_LONG);
        //log("sherlock", tmpToast.getDuration());

        boolean isShowing = false;
        if (System.currentTimeMillis() - toastTime < 2000)
            isShowing = true;


        if (isShowing) {
            if (!lastString.equals(sMsg)) {
                if (lastToast != null)
                    lastToast.cancel();
                lastToast = tmpToast;
                toastTime = System.currentTimeMillis();
                lastString = sMsg;
                lastToast.show();
            }
        } else {
            if (lastToast != null)
                lastToast.cancel();
            lastToast = tmpToast;
            toastTime = System.currentTimeMillis();
            lastString = sMsg;
            lastToast.show();
        }
    }

    /**
     * 用于服务器下发的字段是null时，JSONObject的get和opt方法无法识别null
     *
     * @param json
     * @param key
     * @return
     */
    public static String jsonOptString(JSONObject json, String key) {

        if (json == null || json.isNull(key)) {
            return "";
        }
        try {
            return json.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 用于服务器下发的字段是null时，JSONObject的get和opt方法无法识别null
     *
     * @param json
     * @param key
     * @return
     */
    public static int jsonOptInt(JSONObject json, String key) {

        if (json == null || json.isNull(key)) {
            return -1;
        }
        try {
            return json.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void log(Throwable t) {
        if (Config.DEBUG && t != null) {
            t.printStackTrace();
        }
    }

    /**
     * 打印log
     */
    public static void log(String tag, Object... msg) {
        if (Config.DEBUG && msg != null) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (Object o : msg) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(o == null ? "null" : o.toString());
                i++;
            }

            int logStrLength = sb.length();
            int maxLogSize = 1000;
            for (i = 0; i <= logStrLength / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i + 1) * maxLogSize;
                end = end > logStrLength ? logStrLength : end;
                if (tag.equals("sherlock")) {
                    Log.i(tag, sb.substring(start, end));
                } else {
                    Log.v("iAround_" + tag, sb.substring(start, end));
                }
            }
        }
    }

    public static void log(Object... msg) {
        log("System.out", msg);
    }

    /**
     * 当域名无法解析时，则使用下发替换的方案。 由服务端下发可被路由的域名，然后由客户端做替换（目前使用在图片访问、视频音频播放）
     *
     * @param originalUrl
     * @return String
     */
    public static String replaceUrl(String originalUrl) {
        if (!isEmptyOrNullStr(Config.sPictureUrlBak) && !isEmptyOrNullStr(originalUrl)) {
            String tmpUrl = originalUrl.substring(0, originalUrl.indexOf("/", 8));// 排除"http://"
            if (tmpUrl.contains("iaround")) { // 避免浏览其它url的域名时，被强制替换
                int index = tmpUrl.length() - tmpUrl.lastIndexOf(".") + "iaround".length();
                String strArr = tmpUrl.substring(tmpUrl.length() - index, tmpUrl.length());
                originalUrl = originalUrl.replace(strArr, Config.sPictureUrlBak);
            }
        }
        return originalUrl;
    }

    /**
     * 判断字符串是否为空字符串、null或“null”字符串包括所有大小写情况
     *
     * @param str
     * @return 是否为空
     */
    public static boolean isEmptyOrNullStr(String str) {
        return TextUtils.isEmpty(str) || "".equals(str) || "null".equals(str);
    }

    public static String StackTraceLog2String(StackTraceElement[] items) {
        String result = "";
        for (StackTraceElement item : items) {
            result += item.toString() + "/n";
        }
        return result;
    }

    /**
     * 获取包id
     *
     * @param context
     * @return
     */
    public static String getPackageMetaData(Context context) {

        String filename = "cw_packet.txt";
        String filedata = "";

        if (!isEmptyOrNullStr(packageID)) {
            return packageID;
        }
        try {


//            packageID = FileService.readAssetsFile(context, filename);
            packageID = BuildConfig.packageID;

            //			packageID = String.valueOf( getMetaData( context , "UMENG_CHANNEL" ) );
            if (!isEmptyOrNullStr(packageID)) {
                CommonFunction.log("iaround", "Channel_ID:" + packageID);
                return packageID.trim();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "11203";
    }


    /**
     * 5.1版本用于测试网络连接失败情况 写文件，tag为 iAround_new_work_error
     *
     * @param msg
     */
    public static void NetWorkErrorLog(Object... msg) {
        //不再使用这个函数来记录网络失败
        if (Config.DEBUG && msg != null) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (Object o : msg) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(o == null ? "null" : o.toString());
                i++;
            }

            int logStrLength = sb.length();
            int maxLogSize = 1000;
            for (i = 0; i <= logStrLength / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i + 1) * maxLogSize;
                end = end > logStrLength ? logStrLength : end;
                Log.v("iAround_new_work_error", sb.substring(start, end));
            }

            writeLog(msg.toString());
        }
    }

    /**
     * 写日志
     *
     * @param log
     * @time 2011-5-31 下午05:45:01
     * @author:linyg
     */
    public static void writeLog(String log) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss");
        String dateFormat = sdf.format(new Date());
        try {
            if (fw == null)
                fw = new FileWriter(getSDPath() + "/test.txt", true);
            fw.write(dateFormat + ":" + log + "\r\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                fw = null;
            }
        }
    }

    /**
     * 获取sd卡路径
     *
     * @return
     */
    public static String getSDPath() {
        String sdDir = "";
        try {
            boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
            if (sdCardExist) {
                sdDir = Environment.getExternalStorageDirectory() + "/iaround/";// 获取跟目录
            } else {
                sdDir = "/data/data/net.iaround/";
                // sdDir = Environment.getDataDirectory() + "/iAround/";
            }
            File file = new File(sdDir);
            if (!file.exists()) {
                file.mkdirs();
                if (!file.exists()) {
                    sdDir = BaseApplication.appContext.getFilesDir().getPath();
                    File tryfile = new File(sdDir);
                    if (!tryfile.exists()) {
                        tryfile.mkdirs();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdDir;
    }

    /**
     * 判断密码是否有效
     * <p/>
     * 0 -- 非法；1 -- 正确； 2 -- 不一致
     */
    public static int isPasswordValid(String password, String repeated) {
        if (password != null) {
            int len = password.length();
            if (len >= 6 && len <= 16) {
                char[] cPsw = password.toCharArray();
                boolean wrongChar = false;
                for (char c : cPsw) {
                    if (c >= 128) { // 找到非ascii码
                        wrongChar = true;
                        break;
                    }
                }
                if (!wrongChar) {
                    return password.equals(repeated) ? 1 : 2;
                }
            }
        }
        return 0;
    }

    /**
     * 密码包含英文和数字
     *
     * @param password
     * @return
     */
    public static boolean isPassword(String password) {
        Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,12}$");
        Matcher m = p.matcher(password);
        return m.matches();
    }

    /**
     * 只包含英文，数字
     *
     * @return
     */
    public static boolean isNewPassword(String password) {
        Pattern p = Pattern.compile("/^[a-z0-9][a-z0-9]{5,11}$/i");
        Matcher m = p.matcher(password);
        return m.matches();
    }

    /**
     * 通用性手机号是否正确
     *
     * @param phoneNumber 手机号
     * @return boolean 是否正确
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
        String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        Pattern pattern2 = Pattern.compile(expression2);
        Matcher matcher2 = pattern2.matcher(inputStr);
        if (matcher.matches() || matcher2.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后八位任意数
     *
     * @param str 手机号
     * @return
     * @throws PatternSyntaxException
     */
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-9])|(14[5-9])|(166)|(198))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param view
     */
    public static void hideInputMethod(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * dip转px
     */
    public static int dipToPx(Context context, int dip) {
        if (density <= 0) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dip * density + 0.5f);
    }

    /**
     * dip转px
     */
    public static int dipToPx(Context context, float dip) {
        if (density <= 0) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dip * density);
    }

    /**
     * px转dip
     */
    public static int pxToDip(Context context, int px) {
        if (density <= 0) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return (int) ((px - 0.5f) / density);
    }

    /**
     * 改变空间的背景颜色
     *
     * @param view  要改变颜色的控件
     * @param color 控件新的背景颜色
     */
    public static void changeColor(View view, int color) {
        GradientDrawable myGrad = (GradientDrawable) view.getBackground();
        myGrad.setColor(color);
    }

    /**
     * 解决ScrollView中嵌套ListView，listView显示不全的问题
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);  //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /**
     * 动态设置ListView的高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildrenOne(ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition 
            // return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 返回根据指定字段与指定集合查询到的数据
     *
     * @param field
     * @param values
     * @return
     */
    public static String getInSelectCode(String field, List<String> values) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(field + " in (");
        if (values != null && values.size() > 0) {
            stringBuilder.append("'").append(values.get(0)).append("'");
            for (int i = 1; i < values.size(); i++) {
                stringBuilder.append(",").append("'").append(values.get(i)).append("'");
            }
        }
        stringBuilder.append(")");
        CommonFunction.log("xiaohua", "sql = " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 返回根据指定字段与指定集合，在指定范围内查询到的数据
     *
     * @param field
     * @param values
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static String getInAndRoundSelectCode(String field, List<String> values, int startIndex, int endIndex) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(field + ">='" + String.format("%04d", startIndex) + "' and " + field + "<='" + String.format("%04d", endIndex) + "' and ");
        stringBuilder.append(field + " in (");
        if (values != null && values.size() > 0) {
            stringBuilder.append("'").append(values.get(0)).append("'");
            for (int i = 1; i < values.size(); i++) {
                stringBuilder.append(",").append("'").append(values.get(i)).append("'");
            }
        }
        stringBuilder.append(")");
        CommonFunction.log("xiaohua", "sql = " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 返回根据指定字段，在指定范围内查询到的数据
     *
     * @param field
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static String getRoundSelectCode(String field, int startIndex, int endIndex) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(field + ">='" + String.format("%04d", startIndex) + "' and " + field + "<='" + String.format("%04d", endIndex) + "'");
        CommonFunction.log("xiaohua", "sql = " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 跳到某渠道评分
     *
     * @param mContext
     */
    public static void takeCredit(Context mContext) {
        /*String packageName = SharedPreferenceUtil.getInstance(mContext).getString(
                SharedPreferenceUtil.COMMENT_PACKAGENAME);
        CommonFunction.log("shifengxiong", "apply setting " + packageName);
        if (isClientInstalled(mContext, packageName)) {
            // CommonFunction.openApp( this , packageName );
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage(packageName);
            intent.setData(Uri.parse("market://details?id=" + mContext.getPackageName()));
            mContext.startActivity(intent);
        } else {
            Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }*/
        Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * 打开Google Play评分
     *
     * @param activity
     */
    public static void showMarket(Activity activity) {
        final String appPackageName = activity.getPackageName();
        try {
            Intent launchIntent = activity.getPackageManager().getLaunchIntentForPackage("com.android.vending");
            // package name and activity
            ComponentName comp = new ComponentName("com.android.vending", "com.google.android.finsky.activities.LaunchUrlHandlerActivity");
            launchIntent.setComponent(comp);
            launchIntent.setData(Uri.parse("market://details?id=" + appPackageName));

            activity.startActivity(launchIntent);

        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    /**
     * 判断本地是否安装了该包
     *
     * @param context
     * @param apkid
     * @param @return
     * @return boolean
     */
    public static boolean isClientInstalled(Context context, String apkid) {
        if (!TextUtils.isEmpty(apkid)) {
            String lowAppLabel = apkid.trim().toLowerCase();
            if (lowAppLabel.length() > 0) {
                PackageManager pm = context.getPackageManager();
                List<PackageInfo> packs = pm.getInstalledPackages(0);
                for (PackageInfo pack : packs) {
                    String appLabel = pack.applicationInfo.packageName.toLowerCase();
                    if (appLabel.equals(lowAppLabel)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int isClientInstalled(Context context, String apkid, int versionCode) {
        if (apkid != null) {
            String lowAppLabel = apkid.trim().toLowerCase();
            if (lowAppLabel.length() > 0) {
                PackageManager pm = context.getPackageManager();
                List<PackageInfo> packs = pm.getInstalledPackages(0);
                for (PackageInfo pack : packs) {
                    String appLabel = pack.applicationInfo.packageName.toLowerCase();
                    if (appLabel.equalsIgnoreCase(lowAppLabel)) {
                        return pack.versionCode < versionCode ? 0 : 1;
                    }
                }
            }
        }
        return -1;
    }

    public static String getThumPicUrl(String picUrl) {
        if (TextUtils.isEmpty(picUrl)) {
            return "";
        }
        if (picUrl.contains("_s.")) {
            return picUrl;
        } else {
            String url = getQQProcessPicUrl(picUrl);
            return url;
        }
//        return picUrl.substring(0, picUrl.lastIndexOf(".")) + "_s" + picUrl.substring(picUrl.lastIndexOf("."), picUrl.length());
//        return null;
    }

    public static String getQQProcessPicUrl(String picUrl) {
        if (TextUtils.isEmpty(picUrl)) {
            return "";
        }
        if (!picUrl.contains("_s.")) {
            return picUrl;
        }
        return null;
    }

    public static int getLoginType(Context context) {
        return SharedPreferenceUtil.getInstance(context).getInt(SharedPreferenceUtil.LOGIN_TYPE);
    }

    public static boolean sendEmail(String emails, String subject, Context context) {
        try {
            Uri mailToUri = Uri.parse("mailto:" + emails);
            Intent inte = new Intent(Intent.ACTION_SENDTO, mailToUri);
            inte.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            // inte.putExtra( android.content.Intent.EXTRA_TEXT , text );
            context.startActivity(inte);
            return true;
        } catch (Throwable t) {
            CommonFunction.log(t);
            Toast.makeText(context, R.string.launch_email_client_failed, Toast.LENGTH_SHORT)
                    .show();
        }
        return false;
    }

    public static String getCachedPath() {
        String sdDir = "";
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory() + "/iaround";// 获取跟目录

        }

        File file = new File(sdDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return sdDir;
    }

    public static String getUSERID(Context context) {
        return SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.USER_ID);
    }

    /**
     * 执行来信声音提示
     */
    public static void receiveMsgVoice(Context context) {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    return;
                }
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(context, R.raw.notify);
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        } catch (Throwable e) {
            log(e);
        }
    }

    /**
     * 执行来信震动提示
     */
    public static void receiveMsgVibration(Context context) {
        if (vibrator == null) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        long[] pattern =
                {50, 150, 50, 150, 50, 150, 50}; // off-on-off-on...
        vibrator.vibrate(pattern, -1);
    }

    /**
     * 释放来信提示的资源
     */
    public static void release() {
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Throwable t) {
                log(t);
            }
            mediaPlayer = null;
        }
    }

    /**
     * 从制定的Assets文件中读取数据
     *
     * @param context
     * @param file
     * @return
     */
    public static String getGsonFromFile(Context context, String file) {
        try {
            InputStreamReader isr = new InputStreamReader(context.getAssets().open(file), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 滑动到顶部
     *
     * @param listView
     */
    public static void scrollToListviewTop(final AbsListView listView) {
        listView.smoothScrollToPosition(0);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listView.getFirstVisiblePosition() > 0) {
                    listView.setSelection(0);
                    handler.postDelayed(this, 100);
                }
            }
        }, 300);
    }

    public static String getStaticMapUrl(double lat, double lon) {
        return "http://restapi.amap.com/v3/staticmap?location=" + lon + "," + lat + "&zoom=15&size=750*300&markers=mid,,A:" + lon + "," + lat + "&&key=" + Constants.AMAP_APIKEY;
    }

    public static String getAuthenToken(Context context) {
        return SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.ACCESS_TOKEN);
    }

    public static String commpressLocalImg(String path) {
        long MAX_SIZE = 2 * 1024 * 1024;//最大上限为2M
        File img = new File(path);
        if (img.length() > MAX_SIZE) {
            String file = CommonFunction.getCachedPath() + path.substring(path.lastIndexOf("/"), path.length());
            Bitmap bmp = getSmallBitmap(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int options = 80;//个人喜欢从80开始,  
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
            while (baos.toByteArray().length > MAX_SIZE) {
                baos.reset();
                options -= 10;
                bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
                return file;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    /**
     * 根据路径获得图片并压缩返回bitmap用于显示
     *
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 保存直播数据
     *
     * @param token
     * @param live
     */
    public static void saveDataLive(String token, String live) {
//        if (live == null | TextUtils.isEmpty(live))
//            return;
//        MyAppConfig.INSTACE.setToken(token);
//        try {
//            JSONObject jsonObj = new JSONObject(live);
//            String dataInfo = jsonObj.optString("dataInfo");
//            Gson gson = new Gson();
//            User user = gson.fromJson(dataInfo, User.class);
//            user.setLoginKey(jsonObj.getString("loginKey"));
//            Common.getInstance().setLoginKey(jsonObj.getString("loginKey"));
//            MyApplication.saveUserLoginInfo(user, token, "email", jsonObj.optString("dataInfo"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public static final String full2HalfChange(String QJstr)
            throws UnsupportedEncodingException {
        if (TextUtils.isEmpty(QJstr)) {
            return QJstr;
        }

        StringBuffer outStrBuf = new StringBuffer("");
        String Tstr = "";
        byte[] b = null;
        for (int i = 0; i < QJstr.length(); i++) {
            Tstr = QJstr.substring(i, i + 1);
            // 全角空格转换成半角空格
            if (Tstr.equals(" ")) {
                outStrBuf.append("");
                continue;
            }
            b = Tstr.getBytes("unicode");
            // 得到 unicode 字节数据
            if (b[2] == -1) {
                b[3] = (byte) (b[3] + 32);
                b[2] = 0;
                outStrBuf.append(new String(b, "unicode"));
            } else {
                outStrBuf.append(Tstr);
            }
        } // end for.
        return outStrBuf.toString().trim();
    }

    /**
     * 判断是否在应用程序中
     *
     * @param context
     * @return
     */
    public static boolean isTopActivity(Context context) {
        String packageName = context.getPackageName();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            ComponentName topActivity = tasksInfo.get(0).topActivity;
            if (packageName.equals(topActivity.getPackageName())) {
                return true;
            }
            if (topActivity.getClassName().contains("net.iaround")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将本应用的栈堆放至前台
     */
    public static void moveTaskToFront(Activity activity, Context context) {
        ActivityManager am;
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.moveTaskToFront(activity.getTaskId(), ActivityManager.MOVE_TASK_NO_USER_ACTION);
    }

    /*
     * 应用切换到后台之后返回时候显示启动广告
     */
    public static void showScreanAd(Context mContext) {
//        if(CloseAllActivity.appContext!=null)
//        {
//            if(CloseAllActivity.appContext == mContext )
//            {
//                //切换到后台多少时间了
//                long backGroundTime = System.currentTimeMillis() - CloseAllActivity.getPauseTime();
//
//                log("shifengxiong","从后台返回  come back" +backGroundTime );
//
//
//                if ( !ConnectSession.getInstance(mContext).checkSessionHeart( ) )
//                {
//                    //心跳已经停止
//                    log("shifengxiong","从后台返回  心跳已经停止"  );
//                    if(Common.getInstance( ).isUserLogin)
//                    {
//                        //心跳已经停止，需要重新登录
//                        ConnectSession.getInstance( mContext ).loginSession( mContext , true );
//                        ConnectGroup.getInstance(mContext).loginGroup( mContext, true );
//                    }
//                    else
//                    {
//                        log("shifengxiong","从后台返回  心跳已经停止 用户登录是还没有登录"  );
//                    }
//                }
//                //显示启动广告
//                if(CloseAllActivity.isShowedBgAd ||!Common.getInstance( ).isUserLogin)
//                {
//                    //启动过一次或者未登录不显示广告
//                    return ;
//                }
//                //应用推荐是否可用的开关同时用于是否显示广告
//                SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( mContext );
//                int iRecommended = sp.getInt(SharedPreferenceUtil.RECOMMENDED_AVAILABLE, 0);
//                if(iRecommended ==1)
//                {
//                    if ( sp.contains( SharedPreferenceUtil.START_PAGE_AD )
//                            && !TextUtils.isEmpty( sp.getString( SharedPreferenceUtil.START_PAGE_AD_URL ) ) )
//                    {
//                        String result = sp.getString( SharedPreferenceUtil.START_PAGE_AD );
//                        LoadEntity bean = GsonUtil.getInstance( ).getServerBean( result,
//                                LoadEntity.class );
//
//                        if ( PathUtil.isExistMD5UserCacheFile( bean.url ) )
//                        {
//                            new FullScreamDialog(mContext, bean).show();
//                            CloseAllActivity.isShowedBgAd = true;
//                        }
//
//                    }
//                }
//
//            }
//            else
//            {
//                CloseAllActivity.appContext = mContext ;
//            }
//        }
//        else
//        {
//            log("shifengxiong","=======================CloseAllActivity.appContext ================null");
//            CloseAllActivity.appContext = mContext ;
//        }
    }

    /**
     * 判断当前应用程序处于前台还是后台
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        return !TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName());
    }

    /**
     * 修改媒体音量（increment > 0为增加，increment < 0为减少，每次只会增加或减少1）
     */
    public static void changeMediaVolume(Context context, int increment) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int curVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        if (increment > 0) {
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
        } else if (increment < 0) {
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
        }
        CommonFunction.log("System.out", "curVol = " + curVol);
    }

    /**
     * 转储logcat
     */
    public static void dumpLogcat() {
        if (Config.DEBUG) {
            try {
                File uncaughtExceptionLogFolder = new File(getSDPath() + "UncaughtExceptions/");
                if (!uncaughtExceptionLogFolder.exists()) {
                    uncaughtExceptionLogFolder.mkdirs();
                }
                String fileName = TimeFormat.convertTimeLong2String(System.currentTimeMillis(), Calendar.SECOND) + ".txt";
                String dumpFile = uncaughtExceptionLogFolder.getAbsolutePath() + "/" + fileName;
                Process pDump = Runtime.getRuntime().exec("logcat -v time -d -f " + dumpFile);
                pDump.waitFor();
                CommonFunction.log("System.err", "Log file has been dump to \"" + dumpFile + "\"");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 用户是否被禁言
     *
     * @param context
     * @return true被禁言 ，false不被禁言
     * @time 2011-10-10 下午03:52:03
     * @author:linyg
     */
    public static boolean forbidSay(Context context) {
        // 用户被禁言
        long time = StartModel.getInstance().getForbidTime();
        if (time != 0) {
            if (time < 0) { // 永久禁言
//				if(forbitToast != null)
//					forbitToast.cancel();
//				forbitToast = Toast
//					.makeText( context, context.getString( R.string.admin_forever_forbid_say ),
//						Toast.LENGTH_SHORT );
//				forbitToast.show();

//				showToast( context , context.getString( R.string.admin_forever_forbid_say ) ,
//						0 );

                toastMsg(context, context.getString(R.string.admin_forever_forbid_say));
                return true;
            } else if (time - (Common.getInstance().serverToClientTime + System.currentTimeMillis()) > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy-MM-dd HH:mm");
                String format_time = sdf.format(time + Common.getInstance().serverToClientTime);

//				if(forbitToast != null)
//					forbitToast.cancel();
//				forbitToast = Toast.makeText( context,
//					String.format( context.getString( R.string.admin_forbid_say ), format_time ),
//					Toast.LENGTH_SHORT );
//				forbitToast.show( );

//				showToast( context , String.format( context.getString( R.string.admin_forbid_say ),
//					format_time ) , 0 );

                toastMsg(context, String.format(context.getString(R.string.admin_forbid_say), format_time));
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String> subFaceString(EditText mEditText, String content,
                                                  int length) {
        ArrayList<String> result = new ArrayList<String>();
        String tmp = content;
        for (; tmp.length() > length; ) {
            int tmpSize = tmp.length() > length + 5 ? length + 5 : tmp.length();
            String newTmp = tmp.substring(0, tmpSize);
            mEditText.setText(newTmp);
            mEditText.setSelection(newTmp.length());
            for (; mEditText.getText().length() > length; ) {
                KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL);
                mEditText.onKeyDown(KeyEvent.KEYCODE_DEL, keyEventDown);
            }
            newTmp = mEditText.getText().toString();
            result.add(newTmp);
            mEditText.getText().clear();
            tmp = tmp.substring(newTmp.length());
        }
        result.add(tmp);
        return result;
    }

    public static void myWriteLog(String tag, String log) {
        if (!Config.DEBUG) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss");
        String dateFormat = sdf.format(new Date());
        try {
            if (fw == null)
                fw = new FileWriter(getSDPath() + "/groupChat.txt", true);
            fw.write(dateFormat + "--" + tag + ":" + log + "\r\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                fw = null;
            }
        }
    }

    /**
     * 把传进来的EditText的光标移动到最后
     *
     * @param editView
     */
    public static void MoveCursorToLast(EditText editView) {
        Spannable contentSpan = editView.getEditableText();
        String editText = editView.getText().toString();
        Selection.setSelection(contentSpan, editText.length());
    }

    /**
     * 回收不使用的bitmap
     */
    public static void recyledBitmap(Bitmap bitmap) {
        try {
            if (bitmap != null) {
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
            System.gc();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 个性化显示地址
     **/
    public static String showAddress(String strAddress) {
        if (strAddress == null) {
            return "";
        }
        if (strAddress.startsWith(",")) {
            return strAddress.replaceAll(",", "");
        }

        if (!",,,".equals(strAddress)) {
            char[] strArray = strAddress.toCharArray();
            if (strArray.length > 0 && strArray[0] < 256) {
                return strAddress;
            }
        }
        return strAddress.replaceAll(",", "");
    }

    /**
     * @param context
     * @param keyword
     * @return
     * @Title: getSensitiveKeyword
     * @Description: 获取字符串中的敏感字符
     */
    public static String getSensitiveKeyword(Context context, String keyword) {
        String k = KeyWord.getInstance(context).filterKeyWord(context, keyword);
        return k;
    }

    public static StringUtil.StringFromTo getHotGroupTopic(String source) {

        StringUtil.StringFromTo subFromString = new StringUtil.StringFromTo(0, 0);
        Pattern p = Pattern.compile("\\#\\w+\\#", Pattern.UNICODE_CASE);// 匹配#
        // #

        // 开始编译
        final Matcher m = p.matcher(source);
        int i = 0;
        int start = 0;
        int end = 0;
        while (m.find()) {
            String icon = m.group();

            start = source.indexOf(icon, start);
            end = start + icon.length();
            if (start > 0 && source.length() > end) {
                // 过滤表情
                if (source.charAt(start - 1) == '[' && source.charAt(end) == ']') {
                } else {
                    subFromString.start = start;
                    subFromString.end = end;
                    subFromString.subStr = icon;
                    break;
                }
            } else {
                subFromString.start = start;
                subFromString.end = end;
                subFromString.subStr = icon;
                break;
            }

            i++;
            m.groupCount();
        }
        return subFromString;
    }

    /**
     * 检查更新
     *
     * @param activity
     */
    public static void showUpdateDialog(final Context activity) {
        if (Common.getInstance().versionFlag == 0) {
            DialogUtil.showOKDialog(activity, R.string.setting_check_updata,
                    R.string.this_is_the_latest_version, null);
        } else {
            String msg = null;
            if (!TextUtils.isEmpty(Common.getInstance().versionContent)) {
                msg = Common.getInstance().versionContent;
            } else {
                msg = activity.getString(R.string.version_description);
            }

            View.OnClickListener onclickListener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 是否强制升级
                    boolean isFocusUpdate = Common.getInstance().versionFlag == 2;
                    if (!PathUtil.isSDcardExist())// sd卡不存在
                    {
                        String noSDCardMsg = activity
                                .getString(R.string.download_new_version_no_sdcard);
                        CommonFunction.showToast(activity, noSDCardMsg, 0);
                        return;
                    }

                    if (!DownloadNewVersionTask.getInstance(activity, isFocusUpdate)
                            .isDownloading()) {
                        // 当前没有正在下载
                        DownloadNewVersionTask.getInstance(activity, isFocusUpdate).reset();
                        DownloadNewVersionTask.getInstance(activity, isFocusUpdate)
                                .execute(Common.getInstance().currentSoftUrl);
                    }
                }
            };

            CustomDialog updateDialog = null;
            // 强制升级
            if (Common.getInstance().versionFlag == 2) {
                updateDialog = new CustomDialog(activity,
                        activity.getString(R.string.version_update), msg,
                        activity.getString(R.string.immediate_update), onclickListener);
                updateDialog.setCancelable(false);
            } else {
                updateDialog = new CustomDialog(activity,
                        activity.getString(R.string.version_update), msg,
                        activity.getString(R.string.wait_update), null,
                        activity.getString(R.string.immediate_update), onclickListener);
            }

            try {
                updateDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 用户列表显示“weibos”字段的图标
     *
     * @author tanzy
     */
    public static void showWeibosIcon(ImageView[] weiboIcons,
                                      ArrayList<WeiboState> weiboes, int jobId, Context context) {
        /*
         * weiboIcons数组，0：圈主，1：职业，2：微博，3：游戏，4：真心话，5：辣椒
         * 现将所有imageview设置为GONE，遍历weiboes时显示对应位置的view
         */

//        int showWeiboType = 0;// 要显示的微博类型ID
//        int verifiedType = -1;// 当是新浪微博时需判断认证类型
//
//        if ( weiboIcons == null || weiboIcons.length == 0 )
//            return;
//        for ( int i = 0 ; i < weiboIcons.length ; i++ )
//        {
//            weiboIcons[ i ].setVisibility( View.GONE );
//        }
//
//        for ( int i = 0 ; i < weiboes.size( ) ; i++ )
//        {
//            WeiboState ws = weiboes.get( i );
//            switch ( ws.getType( ) )
//            {
//                case 12 :// 新浪微博
//                case 25 :// QQ空间
//                case 1 :// 腾讯微博
//                case 24 :// facebook
//                case 23 :// twitter
//                {// 根据权重判断应显示那个图标
//                    if ( showWeiboType == 0 )
//                        showWeiboType = ws.getType( );
//                    else
//                    {
//                        if ( WeiboesMaps.weiboWeight.get( ws.getType( ) ) > WeiboesMaps.weiboWeight
//                                .get( showWeiboType ) )
//                            showWeiboType = ws.getType( );
//                        if ( showWeiboType == 12 )
//                            verifiedType = ws.getVerifiedType( );
//                        else
//                            verifiedType = -1;
//                    }
//                }
//                break;
//                case 981 :// 圈主
//                    //6.0去掉圈子标识
//                    //weiboIcons[ 0 ].setVisibility( View.VISIBLE );
//                    break;
//                case 982 :// 辣椒
//                    //weiboIcons[ 5 ].setVisibility( View.VISIBLE );
//                    break;
//                case 992 :// 真心话
//                    //weiboIcons[ 4 ].setVisibility( View.VISIBLE );
//                    break;
//                case 991 :// 游戏
//                    //weiboIcons[ 3 ].setVisibility( View.VISIBLE );
//                    break;
//                default :
//                    break;
//            }
//        }
//
//        // 职业
//        int jobImageID = 0;
//        int langIndex = getLanguageIndex( context );// 判断语言
//        if ( langIndex == 1 )
//            jobImageID = WeiboesMaps.jobToImage_simple.get( jobId ) == null ? 0
//                    : WeiboesMaps.jobToImage_simple.get( jobId );
//        else if ( langIndex == 2 )
//            jobImageID = WeiboesMaps.jobToImage_tradition.get( jobId ) == null ? 0
//                    : WeiboesMaps.jobToImage_tradition.get( jobId );
//        else
//            jobImageID = WeiboesMaps.jobToImage_english.get( jobId ) == null ? 0
//                    : WeiboesMaps.jobToImage_tradition.get( jobId );
//
//        if ( jobImageID != 0 )
//        {
//            weiboIcons[ 1 ].setVisibility( View.VISIBLE );
//            weiboIcons[ 1 ].setImageResource( jobImageID );
//        }
//        else
//        {
//            weiboIcons[ 1 ].setVisibility( View.GONE );
//        }//gh


    }

    /**
     * 停止logcat重定向
     */
    public static void stopLogcatRedirect() {
        if (Config.DEBUG) {
            try {
                if (logcatProcess != null) {
                    logcatProcess.destroy();
                    logcatProcess = null;
                    CommonFunction.log("System.out", "Logcat process stopped");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行来信声音提示
     */
    public static void notifyMsgVoice(Context context, int msgType) {
        boolean voiceEnable = false;

        String voiceKey = SharedPreferenceUtil.VOICE_ENABLE
                + Common.getInstance().loginUser.getUid();
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);

        if (sp.has(voiceKey)) {
            voiceEnable = sp.getBoolean(voiceKey);
        } else {
            sp.putBoolean(voiceKey, true);
            voiceEnable = true; // 默认是全局打开声音
        }

        // 声音
        if (voiceEnable) {
            String voiceSettings = sp.getString(SharedPreferenceUtil.VOICE_SETTINGS
                    + Common.getInstance().loginUser.getUid());
            if (voiceSettings == null || voiceSettings.length() <= 0) {
                voiceSettings = "false%true%true%true"/* "false%true%true%true%true" */;
            }
            String[] pSettings = voiceSettings.split("%");
            voiceEnable = msgType >= 0 && msgType < pSettings.length
                    && Boolean.parseBoolean(pSettings[msgType]);
            if (voiceEnable) {
                try {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            return;
                        }
                        mediaPlayer.release();
                    }
                    mediaPlayer = MediaPlayer.create(context, R.raw.notify);
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                } catch (Throwable e) {
                    CommonFunction.log(e);
                }
            }
        }
    }

    public static boolean isShakeState(Context context) {
        // 震动
        String vibrateKey = SharedPreferenceUtil.VIBRATE;
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        if (sp.has(vibrateKey)) {
            return sp.getBoolean(vibrateKey);
        } else {
            sp.putBoolean(vibrateKey, false);
        }
        return false;

    }

    /**
     * 执行来信震动提示
     */
    public static void notifyMsgShake(Context context) {
        //gh 是否在麦上
        boolean isMic = SharedPreferenceUtil.getInstance(context).getBoolean(Common.getInstance().loginUser.getUid() + "/" + SharedPreferenceUtil.SYSTEM_MIC);
        if (isMic) return;
        if (vibrator == null) {
            vibrator = (Vibrator) context.getSystemService(SuperActivity.VIBRATOR_SERVICE);
        }
        long[] pattern =
                {50, 150, 50, 150, 50, 150, 50}; // off-on-off-on...
        vibrator.vibrate(pattern, -1);
    }

    /**
     * 关闭后台服务
     */
    public static void stopBackService(Context context) {
        uiRunning = true;
        Intent i = new Intent(BackService.STOP_SERVICE_BROADCAST_KEY);
        context.sendBroadcast(i);
    }

    /**
     * 重定向logcat到指定文件中
     */
    public static void redirectLogcat() {
        //TODO 只能在调试模式下开启 logcat 到文件
        if (Config.DEBUG) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        String logFileDirPath = getSDPath();
                        File logFileDir = new File(logFileDirPath);
                        if (logFileDir.exists() == false) {
                            logFileDir.mkdir();
                        }
                        String logFile = getSDPath() + "/logcat.txt";
                        File log = new File(logFile);
                        if (log.exists() && log.length() > 5000000) {
                            log.delete();
                        }

                        Runtime.getRuntime().exec("logcat -c");

                        if (logcatProcess != null) {
                            logcatProcess.destroy();
                            logcatProcess = null;
                        }
                        logcatProcess = Runtime.getRuntime().exec("logcat -v time -f " + logFile);
                        if (logcatProcess != null) {
                            CommonFunction.log("System.out", "Logcat process started");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }.start();
        }
    }

    public static void quit(Context context) {
        APKDownloadService.stopAllTask(context);

        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();

        ConnectLogin.getInstance(context).logout(context, 1);

        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        try {
            Method killBackgroundProcesses = am.getClass().getDeclaredMethod(
                    "killBackgroundProcesses", String.class);
            killBackgroundProcesses.setAccessible(true);
            killBackgroundProcesses.invoke(am, context.getPackageName());
        } catch (Exception e) {
            CommonFunction.log(e);
        }

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        System.exit(0);
    }

//    /**
//     * 标记启动遇见并且成功登录的次数
//     * @param context
//     */
//    public static void markOpenCount(Context context){
//        if (Common.getInstance().getStartAndLoginSuccess() == 1) {
//            SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
//            String markCountKey = SharedPreferenceUtil.MARK_OPEN_COUNT;
//            String markVersionCode = SharedPreferenceUtil.MARK_VERSION_CODE;
//
//            // 当前的版本versionCode;
//            int versionCode = 0;
//            try {
//                PackageManager pm = context.getPackageManager();
//                PackageInfo pinfo = pm.getPackageInfo(context.getPackageName(),
//                        PackageManager.GET_CONFIGURATIONS);
//                versionCode = pinfo.versionCode;
//            } catch (Throwable e) {
//                log(e);
//            }
//
//            if ( ( !sp.has( markVersionCode ) || ( sp.getInt( markVersionCode ) < versionCode ) ) ) {
//                sp.putInt(markCountKey, 1 );
//                sp.putInt( markVersionCode , versionCode );
//            }else {
//                if (sp.contains(markCountKey)) {
//                    int lastCount = sp.getInt(markCountKey);
//                    lastCount = lastCount + 1 ;
//                    sp.putInt(markCountKey, lastCount );
//                }else {
//                    sp.putInt(markCountKey, 1 );
//                }
//            }
//
//            if (sp.getInt(markCountKey) == 5) {
//                new Handler().postDelayed(new Runnable(){
//                    public void run() {
//                        Activity topActivity = CloseAllActivity.getInstance()
//                                .getTopActivity();
//                        if (topActivity != null && !MainFragmentActivity.getIsLogot()) {
//                            showMarkMeDialog(topActivity);
//                        }
//                    }
//                }, 5*1000);
//
//            }
//
//            Common.getInstance().setStartAndLoginSuccess(0);
//        }
//    }

//    /**
//     * 给遇见评分的dialog
//     * @param mContext
//     */
//    public static void showMarkMeDialog(final Context mContext){
//        final Dialog creditDialog = new Dialog( mContext , R.style.MyDialog );
//        View view = LayoutInflater.from( mContext ).inflate( R.layout.z_mark_me_dialog_view , null );
//        int dp_284 = ( int ) ( 284 * mContext.getResources( ).getDisplayMetrics( ).density );
//        creditDialog.setContentView( view , new LinearLayout.LayoutParams( dp_284 , LinearLayout.LayoutParams.WRAP_CONTENT ) );
//        creditDialog.show( );
//        creditDialog.setCanceledOnTouchOutside(false);
//        view.findViewById( R.id.take_credit ).setOnClickListener( new View.OnClickListener( )
//        {//给好评
//            @Override
//            public void onClick( View v )
//            {
//                takeCredit(mContext);
//                creditDialog.dismiss( );
//            }
//        } );
//
//        view.findViewById( R.id.to_feedback ).setOnClickListener( new View.OnClickListener( )
//        {//去反馈意见
//            @Override
//            public void onClick( View v )
//            {
//                Intent intent = new Intent( mContext , FeedBackActivity.class );
//                mContext.startActivity( intent );
//                creditDialog.dismiss( );
//            }
//        } );
//
//        view.findViewById( R.id.dismiss ).setOnClickListener( new View.OnClickListener( )
//        {//关闭
//            @Override
//            public void onClick( View v )
//            {
//                creditDialog.dismiss( );
//            }
//        } );
//    }

    /**
     * 判断登陆后是否显示引导页
     *
     * @param context
     */
    public static void showGuideView(Context context) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        int versionCode = 0;
        //获取当前版本号
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pinfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionCode = pinfo.versionCode;
        } catch (Throwable e) {
            CommonFunction.log(e);
        }
        //获取记录的版本号
        int oldVersionCode = sp.getInt(SharedPreferenceUtil.VERSION_CODE, 0);
        if (oldVersionCode < versionCode) {

            CommonFunction.log("SharedPreferenceUtil", " curVersionCode: " + versionCode + "  oldVersionCode: " + sp.getInt(SharedPreferenceUtil.VERSION_CODE));
            sp.putInt(SharedPreferenceUtil.VERSION_CODE, versionCode);

            Intent guidIntent = new Intent(context, GuideActivity.class);
            context.startActivity(guidIntent);
        } else {
            Intent intent = new Intent(context, MainFragmentActivity.class);
            context.startActivity(intent);
        }
    }

//    public static void showGuidView(Activity context) {
//        showGuidView(context, 0, null);
//    }
//
//    // 判断是否需要显示引导页面
//    public static void showGuidView(Activity context, int code, Bundle params) {
//        if (context == null) {
//            return;
//        }
//
//        // 第一次安装打开引导
//        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
//        // 当前的版本versionCode;
//        int versionCode = 0;
//        try {
//            PackageManager pm = context.getPackageManager();
//            PackageInfo pinfo = pm.getPackageInfo(context.getPackageName(),
//                    PackageManager.GET_CONFIGURATIONS);
//            versionCode = pinfo.versionCode;
//        } catch (Throwable e) {
//            log(e);
//        }
//
//        if (code <= 0) {
//            if ((!sp.has(SharedPreferenceUtil.VERSION_CODE) || (sp
//                    .getInt(SharedPreferenceUtil.VERSION_CODE) < versionCode))) {
//                int type = BaseHttp.checkNetworkType(context);
//                // 定时检测网络连接情况
//                if (type == BaseHttp.TYPE_NET_WORK_DISABLED) {
//                    //YC 取消LoginMainActivity的使用
////                    Intent loginMainIntent = new Intent(context, LoginMainActivity.class);
//                    Intent loginMainIntent = new Intent(context, RegisterNewActivity.class);
//                    context.startActivity(loginMainIntent);
//                    context.finish();
//                } else {
//
//                    CommonFunction.log(
//                            "--->SharedPreferenceUtil",
//                            versionCode + ":SharedPreferenceUtil:"
//                                    + sp.getInt(SharedPreferenceUtil.VERSION_CODE));
//                    String userKey = "_" + Common.getInstance().loginUser.getUid();
////                    sp.putBoolean( SharedPreferenceUtil.SIDEBAR_GUIDE_SHOWN + userKey , false );
////                    sp.putBoolean( SharedPreferenceUtil.NEARBY_GUIDE_SHOWN + userKey , false );//gh 去掉侧栏
//                    sp.putInt(SharedPreferenceUtil.VERSION_CODE, versionCode);
//
//                    // Intent guidIntent = new Intent(context,
//                    // GuideActivity.class);
//                    Intent guidIntent = new Intent(context, GuideActivity.class);
//                    guidIntent.putExtra("showmain", true);
//                    context.startActivity(guidIntent);
//                }
//            } else {
//                Intent intent = new Intent(context, MainFragmentActivity.class);
//                context.startActivity(intent);
//                return;
//            }
//        } else if (code == 1 && params != null) {
//            // 跳转到主页
//            Intent intent = new Intent(context, MainFragmentActivity.class);
//            intent.putExtra("ChatData", params.getSerializable("ChatData"));
//            intent.putExtra("tab", 2);
//            context.startActivity(intent);
//            return;
//        } else if (code == 2 && params != null) {
//            // 跳转到主页
//            Intent intent = new Intent(context, MainFragmentActivity.class);
//            intent.putExtra("SpaceData_uid", params.getSerializable("SpaceData_uid"));
//            intent.putExtra("tab", 2);
//            context.startActivity(intent);
//            return;
//        }
//    }

    /**
     * 解析出url参数中的键值对 如 "index.php?a=user&m=update" 解析出a:user,m:update,以map方式返回
     *
     * @param url url地址
     * @return map 参数
     */
    public static Map<String, String> paramURL(String url) {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String strUrlParam = null;
        String[] arrSplit = null;
        url = url.trim();
        arrSplit = url.split("[?]");
        if (url.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strUrlParam = arrSplit[1];
                }
            }
        }

        if (strUrlParam == null) {
            return mapRequest;
        }

        String[] paramSplit = null;
        // 每个键值为一组
        paramSplit = strUrlParam.split("[&]");
        for (String strSplit : paramSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");

            // 解析出键值
            if (arrSplitEqual.length > 1) {
                // 正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
                    // 只有参数没有值
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    /**
     * 打开本地apk
     *
     * @param context
     * @param apkid
     * @return void
     */
    public static void launchClient(Context context, String apkid) {
        if (apkid != null && apkid.length() > 0) {
            PackageManager pm = context.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(apkid);
            if (intent != null) {
                context.startActivity(intent);
            }
        }
    }

    /**
     * 执行来信声音提示
     */
    public static void notifyMsgVoice(Context context) {
        //gh 是否在麦上
        boolean isMic = SharedPreferenceUtil.getInstance(context).getBoolean(Common.getInstance().loginUser.getUid() + "/" + SharedPreferenceUtil.SYSTEM_MIC);
        if (isMic) return;
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    return;
                }
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(context, R.raw.notify);
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        } catch (Throwable e) {
            CommonFunction.log(e);
        }
    }

    public static String sourcePicture(String url) {
        if (!TextUtils.isEmpty(url)) {
            int last = url.lastIndexOf("_s");
            if (last < 0) {
                return url;
            }

            try {
                String lastStr = url.substring(url.lastIndexOf("_s") + 2, url.length());
                String startStr = url.substring(0, url.lastIndexOf("_s"));
                return startStr + lastStr;
            } catch (Exception e) {

            }
        }
        return "";
    }

    public static byte[] decodeBitmap(String path, Context context) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;// 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeFile(path, opts);
        int maxSampleSize = Math.min(getScreenPixWidth(context) * getScreenPixHeight(context) * 4, opts.outHeight * opts.outWidth);

        log("shifengxiong", "maxSampleSize ==" + maxSampleSize);
        opts.inSampleSize = computeSampleSize(opts, -1, maxSampleSize);
        opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inDither = false;
        opts.inPurgeable = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inTempStorage = new byte[16 * 1024];
        FileInputStream is = null;
        Bitmap bmp = null;
        ByteArrayOutputStream baos = null;
        try {
            is = new FileInputStream(path);
            if (null != is) {
                bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
//            double scale = getScaling(opts.outWidth * opts.outHeight,
//                    getScreenPixHeight( context ) * getScreenPixWidth( context ));
//            Bitmap bmp2 = Bitmap.createScaledBitmap(bmp,
//                    (int) (opts.outWidth * scale),
//                    (int) (opts.outHeight * scale), true);

                if (null != bmp) {
                    baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    bmp.recycle();
//            bmp2.recycle();
                    return baos.toByteArray();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is)
                    is.close();
                if (null != baos)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.gc();
        }
        return null; //baos.toByteArray();
    }

    public static int computeSampleSize(BitmapFactory.Options options,

                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,

                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) { // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }


    /*判断应用是否在前台*/
    public static boolean isForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public void setStreamVolume(Context context, int yourVolume) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, yourVolume, 0);
    }

    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * @param context
     * @return 判断网路是否是wifi
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * @param context
     * @return 判断网路是否是mobile
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static NetType getAPNType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return NetType.NONE;//网络不可用
        }
        int nType = networkInfo.getType();

        if (nType == ConnectivityManager.TYPE_MOBILE) {//
            return NetType.TYPE_MOBILE;
//            if (networkInfo.getExtraInfo().toLowerCase(Locale.getDefault()).equals("cmnet")) {
//                return NetType.CMNET;
//            } else {
//                return NetType.CMWAP;
//            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            return NetType.WIFI;//wifi 网络
        }
        return NetType.NONE;
    }

    /**
     * 序列化对象
     *
     * @param object
     * @return
     */
    public static String serialize(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        String serStr = "";
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            serStr = byteArrayOutputStream.toString("ISO-8859-1");
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return serStr;
    }

    public static Object getSerializtion(String str) {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        Object object = null;
        try {
            String redStr = java.net.URLEncoder.encode(str, "UTF-8");
            byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            object = objectInputStream.readObject();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }

    /**
     * 深度clone对象
     *
     * @param object
     * @return
     */
    public static Object deepClone(Object object) {
        ByteArrayOutputStream bo = null;
        ObjectOutputStream oo = null;
        ByteArrayInputStream bi = null;
        ObjectInputStream oi = null;
        //将对象写到流里
        try {

            bo = new ByteArrayOutputStream();
            oo = new ObjectOutputStream(bo);
            oo.writeObject(object);
            //从流里读出来
            bi = new ByteArrayInputStream(bo.toByteArray());
            oi = new ObjectInputStream(bi);
            return (oi.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bi != null) {
                try {
                    bi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oi != null) {
                try {
                    oi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bo != null) {
                try {
                    bo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oo != null) {
                try {
                    oo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 根据系统语言获取所需要的数据
     *
     * @param context
     * @param content 需要截取的内容
     * @param flag    需要截取的根据
     * @return
     */
    public static String getNameByLanguage(Context context, String content, String flag) {
        String names[];
        if (content.contains(flag)) {
            if (".".equals(flag))
                names = content.split("\\.");
            else if ("|".equals(flag))
                names = content.split("\\|");
            else
                names = content.split(flag);
            if (names.length >= 3) {
                if (CommonFunction.getLanguageIndex(context) == 0) {//英文
                    return names[0];
                } else if (CommonFunction.getLanguageIndex(context) == 1) {//中文简体
                    return names[1];
                } else if (CommonFunction.getLanguageIndex(context) == 2) {//中文繁体
                    return names[2];
                }
            }
        }
        return content;
    }

    /* 判断一个字符串是不是全是空格
     * */
    public static boolean isStringAllSpace(String str) {
        if (str == null) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取视频第一帧
     *
     * @param url
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static Bitmap createVideoThumbnail(String url) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
//        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
//            bitmap = ThumbnailUtils.createVideoThumbnail(bitmap, width, height,
//                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//        }
        return bitmap;
    }

    /*模糊图片 BITMAP
     *  只支持 API17
     *  radius 0.0f - 25.0f
     * */
    public static Bitmap fastBlur(Context context, Bitmap bitmap, int radius) {
//        if(Build.VERSION.SDK_INT>=17) {
//        if(false) {
//            Bitmap output = Bitmap.createBitmap(bitmap); // 创建输出图片
//            RenderScript rs = RenderScript.create(context); // 构建一个RenderScript对象
//            ScriptIntrinsicBlur gaussianBlue = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)); // 创建高斯模糊脚本
//            Allocation allIn = Allocation.createFromBitmap(rs, bitmap); // 创建用于输入的脚本类型
//            Allocation allOut = Allocation.createFromBitmap(rs, output); // 创建用于输出的脚本类型
//            gaussianBlue.setRadius(radius); // 设置模糊半径，范围0f<radius<=25f
//            gaussianBlue.setInput(allIn); // 设置输入脚本类型
//            gaussianBlue.forEach(allOut); // 执行高斯模糊算法，并将结果填入输出脚本类型中
//            allOut.copyTo(output); // 将输出内存编码为Bitmap，图片大小必须注意
//            gaussianBlue.destroy();
//            if(Build.VERSION.SDK_INT>=23){
//                // 关闭RenderScript对象，API>=23则使用rs.releaseAllContexts()
//                rs.releaseAllContexts();
//            }else {
//                rs.destroy();
//            }
//            return output;
//        }else{
        Bitmap output = bitmap.copy(bitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = output.getWidth();
        int h = output.getHeight();

        int[] pix = new int[w * h];
        output.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        output.setPixels(pix, 0, w, 0, 0, w, h);
        return (output);
//        }
    }


    public static boolean activityIsDestroyed(Activity activity) {
        if (activity == null) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= 17) {
            if (activity.isDestroyed()) {
                return true;
            }
        }
        return false;
    }


    /**
     * 拨打视频会话时或者收到视频会话邀请时 播放音乐文件
     */
    public static void playVideoChatVoice(Context context) {
        try {
            if (mVideoChatMediaPlayer != null) {
                if (mVideoChatMediaPlayer.isPlaying()) {
                    return;
                }
                mVideoChatMediaPlayer.release();
            }
            mVideoChatMediaPlayer = MediaPlayer.create(context, R.raw.videochat);
            if (mVideoChatMediaPlayer != null) {
                mVideoChatMediaPlayer.setLooping(true);
                mVideoChatMediaPlayer.start();
            }
        } catch (Throwable e) {
            log(e);
        }
    }

    /**
     * 视频通话开始后 停止播放音乐文件
     */
    public static void stopVideoChatVoice() {
        try {
            if (mVideoChatMediaPlayer != null) {
                if (mVideoChatMediaPlayer.isPlaying()) {
                    mVideoChatMediaPlayer.stop();
                }
                mVideoChatMediaPlayer.release();
            }
        } catch (Throwable t) {
            log(t);
        } finally {
            mVideoChatMediaPlayer = null;
        }
    }

    /**
     * 拨打语音会话时或者收到视频会话邀请时 播放音乐文件
     */
    public static void playAudioChatVoice(Context context) {
        try {
            if (mAudioChatMediaPlayer != null) {
                if (mAudioChatMediaPlayer.isPlaying()) {
                    return;
                }
                mAudioChatMediaPlayer.release();
            }
            mAudioChatMediaPlayer = MediaPlayer.create(context, R.raw.audiochat);
            if (mAudioChatMediaPlayer != null) {
                mAudioChatMediaPlayer.setLooping(true);
                mAudioChatMediaPlayer.start();
            }
        } catch (Throwable e) {
            log(e);
        }
    }

    /**
     * 语音通话开始后 停止播放音乐文件
     */
    public static void stopAudioChatVoice() {
        try {
            if (mAudioChatMediaPlayer != null) {
                if (mAudioChatMediaPlayer.isPlaying()) {
                    mAudioChatMediaPlayer.stop();
                }
                mAudioChatMediaPlayer.release();
            }
        } catch (Throwable t) {
            log(t);
        } finally {
            mAudioChatMediaPlayer = null;
        }
    }

    /*android服务是否存在
     * */
    public static boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if (serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 陪玩游戏等级排行背景色
     *
     * @param level
     * @return
     */
    public static int getRankColor(int level) {
        switch (level) {
            case 1:
                return BaseApplication.appContext.getResources().getColor(R.color.game_rank_text_color_one);
            case 2:
                return BaseApplication.appContext.getResources().getColor(R.color.game_rank_text_color_two);
            case 3:
                return BaseApplication.appContext.getResources().getColor(R.color.game_rank_text_color_three);
            case 4:
                return BaseApplication.appContext.getResources().getColor(R.color.game_rank_text_color_four);
            case 5:
                return BaseApplication.appContext.getResources().getColor(R.color.game_rank_text_color_five);
            case 6:
                return BaseApplication.appContext.getResources().getColor(R.color.game_rank_text_color_six);
            case 7:
                return BaseApplication.appContext.getResources().getColor(R.color.game_rank_text_color_seven);
            case 8:
                return BaseApplication.appContext.getResources().getColor(R.color.game_rank_text_color_eight);
        }
        return BaseApplication.appContext.getResources().getColor(R.color.game_rank_text_color_one);

    }

    /**
     * 更新展示广告次数,一天最多请求20次
     * @param key
     * @return 未满20次可以展示广告
     */
    public static boolean updateAdCount(String key) {
        if ((TimeFormat.getCurrentTimeMillis() - SharedPreferenceUtil.getInstance(BaseApplication.appContext).getLong("time" + key)) > 86400 * 1000) { //时间间隔大于24小时
            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putInt("count" + key, 0);//重置次数
        }
        int count = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getInt("count" + key, 0);
        if (count <= 20) {
            count++;
            //更新展示启动页广告最后展示时间
            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong("time" + key, TimeFormat.getCurrentTimeMillis());
            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putInt("count" + key, count);
            return true;
        }
        return false;
    }

}
