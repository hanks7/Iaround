
package net.iaround.model.entity;


import java.util.ArrayList;


public class FaceDescrise
{
	/**
	 * 表情描述
	 */
	public ArrayList< Descrise > descriptions = new ArrayList< Descrise >( );
	
	public class Descrise
	{
		public String description;
		public String filename;
		
	}
	
}
