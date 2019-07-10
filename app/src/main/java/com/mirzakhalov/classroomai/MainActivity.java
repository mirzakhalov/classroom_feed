package com.mirzakhalov.classroomai;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    ImageView logo;
    EditText classCode;
    Button joinClass;

    private FirebaseAuth mAuth;
    public FirebaseUser user = null;

    public static String USERID;
// ...
// Initialize Firebase Auth


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mAuth = FirebaseAuth.getInstance();

        signIn();


        // initialize the ui elements
        logo = findViewById(R.id.logo);
        classCode = findViewById(R.id.classCode);
        joinClass = findViewById(R.id.joinClass);


        joinClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String classCodeText = classCode.getText().toString();

                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        // availableQuestions.clear();
                        Boolean ended = (Boolean) dataSnapshot.getValue();
                        if(ended != null) {
                            if(!ended){
                                Intent intent = new Intent(MainActivity.this, SliderActivity.class);
                                intent.putExtra("classCode", classCodeText);
                                startActivity(intent);
                            } else{
                                Toast.makeText(MainActivity.this, "This class doesn't exist or already ended", Toast.LENGTH_LONG).show();
                                return;
                            }
                        } else{
                            Toast.makeText(MainActivity.this, "This class doesn't exist or already ended", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Firebase Error", databaseError.toString());
                    }
                };

                FirebaseDatabase.getInstance().getReference().child("Sessions/" + classCodeText + "/Info/ended").addListenerForSingleValueEvent(postListener);


            }
        });

    }



    private void signIn(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Firebase login", "signInAnonymously:success");
                            user = mAuth.getCurrentUser();
                            if(user != null){
                                USERID = user.getUid();
                            } else{
                                USERID = "unknown";
                            }
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Firebase login", "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }



}
