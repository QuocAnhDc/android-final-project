package com.example.contacthandbook.fragment.students;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.contacthandbook.R;
import com.example.contacthandbook.model.Common;
import com.example.contacthandbook.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentRecyclerAdapter extends RecyclerView.Adapter<StudentRecyclerAdapter.ViewHolder> {

    private List<Student> mData;
    private LayoutInflater mInflater;
    private OnItemListener onItemListener;

    // create arraylist
    private ArrayList<Student> arraymData;

    // data is passed into the constructor
    public StudentRecyclerAdapter(Context context, List<Student> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

        this.arraymData = new ArrayList<Student>();
        this.arraymData.addAll(mData);
    }

    // class filter
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mData.clear();
        if (charText.length() == 0) {
            mData.addAll(arraymData);
        } else {
            for (Student student : arraymData) {
                if (student.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mData.add(student);
                }
            }
        }
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.common_row, parent, false);
        view.setOnClickListener(new RV_ItemListener());
        view.setOnLongClickListener(new RV_ItemListener());
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Common object = mData.get(position);
        holder.myTextView.setText(object.getName());
        holder.itemView.setId(position);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.title);
        }

    }

    public  interface OnItemListener{
        void OnItemClickListener(View view, int position);
        void OnItemLongClickListener(View view, int position);
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

    public void setOnItemListenerListener(OnItemListener listener){
        this.onItemListener = listener;
    }

}
