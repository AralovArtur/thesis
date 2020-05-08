package model;

/**
 * Certificate model
 *
 * @author Artur Aralov
 */
public class Certificate {
    private int signatureID;
    private String publicKey;
    private java.sql.Timestamp validNotBefore;
    private java.sql.Timestamp validNotAfter;
    private String signersCertificateIssuer;

    public Certificate(int signatureID, String publicKey, java.sql.Timestamp validNotBefore, java.sql.Timestamp validNotAfter, String signersCertificateIssuer) {
        this.signatureID = signatureID;
        this.publicKey = publicKey;
        this.validNotBefore = validNotBefore;
        this.validNotAfter = validNotAfter;
        this.signersCertificateIssuer = signersCertificateIssuer;
    }

    public int getSignatureID() {
        return signatureID;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public java.sql.Timestamp getValidNotBefore() {
        return validNotBefore;
    }

    public java.sql.Timestamp getValidNotAfter() {
        return validNotAfter;
    }

    public String getSignersCertificateIssuer() {
        return signersCertificateIssuer;
    }
}
