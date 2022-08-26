package cn.cc1w.app.ui.ui.usercenter.broke.view;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import cn.cc1w.app.ui.adapter.broke.BrokeAdapter;
import cn.cc1w.app.ui.entity.ItemBrokeEntity;
import cn.cc1w.app.ui.utils.AppUtil;

/**
 *
 * @author kpinfo
 * @date 2018/11/12
 */
public class SystemTxtListView {
    private final ItemBrokeEntity entity;
    private final BrokeAdapter.SystemTxtHolder holder;

    public SystemTxtListView( RecyclerView.ViewHolder h, ItemBrokeEntity item) {
        holder = (BrokeAdapter.SystemTxtHolder) h;
        entity = item;
    }

    public void initView() {
        if (null != entity) {
            holder.describeTv.setText(TextUtils.isEmpty(entity.getContent()) ? "" : entity.getContent());
            AppUtil.loadAvatarImg(entity.getHeadpic(), holder.avatarImg);
        }
    }
}
