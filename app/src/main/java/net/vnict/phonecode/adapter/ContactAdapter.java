package net.vnict.phonecode.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.vnict.phonecode.utils.CommonCode;
import net.vnict.phonecode.R;
import net.vnict.phonecode.model.Contact;
import net.vnict.phonecode.utils.Utils;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {
    private Activity context;
    private int resource;
    private List<Contact> objects;
    private static final String MOBIFONE = "07";
    private static final String VINAPHONE = "08";
    private static final String VIETTEL = "03";
    private static final String VIETNAMOBILE = "05";

    public ContactAdapter(Activity context, int resource, List<Contact> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    public static void callContactSystem(Activity activity, String phoneNumber) {
        String url = "tel:" + phoneNumber;
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        activity.startActivity(intent);
    }

    public static void viewContactSystem(Activity activity, String contactId) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, "" + contactId));
        activity.startActivity(intent);
        Utils.LOG("id-Adapter=" + contactId);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(this.resource, null);
            holder.txtTen = (TextView) convertView.findViewById(R.id.txtTen);
            holder.txtPhone = (TextView) convertView.findViewById(R.id.txtPhone);
            holder.call = (ImageButton) convertView.findViewById(R.id.btnCall);
            holder.detail = (ImageButton) convertView.findViewById(R.id.btnChiTiet);
            holder.chkChecked = (CheckBox) convertView.findViewById(R.id.chkChecked);
            holder.imgLogo = (ImageView) convertView.findViewById(R.id.imgLogo);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        final Contact danhBa = this.objects.get(position);
        //Tra ve danh ba muon hien thi
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = danhBa.getNewphonenumber();
                callContactSystem(context, phoneNumber);
            }
        });
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactId = danhBa.getTenCotId();
                viewContactSystem(context, contactId);
            }
        });
        holder.txtTen.setText(danhBa.getName());
        holder.txtPhone.setText(danhBa.getTransnumber() + " -> " + danhBa.getNewphonenumber());
        holder.chkChecked.setChecked(danhBa.isChecked());
        holder.chkChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Cho checkitem bằng true
                    danhBa.setChecked(true);
                } else {
                    //Cho check_all bằng false
                    CheckBox checkBoxAll = (CheckBox) context.findViewById(R.id.check_all);
                    if (checkBoxAll.isChecked())
                        checkBoxAll.setChecked(false);
                    //Cho checkitem bằng false
                    danhBa.setChecked(false);
                }
            }
        });
        String code = CommonCode.subString(danhBa.getNewphonenumber(), 2) ;
        switch (code)
        {
            case MOBIFONE:
                holder.imgLogo.setImageResource(R.drawable.ic_mobifone);
                break;
            case VINAPHONE:
                holder.imgLogo.setImageResource(R.drawable.ic_vinaphone);
                break;
            case VIETTEL:
                holder.imgLogo.setImageResource(R.drawable.ic_viettel);
                break;
            case VIETNAMOBILE:
                if(CommonCode.subString(danhBa.getTransnumber(),4).equals("0188"))
                    holder.imgLogo.setImageResource(R.drawable.ic_vnm);
                else
                    holder.imgLogo.setImageResource(R.drawable.ic_gtel);
                break;
             default:
                 break;
        }
        return convertView;
    }
    static class ViewHolder{
        private  TextView txtTen, txtPhone ;
        private ImageButton call, detail;
        private CheckBox chkChecked;
        private ImageView imgLogo;
    }
}