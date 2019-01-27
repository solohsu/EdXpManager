package com.solohsu.android.edxp.manager.fragment;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.solohsu.android.edxp.manager.R;
import com.solohsu.android.edxp.manager.adapter.AppAdapter;
import com.solohsu.android.edxp.manager.adapter.AppHelper;
import com.solohsu.android.edxp.manager.util.ToastUtils;

public class AppFragment extends Fragment implements AppAdapter.Callback {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private AppAdapter mAppAdapter;

    private SearchView.OnQueryTextListener mSearchListener;

    public AppFragment() {
        setRetainInstance(true);
    }

    public static AppFragment newInstance() {
        return new AppFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_app, menu);
        mSearchView = (SearchView) menu.findItem(R.id.app_search).getActionView();
        mSearchView.setOnQueryTextListener(mSearchListener);
        MenuItem whiteListMenuItem = menu.findItem(R.id.white_list_switch);
        whiteListMenuItem.setChecked(isWhiteListMode());
        whiteListMenuItem.setOnMenuItemClickListener(item -> {
            item.setChecked(!item.isChecked());
            if (AppHelper.setWhiteListMode(item.isChecked())) {
                updateUi(item.isChecked());
            } else {
                ToastUtils.showShortToast(requireContext(), R.string.mode_change_failed);
            }
            return true;
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = view.findViewById(R.id.recyclerView);

        final boolean isWhiteListMode = isWhiteListMode();
        changeTitle(isWhiteListMode);
        mAppAdapter = new AppAdapter(requireActivity(), isWhiteListMode);
        mRecyclerView.setAdapter(mAppAdapter);
        mAppAdapter.setCallback(this);

        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(mAppAdapter::refresh);

        mSearchListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAppAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAppAdapter.filter(newText);
                return false;
            }
        };
        return view;
    }

    private void changeTitle(boolean isWhiteListMode) {
        requireActivity().setTitle(isWhiteListMode ? R.string.title_white_list : R.string.title_black_list);
    }

    private void updateUi(boolean isWhiteListMode) {
        changeTitle(isWhiteListMode);
        mAppAdapter.updateList(isWhiteListMode);
    }

    private boolean isWhiteListMode() {
        return AppHelper.isWhiteListMode();
    }

    @Override
    public void onDataReady() {
        mSwipeRefreshLayout.setRefreshing(false);
        String queryStr = mSearchView != null ? mSearchView.getQuery().toString() : "";
        mAppAdapter.filter(queryStr);
    }

    @Override
    public void onItemClick(View v, ApplicationInfo info) {
        if (getFragmentManager() != null) {
            AppHelper.showMenu(requireActivity(), getFragmentManager(), v, info);
        }
    }
}
