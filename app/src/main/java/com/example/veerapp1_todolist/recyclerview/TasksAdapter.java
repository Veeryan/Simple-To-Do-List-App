package com.example.veerapp1_todolist.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.veerapp1_todolist.R;
import com.example.veerapp1_todolist.data.Task;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    LayoutInflater inflater;
    List<Task> tasks; //don't pass as live data in the recycler view
    private OnItemClickListener listener;

    public TasksAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new TaskViewHolder(viewItem); //remember to return the viewholder with the layout inflated
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) { //set up the holder
        if(tasks != null){
            holder.title.setText(tasks.get(position).getName());
            holder.desc.setText(tasks.get(position).getDescription());
        }
        else{
            holder.title.setText(R.string.null_tasksList_rv);
        }
    }

    @Override
    public int getItemCount() {
        return (tasks == null ? 0 : tasks.size());
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder{

        private final TextView title, desc;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.rv_tvtitle);
            desc = itemView.findViewById(R.id.rv_tvdesc);

            //set on click listener for every itemView + send information to main activity:
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                        Task task = tasks.get(getAdapterPosition());
                        listener.onItemClick(task);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
