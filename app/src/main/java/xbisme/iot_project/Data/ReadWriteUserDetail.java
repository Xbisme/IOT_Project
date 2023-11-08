package xbisme.iot_project.Data;

public class ReadWriteUserDetail {
    private String name, address, numberPhone;
    public ReadWriteUserDetail() {
    }
    public ReadWriteUserDetail(String name, String address, String numberPhone) {
        this.name = name;
        this.address = address;
        this.numberPhone = numberPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }
}
