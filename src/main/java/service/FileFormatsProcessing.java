package service;

import jdbc.PostgreSQL;
import model.Certificate;
import model.OCSP;
import model.Signer;
import model.Timestamp;
import org.apache.log4j.Logger;
import org.digidoc4j.Container;
import org.digidoc4j.ContainerBuilder;
import org.digidoc4j.Signature;
import org.digidoc4j.X509Cert;

import java.io.File;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Retrieve data from file formats.
 *
 * @author Artur Aralov
 */
public class FileFormatsProcessing {
    final static Logger logger = Logger.getLogger(FileFormatsProcessing.class);
    final static PostgreSQL jdbc = new PostgreSQL();

    /**
     * List files in specified directory
     *
     * @param args specifies a directory that contains file formats
     */
    public static void main(String[] args) throws SQLException {
        File directory = new File(args[0]);
        File[] files = listFilesForDirectory(directory);
        
        jdbc.connect();

        for (File file : files) {
            String pathname = file.getPath();
            if (!pathname.endsWith(".ini")) {
                extractData(pathname);
            }
        }

        jdbc.connect().close();
    }

    /**
     * List files in specified directory
     *
     * @param directory specifies directory that contains file formats
     * @return file formats
     */
    public static File[] listFilesForDirectory(File directory) {
        return directory.listFiles();
    }

    /**
     * Extracts data from file format
     *
     * @param pathname specifies pathname of file format
     */
    public static void extractData(String pathname) throws SQLException {
        Container container = ContainerBuilder.aContainer().fromExistingFile(pathname).build();

        for (Signature signature : container.getSignatures()) {
            X509Cert signersCertificate = signature.getSigningCertificate();
            String subjectName = signersCertificate.getSubjectName();
            List<String> subjectTokens = Arrays.asList(subjectName.split(", "));

            String serialNumber = "";

            try {
                serialNumber = subjectTokens
                        .stream()
                        .filter(element -> element.contains("SERIALNUMBER=") == true)
                        .collect(Collectors.toList()).get(0)
                        .split("=")[1];
            } catch (IndexOutOfBoundsException exception) {
                logger.warn("Serial number is not specified!");
            }

            String givenName = "";

            try {
                givenName = subjectTokens
                        .stream()
                        .filter(element -> element.contains("GIVENNAME=") == true)
                        .collect(Collectors.toList()).get(0)
                        .split("=")[1];
            } catch (IndexOutOfBoundsException exception) {
                logger.warn("Given name is not specified!");
            }

            String surname = "";

            try {
                surname = subjectTokens
                        .stream()
                        .filter(element -> element.contains("SURNAME=") == true)
                        .collect(Collectors.toList()).get(0)
                        .split("=")[1];
            } catch (IndexOutOfBoundsException exception) {
                logger.warn("Surname is not specified!");
            }

            String eIDTool = "";

            try {
                eIDTool = subjectTokens
                        .stream()
                        .filter(element -> element.contains("O=") == true)
                        .collect(Collectors.toList()).get(0)
                        .split("=")[1];
            } catch (IndexOutOfBoundsException exception) {
                logger.warn("eID tool is not specified!");
            }

            int signatureID = saveSignature(serialNumber, givenName, surname, eIDTool);

            X509Certificate certificate = signersCertificate.getX509Certificate();
            String publicKey = certificate.getPublicKey().getAlgorithm();
            java.sql.Timestamp validNotBefore = new java.sql.Timestamp(certificate.getNotBefore().getTime());
            java.sql.Timestamp validNotAfter = new java.sql.Timestamp(certificate.getNotAfter().getTime());
            String issuerName = signersCertificate.issuerName();
            List<String> issuerTokens = Arrays.asList(issuerName.split(","));

            String signersCertificateIssuer = "";

            try {
                signersCertificateIssuer = issuerTokens
                        .stream()
                        .filter(element -> element.contains("CN=") == true)
                        .collect(Collectors.toList()).get(0)
                        .split("=")[1];
            } catch (IndexOutOfBoundsException exception) {
                logger.warn("Signer's certificate issuer is not specified!");
            }

            saveCertificate(signatureID, publicKey, validNotBefore, validNotAfter, signersCertificateIssuer);

            java.sql.Timestamp OCSPResponseTime = new java.sql.Timestamp(signature.getOCSPResponseCreationTime().getTime());
            X509Cert OCSPCertificate = signature.getOCSPCertificate();
            String OCSPSubjectName = OCSPCertificate.getSubjectName();
            List<String> OCSPSubjectNameTokens = Arrays.asList(OCSPSubjectName.split(", "));

            String OCSPResponseIssuer = "";

            try {
                OCSPResponseIssuer = OCSPSubjectNameTokens
                        .stream()
                        .filter(element -> element.contains("CN=") == true)
                        .collect(Collectors.toList()).get(0)
                        .split("=")[1];
            } catch (IndexOutOfBoundsException exception) {
                logger.warn("OCSP response issuer is not specified!");
            }

            String OCSPCertificateIssuerName = OCSPCertificate.issuerName();
            List<String> OCSPCertificateIssuerTokens = Arrays.asList(OCSPCertificateIssuerName.split(","));

            String OCSPCertificateIssuer = "";

            try {
                OCSPCertificateIssuer = OCSPCertificateIssuerTokens
                        .stream()
                        .filter(element -> element.contains("CN=") == true)
                        .collect(Collectors.toList()).get(0)
                        .split("=")[1];
            } catch (IndexOutOfBoundsException exception) {
                logger.warn("OCSP certificate issuer is not specified!");
            }

            saveOCSP(signatureID, OCSPResponseTime, OCSPResponseIssuer, OCSPCertificateIssuer);

            if (container.getType().equals("ASICE")) {
                java.sql.Timestamp timestampCreationTime = new java.sql.Timestamp(signature.getTimeStampCreationTime().getTime());
                X509Cert timestampTokenCertificate = signature.getTimeStampTokenCertificate();
                String timestampIssuer = "";
                String del1 = timestampTokenCertificate.getSubjectName();
                String del2 = timestampTokenCertificate.issuerName();
                try {
                    timestampIssuer = Arrays.asList(timestampTokenCertificate.getSubjectName().split(", "))
                            .stream()
                            .filter(element -> element.contains("CN=") == true)
                            .collect(Collectors.toList()).get(0)
                            .split("=")[1];
                } catch (IndexOutOfBoundsException exception) {
                    logger.warn("Timestamp issuer is not specified!");
                }

                String timestampCertificateIssuer = "";

                try {
                    timestampCertificateIssuer = Arrays.asList(timestampTokenCertificate.issuerName().split(","))
                            .stream()
                            .filter(element -> element.contains("CN=") == true)
                            .collect(Collectors.toList()).get(0)
                            .split("=")[1];
                } catch (IndexOutOfBoundsException exception) {
                    logger.warn("Timestamp certificate issuer is not specified!");
                }

                saveTimestamp(signatureID, timestampCreationTime, timestampIssuer, timestampCertificateIssuer);
            }
        }
    }

