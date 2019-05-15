package org.nds.dhp.manager;

import ca.uhn.fhir.rest.client.api.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.nds.dhp.util.ServerProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AccessTokenManager {

    private static Logger logger = LoggerFactory.getLogger(AccessTokenManager.class);

    private static final String ACCESS_TOKEN = "access_token";
    private static final String ACCESS_TOKEN_REQUEST_BODY = "client_id=%1$s&client_secret=%2$s";
    private static final String HEADER_NAME="Content-Type";
    private static final String HEADER_VALUE="application/x-www-form-urlencoded";

    public String request() throws Exception {
        String accessToken=null;
        String response;

        try {

            logger.info("Request Token");

            response = requestAccessToken();

            if(response!=null) {

                accessToken = obtainToken(response);
                logger.info("Obtained Token {}", accessToken);
            }
        } catch(Exception exception) {
            logger.error("Unable to obtain token", exception);

            throw exception;
        }

        return accessToken;
    }

    public boolean validate(String token) throws Exception{

        try{
            logger.info("Validate token {}", token);

            String keyString = ServerProperty.getTokenServicePrivateKey() + Base64.getEncoder().encodeToString(ServerProperty.getTokenServiceSecret().getBytes());

            Key key = new SecretKeySpec(keyString.getBytes(), SignatureAlgorithm.HS256.getJcaName());

            Jwts.parser().setSigningKey(key).setAllowedClockSkewSeconds(15).parseClaimsJws(token);

            logger.info("Validated token {}", token);

        } catch(Exception exception) {
            logger.error("Unable to validate token", exception);
            throw exception;
        }

        return true;
    }

    private static String requestAccessToken() throws Exception {
        HttpClientManager httpClientManager;
        HttpResponse httpResponse;
        String response;

        httpClientManager = new HttpClientManager();

        httpResponse = httpClientManager.post(ServerProperty.getTokenServiceUrl(), createHeaders(), new StringEntity(createAccessTokenRequestBody()));

        response = EntityUtils.toString(httpResponse.getEntity());

        return response;
    }

    private static List<Header> createHeaders() {
        List<Header> headers;

        headers = new ArrayList<>();
        headers.add(new Header(HEADER_NAME, HEADER_VALUE));

        return headers;
    }

    private static String createAccessTokenRequestBody() {
        String requestBody;

        requestBody = String.format(ACCESS_TOKEN_REQUEST_BODY, ServerProperty.getTokenServiceClientId(), ServerProperty.getTokenServiceSecret());

        return requestBody;
    }

    private static String obtainToken(String message) {
        JSONObject json;

        json = new JSONObject(message);

        return json.getString(ACCESS_TOKEN);
    }
}
