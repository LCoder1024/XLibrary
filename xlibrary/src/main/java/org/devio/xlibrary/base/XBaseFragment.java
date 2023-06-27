package org.devio.xlibrary.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.devio.xlibrary.OnApiExceptionClickListener;
import org.devio.xlibrary.http.XException;
import org.devio.xlibrary.loading.LoadingView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public abstract class XBaseFragment<VM extends BaseViewModel> extends Fragment {
    protected VM viewModel;
    private boolean isFirstLoad = true;
    private View view;
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
        view = inflater.inflate(getLayoutId(), container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initImmersionBar();
        //私有的初始化ViewModel方法
        init();
        //私有的ViewModel与View的契约事件回调逻辑
        registerUIChangeLiveDataCallBack();
        //页面数据初始化方法
        initData();
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
    }


    private void init() {
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

    public VM initViewModel() {
        return viewModel;
    }

    public <T extends ViewModel> T createViewModel(Fragment fragment, Class<T> cls) {
        return new ViewModelProvider(fragment).get(cls);
    }


    protected void registerUIChangeLiveDataCallBack() {
        viewModel.getUIChangeLiveData().getShowDialogEvent().observe(this, unused -> {
            loadingLayoutId = getLoadingLayoutId();
            if (loadingLayoutId == 0) {
                LoadingView.startLoading(XBaseFragment.this.getActivity());
            } else {
                LoadingView.startLoading(XBaseFragment.this.getActivity(), loadingLayoutId);
            }
        });

        viewModel.getUIChangeLiveData().getDismissDialogEvent().observe(this, unused -> LoadingView.dismissLoading());
        viewModel.getUIChangeLiveData().getFailureEvent().observe(this, throwable -> XException.requestHandle(getActivity(), throwable, () -> {
            onApiExceptionClickListener = getOnApiExceptionClickListener();
            if (onApiExceptionClickListener != null) {
                onApiExceptionClickListener.OnApiExceptionClick();
            }
        }));
    }

    protected void initImmersionBar() {

    }

    protected abstract int getLayoutId();

    protected abstract void initParam();

    protected abstract void initData();

    protected abstract void initViewObservable();

    protected abstract void onLazyLoad();

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            onLazyLoad();
            isFirstLoad = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoadingView.dismissLoading();
    }

    public View getView() {
        return view;
    }

    public OnApiExceptionClickListener getOnApiExceptionClickListener() {
        return onApiExceptionClickListener;
    }

    public int getLoadingLayoutId() {
        return loadingLayoutId;
    }
}
