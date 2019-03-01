package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.entity.BaseEntity;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.glide.GlideUtil;

import java.io.File;

public class AuthenPicGuideActivity extends TitleActivity implements View.OnClickListener {

    private static final int TAKE_PICTURE = 101;//开启相机的请求码
    private Button btnTakePic;

    private ImageView ivAuthenPic;
    private Button btnSendPic;
    private Button btnTakePicAgain;

    private Uri picUri;
    private LinearLayout llAuthenTakePic;
    private LinearLayout llAuthenSendPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getString(R.string.authen_title), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_authen_guide);

        llAuthenTakePic = findView(R.id.ll_authen_take_pic);
        llAuthenSendPic = findView(R.id.ll_authen_send_pic);

        btnTakePic = findView(R.id.btn_take_pic);

        ivAuthenPic = findView(R.id.iv_authen_pic);
        btnSendPic = findView(R.id.btn_send_pic);
        btnTakePicAgain = findView(R.id.btn_take_pic_again);

    }

    private void initDatas() {

    }

    private void initListeners() {
        btnTakePic.setOnClickListener(this);
        btnSendPic.setOnClickListener(this);
        btnTakePicAgain.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_take_pic:
                takePic(createUri());
                break;
            case R.id.btn_send_pic:
                /*Map<String, File> picFileMap = new HashMap<>();
                //TODO上传图片需要适配
                picFileMap.put("pic.jpg", new File("/storage/emulated/0/iaround/pic/pic.jpg"));
                EditInfoHttpProtocol.uploadHeadPic(picFileMap, "application/octet-stream", "file",new UploadImageCallback(){

                    @Override
                    public void onUploadFileFinish(String result) {
                        CommonFunction.log("xiaohua", "result = " + result);
                        UploadPicEntity uploadPicEntity = GsonUtil.getInstance().getServerBean(result, UploadPicEntity.class);
                        Intent authenStatus = new Intent(AuthenPicGuideActivity.this, AuthenPicStatusActivity.class);
                        if(uploadPicEntity.isSuccess()){
                            setResult(Activity.RESULT_OK);
                            Toast.makeText(AuthenPicGuideActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AuthenPicGuideActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onUploadFileError(String e) {

                    }
                });*/

//                AuthenHttpProtocol.authenPic(this, 1, new File("/storage/emulated/0/iaround/pic/pic.jpg"), new HttpStringCallback() {
//
//                    @Override
//                    public void onGeneralSuccess(String result, int id) {
//                        CommonFunction.log("xiaohua", "authenPic result = " + result);
//                        BaseEntity baseEntity = GsonUtil.getInstance().getServerBean(result, BaseEntity.class);
//                        if(baseEntity.isSuccess()){
//                            setResult(Activity.RESULT_OK);
//                            Toast.makeText(AuthenPicGuideActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
//                            finish();
//                        } else {
//                            Toast.makeText(AuthenPicGuideActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onGeneralError(String error, Exception e, int id) {
//
//                    }
//
//                });
                break;
            case R.id.btn_take_pic_again:
                takePic(createUri());
                break;
        }
    }

    private Uri createUri() {
        File iAroundFile = new File(CommonFunction.getSDPath()+"/pic");
        if(!iAroundFile.exists()){
            iAroundFile.mkdir();
        } else{
            CommonFunction.log("文件已经存在");
        }
        File file =  new File(iAroundFile,"/pic.jpg");
        picUri = Uri.fromFile(file);
        return picUri;
    }

    /**
     * 打开相机照相
     * @param imageUri 相片的保存地址
     */
    private void takePic(Uri imageUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == TAKE_PICTURE){
                llAuthenTakePic.setVisibility(View.GONE);
                llAuthenSendPic.setVisibility(View.VISIBLE);
                GlideUtil.loadImage(BaseApplication.appContext,picUri.toString(),ivAuthenPic,R.drawable.authen_demo_pic, R.drawable.authen_demo_pic);
                /*Intent intent = new Intent(this, AuthenSendPicActivity.class);
                intent.putExtra(Constants.AUTHEN_PIC_PATH, picUri.toString());
                startActivity(intent);*/
            }
        }
    }
}
