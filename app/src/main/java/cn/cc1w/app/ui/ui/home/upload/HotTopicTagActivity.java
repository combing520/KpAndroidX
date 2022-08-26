package cn.cc1w.app.ui.ui.home.upload;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.rxjava.rxlife.RxLife;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.adapter.topic.HotTopicAdapter;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.entity.HotTopicEntity;
import cn.cc1w.app.ui.utils.LogUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.utils.ToastUtil;
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError;
import rxhttp.RxHttp;

/**
 * 热门话题标签
 *
 * @author kpinfo
 */
public class HotTopicTagActivity extends AppCompatActivity implements HotTopicAdapter.TagClickListener {
    @BindView(R.id.list_topic_hot)
    RecyclerView recyclerView;
    private Unbinder unbinder;
    private HotTopicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_topic_tag);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initList();
        requestHotTopicList();
    }

    /**
     * 初始化List
     */
    private void initList() {
        adapter = new HotTopicAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    /**
     * 请求热门话题
     */
    private void requestHotTopicList() {
        if (NetUtil.isNetworkConnected(this)) {
            RxHttp.get(Constant.LIST_TOPIC_HOT)
                    .asResponseList(HotTopicEntity.ItemHotTopicEntity.class)
                    .as(RxLife.asOnMain(this))
                    .subscribe(list -> {
                        if (!isFinishing()) {
                            adapter.setData(list);
                        }
                    }, (OnError) error -> {
                        ToastUtil.showShortToast(error.getErrorMsg());
                    });
        }
    }

    @Override
    public void onTagClick(View v, int pos) {
        LogUtil.e("onItemClick");
        HotTopicEntity.ItemHotTopicEntity entity = adapter.getItem(pos);
        Intent intent = getIntent();
        intent.putExtra("topic", entity.getTopic_name());
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick({R.id.txt_cancel_topic})
    public void onClick(View v) {
        if (v.getId() == R.id.txt_cancel_topic) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}