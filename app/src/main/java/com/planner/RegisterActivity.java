package com.planner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText mRegisternew,mpassword,mname,mphone;
    Button mRegisternewBttn;
    TextView mAlreadyhaveaccnt;
    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Create Account");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        // Init
        mRegisternew=findViewById(R.id.registermaieditext);
        mpassword=findViewById(R.id.registerpassword);
        mRegisternewBttn=findViewById(R.id.registernewbuttn);
        mAlreadyhaveaccnt=findViewById(R.id.have_a_accnt_login);
        mname=findViewById(R.id.registernameeditext);
        mphone=findViewById(R.id.registerphoneeditext);

        progressDialog=new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Registering User...");

        mRegisternewBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=mRegisternew.getText().toString().trim();
                String passwrd=mpassword.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                     mRegisternew.setError("Invalid Email");
                     mRegisternew.setFocusable(true);
                }
                else if (TextUtils.isEmpty(mname.getText().toString())){
                    mname.setError("Enter Username");
                    mname.setFocusable(true);

                }
                else if (TextUtils.isEmpty(mname.getText())){
                    mname.setError("Enter Phone");
                    mname.setFocusable(true);

                }
                else if (passwrd.length()<6){
                    mpassword.setError("Min 6 char");
                    mpassword.setFocusable(true);

                }
                else{
                    registerUser(email,passwrd);
                }

            }
        });

        //handle login textvieew
        mAlreadyhaveaccnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();

            }
        });

    }

    private void registerUser(String email, String passwrd) {

        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, passwrd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Get user mail and uid
                            String email=user.getEmail();
                            String uid=user.getUid();
                            String name=mname.getText().toString().trim();
                            String phone=mphone.getText().toString().trim();


                            //Hashmap
                            HashMap<Object,String>hashMap= new HashMap<>();
                            hashMap.put("email",email);
                            hashMap.put("uid",uid);
                            hashMap.put("name",name);//add later
                            hashMap.put("phone",phone);
                            hashMap.put("image","");
                            hashMap.put("type","client");


                            //Firebasedatabase instance

                            FirebaseDatabase database=FirebaseDatabase.getInstance();
                            //databse path
                            DatabaseReference databaseReference=database.getReference("Users");
                            //putting hashmap
                            databaseReference.child(uid).setValue(hashMap);

                            Toast.makeText(RegisterActivity.this,"Registered...\n"+user.getEmail(),Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this,"Registration Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
