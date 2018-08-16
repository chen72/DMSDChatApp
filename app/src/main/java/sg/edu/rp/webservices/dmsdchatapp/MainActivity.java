package sg.edu.rp.webservices.dmsdchatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private EditText etMessage;
    private FirebaseDatabase firebaseDatabase;
    private Button btnSend;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference messageList;
    private ArrayList<Message> alMessage;
    private CustomArraryAdapter aaMessage;
    private ListView lv;
    String user;
    private DatabaseReference username;
    private TextView tvWeather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMessage=(EditText)findViewById(R.id.editTextMsg);
        btnSend = (Button)findViewById(R.id.btnSend);
        lv = (ListView)findViewById(R.id.lv);
        tvWeather = (TextView)findViewById(R.id.tvWeather);

        alMessage = new ArrayList<Message>();
        aaMessage = new CustomArraryAdapter(this, R.layout.row, alMessage);

//        aaMessage = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1, alMessage);

        lv.setAdapter(aaMessage);
        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        //get current user information
        firebaseUser = firebaseAuth.getCurrentUser();
        username = firebaseDatabase.getReference();
        username.child("profiles/"+firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(String.class);
                Log.i("username",user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        messageList = firebaseDatabase.getReference("messages");
        messageList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("MainActivity", "onChildAdded()");
                Message msg = dataSnapshot.getValue(Message.class);
                if (msg != null) {
                    msg.setId(dataSnapshot.getKey());
                    alMessage.add(msg);
                    aaMessage.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("MainActivity", "onChildChanged()");

                String selectedId = dataSnapshot.getKey();
                Message msg = dataSnapshot.getValue(Message.class);
                if (msg != null) {
                    for (int i = 0; i < alMessage.size(); i++) {
                        if (alMessage.get(i).getId().equals(selectedId)) {
                            msg.setId(selectedId);
                            alMessage.set(i, msg);
                            break;
                        }
                    }
                    aaMessage.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Log.i("MainActivity", "onChildRemoved()");

                String selectedId = dataSnapshot.getKey();
                for(int i= 0; i < alMessage.size(); i++) {
                    if (alMessage.get(i).getId().equals(selectedId)) {
                        alMessage.remove(i);
                        break;
                    }
                }
                aaMessage.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("MainActivity", "onChildMoved()");


                String selectedId = dataSnapshot.getKey();
                Message msg = dataSnapshot.getValue(Message.class);
                if (msg != null) {
                    for (int i = 0; i < alMessage.size(); i++) {
                        if (alMessage.get(i).getId().equals(selectedId)) {
                            msg.setId(selectedId);
                            alMessage.set(i, msg);
                            break;
                        }
                    }
                    aaMessage.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Message select = alMessage.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete message")
                        // Set text for the positive button and the corresponding
                        //  OnClickListener when it is clicked
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                messageList.child(select.getId()).removeValue();
                                Toast.makeText(getApplicationContext(), "Item record Delete successfully", Toast.LENGTH_SHORT).show();


                            }
                        })
                        // Set text for the negative button and the corresponding
                        //  OnClickListener when it is clicked
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(MainActivity.this, "You clicked no",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                Toast.makeText(MainActivity.this,select.getMessageText(),Toast.LENGTH_SHORT).show();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString();
                Message msg = new Message(message,user,System.currentTimeMillis());
                messageList.push().setValue(msg);
                etMessage.setText("");
            }
        });



        // Code for step 1 start
        HttpRequest request = new HttpRequest
                ("https://api.data.gov.sg/v1/environment/2-hour-weather-forecast");
        request.setOnHttpResponseListener(mHttpResponseListener);
        request.setMethod("GET");
        request.execute();
        // Code for step 1 end

    }
    // Code for step 2 start
    private HttpRequest.OnHttpResponseListener mHttpResponseListener =
            new HttpRequest.OnHttpResponseListener() {
                @Override
                public void onResponse(String response){

                    // process response here
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("items");
                        JSONObject item = jsonArray.getJSONObject(0);
                        JSONArray forecasts = item.getJSONArray("forecasts");
                        JSONObject woodlands = new JSONObject();
                        for (int i=0; i<forecasts.length(); i++){
                            JSONObject singleforcast = forecasts.getJSONObject(i);
                            String area = singleforcast.getString("area");
                            if(area.equalsIgnoreCase("Woodlands")){
                                String weather = singleforcast.getString("forecast");
                                tvWeather.setText("Weather Forcast @ Woodlands: "+weather);
                            }
                        }

                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }


                }
            };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
   if (id == R.id.action_logout) {

            // TODO: User log out
            firebaseAuth.signOut();

            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
