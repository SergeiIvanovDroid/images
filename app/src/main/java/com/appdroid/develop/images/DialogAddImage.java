package com.appdroid.develop.images;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;

public class DialogAddImage extends DialogFragment {


    private Unbinder unbinder;
    private Context context;
    private Uri uriImage;
    private Realm realm;
    private OnImageAddListener onImageAddListener;
    @BindView(R.id.imageViewAddDialog)ImageView imageViewAdd;
    @BindView(R.id.editTextAddTags)EditText editTextAddTags;
    @BindView(R.id.buttonPositiveAddImage)Button buttonPositive;
    @BindView(R.id.buttonNegativeAddImage)Button buttonNegative;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_image,null);
        context = getActivity();
        onImageAddListener = (OnImageAddListener)context;
        unbinder = ButterKnife.bind(this, view);
        Glide.with(this)
                .load(uriImage)
                .into(imageViewAdd);
        buttonPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tags = editTextAddTags.getText().toString();
                if(tags.equals("")){
                    Toast.makeText(context, R.string.error_empty_tags,Toast.LENGTH_LONG).show();
                }else{
                    onImageAddListener.onCompleted(System.currentTimeMillis(),tags.replaceAll(" ",""),getRealPathFromURI(uriImage));
                    getDialog().dismiss();
                }

            }
        });
        buttonNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setUriImage(Uri uriImage) {
        this.uriImage = uriImage;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
