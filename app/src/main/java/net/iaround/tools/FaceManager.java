package net.iaround.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import net.iaround.BaseApplication;
import net.iaround.tools.glide.GlideUtil;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.model.entity.Face;
import net.iaround.model.entity.FaceCenterModel;
import net.iaround.model.entity.MyFaceListBean;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.iaround.model.entity.FaceDescrise;
import net.iaround.model.entity.FaceDescrise.Descrise;

/**
 * 表情资源文件对照表
 *
 * @author Administrator
 */
public class FaceManager {
    public static final String catRegex = "\\[\\w+\\]";
    public static final String emojoRegex = "\\[\\#\\w+\\#\\]";
    public static final String FACE_INIT_ACTION = "net.iaround.face.chatface.getVisitorList";
    public static final String replaceNoFace = "...";
    private static final String leftflag = "[#";
    public static final String catFlag = "[#";
    public static final String catFlagright = "#]";
    private static final String rightflag = "#]";
    private static final String otherLeftFlag = "{#";
    private static final String otherRightFlag = "#}";
    private static Map<String, Integer> sCatFace = new LinkedHashMap<String, Integer>();// 小情猫对应中文英文
    private static Map<String, Integer> sLocalAdd = new LinkedHashMap<String, Integer>();// emoji表情IOS7新增
    private static Map<String, Integer> sLocalFace = new LinkedHashMap<String, Integer>();// emoji表情
    private static Map<String, Integer> sSpecialFace = new LinkedHashMap<String, Integer>();// 小情猫表情
    public static Map<String, String> sCatFace2Str; // [#xxx#]表情对应文本
    private static Map<String, Drawable> sRemoteFace = new LinkedHashMap<String, Drawable>();// 远程表情

    private static String catemoji = "\\[\\#\\w+\\#\\]|";
    private static String regex1 = "\ud83c\udde8\ud83c\uddf3|\ud83c\udde9\ud83c\uddea|\ud83c\uddea\ud83c\uddf8|\ud83c\uddeb\ud83c\uddf7|\ud83c\uddec\ud83c\udde7|\ud83c\uddee\ud83c\uddf9|\ud83c\uddef\ud83c\uddf5|\ud83c\uddf0\ud83c\uddf7|\ud83c\uddf7\ud83c\uddfa|\ud83c\uddfa\ud83c\uddf8|";
    private static String regex2 = "\u00a9|\u00ae|\u203c|\u2049|\u2122|\u2139|\u2194|\u2195|\u2196|\u2197|\u2198|\u2199|\u21a9|\u21aa|\u231a|\u231b|\u23e9|\u23ea|\u23eb|\u23ec|\u23f0|\u23f3|\u24c2|\u25aa|\u25ab|\u25b6|\u25c0|\u25fb|\u25fc|\u25fd|\u25fe|\u2600|\u2601|\u260e|\u2611|\u2614|\u2615|\u261d|\u263a|\u2648|\u2649|\u264a|\u264b|\u264c|\u264d|\u264e|\u264f|\u2650|\u2651|\u2652|\u2653|\u2660|\u2663|\u2665|\u2666|\u2668|\u267b|\u267f|\u2693|\u26a0|\u26a1|\u26aa|\u26ab|\u26bd|\u26be|\u26c4|\u26c5|\u26ce|\u26d4|\u26ea|\u26f2|\u26f3|\u26f5|\u26fa|\u26fd|\u2702|\u2705|\u2708|\u2709|\u270a|\u270b|\u270c|\u270f|\u2712|\u2714|\u2716|\u2728|\u2733|\u2734|\u2744|\u2747|\u274c|\u274e|\u2753|\u2754|\u2755|\u2757|\u2764|\u2795|\u2796|\u2797|\u27a1|\u27b0|\u27bf|\u2934|\u2935|\u2b05|\u2b06|\u2b07|\u2b1b|\u2b1c|\u2b50|\u2b55|\u3030|\u303d|\u3297|\u3299|";
    public static String regex = catemoji + regex1 + regex2 +
            "[\ud83d\udc00-\ud83d\udec5]|[0-9]\u20E3|#\u20E3|[\ud83c\udc04-\ud83d\udff0]";

    private Pattern emojiPattern;
    private Pattern regexPattern;
    private Pattern catRegexPattern;

    private static Map<String, String> sHistoryFace = new LinkedHashMap<String, String>();// 兼容就版本的表情key

    // 所有表情每个分组的所有表情
    private static HashMap<String, ArrayList<FaceIcon>> mFaceGroup = new HashMap<String, ArrayList<FaceIcon>>();

    // 动态表情的分组
    public static ArrayList<FaceLogoIcon> mFaceGroupLogos = new ArrayList<FaceLogoIcon>();
    // 我拥有的动态表情
    public static ArrayList<FaceLogoIcon> mOwnGifFaces = new ArrayList<FaceLogoIcon>();

    private static Map<String, FaceLogoIcon> sLastUseFace = new LinkedHashMap<String, FaceLogoIcon>();// 最后使用的动态表情

    private static FaceManager faceManager;
    private static String faceDir;
    private static Context mContext;
    private static ArrayList<Face> face;
    private static ArrayList<FaceLogoIcon> temList = new ArrayList<FaceLogoIcon>();
    /**
     * 存储获取到的已排序的表情列表
     */
    private static ArrayList<Face> sortList = new ArrayList<Face>();
    private static Map<String, ArrayList<FaceIcon>> descriseFacesMap = new HashMap<String, ArrayList<FaceIcon>>();

