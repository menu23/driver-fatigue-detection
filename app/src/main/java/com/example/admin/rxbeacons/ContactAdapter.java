package com.example.admin.rxbeacons;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Artemis on 19-May-18.
 */

public class ContactAdapter extends ArrayAdapter {

    public ContactAdapter(Activity context, ArrayList<Contact> contacts) {
        super(context,0,contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.contact_list_item, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        Contact currentContact = (Contact) getItem(position);

        ImageView imageView = (ImageView)listItemView.findViewById(R.id.contactImage);
        imageView.setImageResource(currentContact.getImage());

        TextView nameView = (TextView)listItemView.findViewById(R.id.name);
        nameView.setText(currentContact.getName());

        TextView phoneView = (TextView)listItemView.findViewById(R.id.number);
        phoneView.setText(currentContact.getNumber());

        return listItemView;
    }
}
