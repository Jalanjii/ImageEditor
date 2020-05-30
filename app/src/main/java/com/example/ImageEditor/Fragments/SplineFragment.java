package com.example.ImageEditor.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ImageEditor.R;
import com.example.ImageEditor.Spline.CurveView;

public class SplineFragment extends Fragment {
    private CurveView view;

    public static Fragment newInstance() {
        return new SplineFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = new CurveView(getContext());
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.spline_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.spline_add:
                view.selectAdd();
                return true;

            case R.id.spline_move:
                view.selectMove();
                return true;

            case R.id.spline_delete:
                view.selectDelete();
                return true;

            case R.id.spline_clear:
                view.clearList();
                return true;

            case R.id.spline_view:
                view.setVisibilityControlPoints(false);
                return true;
        }
        return false;
    }
}
