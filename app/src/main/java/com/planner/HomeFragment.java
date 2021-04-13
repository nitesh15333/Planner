package com.planner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    List<modelTask> taskList;
    AdapterTask adapterTask;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser currentuser;
    FirebaseAuth firebaseAuth;
    Query userDbRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView=view.findViewById(R.id.taskrecyclerview);

        firebaseAuth= FirebaseAuth.getInstance();
        currentuser=FirebaseAuth.getInstance().getCurrentUser();

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        taskList=new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDbRef = firebaseDatabase.getReference("Task");


        userDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){

                    modelTask modelTask=ds.getValue(com.planner.models.modelTask.class);

                    taskList.add(modelTask);

                    adapterTask = new AdapterTask(getActivity(),taskList);
                    //set adapter to recyclerview
                    recyclerView.setAdapter(adapterTask);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
}