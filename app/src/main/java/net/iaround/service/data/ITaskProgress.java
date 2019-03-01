
package net.iaround.service.data;


public interface ITaskProgress< T >
{
	void onTaskStart(T task);
	
	void onTaskProgress(T task);
	
	void onTaskCancel(T task);
	
	void onTaskPause(T task);
	
	void onTaskComplete(T task);
	
	void onTaskError(T task);
}
