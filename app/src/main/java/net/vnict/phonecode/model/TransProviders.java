package net.vnict.phonecode.model;


public class TransProviders {
    private String oldprovides;
    private String newprovides;

    public TransProviders() {
    }

    public TransProviders(String oldprovides, String newprovides) {
        this.oldprovides = oldprovides;
        this.newprovides = newprovides;
    }

    public String getOldprovides() {
        return oldprovides;
    }

    public void setOldprovides(String oldprovides) {
        this.oldprovides = oldprovides;
    }

    public String getNewprovides() {
        return newprovides;
    }

    public void setNewprovides(String newprovides) {
        this.newprovides = newprovides;
    }
}
