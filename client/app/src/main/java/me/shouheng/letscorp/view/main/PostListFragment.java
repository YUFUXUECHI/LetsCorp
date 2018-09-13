package me.shouheng.letscorp.view.main;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Objects;

import javax.inject.Inject;

import me.shouheng.commons.util.LogUtils;
import me.shouheng.commons.util.PalmUtils;
import me.shouheng.commons.util.ToastUtils;
import me.shouheng.commons.widget.DividerItemDecoration;
import me.shouheng.letscorp.R;
import me.shouheng.letscorp.common.PrefUtils;
import me.shouheng.letscorp.databinding.FragmentPostListBinding;
import me.shouheng.letscorp.model.article.CategoryInfo;
import me.shouheng.letscorp.model.article.PostItem;
import me.shouheng.letscorp.view.CommonDaggerFragment;
import me.shouheng.letscorp.view.article.ArticleActivity;
import me.shouheng.letscorp.viewmodel.LetsCorpViewModel;

/**
 * @author shouh
 * @version $Id: PostListFragment, v 0.1 2018/6/23 13:46 shouh Exp$
 */
public class PostListFragment extends CommonDaggerFragment<FragmentPostListBinding> implements SwipeRefreshLayout.OnRefreshListener {

    private final static String EXTRA_CATEGORY_INFO = "__key_extra_category_info";

    private int currentPage = 1;

    private boolean loading = false;

    private CategoryInfo categoryInfo;

    private ArticleAdapter adapter;

    @Inject
    LetsCorpViewModel letsCorpViewModel;

    public static PostListFragment newInstance(CategoryInfo categoryInfo) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CATEGORY_INFO, categoryInfo);
        PostListFragment fragment = new PostListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_post_list;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        handleArguments();

        configList();
    }

    private void handleArguments() {
        Bundle arguments = getArguments();
        assert arguments != null;
        categoryInfo = (CategoryInfo) arguments.getSerializable(EXTRA_CATEGORY_INFO);
    }

    private void configList() {
        getBinding().srl.setOnRefreshListener(this);

        adapter = new ArticleAdapter();
        adapter.setOnItemClickListener((adapter, view, position) ->
                ArticleActivity.start(PostListFragment.this, (PostItem) adapter.getItem(position)));

        getBinding().rv.setAdapter(adapter);
        getBinding().rv.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()),
                DividerItemDecoration.VERTICAL_LIST, PrefUtils.getInstance().isNightTheme()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        getBinding().rv.setLayoutManager(layoutManager);
        getBinding().rv.setEmptyView(getBinding().ev);
        if (PrefUtils.getInstance().isNightTheme()) {
            getBinding().ev.useDarkTheme();
        }
        getBinding().rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                if (lastVisibleItem + 1 == totalItemCount && dy > 0) {
                    if (!loading) {
                        currentPage++;
                        getBinding().mpb.setVisibility(View.VISIBLE);
                        recyclerView.post(() -> fetchPostItems(false));
                    }
                }
            }
        });
        getBinding().rv.getFastScrollDelegate().setThumbDrawable(PalmUtils.getDrawableCompact(
                isDarkTheme() ? R.drawable.fast_scroll_bar_dark : R.drawable.fast_scroll_bar_light));
        getBinding().rv.getFastScrollDelegate().setThumbSize(16, 40);
        getBinding().rv.getFastScrollDelegate().setThumbDynamicHeight(false);

        fetchPostItems(false);
        getBinding().srl.setRefreshing(true);
    }

    private void fetchPostItems(boolean refresh) {
        LogUtils.d(currentPage);
        loading = true;
        letsCorpViewModel.fetchPostItems(categoryInfo, currentPage).observe(this, listResource -> {
            loading = false;
            getBinding().mpb.setVisibility(View.GONE);
            getBinding().srl.setRefreshing(false);
            if (listResource == null) {
                return;
            }
            switch (listResource.status) {
                case SUCCESS:
                    assert listResource.data != null;
                    LogUtils.d(listResource.data);
                    if (refresh) {
                        adapter.setNewData(listResource.data);
                    } else {
                        adapter.addData(listResource.data);
                    }
                    break;
                case FAILED:
                    ToastUtils.makeToast(listResource.message);
                    break;
                case CANCELED:
                    ToastUtils.makeToast(R.string.request_cancelled);
                    break;
            }
        });
    }

    @Override
    public void onRefresh() {
        currentPage = 0;
        fetchPostItems(true);
    }
}
