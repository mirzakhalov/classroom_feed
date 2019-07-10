package com.mirzakhalov.classroomai;



import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CommentRecyclerView extends RecyclerView.Adapter<CommentRecyclerView.MyViewHolder> {
    private ArrayList<HashMap<String, Object>> mResults = new ArrayList<>();
    public Context mContext;


    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView comment;
        TextView upvoteCount;


        public MyViewHolder(View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.comment);
            upvoteCount = itemView.findViewById(R.id.upvoteCount);

        }

        public void bind(final String id, final Context context) {
            upvoteCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()){

                               // Toast.makeText(context, "You already upvoted this comment", Toast.LENGTH_LONG).show();
                                int newCount = Integer.parseInt(upvoteCount.getText().toString()) - 1;
                                FirebaseDatabase.getInstance().getReference().child("Sessions/" + SliderActivity.CLASSCODE + "/Comments/" + id + "/upvotes").setValue(newCount);
                                FirebaseDatabase.getInstance().getReference().child("Sessions/" + SliderActivity.CLASSCODE + "/Audience/" + MainActivity.USERID + "/comments/" + id).removeValue();


                            } else{

                                int newCount = Integer.parseInt(upvoteCount.getText().toString()) + 1;
                                FirebaseDatabase.getInstance().getReference().child("Sessions/" + SliderActivity.CLASSCODE + "/Comments/" + id + "/upvotes").setValue(newCount);
                                FirebaseDatabase.getInstance().getReference().child("Sessions/" + SliderActivity.CLASSCODE + "/Audience/" + MainActivity.USERID + "/comments/" + id).setValue("1");
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("Firebase Error", databaseError.toString());
                        }
                    };

                    FirebaseDatabase.getInstance().getReference().child("Sessions/" + SliderActivity.CLASSCODE + "/Audience/" + MainActivity.USERID + "/comments/" + id).addListenerForSingleValueEvent(postListener);

                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CommentRecyclerView(ArrayList<HashMap<String, Object>> mResults, Context context) {
        updateEmployeeListItems(mResults);
        Collections.sort(this.mResults, new HashMapComparator("upvotes"));
        mContext = context;

    }

    public void updateEmployeeListItems(ArrayList<HashMap<String, Object>> newComments) {
        final HashMapDiffCallback diffCallback = new HashMapDiffCallback(mResults, newComments);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.mResults.clear();
        this.mResults.addAll(newComments);
        diffResult.dispatchUpdatesTo(this);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CommentRecyclerView.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_row, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.comment.setText(String.valueOf(mResults.get(position).get("text")));
        holder.upvoteCount.setText(String.valueOf(mResults.get(position).get("upvotes")));

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    holder.upvoteCount.setBackground(mContext.getDrawable(R.drawable.rounded_upvote));
                    holder.upvoteCount.setVisibility(View.VISIBLE);

                } else{
                    holder.upvoteCount.setBackground(mContext.getDrawable(R.drawable.rounded_not_upvoted));
                    holder.upvoteCount.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Firebase Error", databaseError.toString());
            }
        };

        FirebaseDatabase.getInstance().getReference().child("Sessions/" + SliderActivity.CLASSCODE + "/Audience/" + MainActivity.USERID + "/comments/" + mResults.get(position).get("key")).addListenerForSingleValueEvent(postListener);



        holder.bind(String.valueOf(mResults.get(position).get("key")), mContext);


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mResults.size();
    }
}
