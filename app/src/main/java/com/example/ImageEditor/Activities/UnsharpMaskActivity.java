package com.example.ImageEditor.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.example.ImageEditor.Fragments.UnsharpMaskFragment;

public class UnsharpMaskActivity extends FragmentActivity {
    public static final String IMAGE_URI = "URI_IMAGE";

    public static Intent getActivity(Context context, Uri uri) {
        Intent intent = new Intent(context, UnsharpMaskActivity.class);
        intent.putExtra(IMAGE_URI, uri);
        return intent;
    }

    @Override
    Fragment createFragment() {
        return UnsharpMaskFragment.newInstance();
    }
}
