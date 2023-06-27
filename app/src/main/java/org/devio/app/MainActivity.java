package org.devio.app;

import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.tabs.TabLayoutMediator;

import org.devio.app.databinding.ActivityMainBinding;
import org.devio.xlibrary.OnNavBackClickListener;
import org.devio.xlibrary.OnNavEndClickListener;
import org.devio.xlibrary.base.XBaseDataBindingActivity;

public class MainActivity extends XBaseDataBindingActivity<ActivityMainBinding, MainViewModel> {

    private final String[] titles = {"页面一", "页面二", "页面三"};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int initVariableId() {
        return BR.vm;
    }

    @Override
    protected void initParam() {

    }

    @Override
    protected void initData() {
        TestStateAdapter stateAdapter = new TestStateAdapter(this, titles);
        binding.viewPager2.setAdapter(stateAdapter);
        binding.viewPager2.setOffscreenPageLimit(titles.length);
        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager2, (tab, position) -> tab.setText(titles[position]));
        mediator.attach();
    }

    @Override
    protected void initViewObservable() {

    }

    @Override
    public int getStatusBarColor() {
        return org.devio.xlibrary.R.color.white;
    }

    @Override
    public int getNavigationBarColor() {
        return org.devio.xlibrary.R.color.white;
    }

    @Override
    public int getNavIcon() {
        return org.devio.xlibrary.R.drawable.icon_back;
    }

    @Override
    public int getNavBackgroundResource() {
        return org.devio.xlibrary.R.color.white;
    }

    @Override
    public String getNavTitle() {
        return "我是列表";
    }

    @Override
    public int getNavTitleColor() {
        return org.devio.xlibrary.R.color.black;
    }

    @Override
    public OnNavBackClickListener getOnNavBackClickListener() {
        return () -> ToastUtils.showShort("返回按钮被拦截自定义处理");
    }

    @Override
    public OnNavEndClickListener getOnNavEndClickListener() {
        return () -> {

        };
    }

}