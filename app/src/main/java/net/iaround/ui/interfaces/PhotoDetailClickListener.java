
package net.iaround.ui.interfaces;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import net.iaround.ui.space.SpacePictureActivity;

import java.util.ArrayList;


//查看图片详情
public class PhotoDetailClickListener implements OnClickListener {
    private String photoid;
    private Context mContext;
    private ArrayList<String> ids;
    private ArrayList<String> smallphotos;
    private int entrance; // 跳转来路
    private int requestCode;
    private long uid;
    private boolean mClickAvatarIsFinish;

    public PhotoDetailClickListener(Context context, int requestCode, String phototid,
                                    ArrayList<String> ids, ArrayList<String> smalls, int entrance) {
        mContext = context;
        this.requestCode = requestCode;
        this.photoid = phototid;
        this.ids = ids;
        this.smallphotos = smalls;
        this.entrance = entrance;

    }

    public PhotoDetailClickListener(Context context, long uid, int requestCode,
                                    String phototid, ArrayList<String> ids, ArrayList<String> smalls,
                                    int entrance) {
        mContext = context;
        this.requestCode = requestCode;
        this.photoid = phototid;
        this.ids = ids;
        this.uid = uid;
        this.smallphotos = smalls;
        this.entrance = entrance;
    }

    public PhotoDetailClickListener(Context context, long uid, int requestCode,
                                    String phototid, ArrayList<String> ids, ArrayList<String> smalls,
                                    int entrance, boolean clickAvatarIsFinish) {
        mContext = context;
        this.requestCode = requestCode;
        this.photoid = phototid;
        this.ids = ids;
        this.uid = uid;
        this.smallphotos = smalls;
        this.entrance = entrance;
        this.mClickAvatarIsFinish = clickAvatarIsFinish;
    }

    @Override
    public void onClick(View v) {

        Intent i = new Intent(mContext, SpacePictureActivity.class);
        i.putExtra("photoid", photoid);
        i.putExtra("uid", uid);
        if (ids != null && ids.size() > 0) {
            i.putExtra("ids", ids);
        }
        if (smallphotos != null && smallphotos.size() > 0) {
            i.putExtra("smallPhotos", smallphotos);
        }
        i.putExtra("entrance", entrance);
        i.putExtra("finish", mClickAvatarIsFinish);
        if (requestCode > 0) {
            ((Activity) mContext).startActivityForResult(i, requestCode);
        } else {
            mContext.startActivity(i);
        }
    }
}
