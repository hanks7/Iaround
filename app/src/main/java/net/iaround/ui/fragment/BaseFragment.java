package net.iaround.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import net.iaround.R;
import net.iaround.tools.DialogUtil;

/**
 * Class:
 * Author：gh
 * Date: 2016/12/29 12:18
 * Email：jt_gaohang@163.com
 */
public class BaseFragment extends Fragment {

    private Dialog mWaitDialog = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showWaitDialog( )
    {
        if ( mWaitDialog == null )
        {
            mWaitDialog = DialogUtil.getProgressDialog( getActivity(), getString( R.string.dialog_title ),
                    getString( R.string.please_wait ), null );
        }
        mWaitDialog.show( );
    }

    protected void destroyWaitDialog( )
    {
        if ( mWaitDialog != null )
        {
            mWaitDialog.dismiss( );
        }
    }

    protected void hideWaitDialog( )
    {
        if ( mWaitDialog != null )
        {
            if ( mWaitDialog.isShowing( ) )
            {
                mWaitDialog.hide( );
            }
        }
    }

    /** 关闭等待对话框 */
    protected void cancleWaitDialog( )
    {
        if ( mWaitDialog != null && mWaitDialog.isShowing( ) )
        {
            mWaitDialog.dismiss( );
        }
    }





}
