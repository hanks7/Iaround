// ISTNService.aidl
package net.iaround.im.aidl;

import net.iaround.im.aidl.ITaskWrapper;
import net.iaround.im.aidl.IPushMessageFilter;

/* STNService接口
*/

interface ISTNService {
     /* App 切到后台
        * */
    void onAppBackground();

    /* App 切到前台
        * */
    void onAppForeground();

    /* 账号信息更改
        * */
    void onUserInfoChange();

    /* 开始一个任务
        * return 任务ID
        * */
    int startTask(ITaskWrapper task);

     /* 停止一个任务
        * */
    void stopTask(int taskID);

    /* 注册推送消息处理过滤器
        * */
    void registerPushMessageFilter(IPushMessageFilter filter);

    /* 注销推送消息处理过滤器
       * */
    void unregisterPushMessageFilter(IPushMessageFilter filter);
}
