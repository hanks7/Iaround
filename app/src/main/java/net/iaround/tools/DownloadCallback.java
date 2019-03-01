package net.iaround.tools;

/**
 * 作者：zx on 2017/7/20 13:54
 */
public interface DownloadCallback {
    void DownloadSuccess();

    void DownloadFailure(String errorMsg);
}
