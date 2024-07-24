package com.motorolasolutions.msismartreply;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class MessageRecyclerAdapter extends RecyclerView.Adapter {
    private String[] MessageList;

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return createViewHolder(viewGroup,  viewType);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

    }

    public int getItemCount() {
        return MessageList.length;
    }

}
