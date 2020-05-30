package com.example.ImageEditor.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ImageEditor.Activities.ResizeActivity;
import com.example.ImageEditor.BitmapUnsharpMask;
import com.example.ImageEditor.DefaultMethods;
import com.example.ImageEditor.R;
import com.example.ImageEditor.ResultContract;

public class UnsharpMaskFragment extends Fragment implements ResultContract {
    private View view;
    private SeekBar seekBarAmount, seekBarTreshold;
    private Button buttonApply;
    private ImageView imageViewFinal;
    private float filterAmount;
    private int filterTreshold;
    private Bitmap bitmap;
    private BitmapUnsharpMask bitmapUnsharpMask;

    public static Fragment newInstance() {
        return new UnsharpMaskFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_unsharpmask, container, false);
        defineIds();

        Uri uriFromActivity = getUriFromActivity();
        if (uriFromActivity == null) {
            bitmap = DefaultMethods.loadBitmapExample(getContext());
        } else {
            bitmap = DefaultMethods.loadBitmapFromUri(getContext(), uriFromActivity);
        }
        setMainImageBitmap(bitmap);

        seekBarAmount.setProgress(0);
        seekBarAmount.setMax(50);
        seekBarTreshold.setProgress(0);
        seekBarTreshold.setMax(255);
        setMainImageBitmap(bitmap);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int id = seekBar.getId();
                if (id == seekBarAmount.getId()) {
                    filterAmount = (float) progress / 10;
                } else if (id == seekBarTreshold.getId()) {
                    filterTreshold = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        seekBarAmount.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBarTreshold.setOnSeekBarChangeListener(seekBarChangeListener);
        imageViewFinal.setOnClickListener(v -> checkIfPermissionGranted());
        buttonApply.setOnClickListener(v -> {
            if(bitmapUnsharpMask != null){
                bitmapUnsharpMask.cancel(true);
            }
            applyEffect();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == DefaultMethods.GALLERY_REQUEST_CODE) {
                    Uri uri = data.getData();

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
    public void getResult(Bitmap result) {
        setMainImageBitmap(result);
    }

    private void defineIds() {
        seekBarAmount = view.findViewById(R.id.seekBarAmount);
        seekBarTreshold = view.findViewById(R.id.seekBarThreshold);
        buttonApply = view.findViewById(R.id.buttonReset);
        imageViewFinal = view.findViewById(R.id.imageViewFinal);
    }

    private void applyEffect() {
        bitmapUnsharpMask = new BitmapUnsharpMask(this, bitmap, filterAmount, filterTreshold);
        bitmapUnsharpMask.execute();
    }

    private void setMainImageBitmap(Bitmap bitmap) {
        imageViewFinal.setImageBitmap(bitmap);
    }

    private Uri getUriFromActivity() {
        return getActivity().getIntent().getParcelableExtra(ResizeActivity.IMAGE_URI);
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
