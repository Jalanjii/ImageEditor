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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ImageEditor.Activities.ResizeActivity;
import com.example.ImageEditor.BitmapResizer;
import com.example.ImageEditor.DefaultMethods;
import com.example.ImageEditor.R;

public class ResizeFragment extends Fragment {
    private TextView textViewOriginal, textViewFinal;
    private EditText editTextWidth, editTextHeight;
    private ImageView imageViewFinal;
    private Button buttonApply, buttonImageReset;
    private View view;
    private Bitmap bitmap;

    public static Fragment newInstance() {
        return new ResizeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_imageresize, container, false);
        defineIds();
        Uri uriFromActivity = getUriFromActivity();
        if(uriFromActivity == null){
            bitmap = DefaultMethods.loadBitmapExample(getContext());
        } else {
            bitmap = DefaultMethods.loadBitmapUntouchedFromUri(getContext(), uriFromActivity);
        }
        setMainImageBitmap(bitmap);
        setTextViewSizeText(textViewOriginal, R.string.textView_originalImage, bitmap);

        buttonApply.setOnClickListener(v -> {
            Bitmap bitmapNew = BitmapResizer.getResizedBitmap(bitmap,
                    Integer.parseInt(editTextWidth.getText().toString()),
                    Integer.parseInt(editTextHeight.getText().toString()));

            setTextViewSizeText(textViewFinal, R.string.textView_newImage, bitmapNew);

            setMainImageBitmap(bitmapNew);
        });

        imageViewFinal.setOnClickListener(v-> checkIfPermissionGranted());

        buttonImageReset.setOnClickListener(v -> setMainImageBitmap(bitmap));


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == DefaultMethods.GALLERY_REQUEST_CODE) {
                    Uri uri = data.getData();

                    if(uri == null){
                        bitmap = DefaultMethods.loadBitmapExample(getContext());
                    } else {
                        bitmap = DefaultMethods.loadBitmapUntouchedFromUri(getContext(), uri);
                    }
                    setMainImageBitmap(bitmap);
                    setTextViewSizeText(textViewOriginal,R.string.textView_originalImage,bitmap);

                }
            }
        } catch (Exception e) {
            Log.e("FileSelectorActivity", "File select error", e);
        }
    }

    private void setTextViewSizeText(TextView textView, int text, Bitmap bitmap) {
        textView.setText(getString(text, String.valueOf(bitmap.getWidth()), String.valueOf(bitmap.getHeight())));
    }

    private void defineIds() {
        textViewOriginal = view.findViewById(R.id.textViewOriginal);
        textViewFinal = view.findViewById(R.id.textViewFinal);
        editTextWidth = view.findViewById(R.id.editTextWidth);
        editTextHeight = view.findViewById(R.id.editTextHeight);
        imageViewFinal = view.findViewById(R.id.imageViewFinal);
        buttonApply = view.findViewById(R.id.buttonReset);
        buttonImageReset = view.findViewById(R.id.buttonImageReset);
    }

    private void setMainImageBitmap(Bitmap bitmap) {
        imageViewFinal.setImageBitmap(bitmap);
    }

    private Uri getUriFromActivity() {
        return getActivity().getIntent().getParcelableExtra(ResizeActivity.IMAGE_URI);
    }

    private void checkIfPermissionGranted(){
        if (DefaultMethods.checkGalleryPermission(getContext())) {
            DefaultMethods.pickFromGallery(this);
        } else {
            bitmap = DefaultMethods.loadBitmapExample(getContext());
            DefaultMethods.showSnackBarForPermission(getActivity(), R.id.fragment_container);
        }
    }

}
