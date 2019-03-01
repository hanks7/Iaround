package net.iaround.connector;

import android.content.Context;

import net.iaround.download.HttpMultipartPost;
import net.iaround.download.HttpMultipartPost.CallBack;
import net.iaround.download.HttpMultipartPost.CallBackMsg;
import net.iaround.tools.CommonFunction;

import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 上传图片
 *
 * @author linyg
 */
public class UploadFile implements Runnable {
    private long flag; // 本次请求标识
    private InputStream is; // 文件流
    private String filename; // 文件名称
    private String inputname = "file"; // 表单名称
    private Map<String, String> map;// 请求参数
    private UploadFileCallback callback;

    private String url;

    private String fileDir = "";

    private int maxConnectTime;//连接超时最大时间
    private int maxRequestTime;//请求超时最大时间

    /**
     * 上传文件
     *
     * @param is
     * @param filename
     * @param url
     * @param flag
     * @param callback
     * @throws ConnectionException
     * @throws ConnectionException
     */
    public UploadFile(Context context, InputStream is, String filename,
                      String url, Map<String, String> map, UploadFileCallback _callback,
                      long flag) throws ConnectionException {
        this.url = url;
        this.is = is;
        this.flag = flag;
        this.filename = filename;
        this.callback = _callback;
        this.map = map;
    }

    public UploadFile(Context context, String fileDir, String filename,
                      String url, Map<String, String> map, UploadFileCallback _callback,
                      long flag) throws ConnectionException {
        this.url = url;
        this.fileDir = fileDir;
        this.flag = flag;
        this.filename = filename;
        this.callback = _callback;
        this.map = map;
    }

    public void setTimeoutParams(int maxConnectTime, int maxRequestTime) {
        this.maxConnectTime = maxConnectTime;
        this.maxRequestTime = maxRequestTime;
    }

    @Override
    public void run() {

        FormBodyPart[] parts = new FormBodyPart[map.size()];
        try {
            int index = 0;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                parts[index++] = new FormBodyPart(entry.getKey(),
                        new StringBody(entry.getValue()));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpMultipartPost post = new HttpMultipartPost(url,
                new String[]{fileDir}, filename, HTTP.UTF_8, parts);
        if (maxConnectTime > 0 && maxRequestTime > 0) {
            post.setTimeoutParams(maxConnectTime, maxRequestTime);
        }
        post.setCallBack(new CallBack() {

            @Override
            public void update(Integer i) {
                callback.onUploadFileProgress(i, flag);
            }
        });
        post.setCallBackMsg(new CallBackMsg() {

            @Override
            public void msg(String msg) {
                CommonFunction.log("AnchorsCertificationActivity", "msg = " + msg);
                if (msg.contains("status")) {
                    callback.onUploadFileFinish(flag, msg);
                } else {
                    callback.onUploadFileError(msg, flag);
                }
            }
        });
        post.execute();
    }
}
