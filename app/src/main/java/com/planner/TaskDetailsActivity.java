package com.planner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.planner.adapters.AdapterTask;
import com.planner.models.modelTask;

import java.util.HashMap;

public class TaskDetailsActivity extends AppCompatActivity {

    TextView tname,tdescription,tdeadline,tstatus;
    String t_name,t_desc,t_deadline,t_status,t_id;
    Button statuschangebutton_initialised;

    String status_constant="";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDbRef;
    FirebaseUser currentuser;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        tname=findViewById(R.id.t_name);
        tdescription=findViewById(R.id.t_description);
        tdeadline=findViewById(R.id.tdeadline);
        tstatus=findViewById(R.id.t_status);
        statuschangebutton_initialised=findViewById(R.id.statuschangebutton_initialised);


        Intent intent=getIntent();
        t_name=intent.getStringExtra("tname");
        t_desc=intent.getStringExtra("tdescription");
        t_deadline=intent.getStringExtra("tdeadline");
        t_status=intent.getStringExtra("tstatus");
        t_id=intent.getStringExtra("tid");

        tname.setText(t_name);
        tdescription.setText(t_desc);
        tdeadline.setText(t_deadline);
        tstatus.setText(t_status);

        firebaseDatabase = FirebaseDatabase.getInstance();
        userDbRef = firebaseDatabase.getReference("Task");
        HashMap<Object,String> hashMap= new HashMap<>();


        if (t_status.equals("nill")){
            statuschangebutton_initialised.setText("Started");
            status_constant="Started";
        }
        else if (t_status.equals("Started")){
            statuschangebutton_initialised.setText("In Progress");
            status_constant="In_Progress";
        }
        else if (t_status.equals("In_Progress")){
            statuschangebutton_initialised.setText("Completed");
            status_constant="Completed";
        }
        else if (t_status.equals("Completed")){
            statuschangebutton_initialised.setVisibility(View.INVISIBLE);
            status_constant="";
        }
        progressDialog=new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Updating Status");



        statuschangebutton_initialised.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                userDbRef.child(t_id).child("task_status").setValue(status_constant).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            status_constant="";
                            finish();


                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(TaskDetailsActivity.this, "Failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TaskDetailsActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });


            }
        });


    }
}