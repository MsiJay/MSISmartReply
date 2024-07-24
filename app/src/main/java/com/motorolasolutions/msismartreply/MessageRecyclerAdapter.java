package com.motorolasolutions.msismartreply;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.ViewHolder> {
    private ArrayList<String> messageList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.messageText);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public MessageRecyclerAdapter(ArrayList<String> messages) {
        this.messageList = messages;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_container, viewGroup, false);
        return new MessageRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageRecyclerAdapter.ViewHolder holder, int position) {
        holder.getTextView().setText(messageList.get(position));
    }

    public int getItemCount() {
        return messageList.size();
    }

}
