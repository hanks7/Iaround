
package net.iaround.connector;

/**
 * 下载图片回调
 *
 * @author linyg
 */
public interface DownloadFileCallback {
    /**
     * 下载进度
     *
     * @param lenghtOfFile       文件总长度
     * @param LengthOfDownloaded 已下载的长度
     * @param flag
     */
    void onDownloadFileProgress(long lenghtOfFile, long LengthOfDownloaded, int flag);

    /**
     * 下载文件结束
     *
     * @param flag
     * @param savePath
     */
    void onDownloadFileFinish(int flag, String fileName, String savePath);

    /**
     * 下载文件错误
     *
     * @param flag
     */
    void onDownloadFileError(int flag, String fileName, String savePath);
}
