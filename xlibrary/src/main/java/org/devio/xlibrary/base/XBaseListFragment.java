package org.devio.xlibrary.base;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import org.devio.xlibrary.R;

public abstract class XBaseListFragment<VM extends BaseViewModel> extends XBaseFragment<VM> {
    private int layoutId = 0;
    private int pageNo = 1;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.ItemDecoration itemDecoration;

    @Override
    protected int getLayoutId() {
        layoutId = createLayoutId();
        return layoutId != 0 ? layoutId : R.layout.fragment_xbase_list;
    }

    public int createLayoutId() {
        return layoutId;
    }

    @Override
    protected void initData() {
        initView();
        initAdapter();
        initRefresh();
    }

    private void initView() {
        if (getView() != null) {
            refreshLayout = getView().findViewById(R.id.refreshLayout);
            recyclerView = getView().findViewById(R.id.recyclerView);
        }
        layoutManager = createLayoutManager();
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(getActivity());
        }
        recyclerView.setLayoutManager(layoutManager);
        itemDecoration = createItemDecoration();
        if (itemDecoration != null) {
            recyclerView.addItemDecoration(itemDecoration);
        }
    }

    private void initRefresh() {
        if (getActivity() != null) {
            MaterialHeader header = new MaterialHeader(getActivity());
            refreshLayout.setEnableRefresh(enableRefresh());//是否启用下拉刷新功能
            refreshLayout.setEnableLoadMore(enableLoadMore());//是否启用上拉加载功能
            refreshLayout.setRefreshHeader(header);
            refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()).setDrawableSize(14));
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

    protected boolean enableRefresh() {
        return true;
    }

    protected boolean enableLoadMore() {
        return true;
    }

    protected abstract void initAdapter();

    protected abstract void updateData(int pageNo);
    @Override
    protected void onLazyLoad() {
        updateData(pageNo);
    }
}
