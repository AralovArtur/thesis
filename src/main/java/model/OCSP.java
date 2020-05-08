package model;

import java.sql.Date;

/**
 * OCSP model
 *
 * @author Artur Aralov
 */
public class OCSP {
    private int signatureID;
    private java.sql.Timestamp responseTime;
    private String responseIssuer;
    private String certificateIssuer;

    public OCSP(int signatureID, java.sql.Timestamp responseTime, String responseIssuer, String certificateIssuer) {
        this.signatureID = signatureID;
        this.responseTime = responseTime;
        this.responseIssuer = responseIssuer;
        this.certificateIssuer = certificateIssuer;
    }

    public int getSignatureID() {
        return signatureID;
    }

    public java.sql.Timestamp getResponseTime() {
        return responseTime;
    }

    public String getResponseIssuer() {
        return responseIssuer;
    }

    public String getCertificateIssuer() {
        return certificateIssuer;
    }
}
