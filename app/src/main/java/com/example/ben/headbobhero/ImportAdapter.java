package com.example.ben.headbobhero;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class ImportAdapter extends BaseAdapter{
    private final ArrayList songData;
    public ImportAdapter(Map<Long, String> songMap){
        songData = new ArrayList();
        songData.addAll(songMap.entrySet());
    }

    @Override
    public int getCount(){
        return songData.size();
    }

    @Override
    public Map.Entry<Long, String> getItem(int position){
        return (Map.Entry) songData.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<Long, String> item = getItem(position);

        ((TextView) result.findViewById(R.id.song)).setText(item.getValue());

        return result;
    }
}
