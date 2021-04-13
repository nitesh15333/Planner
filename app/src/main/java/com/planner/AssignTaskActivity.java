package com.planner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.planner.adapters.AdapterUsers;
import com.planner.models.modelUser;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AssignTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    TextView datedialogbutton;
    TextView test_selected_date;
    TextView test;
    private Spinner mySpinner;
    public String picked_date,uid;
    EditText task_name,task_description;
    Button tasksubmitbtn;
    // Custom Spinner adapter (ArrayAdapter<User>)
    // You can define as a private to use it in the all class
    // This is the object that is going to do the "magic"
    private AdapterUsers adapter;
    List<modelUser> userList;

    FirebaseAuth firebaseAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDbRef;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_task);
        datedialogbutton=findViewById(R.id.datepicker);
        test_selected_date=findViewById(R.id.test_selected_date);
        tasksubmitbtn=findViewById(R.id.tasksubmitbtn);
        test=findViewById(R.id.test);
        task_name=findViewById(R.id.task_name);
        task_description=findViewById(R.id.task_description);

        progressDialog=new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Sending Task");

        datedialogbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker=new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(),"Select Date");

            }
        });

        userList = new ArrayList<>();


        //get all users
        getAllUsers();

        tasksubmitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendTaskToServer();
            }
        });
    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c=Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK,dayOfMonth);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.YEAR,year);


        String currentdatestring= DateFormat.getDateInstance().format(c.getTime());
        picked_date=currentdatestring;
        test_selected_date.setText(currentdatestring);

    }

    private void getAllUsers() {
        //get current user
        final FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();

        //get path of user
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    final modelUser modelUser =ds.getValue(com.planner.models.modelUser.class);

                    if(!modelUser.getUid().equalsIgnoreCase(fuser.getUid())){
                        userList.add(modelUser);
                    }
                    adapter = new AdapterUsers(getApplicationContext(),R.layout.spinner,userList);
                    //set adapter to recyclerview
                    mySpinner=findViewById(R.id.user_spinner);
                    mySpinner.setAdapter(adapter); // Set the custom adapter to the spinner
                    mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            modelUser user=adapter.getItem(position);
                            uid=user.getUid();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void SendTaskToServer() {
        String task_name_string=task_name.getText().toString().trim();
        String task_description_string=task_description.getText().toString().trim();
        String timestamp=String.valueOf(System.currentTimeMillis());

        HashMap<Object,String> hashMap= new HashMap<>();
        hashMap.put("task_name",task_name_string);
        hashMap.put("task_description",task_description_string);
        hashMap.put("submission_date",picked_date);
        hashMap.put("task_status","nill");
        hashMap.put("id",uid);
        hashMap.put("task_id",timestamp);

        //Firebasedatabase instance
        progressDialog.show();

        firebaseDatabase = FirebaseDatabase.getInstance();
        userDbRef = firebaseDatabase.getReference("Task");
        userDbRef.child(timestamp).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    task_name.setText("");
                    task_description.setText("");



                }else {
                    progressDialog.dismiss();
                    Toast.makeText(AssignTaskActivity.this, "Failed.",
                            Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AssignTaskActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}