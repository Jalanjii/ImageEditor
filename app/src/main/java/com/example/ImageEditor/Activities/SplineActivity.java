package com.example.ImageEditor.Activities;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.ImageEditor.Fragments.SplineFragment;

public class SplineActivity extends FragmentActivity {

    public static Intent getActivity(Context context){
        Intent intent = new Intent(context, SplineActivity.class);
        return intent;
    }

    @Override
    Fragment createFragment() {
        return SplineFragment.newInstance();
    }
}
