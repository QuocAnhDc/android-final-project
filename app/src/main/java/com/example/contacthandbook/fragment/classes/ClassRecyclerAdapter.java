package com.example.contacthandbook.fragment.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.contacthandbook.R;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.model.Classes;
import com.example.contacthandbook.model.Teacher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClassRecyclerAdapter extends RecyclerView.Adapter<ClassRecyclerAdapter.ViewHolder> {

    private List<Classes> mData;
    private LayoutInflater mInflater;
    private OnItemListener onItemListener;

    // create arrayList
    private ArrayList<Classes> arraymData;
    private Context context;

    // data is passed into the constructor
    public ClassRecyclerAdapter(Context context, List<Classes> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;

        this.arraymData = new ArrayList<Classes>();
        this.arraymData.addAll(mData);
    }

    // class filter
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mData.clear();
        if (charText.length() == 0) {
            mData.addAll(arraymData);
        } else {
        }
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public ClassRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.class_row, parent, false);
        view.setOnClickListener(new ClassRecyclerAdapter.RV_ItemListener());
        view.setOnLongClickListener(new ClassRecyclerAdapter.RV_ItemListener());
        return new ClassRecyclerAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ClassRecyclerAdapter.ViewHolder holder, int position) {
        //Common object = mData.get(position);
        Classes classes = mData.get(position);
        holder.mainTextView.setText(classes.getClassName());
        FirebaseManager firebaseManager = new FirebaseManager(context);
        firebaseManager.getTeacher(classes.getTeacher().getId(), new FirebaseCallBack.SingleTeacher() {
            @Override
            public void onCallback(Teacher teacher) {
                if (teacher.getName() == null && teacher.getName().equals("")) {
                    holder.teacherName.setText("No Teacher");
                }
                else {
                    holder.teacherName.setText("Teacher: " + teacher.getName());
                }
            }
        });

        holder.itemView.setId(position);
    }
    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView mainTextView;
        TextView teacherName;

        ViewHolder(View itemView) {
            super(itemView);
            mainTextView = itemView.findViewById(R.id.className);
            teacherName = itemView.findViewById(R.id.teacherName);
        }

    }

    class RV_ItemListener implements View.OnClickListener, View.OnLongClickListener{
        @Override
        public void onClick(View view) {
            if (onItemListener != null){
                onItemListener.OnItemClickListener(view, view.getId());
            }
        }
        @Override
        public boolean onLongClick(View view) {
            if (onItemListener != null){
                onItemListener.OnItemLongClickListener(view,view.getId());
            }
            return true;
        }
    }

    public  interface OnItemListener{
        void OnItemClickListener(View view, int position);
        void OnItemLongClickListener(View view, int position);
    }

    public void setOnItemListenerListener(OnItemListener listener){
        this.onItemListener = listener;
    }

}
