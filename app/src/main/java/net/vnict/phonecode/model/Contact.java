package net.vnict.phonecode.model;


public class Contact {
    private String tenCotId;
    private String name;
    private String phone;
    private String transnumber;
    private String newphonenumber;
    private boolean checked = false;

    public Contact() {
    }

    public Contact(String tenCotId, String name, String phone) {
//        this.vtTenCotName = vtTenCotName;
        this.tenCotId = tenCotId;
        this.name = name;
        this.phone = phone;
    }

    public Contact(String tenCotId, String name, String phone, boolean checked) {
        this.tenCotId = tenCotId;
        this.name = name;
        this.phone = phone;
        this.checked = checked;
    }

    public String getTenCotId() {
        return tenCotId;
    }

    public void setTenCotId(String tenCotId) {
        this.tenCotId = tenCotId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    /*public String toString() {
        return this.name+"\n"+this.newphonenumber;
    }*/
    public String toString() {
        return this.newphonenumber + " id=" + this.getTenCotId();
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getTransnumber() {
        return transnumber;
    }

    public void setTransnumber(String transnumber) {
        this.transnumber = transnumber;
    }

    public String getNewphonenumber() {
        return newphonenumber;
    }

    public void setNewphonenumber(String newphonenumber) {
        this.newphonenumber = newphonenumber;
    }
}
