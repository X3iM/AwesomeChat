package com.android.greena.awesomechat;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, int resource, List<Message> messageList) {
        super(context, resource, messageList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.message_item, parent, false);

        ImageButton photoImageButton = convertView.findViewById(R.id.photoImageView);
        TextView textTextView = convertView.findViewById(R.id.textTextView);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);

        Message message = getItem(position);
        if (message.getImageUrl() ==  null) {
            textTextView.setVisibility(View.VISIBLE);
            textTextView.setText(message.getText());
            photoImageButton.setVisibility(View.GONE);
        } else {
            textTextView.setVisibility(View.GONE);
            photoImageButton.setVisibility(View.VISIBLE);
            Glide.with(photoImageButton.getContext())
                    .load(message.getImageUrl())
                    .into(photoImageButton);
        }

        nameTextView.setText(message.getName());

        return convertView;
    }
}