package cn.cc1w.app.ui.ui.usercenter.broke;

import android.os.Bundle;
import android.view.View;

import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.photoview.PhotoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.utils.AppUtil;
import cn.cc1w.app.ui.R;
/**
 * 爆料 图片详情显示
 * @author kpinfo
 */
public class BrokeAlbumDetailActivity extends CustomActivity {
    @BindView(R.id.img_broke_album_detail)
    PhotoView picImg;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broke_album_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        overridePendingTransition(0, 0);
        ImmersionBar.with(this).statusBarColor(R.color.colorBlack).statusBarDarkFont(false).init();
        unbinder = ButterKnife.bind(this);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        String picPath = getIntent().getStringExtra("pic");
        AppUtil.loadSplashImg(picPath, picImg);
    }

    @OnClick({R.id.img_broke_album_detail})
    public void onClick(View view) {
        if (view.getId() == R.id.img_broke_album_detail) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
