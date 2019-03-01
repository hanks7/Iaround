/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/zhangsen/iaround/iaround-android-online/app/src/main/aidl/net/iaround/im/aidl/ITaskWrapper.aidl
 */
package net.iaround.im.aidl;
// Declare any non-default types here with import statements

public interface ITaskWrapper extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements net.iaround.im.aidl.ITaskWrapper
{
private static final java.lang.String DESCRIPTOR = "net.iaround.im.aidl.ITaskWrapper";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an net.iaround.im.aidl.ITaskWrapper interface,
 * generating a proxy if needed.
 */
public static net.iaround.im.aidl.ITaskWrapper asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof net.iaround.im.aidl.ITaskWrapper))) {
return ((net.iaround.im.aidl.ITaskWrapper)iin);
}
return new net.iaround.im.aidl.ITaskWrapper.Stub.Proxy(obj);
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
case TRANSACTION_getTaskID:
{
data.enforceInterface(descriptor);
int _result = this.getTaskID();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getProperties:
{
data.enforceInterface(descriptor);
android.os.Bundle _result = this.getProperties();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_req2buf:
{
data.enforceInterface(descriptor);
byte[] _result = this.req2buf();
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
case TRANSACTION_buf2resp:
{
data.enforceInterface(descriptor);
byte[] _arg0;
_arg0 = data.createByteArray();
int _result = this.buf2resp(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_onTaskEnd:
{
data.enforceInterface(descriptor);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.onTaskEnd(_arg0, _arg1);
reply.writeNoException();
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements net.iaround.im.aidl.ITaskWrapper
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
@Override public int getTaskID() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getTaskID, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//任务ID

@Override public android.os.Bundle getProperties() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getProperties, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//任务属性

@Override public byte[] req2buf() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_req2buf, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int buf2resp(byte[] buf) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(buf);
mRemote.transact(Stub.TRANSACTION_buf2resp, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void onTaskEnd(int errType, int errCode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(errType);
_data.writeInt(errCode);
mRemote.transact(Stub.TRANSACTION_onTaskEnd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getTaskID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getProperties = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_req2buf = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_buf2resp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onTaskEnd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public int getTaskID() throws android.os.RemoteException;
//任务ID

public android.os.Bundle getProperties() throws android.os.RemoteException;
//任务属性

public byte[] req2buf() throws android.os.RemoteException;
public int buf2resp(byte[] buf) throws android.os.RemoteException;
public void onTaskEnd(int errType, int errCode) throws android.os.RemoteException;
}
