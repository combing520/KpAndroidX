package cn.cc1w.app.ui.ui.usercenter.message;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cc1w.app.ui.base.CustomActivity;
import cn.cc1w.app.ui.ui.usercenter.message.fragment.CommentFragment;
import cn.cc1w.app.ui.ui.usercenter.message.fragment.MessageFragment;
import cn.cc1w.app.ui.R;

/**
 * 通知和评论
 *
 * @author kpinfo
 */
public class MessageAndCommentActivity extends CustomActivity {
    private Unbinder unbinder;
    @BindView(R.id.ll_message_messageAndComment)
    LinearLayout messageLayout;
    @BindView(R.id.ll_comment_messageAndComment)
    LinearLayout commentLayout;
    @BindView(R.id.txt_header_not_title)
    TextView titleTv;
    private MessageFragment messageFragment;
    private CommentFragment commentFragment;
    private FragmentManager manager;
    private Fragment currentFragment;
    private static final int POS_MESSAGE = 0;
    private static final int POS_COMMENT = 1;
    private int currentPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_and_comment);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        unbinder = ButterKnife.bind(this);
        initNavigation();
        initFragment();
    }

    /**
     * 初始化导航头
     */
    private void initNavigation() {
        titleTv.setText(getResources().getString(R.string.messageAndComment));
    }

    /**
     * 初始化 Fragment
     */
    private void initFragment() {
        manager = getSupportFragmentManager();
        messageFragment = MessageFragment.newInstance();
        commentFragment = CommentFragment.newInstance();
        setSelectTab(POS_MESSAGE);
        switchFragment(messageFragment);
    }


    /**
     * 切换 Fragment
     *
     * @param targetFragment 切换的 目标Fragment
     */
    private void switchFragment(Fragment targetFragment) {
        if (currentFragment != targetFragment) {
            if (!targetFragment.isAdded()) {
                if (null != currentFragment) {
                    manager.beginTransaction().hide(currentFragment).commit();
                }
                manager.beginTransaction().add(R.id.container_messageAndComment, targetFragment).commit();
            } else {
                manager.beginTransaction().hide(currentFragment).show(targetFragment).commit();
            }
            currentFragment = targetFragment;
        }
    }

    @OnClick({R.id.img_back_header_not_title, R.id.ll_message_messageAndComment, R.id.ll_comment_messageAndComment})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back_header_not_title) {
            finish();
        } else if (id == R.id.ll_message_messageAndComment) {
            setSelectedPage(POS_MESSAGE);
        } else if (id == R.id.ll_comment_messageAndComment) {
            setSelectedPage(POS_COMMENT);
        }
    }

    /**
     * 设置当前选中的 tab
     *
     * @param pos 选中的位置
     */
    private void setSelectTab(int pos) {
        if (pos == POS_MESSAGE) {
            commentLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
            messageLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_tab_select));
        } else if (pos == POS_COMMENT) {
            messageLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
            commentLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_tab_select));
        }
    }

    /**
     * 设置选中的 fragment
     *
     * @param selectPos 当前的 fragment
     */
    private void setSelectedPage(int selectPos) {
        if (currentPos != selectPos) {
            if (selectPos == POS_MESSAGE) {
                switchFragment(messageFragment);
            } else if (selectPos == POS_COMMENT) {
                switchFragment(commentFragment);
            }
        }
        setSelectTab(selectPos);
        currentPos = selectPos;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}