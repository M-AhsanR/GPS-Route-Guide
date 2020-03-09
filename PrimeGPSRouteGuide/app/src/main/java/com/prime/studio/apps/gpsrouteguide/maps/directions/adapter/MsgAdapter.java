package com.prime.studio.apps.gpsrouteguide.maps.directions.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import com.prime.studio.apps.gpsrouteguide.maps.directions.R;
import com.prime.studio.apps.gpsrouteguide.maps.directions.model.Contact_Model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Asad on 5/24/2016.
 */
public class MsgAdapter extends BaseAdapter {

    Context context;

    ArrayList<HashMap<String, String>> list = null;
    private ArrayList<Contact_Model> arraylist;

    public MsgAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {

        this.context = context;
        this.list = arrayList;



    }




    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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

        if (convertView == null) {
            holder = new ViewHolder();


            convertView = inflater.inflate(R.layout.list_numbers, parent, false);
            holder.txtname = (TextView) convertView.findViewById(R.id.textView);
            holder.txtphone = (TextView) convertView.findViewById(R.id.textView2);

            convertView.setTag(holder);
            convertView.setTag(R.id.checkbox, holder.checkbox);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtname.setText(list.get(position).get("Info"));
        holder.txtphone.setText(list.get(position).get("Number"));

        return convertView;
    }

    static class ViewHolder {
        private TextView txtname;
        private TextView txtphone;
        private CheckBox checkbox;

    }

    /*// Filter Class
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
    }*/
}
