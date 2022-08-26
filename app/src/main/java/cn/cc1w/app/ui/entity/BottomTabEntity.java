package cn.cc1w.app.ui.entity;

import com.flyco.tablayout.listener.CustomTabEntity;

/**
 * 底部导航
 * @author kpinfo
 */
public class BottomTabEntity implements CustomTabEntity {
    // 显示的 title
    private String title;
    // 选中的 图标
    private int selectedIcon;
    // 未选中的图标
    private int unSelectedIcon;

    public  BottomTabEntity(String title, int selectedIcon, int unSelectedIcon){
        this.title = title;
        this.selectedIcon = selectedIcon;
        this.unSelectedIcon = unSelectedIcon;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public int getTabSelectedIcon() {
        return selectedIcon;
    }

    @Override
    public int getTabUnselectedIcon() {
        return unSelectedIcon;
    }
}
