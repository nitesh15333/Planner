package com.planner.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.planner.R;
import com.planner.models.modelComplaints;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterComplaints extends RecyclerView.Adapter<AdapterComplaints.MyHolder>  {



    Context context;
    List<modelComplaints> complaintsList;

    public AdapterComplaints(Context context, List<modelComplaints> complaintsList) {
        this.context = context;
        this.complaintsList = complaintsList;
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
        return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String message=complaintsList.get(position).getMessage();
        String timestamp=complaintsList.get(position).getTimestamp();

        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestamp));

        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();


        holder.messageTv.setVisibility(View.VISIBLE);
        holder.messageTv.setText(message);
        holder.timeTv.setText(datetime);


    }

    @Override
    public int getItemCount() {
        return complaintsList.size();
    }




    class MyHolder extends RecyclerView.ViewHolder{


        TextView messageTv,timeTv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //init views
            messageTv=itemView.findViewById(R.id.messageTV);
            timeTv=itemView.findViewById(R.id.Time_TV);
        }
    }

}
