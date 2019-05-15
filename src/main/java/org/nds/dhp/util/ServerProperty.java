package org.nds.dhp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.util.Properties;

public class ServerProperty {

    private static Logger logger = LoggerFactory.getLogger(ServerProperty.class);

    private static final String SERVER_PROPERTIES="server.properties";
    private static final String MESSAGE_ENDPOINT="message.endpoint";
    private static final String TOKEN_SERVICE_URL="token.service.url";
    private static final String TOKEN_SERVICE_PRIVATE_KEY="token.service.private.key";
    private static final String TOKEN_SERVICE_SECRET="token.service.secret";
    private static final String TOKEN_SERVICE_CLIENT_ID="token.service.client_id";

    private static Properties properties = obtainProperties();

    public static String getMessageEndpoint() { return properties.get(MESSAGE_ENDPOINT).toString();}

    public static String getTokenServiceUrl() { return properties.get(TOKEN_SERVICE_URL).toString(); }

    public static String getTokenServicePrivateKey() { return properties.get(TOKEN_SERVICE_PRIVATE_KEY).toString(); }

    public static String getTokenServiceSecret() { return properties.get(TOKEN_SERVICE_SECRET).toString(); }

    public static String getTokenServiceClientId() { return properties.get(TOKEN_SERVICE_CLIENT_ID).toString(); }


    private static Properties obtainProperties() {
        Properties properties=null;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(ServerProperty.class.getClassLoader().getResourceAsStream(SERVER_PROPERTIES))) {
            properties = new Properties();
            properties.load(bufferedInputStream);
        } catch(Exception exception) {
            logger.error("Cannot obtain properties file", exception);
        }

        return properties;
    }
}
