package model;

/**
 * Signature model
 *
 * @author Artur Aralov
 */
public class Signer {
    private String serialNumber;
    private String givenName;
    private String surname;
    private String eIDTool;

    public Signer(String serialNumber, String givenName, String surname, String eIDTool) {
        this.serialNumber = serialNumber;
        this.givenName = givenName;
        this.surname = surname;
        this.eIDTool = eIDTool;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getSurname() {
        return surname;
    }

    public String geteIDTool() {
        return eIDTool;
    }
}