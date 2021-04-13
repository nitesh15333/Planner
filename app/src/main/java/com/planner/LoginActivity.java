package com.planner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;

    EditText mEmailET,mPasswordET;
    TextView mdonothaveaacntRstr,mforgotpassTv;

    Button mLoginBtn;
    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure Google Sign In



        mAuth = FirebaseAuth.getInstance();

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Login");


        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Init

        mEmailET=findViewById(R.id.loginmaieditext);
        mPasswordET=findViewById(R.id.loginpassword);
        mdonothaveaacntRstr=findViewById(R.id.donot_have_a_accnt_register);
        mLoginBtn=findViewById(R.id.loginbuttn);
        mforgotpassTv=findViewById(R.id.forgotpasswordTv);


        //login Button handle
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=mEmailET.getText().toString().trim();
                String password=mPasswordET.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                    //invalid email set error message
                    mEmailET.setError("Invalid Email");
                    mEmailET.setFocusable(true);
                }
                else{
                    loginUsr(Email,password);
                }

            }
        });

        //text view click handle
        mdonothaveaacntRstr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });

        //Recover Password
        mforgotpassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        //handle google sigin button



        //Init
        progressDialog=new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);



    }

    private void showRecoverPasswordDialog() {
        //Alert Dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this,R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Recover Password");

        //set linear layout
        LinearLayout linearLayout=new LinearLayout(this);
        //views to set in dialog
        final EditText emailET = new EditText(this);
        emailET.setHint("Email");
        emailET.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        emailET.setMinEms(16);

        linearLayout.addView(emailET);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        //Button Recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Input Email
                String email=emailET.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(LoginActivity.this,"Invalid Email",Toast.LENGTH_SHORT).show();
                }
                else{
                    beginRecovery(email);
                }

            }
        });

        //Button Cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        builder.create().show();
        //show dialog

    }

    private void beginRecovery(String email) {
        progressDialog.setMessage("Sending Email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Email Sent",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Failed...",Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void loginUsr(String email, String password) {
        progressDialog.setMessage("Logging In...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            //Get user mail and uid
                            String email=user.getEmail();
                            String uid=user.getUid();

                            //Hashmap
                            HashMap<Object,String> hashMap= new HashMap<>();
                            hashMap.put("email",email);
                            hashMap.put("uid",uid);
                            hashMap.put("onlineStatus","Online");
                            hashMap.put("name","");//add later
                            hashMap.put("phone","");
                            hashMap.put("image","");

                            //Firebasedatabase instance

                            FirebaseDatabase database=FirebaseDatabase.getInstance();
                            //databse path
                            DatabaseReference databaseReference=database.getReference("Users");
                            //putting hashmap
                            databaseReference.child(uid).setValue(hashMap);



                            Toast.makeText(LoginActivity.this,""+user.getEmail(),Toast.LENGTH_SHORT).show();
                            // Go to profile activity
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this,"Login Failed...",Toast.LENGTH_SHORT).show();

                            //updateUI(null);
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }
}
