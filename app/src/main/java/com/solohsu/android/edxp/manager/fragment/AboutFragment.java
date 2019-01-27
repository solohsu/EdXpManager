package com.solohsu.android.edxp.manager.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.solohsu.android.edxp.manager.R;
import com.solohsu.android.edxp.manager.util.Utils;

public class AboutFragment extends Fragment {

    private static final Uri APP_SOURCE_CODE_URL = Uri.parse("https://github.com/solohsu/EdXpManager");
    private static final Uri EDXP_SOURCE_CODE_URL = Uri.parse("https://github.com/solohsu/EdXposed");


    public AboutFragment() {

    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        requireActivity().setTitle(R.string.nav_title_about);
        view.findViewById(R.id.app_source_code).setOnClickListener(v ->
                Utils.openLink(requireContext(), APP_SOURCE_CODE_URL));
        view.findViewById(R.id.edxp_source_code).setOnClickListener(v ->
                Utils.openLink(requireContext(), EDXP_SOURCE_CODE_URL));
    }
}
