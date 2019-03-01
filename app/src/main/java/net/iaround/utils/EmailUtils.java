package net.iaround.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import net.iaround.tools.CommonFunction;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * email crash logcat 日志
 * Created by liangyuanhuan on 24/10/2017.
 */

public class EmailUtils {
    public static final String TAG = "EmailUtils";

    public static void send(Context context, String receiver){
        EmailWorkThread thread = new EmailWorkThread(context,receiver);
        thread.start();
        CommonFunction.showToast(context, "正在上传异常日志，请耐心等待！", Toast.LENGTH_LONG);
    }

    static class EmailWorkThread extends Thread{
        private WeakReference<Context> mContext;
        private String mReceiver;

        public EmailWorkThread(Context context,String receiver){
            mContext = new WeakReference<Context>(context);
            mReceiver = receiver;
        }

        @Override
        public void run() {
            try {
                String logFilePath = CommonFunction.getSDPath() + "log";
                String crashFilePath = CommonFunction.getSDPath() + "crash";

                String tempFilePath = CommonFunction.getSDPath() + "temp";
                File tempFile = new File(tempFilePath);
                if(tempFile.exists()==false){
                    tempFile.mkdir();
                }
                ZipUtil.zip(logFilePath,tempFilePath,"log.zip");
                ZipUtil.zip(crashFilePath,tempFilePath,"crash.zip");

                String[] receiver = new String[] {mReceiver};
                String subject = "异常日志上报";
                String content = "请点击发送按钮将崩溃日志内容上报，谢谢！";

                Context context = mContext.get();
                if(context==null){
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                intent.setType("message/rfc822");
                intent.setType("application/octet-stream");
                // 设置邮件发收人
                intent.putExtra(Intent.EXTRA_EMAIL, receiver);
                // 设置邮件标题
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                // 设置邮件内容
                intent.putExtra(Intent.EXTRA_TEXT, content);
                //附件
                ArrayList fileUris = new ArrayList<Uri>();
                fileUris.add( Uri.fromFile(new File(tempFilePath + "/log.zip")) );
                fileUris.add( Uri.fromFile(new File(tempFilePath + "/crash.zip")) );
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);

                // 调用系统的邮件系统
                context.startActivity(Intent.createChooser(intent, "请选择邮件发送软件"));
            }catch (Exception ex){

            }
        }
    }
}
