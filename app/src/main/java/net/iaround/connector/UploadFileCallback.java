
package net.iaround.connector;

/**
 * 上传图片回调
 *
 * @author linyg
 */
public interface UploadFileCallback {
    /**
     * 上传进度
     *
     * @param lengthOfUploaded 已经上传的长度
     * @param flag
     */
    void onUploadFileProgress(int lengthOfUploaded, long flag);

    /**
     * 上传结束
     *
     * @param flag
     * @param result
     */
    void onUploadFileFinish(long flag, String result);

    /**
     * 上传错误
     *
     * @param e
     * @param flag
     */
    void onUploadFileError(String e, long flag);
}
