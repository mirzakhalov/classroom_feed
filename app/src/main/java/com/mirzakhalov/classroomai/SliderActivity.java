package com.mirzakhalov.classroomai;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class SliderActivity extends AppCompatActivity {

    public static String CLASSCODE = "";
    public static String FULLNAME = "";
    public int CURRENT_MODE = 0;

    public String LABELS []= {"Confused", "Neutral", "Understood"};



    RecyclerView recyclerView;
    EditText comment;
    SeekBar seekBar;
    ImageView confused;
    ImageView neutral;
    ImageView happy;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<HashMap<String, Object>> comments = new ArrayList<>();

    HashMap<String, Object> newComment = new HashMap();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(SliderActivity.this, "Leave Class", "Are you sure you want to leave this class?", "Yes", "No");
            }


        });

        mLayoutManager = new LinearLayoutManager(SliderActivity.this);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(mLayoutManager);


        comment = findViewById(R.id.comment);
        seekBar = findViewById(R.id.seekBar);

        confused = findViewById(R.id.confused);
        neutral = findViewById(R.id.neutral);
        happy = findViewById(R.id.happy);

        confused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setProgress(0);
                CURRENT_MODE = -1;
                updateState();
            }
        });

        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setProgress(1);
                CURRENT_MODE = 0;
                updateState();
            }
        });

        happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setProgress(2);
                CURRENT_MODE = 1;
                updateState();
            }
        });

        CLASSCODE = getIntent().getStringExtra("classCode");
        FULLNAME = getIntent().getStringExtra("fullName");

        comment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    newComment.clear();
                    String key  = UUID.randomUUID().toString();

                    newComment.put("text", comment.getText().toString());
                    newComment.put("key", key);
                    newComment.put("time", System.currentTimeMillis());
                    newComment.put("upvotes", 0);

                    FirebaseDatabase.getInstance().getReference().child("Sessions/" + CLASSCODE + "/Comments/" + key).setValue(newComment);
                    comment.setText("");
                }
                return false;
            }
        });



        FirebaseDatabase.getInstance().getReference().child("Sessions/"+ CLASSCODE + "/Audience/" + MainActivity.USERID + "/name").setValue(FULLNAME);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.clear();
                Log.d("Firebase Success", dataSnapshot.toString());

                // availableQuestions.clear();
                HashMap<String, Object> AllComments = (HashMap<String, Object>) dataSnapshot.getValue();
                if(AllComments != null) {
                    Set<String> keys = AllComments.keySet();
                    for (Iterator<String> it = keys.iterator(); it.hasNext(); ) {
                        String key = it.next();
                        HashMap<String, Object> singleComment = new HashMap<>();
                        singleComment = (HashMap) AllComments.get(key);
                        if (singleComment.size() != 0) {
                            comments.add(singleComment);
                        }
                    }
                    if (!comments.isEmpty()) {
                        mAdapter = new CommentRecyclerView(comments, SliderActivity.this);
                        recyclerView.setAdapter(mAdapter);

                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Firebase Error", databaseError.toString());
            }
        };

        FirebaseDatabase.getInstance().getReference().child("Sessions/" + CLASSCODE + "/Comments").addValueEventListener(postListener);

        String time = String.valueOf(System.currentTimeMillis());
        FirebaseDatabase.getInstance().getReference().child("Sessions/"+ CLASSCODE + "/Audience/" + MainActivity.USERID + "/logs/" + time).setValue(CURRENT_MODE);
        FirebaseDatabase.getInstance().getReference().child("Sessions/"+ CLASSCODE + "/Audience/" + MainActivity.USERID + "/lastState").setValue(CURRENT_MODE);




        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CURRENT_MODE = i-1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateState();
            }
        });





    }

    private void updateState(){
        String time = String.valueOf(System.currentTimeMillis());
        FirebaseDatabase.getInstance().getReference().child("Sessions/"+ CLASSCODE + "/Audience/" + MainActivity.USERID + "/logs/" + time).setValue(CURRENT_MODE);
        FirebaseDatabase.getInstance().getReference().child("Sessions/"+ CLASSCODE + "/Audience/" + MainActivity.USERID + "/lastState").setValue(CURRENT_MODE);

    }


    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(8);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    public void showAlertDialog(Context context, String title, String message, String posBtnMsg, String negBtnMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(posBtnMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        builder.setNegativeButton(negBtnMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
