// ITaskWrapper.aidl
package net.iaround.im.aidl;

// Declare any non-default types here with import statements

interface ITaskWrapper {
    int getTaskID();  //任务ID

    Bundle getProperties(); //任务属性

    byte[] req2buf();

    int buf2resp(in byte[] buf);

    void onTaskEnd(int errType, int errCode);
}
