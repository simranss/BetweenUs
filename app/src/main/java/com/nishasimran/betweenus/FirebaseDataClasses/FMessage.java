package com.nishasimran.betweenus.FirebaseDataClasses;

import androidx.annotation.NonNull;

import com.nishasimran.betweenus.Values.FirebaseValues;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FMessage {

    Map<String, String> map;

    public FMessage() {
        // required no-argument constructor
    }

    public FMessage(@NotNull @NonNull String id, String encryptedMessage, String from, String to, String messageType, String status, Long currMillis, Long sentCurrMillis, Long deliveredCurrMillis, Long readCurrMillis, String serverPublic, String myPublic, String iv) {
        map = new HashMap<>();
        map.put(FirebaseValues.ID, id);
        map.put(FirebaseValues.ENCRYPTED_MESSAGE, encryptedMessage);
        map.put(FirebaseValues.MESSAGE_FROM, from);
        map.put(FirebaseValues.MESSAGE_TO, to);
        map.put(FirebaseValues.MESSAGE_TYPE, messageType);
        map.put(FirebaseValues.MESSAGE_STATUS, status);
        map.put(FirebaseValues.CURR_MILLIS, String.valueOf(currMillis));
        map.put(FirebaseValues.SENT_CURR_MILLIS, String.valueOf(sentCurrMillis));
        map.put(FirebaseValues.DELIVERED_CURR_MILLIS, String.valueOf(deliveredCurrMillis));
        map.put(FirebaseValues.READ_CURR_MILLIS, String.valueOf(readCurrMillis));
        map.put(FirebaseValues.SERVER_PUBLIC, serverPublic);
        map.put(FirebaseValues.MY_PUBLIC, myPublic);
        map.put(FirebaseValues.IV, iv);
    }

    public FMessage(@NotNull @NonNull Map<String, String> map) {
        this.map = map;
    }


    public void setId(String id) {
        if (map != null) {
            map.put(FirebaseValues.ID, id);
        }
    }

    public void setMessage(String message) {
        if (map != null) {
            map.put(FirebaseValues.ENCRYPTED_MESSAGE, message);
        }
    }

    public void setFrom(String from) {
        if (map != null) {
            map.put(FirebaseValues.MESSAGE_FROM, from);
        }
    }

    public void setTo(String to) {
        if (map != null) {
            map.put(FirebaseValues.MESSAGE_TO, to);
        }
    }

    public void setMessageType(String messageType) {
        if (map != null) {
            map.put(FirebaseValues.MESSAGE_TYPE, messageType);
        }
    }

    public void setStatus(String status) {
        if (map != null) {
            map.put(FirebaseValues.MESSAGE_STATUS, status);
        }
    }

    public void setCurrMillis(String currMillis) {
        if (map != null) {
            map.put(FirebaseValues.CURR_MILLIS, currMillis);
        }
    }

    public void setDeliveredCurrMillis(String deliveredCurrMillis) {
        if (map != null) {
            map.put(FirebaseValues.DELIVERED_CURR_MILLIS, deliveredCurrMillis);
        }
    }

    public void setSentCurrMillis(String sentCurrMillis) {
        if (map != null) {
            map.put(FirebaseValues.SENT_CURR_MILLIS, sentCurrMillis);
        }
    }

    public void setReadCurrMillis(String readCurrMillis) {
        if (map != null) {
            map.put(FirebaseValues.READ_CURR_MILLIS, readCurrMillis);
        }
    }

    public void setServerPublic(String serverPublic) {
        if (map != null) {
            map.put(FirebaseValues.SERVER_PUBLIC, serverPublic);
        }
    }

    public void setMyPublic(String myPublic) {
        if (map != null) {
            map.put(FirebaseValues.MY_PUBLIC, myPublic);
        }
    }

    public void setIv(String iv) {
        if (map != null) {
            map.put(FirebaseValues.IV, iv);
        }
    }


    public String getId() {
        if (map != null) {
            return map.get(FirebaseValues.ID);
        }
        return null;
    }

    public String getMessage() {
        if (map != null) {
            return map.get(FirebaseValues.ENCRYPTED_MESSAGE);
        }
        return null;
    }

    public String getFrom() {
        if (map != null) {
            return map.get(FirebaseValues.MESSAGE_FROM);
        }
        return null;
    }

    public String getTo() {
        if (map != null) {
            return map.get(FirebaseValues.MESSAGE_TO);
        }
        return null;
    }

    public String getMessageType() {
        if (map != null) {
            return map.get(FirebaseValues.MESSAGE_TYPE);
        }
        return null;
    }

    public String getStatus() {
        if (map != null) {
            return map.get(FirebaseValues.MESSAGE_STATUS);
        }
        return null;
    }

    public Long getCurrMillis() {
        if (map != null) {
            String currMillis = map.get(FirebaseValues.CURR_MILLIS);
            if (currMillis != null)
                return Long.parseLong(currMillis);
            else return null;
        }
        return null;
    }

    public Long getDeliveredCurrMillis() {
        if (map != null) {
            String currMillis = map.get(FirebaseValues.DELIVERED_CURR_MILLIS);
            if (currMillis != null)
                return Long.parseLong(currMillis);
            else return null;
        }
        return null;
    }

    public Long getSentCurrMillis() {
        if (map != null) {
            String currMillis = map.get(FirebaseValues.SENT_CURR_MILLIS);
            if (currMillis != null)
                return Long.parseLong(currMillis);
            else return null;
        }
        return null;
    }

    public Long getReadCurrMillis() {
        if (map != null) {
            String currMillis = map.get(FirebaseValues.READ_CURR_MILLIS);
            if (currMillis != null)
                return Long.parseLong(currMillis);
            else return null;
        }
        return null;
    }

    public String getServerPublic() {
        if (map != null) {
            return map.get(FirebaseValues.SERVER_PUBLIC);
        }
        return null;
    }

    public String getMyPublic() {
        if (map != null) {
            return map.get(FirebaseValues.MY_PUBLIC);
        }
        return null;
    }

    public String getIv() {
        if (map != null) {
            return map.get(FirebaseValues.IV);
        }
        return null;
    }

    public Map<String, String> getMap() {
        return map;
    }

    @Override
    public @NotNull String toString() {
        return "FMessage{" + getMap() + "}";
    }
}
