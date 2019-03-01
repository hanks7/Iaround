package net.iaround.download;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;

import net.iaround.tools.CommonFunction;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.nio.charset.Charset;

import me.huyunfeng.libs.android.toolbox.StringUtils;

/**
 * 文件上传
 * 
 */
public class HttpMultipartPost extends AsyncTask<HttpResponse, Integer, String> {
	private static final String TAG = HttpMultipartPost.class.getSimpleName();
	private String url;
	private long totalSize;
	private String[] files;
	private String fileName;
	private FormBodyPart[] parts;
	private CallBack mCallBack;
	private CallBackMsg mCallBackMsg;
	private String encode = HTTP.UTF_8;

	private int maxConnectTime = 0;//连接超时最大时间
	private int maxRequestTime = 0;//请求超时最大时间

	/**
	 * 
	 * @param url
	 * @param files
	 *            文件路径
	 * @param parts
	 *            其他参数
	 * @param fileEncode 编码 默认UTF-8
	 * 
	 */
	public HttpMultipartPost(String url, String[] files, String fileNames, String fileEncode,
							 FormBodyPart... parts) {
		super();
		this.url = url;
		this.files = files;
		this.fileName = fileNames;
		this.parts = parts;
		this.encode = StringUtils.isEmpty(fileEncode) ? HTTP.UTF_8 : fileEncode;
	}

	public void setTimeoutParams( int maxConnectTime , int maxRequestTime )
	{
		this.maxConnectTime = maxConnectTime;
		this.maxRequestTime = maxRequestTime;
	}

	@Override
	protected void onPreExecute() {
		if (!URLUtil.isNetworkUrl(url)) {
			throw new IllegalArgumentException("unvalid url for post!");
		}
	}

	@Override
	protected String doInBackground(HttpResponse... arg0) {
		HttpClient httpClient = new DefaultHttpClient();
		if ( maxConnectTime > 0 )
		{
			httpClient.getParams( ).setParameter( CoreConnectionPNames.CONNECTION_TIMEOUT , maxConnectTime );
		}
		if ( maxRequestTime > 0 )
		{
			httpClient.getParams( ).setParameter( CoreConnectionPNames.SO_TIMEOUT, maxRequestTime );
		}
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);

		try {
			CustomMultiPartEntity multipartContent = new CustomMultiPartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE, null,
					Charset.forName(encode), new CustomMultiPartEntity.ProgressListener() {
						@Override
						public void transferred(long num) {
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});
			// add all file
			
			for (String file : files) {
				FileBody fileBody = new FileBody(new File(file), fileName,  "application/octet-stream", null);
				multipartContent.addPart("file", fileBody);
			}
			// add other parts
			for (FormBodyPart part : parts) {
				multipartContent.addPart(part);
			}
			totalSize = multipartContent.getContentLength();

			// Send it
			httpPost.setEntity(multipartContent);
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			String serverResponse = EntityUtils.toString(response.getEntity());
			return serverResponse;
		} catch (Exception e) {
			e.printStackTrace( );
			httpClient.getConnectionManager( ).shutdown( );
			return "{\"msg\":\"post failed!\"}";
		}
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		if (mCallBack != null) {
			mCallBack.update(progress[0]);
		}
	}

	@Override
	protected void onPostExecute(String param) {
		CommonFunction.log(TAG, param + "");
		if (mCallBackMsg != null) {
			mCallBackMsg.msg(param);
		}
	}

	public void setCallBack(CallBack mCallBack) {
		this.mCallBack = mCallBack;
	}

	public void setCallBackMsg(CallBackMsg mCallBackMsg) {
		this.mCallBackMsg = mCallBackMsg;
	}

	public interface CallBack {
		void update(Integer i);
	}

	public interface CallBackMsg {
		void msg(String msg);
	}
}
