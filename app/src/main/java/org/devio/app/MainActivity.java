package org.devio.app;

import org.devio.xlibrary.base.XBaseListActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends XBaseListActivity<MainViewModel> {
    private MainAdapter mainAdapter;


    @Override
    protected void initView() {
        setTitle("我是标题我是标题我是标题我是标题我是标题我是标题我是标题我是标题");
        mainAdapter = new MainAdapter();
        getRecyclerView().setAdapter(mainAdapter);
    }


    @Override
    protected void updateData(int pageNo) {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            strings.add(String.valueOf(i));
        }
        mainAdapter.submitList(strings);
    }

    @Override
    protected void initViewObservable() {

    }
}