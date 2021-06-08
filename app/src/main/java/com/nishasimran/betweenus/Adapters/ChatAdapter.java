package com.nishasimran.betweenus.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.nishasimran.betweenus.DataClasses.Message;
import com.nishasimran.betweenus.Fragments.ChatFragment;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final ArrayList<Message> messages;
    private final String uid;
    private final Context context;
    private final ChatFragment fragment;

    public ChatAdapter(ChatFragment fragment, ArrayList<Message> messages, String uid) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.messages = messages;
        this.uid = uid;
    }

    @NonNull
    @NotNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_list_item, parent, false);
        int width = (int) (parent.getWidth() * 0.75);
        return new ChatViewHolder(context, view, this, width);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);
        String date = Utils.getFormattedDate(message.getCurrMillis());
        String time = Utils.getFormattedTime(message.getCurrMillis());
        String messageText = message.getMessage();
        if (message.getFrom().trim().equals(uid.trim())) {
            holder.comeContainer.setVisibility(View.GONE);
            holder.sendContainer.setVisibility(View.VISIBLE);

            holder.sendMessageTxt.setText(messageText);
            holder.sendTimeTxt.setText(time);
            holder.sendDateTxt.setText(date);

        } else if (message.getTo().trim().equals(uid.trim())){
            holder.comeContainer.setVisibility(View.VISIBLE);
            holder.sendContainer.setVisibility(View.GONE);

            holder.comeMessageTxt.setText(messageText);
            holder.comeTimeTxt.setText(time);
            holder.comeDateTxt.setText(date);
        }
    }

    @Override
    public int getItemCount() {
        int size = messages.size();
        fragment.noMessages(size < 1);
        return size;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout sendContainer;
        private final ConstraintLayout comeContainer;
        private final TextView comeTimeTxt, sendTimeTxt, comeMessageTxt, sendMessageTxt, comeDateTxt, sendDateTxt;

        public ChatViewHolder(final Context context, @NonNull @NotNull View itemView, final ChatAdapter adapter, int width) {
            super(itemView);

            ConstraintLayout root = itemView.findViewById(R.id.message_root);
            sendContainer = root.findViewById(R.id.message_send_msg);
            comeContainer = root.findViewById(R.id.message_come_msg);

            sendContainer.setMaxWidth(width);
            comeContainer.setMaxWidth(width);

            comeTimeTxt = comeContainer.findViewById(R.id.message_come_time);
            comeMessageTxt = comeContainer.findViewById(R.id.message_come_text);
            comeDateTxt = comeContainer.findViewById(R.id.message_come_date);

            sendTimeTxt = sendContainer.findViewById(R.id.message_send_time);
            sendMessageTxt = sendContainer.findViewById(R.id.message_send_text);
            sendDateTxt = sendContainer.findViewById(R.id.message_send_date);

            sendContainer.setVisibility(View.GONE);
            comeContainer.setVisibility(View.GONE);
        }
    }
}
