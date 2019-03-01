package net.iaround.service;

import net.iaround.tools.CommonFunction;

public interface NetChangeObserver {
    /**
     * 网络连接回调 type为网络类型
     */
    void onNetConnected(CommonFunction.NetType type);

    /**
     * 没有网络
     */
    void onNetDisConnect();
}
