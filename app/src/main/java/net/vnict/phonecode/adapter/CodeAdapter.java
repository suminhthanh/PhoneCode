package net.vnict.phonecode.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.vnict.phonecode.R;

import java.util.ArrayList;
import java.util.HashMap;

import static net.vnict.phonecode.utils.Constants.FIRST_COLUMN;
import static net.vnict.phonecode.utils.Constants.SECOND_COLUMN;
import static net.vnict.phonecode.utils.Constants.THIRD_COLUMN;

public class CodeAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> list;
    private Activity activity;

    public CodeAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = activity.getLayoutInflater();
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_code, null);
            holder.txtFirst = (TextView) convertView.findViewById(R.id.tinhThanh);
            holder.txtSecond = (TextView) convertView.findViewById(R.id.oldCode);
            holder.txtThird = (TextView) convertView.findViewById(R.id.newCode);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HashMap<String, String> map = list.get(position);
        holder.txtFirst.setText(map.get(FIRST_COLUMN));
        holder.txtSecond.setText(map.get(SECOND_COLUMN));
        holder.txtThird.setText(map.get(THIRD_COLUMN));
        return convertView;
    }

    static class ViewHolder {
        private TextView txtFirst, txtSecond, txtThird;
    }

}
