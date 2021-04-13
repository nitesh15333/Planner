package com.planner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.StorageReference;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class DashboardActivity extends AppCompatActivity {
    //Fibase auth
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    DatabaseReference userDbRef;
    FirebaseUser currentuser;
    public static int constant =0;




    BottomNavigationView mBottomNavigationView;



    Toolbar actionBar;
    TextView mFragment_name;
    ImageView profilepicIm;
    String mUid;
    public String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        actionBar=findViewById(R.id.toolbar_main);
        mFragment_name=findViewById(R.id.Fragment_name);


        setSupportActionBar(actionBar);

        firebaseAuth=FirebaseAuth.getInstance();



        //bottom nav navigator
        BottomNavigationView navigationView=findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //Default home frag

        mFragment_name.setText("Home");
        HomeFragment fragment1=new HomeFragment();
        FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1,"");
        ft1.commit();

        CheckUserStatus();
        //update Token




        mBottomNavigationView=findViewById(R.id.navigation);
        currentuser=FirebaseAuth.getInstance().getCurrentUser();
        mUid=currentuser.getUid();
        firebaseAuth=FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        userDbRef = firebaseDatabase.getReference("Users");

        Query userQuery = userDbRef.orderByChild("uid").equalTo(mUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    type = "" + ds.child("type").getValue().toString().trim();
                    if (type.equals("admin")){
                        mBottomNavigationView.getMenu()
                                .findItem(R.id.nav_task)
                                .setVisible(false);
                        mBottomNavigationView.getMenu()
                                .findItem(R.id.nav_assign_task)
                                .setVisible(true);
                        mBottomNavigationView.invalidate();


                    }
                    else if (type.equals("client")){
                        mBottomNavigationView.getMenu()
                                .findItem(R.id.nav_task)
                                .setVisible(true);
                        mBottomNavigationView.getMenu()
                                .findItem(R.id.nav_assign_task)
                                .setVisible(false);
                        mBottomNavigationView.getMenu()
                                .findItem(R.id.nav_home)
                                .setVisible(false);
                        mBottomNavigationView.invalidate();


                    }

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume() {
        CheckUserStatus();
        super.onResume();
    }




    public BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()){
                    case R.id.nav_home:
                        //fragment transcation
                        mFragment_name.setText("Home");
                        HomeFragment fragment1=new HomeFragment();
                        FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                        ft1.replace(R.id.content,fragment1,"");
                        ft1.commit();
                        return true;

                    case R.id.nav_assign_task:
                        //fragment transcation
                        mFragment_name.setText("Assign Task");
                        overridePendingTransition(0,0);
                        startActivity(new Intent(DashboardActivity.this,AssignTaskActivity.class));
                        return true;

                    case R.id.nav_task:
                        //fragment transcation
                        mFragment_name.setText("Tasks");
                        startActivity(new Intent(DashboardActivity.this,TaskActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                        
                    case R.id.nav_complaint:
                        //fragment transcation

                        if (type.equals("admin")){
                            mFragment_name.setText("H.0.D Complaints");
                            Admin_Complaint_Fragment fragment2=new Admin_Complaint_Fragment();
                            FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content,fragment2,"");
                            ft2.commit();
                        }
                        else if (type.equals("client")){
                            mFragment_name.setText("Employees Complaint");
                            Client_Complaint_Fragment fragment3=new Client_Complaint_Fragment();
                            FragmentTransaction ft3=getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content,fragment3,"");
                            ft3.commit();
                        }
                        return true;

                    case R.id.nav_profile:
                        //fragment transcation
                        mFragment_name.setText("Users");
                        ProfileFragment fragment4=new ProfileFragment();
                        FragmentTransaction ft4=getSupportFragmentManager().beginTransaction();
                        ft4.replace(R.id.content,fragment4,"");
                        ft4.commit();
                        return true;

                }
            //handle item clicks


            return false;

        }
    };


    private void CheckUserStatus(){
        //get current user

        user=firebaseAuth.getCurrentUser();
        if(user != null){

            //signed in user stay here

            mUid=user.getUid();
            firebaseDatabase=FirebaseDatabase.getInstance();
            databaseReference=firebaseDatabase.getReference("Users");
            storageReference = getInstance().getReference();

            Query query= databaseReference.orderByChild("email").equalTo(user.getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds :dataSnapshot.getChildren()) {

                        String image = "" + ds.child("image").getValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



            SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("Current_USERID",mUid);
            editor.apply();


        }
        else {
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        CheckUserStatus();
        super.onStart();

    }


}
