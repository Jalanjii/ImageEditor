package com.example.ImageEditor.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ImageEditor.Activities.RetouchActivity;
import com.example.ImageEditor.DefaultMethods;
import com.example.ImageEditor.GaussianBlur;
import com.example.ImageEditor.R;

public class RetouchFragment extends Fragment {
    private View view;
    private Bitmap bitmap;
    private SeekBar seekBarEffect, seekBarCircle;
    private Button buttonReset, buttonSelectNew;
    private ImageView imageView;
    private int circleCenterX, circleCenterY;
    private int effect = 1, radius = 1;

    public static Fragment newInstance() {
        return new RetouchFragment();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_retouch, container, false);
        defineIds();
        setSeekBarDefaults();
        Uri uriFromActivity = getUriFromActivity();
        checkUriAndLoad(uriFromActivity);
        setMainImageBitmap(bitmap);

        buttonReset.setOnClickListener(v -> setMainImageBitmap(bitmap));
        buttonSelectNew.setOnClickListener(v -> checkIfPermissionGranted());

        imageView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                circleCenterX = (int) ((event.getX() / imageView.getMeasuredWidth()) * bitmap.getWidth());
                circleCenterY = (int) ((event.getY() / imageView.getMeasuredHeight()) * bitmap.getHeight());

                setMainImageBitmap(GaussianBlur.blur(bitmap, effect, circleCenterX, circleCenterY, radius));
            }
            return true;
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == DefaultMethods.GALLERY_REQUEST_CODE) {
                    Uri uri = data.getData();
                    checkUriAndLoad(uri);
                    setMainImageBitmap(bitmap);

                }
            }
        } catch (Exception e) {
            Log.e("FileSelectorActivity", "File select error", e);
        }
    }

    private void defineIds() {
        seekBarCircle = view.findViewById(R.id.seekBarCircle);
        seekBarEffect = view.findViewById(R.id.seekBarEffect);
        buttonReset = view.findViewById(R.id.buttonReset);
        buttonSelectNew = view.findViewById(R.id.buttonSelectNew);
        imageView = view.findViewById(R.id.imageView);
    }

    private Uri getUriFromActivity() {
        return getActivity().getIntent().getParcelableExtra(RetouchActivity.IMAGE_URI);
    }

    private void setSeekBarDefaults() {
        seekBarEffect.setProgress(0);
        seekBarEffect.setMax(99);
        seekBarCircle.setProgress(0);
        seekBarCircle.setMax(19);

        seekBarCircle.setOnSeekBarChangeListener(seekBarChangeListener());
        seekBarEffect.setOnSeekBarChangeListener(seekBarChangeListener());
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int id = seekBar.getId();
                if (id == seekBarCircle.getId()) {
                    radius = progress;
                } else if (id == seekBarEffect.getId()) {
                    effect = progress;
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

    private void setMainImageBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    private void checkUriAndLoad(Uri uri) {
        if (uri == null) {
            bitmap = DefaultMethods.loadBitmapExample(getContext());
        } else {
            bitmap = DefaultMethods.loadBitmapFromUri(getContext(), uri, 1280, 720);
        }
    }

    private void checkIfPermissionGranted() {
        if (DefaultMethods.checkGalleryPermission(getContext())) {
            DefaultMethods.pickFromGallery(this);
        } else {
            bitmap = DefaultMethods.loadBitmapExample(getContext());
            DefaultMethods.showSnackBarForPermission(getActivity(), R.id.fragment_container);
        }
    }
}
