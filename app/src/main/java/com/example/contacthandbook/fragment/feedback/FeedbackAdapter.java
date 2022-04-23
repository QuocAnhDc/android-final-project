package com.example.contacthandbook.fragment.feedback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;


import com.example.contacthandbook.R;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.model.Feedback;
import com.example.contacthandbook.model.User;


import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder>  {

    private List<Feedback> mData;
    private LayoutInflater mInflater;
    private OnItemListener onItemListener;
    FirebaseManager firebaseManager ;
    // data is passed into the constructor
    public FeedbackAdapter(Context context, List<Feedback> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        firebaseManager = new FirebaseManager(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.feedback_row, parent, false);
        view.setOnClickListener(new RV_ItemListener());
        view.setOnLongClickListener(new RV_ItemListener());
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Feedback feedback = mData.get(position);

        holder.titleTextView.setText("Title: " + feedback.getTitle());
        holder.messageTextView.setText(feedback.getContent());
        holder.dateTextView.setText("Date: " + feedback.getDateStr());
        firebaseManager.getUser(feedback.getSender(), new FirebaseCallBack.UserCallBack() {
            @Override
            public void onCallback(User user) {
                holder.sender.setText("From: " + user.getName());
            }
        });
        firebaseManager.getUser(feedback.getReciver(), new FirebaseCallBack.UserCallBack() {
            @Override
            public void onCallback(User user) {
                holder.destinationTextView.setText("To: " + user.getName());
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
        TextView destinationTextView;
        TextView titleTextView;
        TextView messageTextView;
        TextView dateTextView;
        TextView sender;
        ViewHolder(View itemView) {
            super(itemView);
            destinationTextView = itemView.findViewById(R.id.destination);
            titleTextView = itemView.findViewById(R.id.title);
            messageTextView = itemView.findViewById(R.id.message);
            dateTextView = itemView.findViewById(R.id.date);
            sender = itemView.findViewById(R.id.sender);
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

