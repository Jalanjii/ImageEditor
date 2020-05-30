package com.example.ImageEditor.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ImageEditor.Activities.AStarActivity;
import com.example.ImageEditor.Activities.ResizeActivity;
import com.example.ImageEditor.Activities.RetouchActivity;
import com.example.ImageEditor.Activities.SplineActivity;
import com.example.ImageEditor.Activities.UnsharpMaskActivity;
import com.example.ImageEditor.BitmapFilters;
import com.example.ImageEditor.DefaultMethods;
import com.example.ImageEditor.R;


public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    private static final String FILTER_BLACKNWHITE = "BLACK AND WHITE";
    private static final String FILTER_CHANGECOLOR = "CHANGE COLOR";
    private static final String FILTER_INVERT = "INVERT";
    private String currentFilter = FILTER_CHANGECOLOR;
    private RadioButton filter_changecolor, filter_blackwhite, filter_invert;
    private RadioGroup filterGroup;
    private View view;
    private ImageView imageViewSource;
    private SeekBar seekBarRed, seekBarGreen, seekBarBlue;
    private Button buttonApply;
    private Bitmap bitmap;
    private Uri defaultImageUri;


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);


        defineIds();
//        checkIfPermissionGranted();
        bitmap = DefaultMethods.loadBitmapExample(getContext());
        setMainImageBitmap(bitmap);

        setRGBSeekBarDefaults();

        setRGBSeekBarChangeListener(createRGBSeekBarChangeListener());

        filterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == filter_blackwhite.getId()) {
                currentFilter = FILTER_BLACKNWHITE;
            } else if (checkedId == filter_changecolor.getId()) {
                currentFilter = FILTER_CHANGECOLOR;
            } else if (checkedId == filter_invert.getId()) {
                currentFilter = FILTER_INVERT;
            }
        });


        imageViewSource.setOnClickListener(v -> checkIfPermissionGranted());


        buttonApply.setOnClickListener(v -> {
            switch (currentFilter) {
                case FILTER_BLACKNWHITE:
                    setMainImageBitmap(BitmapFilters.blackNWhite(bitmap));
                    break;
                case FILTER_CHANGECOLOR:
                    setMainImageBitmap(BitmapFilters.changeBitmapColor(bitmap));
                    break;
                case FILTER_INVERT:
                    setMainImageBitmap(BitmapFilters.invert(bitmap));
                    break;
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == DefaultMethods.GALLERY_REQUEST_CODE) {
                    Uri uri = data.getData();

                    defaultImageUri = uri;

                    if (uri == null) {
                        bitmap = DefaultMethods.loadBitmapExample(getContext());
                    } else {
                        bitmap = DefaultMethods.loadBitmapFromUri(getContext(), uri);
                    }
                    setMainImageBitmap(bitmap);

                }
            }
        } catch (Exception e) {
            Log.e("FileSelectorActivity", "File select error", e);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.startResizer:
                startActivity(ResizeActivity.getActivity(getContext(), defaultImageUri));
                return true;

            case R.id.startAStar:
                startActivity(AStarActivity.getActivity(getContext()));
                return true;

            case R.id.startUnsharp:
                startActivity(UnsharpMaskActivity.getActivity(getContext(), defaultImageUri));
                return true;

            case R.id.startSpline:
                startActivity(SplineActivity.getActivity(getContext()));
                return true;

            case R.id.startRetouch:
                startActivity(RetouchActivity.getActivity(getContext(), defaultImageUri));
                return true;
        }
        return false;
    }

    private void defineIds() {
        filterGroup = view.findViewById(R.id.select_filters);
        filter_blackwhite = view.findViewById(R.id.filter_blackNWhite);
        filter_changecolor = view.findViewById(R.id.filter_changeColor);
        filter_invert = view.findViewById(R.id.filter_invert);
        imageViewSource = view.findViewById(R.id.imageView);
        seekBarRed = view.findViewById(R.id.seekBarEffect);
        seekBarGreen = view.findViewById(R.id.seekBarCircle);
        seekBarBlue = view.findViewById(R.id.seekBarBlue);
        buttonApply = view.findViewById(R.id.buttonReset);
    }

    private void setMainImageBitmap(Bitmap bitmap) {
        imageViewSource.setImageBitmap(bitmap);
    }

    private void setRGBSeekBarDefaults() {
        seekBarRed.setMax(BitmapFilters.MAX_RGB_VALUE);
        seekBarGreen.setMax(BitmapFilters.MAX_RGB_VALUE);
        seekBarBlue.setMax(BitmapFilters.MAX_RGB_VALUE);
        seekBarRed.setProgress(BitmapFilters.MAX_RGB_VALUE);
        seekBarGreen.setProgress(BitmapFilters.MAX_RGB_VALUE);
        seekBarBlue.setProgress(BitmapFilters.MAX_RGB_VALUE);
    }

    private void setRGBSeekBarChangeListener(SeekBar.OnSeekBarChangeListener seekBarChangeListener) {
        seekBarRed.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBarGreen.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBarBlue.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    private void checkIfPermissionGranted() {
        if (DefaultMethods.checkGalleryPermission(getContext())) {
            DefaultMethods.pickFromGallery(this);
        } else {
            bitmap = DefaultMethods.loadBitmapExample(getContext());
            DefaultMethods.showSnackBarForPermission(getActivity(), R.id.fragment_container);
        }
    }


    private SeekBar.OnSeekBarChangeListener createRGBSeekBarChangeListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar == seekBarRed) {
                    BitmapFilters.changeRed(progress);
                } else if (seekBar == seekBarGreen) {
                    BitmapFilters.changeGreen(progress);
                } else if (seekBar == seekBarBlue) {
                    BitmapFilters.changeBlue(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

}
