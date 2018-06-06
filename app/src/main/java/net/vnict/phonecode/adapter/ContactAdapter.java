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
    Activity context;
    int resource;
    List<Contact> objects;
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
        final View row = inflater.inflate(this.resource, null);


        TextView txtTen = (TextView) row.findViewById(R.id.txtTen);
        TextView txtPhone = (TextView) row.findViewById(R.id.txtPhone);
        ImageButton call = (ImageButton) row.findViewById(R.id.btnCall);
        ImageButton detail = (ImageButton) row.findViewById(R.id.btnChiTiet);
        CheckBox chkChecked = (CheckBox) row.findViewById(R.id.chkChecked);
        ImageView imgLogo = (ImageView) row.findViewById(R.id.imgLogo);

        final Contact danhBa = this.objects.get(position);
        //Tra ve danh ba muon hien thi
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = danhBa.getNewphonenumber();
                callContactSystem(context, phoneNumber);
            }


        });
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactId = danhBa.getTenCotId();
                viewContactSystem(context, contactId);
            }
        });
        txtTen.setText(danhBa.getName());
        txtPhone.setText(danhBa.getTransnumber() + " -> " + danhBa.getNewphonenumber());
        chkChecked.setChecked(danhBa.isChecked());
        chkChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
                imgLogo.setImageResource(R.drawable.ic_mobifone);
                break;
            case VINAPHONE:
                imgLogo.setImageResource(R.drawable.ic_vinaphone);
                break;
            case VIETTEL:
                imgLogo.setImageResource(R.drawable.ic_viettel);
                break;
            case VIETNAMOBILE:
                if(CommonCode.subString(danhBa.getTransnumber(),4).equals("0188"))
                    imgLogo.setImageResource(R.drawable.ic_vnm);
                else
                    imgLogo.setImageResource(R.drawable.ic_gtel);
                break;
             default:
                 break;


        }

       // imgLogo.setImageDrawable();
        return row;
    }

}
