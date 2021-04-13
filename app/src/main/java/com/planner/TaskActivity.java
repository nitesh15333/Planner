package com.planner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.planner.adapters.AdapterTask;
import com.planner.models.modelTask;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<modelTask> taskList;
    AdapterTask adapterTask;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser currentuser;
    FirebaseAuth firebaseAuth;
    Query userDbRef;

    TextView notasktv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        recyclerView=findViewById(R.id.taskrecyclerview);

        firebaseAuth= FirebaseAuth.getInstance();
        currentuser=FirebaseAuth.getInstance().getCurrentUser();

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(TaskActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        notasktv=findViewById(R.id.tv_no_task);

        taskList=new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDbRef = firebaseDatabase.getReference("Task");

        Query userQuery = userDbRef.orderByChild("id").equalTo(currentuser.getUid());
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){

                    modelTask modelTask=ds.getValue(com.planner.models.modelTask.class);

                    taskList.add(modelTask);
                    if (taskList.size()!=0){
                        notasktv.setVisibility(View.INVISIBLE);
                    }
                    adapterTask = new AdapterTask(TaskActivity.this,taskList);
                    //set adapter to recyclerview
                    recyclerView.setAdapter(adapterTask);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


}

}