package com.example.stims_v9;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.MultiFormatOneDReader;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.OutputStream;
import java.util.Objects;


public class QRFragment extends Fragment {


    private final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private final DatabaseReference root = studentDatabase.getReference();

    EditText etQR;
    Button btnGenerateQR, btnSaveQR;
    ImageView imgQR;
    String bitmap_name, userId, userName, name;

    Bitmap bitmap;

    FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_q_r, container, false);


            imgQR = v.findViewById(R.id.imgQR);
            btnGenerateQR = v.findViewById(R.id.btnGenerateQR);
            btnSaveQR = v.findViewById(R.id.btnSaveQR);

            mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        DatabaseReference userDataRef = root.child("UserData").child(userId);
        userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue(String.class);
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){   }});



                btnGenerateQR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        name = userName;
                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                            try {
                                BitMatrix bitMatrix = multiFormatWriter.encode(userId, BarcodeFormat.QR_CODE, 275, 275);
                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                imgQR.setImageBitmap(bitmap);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                    }
                });

                btnSaveQR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "QR ADDED TO GALLERY", Toast.LENGTH_SHORT).show();
                        saveImage(bitmap);
                    }
                });



        return v;
    }
    private void saveImage(Bitmap bitmap){
        OutputStream fos;

        try{
            ContentResolver resolver = getActivity().getContentResolver();

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"STIms_QR_Generated_" + "something" + ".jpg");
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