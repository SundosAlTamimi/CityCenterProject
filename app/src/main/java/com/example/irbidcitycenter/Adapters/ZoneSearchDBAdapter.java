package com.example.irbidcitycenter.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.irbidcitycenter.Activity.AddZone;
import com.example.irbidcitycenter.R;
import com.example.irbidcitycenter.RoomAllData;

import java.util.List;

public class ZoneSearchDBAdapter extends BaseAdapter {
    private Context context; //context
    private List<String> items; //data source of the list adapter
    public RoomAllData my_dataBase;
    //public constructor
    public ZoneSearchDBAdapter (Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.zonenamelist, parent, false);
        }
        my_dataBase = RoomAllData.getInstanceDataBase(context);
        // get current item to be displayed
        String currentItem = (String) getItem(position);

        // get the TextView for item name and item description
        TextView textViewItemName = (TextView)
                convertView.findViewById(R.id.itemname);
        LinearLayout linearLayout= convertView.findViewById(R.id.linear);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AddZone.flage3==0) {



                    try {
                        AddZone.zonebarecode.setText(items.get(position));
                        //set zone barecode
                        Log.e("here","hee");
                            AddZone.zonecode1.setText(items.get(position));


                        //set qty of zone
                        int sumqty = my_dataBase.zoneDao().GetQtyOfZone(items.get(position));
                     AddZone.qty1.setText(sumqty + "");


                        //set zonename
                        String zoneNam = my_dataBase.zoneDao().GetNameOfZone(items.get(position));
                            AddZone.zonename1.setText(zoneNam);

                    } catch (Exception e) {

                    }
                }

                else
                {

                    AddZone.DDzoneEDT.setText(items.get(position));
                    AddZone.DDitemcode.setText("");
                    AddZone.DI_zonecode.setText("");
                    AddZone.DIqty.setText("");
                    AddZone.DD_preQTY.setText("");
                    AddZone.DI_itemcode.setText("");
                    AddZone.DDitemcode.setEnabled(true);
                    AddZone.DDitemcode.requestFocus();


                }



               AddZone.searchdialog.dismiss();

            }
        });
        //sets the text for item name and item description from the current item object
        textViewItemName.setText(currentItem);

        // returns the view for the current row
        return convertView;
    }
}