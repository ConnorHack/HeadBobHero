package com.example.ben.headbobhero;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SongAdapter extends ArrayAdapter<RegisteredSong> {

    public SongAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public SongAdapter(Context context, int resource, List<RegisteredSong> songs) {
        super(context, resource, songs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row_layout, null);
        }

        RegisteredSong p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.song);
            tt1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 25);

            if (tt1 != null) {
                tt1.setText(p.getSongName());
            }
        }

        return v;
    }

}
