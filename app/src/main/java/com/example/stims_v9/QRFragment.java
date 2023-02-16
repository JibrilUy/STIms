package com.example.stims_v9;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.MultiFormatOneDReader;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.OutputStream;
import java.util.Objects;


public class QRFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    EditText etQR;
    Button btnGenerateQR, btnSignOut;
    ImageView imgQR;
    String bitmap_name;

    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_q_r, container, false);


        etQR = (EditText) v.findViewById(R.id.etQR);
            imgQR = (ImageView) v.findViewById(R.id.imgQR);
            btnGenerateQR = (Button) v.findViewById(R.id.btnGenerateQR);
            btnSignOut = v.findViewById(R.id.btnSignOut);
            mAuth = FirebaseAuth.getInstance();

            btnSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent (getActivity(), com.example.stims_v9.Login.SignIn.class);
                    startActivity(intent);
                }
            });


            String inputText = etQR.getText().toString();



                btnGenerateQR.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if(TextUtils.isEmpty(etQR.getText())){

                            Toast.makeText(getActivity(), "NO TEXT", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(getActivity(), "QR ADDED TO GALLERY", Toast.LENGTH_SHORT).show();
                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                            try {
                                BitMatrix bitMatrix = multiFormatWriter.encode(etQR.getText().toString(),
                                        BarcodeFormat.QR_CODE, 275, 275);

                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                                imgQR.setImageBitmap(bitmap);
                                // BULLSHIT ITO
                                saveImage(bitmap);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });



        return v;
    }
    private void saveImage(Bitmap bitmap){
        OutputStream fos;
        bitmap_name = etQR.getText().toString();

        try{
            ContentResolver resolver = getActivity().getContentResolver();

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"STIms_QR_Generated_" + bitmap_name + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"Image/jpg");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
            fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }}catch (Exception e){
            Log.d("error", e.toString());
        }
    }
}