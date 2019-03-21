/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/zhangsen/iaround/iaround-android-online/app/src/main/aidl/net/iaround/im/aidl/ISTNService.aidl
 */
package net.iaround.im.aidl;
/* STNService接口
*/
public interface ISTNService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements net.iaround.im.aidl.ISTNService
{
private static final java.lang.String DESCRIPTOR = "net.iaround.im.aidl.ISTNService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an net.iaround.im.aidl.ISTNService interface,
 * generating a proxy if needed.
 */
public static net.iaround.im.aidl.ISTNService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof net.iaround.im.aidl.ISTNService))) {
return ((net.iaround.im.aidl.ISTNService)iin);
}
return new net.iaround.im.aidl.ISTNService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
java.lang.String descriptor = DESCRIPTOR;
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(descriptor);
return true;
}
case TRANSACTION_onAppBackground:
{
data.enforceInterface(descriptor);
this.onAppBackground();
reply.writeNoException();
return true;
}
case TRANSACTION_onAppForeground:
{
data.enforceInterface(descriptor);
this.onAppForeground();
reply.writeNoException();
return true;
}
case TRANSACTION_onUserInfoChange:
{
data.enforceInterface(descriptor);
this.onUserInfoChange();
reply.writeNoException();
return true;
}
case TRANSACTION_startTask:
{
data.enforceInterface(descriptor);
net.iaround.im.aidl.ITaskWrapper _arg0;
_arg0 = net.iaround.im.aidl.ITaskWrapper.Stub.asInterface(data.readStrongBinder());
int _result = this.startTask(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_stopTask:
{
data.enforceInterface(descriptor);
int _arg0;
_arg0 = data.readInt();
this.stopTask(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_registerPushMessageFilter:
{
data.enforceInterface(descriptor);
net.iaround.im.aidl.IPushMessageFilter _arg0;
_arg0 = net.iaround.im.aidl.IPushMessageFilter.Stub.asInterface(data.readStrongBinder());
this.registerPushMessageFilter(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterPushMessageFilter:
{
data.enforceInterface(descriptor);
net.iaround.im.aidl.IPushMessageFilter _arg0;
_arg0 = net.iaround.im.aidl.IPushMessageFilter.Stub.asInterface(data.readStrongBinder());
this.unregisterPushMessageFilter(_arg0);
reply.writeNoException();
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements net.iaround.im.aidl.ISTNService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/* App 切到后台
        * */
@Override public void onAppBackground() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onAppBackground, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/* App 切到前台
        * */
@Override public void onAppForeground() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onAppForeground, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/* 账号信息更改
        * */
@Override public void onUserInfoChange() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onUserInfoChange, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int startTask(net.iaround.im.aidl.ITaskWrapper task) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((task!=null))?(task.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_startTask, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* 停止一个任务
        * */
@Override public void stopTask(int taskID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(taskID);
mRemote.transact(Stub.TRANSACTION_stopTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/* 注册推送消息处理过滤器
        * */
@Override public void registerPushMessageFilter(net.iaround.im.aidl.IPushMessageFilter filter) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((filter!=null))?(filter.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerPushMessageFilter, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/* 注销推送消息处理过滤器
       * */
@Override public void unregisterPushMessageFilter(net.iaround.im.aidl.IPushMessageFilter filter) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((filter!=null))?(filter.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterPushMessageFilter, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onAppBackground = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onAppForeground = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onUserInfoChange = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_startTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_stopTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_registerPushMessageFilter = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_unregisterPushMessageFilter = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
/* App 切到后台
        * */
public void onAppBackground() throws android.os.RemoteException;
/* App 切到前台
        * */
public void onAppForeground() throws android.os.RemoteException;
/* 账号信息更改
        * */
public void onUserInfoChange() throws android.os.RemoteException;
public int startTask(net.iaround.im.aidl.ITaskWrapper task) throws android.os.RemoteException;
/* 停止一个任务
        * */
public void stopTask(int taskID) throws android.os.RemoteException;
/* 注册推送消息处理过滤器
        * */
public void registerPushMessageFilter(net.iaround.im.aidl.IPushMessageFilter filter) throws android.os.RemoteException;
/* 注销推送消息处理过滤器
       * */
public void unregisterPushMessageFilter(net.iaround.im.aidl.IPushMessageFilter filter) throws android.os.RemoteException;
}
