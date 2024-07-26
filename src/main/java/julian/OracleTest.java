package julian;

import java.security.Security;
import java.sql.Driver;
import java.util.Properties;

public class OracleTest {
  public void execute() {
    // Prioritize our custom provider over the default SunJSSE TLS provider
    Security.insertProviderAt(new CustomProvider(), 1);
    try {
      Driver d = (Driver) Class.forName("oracle.jdbc.driver.OracleDriver").getDeclaredConstructor().newInstance();
      String connectionUrl = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcps)(HOST=mousumi-oracle.c5bl3ymevjvd.us-east-1.rds.amazonaws.com)(PORT=2484))(CONNECT_DATA=(SERVICE_NAME=ORCL)))";
      Properties properties = new Properties();
      properties.setProperty("user", "admin");
      properties.setProperty("password", "utf8mb3utf8mb3");
      d.connect(connectionUrl, properties);
      System.out.println("Connection successful");
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
