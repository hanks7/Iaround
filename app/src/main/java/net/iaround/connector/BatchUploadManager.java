
package net.iaround.connector;


import android.content.Context;
import android.text.TextUtils;
import android.util.LongSparseArray;

import net.iaround.BaseApplication;
import net.iaround.conf.Config;
import net.iaround.connector.FileUploadManager.FileProfix;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.JsonUtil;
import net.iaround.tools.MD5Utility;
import net.iaround.tools.PathUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-11-25 下午1:02:02
 * @Description 批量图片上传管理器, 把传入的图片上传, 完成后返会flag和上传成功的图片list<br />
 * <b>参数:</b><br/>
 * flag: 用于标识属于哪个任务的,返回的flag <br/>
 * photoList: 上传的图片List<br/>
 * uploadType: 上传的图片属于哪个类型 @FileUploadType<br/>
 * BatchUploadCallBack: 上传成功后回调<br/>
 */
public class BatchUploadManager implements UploadFileCallback {
    private Context mContext;
    private BatchUploadCallBack callback;

    //记录图片任务flag以及对应的图片的list
    protected LongSparseArray<ArrayList<String>> taskArray = new LongSparseArray<ArrayList<String>>();
    // 记录传进来任务的falg和对应图片任务flagflag
    protected LongSparseArray<Long> imageFlagArray = new LongSparseArray<Long>();

    protected HashMap<Long, Long> tmpMap;
    /**
     * 因为最多只能发6张图片,所以用3位就足够,[0111] 0~7,第一张图片为0；
     */
    public static long IMAGE_TASK_MASK = Long.MAX_VALUE ^ 7;// 值为[111111......000]
    protected long IMAGE_POSITION_MASK = 7;// Image对应的位置的Mask 值为[000000......111]

    private int uploadType;

    public BatchUploadManager(Context context) {
        mContext = context;
    }

    // 上传图片
    public void uploadImage(final long flag, ArrayList<String> photoList, final int uploadType,
                            final BatchUploadCallBack callback) throws Exception {
        this.callback = callback;
        this.uploadType = uploadType;
        long imageTaskFlag = flag & IMAGE_TASK_MASK;//计算照片上传的任务flag
        imageFlagArray.put(flag, imageTaskFlag);

        // 把要上传的图片作为一个任务,放到taskArray
        int imageCount = photoList.size();
        ArrayList<String> imageList = new ArrayList<String>(imageCount);
        boolean isNeedUpload = false;//是否不需要上传图片,也就是所有的图片都是服务端的图片
        for (int i = 0; i < imageCount; i++) {
            String imageUrl = photoList.get(i);
            imageList.add(imageUrl);

            if (!isNeedUpload && !imageUrl.contains(PathUtil.getHTTPPrefix())) {
                isNeedUpload = true;
            }
        }
        taskArray.put(imageTaskFlag, imageList);

        if (!isNeedUpload)//如果所有图片都是不需要上传的,那么就直接调用上传成功的方法
        {
            callback.batchUploadSuccess(flag, photoList);
            return;
        }

        //以下进入上传图片的过程
        //生成图片上传需要的参数,key和type
        final Map<String, String> map = new HashMap<String, String>();
        map.put("key", ConnectorManage.getInstance(mContext).getKey());
        map.put("type", String.valueOf(uploadType));

        for (int i = 0; i < imageCount; i++) {
            final long imagePostionFlag = imageTaskFlag + i;
            String imageUrl = photoList.get(i);

            //如果该图片本身就是服务端的图片,就不需要上传
            if (imageUrl.contains(PathUtil.getHTTPPrefix())) {
                continue;
            }

//			// 启动文件上传线程
//			File uploadFile = new File( imageUrl );
//			//计算上传的文件的md5 值
//			CommonFunction.log( "shifengxiong","MD5Utility Start ==="+ System.currentTimeMillis() );
//			if (uploadFile.exists( ) )
//			{
//				String md5String =  MD5Utility.getFileMD5( uploadFile );
//				CommonFunction.log( "shifengxiong","MD5Utility ==="+md5String );
//				CommonFunction.log( "shifengxiong","MD5Utility end ==="+ System.currentTimeMillis() );
//			}
//
//			// 对图片进行质量压缩处理
//			try
//			{
//				String compressPath = CommonFunction.compressImage(imageUrl);
//				if ( !TextUtils.isEmpty( compressPath ) )
//				{
//					uploadFile = new File( compressPath );
//				}
//			}
//			catch ( Exception e )
//			{
//				e.printStackTrace( );
//			}
//
//			if ( !uploadFile.exists( ) )
//			{
//				callback.batchUploadFail(flag);
//				return;
//			}
//
//			FileUploadManager.createUploadTask( mContext , uploadFile.getAbsolutePath() , FileProfix.JPG ,
//					Config.sPictureHost , map , this , imagePostionFlag ).start( );


            uploadPic(imageUrl, flag, imagePostionFlag, map);
        }
    }


