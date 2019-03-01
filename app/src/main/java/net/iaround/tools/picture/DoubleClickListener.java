package net.iaround.tools.picture;

import android.view.MotionEvent;
import android.view.View;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-10-28 下午9:33:08
 * @Description: 双击事件监听器
 */
public class DoubleClickListener implements View.OnTouchListener {
	
	private int count = 0;
	private int firClick = 0;
	private int secClick = 0;

	private View.OnClickListener listener;
	
	public DoubleClickListener(View.OnClickListener listener) {
		// TODO Auto-generated constructor stub
		this.listener = listener;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (MotionEvent.ACTION_DOWN == event.getAction()) {
			count++;
			if (count == 1) {
				firClick = (int) System.currentTimeMillis();

			} else if (count == 2) {
				secClick = (int) System.currentTimeMillis();
				if (secClick - firClick < 1000) {
					// 双击事件
					if(listener != null)
					{
						listener.onClick(v);
					}
				}
				count = 0;
				firClick = 0;
				secClick = 0;
			}
		}
		return true;
	}
}