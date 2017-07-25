package com.ksni.roots.ngsales.domain;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.*;
import com.ksni.roots.ngsales.util.Helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by #roots on 15/09/2015.
 */
public class CustomerPhoto extends Fragment {
    private OnCompleteListener mListener;
    private Uri imageToUploadUri = null;


    public static interface OnCompleteListener {
        public abstract void onCompleteCustomerPhoto();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }


    public void onViewCreated (View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mListener.onCompleteCustomerPhoto();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ui_customer_photo, container, false);
        ImageView img = (ImageView)v.findViewById(R.id.imgPhoto);

        if (CustomerInput.bitmap!=null) {
            img.setImageBitmap(CustomerInput.bitmap);
            img.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageToUploadUri = null;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File file = Helper.getOutputMediaPhotoFile();
                    takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    Uri fileUri = Uri.fromFile(file);
                    imageToUploadUri = fileUri;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(takePictureIntent, 0);
                }

            }
        });


        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == getActivity().RESULT_OK) {
                if (imageToUploadUri!=null) {
                    Bitmap takenImage = BitmapFactory.decodeFile(imageToUploadUri.getPath());

                    File f = new File(imageToUploadUri.getPath());

                    Matrix m = new Matrix();
                    m.postRotate( Helper.rotate(f) );

                    takenImage = Bitmap.createBitmap(takenImage,
                            0, 0, takenImage.getWidth(), takenImage.getHeight(),
                            m, true);


                    ImageView img = (ImageView) getView().findViewById(R.id.imgPhoto);
                    CustomerInput.bitmap = takenImage;


                    img.setImageBitmap(takenImage);
                    img.setScaleType(ImageView.ScaleType.FIT_XY);

                    com.ksni.roots.ngsales.model.Customer c = (com.ksni.roots.ngsales.model.Customer)getActivity().getIntent().getSerializableExtra("objCust");
                    if (c!=null) {

                        Bitmap xImage = BitmapFactory.decodeFile(imageToUploadUri.getPath());
                        xImage = Helper.rotateImage(xImage,90);
                        String data  =Helper.getStrResizePhotoBase64(xImage, 70,124,Bitmap.CompressFormat.JPEG,100);

                        if (com.ksni.roots.ngsales.model.Customer.updatePictureProfile( getActivity(), c.getCustomerNumber(), data))
                            Log.e("update pic ok","ok");

                        Helper.deleteFile(imageToUploadUri.getPath());
                    }

                }
            }
        }
    }

}
