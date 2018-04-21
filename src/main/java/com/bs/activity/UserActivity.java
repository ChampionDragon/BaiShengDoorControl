package com.bs.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.bs.R;
import com.bs.account.ResetPwd;
import com.bs.base.BaseActivity;
import com.bs.bean.UserInfo;
import com.bs.constant.Constant;
import com.bs.util.ObjectSave;
import com.bs.util.PhotoUtil;
import com.bs.util.SmallUtil;
import com.bs.view.RoundImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UserActivity extends BaseActivity implements OnClickListener {
    private TextView photo, look;
    private Button cancle;
    private EditText company, name, position;
    private RoundImageView head;
    private PopupWindow popupWindow;
    private File fileHead = new File(Constant.fileDir, Constant.filehead);
    private File fileTemp = new File(Constant.fileDir, Constant.filehead_temp);
    private static final int looking = 1;
    private static final int photoing = 2;
    private Bitmap bitmap = null;
    private Bitmap small = null;
    private Uri uriTemp;
    private UserInfo userInfo;
    String tag = "lcb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initview();
        initPopWindow();
        mkdir();
        initHead();
        company.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popup_enter));
    }

    /**
     * 初始化头像
     */
    private void initHead() {
        userInfo = ObjectSave.getUserInfo();
        String headPath = userInfo.getHeadpath();
        if (headPath != null && !headPath.isEmpty()) {
            File filehead = new File(headPath);
            Uri uri = Uri.fromFile(filehead);
            head.setImageURI(uri);
        }
    }

    private void mkdir() {
        if (!fileHead.exists()) {
            fileHead.mkdirs();
        }
        if (!fileTemp.exists()) {
            fileTemp.mkdirs();
        }
    }

    /**
     * 初始化popwindow
     */
    private void initPopWindow() {
        View popView = View.inflate(this, R.layout.popowindow_photo, null);
        popupWindow = new PopupWindow(popView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        popupWindow.setAnimationStyle(R.style.PopupAnimation); // 设置弹出动画
        ColorDrawable colorDrawable = new ColorDrawable(getResources()
                .getColor(R.color.transparent));
        popupWindow.setBackgroundDrawable(colorDrawable);// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // popupWindow.setBackgroundDrawable(new BitmapDrawable(
        // getApplicationContext().getResources(), Bitmap.createBitmap(1,
        // 1, Bitmap.Config.ARGB_8888)));
        popupWindow.setFocusable(true);// 设置PopupWindow可获得焦点
        popupWindow.setOutsideTouchable(true);// PopupWindow以外的区域是否可点击,点击后是否会消失。
        cancle = (Button) popView.findViewById(R.id.btn_cancle);
        cancle.setOnClickListener(this);
        photo = (TextView) popView.findViewById(R.id.photo_ing);
        photo.setOnClickListener(this);
        look = (TextView) popView.findViewById(R.id.photo_look);
        look.setOnClickListener(this);
        // popupWindow消失时监听
        popupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpaha(UserActivity.this, 1.0f);
            }
        });
    }

    private void initview() {
        findViewById(R.id.back_user).setOnClickListener(this);
        company = (EditText) findViewById(R.id.user_company);
        name = (EditText) findViewById(R.id.user_name);
        position = (EditText) findViewById(R.id.user_position);
        company.setOnClickListener(this);
        name.setOnClickListener(this);
        position.setOnClickListener(this);
        head = (RoundImageView) findViewById(R.id.user_riv);
        head.setOnClickListener(this);
        findViewById(R.id.user_pwd).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_pwd:
                SmallUtil.getActivity(UserActivity.this, ResetPwd.class);
                break;
            case R.id.back_user:
                finish();
                break;
            case R.id.user_company:
                break;
            case R.id.user_name:
                break;
            case R.id.user_position:
                break;
            case R.id.user_riv:
                popWindow();
                break;
            case R.id.btn_cancle:
                popupWindow.dismiss();// popwindow消失
                break;
            case R.id.photo_ing:
                //启动系统的拍照功能
                Intent takephoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //新建个.jpg文件存放拍照出来的图片
                uriTemp = Uri.fromFile(new File(fileTemp, "临时.jpg"));
                takephoto.putExtra(MediaStore.EXTRA_OUTPUT, uriTemp);
                startActivityForResult(takephoto, photoing);
                popupWindow.dismiss();
                break;
            case R.id.photo_look:
                //启动系统给的查询照片功能
                Intent pic = new Intent(Intent.ACTION_GET_CONTENT);
                //设置成所有照片类型
                pic.setType("image/*");
                startActivityForResult(pic, looking);
                popupWindow.dismiss();
                break;
        }

    }

    /**
     * popwindow显示
     */
    private void popWindow() {
        View rootView = findViewById(R.id.useractivity); // 设置当前根目录
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int y = dm.heightPixels * 1 / 12;
        //相对位移，popwindow出现在距离底部整个屏幕1/12距离
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, y);
        // popupWindow.update();//更新后显示，比如做了长宽缩小放大的处理
        backgroundAlpaha(this, 0.5f);
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpaha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case looking:
                    Uri uri = data.getData();
                    setBitmap(uri);
                    break;
                case photoing:
                    setBitmap(uriTemp);
                    break;
            }
        }

    }

    private void setBitmap(Uri uri) {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    this.getContentResolver(), uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        small = PhotoUtil.SizeImage(bitmap);
        head.setImageBitmap(small);
        executor.submit(SavePic);

    }

    Runnable SavePic = new Runnable() {
        @Override
        public void run() {
            Save();
        }
    };

    /**
     * 保存数据（本地路径，服务器）
     */
    private void Save() {
        File file = PhotoUtil.SavePhoto(small, fileHead.getAbsolutePath(),
                "lcb");
        userInfo.setHeadpath(file.getAbsolutePath());
        ObjectSave.SaveUserInfo(userInfo);
    }

}
