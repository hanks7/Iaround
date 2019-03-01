
package net.iaround.connector;

/**
 * 上传图片回调
 *
 * @author linyg
 */
public interface UploadImageCallback {

    /**
     * 上传结束
     *
     * @param result
     */
    void onUploadFileFinish(String result);

    /**
     * 上传错误
     *
     * @param e
     */
    void onUploadFileError(String e);
}
