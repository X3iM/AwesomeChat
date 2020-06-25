package com.android.greena.awesomechat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private List<Message> messages;
    private Activity      activity;

    public MessageAdapter(Activity context, int resource, List<Message> messageList) {
        super(context, resource, messageList);

        this.messages = messageList;
        this.activity = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        Message messag = getItem(position);
        int layoutResource = 0;
        int viewType = getItemViewType(position);

        if (viewType == 0)
            layoutResource = R.layout.my_message_item;
        else
            layoutResource = R.layout.your_message_item;

        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        if (messag.getImageUrl() == null) {
            viewHolder.messageTextView.setText(messag.getText());
            viewHolder.messageTextView.setVisibility(View.VISIBLE);
            viewHolder.photoImageView.setVisibility(View.GONE);
        } else {
            viewHolder.messageTextView.setVisibility(View.GONE);
            viewHolder.photoImageView.setVisibility(View.VISIBLE);
            Glide.with(viewHolder.photoImageView.getContext())
                    .load(messag.getImageUrl())
                    .into(viewHolder.photoImageView);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        int flag = 1;
        Message message = messages.get(position);
        if (message.isMine()) flag = 0;
        return flag;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private static class ViewHolder {
        private TextView    messageTextView;
        private ImageView   photoImageView;

        public ViewHolder(View view) {
            this.messageTextView = view.findViewById(R.id.messageTextView);
            this.photoImageView = view.findViewById(R.id.photoImageView);
        }
    }
}