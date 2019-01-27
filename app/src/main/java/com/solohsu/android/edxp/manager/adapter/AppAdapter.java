package com.solohsu.android.edxp.manager.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.solohsu.android.edxp.manager.R;
import com.solohsu.android.edxp.manager.util.ToastUtils;
import com.solohsu.android.edxp.manager.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private static final String TAG = "AppAdapter";
    private final ApplicationInfo.DisplayNameComparator displayNameComparator;
    private final Context context;
    private List<ApplicationInfo> fullList, showList;
    private List<String> whiteList;
    private List<String> blackList;
    private List<String> checkedList;
    private PackageManager pm;
    private ApplicationFilter filter;
    private Callback callback;
    private boolean isWhiteListMode;

    public AppAdapter(Context context, boolean isWhiteListMode) {
        this.context = context;
        this.isWhiteListMode = isWhiteListMode;
        fullList = showList = Collections.emptyList();
        whiteList = blackList = Collections.emptyList();
        checkedList = Collections.emptyList();
        filter = new ApplicationFilter();
        pm = context.getPackageManager();
        displayNameComparator = new ApplicationInfo.DisplayNameComparator(pm);
        AsyncTask.THREAD_POOL_EXECUTOR.execute(this::loadApps);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void updateList(boolean isWhiteListMode) {
        this.isWhiteListMode = isWhiteListMode;
        checkedList = isWhiteListMode ? whiteList : blackList;
        sortApps();
        if (callback != null) {
            callback.onDataReady();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_app, parent, false);
        return new ViewHolder(v);
    }

    private void loadApps() {
        fullList = pm.getInstalledApplications(0);
        AppHelper.makeSurePath();
        whiteList = AppHelper.getWhiteList();
        blackList = AppHelper.getBlackList();
        Log.d(TAG, "whiteList: " + whiteList);
        Log.d(TAG, "blackList: " + blackList);
        checkedList = isWhiteListMode ? this.whiteList : blackList;
        sortApps();
        if (callback != null) {
            callback.onDataReady();
        }
    }

    private void sortApps() {
        Collections.sort(fullList, (a, b) -> {
            boolean aChecked = checkedList.contains(a.packageName);
            boolean bChecked = checkedList.contains(b.packageName);
            if (aChecked == bChecked) {
                return displayNameComparator.compare(a, b);
            } else if (aChecked) {
                return -1;
            } else {
                return 1;
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApplicationInfo info = showList.get(position);

        holder.appIcon.setImageDrawable(info.loadIcon(pm));
        holder.appName.setText(Utils.getAppLabel(info, pm));
        holder.appPackage.setText(info.packageName);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(checkedList.contains(info.packageName));
        holder.checkBox.setOnCheckedChangeListener((v, isChecked) -> {
            boolean success = isChecked ?
                    AppHelper.addPackageName(isWhiteListMode, info.packageName) :
                    AppHelper.removePackageName(isWhiteListMode, info.packageName);
            if (success) {
                if (isChecked) checkedList.add(info.packageName);
                else checkedList.remove(info.packageName);
            } else {
                ToastUtils.showShortToast(context, R.string.add_package_failed);
                v.setChecked(!isChecked);
            }
        });
        holder.infoLayout.setOnClickListener(v -> {
            if (callback != null) callback.onItemClick(v, info);
        });
    }

    @Override
    public int getItemCount() {
        return showList.size();
    }

    public void filter(String constraint) {
        filter.filter(constraint);
    }

    public void refresh() {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(this::loadApps);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View infoLayout;
        ImageView appIcon;
        TextView appName;
        TextView appPackage;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            infoLayout = itemView.findViewById(R.id.info_layout);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            appPackage = itemView.findViewById(R.id.package_name);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }

    class ApplicationFilter extends Filter {

        private boolean lowercaseContains(String s, CharSequence filter) {
            return !TextUtils.isEmpty(s) && s.toLowerCase().contains(filter);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint == null || constraint.length() == 0) {
                showList = fullList;
            } else {
                showList = new ArrayList<>();
                String filter = constraint.toString().toLowerCase();
                for (ApplicationInfo info : fullList) {
                    if (lowercaseContains(Utils.getAppLabel(info, pm), filter)
                            || lowercaseContains(info.packageName, filter)) {
                        showList.add(info);
                    }
                }
            }
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifyDataSetChanged();
        }
    }

    public interface Callback {
        void onDataReady();
        void onItemClick(View v, ApplicationInfo info);
    }
}
