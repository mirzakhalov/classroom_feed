package com.mirzakhalov.classroomai;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class HashMapDiffCallback extends DiffUtil.Callback {

    private final ArrayList<HashMap<String, Object>> mOldCommentsList;
    private final ArrayList<HashMap<String, Object>> mNewCommentsList;

    public HashMapDiffCallback(ArrayList<HashMap<String, Object>> oldCommentsList, ArrayList<HashMap<String, Object>> newCommentsList) {
        this.mOldCommentsList = oldCommentsList;
        this.mNewCommentsList = newCommentsList;
    }

    @Override
    public int getOldListSize() {
        return mOldCommentsList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewCommentsList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return String.valueOf(mOldCommentsList.get(oldItemPosition).get("key")).equals(String.valueOf(mNewCommentsList.get(
                newItemPosition).get("key")));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final HashMap<String, Object> oldcomments = mOldCommentsList.get(oldItemPosition);
        final HashMap<String, Object> newComments = mNewCommentsList.get(newItemPosition);

        return String.valueOf(oldcomments.get("key")).equals(String.valueOf(newComments.get("key")));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
