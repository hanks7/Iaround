
package net.iaround.ui.datamodel;

/**
 * 后台发送实体类
 * 
 * @author linyg
 * 
 */
public class SendTaskItem
{
	private long id;
	private int type; // 发送类型 1图片，2话题
	private long uid;
	private String filePath; // 附件本地地址
	private String hFilePath; // 高清图片本地地址
	
	/** 上传文件的类型  {@link FileUploadType}*/
	private int uploadType;
	
	private String contentJsonToString; // 发送内容
	private long createtime; // 创建时间
	
	/**
	 * 后台发送初始化
	 * 
	 * @param id
	 *            消息id ,第一次发送时，则0 ;如从草稿箱获取出来发送时，则传相应的本地id
	 * @param uid
	 *            发送者uid
	 * @param type
	 *            发送类型 1图片，2话题
	 * @pa
	 * @param contentJsonToString
	 *            发送的参数，现将map转成json字符串
	 */
	public SendTaskItem(long id , long uid , int type , String filePath , String hFilePath ,
						String contentJsonToString , long createtime, int uploadType )
	{
		if ( id == 0 )
		{
			id = System.currentTimeMillis( ); // 如果传递的id为0,则生成一个时间戳作为当前的唯一临时id
		}
		this.id = id;
		this.uid = uid;
		this.type = type;
		this.filePath = filePath;
		this.hFilePath = hFilePath;
		this.contentJsonToString = contentJsonToString;
		this.createtime = createtime;
		this.uploadType = uploadType;
	}
	
	public long getId( )
	{
		return id;
	}
	
	/** 发送类型 1图片，2话题 */
	public int getType( )
	{
		return type;
	}
	
	public String getFilePath( )
	{
		return filePath;
	}
	
	public void setFilePath( String filePath )
	{
		this.filePath = filePath;
	}
	
	public String gethFilePath( )
	{
		return hFilePath;
	}
	
	public void sethFilePath( String hFilePath )
	{
		this.hFilePath = hFilePath;
	}
	
	public long getUid( )
	{
		return uid;
	}
	
	public String getContentJsonToString( )
	{
		return contentJsonToString;
	}
	
	public long getCreatetime( )
	{
		return createtime;
	}
	
	@Override
	public boolean equals( Object o )
	{
		// TODO Auto-generated method stub
		if ( o instanceof SendTaskItem )
		{
			if ( ( ( SendTaskItem ) o ).getContentJsonToString( ).equals( contentJsonToString ) )
			{
				
			}
		}
		return super.equals( o );
	}

	public int getUploadType( )
	{
		return uploadType;
	}

	public void setUploadType( int uploadType )
	{
		this.uploadType = uploadType;
	}
}
