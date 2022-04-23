package com.example.contacthandbook.fragment.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.contacthandbook.R;
import com.example.contacthandbook.model.Notification;

import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> mData;
    private LayoutInflater mInflater;
    private OnItemListener onItemListener;

    // data is passed into the constructor
    public NotificationAdapter(Context context, List<Notification> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.notification_row, parent, false);
        view.setOnClickListener(new RV_ItemListener());
        view.setOnLongClickListener(new RV_ItemListener());
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notification notification = mData.get(position);
        holder.destinationTextView.setText("To: " + notification.getDesitnation().toString());
        holder.titleTextView.setText("Title: " + notification.getTitle());
        holder.messageTextView.setText(notification.getContent());
        holder.dateTextView.setText("Date: " + notification.getDateStr());
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

        ViewHolder(View itemView) {
            super(itemView);
            destinationTextView = itemView.findViewById(R.id.destination);
            titleTextView = itemView.findViewById(R.id.title);
            messageTextView = itemView.findViewById(R.id.message);
            dateTextView = itemView.findViewById(R.id.date);
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