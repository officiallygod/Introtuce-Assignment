package com.allen.introtuce;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class fragmentAddNewUsers extends Fragment {

    String imageLink = "photo";
    private CircleImageView profileImage;
    private ProgressDialog loadingDialog;
    private DatabaseReference userDataRef;
    private static final int REQUEST_CODE=1;
    private StorageReference storageReference;
    List<String> newListPhone = null;
    public static final String TITLE = "ADD USERS";
    private final String UID = "5qFP2xq8PGcRKTczgegGiPFWABC3";
    String uuid = String.valueOf(Instant.now().getEpochSecond());


    public static fragmentAddNewUsers newInstance() {

        return new fragmentAddNewUsers();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_new_users, container, false);

        final Calendar myCalendar = Calendar.getInstance();

        loadingDialog = new ProgressDialog(getActivity());
        userDataRef = FirebaseDatabase.getInstance("https://introtuce-officiallygod-default-rtdb" +
                ".firebaseio.com/").getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        profileImage = view.findViewById(R.id.add_new_user_image_view);
        EditText firstName = view.findViewById(R.id.add_user_first_name_text);
        EditText lastName = view.findViewById(R.id.add_user_last_name_text);
        EditText birthday = view.findViewById(R.id.add_user_dob_text);
        EditText country = view.findViewById(R.id.add_user_country_text);
        EditText hometown = view.findViewById(R.id.add_user_hometown_text);
        EditText state = view.findViewById(R.id.add_user_state_text);
        EditText phone = view.findViewById(R.id.add_user_phone_text);
        EditText telephone = view.findViewById(R.id.add_user_telephone_text);

        Button chooseImgBtn = view.findViewById(R.id.add_new_user_image_btn);
        RelativeLayout submitBtn = view.findViewById(R.id.add_new_user_relative);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                birthday.setText(sdf.format(myCalendar.getTime()));
            }

        };


        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        chooseImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraPermission();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();

                String firstNameVal = firstName.getText().toString().trim();
                String lastNameVal = lastName.getText().toString().trim();

                if(!imageLink.equals("photo")){
                    if(!(firstNameVal.isEmpty() || lastNameVal.isEmpty())) {
                        String birthdayVal = birthday.getText().toString().trim();

                        if(!(birthdayVal.isEmpty())) {

                            String countryVal = country.getText().toString().trim();
                            String hometownVal = hometown.getText().toString().trim();
                            String stateVal = state.getText().toString().trim();

                            if(!(countryVal.isEmpty() || hometownVal.isEmpty() || stateVal.isEmpty())) {

                                String phoneVal = phone.getText().toString().trim();

                                if(!phoneVal.isEmpty() && phoneVal.length() == 10) {

                                    String telephoneVal = telephone.getText().toString().trim();

                                    if(telephoneVal.isEmpty())
                                        telephoneVal = "Not Given";

                                    HashMap<String, String> user_value = new HashMap<>();
                                    user_value.put("photo", imageLink);
                                    user_value.put("first_name", firstNameVal);
                                    user_value.put("last_name", lastNameVal);
                                    user_value.put("birthday", birthdayVal);
                                    user_value.put("country", countryVal);
                                    user_value.put("hometown", hometownVal);
                                    user_value.put("state", stateVal);
                                    user_value.put("phone", phoneVal);
                                    user_value.put("telephone", telephoneVal);

                                    userDataRef.child("Users").child(uuid).setValue(user_value).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()) {

                                                Intent intent = new Intent(getActivity(),
                                                        MainActivity.class);
                                                startActivity(intent);
                                                Toast.makeText(getActivity(), "User Added Successfully", Toast.LENGTH_SHORT).show();
                                            }else {
                                                loadingDialog.dismiss();
                                                Toast.makeText(getActivity(), "User not Added ",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }else {
                                    loadingDialog.dismiss();
                                    Toast.makeText(getActivity(), "Enter your Phone No. correctly",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }else {
                                loadingDialog.dismiss();
                                Toast.makeText(getActivity(), "Enter your Address correctly",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            loadingDialog.dismiss();
                            Toast.makeText(getActivity(), "Enter your Birthday correctly",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        loadingDialog.dismiss();
                        Toast.makeText(getActivity(), "Enter your name correctly", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    loadingDialog.dismiss();
                    Toast.makeText(getActivity(), "Select an Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == -1){
                loadingDialog.show();
                Uri resultUri = result.getUri();
                File thumb_file = new File(resultUri.getPath());
                Bitmap thumb_bitmap = new Compressor(Objects.requireNonNull(getContext()))
                        .setQuality(60)
                        .compressToBitmap(thumb_file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                final byte[] thumb_byte = baos.toByteArray();

                String uuidRand = uuid.toString();

                final StorageReference filepath =
                        storageReference.child("user_image").child(UID).child(uuidRand+"jpg");

                filepath.putBytes(thumb_byte).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();

                    }

                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if(task.isSuccessful()){
                            final String downloadurl = task.getResult().toString();
                            imageLink = downloadurl;
                            Picasso.get().load(imageLink).placeholder(R.drawable.placeholder).into(profileImage);
                            loadingDialog.dismiss();
                        }else{
                            loadingDialog.dismiss();
                        }


                    }
                });

            }

        }
    }

    private void CameraPermission(){
        String[] permission={Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(getContext(), permission[0])== PackageManager.PERMISSION_GRANTED){
            Intent intent = CropImage.activity()
                    .getIntent(getContext());

            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
        }else{
            ActivityCompat.requestPermissions(getActivity(),permission,REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CameraPermission();
    }

}