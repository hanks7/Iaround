package net.iaround.model.entity;

import net.iaround.model.im.BaseServerBean;
import net.iaround.ui.datamodel.ResourceBanner;

import java.util.ArrayList;

public class NearbyUserListBean extends BaseServerBean
{
	public int pageno;
	
	public int pagesize;
	
	public int amount;

	public ArrayList<ResourceBanner> topbanners;
	
	public ArrayList< NearByExtendUser > users;


}
