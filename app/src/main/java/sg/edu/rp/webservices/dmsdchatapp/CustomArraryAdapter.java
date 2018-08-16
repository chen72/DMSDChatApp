package sg.edu.rp.webservices.dmsdchatapp;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomArraryAdapter extends ArrayAdapter {

    private ArrayList<Message> messages;
    private Context context;
    private TextView tvMsg,tvUser,tvtime;

    public CustomArraryAdapter(Context context, int resource, ArrayList<Message> objects){
        super(context, resource, objects);
        // Store the food that is passed to this adapter
        messages = objects;
        // Store Context object as we would need to use it later
        this.context = context;
    }

    // getView() is the method ListView will call to get the
    //  View object every time ListView needs a row
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // The usual way to get the LayoutInflater object to
        //  "inflate" the XML file into a View object
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // "Inflate" the row.xml as the layout for the View object
        View rowView = inflater.inflate(R.layout.row, parent, false);

        // Get the TextView object
        tvMsg = (TextView) rowView.findViewById(R.id.tvMessage);
        tvtime = (TextView) rowView.findViewById(R.id.tvTime);
        tvUser = (TextView) rowView.findViewById(R.id.tvUsername);


        // The parameter "position" is the index of the
        //        //  row ListView is requesting.
        //  We get back the food at the same index.
        Message msg = messages.get(position);
        // Set the TextView to show the food

        tvUser.setText(msg.getMessageUser());
        tvtime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", msg.getMessageTime()));
        tvMsg.setText(msg.getMessageText());


        // Return the nicely done up View to the ListView
        return rowView;
    }
}