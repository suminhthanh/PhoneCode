package net.vnict.phonecode;

import android.support.annotation.NonNull;

import net.vnict.phonecode.model.Contact;
import net.vnict.phonecode.model.TransProviders;

import java.util.ArrayList;

public class CommonCode {

    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }
    public static String subString(String s, int pos) {
        return s.substring(0, pos);
    }

    public static String ChuanHoaChuoi(Contact contact) {
        String phone = contact.getPhone().toString();
        phone.trim();
        while (phone.indexOf(" ") != -1) phone = phone.replaceAll(" ", "");
        while (phone.indexOf("(") != -1) phone = phone.replaceAll("\\(", "");
        while (phone.indexOf(")") != -1) phone = phone.replaceAll("\\)", "");
        while (phone.indexOf("+84")!= -1) phone = phone.replaceAll("\\+84", "0");
        while (phone.indexOf("-") != -1) phone = phone.replaceAll("-", "");
        return phone;
    }

    public static ArrayList<ArrayList<TransProviders>> TachMaVungTheoDoDai(ArrayList<String> arrOldCode, ArrayList<String> arrNewCode) {
        ArrayList<ArrayList<TransProviders>> listCode = new ArrayList<>();
        ArrayList<TransProviders> list4So = new ArrayList<TransProviders>();
        ArrayList<TransProviders> list3So = new ArrayList<TransProviders>();
        ArrayList<TransProviders> list2So = new ArrayList<TransProviders>();
        listCode.add(list4So);
        listCode.add(list3So);
        listCode.add(list2So);
        for (int i = 0; i < arrOldCode.size(); i++) {
            TransProviders trans;
            if (arrOldCode.get(i).length() == 4) {
                trans = new TransProviders(arrOldCode.get(i).toString(), arrNewCode.get(i).toString());
                list4So.add(trans);
            }
            if (arrOldCode.get(i).length() == 3) {
                trans = new TransProviders(arrOldCode.get(i).toString(), arrNewCode.get(i).toString());
                list3So.add(trans);
            }
            if (arrOldCode.get(i).length() == 2) {
                trans = new TransProviders(arrOldCode.get(i).toString(), arrNewCode.get(i).toString());
                list2So.add(trans);
            }
        }
        return listCode;
    }

    @NonNull
    public static Boolean CheckChangingPhone(Contact contact, ArrayList<ArrayList<TransProviders>> listCode) {
        String number = contact.getTransnumber();

        if (number.substring(0, 3).equals("020") || number.substring(0, 3).equals("025") || number.substring(0, 3).equals("086") || number.substring(0, 3).equals("086") || number.substring(0, 2).equals("088") || number.substring(0, 2).equals("089"))
            return false;
        if(number.length()<=10)
            return false;
        else
            for (int i = 4; i >= 2; i--) {
                for (int a4 = 0; a4 < listCode.get(4 - i).size(); a4++) {
                    if (number.substring(0, i).equals(listCode.get(4 - i).get(a4).getOldprovides())) {
                        return true;
                    }
                }
            }
        return false;
    }

    public static String ChangingPhone(Contact contact, ArrayList<ArrayList<TransProviders>> listCode) {
        String number = contact.getTransnumber();
        for (int i = 4; i >= 2; i--) {
            for (int a4 = 0; a4 < listCode.get(4 - i).size(); a4++) {
                if (number.substring(0, i).equals(listCode.get(4 - i).get(a4).getOldprovides())) {
                    return number.replaceFirst(listCode.get(4 - i).get(a4).getOldprovides(), listCode.get(4 - i).get(a4).getNewprovides());
                }
            }
        }
        return number;
    }

}
