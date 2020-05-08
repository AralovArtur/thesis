package model;

/**
 * Timestamp model
 *
 * @author Artur Aralov
 */
public class Timestamp {
    private int signatureID;
    private java.sql.Timestamp creationTime;
    private String timestampIssuer;
    private String certificateIssuer;

    public Timestamp(int signatureID, java.sql.Timestamp creationTime, String timestampIssuer, String certificateIssuer) {
        this.signatureID = signatureID;
        this.creationTime = creationTime;
        this.timestampIssuer = timestampIssuer;
        this.certificateIssuer = certificateIssuer;
    }

    public int getSignatureID() {
        return signatureID;
    }

    public java.sql.Timestamp getCreationTime() {
        return creationTime;
    }

    public String getTimestampIssuer() {
        return timestampIssuer;
    }

    public String getCertificateIssuer() {
        return certificateIssuer;
    }
}
