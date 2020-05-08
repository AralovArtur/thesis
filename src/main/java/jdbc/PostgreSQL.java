package jdbc;

import model.Certificate;
import model.OCSP;
import model.Signer;
import model.Timestamp;

import java.sql.*;

/**
 * JDBC for interaction with database
 *
 * @author Artur Aralov
 */
public class PostgreSQL {

    private final String url = "jdbc:postgresql://localhost:5432/File formats";
    private final String user = "postgres";
    private final String password = "12345";
    private Connection connection;

    /**
     * Connects to a database
     */
    public Connection connect() {
        connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return connection;
    }

    public int insertSigner(Signer signer) throws SQLException {
        String SQL = "insert into signer (serial_number, given_name, surname, eid_tool)" + "values (?, ?, ?, ?)";

        int signatureID = 0;

        PreparedStatement preparedStatement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, signer.getSerialNumber());
        preparedStatement.setString(2, signer.getGivenName());
        preparedStatement.setString(3, signer.getSurname());
        preparedStatement.setString(4, signer.geteIDTool());

        int affectedRows = preparedStatement.executeUpdate();

        if (affectedRows > 0) {
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    signatureID = resultSet.getInt(1);
                }
            } catch (SQLException exception) {
                    System.out.println(exception.getMessage());
            }
        }

        return signatureID;
    }

    public void insertCertificate(Certificate certificate) throws SQLException {
        String SQL = "insert into certificate (signer_id, public_key, valid_not_before, valid_not_after, certificate_issuer)" + "values (?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(SQL);
        preparedStatement.setInt(1, certificate.getSignatureID());
        preparedStatement.setString(2, certificate.getPublicKey());
        preparedStatement.setTimestamp(3, certificate.getValidNotBefore());
        preparedStatement.setTimestamp(4, certificate.getValidNotAfter());
        preparedStatement.setString(5, certificate.getSignersCertificateIssuer());
        preparedStatement.executeUpdate();
    }

    public void insertOCSP(OCSP ocsp) throws SQLException {
        String SQL = "insert into ocsp (signer_id, response_time, response_issuer, certificate_issuer)" + "values (?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(SQL);
        preparedStatement.setInt(1, ocsp.getSignatureID());
        preparedStatement.setTimestamp(2, ocsp.getResponseTime());
        preparedStatement.setString(3, ocsp.getResponseIssuer());
        preparedStatement.setString(4, ocsp.getCertificateIssuer());
        preparedStatement.executeUpdate();
    }

    public void insertTimestamp(Timestamp timestamp) throws SQLException {
        String SQL = "insert into timestamp(signer_id, creation_time, timestamp_issuer, certificate_issuer)" + "values (?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(SQL);
        preparedStatement.setInt(1, timestamp.getSignatureID());
        preparedStatement.setTimestamp(2, timestamp.getCreationTime());
        preparedStatement.setString(3, timestamp.getTimestampIssuer());
        preparedStatement.setString(4, timestamp.getCertificateIssuer());
        preparedStatement.executeUpdate();
    }
}