    private void uploadPic(final String imageUrl, final long flag, final long imagePostionFlag, final Map<String, String> map) {
        Luban.with(BaseApplication.appContext)
                .load(imageUrl)                                   // 传人要压缩的图片列表
                .ignoreBy(300)                                  // 忽略不压缩图片的大小
                .setTargetDir(PathUtil.getPictureDir())                        // 设置压缩后文件存储位置
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        if (!file.exists()) {
                            callback.batchUploadFail(flag);
                            return;
                        }

                        try {
                            CommonFunction.log("hanggao", " getSize = " + file.length());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        CommonFunction.log("hanggao", "getAbsolutePath =" + file.getAbsolutePath());
                        CommonFunction.log("hanggao", "imagePostionFlag = " + imagePostionFlag + "    key=" + ConnectorManage.getInstance(BaseApplication.appContext).getKey() + "    type=" + uploadType);
                        FileUploadManager.createUploadTask(mContext, file.getAbsolutePath(), FileProfix.JPG,
                                Config.sPictureHost, map, BatchUploadManager.this, imagePostionFlag).start();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                        callback.batchUploadFail(flag);
                    }
                }).launch();    //启动压缩
    }


    @Override
    public void onUploadFileProgress(int lengthOfUploaded, long flag) {
    }

    @Override
    public void onUploadFileFinish(long flag, String result) {
        CommonFunction.log("hanggao", "flag = " + flag + "    result" + result);
        Map<String, Object> map = null;
        map = JsonUtil.jsonToMap(result);
        String serverUrl = String.valueOf(map.get("url"));
        int error = 0;
        if (map.containsKey("error")) {
            error = (Integer) map.get("error");
        }

        long imageTaskFlag = flag & IMAGE_TASK_MASK;
        int imagePosition = (int) (flag & IMAGE_POSITION_MASK);

        int taskFlagValueIndex = imageFlagArray.indexOfValue(imageTaskFlag);
        ArrayList<String> imageList = taskArray.get(imageTaskFlag);

        if (imageList != null) {
            int count = imageList.size();
            if (imagePosition < count) {
                imageList.set(imagePosition, serverUrl);

            }
        }

        for (int i = 0; i < imageFlagArray.size(); i++) {
            if (imageFlagArray.valueAt(i).longValue() == imageTaskFlag) {
                taskFlagValueIndex = i;
                break;
            }
        }

        // 上传完成,发布
        if (bIsFileUploadFinsh(imageTaskFlag)) {
            // 上传成功
            //获取的keyIndex返回为-1?
//			int taskFlagValueIndex = imageFlagArray.indexOfValue( imageTaskFlag );


            if (taskFlagValueIndex < 0) {
                throw new ArrayIndexOutOfBoundsException("找不到对应的图片任务");
            } else {
                long taskFlag = imageFlagArray.keyAt(taskFlagValueIndex);//通过图片任务的flag,得到传进来的flag
                if (callback != null) {
                    callback.batchUploadSuccess(taskFlag, taskArray.get(imageTaskFlag));
                }

            }
        }
        if (error > 0) {
            if (taskFlagValueIndex < 0) {
                throw new ArrayIndexOutOfBoundsException("找不到对应的图片任务");
            } else {
                long taskFlag = imageFlagArray.keyAt(taskFlagValueIndex);//通过图片任务的flag,得到传进来的flag
                if (callback != null) {
                    callback.batchUploadFail(taskFlag);
                }

            }
        }
    }

    @Override
    public void onUploadFileError(String e, long flag) {
        long imageTaskFlag = flag & IMAGE_TASK_MASK;//计算该图片属于哪个图片上传的任务flag
        // 上传失败,返回taskFlag
        //获取的keyIndex返回为-1?
        int taskFlagValueIndex = imageFlagArray.indexOfValue(imageTaskFlag);
        for (int i = 0; i < imageFlagArray.size(); i++) {
            if (imageFlagArray.valueAt(i).longValue() == imageTaskFlag) {
                taskFlagValueIndex = i;
                break;
            }
        }
        if (taskFlagValueIndex < 0) {
            throw new ArrayIndexOutOfBoundsException("找不到对应的图片任务");
        } else {
            long taskFlag = imageFlagArray.keyAt(taskFlagValueIndex);//通过图片任务的flag,得到传进来的flag
            callback.batchUploadFail(taskFlag);
        }
    }

    // 判断文件上传是否完成
    private boolean bIsFileUploadFinsh(long imageTaskFlag) {
        ArrayList<String> imgTask = taskArray.get(imageTaskFlag);
        if (imgTask == null) {
            return false;
        }

        int count = imgTask.size();
        for (int i = 0; i < count; i++) {
            String url = imgTask.get(i);
            if (!url.contains(PathUtil.getHTTPPrefix())) {
                return false;
            }
        }
        return true;
    }

    public interface BatchUploadCallBack {

        /**
         * 批量上传成功 serverUrlList: 服务端下发的url
         */
        void batchUploadSuccess(long taskFlag, ArrayList<String> serverUrlList);

        /**
         * 批量上传失败 taskFlag上传任务的flag
         */
        void batchUploadFail(long taskFlag);
    }
}
