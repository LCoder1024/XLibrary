package org.devio.xlibrary.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public abstract class XBaseActivity<VM extends BaseViewModel> extends AppCompatActivity {
    protected VM viewModel;
    private int statusBarColor = 0;
    private int navigationBarColor = 0;
    private int navIcon = 0;
    private String navTitle = "";
    private int navTitleColor = 0;
    private String navEnd = "";
    private int navBackgroundResource = 0;  //导航栏背景色
    private int backgroundResource = 0;//背景色

    private OnNavBackClickListener onNavBackClickListener;
    private OnNavEndClickListener onNavEndClickListener;

    private OnApiExceptionClickListener onApiExceptionClickListener;

    private int loadingLayoutId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xbase);
        initContentView();
        initView();
        initImmersionBar();
        registerUIChangeLiveDataCallBack();
        initData();
        initViewObservable();
    }

    private void initContentView() {
        ImageView iv_back = findViewById(R.id.iv_back);
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_end = findViewById(R.id.tv_end);
        ConstraintLayout cl_layout = findViewById(R.id.cl_layout);
        ConstraintLayout cl_nav = findViewById(R.id.cl_nav);
        backgroundResource = getBackgroundResource();
        if (backgroundResource == 0) {
            backgroundResource = R.color.white;
        }
        cl_layout.setBackgroundResource(backgroundResource);

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
            cl_nav.setBackgroundResource(R.color.white);
        } else {
            cl_nav.setBackgroundResource(navBackgroundResource);
        }

        FrameLayout fl_content = findViewById(R.id.fl_content);
        LayoutInflater.from(this).inflate(getLayoutId(), fl_content, true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
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


    private void initView() {
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
        getLifecycle().addObserver(viewModel);

    }

    private <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls) {
        return new ViewModelProvider(activity).get(cls);
    }

    private VM initViewModel() {
        return viewModel;
    }

    protected void initImmersionBar() {
        statusBarColor = getStatusBarColor();
        navigationBarColor = getNavigationBarColor();
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(statusBarColor != 0 ? statusBarColor : R.color.white).navigationBarColor(navigationBarColor != 0 ? navigationBarColor : R.color.white).autoDarkModeEnable(true).keyboardEnable(true).init();
    }

    private void registerUIChangeLiveDataCallBack() {
        viewModel.getUIChangeLiveData().getShowDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                loadingLayoutId = getLoadingLayoutId();
                if (loadingLayoutId == 0) {
                    LoadingView.startLoading(XBaseActivity.this);
                } else {
                    LoadingView.startLoading(XBaseActivity.this, loadingLayoutId);
                }

            }
        });
        viewModel.getUIChangeLiveData().getDismissDialogEvent().observe(this, unused -> LoadingView.dismissLoading());
        viewModel.getUIChangeLiveData().getFailureEvent().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                XException.requestHandle(XBaseActivity.this, throwable, () -> {
                    onApiExceptionClickListener = XBaseActivity.this.getOnApiExceptionClickListener();
                    if (onApiExceptionClickListener != null) {
                        onApiExceptionClickListener.OnApiExceptionClick();
                    }
                });
            }
        });
    }

    protected abstract int getLayoutId();

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

    public int getBackgroundResource() {
        return backgroundResource;
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