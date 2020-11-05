package com.onesignal.flutter;

import android.util.Log;

import com.onesignal.OSDeviceState;
import com.onesignal.OSEmailSubscriptionState;
import com.onesignal.OSEmailSubscriptionStateChanges;
import com.onesignal.OSInAppMessageAction;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OSPermissionState;
import com.onesignal.OSPermissionStateChanges;
import com.onesignal.OSSubscriptionState;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OutcomeEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class OneSignalSerializer {
    private static HashMap<String, Object> convertSubscriptionStateToMap(OSSubscriptionState state) {
        HashMap<String, Object> hash = new HashMap<>();

        hash.put("isSubscribed", state.isSubscribed());
        hash.put("isPushDisabled", state.isPushDisabled());
        hash.put("pushToken", state.getPushToken());
        hash.put("userId", state.getUserId());

        return hash;
    }

    private static HashMap<String, Object> convertPermissionStateToMap(OSPermissionState state) {
        HashMap<String, Object> permission = new HashMap<>();

        permission.put("areNotificationsEnabled", state.areNotificationsEnabled());

        return permission;
    }

    private static HashMap<String, Object> convertEmailSubscriptionStateToMap(OSEmailSubscriptionState state) {
        HashMap<String, Object> hash = new HashMap<>();

        hash.put("emailUserId", state.getEmailUserId());
        hash.put("emailAddress", state.getEmailAddress());
        hash.put("subscribed", state.getSubscribed());

        return hash;
    }

    static HashMap<String, Object> convertDeviceStateToMap(OSDeviceState state) {
        HashMap<String, Object> hash = new HashMap<>();

        hash.put("areNotificationsEnabled", state.areNotificationsEnabled());
        hash.put("pushDisabled", state.isPushDisabled());
        hash.put("subscribed", state.isSubscribed());
        hash.put("emailSubscribed", state.isEmailSubscribed());
        hash.put("userId", state.getUserId());
        hash.put("pushToken", state.getPushToken());
        hash.put("emailUserId", state.getEmailUserId());
        hash.put("emailAddress", state.getEmailAddress());

        return hash;
    }

    static HashMap<String, Object> convertSubscriptionStateChangesToMap(OSSubscriptionStateChanges changes) {
        HashMap<String, Object> hash = new HashMap<>();

        hash.put("to", convertSubscriptionStateToMap(changes.getTo()));
        hash.put("from", convertSubscriptionStateToMap(changes.getFrom()));

        return hash;
    }

    static HashMap<String, Object> convertEmailSubscriptionStateChangesToMap(OSEmailSubscriptionStateChanges changes) {
       HashMap<String, Object> hash = new HashMap<>();

        hash.put("to", convertEmailSubscriptionStateToMap(changes.getTo()));
        hash.put("from", convertEmailSubscriptionStateToMap(changes.getFrom()));

        return hash;
    }

    static HashMap convertPermissionStateChangesToMap(OSPermissionStateChanges changes) {
        HashMap<String, Object> hash = new HashMap<>();

        hash.put("to", convertPermissionStateToMap(changes.getTo()));
        hash.put("from", convertPermissionStateToMap(changes.getFrom()));

        return hash;
    }

    static HashMap<String, Object> convertNotificationToMap(OSNotification notification) throws JSONException {
        HashMap<String, Object> hash = new HashMap<>();

        hash.put("androidNotificationId", notification.getAndroidNotificationId());

        JSONArray payloadJsonArray = new JSONArray();
        if (notification.getGroupedNotifications() != null) {
            for (OSNotification groupedNotification : notification.getGroupedNotifications())
                payloadJsonArray.put(groupedNotification.toJSONObject());
        }

        hash.put("groupedNotifications", payloadJsonArray);

        hash.put("notificationId", notification.getNotificationId());
        hash.put("title", notification.getTitle());
        hash.put("body", notification.getBody());
        hash.put("smallIcon", notification.getSmallIcon());
        hash.put("largeIcon", notification.getLargeIcon());
        hash.put("bigPicture", notification.getBigPicture());
        hash.put("smallIconAccentColor", notification.getSmallIconAccentColor());
        hash.put("launchURL", notification.getLaunchURL());
        hash.put("sound", notification.getSound());
        hash.put("ledColor", notification.getLedColor());
        hash.put("lockScreenVisibility", notification.getLockScreenVisibility());
        hash.put("groupKey", notification.getGroupKey());
        hash.put("groupMessage", notification.getGroupMessage());
        hash.put("fromProjectNumber", notification.getFromProjectNumber());
        hash.put("collapseId", notification.getCollapseId());
        hash.put("priority", notification.getPriority());
        hash.put("additionalData", notification.getAdditionalData());

        ArrayList<HashMap> buttons = new ArrayList<>();

        if (notification.getActionButtons() != null) {
            List<OSNotification.ActionButton> actionButtons = notification.getActionButtons();
            for (int i = 0; i < actionButtons.size(); i++) {
                OSNotification.ActionButton button = actionButtons.get(i);

                HashMap<String, Object> buttonHash = new HashMap<>();
                buttonHash.put("id", button.getId());
                buttonHash.put("text", button.getText());
                buttonHash.put("icon", button.getIcon());
                buttons.add(buttonHash);
            }
        }

        hash.put("actionButtons", buttons);

        if (notification.getBackgroundImageLayout() != null)
            hash.put("backgroundImageLayout", convertAndroidBackgroundImageLayoutToMap(notification.getBackgroundImageLayout()));

        hash.put("rawPayload", notification.getRawPayload());

        Log.d("onesignal", "Created json raw payload: " + convertJSONObjectToHashMap(new JSONObject(notification.getRawPayload())).toString());

        return hash;
    }

    static HashMap<String, Object> convertNotificationOpenResultToMap(OSNotificationOpenedResult openResult) throws JSONException {
        HashMap<String, Object> hash = new HashMap<>();

        hash.put("notification", convertNotificationToMap(openResult.getNotification()));
        hash.put("action", convertNotificationActionToMap(openResult.getAction()));

        return hash;
    }

    private static HashMap<String, Object> convertNotificationActionToMap(OSNotificationAction action) {
        HashMap<String, Object> hash = new HashMap<>();

        hash.put("id", action.getActionId());

        switch (action.getType()) {
            case Opened:
                hash.put("type", 0);
                break;
            case ActionTaken:
                hash.put("type", 1);
        }

        return hash;
    }

    static HashMap<String, Object> convertInAppMessageClickedActionToMap(OSInAppMessageAction action) {
        HashMap<String, Object> hash = new HashMap<>();

        hash.put("click_name", action.getClickName());
        hash.put("click_url", action.getClickUrl());
        hash.put("first_click", action.isFirstClick());
        hash.put("closes_message", action.doesCloseMessage());

        return hash;
    }

    static HashMap<String, Object> convertOutcomeEventToMap(OutcomeEvent outcomeEvent) {
        HashMap<String, Object> hash = new HashMap<>();

        hash.put("session", outcomeEvent.getSession().toString());

        if (outcomeEvent.getNotificationIds() == null)
            hash.put("notification_ids", new JSONArray().toString());
        else
            hash.put("notification_ids", outcomeEvent.getNotificationIds().toString());

        hash.put("id", outcomeEvent.getName());
        hash.put("timestamp", outcomeEvent.getTimestamp());
        hash.put("weight", String.valueOf(outcomeEvent.getWeight()));

        return hash;
    }

    private static HashMap<String, Object> convertAndroidBackgroundImageLayoutToMap(OSNotification.BackgroundImageLayout layout) {
        HashMap<String, Object> hash = new HashMap<>();

        hash.put("image", layout.getImage());
        hash.put("bodyTextColor", layout.getBodyTextColor());
        hash.put("titleTextColor", layout.getBodyTextColor());

        return hash;
    }

    static HashMap<String, Object> convertJSONObjectToHashMap(JSONObject object) throws JSONException {
        HashMap<String, Object> hash = new HashMap<>();

        if (object == null || object == JSONObject.NULL)
           return hash;

        Iterator<String> keys = object.keys();

        while (keys.hasNext()) {
            String key = keys.next();

            if (object.isNull(key))
                continue;

            Object val = object.get(key);

            if (val instanceof JSONArray) {
                val = convertJSONArrayToList((JSONArray)val);
            } else if (val instanceof JSONObject) {
                val = convertJSONObjectToHashMap((JSONObject)val);
            }

            hash.put(key, val);
        }

        return hash;
    }

    private static List<Object> convertJSONArrayToList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            Object val = array.get(i);

            if (val instanceof JSONArray)
                val = OneSignalSerializer.convertJSONArrayToList((JSONArray)val);
            else if (val instanceof JSONObject)
                val = convertJSONObjectToHashMap((JSONObject)val);

            list.add(val);
        }

        return list;
    }
}
