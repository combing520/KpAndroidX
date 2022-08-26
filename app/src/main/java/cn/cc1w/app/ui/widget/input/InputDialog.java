package cn.cc1w.app.ui.widget.input;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.cc1w.app.ui.R;
import cn.cc1w.app.ui.constants.Constant;
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity;
import cn.cc1w.app.ui.utils.IntentUtil;
import cn.cc1w.app.ui.utils.KeybordUtil;
import cn.cc1w.app.ui.utils.NetUtil;
import cn.cc1w.app.ui.utils.ToastUtil;


/**
 * @author kpinfo
 */
public class InputDialog {
    private final Activity mContext;
    private Dialog mDialog;
    private static int SCREEN_WIDTH = 0;
    private LinearLayout mRootDialog;
    private OnDismissCallback mOnDismissCallback;
    private EditText addCommentEdit;
    private TextView addCommentSend;
    private boolean isNeedCheckLogin = false;
    private boolean isSending = false;
    private InputSendListener mInputSendListener;

    public InputDialog(Activity context) {
        this.mContext = context;
    }

    @SuppressLint("InflateParams")
    public InputDialog builder() {
        // 获取Dialog布局
        mRootDialog = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.view_input_dialog, null);
        initViews();
        // 定义Dialog布局和参数
        mDialog = new Dialog(mContext, R.style.InputDialog);
        mDialog.setContentView(mRootDialog);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
        // 修改窗体宽高
        layoutParams.width = screenWidth(mContext);
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;
        mDialog.getWindow().setAttributes(layoutParams);
        mDialog.setOnDismissListener(dialogInterface -> {
            if (mOnDismissCallback != null) {
                mOnDismissCallback.dismiss(addCommentEdit.getText().toString());
            }
        });

        SoftKeyBoardListener.setListener(mContext, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                //mRootDialog.setVisibility(View.VISIBLE);
            }

            @Override
            public void keyBoardHide(int height) {
                if (mDialog != null) {
                    mRootDialog.setVisibility(View.INVISIBLE);
                    mDialog.dismiss();
                }
            }
        });

        return this;
    }

    private void initViews() {
        addCommentEdit = mRootDialog.findViewById(R.id.detail_add_comment_edittext);
        addCommentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                addCommentEdit.postDelayed(() -> changeSendBtnStatus(), 200);
            }
        });
        addCommentSend = mRootDialog.findViewById(R.id.send_btn);
        changeSendBtnStatus();
        addCommentSend.setOnClickListener(v -> {
            if (!NetUtil.isNetworkConnected(mContext)) {
                ToastUtil.showShortToast(mContext.getResources().getString(R.string.network_error));
                return;
            }
            if (isNeedCheckLogin) {
                if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                    IntentUtil.startActivity(mContext, LoginActivity.class, null);
                } else {
                    if (mInputSendListener != null) {
                        String content = addCommentEdit.getText().toString().trim();
                        if (TextUtils.isEmpty(content)) {
                            ToastUtil.showLongToast("请输入内容");
                        } else {
                            mInputSendListener.onSendClick(content);
                            addCommentEdit.setText("");
                            dismiss();
                        }
                    }
                }
            } else {
                if (mInputSendListener != null) {
                    String content = addCommentEdit.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        ToastUtil.showLongToast("请输入内容");
                    } else {
                        mInputSendListener.onSendClick(content);
                        addCommentEdit.setText("");
                        dismiss();
                    }
                }
            }
        });
    }

    public InputDialog setNeedCheckLogin(boolean need) {
        isNeedCheckLogin = need;
        return this;
    }

    public InputDialog setText(String text) {
        if (addCommentEdit != null) {
            addCommentEdit.setText(text);
        }
        return this;
    }

    public InputDialog setCancelable(boolean cancel) {
        mDialog.setCancelable(cancel);
        return this;
    }

    public InputDialog setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public void show() {
//        mDialog.show();
//        mRootDialog.postDelayed(() -> {
//            mRootDialog.setVisibility(View.VISIBLE);
//            int length = addCommentEdit.getText().length();
//            addCommentEdit.setSelection(length);
//            KeybordUtil.showSoftInput(mContext, addCommentEdit);
//        }, 10);


        mDialog.show();
        mRootDialog.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRootDialog.setVisibility(View.VISIBLE);
                int length = addCommentEdit.getText().length();
                addCommentEdit.setSelection(length);
                KeybordUtil.showSoftInput(mContext, addCommentEdit);
            }
        }, 100);
    }

    public void dismiss() {
        if (mDialog != null) {
            KeybordUtil.hideSoftInput(mContext, addCommentEdit);
            mDialog.dismiss();
        }
    }

    public boolean isShow() {

        return mDialog != null && mDialog.isShowing();
    }

    public void setHint(String txt) {
        addCommentEdit.setHint(txt);
    }

    public void sendComplete() {
        isSending = false;
        addCommentEdit.setText("");
        changeSendBtnStatus();
    }

    public InputDialog setOnSendClickListener(InputSendListener listener) {
        mInputSendListener = listener;
        return this;
    }

    public InputDialog setOnDismissCallback(OnDismissCallback callback) {
        mOnDismissCallback = callback;
        return this;
    }

    private void changeSendBtnStatus() {
        String text = addCommentEdit.getText().toString();
        int length = text.length();
        if (isSending) {
            addCommentSend.setText("发表中...");
            addCommentSend.setClickable(false);
        } else {
            if (length < 1) {
                addCommentSend.setClickable(false);
            } else {
                if (length > 200) {
                    text = text.substring(0, 200);
                    addCommentEdit.setText(text);
                    addCommentEdit.setSelection(200);
                    ToastUtil.showLongToast("最多只能输入200字");
                }
                addCommentSend.setClickable(true);
            }
            addCommentSend.setText("发表");
        }
    }

    /**
     * 屏幕的宽,像素
     */
    private int screenWidth(Context context) {
        if (SCREEN_WIDTH == 0) {
            SCREEN_WIDTH = context.getResources().getDisplayMetrics().widthPixels;
        }
        return SCREEN_WIDTH;
    }

    public interface InputSendListener {
        void onSendClick(String txt);
    }

    public interface OnDismissCallback {
        void dismiss(String draft);
    }
}
