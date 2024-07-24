package com.motorolasolutions.msismartreply;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.ViewHolder> {
    private ArrayList<MessageModel> messageList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        TextView message;
        ConstraintLayout constraintLayout;
        ConstraintSet constraintSet;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.messageText);
            constraintLayout = itemView.findViewById(R.id.messageLayout);
            constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public MessageRecyclerAdapter(ArrayList<MessageModel> messages) {
        this.messageList = messages;
    }

    public void addMessage(MessageModel message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_container, viewGroup, false);
        return new MessageRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageRecyclerAdapter.ViewHolder holder, int position) {


        //Change background and indentation depending on who sent the message
        boolean fromMe = messageList.get(position).isFromMe();
        holder.constraintSet.clone(holder.constraintLayout);
        if (fromMe) {
            holder.getTextView().setBackgroundResource(R.drawable.message_background);
            holder.constraintSet.clear(R.id.messageText, ConstraintSet.START);
            holder.constraintSet.clear(R.id.messageText, ConstraintSet.END);
            holder.constraintSet.connect(R.id.messageText, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            holder.getTextView().setBackgroundResource(R.drawable.message_received_background);
            holder.constraintSet.clear(R.id.messageText, ConstraintSet.START);
            holder.constraintSet.clear(R.id.messageText, ConstraintSet.END);
            holder.constraintSet.connect(R.id.messageText, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        }
        //Set message bubble text
        holder.constraintSet.applyTo(holder.constraintLayout);
        holder.getTextView().setText(messageList.get(position).getMessage());
    }

    public int getItemCount() {
        return messageList.size();
    }

}
