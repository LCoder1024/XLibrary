package org.devio.xlibrary.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;

import org.devio.xlibrary.OnApiExceptionClickListener;
import org.devio.xlibrary.OnNavBackClickListener;
import org.devio.xlibrary.OnNavEndClickListener;
import org.devio.xlibrary.R;
import org.devio.xlibrary.http.XException;
import org.devio.xlibrary.loading.LoadingView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class XBaseDataBindingActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity {
    protected V binding;
    protected VM viewModel;
    private int statusBarColor = 0;
    private int navigationBarColor = 0;
    private int navIcon = 0;
    private String navTitle = "";
    private int navTitleColor = 0;
    private String navEnd = "";
    private int navBackgroundResource = 0;  //导航栏背景色
    private OnNavBackClickListener onNavBackClickListener;
    private OnNavEndClickListener onNavEndClickListener;
    private LinearLayout contentLayout;

    private OnApiExceptionClickListener onApiExceptionClickListener;

    private int loadingLayoutId = 0;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //私有的初始化DataBinding和ViewModel方法
        initViewDataBinding();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initImmersionBar();
        registerUIChangeLiveDataCallBack();
        //页面接受的参数方法
        initParam();
        //页面数据初始化方法
        initData();
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 设置为当前的 Intent，避免 Activity 被杀死后重启 Intent 还是最原先的那个
        setIntent(intent);
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return resources;
    }

    private void initViewDataBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        int viewModelId = initVariableId();
        viewModel = initViewModel();
        if (viewModel == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                modelClass = BaseViewModel.class;
            }
            viewModel = (VM) createViewModel(this, modelClass);
        }
        //关联ViewModel
        binding.setVariable(viewModelId, viewModel);
        //支持LiveData绑定xml，数据改变，UI自动会更新
        binding.setLifecycleOwner(this);
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(viewModel);

        initContentView();
        setContentView(binding.getRoot());
    }

    private void initContentView() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        viewGroup.removeAllViews();
        contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        viewGroup.addView(contentLayout);
        LayoutInflater.from(this).inflate(R.layout.layout_nav, contentLayout, true);
        ImageView iv_back = findViewById(R.id.iv_back);
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_end = findViewById(R.id.tv_end);
        ConstraintLayout cl_nav = findViewById(R.id.cl_nav);
        navIcon = getNavIcon();
        if (navIcon != 0) {
            iv_back.setImageResource(navIcon);
        }
        iv_back.setOnClickListener(v -> {
            onNavBackClickListener = getOnNavBackClickListener();
            if (onNavBackClickListener != null) {
                onNavBackClickListener.onBackClick();
            } else {
                finish();
            }
        });

        navTitle = getNavTitle();
        if (!TextUtils.isEmpty(navTitle)) {
            tv_title.setText(navTitle);
        }
        navTitleColor = getNavTitleColor();
        if (navTitleColor != 0) {
            tv_title.setTextColor(getColor(navTitleColor));
        }
        navEnd = getNavEnd();
        if (!TextUtils.isEmpty(navEnd)) {
            tv_end.setText(navEnd);
            onNavEndClickListener = getOnNavEndClickListener();
            tv_end.setOnClickListener(v -> {
                if (onNavEndClickListener != null) {
                    onNavEndClickListener.onEndClick();
                }
            });
        }
        navBackgroundResource = getNavBackgroundResource();
        if (navBackgroundResource == 0) {
            cl_nav.setBackgroundResource(R.color.black);
        } else {
            cl_nav.setBackgroundResource(navBackgroundResource);
        }
    }

    @Override
    public void setContentView(View view) {
        contentLayout.addView(view);
    }

    protected void initImmersionBar() {
        statusBarColor = getStatusBarColor();
        navigationBarColor = getNavigationBarColor();
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(statusBarColor != 0 ? statusBarColor : R.color.white)
                .navigationBarColor(navigationBarColor != 0 ? navigationBarColor : R.color.white)
                .autoDarkModeEnable(true)
                .keyboardEnable(true)
                .init();
    }

    protected abstract int getLayoutId();

    protected abstract int initVariableId();

    private VM initViewModel() {
        return viewModel;
    }

    private <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls) {
        return new ViewModelProvider(activity).get(cls);
    }

    protected void registerUIChangeLiveDataCallBack() {
        viewModel.getUIChangeLiveData().getShowDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                loadingLayoutId = getLoadingLayoutId();
                if (loadingLayoutId == 0) {
                    LoadingView.startLoading(XBaseDataBindingActivity.this);
                } else {
                    LoadingView.startLoading(XBaseDataBindingActivity.this, loadingLayoutId);

                }
            }
        });
        viewModel.getUIChangeLiveData().getDismissDialogEvent().observe(this, unused -> LoadingView.dismissLoading());
        viewModel.getUIChangeLiveData().getFailureEvent().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                XException.requestHandle(XBaseDataBindingActivity.this, throwable, () -> {
                    onApiExceptionClickListener = getOnApiExceptionClickListener();
                    if (onApiExceptionClickListener != null) {
                        onApiExceptionClickListener.OnApiExceptionClick();
                    }
                });
            }
        });
    }

    protected abstract void initParam();

    protected abstract void initData();

    protected abstract void initViewObservable();

    public int getStatusBarColor() {
        return statusBarColor;
    }

    public int getNavigationBarColor() {
        return navigationBarColor;
    }

    public int getNavIcon() {
        return navIcon;
    }

    public String getNavTitle() {
        return navTitle;
    }

    public int getNavTitleColor() {
        return navTitleColor;
    }

    public String getNavEnd() {
        return navEnd;
    }

    public int getNavBackgroundResource() {
        return navBackgroundResource;
    }

    public OnNavBackClickListener getOnNavBackClickListener() {
        return onNavBackClickListener;
    }

    public OnNavEndClickListener getOnNavEndClickListener() {
        return onNavEndClickListener;
    }

    public OnApiExceptionClickListener getOnApiExceptionClickListener() {
        return onApiExceptionClickListener;
    }

    public int getLoadingLayoutId() {
        return loadingLayoutId;
    }
}
