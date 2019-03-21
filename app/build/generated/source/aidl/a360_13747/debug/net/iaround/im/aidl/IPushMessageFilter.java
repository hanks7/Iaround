/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/zhangsen/iaround/iaround-android-online/app/src/main/aidl/net/iaround/im/aidl/IPushMessageFilter.aidl
 */
package net.iaround.im.aidl;
// Declare any non-default types here with import statements

public interface IPushMessageFilter extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements net.iaround.im.aidl.IPushMessageFilter
{
private static final java.lang.String DESCRIPTOR = "net.iaround.im.aidl.IPushMessageFilter";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an net.iaround.im.aidl.IPushMessageFilter interface,
 * generating a proxy if needed.
 */
public static net.iaround.im.aidl.IPushMessageFilter asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof net.iaround.im.aidl.IPushMessageFilter))) {
return ((net.iaround.im.aidl.IPushMessageFilter)iin);
}
return new net.iaround.im.aidl.IPushMessageFilter.Stub.Proxy(obj);
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
case TRANSACTION_onReceiveMessage:
{
data.enforceInterface(descriptor);
int _arg0;
_arg0 = data.readInt();
byte[] _arg1;
_arg1 = data.createByteArray();
boolean _result = this.onReceiveMessage(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_onReceiveCache:
{
data.enforceInterface(descriptor);
int _arg0;
_arg0 = data.readInt();
byte[] _arg1;
_arg1 = data.createByteArray();
boolean _result = this.onReceiveCache(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements net.iaround.im.aidl.IPushMessageFilter
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
//处理推送消息,返回真不传递消息到下一个过滤器

@Override public boolean onReceiveMessage(int cmdId, byte[] buffer) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(cmdId);
_data.writeByteArray(buffer);
mRemote.transact(Stub.TRANSACTION_onReceiveMessage, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//处理缓存的推送消息,返回真不传递消息到下一个过滤器, cmdId 为 -1 时候表示缓存消息结束标记

@Override public boolean onReceiveCache(int cmdId, byte[] buffer) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(cmdId);
_data.writeByteArray(buffer);
mRemote.transact(Stub.TRANSACTION_onReceiveCache, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_onReceiveMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onReceiveCache = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
//处理推送消息,返回真不传递消息到下一个过滤器

public boolean onReceiveMessage(int cmdId, byte[] buffer) throws android.os.RemoteException;
//处理缓存的推送消息,返回真不传递消息到下一个过滤器, cmdId 为 -1 时候表示缓存消息结束标记

public boolean onReceiveCache(int cmdId, byte[] buffer) throws android.os.RemoteException;
}
