/**
 * Created by gxy on 18-2-13.
 */
package com.example.gxy.intel.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gxy.intel.Camera2BasicFragment;
import com.example.gxy.intel.R;

public class DiagnoseFragment extends android.support.v4.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diagnose_main, container, false);
        getFragmentManager().
                beginTransaction().
                replace(R.id.camera_container, Camera2BasicFragment.newInstance()).
                commit();
        return view;
    }
}
