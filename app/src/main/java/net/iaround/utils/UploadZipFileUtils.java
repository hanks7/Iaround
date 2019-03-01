package net.iaround.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.http.multipart.Part;
import com.android.internal.http.multipart.FilePart;
import net.android.volley.AuthFailureError;
import net.android.volley.Request;
import net.android.volley.RequestQueue;
import net.android.volley.Response;
import net.android.volley.VolleyError;
import net.android.volley.VolleyLog;
import net.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.android.volley.toolbox.Volley;
import net.iaround.BaseApplication;
import net.iaround.model.entity.ObtainInfoBean;
import net.iaround.tools.CommonFunction;
import net.iaround.utils.logger.Logger;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * upload dir: crash log 日志
 * Created by liangyuanhuan on 24/10/2017.
 */

public class UploadZipFileUtils {
    public static final String TAG = "UploadZipFileUtils";

    public interface IUploadListener{
        public void onUploadResult(int state);
    }

    public static void upload(IUploadListener listener, String user, String url, String token){
        UploadWorkThread thread = new UploadWorkThread(listener,user, url,token);
        thread.start();
    }

    static class UploadWorkThread extends Thread{
        private WeakReference<IUploadListener> mListener;
        private String mUrl; //上传接口
        private String mUser; //上传用户
        private String logFileName;
        private String crashFileName;
        private String mSDPath;
        private Object mUploadFinishSignal = new Object();
        private String mToken;
        private boolean mUploadFinish = false;

        public UploadWorkThread(IUploadListener listener,String user, String url, String token){
            mListener = new WeakReference<IUploadListener>(listener);
            mUrl = url;
            mUser = user;
            mToken = token;
        }

        @Override
        public void run() {
            CommonFunction.log(TAG, "run() start");
            mSDPath = CommonFunction.getSDPath();
            if(TextUtils.isEmpty(mSDPath)){
                IUploadListener listener = mListener.get();
                if(listener==null){
                    return;
                }
                listener.onUploadResult(-1);
                return;
            }

            SimpleDateFormat form = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date date = new Date();
            String ds = form.format(date);
            logFileName = mUser+"_"+ds+"_"+"log.zip";
            crashFileName = mUser+"_"+ds+"_"+"crash.zip";

            //将缓存日志刷新到文件中
            Logger.flush(true);

            //压缩日志
            CommonFunction.log(TAG, "run() to zip file");
            if( zipFile() ==false ){
                IUploadListener listener = mListener.get();
                if(listener==null){
                    return;
                }
                listener.onUploadResult(-1);
                return;
            }

            //上传日志
            CommonFunction.log(TAG, "run() to upload file");
            if( uploadFile() == false ){
                CommonFunction.log(TAG, "run() upload result fail");
                return;
            }else{
                CommonFunction.log(TAG, "run() upload result success");
            }

            CommonFunction.log(TAG, "run() end");
        }