    /**
     * Saves signature to database
     *
     * @param serialNumber specifies signer's serial number
     * @param givenName specifies signer's given name
     * @param surname specifies signer's surname
     * @param eIDTool specifies eID tool that signer used
     * @return signature id
     */
    public static int saveSignature(String serialNumber, String givenName, String surname, String eIDTool) throws SQLException {
        int signatureID = jdbc.insertSigner(new Signer(serialNumber, givenName, surname, eIDTool));
        logger.info(String.format("Save signature with id %d.", signatureID));
        return signatureID;
    }

    /**
     * Saves signer's certificate to database
     *
     * @param signatureID specifies signature id
     * @param publicKey specifies signer's public key
     * @param validNotBefore specifies time until which the certificate is not valid
     * @param validNotAfter specifies time after which the certificate is not valid
     * @param signersCertificateIssuer specifies issuer of certificate
     */
    public static void saveCertificate(int signatureID, String publicKey, java.sql.Timestamp validNotBefore, java.sql.Timestamp validNotAfter, String signersCertificateIssuer) throws SQLException {
        jdbc.insertCertificate(new Certificate(signatureID, publicKey, validNotBefore, validNotAfter, signersCertificateIssuer));
        logger.info(String.format("Save certificate."));
    }

    /**
     * Saves OCSP to database
     *
     * @param signatureID specifies signature id
     * @param OCSPResponseTime specifies time when OCSP response was given
     * @param OCSPResponseIssuer specifies issuer who gave response
     * @param OCSPCertificateIssuer specifies authority that gave certificate to OCSP
     */
    public static void saveOCSP(int signatureID, java.sql.Timestamp OCSPResponseTime, String OCSPResponseIssuer, String OCSPCertificateIssuer) throws SQLException {
        jdbc.insertOCSP(new OCSP(signatureID, OCSPResponseTime, OCSPResponseIssuer, OCSPCertificateIssuer));
        logger.info(String.format("Save OCSP."));
    }

    /**
     * Saves OCSP to database
     *
     * @param signatureID specifies signature id
     * @param timestampCreationTime specifies time when timestamp was created
     * @param timestampIssuer specifies issuer of the timestamp
     * @param timestampCertificateIssuer specifies authority that gave certificate to timestamp issuer
     */
    public static void saveTimestamp(int signatureID, java.sql.Timestamp timestampCreationTime, String timestampIssuer, String timestampCertificateIssuer) throws SQLException {
        jdbc.insertTimestamp(new Timestamp(signatureID, timestampCreationTime, timestampIssuer, timestampCertificateIssuer));
        logger.info(String.format("Save timestamp."));
    }
}
