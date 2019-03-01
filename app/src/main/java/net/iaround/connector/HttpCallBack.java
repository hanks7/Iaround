
package net.iaround.connector;

public interface HttpCallBack {
    void onGeneralSuccess(String result, long flag);

    void onGeneralError(int e, long flag);
}
