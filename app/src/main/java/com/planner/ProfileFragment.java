package com.planner;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    String uid;


    //path where image will go
    String storagepath = "User_profile_Img/";
    //permission consts
    private static final int CAMERA_REQUEST_CODE =100;
    private static final int STORAGE_REQUEST_CODE =200;
    private static final int IMAGE_PICK_GALLERY_CODE =300;
    private static final int IMAGE_PICK_CAMERA_CODE =400;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;
    ImageView avatarIV;
    TextView nameTV,emailTV,phoneTV;
    Button fab;
    Button settingsfab;

    ProgressDialog pd;




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth= FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");
        storageReference = getInstance().getReference();


        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        cameraPermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init views
        avatarIV=view.findViewById(R.id.avatarIV);
        nameTV=view.findViewById(R.id.name_TV);
        emailTV=view.findViewById(R.id.emailTV);
        phoneTV=view.findViewById(R.id.phoneTV);
        settingsfab=view.findViewById(R.id.logoutbttn);
        fab=view.findViewById(R.id.fab);


        pd=new ProgressDialog(getActivity(),R.style.AppCompatAlertDialogStyle);

        Query query= databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds :dataSnapshot.getChildren()){
                    String name=""+ds.child("name").getValue();
                    String email=""+ds.child("email").getValue();
                    String phone=""+ds.child("phone").getValue();
                    String image=""+ds.child("image").getValue();

                    nameTV.setText(name);
                    phoneTV.setText(phone);
                    emailTV.setText(email);
                    try{
                        Picasso.get().load(image).into(avatarIV);

                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.ic_profile_24).into(avatarIV);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        settingsfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                CheckUserStatus();
            }
        });

        CheckUserStatus();
        return view;
    }

    private void CheckUserStatus(){
        //get current user
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user != null){
            uid=user.getUid();
        }
        else {
            startActivity(new Intent(getActivity(),MainActivity.class));
        }
    }

    private boolean checkStoragePermission(){

        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission(){
        requestPermissions(storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){

        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission(){
        requestPermissions(cameraPermission,CAMERA_REQUEST_CODE);
    }


    private void showEditProfileDialog() {
        String options[]={"Edit Profile Pics","Edit Name","Edit Phone"};
        //alert Dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(),R.style.AppCompatAlertDialogStyle);
        //setting title
        builder.setTitle("Choose Action");
        //setting menu
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    //edit profile pic
                    pd.setMessage("Editing Profile pic");
                    showImagePicDialog();

                }
                else if (which == 1){
                    //edit name
                    pd.setMessage("Editing Name");
                    shownamephoneupdateDialog("name");

                }
                else if (which == 2){
                    //edit phone
                    pd.setMessage("Editing Phone number");
                    shownamephoneupdateDialog("phone");

                }

            }
        });
        //create
        builder.create().show();
    }

    private void shownamephoneupdateDialog(final String key) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(),R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Update"+key);
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter"+key);
        editText.setHintTextColor(getResources().getColor(R.color.LIGHT_GRAY));
        editText.setTextColor(getResources().getColor(R.color.BLACK));
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String value=editText.getText().toString().trim();

                if (!TextUtils.isEmpty(value)){
                    HashMap<String,Object> result= new HashMap<>();
                    result.put(key,value);
                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),"Update Succesfull",Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            });
                    if (key.equals("name")){
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
                        Query query=reference.orderByChild("uid").equalTo(uid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child=ds.getKey();
                                    dataSnapshot.getRef().child(child).child("uName").setValue(value);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        //ubdate name in current users in comment section
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child=ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("Comments")){
                                        String child1=""+dataSnapshot.child(child).getKey();
                                        Query child2=FirebaseDatabase.getInstance().getReference("Posts").child(child1).child("Comments").orderByChild("uid").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds:dataSnapshot.getChildren()){
                                                    String child=ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("uName").setValue(value);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }


                }
                else {
                    Toast.makeText(getActivity(),"Empty feild",Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.create().show();
    }

    private void showImagePicDialog() {

        String options[]={"Camera","Gallery"};
        //alert Dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        //setting title
        builder.setTitle("Pick Image From");
        //setting menu
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    //camera
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else{
                        pickFromCamera();
                    }

                }
                else if (which == 1){
                    //Gallery
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }

            }
        });
        //create
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean cameraAccepted= grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted= grantResults[1]== PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickFromCamera();

                    }
                    else {
                        Toast.makeText(getActivity(),"Please enable camera & storage permission",Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean writeStorageAccepted= grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickFromGallery();

                    }
                    else {
                        Toast.makeText(getActivity(),"Please enable storage permission",Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK){

            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();

                uploadProfilePic(image_uri);

            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){

                uploadProfilePic(image_uri);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePic(final Uri image_uri) {
        pd.show();

        String filePathAndName=storagepath+"_"+user.getUid();
        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask =taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                final Uri downloadUri = uriTask.getResult();

                if(uriTask.isSuccessful()){
                    HashMap<String,Object> results= new HashMap<>();
                    results.put("image",downloadUri.toString());

                    databaseReference.child(user.getUid()).updateChildren(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),"Image Updated...",Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),"Error Updating image",Toast.LENGTH_SHORT).show();

                                }
                            });
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
                    Query query=reference.orderByChild("uid").equalTo(uid);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds:dataSnapshot.getChildren()){
                                String child=ds.getKey();
                                dataSnapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //updating image in comment section as well
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds:dataSnapshot.getChildren()){
                                String child=ds.getKey();
                                if (dataSnapshot.child(child).hasChild("Comments")){
                                    String child1=""+dataSnapshot.child(child).getKey();
                                    Query child2=FirebaseDatabase.getInstance().getReference("Posts").child(child1).child("Comments").orderByChild("uid").equalTo(uid);
                                    child2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds:dataSnapshot.getChildren()){
                                                String child=ds.getKey();
                                                dataSnapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                else {
                    pd.dismiss();
                    Toast.makeText(getActivity(),"Some error occured",Toast.LENGTH_SHORT).show();
                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pickFromGallery() {
        Intent gelleryIntent=new Intent(Intent.ACTION_PICK);
        gelleryIntent.setType("image/*");
        startActivityForResult(gelleryIntent,IMAGE_PICK_GALLERY_CODE);

    }

    private void pickFromCamera() {

        //Intent picking image from device
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        //put image uri
        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //intent to start camera
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);


    }



}