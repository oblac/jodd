package jodd.mail;

import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SmtpServerTest {

    @Test
    public void testAddsPropertyToServerSession() {
        Properties overridenProperties = new Properties();

        overridenProperties.setProperty("mail.smtp.from", "bounce@jodd.org");

        SmtpServer smtpServer = SmtpServer.newSmtpServer()
                .authenticateWith()
                .usernameAndPassword("test", "password")
                .host("some.host.com")
                .port(587)
                .timeout(10)
                .properties(overridenProperties)
                .build();

        Properties sessionProperties = smtpServer.createSession().mailSession.getProperties();

        assertThat(sessionProperties.getProperty("mail.smtp.from"), is("bounce@jodd.org"));
    }
}