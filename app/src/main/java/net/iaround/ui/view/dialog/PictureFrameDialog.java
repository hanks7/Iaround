package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.ui.datamodel.Photo;
import net.iaround.ui.datamodel.Photos.PhotosBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: 图片弹框样式
 * Author：GH on 2017/1/08 16:09
 * Email：jt_gaohang@163.com
 */
public class PictureFrameDialog extends Dialog {
    public static final int HEAD = 1;
    public static final int ALBUM = 2;
    public static final int SELF = 3;

    private ItemOnclick itemOnclick;

    private FrameLayout llBackground;
    private TextView modifyHead;
    private View line1;
    private View line2;
    private TextView reviewBig;
    private TextView delete;
    private TextView cancel;

    private int type;
    private List<PhotosBean> picsThum;


    public PictureFrameDialog(Context context, int type) {
        super(context, R.style.transparent_dialog);
        this.type = type;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_picture_frame);

        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.popwin_anim_style);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        llBackground = (FrameLayout) findViewById(R.id.ly_picture_frame);
        initView();
        llBackground.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    hide();
                }
                return false;
            }
        });
    }

    private void initView() {

        modifyHead = (TextView) findViewById(R.id.tv_pop_picture_frame_modify_head);
        reviewBig = (TextView) findViewById(R.id.tv_pop_picture_frame_review_big);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        delete = (TextView) findViewById(R.id.tv_pop_picture_frame_delete);
        cancel = (TextView) findViewById(R.id.tv_pop_picture_frame_cancel);

        if (type == HEAD) {
            modifyHead.setVisibility(View.VISIBLE);
            reviewBig.setVisibility(View.VISIBLE);
            line1.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        } else if (type == ALBUM) {
            modifyHead.setVisibility(View.GONE);
            reviewBig.setVisibility(View.VISIBLE);
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        }else if (type == SELF){
            modifyHead.setVisibility(View.GONE);
            reviewBig.setVisibility(View.VISIBLE);
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }

        modifyHead.setTag("modifyHead");
        modifyHead.setOnClickListener(listener);

        if (type == 2 | type == 3){
            reviewBig.setTag("reviewBig");
        }else{
            reviewBig.setTag("headReviewBig");
        }

        reviewBig.setOnClickListener(listener);

        delete.setTag("delete");
        delete.setOnClickListener(listener);

        cancel.setTag("cancel");
        cancel.setOnClickListener(listener);
    }

    private int postion;
    private List<PhotosBean> photosBeanList;
    private ArrayList<String> photos;
    private Photo photo;

    public void show(int postion, Photo photo) {
        this.postion = postion;
        this.photo = photo;
        this.show();
    }
    public void show(int postion, List<PhotosBean> bean) {
        this.postion = postion;
        this.photosBeanList = bean;
        this.show();
    }
    public void show(int postion,ArrayList<String> photo) {
        this.postion = postion;
        this.show();
    }

    public int getPostion() {
        return postion;
    }

    public List<PhotosBean> getPhotoBean() {
        return photosBeanList;
    }

    public Photo getPhoto() {
        return photo;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public int getType() {
        return type;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (itemOnclick == null)
                return;
            itemOnclick.itemOnclick(view);
            dismiss();
        }
    };

    public void setItemOnclick(ItemOnclick itemOnclick) {
        this.itemOnclick = itemOnclick;
    }

    public interface ItemOnclick {
        void itemOnclick(View view);
    }


}
