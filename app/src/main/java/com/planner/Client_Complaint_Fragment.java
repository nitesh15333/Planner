package com.planner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.planner.adapters.AdapterComplaints;
import com.planner.adapters.AdapterTask;
import com.planner.models.modelComplaints;
import com.planner.models.modelTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Client_Complaint_Fragment extends Fragment {

    RecyclerView recyclerView;
    EditText mTextEt;
    ImageButton sendbtn;

    String myUid;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentuser;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDbRef;

    List<modelComplaints> complaintsList;
    AdapterComplaints adapterComplaints;

    String name;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_client__complaint_, container, false);
        recyclerView=view.findViewById(R.id.chat_recycler_view);
        mTextEt=view.findViewById(R.id.textET);
        sendbtn=view.findViewById(R.id.snd_btn);

        firebaseAuth= FirebaseAuth.getInstance();
        currentuser=FirebaseAuth.getInstance().getCurrentUser();
        myUid=currentuser.getUid().trim();

        userDbRef=FirebaseDatabase.getInstance().getReference("Users");
        Query query=userDbRef.orderByChild("uid").equalTo(myUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    name=""+ds.child("name").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        readMessage();



        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get text from edit text
                String message= mTextEt.getText().toString().trim();
                //check if text is empty
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(getActivity(),"Cannot send empty text...",Toast.LENGTH_SHORT).show();

                }
                else {
                    sendmessage(message,name);
                }
                mTextEt.setText("");
            }
        });

        return view;

    }


    private void sendmessage(final String message,final String name) {



        String timestamp=String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("name",name);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);

        firebaseDatabase = FirebaseDatabase.getInstance();
        userDbRef = firebaseDatabase.getReference("Complaints");
        userDbRef.child(timestamp).setValue(hashMap);
    }
    private void readMessage() {
        complaintsList=new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDbRef = firebaseDatabase.getReference("Complaints");

        firebaseAuth= FirebaseAuth.getInstance();
        currentuser=FirebaseAuth.getInstance().getCurrentUser();

        Query userQuery = userDbRef.orderByChild("sender").equalTo(currentuser.getUid());
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                complaintsList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){

                    modelComplaints modelComplaints=ds.getValue(com.planner.models.modelComplaints.class);

                    complaintsList.add(modelComplaints);

                    adapterComplaints = new AdapterComplaints(getContext(),complaintsList);
                    //set adapter to recyclerview
                    recyclerView.setAdapter(adapterComplaints);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}