        private boolean zipFile(){
            try {
                CommonFunction.log(TAG, "zipFile() start");
                String logFilePath = mSDPath + "log";
                String crashFilePath = mSDPath + "crash";

                String tempFilePath = mSDPath + "temp";
                File tempFile = new File(tempFilePath);
                if(tempFile.exists()==false){
                    tempFile.mkdir();
                }

                CommonFunction.log(TAG, "zipFile() log file path=" + logFilePath +", temp file path=" + tempFilePath);
                ZipUtil.zip(logFilePath,tempFilePath,logFileName);
                ZipUtil.zip(crashFilePath,tempFilePath,crashFileName);
                CommonFunction.log(TAG, "zipFile() end");
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        private boolean uploadFile(){
            //log
            String tempFileName = mSDPath + "temp" +  "/"+ logFileName;
            File tempFile = null;
            try {
                tempFile = new File(tempFileName);
                CommonFunction.log(TAG, "uploadFile() log file length=" + tempFile.length());
                if(tempFile.length()>0) {
                    CommonFunction.log(TAG, "uploadFile() upload log start");
                    if (upload(tempFileName) == false) {
                        Log.d(TAG, "uploadFile() upload log fail");
                        return false;
                    }
                    synchronized (mUploadFinishSignal) {
                        while (mUploadFinish == false) {
                            mUploadFinishSignal.wait();
                        }
                        mUploadFinish = false;
                    }
                    CommonFunction.log(TAG, "uploadFile() upload log end");
                }else {
                    CommonFunction.log(TAG, "uploadFile() log file length 0");
                }
            }catch (Exception e){
                CommonFunction.log(TAG, "uploadFile() upload log error");
                e.printStackTrace();
                return false;
            }finally {
                try{
                    if(tempFile!=null&&tempFile.exists()){
                        tempFile.delete();
                    }
                }catch (Exception e){

                }
            }

            //crash
            String tempFileName2 = mSDPath + "temp" +  "/"+ crashFileName;
            File tempFile2 = null;
            try {
                tempFile2 = new File(tempFileName2);
                CommonFunction.log(TAG, "uploadFile() crash file length=" + tempFile2.length());
                if(tempFile2.length()>0) {
                    CommonFunction.log(TAG, "uploadFile() crash start");
                    if (upload(tempFileName2) == false) {
                        CommonFunction.log(TAG, "uploadFile() upload crash fail");
                        return false;
                    }
                    synchronized (mUploadFinishSignal) {
                        while (mUploadFinish == false) {
                            mUploadFinishSignal.wait();
                        }
                    }
                    CommonFunction.log(TAG, "uploadFile() crash end");
                }else{
                    CommonFunction.log(TAG, "uploadFile() crash file length 0");
                }
                return true;
            }catch (Exception e){
                CommonFunction.log(TAG, "uploadFile() upload crash error");
                e.printStackTrace();
                return false;
            }finally {
                try{
                    if(tempFile2!=null&&tempFile2.exists()){
                        tempFile2.delete();
                    }
                }catch (Exception e){

                }
            }
        }

        private boolean upload(String filePath){
            CommonFunction.log(TAG, "upload() filePath=" + filePath);
            //构造参数列表
            List<Part> partList = new ArrayList<Part>();
            try {
                partList.add(new FilePart("file", new File(filePath)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                CommonFunction.log(TAG, "upload() add file error");
                return false;
            }
            try {
                //获取队列
                RequestQueue requestQueue = Volley.newRequestQueue(BaseApplication.appContext);
                String url = mUrl+"?key="+mToken;
                //生成请求
                MultipartRequest uploadRequest = new MultipartRequest(url, partList.toArray(new Part[partList.size()]), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s, Request<String> request) {
                        //处理成功消息
                        Log.d(TAG, "upload() onResponse s=" + s);
                        IUploadListener listener = mListener.get();
                        if(listener==null){
                            return;
                        }
                        if(s!=null && s.contains("200")==true){
                            listener.onUploadResult(0);
                        }else{
                            listener.onUploadResult(-1);
                        }
                        synchronized(mUploadFinishSignal) {
                            mUploadFinish = true;
                            mUploadFinishSignal.notify();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //处理失败错误信息
                        CommonFunction.log(TAG, "upload() onErrorResponse error=" + error.toString());
                        IUploadListener listener = mListener.get();
                        if(listener==null){
                            return;
                        }
                        listener.onUploadResult(-1);
                        synchronized(mUploadFinishSignal) {
                            mUploadFinish = true;
                            mUploadFinishSignal.notify();
                        }
                    }
                });
                //将请求加入队列
                requestQueue.add(uploadRequest);
                CommonFunction.log(TAG, "upload() add to queue success");
                return true;
            }catch (Exception e){
                e.printStackTrace();
                CommonFunction.log(TAG, "upload() upload file error");
                return false;
            }
        }
    }

    static class MultipartRequest extends StringRequest {
        private Part[] parts;

        public MultipartRequest(String url, Part[] parts, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(Method.POST, url, listener, errorListener);
            this.parts = parts;
        }

        @Override
        public String getBodyContentType() {
            return "multipart/form-data; boundary=" + Part.getBoundary();
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                Part.sendParts(baos, parts);
            } catch (IOException e) {
                VolleyLog.e(e, "error when sending parts to output!");
            }
            return baos.toByteArray();
        }
    }
}
