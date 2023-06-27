package org.devio.xlibrary.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;

import org.devio.xlibrary.OnApiExceptionClickListener;
import org.devio.xlibrary.R;
import org.devio.xlibrary.http.XException;
import org.devio.xlibrary.loading.LoadingView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseDataBindingActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity {
    protected V binding;
    protected VM viewModel;
    private int statusBarColor = 0;
    private int navigationBarColor = 0;

    private OnApiExceptionClickListener onApiExceptionClickListener;
    private int loadingLayoutId = 0;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //私有的初始化DataBinding和ViewModel方法
        initViewDataBinding(savedInstanceState);
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


    private void initViewDataBinding(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState));
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

    public abstract int initContentView(Bundle savedInstanceState);

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
                    LoadingView.startLoading(BaseDataBindingActivity.this);
                } else {
                    LoadingView.startLoading(BaseDataBindingActivity.this, loadingLayoutId);
                }
            }
        });

        viewModel.getUIChangeLiveData().getDismissDialogEvent().observe(this, unused -> LoadingView.dismissLoading());
        viewModel.getUIChangeLiveData().getFailureEvent().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                XException.requestHandle(BaseDataBindingActivity.this, throwable, () -> {
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

    public OnApiExceptionClickListener getOnApiExceptionClickListener() {
        return onApiExceptionClickListener;
    }

    public int getLoadingLayoutId() {
        return loadingLayoutId;
    }
}
