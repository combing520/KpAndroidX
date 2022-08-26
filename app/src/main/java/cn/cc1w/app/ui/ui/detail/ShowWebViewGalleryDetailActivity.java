package cn.cc1w.app.ui.ui.detail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.List;

import app.cloud.ccwb.cn.linlibrary.loading.view.LoadingDialog;
import app.cloud.ccwb.cn.linlibrary.viewpager.LinViewpager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.adapter.gallery.GalleryDetailAdapter;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 显示 WebView 中富文本中的图片的 gallery
 *
 * @author kpinfo
 */
public class ShowWebViewGalleryDetailActivity extends CustomActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.viewPager_detail_gallery)
    LinViewpager viewPager;
    @BindView(R.id.txt_cnt_gallery)
    TextView cntTv;
    @BindView(R.id.txt_save_gallery)
    TextView savePicBtn;
    private List<String> picList = null;
    private Unbinder unbinder;
    private long lastTime;
    private int selectPos = 0;
    private LoadingDialog loading;
    private static final String LABEL = " / ";
    private final String[] mPermissionList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE_SAVE_IMG = 10;
    private final LinkedHashMap<String, Bitmap> map = new LinkedHashMap<>();
    private long lastClickTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_web_view_gallery_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        overridePendingTransition(0, 0);
        ImmersionBar.with(this).statusBarDarkFont(false).transparentStatusBar().init();
        unbinder = ButterKnife.bind(this);
        viewPager.setIsCanScroll(true);
        initData();
        initLoading();
        lastTime = System.currentTimeMillis();
    }

    /**
     * 初始化 loading
     */
    private void initLoading() {
        loading = AppUtil.getLoading(this);
        if (null != loading) {
            loading.setLoadingText(null);
        }
    }

    /**
     * 初始化数据
     */
    @SuppressLint("SetTextI18n")
    private void initData() {
        picList = getIntent().getStringArrayListExtra("picList");
        selectPos = getIntent().getIntExtra("selectPos", 0);
        if (null != picList) {
            GalleryDetailAdapter adapter = new GalleryDetailAdapter(this, picList);
            viewPager.setAdapter(adapter);
            cntTv.setText((selectPos + 1) + LABEL + (picList.size()));
            viewPager.setCurrentItem(selectPos, false);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                LogUtil.e("onPageSelected position = " + position);
                selectPos = position;
                cntTv.setText((selectPos + 1) + LABEL + (picList.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 请求读取sd卡的权限
     */
    private void requestPermission(String picPath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(this, mPermissionList)) {
                LogUtil.d("授权");
                savePic2Gallery(picPath);
            } else {
                EasyPermissions.requestPermissions(this, "开屏新闻需要获取SD卡读写权限用于保存图片", REQUEST_CODE_SAVE_IMG, mPermissionList);
            }
        } else {
            savePic2Gallery(picPath);
        }
    }

    /**
     * 将图片保存到图库
     */
    private void savePic2Gallery(String picPath) {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtil.showShortToast(getString(R.string.network_error));
            return;
        }
        if (!TextUtils.isEmpty(picPath) && null != viewPager) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= Constant.MIN_TIME_INTERVAL) {
                if (map.containsKey(picPath)) {
                    Bitmap bm = map.get(picPath);
                    if (bm != null) {
                        saveToSystemGallery(ShowWebViewGalleryDetailActivity.this, bm);
                    }
                } else {
                    try {
                        PhotoView photoView = viewPager.findViewWithTag(viewPager.getCurrentItem());
                        if (photoView != null) {
                            LogUtil.d("不为空 ！！！");
                            photoView.setDrawingCacheEnabled(true);
                            photoView.setScale(1.0f);
                            Bitmap bm = Bitmap.createBitmap(photoView.getDrawingCache());
                            photoView.setDrawingCacheEnabled(false);
                            if (bm != null) {
                                map.put(picPath, bm);
                                saveToSystemGallery(ShowWebViewGalleryDetailActivity.this, bm);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            lastTime = currentTime;
        }
    }

    /**
     * 将图片保存到相册
     */
    public void saveToSystemGallery(Context context, Bitmap bmp) {
        if (getExternalCacheDir() != null) {
            File appDir = new File(getExternalCacheDir().getAbsolutePath() + File.separator);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(appDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
                Uri uri = Uri.fromFile(file);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                ToastUtil.showShortToast("图片保存成功");
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("文件没有找到 = " + e);
                ToastUtil.showShortToast("图片保存失败,请稍后重试");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> list) {
        if (requestCode == REQUEST_CODE_SAVE_IMG && EasyPermissions.hasPermissions(this, mPermissionList)) {
            savePic2Gallery(picList.get(selectPos));
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == REQUEST_CODE_SAVE_IMG && EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            ToastUtil.showShortToast("权限拒绝，保存失败");
            new AppSettingsDialog.Builder(this).setTitle("权限请求").setRationale("图片保存需要读取本地SD卡权限").build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.img_back_header_detail_gallery, R.id.txt_save_gallery})
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= Constant.MIN_TIME_INTERVAL) {
            int id = view.getId();
            if (id == R.id.img_back_header_detail_gallery) {
                finish();
            } else if (id == R.id.txt_save_gallery) {
                requestPermission(picList.get(selectPos));
            }
        }
        lastClickTime = currentTime;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != loading) {
            loading.close();
            loading = null;
        }
        unbinder.unbind();
    }
}