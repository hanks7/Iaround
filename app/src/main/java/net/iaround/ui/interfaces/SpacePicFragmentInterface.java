
package net.iaround.ui.interfaces;

public interface SpacePicFragmentInterface
{
	boolean getIsDeletePhoto();
	
	void setIsDeletePhoto(boolean isDeletePhoto);
	
	void delPhoto(int index);
	
	long getPhotoUid();
}
