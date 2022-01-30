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
import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.Fragments.ChatFragment;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.Utils;
import com.nishasimran.betweenus.Values.CommonValues;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

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
        return new ChatViewHolder(view);
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
                holder.comeTextContainer.setVisibility(View.GONE);
                holder.sendTextContainer.setVisibility(View.VISIBLE);

                if (message.getMessageType().equals(CommonValues.MESSAGE_TYPE_TEXT)) {
                    holder.sendMessageTxt.setText(messageText);
                    holder.sendTimeTxt.setText(time);
                    holder.sendStatusTxt.setText(Utils.getMessageStatus(message.getStatus()));
                } else if (CommonValues.MESSAGE_TYPE_IMAGE.equals(message.getMessageType())) {
                    // TODO: show image
                    holder.sendMessageTxt.setText("image");
                    holder.sendTimeTxt.setText(time);
                    holder.sendStatusTxt.setText(Utils.getMessageStatus(message.getStatus()));
                }

                if (message.getStatus().equals(CommonValues.STATUS_SENDING)) {
                    sendMessages(message);
                }

            } else if (message.getTo().trim().equals(uid.trim())) {
                holder.comeTextContainer.setVisibility(View.VISIBLE);
                holder.sendTextContainer.setVisibility(View.GONE);

                if (message.getMessageType().equals(CommonValues.MESSAGE_TYPE_TEXT)) {
                    holder.comeMessageTxt.setText(messageText);
                    holder.comeTimeTxt.setText(time);
                } else if (CommonValues.MESSAGE_TYPE_IMAGE.equals(message.getMessageType())) {
                    // TODO: show image
                    holder.comeMessageTxt.setText("image");
                    holder.comeTimeTxt.setText(time);
                }

                if (message.getUnread() != null) {
                    if (message.getUnread())
                        if (!(holder.dateTxt.getVisibility() == View.VISIBLE)) {
                            holder.dateTxt.setVisibility(View.VISIBLE);
                            holder.dateTxt.setText(R.string.unread);
                        }
                        message.setUnread(false);
                        FirebaseDb.getInstance().updateMessageStatus(message.getId(), CommonValues.STATUS_SEEN, System.currentTimeMillis());
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        int size = messages == null ? 0 : messages.size();
        fragment.noMessages(size < 1);
        return size;
    }

    private void sendMessages(Message message) {
        if (Utils.isNetworkAvailable(fragment.activity)) {

            if (message.getMessageType().equals(CommonValues.MESSAGE_TYPE_TEXT)) {
                Map<String, String> map = fragment.encryptTextMessage(message.getMessage(), message.getCurrMillis());
                if (map != null)
                    fragment.createAndSendMessage(map, message);
            }
        }
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

        // TODO: Add an imageview in viewHolder to show images
        private final ConstraintLayout sendTextContainer;
        private final ConstraintLayout comeTextContainer;
        private final TextView dateTxt, comeTimeTxt, sendTimeTxt, comeMessageTxt, sendMessageTxt, sendStatusTxt;

        public ChatViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ConstraintLayout root = itemView.findViewById(R.id.message_root);
            sendTextContainer = root.findViewById(R.id.message_send_msg);
            comeTextContainer = root.findViewById(R.id.message_come_msg);

            dateTxt = root.findViewById(R.id.message_date);

            comeTimeTxt = comeTextContainer.findViewById(R.id.message_come_time);
            comeMessageTxt = comeTextContainer.findViewById(R.id.message_come_text);

            sendTimeTxt = sendTextContainer.findViewById(R.id.message_send_time);
            sendMessageTxt = sendTextContainer.findViewById(R.id.message_send_text);
            sendStatusTxt = sendTextContainer.findViewById(R.id.message_send_status);

            sendTextContainer.setVisibility(View.GONE);
            comeTextContainer.setVisibility(View.GONE);
            dateTxt.setVisibility(View.GONE);
        }
    }
}
