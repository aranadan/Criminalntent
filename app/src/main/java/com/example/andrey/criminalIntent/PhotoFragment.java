package com.example.andrey.criminalIntent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class PhotoFragment extends DialogFragment {
private static String ARG_BITMAP = "bitmap";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        File mPhotoFile = (File) getArguments().getSerializable(ARG_BITMAP);
        Bitmap bitmap = PictureUtils.getScaleBitmap(mPhotoFile.getPath(),getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo,null);
        ImageView imageView = view.findViewById(R.id.image_full_photo);
        imageView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(mPhotoFile.getPath())
                .setPositiveButton(android.R.string.ok,null)
                .create();
    }

    public static PhotoFragment newInstance(File mPhotoFile){
        Bundle args = new Bundle();
        args.putSerializable(ARG_BITMAP, mPhotoFile);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

   /* @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        File mPhotoFile = (File) getArguments().getSerializable(ARG_BITMAP);
        View v = inflater.inflate(R.layout.dialog_photo, container, false);
        ImageView imageView = v.findViewById(R.id.image_full_photo);
        Bitmap bitmap = PictureUtils.getScaleBitmap(mPhotoFile.getPath(),getActivity());
        imageView.setImageBitmap(bitmap);
        return v;
    }*/
}
