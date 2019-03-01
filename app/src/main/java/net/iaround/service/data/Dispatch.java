
package net.iaround.service.data;

public interface Dispatch
{
	void runTask(Runnable task);
	
	void shutDown();
}
