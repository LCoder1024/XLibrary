package org.devio.xlibrary.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.devio.xlibrary.OnApiExceptionClickListener;
import org.devio.xlibrary.http.XException;
import org.devio.xlibrary.loading.LoadingView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public abstract class XBaseDataBindingFragment<V extends ViewDataBinding, VM extends BaseViewModel> extends Fragment {

    protected V binding;
    protected VM viewModel;
    private boolean isFirstLoad = true;

    private OnApiExceptionClickListener onApiExceptionClickListener;

    private int loadingLayoutId = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //页面接受的参数方法
        initParam();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, initContentView(inflater, container, savedInstanceState), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initImmersionBar();
        //私有的初始化DataBinding和ViewModel方法
        initViewDataBinding();
        //私有的ViewModel与View的契约事件回调逻辑
        registerUIChangeLiveDataCallBack();
        //页面数据初始化方法
        initData();
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
    }

    public abstract int initContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected void initImmersionBar() {

    }

    private void initViewDataBinding() {
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
        binding.setVariable(viewModelId, viewModel);
        //支持LiveData绑定xml，数据改变，UI自动会更新
        binding.setLifecycleOwner(this);
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(viewModel);
    }

    public abstract int initVariableId();

    public VM initViewModel() {
        return viewModel;
    }

    public <T extends ViewModel> T createViewModel(Fragment fragment, Class<T> cls) {
        return new ViewModelProvider(fragment).get(cls);
    }


    protected void registerUIChangeLiveDataCallBack() {
        viewModel.getUIChangeLiveData().getShowDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                loadingLayoutId = getLoadingLayoutId();
                if (loadingLayoutId == 0) {
                    LoadingView.startLoading(XBaseDataBindingFragment.this.getActivity());
                } else {
                    LoadingView.startLoading(XBaseDataBindingFragment.this.getActivity(), loadingLayoutId);
                }
            }
        });

        viewModel.getUIChangeLiveData().getDismissDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                LoadingView.dismissLoading();
            }
        });

        viewModel.getUIChangeLiveData().getFailureEvent().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                XException.requestHandle(getActivity(), throwable, () -> {
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

    protected abstract void updateData();

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            updateData();
            isFirstLoad = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoadingView.dismissLoading();
    }

    public OnApiExceptionClickListener getOnApiExceptionClickListener() {
        return onApiExceptionClickListener;
    }

    public int getLoadingLayoutId() {
        return loadingLayoutId;
    }


}
