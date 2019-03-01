
package net.iaround.ui.group;


import net.iaround.ui.group.bean.CreateGroupInfo;
import net.iaround.ui.group.bean.GroupNextStep;

public interface ICreateGroupParentCallback
{
	
	void showWaitDialog(boolean isShow);
	
	CreateGroupInfo getGroupInfo();
	
	void goNext(GroupNextStep step);
}
