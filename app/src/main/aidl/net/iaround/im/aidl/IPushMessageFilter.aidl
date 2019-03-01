// IPushMessageFilter.aidl
package net.iaround.im.aidl;

// Declare any non-default types here with import statements

interface IPushMessageFilter {
    //处理推送消息,返回真不传递消息到下一个过滤器
    boolean onReceiveMessage(int cmdId,in byte[] buffer);

    //处理缓存的推送消息,返回真不传递消息到下一个过滤器, cmdId 为 -1 时候表示缓存消息结束标记
    boolean onReceiveCache(int cmdId,in byte[] buffer);
}
