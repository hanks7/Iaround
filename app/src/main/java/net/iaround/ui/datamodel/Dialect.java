
package net.iaround.ui.datamodel;


import java.io.Serializable;
import java.util.List;


public class Dialect implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8835681642563514921L;
	public int id;
	public String name;
	public List< Dialect > childs;
}
