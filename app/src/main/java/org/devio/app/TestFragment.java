package org.devio.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;

import org.devio.app.databinding.FragmentTestBinding;
import org.devio.xlibrary.base.XBaseDataBindingFragment;

public class TestFragment extends XBaseDataBindingFragment<FragmentTestBinding, TestViewModel> {
    private String title;

    public static TestFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int initContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return R.layout.fragment_test;
    }

    @Override
    public int initVariableId() {
        return BR.vm;
    }

    @Override
    protected void initParam() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString("title");
        }
    }

    @Override
    protected void initData() {
        binding.tvTitle.setText(title);

    }

    @Override
    protected void initViewObservable() {
        viewModel.getHomePageConfigLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

            }
        });
    }

    @Override
    protected void updateData() {
        viewModel.getHomePageConfig();
    }

}
