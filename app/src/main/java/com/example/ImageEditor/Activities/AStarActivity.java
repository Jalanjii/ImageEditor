package com.example.ImageEditor.Activities;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.ImageEditor.Fragments.AStarFragment;

public class AStarActivity extends FragmentActivity {

    public static Intent getActivity(Context context) {
        Intent intent = new Intent(context, AStarActivity.class);

        return intent;
    }

    @Override
    Fragment createFragment() {
        return AStarFragment.newInstance();
    }
}
