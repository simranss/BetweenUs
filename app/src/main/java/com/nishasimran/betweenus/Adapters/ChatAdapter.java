package com.nishasimran.betweenus.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nishasimran.betweenus.DataClasses.Message;
import com.nishasimran.betweenus.Fragments.ChatFragment;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> messages;
    private final String uid;
    private final Context context;
    private final ChatFragment fragment;

    public ChatAdapter(ChatFragment fragment, String uid) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.uid = uid;
    }

    @NonNull
    @NotNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_list_item, parent, false);
        int width = (int) (parent.getWidth() * 0.75);
        return new ChatViewHolder(view, width);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatViewHolder holder, int position) {
        if (messages != null) {
            Message message = messages.get(position);
            String date = Utils.getFormattedDate(message.getCurrMillis());
            String time = Utils.getFormattedTime(message.getCurrMillis());
            String messageText = message.getMessage();
            if (position == 0) {
                holder.dateTxt.setVisibility(View.VISIBLE);
                holder.dateTxt.setText(date);
            } else {
                String prevDate = Utils.getFormattedDate(messages.get(position - 1).getCurrMillis());
                if (!date.equals(prevDate)) {
                    holder.dateTxt.setVisibility(View.VISIBLE);
                    holder.dateTxt.setText(date);
                } else {
                    holder.dateTxt.setVisibility(View.GONE);
                }
            }
            if (message.getFrom().trim().equals(uid.trim())) {
                holder.comeContainer.setVisibility(View.GONE);
                holder.sendContainer.setVisibility(View.VISIBLE);

                holder.sendMessageTxt.setText(messageText);
                holder.sendTimeTxt.setText(time);
                holder.sendStatusTxt.setText(Utils.getMessageStatus(message.getStatus()));

            } else if (message.getTo().trim().equals(uid.trim())) {
                holder.comeContainer.setVisibility(View.VISIBLE);
                holder.sendContainer.setVisibility(View.GONE);

                holder.comeMessageTxt.setText(messageText);
                holder.comeTimeTxt.setText(time);
            }
        }
    }

    @Override
    public int getItemCount() {
        int size = messages == null ? 0 : messages.size();
        fragment.noMessages(size < 1);
        return size;
    }

    public void setMessages(final List<Message> messages1) {
        if (messages == null || messages.isEmpty()) {
            messages = messages1;
            notifyItemRangeInserted(0, messages1.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return messages.size();
                }
                @Override
                public int getNewListSize() {
                    return messages1.size();
                }
                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return messages.get(oldItemPosition).getId().equals(messages1.get(newItemPosition).getId());
                }
                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Message newMessage = messages1.get(newItemPosition);
                    Message oldMessage = messages.get(oldItemPosition);
                    return newMessage.equals(oldMessage);
                }
            });
            messages = messages1;
            result.dispatchUpdatesTo(this);
        }
    }

    public Integer getIndexOf(Message message) {
        if (messages != null && !messages.isEmpty()) {
            return messages.indexOf(message);
        }
        return null;
    }

    public void updateMessage(int index, Message message) {
        messages.set(index, message);
        notifyItemChanged(index);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout sendContainer;
        private final ConstraintLayout comeContainer;
        private final TextView dateTxt, comeTimeTxt, sendTimeTxt, comeMessageTxt, sendMessageTxt, sendStatusTxt;

        public ChatViewHolder(@NonNull @NotNull View itemView, int width) {
            super(itemView);

            ConstraintLayout root = itemView.findViewById(R.id.message_root);
            sendContainer = root.findViewById(R.id.message_send_msg);
            comeContainer = root.findViewById(R.id.message_come_msg);

            dateTxt = root.findViewById(R.id.message_date);

            sendContainer.setMaxWidth(width);
            comeContainer.setMaxWidth(width);

            comeTimeTxt = comeContainer.findViewById(R.id.message_come_time);
            comeMessageTxt = comeContainer.findViewById(R.id.message_come_text);

            sendTimeTxt = sendContainer.findViewById(R.id.message_send_time);
            sendMessageTxt = sendContainer.findViewById(R.id.message_send_text);
            sendStatusTxt = sendContainer.findViewById(R.id.message_send_status);

            sendContainer.setVisibility(View.GONE);
            comeContainer.setVisibility(View.GONE);
            dateTxt.setVisibility(View.GONE);
        }
    }
}
