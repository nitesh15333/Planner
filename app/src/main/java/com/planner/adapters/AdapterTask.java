package com.planner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.planner.R;
import com.planner.TaskDetailsActivity;
import com.planner.models.modelTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterTask extends RecyclerView.Adapter<AdapterTask.Myholder> {

    Context context;
    List<modelTask> TaskList;//get user info



    public AdapterTask(Context context, List<modelTask> taskList) {
        this.context = context;
        this.TaskList = taskList;
    }
    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate row chatlist
        View view= LayoutInflater.from(context).inflate(R.layout.row_task,parent,false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        //get dat
        final String tid=TaskList.get(position).getTask_id();
        final String tname=TaskList.get(position).getTask_name();
        final String tdescription=TaskList.get(position).getTask_description();
        final String tdeadlinedate=TaskList.get(position).getSubmission_date();
        final String tstatus=TaskList.get(position).getTask_status();

        holder.tnameTv.setText(tname);
        holder.tdescriptionTv.setText(tdescription);
        holder.tdeadlineTv.setText(tdeadlinedate);
        holder.tstatusTv.setText(tstatus);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, TaskDetailsActivity.class);
                intent.putExtra("tname",tname);
                intent.putExtra("tdescription",tdescription);
                intent.putExtra("tdeadline",tdeadlinedate);
                intent.putExtra("tstatus",tstatus);
                intent.putExtra("tid",tid);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return TaskList.size();
    }

    class Myholder extends RecyclerView.ViewHolder{
        //views of row chatlis
        TextView tnameTv,tdescriptionTv,tdeadlineTv,tstatusTv;


        public Myholder(@NonNull View itemView) {
            super(itemView);
            tnameTv=itemView.findViewById(R.id.nameTv);
            tdescriptionTv=itemView.findViewById(R.id.descriptionTv);
            tdeadlineTv=itemView.findViewById(R.id.deadlineTv);
            tstatusTv=itemView.findViewById(R.id.tstatus);

        }
    }
}
