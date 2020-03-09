package com.prime.studio.apps.gpsrouteguide.maps.directions.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.prime.studio.apps.gpsrouteguide.maps.directions.R;
import com.prime.studio.apps.gpsrouteguide.maps.directions.model.Contact_Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Asad on 5/24/2016.
 */
public class SelectContactAdapter extends ArrayAdapter<Contact_Model> {

    Context context;
    ArrayList<Contact_Model> arrayList;
    private List<Contact_Model> worldpopulationlist = null;
    private ArrayList<Contact_Model> arraylist;

    public SelectContactAdapter(Context context, ArrayList<Contact_Model> arrayList) {
        super(context, R.layout.item_phonelist);

        this.context = context;


        this.worldpopulationlist = arrayList;

        this.arraylist = new ArrayList<Contact_Model>();
        this.arraylist.addAll(worldpopulationlist);


    }

    public SelectContactAdapter(Context context, int resource) {
        super(context, resource);
    }


    @Override
    public int getCount() {
        return worldpopulationlist.size();
    }

  /*  @Override
    public Contact_Model getItem(int position) {
        return position;
    }*/

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;
        final Contact_Model iData = worldpopulationlist.get(position);
        if (convertView == null) {
            holder = new ViewHolder();


            convertView = inflater.inflate(R.layout.item_phonelist, parent, false);
            holder.txtname = (TextView) convertView.findViewById(R.id.name);
            holder.txtphone = (TextView) convertView.findViewById(R.id.phone_no);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);

            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    worldpopulationlist.get(getPosition).setChecked(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            convertView.setTag(holder);
            convertView.setTag(R.id.checkbox, holder.checkbox);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtname.setText(iData.getName());
        holder.txtphone.setText(iData.getPhone_number());
        holder.checkbox.setTag(position); // This line is important.
        holder.checkbox.setChecked(worldpopulationlist.get(position).isChecked());


        return convertView;
    }

    static class ViewHolder {
        private TextView txtname;
        private TextView txtphone;
        private CheckBox checkbox;

    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        worldpopulationlist.clear();
        if (charText.length() == 0) {
            worldpopulationlist.addAll(arraylist);
        } else {
            for (Contact_Model wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getPhone_number().contains(charText) ) {
                    worldpopulationlist.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
