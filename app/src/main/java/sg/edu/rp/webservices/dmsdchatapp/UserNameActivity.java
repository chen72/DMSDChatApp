package sg.edu.rp.webservices.dmsdchatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserNameActivity extends AppCompatActivity {

    private Button btnSubmit;
    private EditText etUserName;

    // TODO: Task 1 - Declare an instance of FirebaseAuth
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseRef;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userProfileRef;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);


        etUserName = (EditText) findViewById(R.id.editTextUserName);
        btnSubmit = (Button) findViewById(R.id.btnSubmitDName);

        firebaseAuth = FirebaseAuth.getInstance();
        //get current user information
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        Log.i("user",firebaseUser+"");

        userProfileRef = firebaseDatabase.getReference("profiles");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userProfileRef.child(firebaseUser.getUid()).setValue(etUserName.getText().toString());
                Toast.makeText(getBaseContext(),"done",Toast.LENGTH_SHORT).show();

                Intent i = new Intent(UserNameActivity.this,MainActivity.class);
                startActivity(i);


            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(userProfileRef.child(firebaseUser.getUid()).getDatabase()!=null){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

    }




}
