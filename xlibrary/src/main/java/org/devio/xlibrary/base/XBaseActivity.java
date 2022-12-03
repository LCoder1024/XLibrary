package org.devio.xlibrary.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;

import org.devio.xlibrary.R;
import org.devio.xlibrary.base.BaseViewModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class XBaseActivity<VM extends BaseViewModel> extends AppCompatActivity {
    protected VM viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initImmersionBar();
        registerUIChangeLiveDataCallBack();
        initData();
        initViewObservable();
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
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
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
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.white)
                .navigationBarColor(R.color.white)
                .autoDarkModeEnable(true)
                .keyboardEnable(true)
                .init();
    }

    private void registerUIChangeLiveDataCallBack() {
        viewModel.getUIChangeLiveData().getShowDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {

            }
        });

        viewModel.getUIChangeLiveData().getDismissDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {

            }
        });
        viewModel.getUIChangeLiveData().getFailureEvent().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {

            }
        });
    }

    protected abstract int getLayoutId();

    protected abstract void initData();

    protected abstract void initViewObservable();
}