    private FaceManager(Context context) {
        faceDir = CommonFunction.getSDPath() + "/face";
        mContext = context;

        if (sLastUseFace.isEmpty()) {
            String strSave = SharedPreferenceCache.getInstance(context)
                    .getString("last_use_face" + Common.getInstance().getUid());
            if (!TextUtils.isEmpty(strSave)) {
                try {
                    JSONObject js = new JSONObject(strSave);
                    JSONArray arr = js.getJSONArray("items");
                    if (arr != null && arr.length() > 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            FaceLogoIcon face = new FaceLogoIcon();
                            face.feetType = arr.getJSONObject(i).optInt("feetType");
                            face.pkgId = arr.getJSONObject(i).optInt("pkgId");
                            face.key = arr.getJSONObject(i).optString("key");
                            face.iconPath = arr.getJSONObject(i).optString("iconPath");
                            if (arr.getJSONObject(i).optString("description") == null) {
                                face.description = "";
                            } else {
                                face.description = arr.getJSONObject(i)
                                        .optString("description");
                            }
                            sLastUseFace.put(face.key, face);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        initFace();
        initPattern();
    }

    /**
     * 初始化正则表达式，降低compile占用的时间
     */
    private void initPattern() {
        if (null == emojiPattern) {
            emojiPattern = Pattern.compile(emojoRegex);// 匹配[# #]
        }

        if (null == regexPattern) {
            regexPattern = Pattern.compile(regex);
        }

        if (null == catRegexPattern) {
            catRegexPattern = Pattern.compile(catRegex, Pattern.UNICODE_CASE);
        }
    }

    public static synchronized FaceManager getInstance(Context context) {
        if (faceManager == null) {
            faceManager = new FaceManager(context.getApplicationContext());

        }

        return faceManager;
    }

    /**
     * 初始化所有表情
     */
    public static void initFace() {
        initLastUseFace(); // 初始化最后使用表情
        initSpecialFace(); // 初始化遇见表情
        initLoaclFace(); // 初始化常规表情
        initOtherFaceGroup();// 初始化动态表情
    }

    /**
     * 初始化动态表情
     */
    public static void resetOtherFace() {
        mFaceGroupLogos.clear();
        mFaceGroup.clear();
        initLastUseFace(); // 初始化最后使用表情
        initSpecialFace(); // 初始化遇见表情
        initLoaclFace(); // 初始化常规表情
        initOtherFaceGroup();// 初始化动态表情
    }

    /*
     * 最近使用的表情
     */
    public static void addLastUserFace(Context context, String key, FaceLogoIcon face) {

        if (sLastUseFace.containsKey(key)) {
            sLastUseFace.remove(key);
        }
        sLastUseFace.put(key, face);
        // 将map保存文件
        String str = toJsonString(sLastUseFace);

        SharedPreferenceCache.getInstance(context).putString("last_use_face" + Common.getInstance().getUid(), str);

    }

    /**
     * 清除所有动态表情
     */
    public void clearFace() {
        mFaceGroupLogos.clear();
        mFaceGroup.clear();
        sLastUseFace.clear();
        faceManager = null;
    }

    /**
     * 初始化小情猫表情
     */
    private static void initSpecialFace() {
        ArrayList<FaceIcon> faces = new ArrayList<FaceIcon>();
        for (Entry<String, Integer> entry : sSpecialFace.entrySet()) {
            FaceIcon face = new FaceIcon();
            face.key = catFlag + entry.getKey() + catFlagright;
            face.iconPath = "android.resource://net.iaround/raw/" + entry.getValue();
            face.iconId = entry.getValue();
            faces.add(face);
        }

        FaceLogoIcon specialFaceLogo = new FaceLogoIcon();
        specialFaceLogo.key = "special";
        specialFaceLogo.iconPath = "android.resource://net.iaround/raw/" + R.raw.f62; // 遇见分组封面
        mFaceGroupLogos.add(specialFaceLogo);
        mFaceGroup.put("special", faces);
    }

    /**
     * 初始化最后使用的表情
     */
    private static void initLastUseFace() {
        ArrayList<FaceIcon> faces = new ArrayList<FaceIcon>();
        ArrayList<FaceIcon> tempfaces = new ArrayList<FaceIcon>();
        ArrayList<FaceIcon> delFace = new ArrayList<FaceIcon>();

        for (Entry<String, FaceLogoIcon> entry : sLastUseFace.entrySet()) {

            FaceIcon face = new FaceIcon();
            face.key = entry.getKey();
            face.iconPath = entry.getValue().iconPath;
            face.pkgId = entry.getValue().pkgId;
            if (entry.getValue().description == null ||
                    entry.getValue().description.equals("null")) {
                face.description = "";
            } else {
                face.description = entry.getValue().description;
            }
            String sub = face.iconPath.replace("file://", "");

            if (CommonFunction.isFileExist(sub)) {
                tempfaces.add(face); // 添加表情至数组
            } else {
                delFace.add(face);
            }
        }

        int imax = tempfaces.size();
        int size = tempfaces.size();
        if (tempfaces.size() > 24) {
            imax = 24;
        }

        for (int i = 0; i < imax; i++) {
            FaceIcon face = new FaceIcon();
            faces.add(face);
        }
        for (int i = 0; i < size; i++) {
            FaceIcon face = new FaceIcon();
            face.key = tempfaces.get(size - i - 1).key;
            face.iconPath = tempfaces.get(size - i - 1).iconPath;
            face.pkgId = tempfaces.get(size - i - 1).pkgId;
            face.iconId = tempfaces.get(size - i - 1).iconId;
            if (tempfaces.get(size - i - 1).description == null ||
                    tempfaces.get(size - i - 1).description.equals("")) {
                face.description = "";
            } else {
                face.description = tempfaces.get(size - i - 1).description;
            }
            if (i < imax) {
                faces.set(i, face);
            } else {
                delFace.add(face);
            }
        }

        for (FaceIcon face : delFace) {
            sLastUseFace.remove(face.key);
        }
        FaceLogoIcon specialFaceLogo = new FaceLogoIcon();
        specialFaceLogo.key = "last";
        specialFaceLogo.iconPath = "android.resource://net.iaround/raw/" + R.drawable.face_recently_use; // 遇见分组封面
        mFaceGroupLogos.add(specialFaceLogo);
        mFaceGroup.put("last", faces);
    }

    /**
     * 初始化Emoji表情
     */
    private static void initLoaclFace() { // 初始化基本表情 标签为 NORMAL
        ArrayList<FaceIcon> faces = new ArrayList<FaceIcon>();
        for (Entry<String, Integer> entry : sLocalFace.entrySet()) {
            FaceIcon face = new FaceIcon();
            face.key = entry.getKey();
            face.iconPath = "android.resource://net.iaround/raw/" + entry.getValue();
            face.iconId = entry.getValue();
            faces.add(face);
        }

        FaceLogoIcon localFaceLogo = new FaceLogoIcon();
        localFaceLogo.key = "normal";
        localFaceLogo.iconPath = "android.resource://net.iaround/raw/" + R.raw.em_d83dde03; // 常规分组封面
        mFaceGroupLogos.add(localFaceLogo);
        mFaceGroup.put("normal", faces);
    }

    /**
     * 初始化动态表情
     */
    public static void initOtherFaceGroup() {
        // 将文件夹下的目录对应至数组中，在需要的時候再取出对应的文件
        String faceDirPath = faceDir + "/" + Common.getInstance().loginUser.getUid();
        File[] faceDirs = null;
        File faceGroupFile = new File(faceDirPath);
        if (faceGroupFile.exists() && faceGroupFile.isDirectory()) {
            faceDirs = faceGroupFile.listFiles();
            for (File faceDir : faceDirs) { // 1级目录 /iaround/face/UID/
                if (!faceDir.isDirectory()) {
                    continue;
                }
                String name = "";
                ArrayList<FaceIcon> faces = new ArrayList<FaceIcon>();
                FaceLogoIcon faceLogo = null;// 封面key=文件夹名 需要的时候再去下面取logo
                File[] faceFiles = faceDir.listFiles();
                for (File faceFile : faceFiles) { // 2级目录
                    if (!faceFile.isFile() || faceFile.getName().equals(".nonmedia")) {
                        continue;
                    }
                    String fileName = faceFile.getName(); // /iaround/face/UID/**/
                    if (fileName.indexOf("logo") == -1) {
                        name = fileName.substring(0, fileName.indexOf("."));
                        FaceIcon face = new FaceIcon();
                        face.key = otherLeftFlag + name + otherRightFlag;
                        face.iconPath = "file://" + faceFile.getAbsolutePath();

                        int index = 0;
                        try {
                            index = Integer.parseInt(
                                    name.substring(name.indexOf("_") + 1, name.length()));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            continue;
                        }
                        face.setSortIndex(index);
                        faces.add(face); // 添加表情至数组
                    } else {
                        faceLogo = new FaceLogoIcon();
                        faceLogo.pkgId = Integer.valueOf(faceDir.getName());
                        faceLogo.key = otherLeftFlag + faceLogo.pkgId + "_" +
                                fileName.substring(0, fileName.indexOf(".")) + otherRightFlag;
                        faceLogo.iconPath = "file://" + faceFile.getAbsolutePath();
                    }
                }
                if (faceLogo != null) {
                    Collections.sort(faces);
                    mFaceGroupLogos.add(faceLogo);// 添加logo对象至數組
                    mFaceGroup.put(faceLogo.key, faces);// 完成一个分组的添加
                    descriseFacesMap.put(faceDir.getName(), faces);
                }
            }
        }

        SortFace();// 将表情菜单视图的表情按排序后的顺序显示
        if (faceDirs != null) {
            addFaceDescrise(faceDirs);// 添加表情描述
        }
    }

    /**
     * 添加表情描述
     *
     * @param faceDirs
     */
    private static void addFaceDescrise(File[] faceDirs) {
        Iterator<Entry<String, ArrayList<FaceIcon>>> it = descriseFacesMap.entrySet()
                .iterator();
        while (it.hasNext()) {
            Entry<String, ArrayList<FaceIcon>> entry = it.next();
            String key = entry.getKey();

            for (File txtFile : faceDirs) {
                if (txtFile.getName().contains(".txt") &&
                        txtFile.getName().replace(".txt", "").equals(key)) {
                    ArrayList<FaceIcon> faceList = entry.getValue();
                    initDescrise(faceList, txtFile);// 增加表情描述
                    break;
                }
            }
        }
    }

    /**
     * 初始化表情描述
     *
     * @param faces
     * @param txtFile
     */
    private static void initDescrise(ArrayList<FaceIcon> faces, File txtFile) {
        ArrayList<Descrise> list = readFaceDescribe(txtFile.getAbsolutePath());
        for (Descrise descrise : list) {
            if (faces != null && faces.size() > 0) {
                for (FaceIcon faceIcon : faces) {
                    String str = faceIcon.key;
                    str = str.replace("{", "").replace("#", "").replace("}", "");
                    if (str.equals(descrise.filename) && descrise.description.contains("@")) {
                        String[] split = descrise.description.split("@");
                        if (split.length == 3) {
                            for (int i = 0; i < split.length; i++) {
                                int Lang = CommonFunction.getLang(mContext);
                                faceIcon.description = split[Lang - 1];
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 从表情压缩包里的文件读取表情描述
     *
     * @param filePath
     * @return
     */
    private static ArrayList<Descrise> readFaceDescribe(String filePath) {
        String result = "";
        ArrayList<Descrise> list = new ArrayList<Descrise>();

        if (!filePath.isEmpty()) {
            try {
                result = readFileSdcardFile(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!result.isEmpty()) {
                FaceDescrise bean = GsonUtil.getInstance()
                        .getServerBean(result, FaceDescrise.class);
                if (bean != null) {
                    list = bean.descriptions;
                    return list;
                }
            }
        }
        return null;
    }

    // 读SD中的文件
    public static String readFileSdcardFile(String fileName) throws IOException {
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 将表情菜单视图的表情按排序后的顺序显示
     */
    private static void SortFace() {
        sortList = getSortFaceList();// 获取保存过的，有顺序的表情列表
        if (sortList.size() > 0) {
            Sort();// 进行排序
            addNewInstalledFace();// 从获取已排序列表后，若有新下载的，添加到小情猫的后面
        }
    }

    /**
     * 获取保存过的，有顺序的表情列表
     *
     * @return
     */
    private static ArrayList<Face> getSortFaceList() {
        ArrayList<Face> getSortList = new ArrayList<Face>();

        if (FaceCenterModel.ownFace != null && FaceCenterModel.ownFace.size() > 0) {
            getSortList = FaceCenterModel.ownFace;
        } else {
            String Uid = String.valueOf(Common.getInstance().getUid());
            String data = SharedPreferenceCache.getInstance(mContext).getString("myFace" + Uid);
            if (!data.isEmpty()) {
                MyFaceListBean bean = GsonUtil.getInstance()
                        .getServerBean(data, MyFaceListBean.class);
                getSortList = bean.ownList;
            }
        }

        return getSortList;
    }

    /**
     * 进行排序
     */
    private static void Sort() {
        temList = delFaceGroupLogos();// 返回暂存的所有Logo數組，再清除原本没顺序
        for (Face face : sortList) {
            for (FaceLogoIcon logo : temList) {
                if (face.getFaceid() == logo.pkgId) {
                    mFaceGroupLogos.add(logo);
                }
            }
        }
    }

    /**
     * 若有新下载的，添加在小情猫的logo后面
     */
    private static void addNewInstalledFace() {
        ArrayList<Integer> logoTemp = new ArrayList<Integer>();
        for (FaceLogoIcon logo : temList) {
            logoTemp.add(logo.pkgId);
        }
        ArrayList<Integer> sortTemp = new ArrayList<Integer>();
        for (Face sort : sortList) {
            sortTemp.add(sort.getFaceid());
        }

        logoTemp.removeAll(sortTemp);// 移除已经有排序过的logo

        for (Integer temp : logoTemp) {
            for (FaceLogoIcon faceLogo : temList) {
                if (temp == faceLogo.pkgId) {
                    if (faceLogo.pkgId == 0) {
                        continue;
                    }
                    mFaceGroupLogos.add(3, faceLogo);
                    break;
                }
            }
        }
    }

    /**
     * 返回暂存的所有Logo數組，再清除原本没顺序
     *
     * @return tempList
     */
    private static ArrayList<FaceLogoIcon> delFaceGroupLogos() {
        // 临时存放logo对象的数组
        ArrayList<FaceLogoIcon> tempList = new ArrayList<FaceLogoIcon>();
        for (FaceLogoIcon faceLogo : mFaceGroupLogos) {
            tempList.add(faceLogo);// 添加logo对象至數組
        }

        // 清除原本没有排序过的數組，除了pkgId为0的，0为小情猫与Emoji表情
        for (int i = 0; i < mFaceGroupLogos.size(); i++) {
            if (mFaceGroupLogos.get(i).pkgId != 0) {
                mFaceGroupLogos.remove(i);
                i = i - 1;
            }
        }
        return tempList;
    }

    /**
     * 检查表情是否是VIP的
     *
     * @param pkgId
     * @return
     */
    public boolean checkPackIsVip(int pkgId) {
        if (mOwnGifFaces == null) {
            return false;
        }

        for (FaceLogoIcon f : mOwnGifFaces) {
            if (f.pkgId == pkgId && f.feetType == 2) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前用户动态表情分组
     *
     * @return
     */
    public static ArrayList<FaceLogoIcon> getGifFaceGroupLogos() {// 返回持有分组目录的数组
        Iterator<FaceLogoIcon> iconIterator = mFaceGroupLogos.iterator();
        iconIterator.next();
        iconIterator.next();
        while (iconIterator.hasNext()) {
            FaceLogoIcon icon = iconIterator.next();
            if (mOwnGifFaces != null) {
                boolean ver = false;
                for (FaceLogoIcon face : mOwnGifFaces) {
                    if (face.pkgId == icon.pkgId) {
                        ver = true;
                        break;
                    }
                }

                if (!ver) {
                    // 表情已失效
                    icon.valid = 0;
                    // iconIterator.remove();
                }
            }
        }

        return mFaceGroupLogos;
    }

    /**
     * 根据表情文件的路径获取文件
     *
     * @param path
     * @return
     */
    public File getFaceStreamFromPath(String path) { // 根据路径找到文件
        try {
            StringBuilder sb = new StringBuilder(faceDir);
            String[] relativePath = path.split("_");
            if (relativePath.length == 2) {
                sb.append("/").append(Common.getInstance().getUid()).append("/")
                        .append(relativePath[0]).append("/").append(path);
            }

            File faceFile = new File(sb.toString() + PathUtil.getDynamicFacePostfix());
            File faceFile1 = new File(sb.toString() + PathUtil.getGifPostfix());
            if (faceFile.exists() || faceFile1.exists()) {
                if (faceFile.exists()) {
                    return faceFile;
                } else {
                    return faceFile1;
                }
            } else {
                faceFile = new File(sb.toString() + PathUtil.getStaticFacePostfix());
                faceFile1 = new File(sb.toString() + PathUtil.getPNGPostfix());
                if (faceFile.exists() || faceFile1.exists()) {
                    if (faceFile.exists()) {
                        return faceFile;
                    } else {
                        return faceFile1;
                    }
                } else {
                    return null;
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Drawable getDrawableWithCatKey(Context context, String key) {
        int id = 0; // 默认

        if (sCatFace.containsKey(key)) {
            id = sCatFace.get(key);
        }
        if (id > 0) {
            return context.getResources().getDrawable(id);
        } else
            return null;
    }

    public Drawable getDrawableWithFaceKey(Context context, String key) {
        return getDrawableWithFaceKey(context, key, null);
    }

    public interface FaceListener {
        void onRefresh();
    }

    /**
     * 根据表情图标显示表情
     *
     * @param key 图标名称
     * @return 返回对应资源文件的id ,当找不到相应的id是则返回0，不显示表情
     * @time 2011-6-29 上午11:36:12
     * @author:linyg
     */
    @SuppressWarnings("ResourceType")
    public Drawable getDrawableWithFaceKey(Context context, String key,
                                           final FaceListener listener) {
        int id = 0; // 默认
        key = key.replace(leftflag, "").replace(rightflag, "");

        if (sLocalFace.containsKey(key)) {
            id = sLocalFace.get(key);
        } else if (sLocalAdd.containsKey(key)) {
            id = sLocalAdd.get(key);
        } else if (sSpecialFace.containsKey(key)) {
            id = sSpecialFace.get(key);
        } else if (sRemoteFace.containsKey(key)) {
            return sRemoteFace.get(key);
        } else if ("back".equals(key)) {
            return context.getResources().getDrawable(R.raw.delete_face_unselect);
        }

        if (id > 0) {
            return context.getResources().getDrawable(id);
        } else {

            final String finalKey = key;
            GlideUtil.loadImage(BaseApplication.appContext, Config.sFaceHost + key + ".png", new GlideDrawableImageViewTarget(new ImageView(context)) {
                @Override
                public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                    super.onResourceReady(drawable, anim);
                    sRemoteFace.put(finalKey, drawable);
                }
            });
            return null;
        }
    }

    /**
     * 将Icon对应的图片贴到ImageView上
     *
     * @param view
     * @param iconPath
     * @return void
     */
    public void showFaceSelect(ImageView view, String iconPath) {
        if (TextUtils.isEmpty(iconPath) || view == null) {
            return;
        }

        GlideUtil.loadImage(BaseApplication.appContext, iconPath, view);
    }

    public void showFaceSelect(ImageView view, int iconid) {
        if (iconid <= 0 || view == null) {
            return;
        }
        view.setBackgroundResource(iconid);
    }

    /**
     * 根据tab获取指定的表情
     */
    public ArrayList<FaceIcon> getFaceLocalList(String tag) {
        return mFaceGroup.get(tag);
    }

    /**
     * 替换图标： 用于EditText
     */
//    EditText spanEtContent;
    public void parseIconForEditText(Context context, EditText etContent) {
        String text = etContent.getText().toString();
        FaceManager faceManager = FaceManager.getInstance(context.getApplicationContext());
//        if (spanEtContent == null || spanEtContent != etContent) {
//            spanEtContent = etContent;
//        }
        List<EditSpan> tempSpanList = new ArrayList<EditSpan>();
        tempSpanList.clear();
        boolean isHaveEmoji = false;

        Resources res = context.getResources();
        float faceSize = res.getDimension(R.dimen.face_height);

        Pattern p = Pattern.compile(regex, Pattern.UNICODE_CASE);
        Matcher m = p.matcher(text);// 开始编译

        while (m.find()) {
            String str = m.group();
            String icon = "";
            if (str.startsWith(catFlag) && str.endsWith(catFlagright)) {
                icon = str;
            } else {

                for (int i = 0; i < str.length(); i++) {
                    char c = str.charAt(i);
                    int v = (int) c;
                    icon += Integer.toHexString(v);
                }
            }

            // 查找对应的图标
            Drawable drawable = faceManager.getDrawableWithFaceKey(context, icon);

            if (drawable != null) {
                drawable.setBounds(0, 0, (int) faceSize, (int) faceSize);
                ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                isHaveEmoji = true;
                EditSpan temEdtSpan = new EditSpan();
                temEdtSpan.span = span;
                temEdtSpan.start = m.start();
                temEdtSpan.end = m.end();
                temEdtSpan.key = str;
                tempSpanList.add(temEdtSpan);
            }

            m.groupCount();
        }

        p = Pattern.compile(catRegex, Pattern.UNICODE_CASE);
        m = p.matcher(text);// 开始编译

        while (m.find()) {
            String str = m.group();
            // 查找对应的图标
            Drawable drawable = faceManager.getDrawableWithCatKey(context, str);

            if (drawable != null) {
                drawable.setBounds(0, 0, (int) faceSize, (int) faceSize);
                ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                isHaveEmoji = true;

                EditSpan temEdtSpan = new EditSpan();
                temEdtSpan.span = span;
                temEdtSpan.start = m.start();
                temEdtSpan.end = m.end();
                temEdtSpan.key = str;
                tempSpanList.add(temEdtSpan);
            }

            m.groupCount();
        }

        if (isHaveEmoji) {
            Editable edable = etContent.getText();
            for (int i = 0, imax = tempSpanList.size(); i < imax; i++) {
                edable.setSpan(tempSpanList.get(i).span, tempSpanList.get(i).start,
                        tempSpanList.get(i).end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public void parseIconForEditText(Context context, EditText etContent, int start, int before, int count) {
        String resource = etContent.getText().toString();
        String text = "";

        if (count > 0) {
            text = resource.substring(start, start + count);
        } else {
            return;
        }

        FaceManager faceManager = FaceManager.getInstance(context.getApplicationContext());
//        if (spanEtContent == null || spanEtContent != etContent) {
//            spanEtContent = etContent;
//            CommonFunction.log("FaceManager", "spanList.clear =====================");
//        }
        List<EditSpan> tempSpanList = new ArrayList<EditSpan>();
        tempSpanList.clear();
        boolean isHaveEmoji = false;

        Resources res = context.getResources();
        float faceSize = res.getDimension(R.dimen.face_height);

        Pattern p = Pattern.compile(regex, Pattern.UNICODE_CASE);
        Matcher m = p.matcher(text);// 开始编译

        while (m.find()) {
            String str = m.group();
            String icon = "";
            if (str.startsWith(catFlag) && str.endsWith(catFlagright)) {
                icon = str;
            } else {

                for (int i = 0; i < str.length(); i++) {
                    char c = str.charAt(i);
                    int v = (int) c;
                    icon += Integer.toHexString(v);
                }
            }

            // 查找对应的图标
            Drawable drawable = faceManager.getDrawableWithFaceKey(context, icon);

            if (drawable != null) {
                drawable.setBounds(0, 0, (int) faceSize, (int) faceSize);
                ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                isHaveEmoji = true;
                EditSpan temEdtSpan = new EditSpan();
                temEdtSpan.span = span;
                temEdtSpan.start = m.start();
                temEdtSpan.end = m.end();
                temEdtSpan.key = str;
                tempSpanList.add(temEdtSpan);
            }
            m.groupCount();
        }

        p = Pattern.compile(catRegex, Pattern.UNICODE_CASE);
        m = p.matcher(text);// 开始编译

        while (m.find()) {
            String str = m.group();
            // 查找对应的图标
            Drawable drawable = faceManager.getDrawableWithCatKey(context, str);

            if (drawable != null) {
                drawable.setBounds(0, 0, (int) faceSize, (int) faceSize);
                ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                isHaveEmoji = true;

                EditSpan temEdtSpan = new EditSpan();
                temEdtSpan.span = span;
                temEdtSpan.start = m.start();
                temEdtSpan.end = m.end();
                temEdtSpan.key = str;
                tempSpanList.add(temEdtSpan);
            }

            m.groupCount();
        }

        if (isHaveEmoji) {
            Editable edable = etContent.getText();
            for (int i = 0, imax = tempSpanList.size(); i < imax; i++) {
                edable.setSpan(tempSpanList.get(i).span, start + tempSpanList.get(i).start,
                        start + tempSpanList.get(i).end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
        }
    }

    class EditSpan {
        public ImageSpan span;
        public int start;
        public int end;
        public String key;
    }

    public SpannableString parseIconForString(final TextView view, final Context context,
                                              final String text, final int faceSizeDp) {
        return parseIconForString(context, text, faceSizeDp, new FaceListener() {

            @Override
            public void onRefresh() {
                view.setText(parseIconForString(view, context, text, faceSizeDp));
            }
        });
    }

    public SpannableString parseIconForStringBaseline(final TextView view, final Context context,
                                                      final String text, final int faceSizeDp) {
        return parseIconForStringBaseline(context, text, faceSizeDp, new FaceListener() {

            @Override
            public void onRefresh() {
                view.setText(parseIconForString(view, context, text, faceSizeDp));
            }
        });
    }


    /**
     * 替换图标: 用于TextView、String等
     *
     * @param text
     * @param faceSizeDp 表情的大小(单位：dp)，默认值为24dp；不需要指定大小时，参数设置为0即可 表情靠下显示
     */
    public SpannableString parseIconForString(Context context, String text, int faceSizeDp,
                                              FaceListener listener) {
        if (CommonFunction.isEmptyOrNullStr(text)) {
            return new SpannableString("");
        }

        try {
            String strSource = new String(text);

            FaceManager faceManager = FaceManager.getInstance(context.getApplicationContext());
            Resources res = context.getResources();
            float faceSize = res.getDimension(R.dimen.face_height); // 默认值为24dp
            if (faceSizeDp > 0) {
                float oneDp = res.getDimension(R.dimen.dp_1);
                faceSize = faceSizeDp * oneDp;
            }

            SpannableString spannable = null;
//            Pattern emojiP = Pattern.compile(emojoRegex);// 匹配[# #]
            Matcher emojim = emojiPattern.matcher(strSource);// 开始编译

            while (emojim.find()) {
                String str = emojim.group();
                String icon = sHistoryFace.get(str);
                if (icon != null && strSource.contains(str)) {
                    strSource = strSource.replace(str, icon);
                }

            }
//            Pattern p = Pattern.compile(regex);
            Matcher m = regexPattern.matcher(strSource);// 开始编译

            spannable = new SpannableString(strSource.toString());

            while (m.find()) {
                // String icon = m.group( );

                String str = m.group();
                String icon = "";
                if (str.startsWith(catFlag) && str.endsWith(catFlagright)) {
                    icon = str;
                } else {

                    for (int i = 0; i < str.length(); i++) {
                        char c = str.charAt(i);
                        int v = (int) c;
                        icon += Integer.toHexString(v);
                    }
                }

                // 查找对应的图标
                Drawable drawable = faceManager.getDrawableWithFaceKey(context, icon, listener);
                if (drawable != null) {
                    drawable.setBounds(0, 0, (int) faceSize, (int) faceSize);
                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    spannable.setSpan(span, Math.max(0, m.start()),
                            Math.min(spannable.length(), m.end()),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                m.groupCount();
            }
//            p = Pattern.compile(catRegex, Pattern.UNICODE_CASE);
            m = catRegexPattern.matcher(strSource);// 开始编译

            while (m.find()) {
                String str = m.group();
                // 查找对应的图标
                Drawable drawable = faceManager.getDrawableWithCatKey(context, str);

                if (drawable != null) {
                    drawable.setBounds(0, 0, (int) faceSize, (int) faceSize);
                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);

                    CommonFunction.log("sherlock",
                            "FaceManager.parseIconForString apannable == " + spannable.toString(),
                            spannable.length());
                    CommonFunction.log("sherlock",
                            "FaceManager.parseIconForString m start end " + m.start(), m.end());
                    spannable
                            .setSpan(span, Math.max(0, m.start()),
                                    Math.min(spannable.length(), m.end()),
                                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }

                m.groupCount();
            }

            return spannable;
        } catch (Exception e) {
            e.printStackTrace();
            return (new SpannableString(text.toString()));
        }
    }

    /**
     * 替换图标: 用于TextView、String等
     *
     * @param text
     * @param faceSizeDp 表情的大小(单位：dp)，默认值为24dp；不需要指定大小时，参数设置为0即可 表情与文字基线对齐显示
     */
    public SpannableString parseIconForStringBaseline(Context context, String text, int faceSizeDp,
                                                      FaceListener listener) {
        if (CommonFunction.isEmptyOrNullStr(text)) {
            return new SpannableString("");
        }

        String strSource = text;
        FaceManager faceManager = FaceManager.getInstance(context.getApplicationContext());
        Resources res = context.getResources();
        float faceSize = res.getDimension(R.dimen.face_height); // 默认值为24dp
        if (faceSizeDp > 0) {
            float oneDp = res.getDimension(R.dimen.dp_1);
            faceSize = faceSizeDp * oneDp;
        }

        Pattern emojiP = Pattern.compile(emojoRegex);// 匹配[# #]
        Matcher emojim = emojiP.matcher(strSource);// 开始编译

        while (emojim.find()) {
            String str = emojim.group();
            String icon = sHistoryFace.get(str);
            if (icon != null && strSource.contains(str)) {
                strSource = strSource.replace(str, icon);
            }

        }

        SpannableString spannable = null;

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(strSource);// 开始编译

        spannable = new SpannableString(strSource.toString());

        while (m.find()) {
            String str = m.group();
            String icon = "";
            if (str.startsWith(catFlag) && str.endsWith(catFlagright)) {
                icon = str;
            } else {

                for (int i = 0; i < str.length(); i++) {
                    char c = str.charAt(i);
                    int v = (int) c;
                    icon += Integer.toHexString(v);
                }
            }

            // 查找对应的图标
            Drawable drawable = faceManager.getDrawableWithFaceKey(context, icon, listener);
            if (drawable != null) {
                drawable.setBounds(0, 0, (int) faceSize, (int) faceSize);
                ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                spannable.setSpan(span, Math.max(0, m.start()),
                        Math.min(spannable.length(), m.end()), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            m.groupCount();
        }
        p = Pattern.compile(catRegex, Pattern.UNICODE_CASE);
        m = p.matcher(strSource);// 开始编译

        while (m.find()) {
            String str = m.group();
            // 查找对应的图标
            Drawable drawable = faceManager.getDrawableWithCatKey(context, str);

            if (drawable != null) {
                drawable.setBounds(0, 0, (int) faceSize, (int) faceSize);
                ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                spannable.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            m.groupCount();
        }

        return spannable;
    }

    /**
     * @author tanzy 根据key获取drawable，如果本地没有则在线获取 获取到drawable之后调用callback
     */
    @SuppressWarnings("ResourceType")
    public void getDrawableWithFaceKeyWithCallBack(Context context, String key,
                                                   final DrawableGetCallBack callback) {
        int id = 0; // 默认
        key = key.replace(leftflag, "").replace(rightflag, "");

        if (sLocalFace.containsKey(key)) {
            id = sLocalFace.get(key);
        } else if (sLocalAdd.containsKey(key)) {
            id = sLocalAdd.get(key);
        } else if (sLocalFace.containsKey(key)) {
            id = sLocalAdd.get(key);
        } else if (sSpecialFace.containsKey(key)) {
            id = sSpecialFace.get(key);
        } else if (sRemoteFace.containsKey(key)) {
            callback.onDrawableGet(sRemoteFace.get(key));
        } else if ("back".equals(key)) {
            callback
                    .onDrawableGet(context.getResources().getDrawable(R.raw.delete_face_unselect));
        }

        if (id > 0) {
            callback.onDrawableGet(context.getResources().getDrawable(id));
        } else {

            final String finalKey = key;
            GlideUtil.loadImage(BaseApplication.appContext, Config.sFaceHost + key + ".png", new GlideDrawableImageViewTarget(new ImageView(context)) {
                @Override
                public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                    super.onResourceReady(drawable, anim);
                    sRemoteFace.put(finalKey, drawable);
                }
            });
//			ImageViewUtil.getDefault( )
//				.loadImage( Config.sFaceHost + key + ".png", new ImageView( context ),
//					R.raw.em_25ab, R.raw.em_25ab, new ImageLoadingListener( )
//					{
//
//						@Override
//						public void onLoadingStarted( String arg0, View arg1 )
//						{
//
//						}
//
//						@Override
//						public void onLoadingFailed( String arg0, View arg1, FailReason arg2 )
//						{
//
//						}
//
//						@SuppressWarnings ( "deprecation" )
//						@Override
//						public void onLoadingComplete( String arg0, View arg1, Bitmap arg2 )
//						{
//							String k = arg0.substring( Config.sFaceHost.length( ),
//								arg0.lastIndexOf( ".png" ) );
//							sRemoteFace.put( k, new BitmapDrawable( arg2 ) );
//							callback.onDrawableGet( ( Drawable ) new BitmapDrawable( arg2 ) );
//						}
//
//						@Override
//						public void onLoadingCancelled( String arg0, View arg1 )
//						{
//
//						}
//					} );
        }
    }

    /**
     * @author tanzy
     */
    public interface DrawableGetCallBack {
        void onDrawableGet(Drawable drawable);
    }

    public void setAllFace(ArrayList<Face> face) {
        FaceManager.face = face;
    }

    public static ArrayList<Face> getAllFace() {
        return face;
    }

    static {
        CommonFunction.log("fan", "Init FaceManager sLocalFace============");

        sLocalFace.put("d83dde04", R.raw.em_d83dde04);
        sLocalFace.put("d83dde0a", R.raw.em_d83dde0a);
        sLocalFace.put("d83dde03", R.raw.em_d83dde03);
        sLocalFace.put("263a", R.raw.em_263a);
        sLocalFace.put("d83dde09", R.raw.em_d83dde09);
        sLocalFace.put("d83dde0d", R.raw.em_d83dde0d);
        sLocalFace.put("d83dde18", R.raw.em_d83dde18);
        sLocalFace.put("d83dde1a", R.raw.em_d83dde1a);
        sLocalFace.put("d83dde33", R.raw.em_d83dde33);
        sLocalFace.put("d83dde0c", R.raw.em_d83dde0c);
        sLocalFace.put("d83dde01", R.raw.em_d83dde01);
        sLocalFace.put("d83dde1c", R.raw.em_d83dde1c);
        sLocalFace.put("d83dde1d", R.raw.em_d83dde1d);
        sLocalFace.put("d83dde12", R.raw.em_d83dde12);
        sLocalFace.put("d83dde0f", R.raw.em_d83dde0f);
        sLocalFace.put("d83dde13", R.raw.em_d83dde13);
        sLocalFace.put("d83dde14", R.raw.em_d83dde14);
        sLocalFace.put("d83dde1e", R.raw.em_d83dde1e);
        sLocalFace.put("d83dde16", R.raw.em_d83dde16);
        sLocalFace.put("d83dde25", R.raw.em_d83dde25);
        sLocalFace.put("d83dde30", R.raw.em_d83dde30);
        sLocalFace.put("d83dde28", R.raw.em_d83dde28);
        sLocalFace.put("d83dde23", R.raw.em_d83dde23);
        sLocalFace.put("d83dde22", R.raw.em_d83dde22);
        sLocalFace.put("d83dde2d", R.raw.em_d83dde2d);
        sLocalFace.put("d83dde02", R.raw.em_d83dde02);
        sLocalFace.put("d83dde32", R.raw.em_d83dde32);
        sLocalFace.put("d83dde31", R.raw.em_d83dde31);
        sLocalFace.put("d83dde20", R.raw.em_d83dde20);
        sLocalFace.put("d83dde21", R.raw.em_d83dde21);
        sLocalFace.put("d83dde2a", R.raw.em_d83dde2a);
        sLocalFace.put("d83dde37", R.raw.em_d83dde37);
        sLocalFace.put("d83ddc7f", R.raw.em_d83ddc7f);
        sLocalFace.put("d83ddc7d", R.raw.em_d83ddc7d);
        sLocalFace.put("2764", R.raw.em_2764);
        sLocalFace.put("d83ddc94", R.raw.em_d83ddc94);
        sLocalFace.put("d83ddc98", R.raw.em_d83ddc98);
        sLocalFace.put("2728", R.raw.em_2728);
        sLocalFace.put("2b50", R.raw.em_2b50);
        sLocalFace.put("2757", R.raw.em_2757);
        sLocalFace.put("2753", R.raw.em_2753);
        sLocalFace.put("d83ddca4", R.raw.em_d83ddca4);
        sLocalFace.put("d83ddca6", R.raw.em_d83ddca6);
        sLocalFace.put("d83cdfb5", R.raw.em_d83cdfb5);
        sLocalFace.put("d83ddd25", R.raw.em_d83ddd25);
        sLocalFace.put("d83ddca9", R.raw.em_d83ddca9);
        sLocalFace.put("d83ddc4d", R.raw.em_d83ddc4d);
        sLocalFace.put("d83ddc4e", R.raw.em_d83ddc4e);
        sLocalFace.put("d83ddc4c", R.raw.em_d83ddc4c);
        sLocalFace.put("d83ddc4a", R.raw.em_d83ddc4a);
        sLocalFace.put("270c", R.raw.em_270c);
        sLocalFace.put("d83ddc46", R.raw.em_d83ddc46);
        sLocalFace.put("d83ddc47", R.raw.em_d83ddc47);
        sLocalFace.put("d83ddc49", R.raw.em_d83ddc49);
        sLocalFace.put("d83ddc48", R.raw.em_d83ddc48);
        sLocalFace.put("261d", R.raw.em_261d);
        sLocalFace.put("d83ddc8f", R.raw.em_d83ddc8f);
        sLocalFace.put("d83ddc91", R.raw.em_d83ddc91);
        sLocalFace.put("d83ddc66", R.raw.em_d83ddc66);
        sLocalFace.put("d83ddc67", R.raw.em_d83ddc67);
        sLocalFace.put("d83ddc69", R.raw.em_d83ddc69);
        sLocalFace.put("d83ddc68", R.raw.em_d83ddc68);
        sLocalFace.put("d83ddc7c", R.raw.em_d83ddc7c);
        sLocalFace.put("d83ddc80", R.raw.em_d83ddc80);
        sLocalFace.put("d83ddc44", R.raw.em_d83ddc44);
        sLocalFace.put("2600", R.raw.em_2600);
        sLocalFace.put("2614", R.raw.em_2614);
        sLocalFace.put("2601", R.raw.em_2601);
        sLocalFace.put("26c4", R.raw.em_26c4);
        sLocalFace.put("d83cdf19", R.raw.em_d83cdf19);
        sLocalFace.put("26a1", R.raw.em_26a1);
        sLocalFace.put("d83cdf0a", R.raw.em_d83cdf0a);
        sLocalFace.put("d83ddc31", R.raw.em_d83ddc31);
        sLocalFace.put("d83ddc36", R.raw.em_d83ddc36);
        sLocalFace.put("d83ddc2d", R.raw.em_d83ddc2d);
        sLocalFace.put("d83ddc39", R.raw.em_d83ddc39);
        sLocalFace.put("d83ddc30", R.raw.em_d83ddc30);
        sLocalFace.put("d83ddc3a", R.raw.em_d83ddc3a);
        sLocalFace.put("d83ddc38", R.raw.em_d83ddc38);
        sLocalFace.put("d83ddc2f", R.raw.em_d83ddc2f);
        sLocalFace.put("d83ddc28", R.raw.em_d83ddc28);
        sLocalFace.put("d83ddc3b", R.raw.em_d83ddc3b);
        sLocalFace.put("d83ddc37", R.raw.em_d83ddc37);
        sLocalFace.put("d83ddc2e", R.raw.em_d83ddc2e);
        sLocalFace.put("d83ddc17", R.raw.em_d83ddc17);
        sLocalFace.put("d83ddc12", R.raw.em_d83ddc12);
        sLocalFace.put("d83ddc0e", R.raw.em_d83ddc0e);
        sLocalFace.put("d83ddc26", R.raw.em_d83ddc26);
        sLocalFace.put("d83ddc24", R.raw.em_d83ddc24);
        sLocalFace.put("d83ddc27", R.raw.em_d83ddc27);
        sLocalFace.put("d83ddc1b", R.raw.em_d83ddc1b);
        sLocalFace.put("d83ddc19", R.raw.em_d83ddc19);
        sLocalFace.put("d83ddc35", R.raw.em_d83ddc35);
        sLocalFace.put("d83ddc20", R.raw.em_d83ddc20);
        sLocalFace.put("d83ddc33", R.raw.em_d83ddc33);
        sLocalFace.put("d83ddc2c", R.raw.em_d83ddc2c);
        sLocalFace.put("d83ddc9d", R.raw.em_d83ddc9d);
        sLocalFace.put("d83cdf83", R.raw.em_d83cdf83);
        sLocalFace.put("d83ddc7b", R.raw.em_d83ddc7b);
        sLocalFace.put("d83cdf85", R.raw.em_d83cdf85);
        sLocalFace.put("d83cdf84", R.raw.em_d83cdf84);
        sLocalFace.put("d83cdf81", R.raw.em_d83cdf81);
        sLocalFace.put("d83ddd14", R.raw.em_d83ddd14);
        sLocalFace.put("d83cdf89", R.raw.em_d83cdf89);
        sLocalFace.put("d83cdf88", R.raw.em_d83cdf88);
        sLocalFace.put("d83ddcbf", R.raw.em_d83ddcbf);
        sLocalFace.put("d83ddcf7", R.raw.em_d83ddcf7);
        sLocalFace.put("d83cdfa5", R.raw.em_d83cdfa5);
        sLocalFace.put("d83ddcbb", R.raw.em_d83ddcbb);
        sLocalFace.put("d83ddcfa", R.raw.em_d83ddcfa);
        sLocalFace.put("260e", R.raw.em_260e);
        sLocalFace.put("d83ddd13", R.raw.em_d83ddd13);
        sLocalFace.put("d83ddd12", R.raw.em_d83ddd12);
        sLocalFace.put("d83ddd11", R.raw.em_d83ddd11);
        sLocalFace.put("d83ddca1", R.raw.em_d83ddca1);
        sLocalFace.put("d83ddcea", R.raw.em_d83ddcea);
        sLocalFace.put("d83ddec0", R.raw.em_d83ddec0);
        sLocalFace.put("d83ddcb0", R.raw.em_d83ddcb0);
        sLocalFace.put("d83ddca3", R.raw.em_d83ddca3);
        sLocalFace.put("d83ddd2b", R.raw.em_d83ddd2b);
        sLocalFace.put("d83ddc8a", R.raw.em_d83ddc8a);
        sLocalFace.put("d83cdfc8", R.raw.em_d83cdfc8);
        sLocalFace.put("d83cdfc0", R.raw.em_d83cdfc0);
        sLocalFace.put("26bd", R.raw.em_26bd);
        sLocalFace.put("26be", R.raw.em_26be);
        sLocalFace.put("d83cdfc6", R.raw.em_d83cdfc6);
        sLocalFace.put("d83ddc7e", R.raw.em_d83ddc7e);
        sLocalFace.put("d83cdfa4", R.raw.em_d83cdfa4);
        sLocalFace.put("d83cdfb8", R.raw.em_d83cdfb8);
        sLocalFace.put("d83ddc59", R.raw.em_d83ddc59);
        sLocalFace.put("d83ddc51", R.raw.em_d83ddc51);
        sLocalFace.put("d83cdf02", R.raw.em_d83cdf02);
        sLocalFace.put("d83ddc5c", R.raw.em_d83ddc5c);
        sLocalFace.put("d83ddc84", R.raw.em_d83ddc84);
        sLocalFace.put("d83ddc8d", R.raw.em_d83ddc8d);
        sLocalFace.put("d83ddc8e", R.raw.em_d83ddc8e);
        sLocalFace.put("2615", R.raw.em_2615);
        sLocalFace.put("d83cdf7a", R.raw.em_d83cdf7a);
        sLocalFace.put("d83cdf7b", R.raw.em_d83cdf7b);
        sLocalFace.put("d83cdf78", R.raw.em_d83cdf78);
        sLocalFace.put("d83cdf66", R.raw.em_d83cdf66);
        sLocalFace.put("d83cdf67", R.raw.em_d83cdf67);
        sLocalFace.put("d83cdf82", R.raw.em_d83cdf82);
        sLocalFace.put("d83cdf70", R.raw.em_d83cdf70);
        sLocalFace.put("d83cdf4e", R.raw.em_d83cdf4e);
        sLocalFace.put("2708", R.raw.em_2708);
        sLocalFace.put("d83dde80", R.raw.em_d83dde80);
        sLocalFace.put("d83ddeb2", R.raw.em_d83ddeb2);
        sLocalFace.put("d83dde99", R.raw.em_d83dde99);
        sLocalFace.put("d83dde97", R.raw.em_d83dde97);
        sLocalFace.put("d83dde95", R.raw.em_d83dde95);
        sLocalFace.put("d83dde8c", R.raw.em_d83dde8c);
        sLocalFace.put("d83ddeb9", R.raw.em_d83ddeb9);
        sLocalFace.put("d83ddeba", R.raw.em_d83ddeba);
        sLocalFace.put("2b55", R.raw.em_2b55);
        sLocalFace.put("274c", R.raw.em_274c);
        sLocalFace.put("a9", R.raw.em_00a9);
        sLocalFace.put("ae", R.raw.em_00ae);
        sLocalFace.put("2122", R.raw.em_2122);

        sLocalAdd.put("f1", R.raw.f1);
        sLocalAdd.put("f13", R.raw.f13);
        sLocalAdd.put("f14", R.raw.f14);
        sLocalAdd.put("f20", R.raw.f20);
        sLocalAdd.put("f21", R.raw.f21);
        sLocalAdd.put("f26", R.raw.f26);
        sLocalAdd.put("f29", R.raw.f29);
        sLocalAdd.put("f30", R.raw.f30);
        sLocalAdd.put("f33", R.raw.f33);
        sLocalAdd.put("f33", R.raw.f33);
        sLocalAdd.put("f35", R.raw.f35);
        sLocalAdd.put("f36", R.raw.f36);
        sLocalAdd.put("f41", R.raw.f41);
        sLocalAdd.put("f41", R.raw.f41);
        sLocalAdd.put("f45", R.raw.f45);
        sLocalAdd.put("f46", R.raw.f46);
        sLocalAdd.put("f47", R.raw.f47);
        sLocalAdd.put("f55", R.raw.f55);
        sLocalAdd.put("f56", R.raw.f56);
        sLocalAdd.put("f57", R.raw.f57);
        sLocalAdd.put("f58", R.raw.f58);
        sLocalAdd.put("f6", R.raw.f6);
        sLocalAdd.put("f60", R.raw.f60);
        sLocalAdd.put("f61", R.raw.f61);
        sLocalAdd.put("f62", R.raw.f62);
        sLocalAdd.put("f64", R.raw.f64);
        sLocalAdd.put("f68", R.raw.f68);
        sLocalAdd.put("f70", R.raw.f70);
        sLocalAdd.put("f71", R.raw.f71);
        sLocalAdd.put("f74", R.raw.f74);
        sLocalAdd.put("f76", R.raw.f76);
        sLocalAdd.put("f77", R.raw.f77);
        sLocalAdd.put("f79", R.raw.f79);
        sLocalAdd.put("f80", R.raw.f80);

        sCatFace.put("[憨笑]", R.raw.f1);
        sCatFace.put("[感冒了]", R.raw.f10);
        sCatFace.put("[囧]", R.raw.f11);
        sCatFace.put("[尷尬]", R.raw.f12);
        sCatFace.put("[流口水]", R.raw.f13);
        sCatFace.put("[嚇]", R.raw.f14);
        sCatFace.put("[仰慕]", R.raw.f15);
        sCatFace.put("[酷]", R.raw.f16);
        sCatFace.put("[惡魔]", R.raw.f17);
        sCatFace.put("[偷笑]", R.raw.f18);
        sCatFace.put("[太開心]", R.raw.f19);
        sCatFace.put("[微笑]", R.raw.f2);
        sCatFace.put("[鼓掌]", R.raw.f20);
        sCatFace.put("[擁抱]", R.raw.f21);
        sCatFace.put("[滾]", R.raw.f22);
        sCatFace.put("[害羞]", R.raw.f23);
        sCatFace.put("[砸死你]", R.raw.f24);
        sCatFace.put("[抓狂]", R.raw.f25);
        sCatFace.put("[狂]", R.raw.f26);
        sCatFace.put("[攤手]", R.raw.f27);
        sCatFace.put("[吃飯去]", R.raw.f28);
        sCatFace.put("[咒罵]", R.raw.f29);
        sCatFace.put("[嘻嘻]", R.raw.f3);
        sCatFace.put("[在路上]", R.raw.f30);
        sCatFace.put("[無語]", R.raw.f31);
        sCatFace.put("[冒火]", R.raw.f32);
        sCatFace.put("[好熱]", R.raw.f33);
        sCatFace.put("[好冷]", R.raw.f34);
        sCatFace.put("[發燒了]", R.raw.f35);
        sCatFace.put("[財迷]", R.raw.f36);
        sCatFace.put("[玫瑰]", R.raw.f37);
        sCatFace.put("[暈]", R.raw.f38);
        sCatFace.put("[狂汗]", R.raw.f39);
        sCatFace.put("[哈哈]", R.raw.f4);
        sCatFace.put("[疑問]", R.raw.f40);
        sCatFace.put("[驚訝]", R.raw.f41);
        sCatFace.put("[斜眼]", R.raw.f42);
        sCatFace.put("[餓了]", R.raw.f43);
        sCatFace.put("[嘆氣]", R.raw.f44);
        sCatFace.put("[吐舌頭]", R.raw.f45);
        sCatFace.put("[心碎]", R.raw.f46);
        sCatFace.put("[微怒]", R.raw.f47);
        sCatFace.put("[衰]", R.raw.f48);
        sCatFace.put("[加班中]", R.raw.f49);
        sCatFace.put("[大哭]", R.raw.f5);
        sCatFace.put("[可憐]", R.raw.f50);
        sCatFace.put("[豬頭]", R.raw.f51);
        sCatFace.put("[鄙視]", R.raw.f52);
        sCatFace.put("[挖鼻孔]", R.raw.f53);
        sCatFace.put("[淚奔]", R.raw.f54);
        sCatFace.put("[拜拜]", R.raw.f55);
        sCatFace.put("[調皮]", R.raw.f56);
        sCatFace.put("[扮鬼臉]", R.raw.f57);
        sCatFace.put("[激動]", R.raw.f58);
        sCatFace.put("[花心]", R.raw.f59);
        sCatFace.put("[坏笑]", R.raw.f6);
        sCatFace.put("[圍觀]", R.raw.f60);
        sCatFace.put("[傾心]", R.raw.f61);
        sCatFace.put("[生日]", R.raw.f62);
        sCatFace.put("[委屈]", R.raw.f63);
        sCatFace.put("[渴了]", R.raw.f64);
        sCatFace.put("[打招呼]", R.raw.f65);
        sCatFace.put("[打哈欠]", R.raw.f66);
        sCatFace.put("[晚安]", R.raw.f67);
        sCatFace.put("[早安]", R.raw.f68);
        sCatFace.put("[離開一下]", R.raw.f69);
        sCatFace.put("[奮鬥]", R.raw.f7);
        sCatFace.put("[剛睡醒]", R.raw.f70);
        sCatFace.put("[路過]", R.raw.f71);
        sCatFace.put("[NO]", R.raw.f72);
        sCatFace.put("[嘔吐]", R.raw.f73);
        sCatFace.put("[飛吻]", R.raw.f74);
        sCatFace.put("[石化了]", R.raw.f75);
        sCatFace.put("[潛水]", R.raw.f76);
        sCatFace.put("[加油]", R.raw.f77);
        sCatFace.put("[瞌睡]", R.raw.f78);
        sCatFace.put("[睡覺]", R.raw.f79);
        sCatFace.put("[閉嘴]", R.raw.f8);
        sCatFace.put("[頂]", R.raw.f80);
        sCatFace.put("[贊]", R.raw.f81);
        sCatFace.put("[豎中指]", R.raw.f82);
        sCatFace.put("[V]", R.raw.f83);
        sCatFace.put("[OK]", R.raw.f84);
        sCatFace.put("[愛你]", R.raw.f85);
        sCatFace.put("[得意]", R.raw.f9);

        sCatFace.put("[憨笑]", R.raw.f1);
        sCatFace.put("[感冒了]", R.raw.f10);
        sCatFace.put("[囧]", R.raw.f11);
        sCatFace.put("[尴尬]", R.raw.f12);
        sCatFace.put("[流口水]", R.raw.f13);
        sCatFace.put("[吓]", R.raw.f14);
        sCatFace.put("[仰慕]", R.raw.f15);
        sCatFace.put("[酷]", R.raw.f16);
        sCatFace.put("[恶魔]", R.raw.f17);
        sCatFace.put("[偷笑]", R.raw.f18);
        sCatFace.put("[太开心]", R.raw.f19);
        sCatFace.put("[微笑]", R.raw.f2);
        sCatFace.put("[鼓掌]", R.raw.f20);
        sCatFace.put("[拥抱]", R.raw.f21);
        sCatFace.put("[滚]", R.raw.f22);
        sCatFace.put("[害羞]", R.raw.f23);
        sCatFace.put("[砸死你]", R.raw.f24);
        sCatFace.put("[抓狂]", R.raw.f25);
        sCatFace.put("[狂]", R.raw.f26);
        sCatFace.put("[摊手]", R.raw.f27);
        sCatFace.put("[吃饭去]", R.raw.f28);
        sCatFace.put("[咒骂]", R.raw.f29);
        sCatFace.put("[嘻嘻]", R.raw.f3);
        sCatFace.put("[在路上]", R.raw.f30);
        sCatFace.put("[无语]", R.raw.f31);
        sCatFace.put("[冒火]", R.raw.f32);
        sCatFace.put("[好热]", R.raw.f33);
        sCatFace.put("[好冷]", R.raw.f34);
        sCatFace.put("[发烧了]", R.raw.f35);
        sCatFace.put("[财迷]", R.raw.f36);
        sCatFace.put("[玫瑰]", R.raw.f37);
        sCatFace.put("[晕]", R.raw.f38);
        sCatFace.put("[狂汗]", R.raw.f39);
        sCatFace.put("[哈哈]", R.raw.f4);
        sCatFace.put("[疑问]", R.raw.f40);
        sCatFace.put("[惊讶]", R.raw.f41);
        sCatFace.put("[斜眼]", R.raw.f42);
        sCatFace.put("[饿了]", R.raw.f43);
        sCatFace.put("[叹气]", R.raw.f44);
        sCatFace.put("[吐舌头]", R.raw.f45);
        sCatFace.put("[心碎]", R.raw.f46);
        sCatFace.put("[微怒]", R.raw.f47);
        sCatFace.put("[衰]", R.raw.f48);
        sCatFace.put("[加班中]", R.raw.f49);
        sCatFace.put("[大哭]", R.raw.f5);
        sCatFace.put("[可怜]", R.raw.f50);
        sCatFace.put("[猪头]", R.raw.f51);
        sCatFace.put("[鄙视]", R.raw.f52);
        sCatFace.put("[挖鼻孔]", R.raw.f53);
        sCatFace.put("[泪奔]", R.raw.f54);
        sCatFace.put("[拜拜]", R.raw.f55);
        sCatFace.put("[调皮]", R.raw.f56);
        sCatFace.put("[扮鬼脸]", R.raw.f57);
        sCatFace.put("[激动]", R.raw.f58);
        sCatFace.put("[花心]", R.raw.f59);
        sCatFace.put("[坏笑]", R.raw.f6);
        sCatFace.put("[围观]", R.raw.f60);
        sCatFace.put("[倾心]", R.raw.f61);
        sCatFace.put("[生日]", R.raw.f62);
        sCatFace.put("[委屈]", R.raw.f63);
        sCatFace.put("[渴了]", R.raw.f64);
        sCatFace.put("[打招呼]", R.raw.f65);
        sCatFace.put("[打哈欠]", R.raw.f66);
        sCatFace.put("[晚安]", R.raw.f67);
        sCatFace.put("[早安]", R.raw.f68);
        sCatFace.put("[离开一下]", R.raw.f69);
        sCatFace.put("[奋斗]", R.raw.f7);
        sCatFace.put("[刚睡醒]", R.raw.f70);
        sCatFace.put("[路过]", R.raw.f71);
        sCatFace.put("[不]", R.raw.f72);
        sCatFace.put("[呕吐]", R.raw.f73);
        sCatFace.put("[飞吻]", R.raw.f74);
        sCatFace.put("[石化了]", R.raw.f75);
        sCatFace.put("[潜水]", R.raw.f76);
        sCatFace.put("[加油]", R.raw.f77);
        sCatFace.put("[瞌睡]", R.raw.f78);
        sCatFace.put("[睡觉]", R.raw.f79);
        sCatFace.put("[闭嘴]", R.raw.f8);
        sCatFace.put("[顶]", R.raw.f80);
        sCatFace.put("[赞]", R.raw.f81);
        sCatFace.put("[竖中指]", R.raw.f82);
        sCatFace.put("[胜利]", R.raw.f83);
        sCatFace.put("[好]", R.raw.f84);
        sCatFace.put("[爱你]", R.raw.f85);
        sCatFace.put("[得意]", R.raw.f9);

        sCatFace.put("[simper]", R.raw.f1);
        sCatFace.put("[cold]", R.raw.f10);
        sCatFace.put("[confused]", R.raw.f11);
        sCatFace.put("[embarrassed]", R.raw.f12);
        sCatFace.put("[drool]", R.raw.f13);
        sCatFace.put("[scared]", R.raw.f14);
        sCatFace.put("[admiration]", R.raw.f15);
        sCatFace.put("[cool]", R.raw.f16);
        sCatFace.put("[devil]", R.raw.f17);
        sCatFace.put("[laughing]", R.raw.f18);
        sCatFace.put("[very happy]", R.raw.f19);
        sCatFace.put("[smile]", R.raw.f2);
        sCatFace.put("[applause]", R.raw.f20);
        sCatFace.put("[hug]", R.raw.f21);
        sCatFace.put("[get away]", R.raw.f22);
        sCatFace.put("[shy]", R.raw.f23);
        sCatFace.put("[batter to death]", R.raw.f24);
        sCatFace.put("[crazy]", R.raw.f25);
        sCatFace.put("[mad]", R.raw.f26);
        sCatFace.put("[stand hand]", R.raw.f27);
        sCatFace.put("[go to eat]", R.raw.f28);
        sCatFace.put("[curse]", R.raw.f29);
        sCatFace.put("[hee hee]", R.raw.f3);
        sCatFace.put("[on the road]", R.raw.f30);
        sCatFace.put("[speechless]", R.raw.f31);
        sCatFace.put("[furious]", R.raw.f32);
        sCatFace.put("[so hot]", R.raw.f33);
        sCatFace.put("[so cold]", R.raw.f34);
        sCatFace.put("[fever]", R.raw.f35);
        sCatFace.put("[miser]", R.raw.f36);
        sCatFace.put("[rose]", R.raw.f37);
        sCatFace.put("[halo]", R.raw.f38);
        sCatFace.put("[have nothing to say]", R.raw.f39);
        sCatFace.put("[ha ha]", R.raw.f4);
        sCatFace.put("[doubt]", R.raw.f40);
        sCatFace.put("[surprised]", R.raw.f41);
        sCatFace.put("[strabismus]", R.raw.f42);
        sCatFace.put("[hungry]", R.raw.f43);
        sCatFace.put("[sigh]", R.raw.f44);
        sCatFace.put("[tongue]", R.raw.f45);
        sCatFace.put("[heart]", R.raw.f46);
        sCatFace.put("[micro angry]", R.raw.f47);
        sCatFace.put("[bad]", R.raw.f48);
        sCatFace.put("[overtime]", R.raw.f49);
        sCatFace.put("[crying]", R.raw.f5);
        sCatFace.put("[poor]", R.raw.f50);
        sCatFace.put("[pig]", R.raw.f51);
        sCatFace.put("[contempt]", R.raw.f52);
        sCatFace.put("[pick your nose]", R.raw.f53);
        sCatFace.put("[tears]", R.raw.f54);
        sCatFace.put("[bye]", R.raw.f55);
        sCatFace.put("[naughty]", R.raw.f56);
        sCatFace.put("[grimace]", R.raw.f57);
        sCatFace.put("[excited]", R.raw.f58);
        sCatFace.put("[ahogany]", R.raw.f59);
        sCatFace.put("[grin]", R.raw.f6);
        sCatFace.put("[onlookers]", R.raw.f60);
        sCatFace.put("[sight]", R.raw.f61);
        sCatFace.put("[birthday]", R.raw.f62);
        sCatFace.put("[grievance]", R.raw.f63);
        sCatFace.put("[thirsty]", R.raw.f64);
        sCatFace.put("[say hi]", R.raw.f65);
        sCatFace.put("[yawn]", R.raw.f66);
        sCatFace.put("[good night]", R.raw.f67);
        sCatFace.put("[good morning]", R.raw.f68);
        sCatFace.put("[leave for a moment]", R.raw.f69);
        sCatFace.put("[fight]", R.raw.f7);
        sCatFace.put("[just woke up]", R.raw.f70);
        sCatFace.put("[passing]", R.raw.f71);
        sCatFace.put("[NO]", R.raw.f72);
        sCatFace.put("[vomiting]", R.raw.f73);
        sCatFace.put("[kiss]", R.raw.f74);
        sCatFace.put("[petrified]", R.raw.f75);
        sCatFace.put("[diving]", R.raw.f76);
        sCatFace.put("[fuel]", R.raw.f77);
        sCatFace.put("[sleepy]", R.raw.f78);
        sCatFace.put("[sleep]", R.raw.f79);
        sCatFace.put("[shut up]", R.raw.f8);
        sCatFace.put("[top]", R.raw.f80);
        sCatFace.put("[praise]", R.raw.f81);
        sCatFace.put("[shit]", R.raw.f82);
        sCatFace.put("[V]", R.raw.f83);
        sCatFace.put("[OK]", R.raw.f84);
        sCatFace.put("[love you]", R.raw.f85);
        sCatFace.put("[proud]", R.raw.f9);

        sSpecialFace.put("f2", R.raw.f2);
        sSpecialFace.put("f3", R.raw.f3);
        sSpecialFace.put("f9", R.raw.f9);
        sSpecialFace.put("f16", R.raw.f16);
        sSpecialFace.put("f5", R.raw.f5);
        sSpecialFace.put("f23", R.raw.f23);
        sSpecialFace.put("f38", R.raw.f38);
        sSpecialFace.put("f66", R.raw.f66);
        sSpecialFace.put("f50", R.raw.f50);
        sSpecialFace.put("f32", R.raw.f32);
        sSpecialFace.put("f18", R.raw.f18);
        sSpecialFace.put("f4", R.raw.f4);
        sSpecialFace.put("f42", R.raw.f42);
        sSpecialFace.put("f12", R.raw.f12);
        sSpecialFace.put("f39", R.raw.f39);
        sSpecialFace.put("f73", R.raw.f73);
        sSpecialFace.put("f53", R.raw.f53);
        sSpecialFace.put("f65", R.raw.f65);

        sSpecialFace.put("f19", R.raw.f19); //
        sSpecialFace.put("f15", R.raw.f15); //
        sSpecialFace.put("f31", R.raw.f31);
        sSpecialFace.put("f52", R.raw.f52);
        sSpecialFace.put("f27", R.raw.f27);
        sSpecialFace.put("f8", R.raw.f8);
        sSpecialFace.put("f10", R.raw.f10);
        sSpecialFace.put("f78", R.raw.f78);
        sSpecialFace.put("f67", R.raw.f67);
        sSpecialFace.put("f54", R.raw.f54);
        sSpecialFace.put("f48", R.raw.f48);
        sSpecialFace.put("f59", R.raw.f59);
        sSpecialFace.put("f51", R.raw.f51);
        sSpecialFace.put("f24", R.raw.f24);

        sSpecialFace.put("f40", R.raw.f40);
        sSpecialFace.put("f11", R.raw.f11);//
        sSpecialFace.put("f72", R.raw.f72);
        sSpecialFace.put("f7", R.raw.f7);
        sSpecialFace.put("f37", R.raw.f37);
        // sSpecialFace.put( "f56" , R.raw.f56 );
        // sSpecialFace.put( "f30" , R.raw.f30 );
        // sSpecialFace.put( "f29" , R.raw.f29 );
        sSpecialFace.put("f22", R.raw.f22);
        sSpecialFace.put("f25", R.raw.f25);
        sSpecialFace.put("f17", R.raw.f17);
        sSpecialFace.put("f63", R.raw.f63);
        sSpecialFace.put("f44", R.raw.f44);
        sSpecialFace.put("f49", R.raw.f49);
        sSpecialFace.put("f75", R.raw.f75);
        sSpecialFace.put("f28", R.raw.f28);
        sSpecialFace.put("f69", R.raw.f69);
        sSpecialFace.put("f81", R.raw.f81);
        sSpecialFace.put("f82", R.raw.f82);
        sSpecialFace.put("f83", R.raw.f83);
        sSpecialFace.put("f84", R.raw.f84);
        sSpecialFace.put("f85", R.raw.f85);

    }

    static {
        sLocalAdd.put("a9", R.raw.em_00a9);
        sLocalAdd.put("203c", R.raw.em_203c);
        sLocalAdd.put("2049", R.raw.em_2049);
        sLocalAdd.put("2139", R.raw.em_2139);
        sLocalAdd.put("2194", R.raw.em_2194);
        sLocalAdd.put("2195", R.raw.em_2195);
        sLocalAdd.put("2196", R.raw.em_2196);
        sLocalAdd.put("2197", R.raw.em_2197);
        sLocalAdd.put("2198", R.raw.em_2198);
        sLocalAdd.put("2199", R.raw.em_2199);
        sLocalAdd.put("21a9", R.raw.em_21a9);
        sLocalAdd.put("21aa", R.raw.em_21aa);
        sLocalAdd.put("231a", R.raw.em_231a);
        sLocalAdd.put("231b", R.raw.em_231b);
        sLocalAdd.put("2320e3", R.raw.em_2320e3);
        sLocalAdd.put("23e9", R.raw.em_23e9);
        sLocalAdd.put("23ea", R.raw.em_23ea);
        sLocalAdd.put("23eb", R.raw.em_23eb);
        sLocalAdd.put("23ec", R.raw.em_23ec);
        sLocalAdd.put("23f0", R.raw.em_23f0);
        sLocalAdd.put("23f3", R.raw.em_23f3);
        sLocalAdd.put("24c2", R.raw.em_24c2);
        sLocalAdd.put("25aa", R.raw.em_25aa);
        sLocalAdd.put("25ab", R.raw.em_25ab);
        sLocalAdd.put("25b6", R.raw.em_25b6);
        sLocalAdd.put("25c0", R.raw.em_25c0);
        sLocalAdd.put("25fb", R.raw.em_25fb);
        sLocalAdd.put("25fc", R.raw.em_25fc);
        sLocalAdd.put("25fd", R.raw.em_25fd);
        sLocalAdd.put("25fe", R.raw.em_25fe);
        sLocalAdd.put("2611", R.raw.em_2611);
        sLocalAdd.put("2648", R.raw.em_2648);
        sLocalAdd.put("2649", R.raw.em_2649);
        sLocalAdd.put("264a", R.raw.em_264a);
        sLocalAdd.put("264b", R.raw.em_264b);
        sLocalAdd.put("264c", R.raw.em_264c);
        sLocalAdd.put("264d", R.raw.em_264d);
        sLocalAdd.put("264e", R.raw.em_264e);
        sLocalAdd.put("264f", R.raw.em_264f);
        sLocalAdd.put("2650", R.raw.em_2650);
        sLocalAdd.put("2651", R.raw.em_2651);
        sLocalAdd.put("2652", R.raw.em_2652);
        sLocalAdd.put("2653", R.raw.em_2653);
        sLocalAdd.put("2660", R.raw.em_2660);
        sLocalAdd.put("2663", R.raw.em_2663);
        sLocalAdd.put("2665", R.raw.em_2665);
        sLocalAdd.put("2666", R.raw.em_2666);
        sLocalAdd.put("2668", R.raw.em_2668);
        sLocalAdd.put("267b", R.raw.em_267b);
        sLocalAdd.put("267f", R.raw.em_267f);
        sLocalAdd.put("2693", R.raw.em_2693);
        sLocalAdd.put("26a0", R.raw.em_26a0);
        sLocalAdd.put("26aa", R.raw.em_26aa);
        sLocalAdd.put("26ab", R.raw.em_26ab);
        sLocalAdd.put("26c5", R.raw.em_26c5);
        sLocalAdd.put("26ce", R.raw.em_26ce);
        sLocalAdd.put("26d4", R.raw.em_26d4);
        sLocalAdd.put("26ea", R.raw.em_26ea);
        sLocalAdd.put("26f2", R.raw.em_26f2);
        sLocalAdd.put("26f3", R.raw.em_26f3);
        sLocalAdd.put("26f5", R.raw.em_26f5);
        sLocalAdd.put("26fa", R.raw.em_26fa);
        sLocalAdd.put("26fd", R.raw.em_26fd);
        sLocalAdd.put("2702", R.raw.em_2702);
        sLocalAdd.put("2705", R.raw.em_2705);
        sLocalAdd.put("2709", R.raw.em_2709);
        sLocalAdd.put("270a", R.raw.em_270a);
        sLocalAdd.put("270b", R.raw.em_270b);
        sLocalAdd.put("270f", R.raw.em_270f);
        sLocalAdd.put("2712", R.raw.em_2712);
        sLocalAdd.put("2714", R.raw.em_2714);
        sLocalAdd.put("2716", R.raw.em_2716);
        sLocalAdd.put("2733", R.raw.em_2733);
        sLocalAdd.put("2734", R.raw.em_2734);
        sLocalAdd.put("2744", R.raw.em_2744);
        sLocalAdd.put("2747", R.raw.em_2747);
        sLocalAdd.put("274e", R.raw.em_274e);
        sLocalAdd.put("2754", R.raw.em_2754);
        sLocalAdd.put("2755", R.raw.em_2755);
        sLocalAdd.put("2795", R.raw.em_2795);
        sLocalAdd.put("2796", R.raw.em_2796);
        sLocalAdd.put("2797", R.raw.em_2797);
        sLocalAdd.put("27a1", R.raw.em_27a1);
        sLocalAdd.put("27b0", R.raw.em_27b0);
        sLocalAdd.put("27bf", R.raw.em_27bf);
        sLocalAdd.put("2934", R.raw.em_2934);
        sLocalAdd.put("2935", R.raw.em_2935);
        sLocalAdd.put("2b05", R.raw.em_2b05);
        sLocalAdd.put("2b06", R.raw.em_2b06);
        sLocalAdd.put("2b07", R.raw.em_2b07);
        sLocalAdd.put("2b1b", R.raw.em_2b1b);
        sLocalAdd.put("2b1c", R.raw.em_2b1c);
        sLocalAdd.put("3020e3", R.raw.em_3020e3);
        sLocalAdd.put("3030", R.raw.em_3030);
        sLocalAdd.put("303d", R.raw.em_303d);
        sLocalAdd.put("3120e3", R.raw.em_3120e3);
        sLocalAdd.put("3220e3", R.raw.em_3220e3);
        sLocalAdd.put("3297", R.raw.em_3297);
        sLocalAdd.put("3299", R.raw.em_3299);
        sLocalAdd.put("3320e3", R.raw.em_3320e3);
        sLocalAdd.put("3420e3", R.raw.em_3420e3);
        sLocalAdd.put("3520e3", R.raw.em_3520e3);
        sLocalAdd.put("3620e3", R.raw.em_3620e3);
        sLocalAdd.put("3720e3", R.raw.em_3720e3);
        sLocalAdd.put("3820e3", R.raw.em_3820e3);
        sLocalAdd.put("3920e3", R.raw.em_3920e3);
        sLocalAdd.put("d83cdc04", R.raw.em_d83cdc04);
        sLocalAdd.put("d83cdccf", R.raw.em_d83cdccf);
        sLocalAdd.put("d83cdd70", R.raw.em_d83cdd70);
        sLocalAdd.put("d83cdd71", R.raw.em_d83cdd71);
        sLocalAdd.put("d83cdd7e", R.raw.em_d83cdd7e);
        sLocalAdd.put("d83cdd7f", R.raw.em_d83cdd7f);
        sLocalAdd.put("d83cdd8e", R.raw.em_d83cdd8e);
        sLocalAdd.put("d83cdd91", R.raw.em_d83cdd91);
        sLocalAdd.put("d83cdd92", R.raw.em_d83cdd92);
        sLocalAdd.put("d83cdd93", R.raw.em_d83cdd93);
        sLocalAdd.put("d83cdd94", R.raw.em_d83cdd94);
        sLocalAdd.put("d83cdd95", R.raw.em_d83cdd95);
        sLocalAdd.put("d83cdd96", R.raw.em_d83cdd96);
        sLocalAdd.put("d83cdd97", R.raw.em_d83cdd97);
        sLocalAdd.put("d83cdd98", R.raw.em_d83cdd98);
        sLocalAdd.put("d83cdd99", R.raw.em_d83cdd99);
        sLocalAdd.put("d83cdd9a", R.raw.em_d83cdd9a);
        sLocalAdd.put("d83cdde8d83cddf3", R.raw.em_d83cdde8d83cddf3);
        sLocalAdd.put("d83cdde9d83cddea", R.raw.em_d83cdde9d83cddea);
        sLocalAdd.put("d83cddead83cddf8", R.raw.em_d83cddead83cddf8);
        sLocalAdd.put("d83cddebd83cddf7", R.raw.em_d83cddebd83cddf7);
        sLocalAdd.put("d83cddecd83cdde7", R.raw.em_d83cddecd83cdde7);
        sLocalAdd.put("d83cddeed83cddf9", R.raw.em_d83cddeed83cddf9);
        sLocalAdd.put("d83cddefd83cddf5", R.raw.em_d83cddefd83cddf5);
        sLocalAdd.put("d83cddf0d83cddf7", R.raw.em_d83cddf0d83cddf7);
        sLocalAdd.put("d83cddf7d83cddfa", R.raw.em_d83cddf7d83cddfa);
        sLocalAdd.put("d83cddfad83cddf8", R.raw.em_d83cddfad83cddf8);
        sLocalAdd.put("d83cde01", R.raw.em_d83cde01);
        sLocalAdd.put("d83cde02", R.raw.em_d83cde02);
        sLocalAdd.put("d83cde1a", R.raw.em_d83cde1a);
        sLocalAdd.put("d83cde2f", R.raw.em_d83cde2f);
        sLocalAdd.put("d83cde32", R.raw.em_d83cde32);
        sLocalAdd.put("d83cde33", R.raw.em_d83cde33);
        sLocalAdd.put("d83cde34", R.raw.em_d83cde34);
        sLocalAdd.put("d83cde35", R.raw.em_d83cde35);
        sLocalAdd.put("d83cde36", R.raw.em_d83cde36);
        sLocalAdd.put("d83cde37", R.raw.em_d83cde37);
        sLocalAdd.put("d83cde38", R.raw.em_d83cde38);
        sLocalAdd.put("d83cde39", R.raw.em_d83cde39);
        sLocalAdd.put("d83cde3a", R.raw.em_d83cde3a);
        sLocalAdd.put("d83cde50", R.raw.em_d83cde50);
        sLocalAdd.put("d83cde51", R.raw.em_d83cde51);
        sLocalAdd.put("d83cdf00", R.raw.em_d83cdf00);
        sLocalAdd.put("d83cdf01", R.raw.em_d83cdf01);
        sLocalAdd.put("d83cdf03", R.raw.em_d83cdf03);
        sLocalAdd.put("d83cdf04", R.raw.em_d83cdf04);
        sLocalAdd.put("d83cdf05", R.raw.em_d83cdf05);
        sLocalAdd.put("d83cdf06", R.raw.em_d83cdf06);
        sLocalAdd.put("d83cdf07", R.raw.em_d83cdf07);
        sLocalAdd.put("d83cdf08", R.raw.em_d83cdf08);
        sLocalAdd.put("d83cdf09", R.raw.em_d83cdf09);
        sLocalAdd.put("d83cdf0b", R.raw.em_d83cdf0b);
        sLocalAdd.put("d83cdf0c", R.raw.em_d83cdf0c);
        sLocalAdd.put("d83cdf0d", R.raw.em_d83cdf0d);
        sLocalAdd.put("d83cdf0e", R.raw.em_d83cdf0e);
        sLocalAdd.put("d83cdf0f", R.raw.em_d83cdf0f);
        sLocalAdd.put("d83cdf10", R.raw.em_d83cdf10);
        sLocalAdd.put("d83cdf11", R.raw.em_d83cdf11);
        sLocalAdd.put("d83cdf12", R.raw.em_d83cdf12);
        sLocalAdd.put("d83cdf13", R.raw.em_d83cdf13);
        sLocalAdd.put("d83cdf14", R.raw.em_d83cdf14);
        sLocalAdd.put("d83cdf15", R.raw.em_d83cdf15);
        sLocalAdd.put("d83cdf16", R.raw.em_d83cdf16);
        sLocalAdd.put("d83cdf17", R.raw.em_d83cdf17);
        sLocalAdd.put("d83cdf18", R.raw.em_d83cdf18);
        sLocalAdd.put("d83cdf1a", R.raw.em_d83cdf1a);
        sLocalAdd.put("d83cdf1b", R.raw.em_d83cdf1b);
        sLocalAdd.put("d83cdf1c", R.raw.em_d83cdf1c);
        sLocalAdd.put("d83cdf1d", R.raw.em_d83cdf1d);
        sLocalAdd.put("d83cdf1e", R.raw.em_d83cdf1e);
        sLocalAdd.put("d83cdf1f", R.raw.em_d83cdf1f);
        sLocalAdd.put("d83cdf20", R.raw.em_d83cdf20);
        sLocalAdd.put("d83cdf30", R.raw.em_d83cdf30);
        sLocalAdd.put("d83cdf31", R.raw.em_d83cdf31);
        sLocalAdd.put("d83cdf32", R.raw.em_d83cdf32);
        sLocalAdd.put("d83cdf33", R.raw.em_d83cdf33);
        sLocalAdd.put("d83cdf34", R.raw.em_d83cdf34);
        sLocalAdd.put("d83cdf35", R.raw.em_d83cdf35);
        sLocalAdd.put("d83cdf37", R.raw.em_d83cdf37);
        sLocalAdd.put("d83cdf38", R.raw.em_d83cdf38);
        sLocalAdd.put("d83cdf39", R.raw.em_d83cdf39);
        sLocalAdd.put("d83cdf3a", R.raw.em_d83cdf3a);
        sLocalAdd.put("d83cdf3b", R.raw.em_d83cdf3b);
        sLocalAdd.put("d83cdf3c", R.raw.em_d83cdf3c);
        sLocalAdd.put("d83cdf3d", R.raw.em_d83cdf3d);
        sLocalAdd.put("d83cdf3e", R.raw.em_d83cdf3e);
        sLocalAdd.put("d83cdf3f", R.raw.em_d83cdf3f);
        sLocalAdd.put("d83cdf40", R.raw.em_d83cdf40);
        sLocalAdd.put("d83cdf41", R.raw.em_d83cdf41);
        sLocalAdd.put("d83cdf42", R.raw.em_d83cdf42);
        sLocalAdd.put("d83cdf43", R.raw.em_d83cdf43);
        sLocalAdd.put("d83cdf44", R.raw.em_d83cdf44);
        sLocalAdd.put("d83cdf45", R.raw.em_d83cdf45);
        sLocalAdd.put("d83cdf46", R.raw.em_d83cdf46);
        sLocalAdd.put("d83cdf47", R.raw.em_d83cdf47);
        sLocalAdd.put("d83cdf48", R.raw.em_d83cdf48);
        sLocalAdd.put("d83cdf49", R.raw.em_d83cdf49);
        sLocalAdd.put("d83cdf4a", R.raw.em_d83cdf4a);
        sLocalAdd.put("d83cdf4b", R.raw.em_d83cdf4b);
        sLocalAdd.put("d83cdf4c", R.raw.em_d83cdf4c);
        sLocalAdd.put("d83cdf4d", R.raw.em_d83cdf4d);
        sLocalAdd.put("d83cdf4f", R.raw.em_d83cdf4f);
        sLocalAdd.put("d83cdf50", R.raw.em_d83cdf50);
        sLocalAdd.put("d83cdf51", R.raw.em_d83cdf51);
        sLocalAdd.put("d83cdf52", R.raw.em_d83cdf52);
        sLocalAdd.put("d83cdf53", R.raw.em_d83cdf53);
        sLocalAdd.put("d83cdf54", R.raw.em_d83cdf54);
        sLocalAdd.put("d83cdf55", R.raw.em_d83cdf55);
        sLocalAdd.put("d83cdf56", R.raw.em_d83cdf56);
        sLocalAdd.put("d83cdf57", R.raw.em_d83cdf57);
        sLocalAdd.put("d83cdf58", R.raw.em_d83cdf58);
        sLocalAdd.put("d83cdf59", R.raw.em_d83cdf59);
        sLocalAdd.put("d83cdf5a", R.raw.em_d83cdf5a);
        sLocalAdd.put("d83cdf5b", R.raw.em_d83cdf5b);
        sLocalAdd.put("d83cdf5c", R.raw.em_d83cdf5c);
        sLocalAdd.put("d83cdf5d", R.raw.em_d83cdf5d);
        sLocalAdd.put("d83cdf5e", R.raw.em_d83cdf5e);
        sLocalAdd.put("d83cdf5f", R.raw.em_d83cdf5f);
        sLocalAdd.put("d83cdf60", R.raw.em_d83cdf60);
        sLocalAdd.put("d83cdf61", R.raw.em_d83cdf61);
        sLocalAdd.put("d83cdf62", R.raw.em_d83cdf62);
        sLocalAdd.put("d83cdf63", R.raw.em_d83cdf63);
        sLocalAdd.put("d83cdf64", R.raw.em_d83cdf64);
        sLocalAdd.put("d83cdf65", R.raw.em_d83cdf65);
        sLocalAdd.put("d83cdf68", R.raw.em_d83cdf68);
        sLocalAdd.put("d83cdf69", R.raw.em_d83cdf69);
        sLocalAdd.put("d83cdf6a", R.raw.em_d83cdf6a);
        sLocalAdd.put("d83cdf6b", R.raw.em_d83cdf6b);
        sLocalAdd.put("d83cdf6c", R.raw.em_d83cdf6c);
        sLocalAdd.put("d83cdf6d", R.raw.em_d83cdf6d);
        sLocalAdd.put("d83cdf6e", R.raw.em_d83cdf6e);
        sLocalAdd.put("d83cdf6f", R.raw.em_d83cdf6f);
        sLocalAdd.put("d83cdf71", R.raw.em_d83cdf71);
        sLocalAdd.put("d83cdf72", R.raw.em_d83cdf72);
        sLocalAdd.put("d83cdf73", R.raw.em_d83cdf73);
        sLocalAdd.put("d83cdf74", R.raw.em_d83cdf74);
        sLocalAdd.put("d83cdf75", R.raw.em_d83cdf75);
        sLocalAdd.put("d83cdf76", R.raw.em_d83cdf76);
        sLocalAdd.put("d83cdf77", R.raw.em_d83cdf77);
        sLocalAdd.put("d83cdf79", R.raw.em_d83cdf79);
        sLocalAdd.put("d83cdf7c", R.raw.em_d83cdf7c);
        sLocalAdd.put("d83cdf80", R.raw.em_d83cdf80);
        sLocalAdd.put("d83cdf86", R.raw.em_d83cdf86);
        sLocalAdd.put("d83cdf87", R.raw.em_d83cdf87);
        sLocalAdd.put("d83cdf8a", R.raw.em_d83cdf8a);
        sLocalAdd.put("d83cdf8b", R.raw.em_d83cdf8b);
        sLocalAdd.put("d83cdf8c", R.raw.em_d83cdf8c);
        sLocalAdd.put("d83cdf8d", R.raw.em_d83cdf8d);
        sLocalAdd.put("d83cdf8e", R.raw.em_d83cdf8e);
        sLocalAdd.put("d83cdf8f", R.raw.em_d83cdf8f);
        sLocalAdd.put("d83cdf90", R.raw.em_d83cdf90);
        sLocalAdd.put("d83cdf91", R.raw.em_d83cdf91);
        sLocalAdd.put("d83cdf92", R.raw.em_d83cdf92);
        sLocalAdd.put("d83cdf93", R.raw.em_d83cdf93);
        sLocalAdd.put("d83cdfa0", R.raw.em_d83cdfa0);
        sLocalAdd.put("d83cdfa1", R.raw.em_d83cdfa1);
        sLocalAdd.put("d83cdfa2", R.raw.em_d83cdfa2);
        sLocalAdd.put("d83cdfa3", R.raw.em_d83cdfa3);
        sLocalAdd.put("d83cdfa6", R.raw.em_d83cdfa6);
        sLocalAdd.put("d83cdfa7", R.raw.em_d83cdfa7);
        sLocalAdd.put("d83cdfa8", R.raw.em_d83cdfa8);
        sLocalAdd.put("d83cdfa9", R.raw.em_d83cdfa9);
        sLocalAdd.put("d83cdfaa", R.raw.em_d83cdfaa);
        sLocalAdd.put("d83cdfab", R.raw.em_d83cdfab);
        sLocalAdd.put("d83cdfac", R.raw.em_d83cdfac);
        sLocalAdd.put("d83cdfad", R.raw.em_d83cdfad);
        sLocalAdd.put("d83cdfae", R.raw.em_d83cdfae);
        sLocalAdd.put("d83cdfaf", R.raw.em_d83cdfaf);
        sLocalAdd.put("d83cdfb0", R.raw.em_d83cdfb0);
        sLocalAdd.put("d83cdfb1", R.raw.em_d83cdfb1);
        sLocalAdd.put("d83cdfb2", R.raw.em_d83cdfb2);
        sLocalAdd.put("d83cdfb3", R.raw.em_d83cdfb3);
        sLocalAdd.put("d83cdfb4", R.raw.em_d83cdfb4);
        sLocalAdd.put("d83cdfb6", R.raw.em_d83cdfb6);
        sLocalAdd.put("d83cdfb7", R.raw.em_d83cdfb7);
        sLocalAdd.put("d83cdfb9", R.raw.em_d83cdfb9);
        sLocalAdd.put("d83cdfba", R.raw.em_d83cdfba);
        sLocalAdd.put("d83cdfbb", R.raw.em_d83cdfbb);
        sLocalAdd.put("d83cdfbc", R.raw.em_d83cdfbc);
        sLocalAdd.put("d83cdfbd", R.raw.em_d83cdfbd);
        sLocalAdd.put("d83cdfbe", R.raw.em_d83cdfbe);
        sLocalAdd.put("d83cdfbf", R.raw.em_d83cdfbf);
        sLocalAdd.put("d83cdfc1", R.raw.em_d83cdfc1);
        sLocalAdd.put("d83cdfc2", R.raw.em_d83cdfc2);
        sLocalAdd.put("d83cdfc3", R.raw.em_d83cdfc3);
        sLocalAdd.put("d83cdfc4", R.raw.em_d83cdfc4);
        sLocalAdd.put("d83cdfc7", R.raw.em_d83cdfc7);
        sLocalAdd.put("d83cdfc9", R.raw.em_d83cdfc9);
        sLocalAdd.put("d83cdfca", R.raw.em_d83cdfca);
        sLocalAdd.put("d83cdfe0", R.raw.em_d83cdfe0);
        sLocalAdd.put("d83cdfe1", R.raw.em_d83cdfe1);
        sLocalAdd.put("d83cdfe2", R.raw.em_d83cdfe2);
        sLocalAdd.put("d83cdfe3", R.raw.em_d83cdfe3);
        sLocalAdd.put("d83cdfe4", R.raw.em_d83cdfe4);
        sLocalAdd.put("d83cdfe5", R.raw.em_d83cdfe5);
        sLocalAdd.put("d83cdfe6", R.raw.em_d83cdfe6);
        sLocalAdd.put("d83cdfe7", R.raw.em_d83cdfe7);
        sLocalAdd.put("d83cdfe8", R.raw.em_d83cdfe8);
        sLocalAdd.put("d83cdfe9", R.raw.em_d83cdfe9);
        sLocalAdd.put("d83cdfea", R.raw.em_d83cdfea);
        sLocalAdd.put("d83cdfeb", R.raw.em_d83cdfeb);
        sLocalAdd.put("d83cdfec", R.raw.em_d83cdfec);
        sLocalAdd.put("d83cdfed", R.raw.em_d83cdfed);
        sLocalAdd.put("d83cdfee", R.raw.em_d83cdfee);
        sLocalAdd.put("d83cdfef", R.raw.em_d83cdfef);
        sLocalAdd.put("d83cdff0", R.raw.em_d83cdff0);
        sLocalAdd.put("d83ddc00", R.raw.em_d83ddc00);
        sLocalAdd.put("d83ddc01", R.raw.em_d83ddc01);
        sLocalAdd.put("d83ddc02", R.raw.em_d83ddc02);
        sLocalAdd.put("d83ddc03", R.raw.em_d83ddc03);
        sLocalAdd.put("d83ddc04", R.raw.em_d83ddc04);
        sLocalAdd.put("d83ddc05", R.raw.em_d83ddc05);
        sLocalAdd.put("d83ddc06", R.raw.em_d83ddc06);
        sLocalAdd.put("d83ddc07", R.raw.em_d83ddc07);
        sLocalAdd.put("d83ddc08", R.raw.em_d83ddc08);
        sLocalAdd.put("d83ddc09", R.raw.em_d83ddc09);
        sLocalAdd.put("d83ddc0a", R.raw.em_d83ddc0a);
        sLocalAdd.put("d83ddc0b", R.raw.em_d83ddc0b);
        sLocalAdd.put("d83ddc0c", R.raw.em_d83ddc0c);
        sLocalAdd.put("d83ddc0d", R.raw.em_d83ddc0d);
        sLocalAdd.put("d83ddc0f", R.raw.em_d83ddc0f);
        sLocalAdd.put("d83ddc10", R.raw.em_d83ddc10);
        sLocalAdd.put("d83ddc11", R.raw.em_d83ddc11);
        sLocalAdd.put("d83ddc13", R.raw.em_d83ddc13);
        sLocalAdd.put("d83ddc14", R.raw.em_d83ddc14);
        sLocalAdd.put("d83ddc15", R.raw.em_d83ddc15);
        sLocalAdd.put("d83ddc16", R.raw.em_d83ddc16);
        sLocalAdd.put("d83ddc18", R.raw.em_d83ddc18);
        sLocalAdd.put("d83ddc1a", R.raw.em_d83ddc1a);
        sLocalAdd.put("d83ddc1c", R.raw.em_d83ddc1c);
        sLocalAdd.put("d83ddc1d", R.raw.em_d83ddc1d);
        sLocalAdd.put("d83ddc1e", R.raw.em_d83ddc1e);
        sLocalAdd.put("d83ddc1f", R.raw.em_d83ddc1f);
        sLocalAdd.put("d83ddc21", R.raw.em_d83ddc21);
        sLocalAdd.put("d83ddc22", R.raw.em_d83ddc22);
        sLocalAdd.put("d83ddc23", R.raw.em_d83ddc23);
        sLocalAdd.put("d83ddc25", R.raw.em_d83ddc25);
        sLocalAdd.put("d83ddc29", R.raw.em_d83ddc29);
        sLocalAdd.put("d83ddc2a", R.raw.em_d83ddc2a);
        sLocalAdd.put("d83ddc2b", R.raw.em_d83ddc2b);
        sLocalAdd.put("d83ddc32", R.raw.em_d83ddc32);
        sLocalAdd.put("d83ddc34", R.raw.em_d83ddc34);
        sLocalAdd.put("d83ddc3c", R.raw.em_d83ddc3c);
        sLocalAdd.put("d83ddc3d", R.raw.em_d83ddc3d);
        sLocalAdd.put("d83ddc3e", R.raw.em_d83ddc3e);
        sLocalAdd.put("d83ddc40", R.raw.em_d83ddc40);
        sLocalAdd.put("d83ddc42", R.raw.em_d83ddc42);
        sLocalAdd.put("d83ddc43", R.raw.em_d83ddc43);
        sLocalAdd.put("d83ddc45", R.raw.em_d83ddc45);
        sLocalAdd.put("d83ddc4b", R.raw.em_d83ddc4b);
        sLocalAdd.put("d83ddc4f", R.raw.em_d83ddc4f);
        sLocalAdd.put("d83ddc50", R.raw.em_d83ddc50);
        sLocalAdd.put("d83ddc52", R.raw.em_d83ddc52);
        sLocalAdd.put("d83ddc53", R.raw.em_d83ddc53);
        sLocalAdd.put("d83ddc54", R.raw.em_d83ddc54);
        sLocalAdd.put("d83ddc55", R.raw.em_d83ddc55);
        sLocalAdd.put("d83ddc56", R.raw.em_d83ddc56);
        sLocalAdd.put("d83ddc57", R.raw.em_d83ddc57);
        sLocalAdd.put("d83ddc58", R.raw.em_d83ddc58);
        sLocalAdd.put("d83ddc5a", R.raw.em_d83ddc5a);
        sLocalAdd.put("d83ddc5b", R.raw.em_d83ddc5b);
        sLocalAdd.put("d83ddc5d", R.raw.em_d83ddc5d);
        sLocalAdd.put("d83ddc5e", R.raw.em_d83ddc5e);
        sLocalAdd.put("d83ddc5f", R.raw.em_d83ddc5f);
        sLocalAdd.put("d83ddc60", R.raw.em_d83ddc60);
        sLocalAdd.put("d83ddc61", R.raw.em_d83ddc61);
        sLocalAdd.put("d83ddc62", R.raw.em_d83ddc62);
        sLocalAdd.put("d83ddc63", R.raw.em_d83ddc63);
        sLocalAdd.put("d83ddc64", R.raw.em_d83ddc64);
        sLocalAdd.put("d83ddc65", R.raw.em_d83ddc65);
        sLocalAdd.put("d83ddc6a", R.raw.em_d83ddc6a);
        sLocalAdd.put("d83ddc6b", R.raw.em_d83ddc6b);
        sLocalAdd.put("d83ddc6c", R.raw.em_d83ddc6c);
        sLocalAdd.put("d83ddc6d", R.raw.em_d83ddc6d);
        sLocalAdd.put("d83ddc6e", R.raw.em_d83ddc6e);
        sLocalAdd.put("d83ddc6f", R.raw.em_d83ddc6f);
        sLocalAdd.put("d83ddc70", R.raw.em_d83ddc70);
        sLocalAdd.put("d83ddc71", R.raw.em_d83ddc71);
        sLocalAdd.put("d83ddc72", R.raw.em_d83ddc72);
        sLocalAdd.put("d83ddc73", R.raw.em_d83ddc73);
        sLocalAdd.put("d83ddc74", R.raw.em_d83ddc74);
        sLocalAdd.put("d83ddc75", R.raw.em_d83ddc75);
        sLocalAdd.put("d83ddc76", R.raw.em_d83ddc76);
        sLocalAdd.put("d83ddc77", R.raw.em_d83ddc77);
        sLocalAdd.put("d83ddc78", R.raw.em_d83ddc78);
        sLocalAdd.put("d83ddc79", R.raw.em_d83ddc79);
        sLocalAdd.put("d83ddc7a", R.raw.em_d83ddc7a);
        sLocalAdd.put("d83ddc81", R.raw.em_d83ddc81);
        sLocalAdd.put("d83ddc82", R.raw.em_d83ddc82);
        sLocalAdd.put("d83ddc83", R.raw.em_d83ddc83);
        sLocalAdd.put("d83ddc85", R.raw.em_d83ddc85);
        sLocalAdd.put("d83ddc86", R.raw.em_d83ddc86);
        sLocalAdd.put("d83ddc87", R.raw.em_d83ddc87);
        sLocalAdd.put("d83ddc88", R.raw.em_d83ddc88);
        sLocalAdd.put("d83ddc89", R.raw.em_d83ddc89);
        sLocalAdd.put("d83ddc8b", R.raw.em_d83ddc8b);
        sLocalAdd.put("d83ddc8c", R.raw.em_d83ddc8c);
        sLocalAdd.put("d83ddc90", R.raw.em_d83ddc90);
        sLocalAdd.put("d83ddc92", R.raw.em_d83ddc92);
        sLocalAdd.put("d83ddc93", R.raw.em_d83ddc93);
        sLocalAdd.put("d83ddc95", R.raw.em_d83ddc95);
        sLocalAdd.put("d83ddc96", R.raw.em_d83ddc96);
        sLocalAdd.put("d83ddc97", R.raw.em_d83ddc97);
        sLocalAdd.put("d83ddc99", R.raw.em_d83ddc99);
        sLocalAdd.put("d83ddc9a", R.raw.em_d83ddc9a);
        sLocalAdd.put("d83ddc9b", R.raw.em_d83ddc9b);
        sLocalAdd.put("d83ddc9c", R.raw.em_d83ddc9c);
        sLocalAdd.put("d83ddc9e", R.raw.em_d83ddc9e);
        sLocalAdd.put("d83ddc9f", R.raw.em_d83ddc9f);
        sLocalAdd.put("d83ddca0", R.raw.em_d83ddca0);
        sLocalAdd.put("d83ddca2", R.raw.em_d83ddca2);
        sLocalAdd.put("d83ddca5", R.raw.em_d83ddca5);
        sLocalAdd.put("d83ddca7", R.raw.em_d83ddca7);
        sLocalAdd.put("d83ddca8", R.raw.em_d83ddca8);
        sLocalAdd.put("d83ddcaa", R.raw.em_d83ddcaa);
        sLocalAdd.put("d83ddcab", R.raw.em_d83ddcab);
        sLocalAdd.put("d83ddcac", R.raw.em_d83ddcac);
        sLocalAdd.put("d83ddcad", R.raw.em_d83ddcad);
        sLocalAdd.put("d83ddcae", R.raw.em_d83ddcae);
        sLocalAdd.put("d83ddcaf", R.raw.em_d83ddcaf);
        sLocalAdd.put("d83ddcb1", R.raw.em_d83ddcb1);
        sLocalAdd.put("d83ddcb2", R.raw.em_d83ddcb2);
        sLocalAdd.put("d83ddcb3", R.raw.em_d83ddcb3);
        sLocalAdd.put("d83ddcb4", R.raw.em_d83ddcb4);
        sLocalAdd.put("d83ddcb5", R.raw.em_d83ddcb5);
        sLocalAdd.put("d83ddcb6", R.raw.em_d83ddcb6);
        sLocalAdd.put("d83ddcb7", R.raw.em_d83ddcb7);
        sLocalAdd.put("d83ddcb8", R.raw.em_d83ddcb8);
        sLocalAdd.put("d83ddcb9", R.raw.em_d83ddcb9);
        sLocalAdd.put("d83ddcba", R.raw.em_d83ddcba);
        sLocalAdd.put("d83ddcbc", R.raw.em_d83ddcbc);
        sLocalAdd.put("d83ddcbd", R.raw.em_d83ddcbd);
        sLocalAdd.put("d83ddcbe", R.raw.em_d83ddcbe);
        sLocalAdd.put("d83ddcc0", R.raw.em_d83ddcc0);
        sLocalAdd.put("d83ddcc1", R.raw.em_d83ddcc1);
        sLocalAdd.put("d83ddcc2", R.raw.em_d83ddcc2);
        sLocalAdd.put("d83ddcc3", R.raw.em_d83ddcc3);
        sLocalAdd.put("d83ddcc4", R.raw.em_d83ddcc4);
        sLocalAdd.put("d83ddcc5", R.raw.em_d83ddcc5);
        sLocalAdd.put("d83ddcc6", R.raw.em_d83ddcc6);
        sLocalAdd.put("d83ddcc7", R.raw.em_d83ddcc7);
        sLocalAdd.put("d83ddcc8", R.raw.em_d83ddcc8);
        sLocalAdd.put("d83ddcc9", R.raw.em_d83ddcc9);
        sLocalAdd.put("d83ddcca", R.raw.em_d83ddcca);
        sLocalAdd.put("d83ddccb", R.raw.em_d83ddccb);
        sLocalAdd.put("d83ddccc", R.raw.em_d83ddccc);
        sLocalAdd.put("d83ddccd", R.raw.em_d83ddccd);
        sLocalAdd.put("d83ddcce", R.raw.em_d83ddcce);
        sLocalAdd.put("d83ddccf", R.raw.em_d83ddccf);
        sLocalAdd.put("d83ddcd0", R.raw.em_d83ddcd0);
        sLocalAdd.put("d83ddcd1", R.raw.em_d83ddcd1);
        sLocalAdd.put("d83ddcd2", R.raw.em_d83ddcd2);
        sLocalAdd.put("d83ddcd3", R.raw.em_d83ddcd3);
        sLocalAdd.put("d83ddcd4", R.raw.em_d83ddcd4);
        sLocalAdd.put("d83ddcd5", R.raw.em_d83ddcd5);
        sLocalAdd.put("d83ddcd6", R.raw.em_d83ddcd6);
        sLocalAdd.put("d83ddcd7", R.raw.em_d83ddcd7);
        sLocalAdd.put("d83ddcd8", R.raw.em_d83ddcd8);
        sLocalAdd.put("d83ddcd9", R.raw.em_d83ddcd9);
        sLocalAdd.put("d83ddcda", R.raw.em_d83ddcda);
        sLocalAdd.put("d83ddcdb", R.raw.em_d83ddcdb);
        sLocalAdd.put("d83ddcdc", R.raw.em_d83ddcdc);
        sLocalAdd.put("d83ddcdd", R.raw.em_d83ddcdd);
        sLocalAdd.put("d83ddcde", R.raw.em_d83ddcde);
        sLocalAdd.put("d83ddcdf", R.raw.em_d83ddcdf);
        sLocalAdd.put("d83ddce0", R.raw.em_d83ddce0);
        sLocalAdd.put("d83ddce1", R.raw.em_d83ddce1);
        sLocalAdd.put("d83ddce2", R.raw.em_d83ddce2);
        sLocalAdd.put("d83ddce3", R.raw.em_d83ddce3);
        sLocalAdd.put("d83ddce4", R.raw.em_d83ddce4);
        sLocalAdd.put("d83ddce5", R.raw.em_d83ddce5);
        sLocalAdd.put("d83ddce6", R.raw.em_d83ddce6);
        sLocalAdd.put("d83ddce7", R.raw.em_d83ddce7);
        sLocalAdd.put("d83ddce8", R.raw.em_d83ddce8);
        sLocalAdd.put("d83ddce9", R.raw.em_d83ddce9);
        sLocalAdd.put("d83ddceb", R.raw.em_d83ddceb);
        sLocalAdd.put("d83ddcec", R.raw.em_d83ddcec);
        sLocalAdd.put("d83ddced", R.raw.em_d83ddced);
        sLocalAdd.put("d83ddcee", R.raw.em_d83ddcee);
        sLocalAdd.put("d83ddcef", R.raw.em_d83ddcef);
        sLocalAdd.put("d83ddcf0", R.raw.em_d83ddcf0);
        sLocalAdd.put("d83ddcf1", R.raw.em_d83ddcf1);
        sLocalAdd.put("d83ddcf2", R.raw.em_d83ddcf2);
        sLocalAdd.put("d83ddcf3", R.raw.em_d83ddcf3);
        sLocalAdd.put("d83ddcf4", R.raw.em_d83ddcf4);
        sLocalAdd.put("d83ddcf5", R.raw.em_d83ddcf5);
        sLocalAdd.put("d83ddcf6", R.raw.em_d83ddcf6);
        sLocalAdd.put("d83ddcf9", R.raw.em_d83ddcf9);
        sLocalAdd.put("d83ddcfb", R.raw.em_d83ddcfb);
        sLocalAdd.put("d83ddcfc", R.raw.em_d83ddcfc);
        sLocalAdd.put("d83ddd00", R.raw.em_d83ddd00);
        sLocalAdd.put("d83ddd01", R.raw.em_d83ddd01);
        sLocalAdd.put("d83ddd02", R.raw.em_d83ddd02);
        sLocalAdd.put("d83ddd03", R.raw.em_d83ddd03);
        sLocalAdd.put("d83ddd04", R.raw.em_d83ddd04);
        sLocalAdd.put("d83ddd05", R.raw.em_d83ddd05);
        sLocalAdd.put("d83ddd06", R.raw.em_d83ddd06);
        sLocalAdd.put("d83ddd07", R.raw.em_d83ddd07);
        sLocalAdd.put("d83ddd08", R.raw.em_d83ddd08);
        sLocalAdd.put("d83ddd09", R.raw.em_d83ddd09);
        sLocalAdd.put("d83ddd0a", R.raw.em_d83ddd0a);
        sLocalAdd.put("d83ddd0b", R.raw.em_d83ddd0b);
        sLocalAdd.put("d83ddd0c", R.raw.em_d83ddd0c);
        sLocalAdd.put("d83ddd0d", R.raw.em_d83ddd0d);
        sLocalAdd.put("d83ddd0e", R.raw.em_d83ddd0e);
        sLocalAdd.put("d83ddd0f", R.raw.em_d83ddd0f);
        sLocalAdd.put("d83ddd10", R.raw.em_d83ddd10);
        sLocalAdd.put("d83ddd15", R.raw.em_d83ddd15);
        sLocalAdd.put("d83ddd16", R.raw.em_d83ddd16);
        sLocalAdd.put("d83ddd17", R.raw.em_d83ddd17);
        sLocalAdd.put("d83ddd18", R.raw.em_d83ddd18);
        sLocalAdd.put("d83ddd19", R.raw.em_d83ddd19);
        sLocalAdd.put("d83ddd1a", R.raw.em_d83ddd1a);
        sLocalAdd.put("d83ddd1b", R.raw.em_d83ddd1b);
        sLocalAdd.put("d83ddd1c", R.raw.em_d83ddd1c);
        sLocalAdd.put("d83ddd1d", R.raw.em_d83ddd1d);
        sLocalAdd.put("d83ddd1e", R.raw.em_d83ddd1e);
        sLocalAdd.put("d83ddd1f", R.raw.em_d83ddd1f);
        sLocalAdd.put("d83ddd20", R.raw.em_d83ddd20);
        sLocalAdd.put("d83ddd21", R.raw.em_d83ddd21);
        sLocalAdd.put("d83ddd22", R.raw.em_d83ddd22);
        sLocalAdd.put("d83ddd23", R.raw.em_d83ddd23);
        sLocalAdd.put("d83ddd24", R.raw.em_d83ddd24);
        sLocalAdd.put("d83ddd26", R.raw.em_d83ddd26);
        sLocalAdd.put("d83ddd27", R.raw.em_d83ddd27);
        sLocalAdd.put("d83ddd28", R.raw.em_d83ddd28);
        sLocalAdd.put("d83ddd29", R.raw.em_d83ddd29);
        sLocalAdd.put("d83ddd2a", R.raw.em_d83ddd2a);
        sLocalAdd.put("d83ddd2c", R.raw.em_d83ddd2c);
        sLocalAdd.put("d83ddd2d", R.raw.em_d83ddd2d);
        sLocalAdd.put("d83ddd2e", R.raw.em_d83ddd2e);
        sLocalAdd.put("d83ddd2f", R.raw.em_d83ddd2f);
        sLocalAdd.put("d83ddd30", R.raw.em_d83ddd30);
        sLocalAdd.put("d83ddd31", R.raw.em_d83ddd31);
        sLocalAdd.put("d83ddd32", R.raw.em_d83ddd32);
        sLocalAdd.put("d83ddd33", R.raw.em_d83ddd33);
        sLocalAdd.put("d83ddd34", R.raw.em_d83ddd34);
        sLocalAdd.put("d83ddd35", R.raw.em_d83ddd35);
        sLocalAdd.put("d83ddd36", R.raw.em_d83ddd36);
        sLocalAdd.put("d83ddd37", R.raw.em_d83ddd37);
        sLocalAdd.put("d83ddd38", R.raw.em_d83ddd38);
        sLocalAdd.put("d83ddd39", R.raw.em_d83ddd39);
        sLocalAdd.put("d83ddd3a", R.raw.em_d83ddd3a);
        sLocalAdd.put("d83ddd3b", R.raw.em_d83ddd3b);
        sLocalAdd.put("d83ddd3c", R.raw.em_d83ddd3c);
        sLocalAdd.put("d83ddd3d", R.raw.em_d83ddd3d);
        sLocalAdd.put("d83ddd50", R.raw.em_d83ddd50);
        sLocalAdd.put("d83ddd51", R.raw.em_d83ddd51);
        sLocalAdd.put("d83ddd52", R.raw.em_d83ddd52);
        sLocalAdd.put("d83ddd53", R.raw.em_d83ddd53);
        sLocalAdd.put("d83ddd54", R.raw.em_d83ddd54);
        sLocalAdd.put("d83ddd55", R.raw.em_d83ddd55);
        sLocalAdd.put("d83ddd56", R.raw.em_d83ddd56);
        sLocalAdd.put("d83ddd57", R.raw.em_d83ddd57);
        sLocalAdd.put("d83ddd58", R.raw.em_d83ddd58);
        sLocalAdd.put("d83ddd59", R.raw.em_d83ddd59);
        sLocalAdd.put("d83ddd5a", R.raw.em_d83ddd5a);
        sLocalAdd.put("d83ddd5b", R.raw.em_d83ddd5b);
        sLocalAdd.put("d83ddd5c", R.raw.em_d83ddd5c);
        sLocalAdd.put("d83ddd5d", R.raw.em_d83ddd5d);
        sLocalAdd.put("d83ddd5e", R.raw.em_d83ddd5e);
        sLocalAdd.put("d83ddd5f", R.raw.em_d83ddd5f);
        sLocalAdd.put("d83ddd60", R.raw.em_d83ddd60);
        sLocalAdd.put("d83ddd61", R.raw.em_d83ddd61);
        sLocalAdd.put("d83ddd62", R.raw.em_d83ddd62);
        sLocalAdd.put("d83ddd63", R.raw.em_d83ddd63);
        sLocalAdd.put("d83ddd64", R.raw.em_d83ddd64);
        sLocalAdd.put("d83ddd65", R.raw.em_d83ddd65);
        sLocalAdd.put("d83ddd66", R.raw.em_d83ddd66);
        sLocalAdd.put("d83ddd67", R.raw.em_d83ddd67);
        sLocalAdd.put("d83dddfb", R.raw.em_d83dddfb);
        sLocalAdd.put("d83dddfc", R.raw.em_d83dddfc);
        sLocalAdd.put("d83dddfd", R.raw.em_d83dddfd);
        sLocalAdd.put("d83dddfe", R.raw.em_d83dddfe);
        sLocalAdd.put("d83dddff", R.raw.em_d83dddff);
        sLocalAdd.put("d83dde00", R.raw.em_d83dde00);
        sLocalAdd.put("d83dde05", R.raw.em_d83dde05);
        sLocalAdd.put("d83dde06", R.raw.em_d83dde06);
        sLocalAdd.put("d83dde07", R.raw.em_d83dde07);
        sLocalAdd.put("d83dde08", R.raw.em_d83dde08);
        sLocalAdd.put("d83dde0b", R.raw.em_d83dde0b);
        sLocalAdd.put("d83dde0e", R.raw.em_d83dde0e);
        sLocalAdd.put("d83dde10", R.raw.em_d83dde10);
        sLocalAdd.put("d83dde11", R.raw.em_d83dde11);
        sLocalAdd.put("d83dde15", R.raw.em_d83dde15);
        sLocalAdd.put("d83dde17", R.raw.em_d83dde17);
        sLocalAdd.put("d83dde19", R.raw.em_d83dde19);
        sLocalAdd.put("d83dde1b", R.raw.em_d83dde1b);
        sLocalAdd.put("d83dde1f", R.raw.em_d83dde1f);
        sLocalAdd.put("d83dde24", R.raw.em_d83dde24);
        sLocalAdd.put("d83dde26", R.raw.em_d83dde26);
        sLocalAdd.put("d83dde27", R.raw.em_d83dde27);
        sLocalAdd.put("d83dde29", R.raw.em_d83dde29);
        sLocalAdd.put("d83dde2b", R.raw.em_d83dde2b);
        sLocalAdd.put("d83dde2c", R.raw.em_d83dde2c);
        sLocalAdd.put("d83dde2e", R.raw.em_d83dde2e);
        sLocalAdd.put("d83dde2f", R.raw.em_d83dde2f);
        sLocalAdd.put("d83dde34", R.raw.em_d83dde34);
        sLocalAdd.put("d83dde35", R.raw.em_d83dde35);
        sLocalAdd.put("d83dde36", R.raw.em_d83dde36);
        sLocalAdd.put("d83dde38", R.raw.em_d83dde38);
        sLocalAdd.put("d83dde39", R.raw.em_d83dde39);
        sLocalAdd.put("d83dde3a", R.raw.em_d83dde3a);
        sLocalAdd.put("d83dde3b", R.raw.em_d83dde3b);
        sLocalAdd.put("d83dde3c", R.raw.em_d83dde3c);
        sLocalAdd.put("d83dde3d", R.raw.em_d83dde3d);
        sLocalAdd.put("d83dde3e", R.raw.em_d83dde3e);
        sLocalAdd.put("d83dde3f", R.raw.em_d83dde3f);
        sLocalAdd.put("d83dde40", R.raw.em_d83dde40);
        sLocalAdd.put("d83dde45", R.raw.em_d83dde45);
        sLocalAdd.put("d83dde46", R.raw.em_d83dde46);
        sLocalAdd.put("d83dde47", R.raw.em_d83dde47);
        sLocalAdd.put("d83dde48", R.raw.em_d83dde48);
        sLocalAdd.put("d83dde49", R.raw.em_d83dde49);
        sLocalAdd.put("d83dde4a", R.raw.em_d83dde4a);
        sLocalAdd.put("d83dde4b", R.raw.em_d83dde4b);
        sLocalAdd.put("d83dde4c", R.raw.em_d83dde4c);
        sLocalAdd.put("d83dde4d", R.raw.em_d83dde4d);
        sLocalAdd.put("d83dde4e", R.raw.em_d83dde4e);
        sLocalAdd.put("d83dde4f", R.raw.em_d83dde4f);
        sLocalAdd.put("d83dde81", R.raw.em_d83dde81);
        sLocalAdd.put("d83dde82", R.raw.em_d83dde82);
        sLocalAdd.put("d83dde83", R.raw.em_d83dde83);
        sLocalAdd.put("d83dde84", R.raw.em_d83dde84);
        sLocalAdd.put("d83dde85", R.raw.em_d83dde85);
        sLocalAdd.put("d83dde86", R.raw.em_d83dde86);
        sLocalAdd.put("d83dde87", R.raw.em_d83dde87);
        sLocalAdd.put("d83dde88", R.raw.em_d83dde88);
        sLocalAdd.put("d83dde89", R.raw.em_d83dde89);
        sLocalAdd.put("d83dde8a", R.raw.em_d83dde8a);
        sLocalAdd.put("d83dde8b", R.raw.em_d83dde8b);
        sLocalAdd.put("d83dde8d", R.raw.em_d83dde8d);
        sLocalAdd.put("d83dde8e", R.raw.em_d83dde8e);
        sLocalAdd.put("d83dde8f", R.raw.em_d83dde8f);
        sLocalAdd.put("d83dde90", R.raw.em_d83dde90);
        sLocalAdd.put("d83dde91", R.raw.em_d83dde91);
        sLocalAdd.put("d83dde92", R.raw.em_d83dde92);
        sLocalAdd.put("d83dde93", R.raw.em_d83dde93);
        sLocalAdd.put("d83dde94", R.raw.em_d83dde94);
        sLocalAdd.put("d83dde96", R.raw.em_d83dde96);
        sLocalAdd.put("d83dde98", R.raw.em_d83dde98);
        sLocalAdd.put("d83dde9a", R.raw.em_d83dde9a);
        sLocalAdd.put("d83dde9b", R.raw.em_d83dde9b);
        sLocalAdd.put("d83dde9c", R.raw.em_d83dde9c);
        sLocalAdd.put("d83dde9d", R.raw.em_d83dde9d);
        sLocalAdd.put("d83dde9e", R.raw.em_d83dde9e);
        sLocalAdd.put("d83dde9f", R.raw.em_d83dde9f);
        sLocalAdd.put("d83ddea0", R.raw.em_d83ddea0);
        sLocalAdd.put("d83ddea1", R.raw.em_d83ddea1);
        sLocalAdd.put("d83ddea2", R.raw.em_d83ddea2);
        sLocalAdd.put("d83ddea3", R.raw.em_d83ddea3);
        sLocalAdd.put("d83ddea4", R.raw.em_d83ddea4);
        sLocalAdd.put("d83ddea5", R.raw.em_d83ddea5);
        sLocalAdd.put("d83ddea6", R.raw.em_d83ddea6);
        sLocalAdd.put("d83ddea7", R.raw.em_d83ddea7);
        sLocalAdd.put("d83ddea8", R.raw.em_d83ddea8);
        sLocalAdd.put("d83ddea9", R.raw.em_d83ddea9);
        sLocalAdd.put("d83ddeaa", R.raw.em_d83ddeaa);
        sLocalAdd.put("d83ddeab", R.raw.em_d83ddeab);
        sLocalAdd.put("d83ddeac", R.raw.em_d83ddeac);
        sLocalAdd.put("d83ddead", R.raw.em_d83ddead);
        sLocalAdd.put("d83ddeae", R.raw.em_d83ddeae);
        sLocalAdd.put("d83ddeaf", R.raw.em_d83ddeaf);
        sLocalAdd.put("d83ddeb0", R.raw.em_d83ddeb0);
        sLocalAdd.put("d83ddeb1", R.raw.em_d83ddeb1);
        sLocalAdd.put("d83ddeb3", R.raw.em_d83ddeb3);
        sLocalAdd.put("d83ddeb4", R.raw.em_d83ddeb4);
        sLocalAdd.put("d83ddeb5", R.raw.em_d83ddeb5);
        sLocalAdd.put("d83ddeb6", R.raw.em_d83ddeb6);
        sLocalAdd.put("d83ddeb7", R.raw.em_d83ddeb7);
        sLocalAdd.put("d83ddeb8", R.raw.em_d83ddeb8);
        sLocalAdd.put("d83ddebb", R.raw.em_d83ddebb);
        sLocalAdd.put("d83ddebc", R.raw.em_d83ddebc);
        sLocalAdd.put("d83ddebd", R.raw.em_d83ddebd);
        sLocalAdd.put("d83ddebe", R.raw.em_d83ddebe);
        sLocalAdd.put("d83ddebf", R.raw.em_d83ddebf);
        sLocalAdd.put("d83ddec1", R.raw.em_d83ddec1);
        sLocalAdd.put("d83ddec2", R.raw.em_d83ddec2);
        sLocalAdd.put("d83ddec3", R.raw.em_d83ddec3);
        sLocalAdd.put("d83ddec4", R.raw.em_d83ddec4);
        sLocalAdd.put("d83ddec5", R.raw.em_d83ddec5);
        sLocalAdd.put("e50a", R.raw.em_e50a);

    }

    static {
        sHistoryFace.put("[#a1#]", "\uD83D\uDE04");
        sHistoryFace.put("[#a2#]", "\uD83D\uDE0A");
        sHistoryFace.put("[#a3#]", "\uD83D\uDE03");
        sHistoryFace.put("[#a4#]", "\u263A");
        sHistoryFace.put("[#a5#]", "\uD83D\uDE09");
        sHistoryFace.put("[#a6#]", "\uD83D\uDE0D");
        sHistoryFace.put("[#a7#]", "\uD83D\uDE18");
        sHistoryFace.put("[#a8#]", "\uD83D\uDE1A");
        sHistoryFace.put("[#a9#]", "\uD83D\uDE33");
        sHistoryFace.put("[#a10#]", "\uD83D\uDE0C");
        sHistoryFace.put("[#a11#]", "\uD83D\uDE01");
        sHistoryFace.put("[#a12#]", "\uD83D\uDE1C");
        sHistoryFace.put("[#a13#]", "\uD83D\uDE1D");
        sHistoryFace.put("[#a14#]", "\uD83D\uDE12");
        sHistoryFace.put("[#a15#]", "\uD83D\uDE0F");
        sHistoryFace.put("[#a16#]", "\uD83D\uDE13");
        sHistoryFace.put("[#a17#]", "\uD83D\uDE14");
        sHistoryFace.put("[#a18#]", "\uD83D\uDE1E");
        sHistoryFace.put("[#a19#]", "\uD83D\uDE16");
        sHistoryFace.put("[#a20#]", "\uD83D\uDE25");
        sHistoryFace.put("[#a21#]", "\uD83D\uDE30");
        sHistoryFace.put("[#a22#]", "\uD83D\uDE28");
        sHistoryFace.put("[#a23#]", "\uD83D\uDE23");
        sHistoryFace.put("[#a24#]", "\uD83D\uDE22");
        sHistoryFace.put("[#a25#]", "\uD83D\uDE2D");
        sHistoryFace.put("[#a26#]", "\uD83D\uDE02");
        sHistoryFace.put("[#a27#]", "\uD83D\uDE32");
        sHistoryFace.put("[#a28#]", "\uD83D\uDE31");
        sHistoryFace.put("[#a29#]", "\uD83D\uDE20");
        sHistoryFace.put("[#a30#]", "\uD83D\uDE21");
        sHistoryFace.put("[#a31#]", "\uD83D\uDE2A");
        sHistoryFace.put("[#a32#]", "\uD83D\uDE37");
        sHistoryFace.put("[#a33#]", "\uD83D\uDC7F");
        sHistoryFace.put("[#a34#]", "\uD83D\uDC7D");
        sHistoryFace.put("[#a35#]", "\uD83D\uDC9B");
        sHistoryFace.put("[#a36#]", "\uD83D\uDC99");
        sHistoryFace.put("[#a37#]", "\uD83D\uDC9C");
        sHistoryFace.put("[#a38#]", "\uD83D\uDC97");
        sHistoryFace.put("[#a39#]", "\uD83D\uDC9A");
        sHistoryFace.put("[#a40#]", "\u2764");
        sHistoryFace.put("[#a41#]", "\uD83D\uDC94");
        sHistoryFace.put("[#a42#]", "\uD83D\uDC93");
        sHistoryFace.put("[#a43#]", "\uD83D\uDC98");
        sHistoryFace.put("[#a44#]", "\u2728");
        sHistoryFace.put("[#a45#]", "\u2B50");
        sHistoryFace.put("[#a46#]", "\uD83D\uDCA2");
        sHistoryFace.put("[#a47#]", "\u2757");
        sHistoryFace.put("[#a48#]", "\u2753");
        sHistoryFace.put("[#a49#]", "\uD83D\uDCA4");
        sHistoryFace.put("[#a50#]", "\uD83D\uDCA8");
        sHistoryFace.put("[#a51#]", "\uD83D\uDCA6");
        sHistoryFace.put("[#a52#]", "\uD83C\uDFB6");
        sHistoryFace.put("[#a53#]", "\uD83C\uDFB5");
        sHistoryFace.put("[#a54#]", "\uD83D\uDD25");
        sHistoryFace.put("[#a55#]", "\uD83D\uDCA9");
        sHistoryFace.put("[#a56#]", "\uD83D\uDC4D");
        sHistoryFace.put("[#a57#]", "\uD83D\uDC4E");
        sHistoryFace.put("[#a58#]", "\uD83D\uDC4C");
        sHistoryFace.put("[#a59#]", "\uD83D\uDC4A");
        sHistoryFace.put("[#a60#]", "\u270A");
        sHistoryFace.put("[#a61#]", "\u270C");
        sHistoryFace.put("[#a62#]", "\uD83D\uDC4B");
        sHistoryFace.put("[#a63#]", "\u270B");
        sHistoryFace.put("[#a64#]", "\uD83D\uDC50");
        sHistoryFace.put("[#a65#]", "\uD83D\uDC46");
        sHistoryFace.put("[#a66#]", "\uD83D\uDC47");
        sHistoryFace.put("[#a67#]", "\uD83D\uDC49");
        sHistoryFace.put("[#a68#]", "\uD83D\uDC48");
        sHistoryFace.put("[#a69#]", "\uD83D\uDE4C");
        sHistoryFace.put("[#a70#]", "\uD83D\uDE4F");
        sHistoryFace.put("[#a71#]", "\u261D");
        sHistoryFace.put("[#a72#]", "\uD83D\uDC4F");
        sHistoryFace.put("[#a73#]", "\uD83D\uDCAA");
        sHistoryFace.put("[#a74#]", "\uD83D\uDEB6");
        sHistoryFace.put("[#a75#]", "\uD83C\uDFC3");
        sHistoryFace.put("[#a76#]", "\uD83D\uDC6B");
        sHistoryFace.put("[#a77#]", "\uD83D\uDC83");
        sHistoryFace.put("[#a78#]", "\uD83D\uDC6F");
        sHistoryFace.put("[#a79#]", "\uD83D\uDE46");
        sHistoryFace.put("[#a80#]", "\uD83D\uDE45");
        sHistoryFace.put("[#a81#]", "\uD83D\uDC81");
        sHistoryFace.put("[#a82#]", "\uD83D\uDE47");
        sHistoryFace.put("[#a83#]", "\uD83D\uDC8F");
        sHistoryFace.put("[#a84#]", "\uD83D\uDC91");
        sHistoryFace.put("[#a85#]", "\uD83D\uDC86");
        sHistoryFace.put("[#a86#]", "\uD83D\uDC87");
        sHistoryFace.put("[#a87#]", "\uD83D\uDC85");
        sHistoryFace.put("[#a88#]", "\uD83D\uDC66");
        sHistoryFace.put("[#a89#]", "\uD83D\uDC67");
        sHistoryFace.put("[#a90#]", "\uD83D\uDC69");
        sHistoryFace.put("[#a91#]", "\uD83D\uDC68");
        sHistoryFace.put("[#a92#]", "\uD83D\uDC76");
        sHistoryFace.put("[#a93#]", "\uD83D\uDC75");
        sHistoryFace.put("[#a94#]", "\uD83D\uDC74");
        sHistoryFace.put("[#a95#]", "\uD83D\uDC71");
        sHistoryFace.put("[#a96#]", "\uD83D\uDC72");
        sHistoryFace.put("[#a97#]", "\uD83D\uDC73");
        sHistoryFace.put("[#a98#]", "\uD83D\uDC77");
        sHistoryFace.put("[#a99#]", "\uD83D\uDC6E");
        sHistoryFace.put("[#a100#]", "\uD83D\uDC7C");
        sHistoryFace.put("[#a101#]", "\uD83D\uDC78");
        sHistoryFace.put("[#a102#]", "\uD83D\uDC82");
        sHistoryFace.put("[#a103#]", "\uD83D\uDC80");
        sHistoryFace.put("[#a104#]", "\uD83D\uDC63");
        sHistoryFace.put("[#a105#]", "\uD83D\uDC8B");
        sHistoryFace.put("[#a106#]", "\uD83D\uDC44");
        sHistoryFace.put("[#a107#]", "\uD83D\uDC42");
        sHistoryFace.put("[#a108#]", "\uD83D\uDC40");
        sHistoryFace.put("[#a109#]", "\uD83D\uDC43");
        sHistoryFace.put("[#a110#]", "\uD83D\uDE00");
        sHistoryFace.put("[#a111#]", "\uD83D\uDE17");
        sHistoryFace.put("[#a112#]", "\uD83D\uDE19");
        sHistoryFace.put("[#a113#]", "\uD83D\uDE1B");
        sHistoryFace.put("[#a114#]", "\uD83D\uDE05");
        sHistoryFace.put("[#a115#]", "\uD83D\uDE29");
        sHistoryFace.put("[#a116#]", "\uD83D\uDE2B");
        sHistoryFace.put("[#a117#]", "\uD83D\uDE24");
        sHistoryFace.put("[#a118#]", "\uD83D\uDE06");
        sHistoryFace.put("[#a119#]", "\uD83D\uDE0B");
        sHistoryFace.put("[#a120#]", "\uD83D\uDE0E");
        sHistoryFace.put("[#a121#]", "\uD83D\uDE34");
        sHistoryFace.put("[#a122#]", "\uD83D\uDE35");
        sHistoryFace.put("[#a123#]", "\uD83D\uDE1F");
        sHistoryFace.put("[#a124#]", "\uD83D\uDE26");
        sHistoryFace.put("[#a125#]", "\uD83D\uDE27");
        sHistoryFace.put("[#a126#]", "\uD83D\uDE08");
        sHistoryFace.put("[#a127#]", "\uD83D\uDE2E");
        sHistoryFace.put("[#a128#]", "\uD83D\uDE2C");
        sHistoryFace.put("[#a129#]", "\uD83D\uDE10");
        sHistoryFace.put("[#a130#]", "\uD83D\uDE15");
        sHistoryFace.put("[#a131#]", "\uD83D\uDE2F");
        sHistoryFace.put("[#a132#]", "\uD83D\uDE36");
        sHistoryFace.put("[#a133#]", "\uD83D\uDE07");
        sHistoryFace.put("[#a134#]", "\uD83D\uDE11");
        sHistoryFace.put("[#a135#]", "\uD83D\uDE3A");
        sHistoryFace.put("[#a136#]", "\uD83D\uDE38");
        sHistoryFace.put("[#a137#]", "\uD83D\uDE3B");
        sHistoryFace.put("[#a138#]", "\uD83D\uDE3D");
        sHistoryFace.put("[#a139#]", "\uD83D\uDE3C");
        sHistoryFace.put("[#a140#]", "\uD83D\uDE40");
        sHistoryFace.put("[#a141#]", "\uD83D\uDE3F");
        sHistoryFace.put("[#a142#]", "\uD83D\uDE39");
        sHistoryFace.put("[#a143#]", "\uD83D\uDE3E");
        sHistoryFace.put("[#a144#]", "\uD83D\uDC79");
        sHistoryFace.put("[#a145#]", "\uD83D\uDC7A");
        sHistoryFace.put("[#a146#]", "\uD83D\uDE48");
        sHistoryFace.put("[#a147#]", "\uD83D\uDE49");
        sHistoryFace.put("[#a148#]", "\uD83D\uDE4A");
        sHistoryFace.put("[#a149#]", "\uD83C\uDF1F");
        sHistoryFace.put("[#a150#]", "\uD83D\uDCAB");
        sHistoryFace.put("[#a151#]", "\uD83D\uDCA5");
        sHistoryFace.put("[#a152#]", "\uD83D\uDCA7");
        sHistoryFace.put("[#a153#]", "\uD83D\uDC45");
        sHistoryFace.put("[#a154#]", "\uD83D\uDC6A");
        sHistoryFace.put("[#a155#]", "\uD83D\uDC6C");
        sHistoryFace.put("[#a156#]", "\uD83D\uDC6D");
        sHistoryFace.put("[#a157#]", "\uD83D\uDE4B");
        sHistoryFace.put("[#a158#]", "\uD83D\uDC70");
        sHistoryFace.put("[#a159#]", "\uD83D\uDE4E");
        sHistoryFace.put("[#a160#]", "\uD83D\uDE4D");
        sHistoryFace.put("[#a161#]", "\uD83D\uDC5F");
        sHistoryFace.put("[#a162#]", "\uD83D\uDC5A");
        sHistoryFace.put("[#a163#]", "\uD83C\uDFBD");
        sHistoryFace.put("[#a164#]", "\uD83D\uDC56");
        sHistoryFace.put("[#a165#]", "\uD83D\uDC5D");
        sHistoryFace.put("[#a166#]", "\uD83D\uDC5B");
        sHistoryFace.put("[#a167#]", "\uD83D\uDC53");
        sHistoryFace.put("[#a168#]", "\uD83D\uDC95");
        sHistoryFace.put("[#a169#]", "\uD83D\uDC96");
        sHistoryFace.put("[#a170#]", "\uD83D\uDC9E");
        sHistoryFace.put("[#a171#]", "\uD83D\uDC8C");
        sHistoryFace.put("[#a172#]", "\uD83D\uDC64");
        sHistoryFace.put("[#a173#]", "\uD83D\uDC65");
        sHistoryFace.put("[#a174#]", "\uD83D\uDCAC");
        sHistoryFace.put("[#a175#]", "\uD83D\uDCAD");
        sHistoryFace.put("[#b1#]", "\u2600");
        sHistoryFace.put("[#b2#]", "\u2614");
        sHistoryFace.put("[#b3#]", "\u2601");
        sHistoryFace.put("[#b4#]", "\u26C4");
        sHistoryFace.put("[#b5#]", "\uD83C\uDF19");
        sHistoryFace.put("[#b6#]", "\u26A1");
        sHistoryFace.put("[#b7#]", "\uD83C\uDF00");
        sHistoryFace.put("[#b8#]", "\uD83C\uDF0A");
        sHistoryFace.put("[#b9#]", "\uD83D\uDC31");
        sHistoryFace.put("[#b10#]", "\uD83D\uDC36");
        sHistoryFace.put("[#b11#]", "\uD83D\uDC2D");
        sHistoryFace.put("[#b12#]", "\uD83D\uDC39");
        sHistoryFace.put("[#b13#]", "\uD83D\uDC30");
        sHistoryFace.put("[#b14#]", "\uD83D\uDC3A");
        sHistoryFace.put("[#b15#]", "\uD83D\uDC38");
        sHistoryFace.put("[#b16#]", "\uD83D\uDC2F");
        sHistoryFace.put("[#b17#]", "\uD83D\uDC28");
        sHistoryFace.put("[#b18#]", "\uD83D\uDC3B");
        sHistoryFace.put("[#b19#]", "\uD83D\uDC37");
        sHistoryFace.put("[#b20#]", "\uD83D\uDC2E");
        sHistoryFace.put("[#b21#]", "\uD83D\uDC17");
        sHistoryFace.put("[#b22#]", "\uD83D\uDC12");
        sHistoryFace.put("[#b23#]", "\uD83D\uDC34");
        sHistoryFace.put("[#b24#]", "\uD83D\uDC0E");
        sHistoryFace.put("[#b25#]", "\uD83D\uDC2B");
        sHistoryFace.put("[#b26#]", "\uD83D\uDC11");
        sHistoryFace.put("[#b27#]", "\uD83D\uDC18");
        sHistoryFace.put("[#b28#]", "\uD83D\uDC0D");
        sHistoryFace.put("[#b29#]", "\uD83D\uDC26");
        sHistoryFace.put("[#b30#]", "\uD83D\uDC24");
        sHistoryFace.put("[#b31#]", "\uD83D\uDC14");
        sHistoryFace.put("[#b32#]", "\uD83D\uDC27");
        sHistoryFace.put("[#b33#]", "\uD83D\uDC1B");
        sHistoryFace.put("[#b34#]", "\uD83D\uDC19");
        sHistoryFace.put("[#b35#]", "\uD83D\uDC35");
        sHistoryFace.put("[#b36#]", "\uD83D\uDC20");
        sHistoryFace.put("[#b37#]", "\uD83D\uDC1F");
        sHistoryFace.put("[#b38#]", "\uD83D\uDC33");
        sHistoryFace.put("[#b39#]", "\uD83D\uDC2C");
        sHistoryFace.put("[#b40#]", "\uD83D\uDC90");
        sHistoryFace.put("[#b41#]", "\uD83C\uDF38");
        sHistoryFace.put("[#b42#]", "\uD83C\uDF37");
        sHistoryFace.put("[#b43#]", "\uD83C\uDF40");
        sHistoryFace.put("[#b44#]", "\uD83C\uDF39");
        sHistoryFace.put("[#b45#]", "\uD83C\uDF3B");
        sHistoryFace.put("[#b46#]", "\uD83C\uDF3A");
        sHistoryFace.put("[#b47#]", "\uD83C\uDF41");
        sHistoryFace.put("[#b48#]", "\uD83C\uDF43");
        sHistoryFace.put("[#b49#]", "\uD83C\uDF42");
        sHistoryFace.put("[#b50#]", "\uD83C\uDF34");
        sHistoryFace.put("[#b51#]", "\uD83C\uDF35");
        sHistoryFace.put("[#b52#]", "\uD83C\uDF3E");
        sHistoryFace.put("[#b53#]", "\uD83D\uDC1A");
        sHistoryFace.put("[#b54#]", "\uD83D\uDC3D");
        sHistoryFace.put("[#b55#]", "\uD83D\uDC3C");
        sHistoryFace.put("[#b56#]", "\uD83D\uDC25");
        sHistoryFace.put("[#b57#]", "\uD83D\uDC23");
        sHistoryFace.put("[#b58#]", "\uD83D\uDC22");
        sHistoryFace.put("[#b59#]", "\uD83D\uDC1D");
        sHistoryFace.put("[#b60#]", "\uD83D\uDC1C");
        sHistoryFace.put("[#b61#]", "\uD83D\uDC1E");
        sHistoryFace.put("[#b62#]", "\uD83D\uDC0C");
        sHistoryFace.put("[#b63#]", "\uD83D\uDC0B");
        sHistoryFace.put("[#b64#]", "\uD83D\uDC04");
        sHistoryFace.put("[#b65#]", "\uD83D\uDC0F");
        sHistoryFace.put("[#b66#]", "\uD83D\uDC00");
        sHistoryFace.put("[#b67#]", "\uD83D\uDC03");
        sHistoryFace.put("[#b68#]", "\uD83D\uDC05");
        sHistoryFace.put("[#b69#]", "\uD83D\uDC07");
        sHistoryFace.put("[#b70#]", "\uD83D\uDC09");
        sHistoryFace.put("[#b71#]", "\uD83D\uDC10");
        sHistoryFace.put("[#b72#]", "\uD83D\uDC13");
        sHistoryFace.put("[#b73#]", "\uD83D\uDC15");
        sHistoryFace.put("[#b74#]", "\uD83D\uDC16");
        sHistoryFace.put("[#b75#]", "\uD83D\uDC01");
        sHistoryFace.put("[#b76#]", "\uD83D\uDC02");
        sHistoryFace.put("[#b77#]", "\uD83D\uDC32");
        sHistoryFace.put("[#b78#]", "\uD83D\uDC21");
        sHistoryFace.put("[#b79#]", "\uD83D\uDC0A");
        sHistoryFace.put("[#b80#]", "\uD83D\uDC2A");
        sHistoryFace.put("[#b81#]", "\uD83D\uDC06");
        sHistoryFace.put("[#b82#]", "\uD83D\uDC08");
        sHistoryFace.put("[#b83#]", "\uD83D\uDC29");
        sHistoryFace.put("[#b84#]", "\uD83D\uDC3E");
        sHistoryFace.put("[#b85#]", "\uD83C\uDF3F");
        sHistoryFace.put("[#b86#]", "\uD83C\uDF44");
        sHistoryFace.put("[#b87#]", "\uD83C\uDF32");
        sHistoryFace.put("[#b88#]", "\uD83C\uDF33");
        sHistoryFace.put("[#b89#]", "\uD83C\uDF30");
        sHistoryFace.put("[#b90#]", "\uD83C\uDF31");
        sHistoryFace.put("[#b91#]", "\uD83C\uDF3C");
        sHistoryFace.put("[#b92#]", "\uD83C\uDF10");
        sHistoryFace.put("[#b93#]", "\uD83C\uDF1E");
        sHistoryFace.put("[#b94#]", "\uD83C\uDF1D");
        sHistoryFace.put("[#b95#]", "\uD83C\uDF1A");
        sHistoryFace.put("[#b96#]", "\uD83C\uDF11");
        sHistoryFace.put("[#b97#]", "\uD83C\uDF12");
        sHistoryFace.put("[#b98#]", "\uD83C\uDF13");
        sHistoryFace.put("[#b99#]", "\uD83C\uDF14");
        sHistoryFace.put("[#b100#]", "\uD83C\uDF15");
        sHistoryFace.put("[#b101#]", "\uD83C\uDF16");
        sHistoryFace.put("[#b102#]", "\uD83C\uDF17");
        sHistoryFace.put("[#b103#]", "\uD83C\uDF18");
        sHistoryFace.put("[#b104#]", "\uD83C\uDF1C");
        sHistoryFace.put("[#b105#]", "\uD83C\uDF1B");
        sHistoryFace.put("[#b106#]", "\uD83C\uDF0D");
        sHistoryFace.put("[#b107#]", "\uD83C\uDF0E");
        sHistoryFace.put("[#b108#]", "\uD83C\uDF0F");
        sHistoryFace.put("[#b109#]", "\uD83C\uDF0B");
        sHistoryFace.put("[#b110#]", "\uD83C\uDF0C");
        sHistoryFace.put("[#b111#]", "\uD83C\uDF20");
        sHistoryFace.put("[#b112#]", "\u26C5");
        sHistoryFace.put("[#b113#]", "\u2744");
        sHistoryFace.put("[#b114#]", "\uD83C\uDF01");
        sHistoryFace.put("[#c1#]", "\uD83C\uDF8D");
        sHistoryFace.put("[#c2#]", "\uD83D\uDC9D");
        sHistoryFace.put("[#c3#]", "\uD83C\uDF8E");
        sHistoryFace.put("[#c4#]", "\uD83C\uDF92");
        sHistoryFace.put("[#c5#]", "\uD83C\uDF93");
        sHistoryFace.put("[#c6#]", "\uD83C\uDF8F");
        sHistoryFace.put("[#c7#]", "\uD83C\uDF86");
        sHistoryFace.put("[#c8#]", "\uD83C\uDF87");
        sHistoryFace.put("[#c9#]", "\uD83C\uDF90");
        sHistoryFace.put("[#c10#]", "\uD83C\uDF91");
        sHistoryFace.put("[#c11#]", "\uD83C\uDF83");
        sHistoryFace.put("[#c12#]", "\uD83D\uDC7B");
        sHistoryFace.put("[#c13#]", "\uD83C\uDF85");
        sHistoryFace.put("[#c14#]", "\uD83C\uDF84");
        sHistoryFace.put("[#c15#]", "\uD83C\uDF81");
        sHistoryFace.put("[#c16#]", "\uD83D\uDD14");
        sHistoryFace.put("[#c17#]", "\uD83C\uDF89");
        sHistoryFace.put("[#c18#]", "\uD83C\uDF88");
        sHistoryFace.put("[#c19#]", "\uD83D\uDCBF");
        sHistoryFace.put("[#c20#]", "\uD83D\uDCC0");
        sHistoryFace.put("[#c21#]", "\uD83D\uDCF7");
        sHistoryFace.put("[#c22#]", "\uD83C\uDFA5");
        sHistoryFace.put("[#c23#]", "\uD83D\uDCBB");
        sHistoryFace.put("[#c24#]", "\uD83D\uDCFA");
        sHistoryFace.put("[#c25#]", "\uD83D\uDCF1");
        sHistoryFace.put("[#c26#]", "\uD83D\uDCE0");
        sHistoryFace.put("[#c27#]", "\u260E");
        sHistoryFace.put("[#c28#]", "\uD83D\uDCBD");
        sHistoryFace.put("[#c29#]", "\uD83D\uDCFC");
        sHistoryFace.put("[#c30#]", "\uD83D\uDD0A");
        sHistoryFace.put("[#c31#]", "\uD83D\uDCE2");
        sHistoryFace.put("[#c32#]", "\uD83D\uDCE3");
        sHistoryFace.put("[#c33#]", "\uD83D\uDCFB");
        sHistoryFace.put("[#c34#]", "\uD83D\uDCE1");
        sHistoryFace.put("[#c35#]", "\u27BF");
        sHistoryFace.put("[#c36#]", "\uD83D\uDD0D");
        sHistoryFace.put("[#c37#]", "\uD83D\uDD13");
        sHistoryFace.put("[#c38#]", "\uD83D\uDD12");
        sHistoryFace.put("[#c39#]", "\uD83D\uDD11");
        sHistoryFace.put("[#c40#]", "\u2702");
        sHistoryFace.put("[#c41#]", "\uD83D\uDD28");
        sHistoryFace.put("[#c42#]", "\uD83D\uDCA1");
        sHistoryFace.put("[#c43#]", "\uD83D\uDCF2");
        sHistoryFace.put("[#c44#]", "\uD83D\uDCE9");
        sHistoryFace.put("[#c45#]", "\uD83D\uDCEA");
        sHistoryFace.put("[#c46#]", "\uD83D\uDCEE");
        sHistoryFace.put("[#c47#]", "\uD83D\uDEC0");
        sHistoryFace.put("[#c48#]", "\uD83D\uDEBD");
        sHistoryFace.put("[#c49#]", "\uD83D\uDCBA");
        sHistoryFace.put("[#c50#]", "\uD83D\uDCB0");
        sHistoryFace.put("[#c51#]", "\uD83D\uDD31");
        sHistoryFace.put("[#c52#]", "\uD83D\uDEAC");
        sHistoryFace.put("[#c53#]", "\uD83D\uDCA3");
        sHistoryFace.put("[#c54#]", "\uD83D\uDD2B");
        sHistoryFace.put("[#c55#]", "\uD83D\uDC8A");
        sHistoryFace.put("[#c56#]", "\uD83D\uDC89");
        sHistoryFace.put("[#c57#]", "\uD83C\uDFC8");
        sHistoryFace.put("[#c58#]", "\uD83C\uDFC0");
        sHistoryFace.put("[#c59#]", "\u26BD");
        sHistoryFace.put("[#c60#]", "\u26BE");
        sHistoryFace.put("[#c61#]", "\uD83C\uDFBE");
        sHistoryFace.put("[#c62#]", "\u26F3");
        sHistoryFace.put("[#c63#]", "\uD83C\uDFB1");
        sHistoryFace.put("[#c64#]", "\uD83C\uDFCA");
        sHistoryFace.put("[#c65#]", "\uD83C\uDFC4");
        sHistoryFace.put("[#c66#]", "\uD83C\uDFBF");
        sHistoryFace.put("[#c67#]", "\u2660");
        sHistoryFace.put("[#c68#]", "\u2665");
        sHistoryFace.put("[#c69#]", "\u2663");
        sHistoryFace.put("[#c70#]", "\u2666");
        sHistoryFace.put("[#c71#]", "\uD83C\uDFC6");
        sHistoryFace.put("[#c72#]", "\uD83D\uDC7E");
        sHistoryFace.put("[#c73#]", "\uD83C\uDFAF");
        sHistoryFace.put("[#c74#]", "\uD83C\uDC04");
        sHistoryFace.put("[#c75#]", "\uD83C\uDFAC");
        sHistoryFace.put("[#c76#]", "\uD83D\uDCDD");
        sHistoryFace.put("[#c77#]", "\uD83D\uDCD6");
        sHistoryFace.put("[#c78#]", "\uD83C\uDFA8");
        sHistoryFace.put("[#c79#]", "\uD83C\uDFA4");
        sHistoryFace.put("[#c80#]", "\uD83C\uDFA7");
        sHistoryFace.put("[#c81#]", "\uD83C\uDFBA");
        sHistoryFace.put("[#c82#]", "\uD83C\uDFB7");
        sHistoryFace.put("[#c83#]", "\uD83C\uDFB8");
        sHistoryFace.put("[#c84#]", "\u303D");
        sHistoryFace.put("[#c85#]", "\uD83D\uDC5E");
        sHistoryFace.put("[#c86#]", "\uD83D\uDC61");
        sHistoryFace.put("[#c87#]", "\uD83D\uDC60");
        sHistoryFace.put("[#c88#]", "\uD83D\uDC62");
        sHistoryFace.put("[#c89#]", "\uD83D\uDC55");
        sHistoryFace.put("[#c90#]", "\uD83D\uDC54");
        sHistoryFace.put("[#c91#]", "\uD83D\uDC57");
        sHistoryFace.put("[#c92#]", "\uD83D\uDC58");
        sHistoryFace.put("[#c93#]", "\uD83D\uDC59");
        sHistoryFace.put("[#c94#]", "\uD83C\uDF80");
        sHistoryFace.put("[#c95#]", "\uD83C\uDFA9");
        sHistoryFace.put("[#c96#]", "\uD83D\uDC51");
        sHistoryFace.put("[#c97#]", "\uD83D\uDC52");
        sHistoryFace.put("[#c98#]", "\uD83C\uDF02");
        sHistoryFace.put("[#c99#]", "\uD83D\uDCBC");
        sHistoryFace.put("[#c100#]", "\uD83D\uDC5C");
        sHistoryFace.put("[#c101#]", "\uD83D\uDC84");
        sHistoryFace.put("[#c102#]", "\uD83D\uDC8D");
        sHistoryFace.put("[#c103#]", "\uD83D\uDC8E");
        sHistoryFace.put("[#c104#]", "\u2615");
        sHistoryFace.put("[#c105#]", "\uD83C\uDF75");
        sHistoryFace.put("[#c106#]", "\uD83C\uDF7A");
        sHistoryFace.put("[#c107#]", "\uD83C\uDF7B");
        sHistoryFace.put("[#c108#]", "\uD83C\uDF78");
        sHistoryFace.put("[#c109#]", "\uD83C\uDF76");
        sHistoryFace.put("[#c110#]", "\uD83C\uDF74");
        sHistoryFace.put("[#c111#]", "\uD83C\uDF54");
        sHistoryFace.put("[#c112#]", "\uD83C\uDF5F");
        sHistoryFace.put("[#c113#]", "\uD83C\uDF5D");
        sHistoryFace.put("[#c114#]", "\uD83C\uDF5B");
        sHistoryFace.put("[#c115#]", "\uD83C\uDF71");
        sHistoryFace.put("[#c116#]", "\uD83C\uDF63");
        sHistoryFace.put("[#c117#]", "\uD83C\uDF59");
        sHistoryFace.put("[#c118#]", "\uD83C\uDF58");
        sHistoryFace.put("[#c119#]", "\uD83C\uDF5A");
        sHistoryFace.put("[#c120#]", "\uD83C\uDF5C");
        sHistoryFace.put("[#c121#]", "\uD83C\uDF72");
        sHistoryFace.put("[#c122#]", "\uD83C\uDF5E");
        sHistoryFace.put("[#c123#]", "\uD83C\uDF73");
        sHistoryFace.put("[#c124#]", "\uD83C\uDF62");
        sHistoryFace.put("[#c125#]", "\uD83C\uDF61");
        sHistoryFace.put("[#c126#]", "\uD83C\uDF66");
        sHistoryFace.put("[#c127#]", "\uD83C\uDF67");
        sHistoryFace.put("[#c128#]", "\uD83C\uDF82");
        sHistoryFace.put("[#c129#]", "\uD83C\uDF70");
        sHistoryFace.put("[#c130#]", "\uD83C\uDF4E");
        sHistoryFace.put("[#c131#]", "\uD83C\uDF4A");
        sHistoryFace.put("[#c132#]", "\uD83C\uDF49");
        sHistoryFace.put("[#c133#]", "\uD83C\uDF53");
        sHistoryFace.put("[#c134#]", "\uD83C\uDF46");
        sHistoryFace.put("[#c135#]", "\uD83C\uDF45");
        sHistoryFace.put("[#c136#]", "\uD83C\uDF8B");
        sHistoryFace.put("[#c137#]", "\uD83C\uDF8A");
        sHistoryFace.put("[#c138#]", "\uD83D\uDD2E");
        sHistoryFace.put("[#c139#]", "\uD83D\uDCF9");
        sHistoryFace.put("[#c140#]", "\uD83D\uDCBE");
        sHistoryFace.put("[#c141#]", "\uD83D\uDCDE");
        sHistoryFace.put("[#c142#]", "\uD83D\uDCDF");
        sHistoryFace.put("[#c143#]", "\uD83D\uDD09");
        sHistoryFace.put("[#c144#]", "\uD83D\uDD08");
        sHistoryFace.put("[#c145#]", "\uD83D\uDD07");
        sHistoryFace.put("[#c146#]", "\uD83D\uDD15");
        sHistoryFace.put("[#c147#]", "\u23F3");
        sHistoryFace.put("[#c148#]", "\u231B");
        sHistoryFace.put("[#c149#]", "\u23F0");
        sHistoryFace.put("[#c150#]", "\u231A");
        sHistoryFace.put("[#c151#]", "\uD83D\uDD0F");
        sHistoryFace.put("[#c152#]", "\uD83D\uDD10");
        sHistoryFace.put("[#c153#]", "\uD83D\uDD0E");
        sHistoryFace.put("[#c154#]", "\uD83D\uDD26");
        sHistoryFace.put("[#c155#]", "\uD83D\uDD06");
        sHistoryFace.put("[#c156#]", "\uD83D\uDD05");
        sHistoryFace.put("[#c157#]", "\uD83D\uDD0C");
        sHistoryFace.put("[#c158#]", "\uD83D\uDD0B");
        sHistoryFace.put("[#c159#]", "\uD83D\uDEC1");
        sHistoryFace.put("[#c160#]", "\uD83D\uDEBF");
        sHistoryFace.put("[#c161#]", "\uD83D\uDD27");
        sHistoryFace.put("[#c162#]", "\uD83D\uDD29");
        sHistoryFace.put("[#c163#]", "\uD83D\uDEAA");
        sHistoryFace.put("[#c164#]", "\uD83D\uDD2A");
        sHistoryFace.put("[#c165#]", "\uD83D\uDCB4");
        sHistoryFace.put("[#c166#]", "\uD83D\uDCB5");
        sHistoryFace.put("[#c167#]", "\uD83D\uDCB7");
        sHistoryFace.put("[#c168#]", "\uD83D\uDCB6");
        sHistoryFace.put("[#c169#]", "\uD83D\uDCB3");
        sHistoryFace.put("[#c170#]", "\uD83D\uDCB8");
        sHistoryFace.put("[#c171#]", "\uD83D\uDCE7");
        sHistoryFace.put("[#c172#]", "\uD83D\uDCE5");
        sHistoryFace.put("[#c173#]", "\uD83D\uDCE4");
        sHistoryFace.put("[#c174#]", "\u2709");
        sHistoryFace.put("[#c175#]", "\uD83D\uDCE8");
        sHistoryFace.put("[#c176#]", "\uD83D\uDCEF");
        sHistoryFace.put("[#c177#]", "\uD83D\uDCEB");
        sHistoryFace.put("[#c178#]", "\uD83D\uDCEC");
        sHistoryFace.put("[#c179#]", "\uD83D\uDCED");
        sHistoryFace.put("[#c180#]", "\uD83D\uDCE6");
        sHistoryFace.put("[#c181#]", "\uD83D\uDCC4");
        sHistoryFace.put("[#c182#]", "\uD83D\uDCC3");
        sHistoryFace.put("[#c183#]", "\uD83D\uDCD1");
        sHistoryFace.put("[#c184#]", "\uD83D\uDCCA");
        sHistoryFace.put("[#c185#]", "\uD83D\uDCC8");
        sHistoryFace.put("[#c186#]", "\uD83D\uDCC9");
        sHistoryFace.put("[#c187#]", "\uD83D\uDCDC");
        sHistoryFace.put("[#c188#]", "\uD83D\uDCCB");
        sHistoryFace.put("[#c189#]", "\uD83D\uDCC5");
        sHistoryFace.put("[#c190#]", "\uD83D\uDCC6");
        sHistoryFace.put("[#c191#]", "\uD83D\uDCC7");
        sHistoryFace.put("[#c192#]", "\uD83D\uDCC1");
        sHistoryFace.put("[#c193#]", "\uD83D\uDCC2");
        sHistoryFace.put("[#c194#]", "\uD83D\uDCCC");
        sHistoryFace.put("[#c195#]", "\uD83D\uDCCE");
        sHistoryFace.put("[#c196#]", "\u2712");
        sHistoryFace.put("[#c197#]", "\u270F");
        sHistoryFace.put("[#c198#]", "\uD83D\uDCCF");
        sHistoryFace.put("[#c199#]", "\uD83D\uDCD0");
        sHistoryFace.put("[#c200#]", "\uD83D\uDCD5");
        sHistoryFace.put("[#c201#]", "\uD83D\uDCD7");
        sHistoryFace.put("[#c202#]", "\uD83D\uDCD8");
        sHistoryFace.put("[#c203#]", "\uD83D\uDCD9");
        sHistoryFace.put("[#c204#]", "\uD83D\uDCD3");
        sHistoryFace.put("[#c205#]", "\uD83D\uDCD4");
        sHistoryFace.put("[#c206#]", "\uD83D\uDCD2");
        sHistoryFace.put("[#c207#]", "\uD83D\uDCDA");
        sHistoryFace.put("[#c208#]", "\uD83D\uDD16");
        sHistoryFace.put("[#c209#]", "\uD83D\uDCDB");
        sHistoryFace.put("[#c210#]", "\uD83D\uDD2C");
        sHistoryFace.put("[#c211#]", "\uD83D\uDD2D");
        sHistoryFace.put("[#c212#]", "\uD83D\uDCF0");
        sHistoryFace.put("[#c213#]", "\uD83C\uDFBC");
        sHistoryFace.put("[#c214#]", "\uD83C\uDFB9");
        sHistoryFace.put("[#c215#]", "\uD83C\uDFBB");
        sHistoryFace.put("[#c216#]", "\uD83C\uDFAE");
        sHistoryFace.put("[#c217#]", "\uD83C\uDCCF");
        sHistoryFace.put("[#c218#]", "\uD83C\uDFB4");
        sHistoryFace.put("[#c219#]", "\uD83C\uDFB2");
        sHistoryFace.put("[#c220#]", "\uD83C\uDFC9");
        sHistoryFace.put("[#c221#]", "\uD83C\uDFB3");
        sHistoryFace.put("[#c222#]", "\uD83D\uDEB5");
        sHistoryFace.put("[#c223#]", "\uD83D\uDEB4");
        sHistoryFace.put("[#c224#]", "\uD83C\uDFC7");
        sHistoryFace.put("[#c225#]", "\uD83C\uDFC2");
        sHistoryFace.put("[#c226#]", "\uD83C\uDFA3");
        sHistoryFace.put("[#c227#]", "\uD83C\uDF7C");
        sHistoryFace.put("[#c228#]", "\uD83C\uDF79");
        sHistoryFace.put("[#c229#]", "\uD83C\uDF77");
        sHistoryFace.put("[#c230#]", "\uD83C\uDF55");
        sHistoryFace.put("[#c231#]", "\uD83C\uDF57");
        sHistoryFace.put("[#c232#]", "\uD83C\uDF56");
        sHistoryFace.put("[#c233#]", "\uD83C\uDF64");
        sHistoryFace.put("[#c234#]", "\uD83C\uDF65");
        sHistoryFace.put("[#c235#]", "\uD83C\uDF69");
        sHistoryFace.put("[#c236#]", "\uD83C\uDF6E");
        sHistoryFace.put("[#c237#]", "\uD83C\uDF68");
        sHistoryFace.put("[#c238#]", "\uD83C\uDF6A");
        sHistoryFace.put("[#c239#]", "\uD83C\uDF6B");
        sHistoryFace.put("[#c240#]", "\uD83C\uDF6C");
        sHistoryFace.put("[#c241#]", "\uD83C\uDF6D");
        sHistoryFace.put("[#c242#]", "\uD83C\uDF6F");
        sHistoryFace.put("[#c243#]", "\uD83C\uDF4F");
        sHistoryFace.put("[#c244#]", "\uD83C\uDF4B");
        sHistoryFace.put("[#c245#]", "\uD83C\uDF52");
        sHistoryFace.put("[#c246#]", "\uD83C\uDF47");
        sHistoryFace.put("[#c247#]", "\uD83C\uDF51");
        sHistoryFace.put("[#c248#]", "\uD83C\uDF48");
        sHistoryFace.put("[#c249#]", "\uD83C\uDF4C");
        sHistoryFace.put("[#c250#]", "\uD83C\uDF50");
        sHistoryFace.put("[#c251#]", "\uD83C\uDF4D");
        sHistoryFace.put("[#c252#]", "\uD83C\uDF60");
        sHistoryFace.put("[#c253#]", "\uD83C\uDF3D");
        sHistoryFace.put("[#d1#]", "\uD83C\uDFE0");
        sHistoryFace.put("[#d2#]", "\uD83C\uDFEB");
        sHistoryFace.put("[#d3#]", "\uD83C\uDFE2");
        sHistoryFace.put("[#d4#]", "\uD83C\uDFE3");
        sHistoryFace.put("[#d5#]", "\uD83C\uDFE5");
        sHistoryFace.put("[#d6#]", "\uD83C\uDFE6");
        sHistoryFace.put("[#d7#]", "\uD83C\uDFEA");
        sHistoryFace.put("[#d8#]", "\uD83C\uDFE9");
        sHistoryFace.put("[#d9#]", "\uD83C\uDFE8");
        sHistoryFace.put("[#d10#]", "\uD83D\uDC92");
        sHistoryFace.put("[#d11#]", "\u26EA");
        sHistoryFace.put("[#d12#]", "\uD83C\uDFEC");
        sHistoryFace.put("[#d13#]", "\uD83C\uDF07");
        sHistoryFace.put("[#d14#]", "\uD83C\uDF06");
        sHistoryFace.put("[#d15#]", "\uE50A");
        sHistoryFace.put("[#d16#]", "\uD83C\uDFEF");
        sHistoryFace.put("[#d17#]", "\uD83C\uDFF0");
        sHistoryFace.put("[#d18#]", "\u26FA");
        sHistoryFace.put("[#d19#]", "\uD83C\uDFED");
        sHistoryFace.put("[#d20#]", "\uD83D\uDDFC");
        sHistoryFace.put("[#d21#]", "\uD83D\uDDFB");
        sHistoryFace.put("[#d22#]", "\uD83C\uDF04");
        sHistoryFace.put("[#d23#]", "\uD83C\uDF05");
        sHistoryFace.put("[#d24#]", "\uD83C\uDF03");
        sHistoryFace.put("[#d25#]", "\uD83D\uDDFD");
        sHistoryFace.put("[#d26#]", "\uD83C\uDF08");
        sHistoryFace.put("[#d27#]", "\uD83C\uDFA1");
        sHistoryFace.put("[#d28#]", "\u26F2");
        sHistoryFace.put("[#d29#]", "\uD83C\uDFA2");
        sHistoryFace.put("[#d30#]", "\uD83D\uDEA2");
        sHistoryFace.put("[#d31#]", "\uD83D\uDEA4");
        sHistoryFace.put("[#d32#]", "\u26F5");
        sHistoryFace.put("[#d33#]", "\u2708");
        sHistoryFace.put("[#d34#]", "\uD83D\uDE80");
        sHistoryFace.put("[#d35#]", "\uD83D\uDEB2");
        sHistoryFace.put("[#d36#]", "\uD83D\uDE99");
        sHistoryFace.put("[#d37#]", "\uD83D\uDE97");
        sHistoryFace.put("[#d38#]", "\uD83D\uDE95");
        sHistoryFace.put("[#d39#]", "\uD83D\uDE8C");
        sHistoryFace.put("[#d40#]", "\uD83D\uDE93");
        sHistoryFace.put("[#d41#]", "\uD83D\uDE92");
        sHistoryFace.put("[#d42#]", "\uD83D\uDE91");
        sHistoryFace.put("[#d43#]", "\uD83D\uDE9A");
        sHistoryFace.put("[#d44#]", "\uD83D\uDE83");
        sHistoryFace.put("[#d45#]", "\uD83D\uDE89");
        sHistoryFace.put("[#d46#]", "\uD83D\uDE84");
        sHistoryFace.put("[#d47#]", "\uD83D\uDE85");
        sHistoryFace.put("[#d48#]", "\uD83C\uDFAB");
        sHistoryFace.put("[#d49#]", "\u26FD");
        sHistoryFace.put("[#d50#]", "\uD83D\uDEA5");
        sHistoryFace.put("[#d51#]", "\u26A0");
        sHistoryFace.put("[#d52#]", "\uD83D\uDEA7");
        sHistoryFace.put("[#d53#]", "\uD83D\uDD30");
        sHistoryFace.put("[#d54#]", "\uD83C\uDFE7");
        sHistoryFace.put("[#d55#]", "\uD83C\uDFB0");
        sHistoryFace.put("[#d56#]", "\uD83D\uDE8F");
        sHistoryFace.put("[#d57#]", "\uD83D\uDC88");
        sHistoryFace.put("[#d58#]", "\u2668");
        sHistoryFace.put("[#d59#]", "\uD83C\uDFC1");
        sHistoryFace.put("[#d60#]", "\uD83C\uDF8C");
        sHistoryFace.put("[#d61#]", "\uD83C\uDDEF\uD83C\uDDF5");
        sHistoryFace.put("[#d62#]", "\uD83C\uDDF0\uD83C\uDDF7");
        sHistoryFace.put("[#d63#]", "\uD83C\uDDE8\uD83C\uDDF3");
        sHistoryFace.put("[#d64#]", "\uD83C\uDDFA\uD83C\uDDF8");
        sHistoryFace.put("[#d65#]", "\uD83C\uDDEB\uD83C\uDDF7");
        sHistoryFace.put("[#d66#]", "\uD83C\uDDEA\uD83C\uDDF8");
        sHistoryFace.put("[#d67#]", "\uD83C\uDDEE\uD83C\uDDF9");
        sHistoryFace.put("[#d68#]", "\uD83C\uDDF7\uD83C\uDDFA");
        sHistoryFace.put("[#d69#]", "\uD83C\uDDEC\uD83C\uDDE7");
        sHistoryFace.put("[#d70#]", "\uD83C\uDDE9\uD83C\uDDEA");
        sHistoryFace.put("[#d71#]", "\uD83C\uDFE1");
        sHistoryFace.put("[#d72#]", "\uD83C\uDFE4");
        sHistoryFace.put("[#d73#]", "\uD83D\uDDFE");
        sHistoryFace.put("[#d74#]", "\uD83C\uDF09");
        sHistoryFace.put("[#d75#]", "\uD83C\uDFA0");
        sHistoryFace.put("[#d76#]", "\uD83D\uDEA3");
        sHistoryFace.put("[#d77#]", "\u2693");
        sHistoryFace.put("[#d78#]", "\uD83D\uDE81");
        sHistoryFace.put("[#d79#]", "\uD83D\uDE82");
        sHistoryFace.put("[#d80#]", "\uD83D\uDE8A");
        sHistoryFace.put("[#d81#]", "\uD83D\uDE9E");
        sHistoryFace.put("[#d82#]", "\uD83D\uDE86");
        sHistoryFace.put("[#d83#]", "\uD83D\uDE88");
        sHistoryFace.put("[#d84#]", "\uD83D\uDE9D");
        sHistoryFace.put("[#d85#]", "\uD83D\uDE8B");
        sHistoryFace.put("[#d86#]", "\uD83D\uDE8E");
        sHistoryFace.put("[#d87#]", "\uD83D\uDE8D");
        sHistoryFace.put("[#d88#]", "\uD83D\uDE98");
        sHistoryFace.put("[#d89#]", "\uD83D\uDE96");
        sHistoryFace.put("[#d90#]", "\uD83D\uDE9B");
        sHistoryFace.put("[#d91#]", "\uD83D\uDEA8");
        sHistoryFace.put("[#d92#]", "\uD83D\uDE94");
        sHistoryFace.put("[#d93#]", "\uD83D\uDE90");
        sHistoryFace.put("[#d94#]", "\uD83D\uDEA1");
        sHistoryFace.put("[#d95#]", "\uD83D\uDE9F");
        sHistoryFace.put("[#d96#]", "\uD83D\uDEA0");
        sHistoryFace.put("[#d97#]", "\uD83D\uDE9C");
        sHistoryFace.put("[#d98#]", "\uD83D\uDEA6");
        sHistoryFace.put("[#d99#]", "\uD83C\uDFEE");
        sHistoryFace.put("[#d100#]", "\uD83D\uDDFF");
        sHistoryFace.put("[#d101#]", "\uD83C\uDFAA");
        sHistoryFace.put("[#d102#]", "\uD83C\uDFAD");
        sHistoryFace.put("[#d103#]", "\uD83D\uDCCD");
        sHistoryFace.put("[#d104#]", "\uD83D\uDEA9");
        sHistoryFace.put("[#e1#]", "1\u20E3");
        sHistoryFace.put("[#e2#]", "2\u20E3");
        sHistoryFace.put("[#e3#]", "3\u20E3");
        sHistoryFace.put("[#e4#]", "4\u20E3");
        sHistoryFace.put("[#e5#]", "5\u20E3");
        sHistoryFace.put("[#e6#]", "6\u20E3");
        sHistoryFace.put("[#e7#]", "7\u20E3");
        sHistoryFace.put("[#e8#]", "8\u20E3");
        sHistoryFace.put("[#e9#]", "9\u20E3");
        sHistoryFace.put("[#e10#]", "0\u20E3");
        sHistoryFace.put("[#e11#]", "#\u20E3");
        sHistoryFace.put("[#e12#]", "\u2B06");
        sHistoryFace.put("[#e13#]", "\u2B07");
        sHistoryFace.put("[#e14#]", "\u2B05");
        sHistoryFace.put("[#e15#]", "\u27A1");
        sHistoryFace.put("[#e16#]", "\u2197");
        sHistoryFace.put("[#e17#]", "\u2196");
        sHistoryFace.put("[#e18#]", "\u2198");
        sHistoryFace.put("[#e19#]", "\u2199");
        sHistoryFace.put("[#e20#]", "\u25C0");
        sHistoryFace.put("[#e21#]", "\u25B6");
        sHistoryFace.put("[#e22#]", "\u23EA");
        sHistoryFace.put("[#e23#]", "\u23E9");
        sHistoryFace.put("[#e24#]", "\uD83C\uDD97");
        sHistoryFace.put("[#e25#]", "\uD83C\uDD95");
        sHistoryFace.put("[#e26#]", "\uD83D\uDD1D");
        sHistoryFace.put("[#e27#]", "\uD83C\uDD99");
        sHistoryFace.put("[#e28#]", "\uD83C\uDD92");
        sHistoryFace.put("[#e29#]", "\uD83C\uDFA6");
        sHistoryFace.put("[#e30#]", "\uD83C\uDE01");
        sHistoryFace.put("[#e31#]", "\uD83D\uDCF6");
        sHistoryFace.put("[#e32#]", "\uD83C\uDE35");
        sHistoryFace.put("[#e33#]", "\uD83C\uDE33");
        sHistoryFace.put("[#e34#]", "\uD83C\uDE50");
        sHistoryFace.put("[#e35#]", "\uD83C\uDE39");
        sHistoryFace.put("[#e36#]", "\uD83C\uDE2F");
        sHistoryFace.put("[#e37#]", "\uD83C\uDE3A");
        sHistoryFace.put("[#e38#]", "\uD83C\uDE36");
        sHistoryFace.put("[#e39#]", "\uD83C\uDE1A");
        sHistoryFace.put("[#e40#]", "\uD83C\uDE37");
        sHistoryFace.put("[#e41#]", "\uD83C\uDE38");
        sHistoryFace.put("[#e42#]", "\uD83C\uDE02");
        sHistoryFace.put("[#e43#]", "\uD83D\uDEBB");
        sHistoryFace.put("[#e44#]", "\uD83D\uDEB9");
        sHistoryFace.put("[#e45#]", "\uD83D\uDEBA");
        sHistoryFace.put("[#e46#]", "\uD83D\uDEBC");
        sHistoryFace.put("[#e47#]", "\uD83D\uDEAD");
        sHistoryFace.put("[#e48#]", "\uD83C\uDD7F");
        sHistoryFace.put("[#e49#]", "\u267F");
        sHistoryFace.put("[#e50#]", "\uD83D\uDE87");
        sHistoryFace.put("[#e51#]", "\uD83D\uDEBE");
        sHistoryFace.put("[#e52#]", "\u3299");
        sHistoryFace.put("[#e53#]", "\u3297");
        sHistoryFace.put("[#e54#]", "\uD83D\uDD1E");
        sHistoryFace.put("[#e55#]", "\uD83C\uDD94");
        sHistoryFace.put("[#e56#]", "\u2733");
        sHistoryFace.put("[#e57#]", "\u2734");
        sHistoryFace.put("[#e58#]", "\uD83D\uDC9F");
        sHistoryFace.put("[#e59#]", "\uD83C\uDD9A");
        sHistoryFace.put("[#e60#]", "\uD83D\uDCF3");
        sHistoryFace.put("[#e61#]", "\uD83D\uDCF4");
        sHistoryFace.put("[#e62#]", "\uD83D\uDCB9");
        sHistoryFace.put("[#e63#]", "\uD83D\uDCB1");
        sHistoryFace.put("[#e64#]", "\u2648");
        sHistoryFace.put("[#e65#]", "\u2649");
        sHistoryFace.put("[#e66#]", "\u264A");
        sHistoryFace.put("[#e67#]", "\u264B");
        sHistoryFace.put("[#e68#]", "\u264C");
        sHistoryFace.put("[#e69#]", "\u264D");
        sHistoryFace.put("[#e70#]", "\u264E");
        sHistoryFace.put("[#e71#]", "\u264F");
        sHistoryFace.put("[#e72#]", "\u2650");
        sHistoryFace.put("[#e73#]", "\u2651");
        sHistoryFace.put("[#e74#]", "\u2652");
        sHistoryFace.put("[#e75#]", "\u2653");
        sHistoryFace.put("[#e76#]", "\u26CE");
        sHistoryFace.put("[#e77#]", "\uD83D\uDD2F");
        sHistoryFace.put("[#e78#]", "\uD83C\uDD70");
        sHistoryFace.put("[#e79#]", "\uD83C\uDD71");
        sHistoryFace.put("[#e80#]", "\uD83C\uDD8E");
        sHistoryFace.put("[#e81#]", "\uD83C\uDD7E");
        sHistoryFace.put("[#e82#]", "\uD83D\uDD32");
        sHistoryFace.put("[#e83#]", "\uD83D\uDD34");
        sHistoryFace.put("[#e84#]", "\uD83D\uDD33");
        sHistoryFace.put("[#e85#]", "\uD83D\uDD5B");
        sHistoryFace.put("[#e86#]", "\uD83D\uDD50");
        sHistoryFace.put("[#e87#]", "\uD83D\uDD51");
        sHistoryFace.put("[#e88#]", "\uD83D\uDD52");
        sHistoryFace.put("[#e89#]", "\uD83D\uDD53");
        sHistoryFace.put("[#e90#]", "\uD83D\uDD54");
        sHistoryFace.put("[#e91#]", "\uD83D\uDD55");
        sHistoryFace.put("[#e92#]", "\uD83D\uDD56");
        sHistoryFace.put("[#e93#]", "\uD83D\uDD57");
        sHistoryFace.put("[#e94#]", "\uD83D\uDD58");
        sHistoryFace.put("[#e95#]", "\uD83D\uDD59");
        sHistoryFace.put("[#e96#]", "\uD83D\uDD5A");
        sHistoryFace.put("[#e97#]", "\u2B55");
        sHistoryFace.put("[#e98#]", "\u274C");
        sHistoryFace.put("[#e99#]", "\u00A9");
        sHistoryFace.put("[#e100#]", "\u00AE");
        sHistoryFace.put("[#e101#]", "\u2122");
        sHistoryFace.put("[#e102#]", "\uD83D\uDD1F");
        sHistoryFace.put("[#e103#]", "\uD83D\uDD22");
        sHistoryFace.put("[#e104#]", "\uD83D\uDD23");
        sHistoryFace.put("[#e105#]", "\uD83D\uDD20");
        sHistoryFace.put("[#e106#]", "\uD83D\uDD21");
        sHistoryFace.put("[#e107#]", "\uD83D\uDD24");
        sHistoryFace.put("[#e108#]", "\u2194");
        sHistoryFace.put("[#e109#]", "\u2195");
        sHistoryFace.put("[#e110#]", "\uD83D\uDD04");
        sHistoryFace.put("[#e111#]", "\uD83D\uDD3C");
        sHistoryFace.put("[#e112#]", "\uD83D\uDD3D");
        sHistoryFace.put("[#e113#]", "\u21A9");
        sHistoryFace.put("[#e114#]", "\u21AA");
        sHistoryFace.put("[#e115#]", "\u2139");
        sHistoryFace.put("[#e116#]", "\u23EB");
        sHistoryFace.put("[#e117#]", "\u23EC");
        sHistoryFace.put("[#e118#]", "\u2935");
        sHistoryFace.put("[#e119#]", "\u2934");
        sHistoryFace.put("[#e120#]", "\uD83D\uDD00");
        sHistoryFace.put("[#e121#]", "\uD83D\uDD01");
        sHistoryFace.put("[#e122#]", "\uD83D\uDD02");
        sHistoryFace.put("[#e123#]", "\uD83C\uDD93");
        sHistoryFace.put("[#e124#]", "\uD83C\uDD96");
        sHistoryFace.put("[#e125#]", "\uD83C\uDE34");
        sHistoryFace.put("[#e126#]", "\uD83C\uDE32");
        sHistoryFace.put("[#e127#]", "\uD83D\uDEB0");
        sHistoryFace.put("[#e128#]", "\uD83D\uDEAE");
        sHistoryFace.put("[#e129#]", "\u24C2");
        sHistoryFace.put("[#e130#]", "\uD83D\uDEC2");
        sHistoryFace.put("[#e131#]", "\uD83D\uDEC4");
        sHistoryFace.put("[#e132#]", "\uD83D\uDEC5");
        sHistoryFace.put("[#e133#]", "\uD83D\uDEC3");
        sHistoryFace.put("[#e134#]", "\uD83C\uDE51");
        sHistoryFace.put("[#e135#]", "\uD83C\uDD91");
        sHistoryFace.put("[#e136#]", "\uD83C\uDD98");
        sHistoryFace.put("[#e137#]", "\uD83D\uDEAB");
        sHistoryFace.put("[#e138#]", "\uD83D\uDCF5");
        sHistoryFace.put("[#e139#]", "\uD83D\uDEAF");
        sHistoryFace.put("[#e140#]", "\uD83D\uDEB1");
        sHistoryFace.put("[#e141#]", "\uD83D\uDEB3");
        sHistoryFace.put("[#e142#]", "\uD83D\uDEB7");
        sHistoryFace.put("[#e143#]", "\uD83D\uDEB8");
        sHistoryFace.put("[#e144#]", "\u26D4");
        sHistoryFace.put("[#e145#]", "\u2747");
        sHistoryFace.put("[#e146#]", "\u274E");
        sHistoryFace.put("[#e147#]", "\u2705");
        sHistoryFace.put("[#e148#]", "\uD83D\uDCA0");
        sHistoryFace.put("[#e149#]", "\u267B");
        sHistoryFace.put("[#e150#]", "\uD83D\uDCB2");
        sHistoryFace.put("[#e151#]", "\u203C");
        sHistoryFace.put("[#e152#]", "\u2049");
        sHistoryFace.put("[#e153#]", "\u2755");
        sHistoryFace.put("[#e154#]", "\u2754");
        sHistoryFace.put("[#e155#]", "\uD83D\uDD1A");
        sHistoryFace.put("[#e156#]", "\uD83D\uDD19");
        sHistoryFace.put("[#e157#]", "\uD83D\uDD1B");
        sHistoryFace.put("[#e158#]", "\uD83D\uDD1C");
        sHistoryFace.put("[#e159#]", "\uD83D\uDD03");
        sHistoryFace.put("[#e160#]", "\uD83D\uDD67");
        sHistoryFace.put("[#e161#]", "\uD83D\uDD5C");
        sHistoryFace.put("[#e162#]", "\uD83D\uDD5D");
        sHistoryFace.put("[#e163#]", "\uD83D\uDD5E");
        sHistoryFace.put("[#e164#]", "\uD83D\uDD5F");
        sHistoryFace.put("[#e165#]", "\uD83D\uDD60");
        sHistoryFace.put("[#e166#]", "\uD83D\uDD61");
        sHistoryFace.put("[#e167#]", "\uD83D\uDD62");
        sHistoryFace.put("[#e168#]", "\uD83D\uDD63");
        sHistoryFace.put("[#e169#]", "\uD83D\uDD64");
        sHistoryFace.put("[#e170#]", "\uD83D\uDD65");
        sHistoryFace.put("[#e171#]", "\uD83D\uDD66");
        sHistoryFace.put("[#e172#]", "\u2716");
        sHistoryFace.put("[#e173#]", "\u2795");
        sHistoryFace.put("[#e174#]", "\u2796");
        sHistoryFace.put("[#e175#]", "\u2797");
        sHistoryFace.put("[#e176#]", "\uD83D\uDCAE");
        sHistoryFace.put("[#e177#]", "\uD83D\uDCAF");
        sHistoryFace.put("[#e178#]", "\u2714");
        sHistoryFace.put("[#e179#]", "\u2611");
        sHistoryFace.put("[#e180#]", "\uD83D\uDD18");
        sHistoryFace.put("[#e181#]", "\uD83D\uDD17");
        sHistoryFace.put("[#e182#]", "\u27B0");
        sHistoryFace.put("[#e183#]", "\u3030");
        sHistoryFace.put("[#e184#]", "\u25FC");
        sHistoryFace.put("[#e185#]", "\u25FB");
        sHistoryFace.put("[#e186#]", "\u25FE");
        sHistoryFace.put("[#e187#]", "\u25FD");
        sHistoryFace.put("[#e188#]", "\u25AA");
        sHistoryFace.put("[#e189#]", "\u25AB");
        sHistoryFace.put("[#e190#]", "\uD83D\uDD3A");
        sHistoryFace.put("[#e191#]", "\u26AB");
        sHistoryFace.put("[#e192#]", "\u26AA");
        sHistoryFace.put("[#e193#]", "\uD83D\uDD35");
        sHistoryFace.put("[#e194#]", "\uD83D\uDD3B");
        sHistoryFace.put("[#e195#]", "\u2B1C");
        sHistoryFace.put("[#e196#]", "\u2B1B");
        sHistoryFace.put("[#e197#]", "\uD83D\uDD36");
        sHistoryFace.put("[#e198#]", "\uD83D\uDD37");
        sHistoryFace.put("[#e199#]", "\uD83D\uDD38");
        sHistoryFace.put("[#e200#]", "\uD83D\uDD39");

    }

    public static class FaceIcon implements Comparable<FaceIcon> {
        public String key;
        public String iconPath;
        public int pkgId;
        public int iconId;
        public String description;
        public Integer sortIndex;

        public void setSortIndex(Integer sortIndex) {
            this.sortIndex = sortIndex;
        }

        public Integer getSortIndex() {
            return sortIndex;
        }

        public int compareTo(FaceIcon arg0) {
            return this.sortIndex.compareTo(arg0.getSortIndex());
        }
    }

    public static class FaceLogoIcon implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = -9176887479466911267L;
        public String key;
        public String iconPath;
        public int pkgId;
        public int feetType;
        public String description;
        // zhonglong 2013-10-17新增表情是否有效
        public int valid = 1;

        public FaceLogoIcon() {
        }

        public FaceLogoIcon(int pkgId) {
            this.pkgId = pkgId;
        }

    }

    public static String toJsonString(Map<String, FaceLogoIcon> param) {

        StringBuilder sb = new StringBuilder();
        sb.append("{\"items\":[");
        if (param != null) {
            int i = 0;
            for (Entry<String, FaceLogoIcon> entry : param.entrySet()) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append("{");
                sb.append("\"iconPath\":\"" + entry.getValue().iconPath + "\",");

                sb.append("\"key\":\"" + entry.getValue().key + "\",");

                sb.append("\"pkgId\":" + entry.getValue().pkgId + ",");
                sb.append("\"feetType\":" + entry.getValue().feetType + ",");
                sb.append("\"description\":" + entry.getValue().description);
                sb.append("}");

                i++;
            }

        }
        sb.append("]}");

        return sb.toString();
    }

    // 将包含小猫表情[#xxx#]的字符串转成 文本[xxx]的字符串,主要用于负责文本和粘贴文本
    public static String catFace2Text(Context context, String source) {
        String result = source;

        if (sCatFace2Str == null) {
            String[] catArr = context.getResources().getStringArray(R.array.CatCode);
            sCatFace2Str = new LinkedHashMap<String, String>();
            for (int i = 0; i < catArr.length; i++) {
                String[] subStr = catArr[i].split("@");
                if (subStr != null && subStr.length > 1) {
                    sCatFace2Str.put(subStr[0], subStr[1]);
                }
            }
        }
        Pattern emojiP = Pattern.compile(emojoRegex);// 匹配[# #]
        Matcher emojim = emojiP.matcher(source);// 开始编译

        while (emojim.find()) {
            String str = emojim.group();
            String icon = sCatFace2Str.get(str);
            if (icon != null && result.contains(str)) {
                result = result.replace(str, icon);
            }

        }

        return result;
    }

    /**
     * 计算字符串中包括表情的长度
     *
     * @param value
     * @return
     */
    public static int calculateLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";// 汉字
        Object[] objects = replaceFace(value);
        int faceNum = (Integer) objects[0];
        String replaceAfterValue = String.valueOf(objects[1]);
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < replaceAfterValue.length(); i++) {
            /* 获取一个字符 */
            String temp = replaceAfterValue.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为1 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return
                value.length() > valueLength + (faceNum * 4) ?
                        valueLength + (faceNum * 4) : value.length();
    }

    public static Object[] replaceFace(String str) {
        String regex = FaceManager.regex;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        int cishu = 0;
        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
            cishu++;
        }
        matcher.appendTail(sb);

        return new Object[]{Integer.valueOf(cishu), sb.toString()};
    }

    public static boolean checkStrIncludeFace(String str) {
        String regex = FaceManager.regex;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        int faceNum = 0;
        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
            faceNum++;
        }
        CommonFunction.log("FaceManager", "faceNum***" + faceNum);
        return faceNum != 0;
    }

    /**
     * 返回替换表情后的SpannableString
     */
    public SpannableString iconReplacedSpannableString(Context context, String text,
                                                       int faceSizeDp) {
        if (CommonFunction.isEmptyOrNullStr(text)) {
            return new SpannableString("");
        }

        try {
            String strSource = new String(text);

            FaceManager faceManager = FaceManager.getInstance(context.getApplicationContext());
            Resources res = context.getResources();
            float faceSize = res.getDimension(R.dimen.face_height); // 默认值为24dp
            if (faceSizeDp > 0) {
                float oneDp = res.getDimension(R.dimen.dp_1);
                faceSize = faceSizeDp * oneDp;
            }

            SpannableString spannable = null;
            Pattern emojiP = Pattern.compile(emojoRegex);// 匹配[# #]
            Matcher emojim = emojiP.matcher(strSource);// 开始编译

            while (emojim.find()) {//将以前emojim的格式替换成字符集里面emojim的字符
                String str = emojim.group();
                String icon = sHistoryFace.get(str);
                if (icon != null && strSource.contains(str)) {
                    strSource = strSource.replace(str, icon);
                }

            }


            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(strSource);// 开始编译
            spannable = new SpannableString(strSource.toString());

            while (m.find()) {
                // String icon = m.group( );

                String str = m.group();
                String icon = "";
                if (str.startsWith(catFlag) && str.endsWith(catFlagright)) {
                    icon = str;
                } else {

                    for (int i = 0; i < str.length(); i++) {
                        char c = str.charAt(i);
                        int v = (int) c;
                        icon += Integer.toHexString(v);
                    }
                }


                // 查找对应的图标
                Drawable drawable = faceManager.getDrawableWithFaceKey(context, icon);
                if (drawable != null) {
                    drawable.setBounds(0, 0, (int) faceSize, (int) faceSize);
                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    spannable.setSpan(span, Math.max(0, m.start()),
                            Math.min(spannable.length(), m.end()),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                m.groupCount();
            }


            p = Pattern.compile(catRegex, Pattern.UNICODE_CASE);
            m = p.matcher(strSource);// 开始编译

            while (m.find()) {
                String str = m.group();
                // 查找对应的图标
                Drawable drawable = faceManager.getDrawableWithCatKey(context, str);

                if (drawable != null) {
                    drawable.setBounds(0, 0, (int) faceSize, (int) faceSize);
                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    spannable
                            .setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                m.groupCount();
            }

            return spannable;
        } catch (Exception e) {
            e.printStackTrace();
            return (new SpannableString(text.toString()));
        }
    }

}
