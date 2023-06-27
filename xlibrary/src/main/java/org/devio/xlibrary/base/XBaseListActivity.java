package org.devio.xlibrary.base;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import org.devio.xlibrary.R;

public abstract class XBaseListActivity<VM extends BaseViewModel> extends XBaseActivity<VM> {
    private int layoutId = 0;
    private int pageNo = 1;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.ItemDecoration itemDecoration;

    @Override
    protected int getLayoutId() {
        layoutId = createLayoutId();
        return layoutId != 0 ? layoutId : R.layout.activity_xbase_list;
    }

    @Override
    protected void initData() {
        init();
        initView();
        initAdapter();
        updateData(pageNo);
        initRefresh();
    }

    private void init() {
        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = createLayoutManager();
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(this);
        }
        recyclerView.setLayoutManager(layoutManager);
        itemDecoration = createItemDecoration();
        if (itemDecoration != null) {
            recyclerView.addItemDecoration(itemDecoration);
        }
    }

    private void initRefresh() {
        MaterialHeader header = new MaterialHeader(this);
        refreshLayout.setEnableRefresh(enableRefresh());//是否启用下拉刷新功能
        refreshLayout.setEnableLoadMore(enableLoadMore());//是否启用上拉加载功能
        refreshLayout.setRefreshHeader(header);
        refreshLayout.setRefreshFooter(new ClassicsFooter(this).setDrawableSize(14));
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            pageNo = 1;
            updateData(pageNo);
            refreshLayout.finishRefresh();
        });
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            pageNo++;
            updateData(pageNo);
            refreshLayout.finishLoadMore();
        });
    }

    protected abstract void initView();

    protected abstract void initAdapter();

    protected abstract void updateData(int pageNo);

    protected boolean enableRefresh() {
        return true;
    }

    protected boolean enableLoadMore() {
        return true;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public LinearLayoutManager createLayoutManager() {
        return layoutManager;
    }

    public RecyclerView.ItemDecoration createItemDecoration() {
        return itemDecoration;
    }

    //完成加载并标记没有更多数据
    protected void finishLoadMoreWithNoMoreData() {
        if (refreshLayout != null) {
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
    }

    public int createLayoutId() {
        return layoutId;
    }


}