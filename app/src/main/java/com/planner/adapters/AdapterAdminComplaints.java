package com.planner.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.planner.R;
import com.planner.TaskActivity;
import com.planner.models.modelAdminComplaints;
import com.planner.models.modelTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterAdminComplaints extends RecyclerView.Adapter<AdapterAdminComplaints.Myholder>  {

    Context context;
    List<modelAdminComplaints> modelAdminComplaints;//get user info
    FirebaseDatabase firebaseDatabase;
    String name;
    DatabaseReference userDbRef;

    public AdapterAdminComplaints(Context context, List<com.planner.models.modelAdminComplaints> modelAdminComplaints) {
        this.context = context;
        this.modelAdminComplaints = modelAdminComplaints;
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_complaints_admin,parent,false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        final String name=modelAdminComplaints.get(position).getName();
        final String message=modelAdminComplaints.get(position).getMessage();
        final String time=modelAdminComplaints.get(position).getTimestamp();
        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(time));

        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();
        holder.sendernameTv.setText(name);
        holder.messageTv.setText(message);
        holder.TimeTv.setText(datetime);

    }

    @Override
    public int getItemCount() {
        return modelAdminComplaints.size();
    }


    class Myholder extends RecyclerView.ViewHolder{
        //views of row chatlis
        TextView sendernameTv,messageTv,TimeTv;
        public Myholder(@NonNull View itemView) {
            super(itemView);
            sendernameTv=itemView.findViewById(R.id.sendernameTv);
            messageTv=itemView.findViewById(R.id.messageTv);
            TimeTv=itemView.findViewById(R.id.timeTv);

        }
    }


}
