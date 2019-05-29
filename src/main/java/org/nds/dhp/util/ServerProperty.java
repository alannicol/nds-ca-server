package org.nds.dhp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.util.Properties;

public class ServerProperty {

    private static Logger logger = LoggerFactory.getLogger(ServerProperty.class);

    private static final String SERVER_PROPERTIES="server.properties";
    private static final String SECURITY_PROPERTIES="security.properties";
    private static final String MESSAGE_ENDPOINT="message.endpoint";
    private static final String TOKEN_SERVICE_URL="token.service.url";
    private static final String TOKEN_SERVICE_PRIVATE_KEY="token.service.private.key";
    private static final String TOKEN_SERVICE_SECRET="token.service.secret";
    private static final String TOKEN_SERVICE_CLIENT_ID="token.service.client_id";

    private static Properties serverProperties = obtainProperties(SERVER_PROPERTIES);
    private static Properties securityProperties = obtainProperties(SECURITY_PROPERTIES);

    public static String getMessageEndpoint() { return serverProperties.get(MESSAGE_ENDPOINT).toString();}

    public static String getTokenServiceUrl() { return serverProperties.get(TOKEN_SERVICE_URL).toString(); }

    public static String getTokenServicePrivateKey() { return securityProperties.get(TOKEN_SERVICE_PRIVATE_KEY).toString(); }

    public static String getTokenServiceSecret() { return securityProperties.get(TOKEN_SERVICE_SECRET).toString(); }

    public static String getTokenServiceClientId() { return serverProperties.get(TOKEN_SERVICE_CLIENT_ID).toString(); }


    private static Properties obtainProperties(String propertiesFile) {
        Properties properties=null;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(ServerProperty.class.getClassLoader().getResourceAsStream(propertiesFile))) {
            properties = new Properties();
            properties.load(bufferedInputStream);
        } catch(Exception exception) {
            logger.error("Cannot obtain serverProperties file", exception);
        }

        return properties;
    }
}
