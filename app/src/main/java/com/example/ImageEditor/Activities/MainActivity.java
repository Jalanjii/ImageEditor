package com.example.ImageEditor.Activities;

import androidx.fragment.app.Fragment;

import com.example.ImageEditor.Fragments.MainFragment;

public class MainActivity extends FragmentActivity {

    @Override
    Fragment createFragment() {
        return MainFragment.newInstance();
    }
}
