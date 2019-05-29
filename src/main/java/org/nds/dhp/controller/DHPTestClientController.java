package org.nds.dhp.controller;

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.rest.client.api.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.nds.dhp.factory.BundleFactory;
import org.nds.dhp.service.AccessTokenService;
import org.nds.dhp.service.HttpClientService;
import org.nds.dhp.model.DHPTestClient;
import org.nds.dhp.util.MessageParser;
import org.nds.dhp.util.ServerProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class DHPTestClientController {

    private static Logger logger = LoggerFactory.getLogger(AccessTokenService.class);
    private static String data = "<Bundle xmlns=\"http://hl7.org/fhir\"><id value=\"83e3f864-718e-11e9-9071-525400fdb384\"></id><type value=\"message\"></type><entry><fullUrl value=\"urn:uuid:83e41826-718e-11e9-9071-525400fdb384\"></fullUrl><resource><MessageHeader><id value=\"83e41826-718e-11e9-9071-525400fdb384\"></id><timestamp value=\"2019-05-01T09:14:05Z\"></timestamp><event><system value=\"https://digitalhealthplatform.scot/fhir/messagetypes\"></system><code value=\"DvaNotif\"></code></event><source><name value=\"Hub\"></name><endpoint value=\"http://hub/\"></endpoint></source><destination><name value=\"DHP\"></name><endpoint value=\"http://dhp/\"></endpoint></destination><data><reference value=\"urn:uuid:83e429c4-718e-11e9-9071-525400fdb384\"></reference></data><data><reference value=\"urn:uuid:83e4212c-718e-11e9-9071-525400fdb384\"></reference></data><data><reference value=\"urn:uuid:83e4311c-718e-11e9-9071-525400fdb384\"></reference></data></MessageHeader></resource></entry><entry><fullUrl value=\"urn:uuid:83e4212c-718e-11e9-9071-525400fdb384\"></fullUrl><resource><Appointment><id value=\"83e4212c-718e-11e9-9071-525400fdb384\"></id><meta><profile value=\"https://digitalhealthplatform.scot/fhir/DhpAppointment\"></profile></meta><contained><Practitioner><id value=\"prac\"></id><identifier><system value=\"https://digitalhealthplatform.scot/fhir/coresystems/ggctrakconsultingdoctor\"></system><value value=\"NUR998\"></value></identifier><name><text value=\"Nurse Gillian McCormick\"></text></name></Practitioner></contained><identifier><system value=\"https://digitalhealthplatform.scot/fhir/coresystems/ggctrakuniqueapptid\"></system><value value=\"27165-19509-1\"></value></identifier><identifier><system value=\"https://digitalhealthplatform.scot/fhir/coresystems/ggctrakcontrolId\"></system><value value=\"5627615414988\"></value></identifier><identifier><system value=\"https://digitalhealthplatform.scot/fhir/coresystems/ggctrakvisitnumberId\"></system><value value=\"O0015512368\"></value></identifier><status value=\"booked\"></status><type><text value=\"Dermatology Virtual\"></text></type><description value=\"GRGMDEV9-NURSE MCCORMICK DERM VIRTUAL ONLINE FRI AM\"></description><start value=\"2019-06-14T08:00:00Z\"></start><participant><actor><reference value=\"urn:uuid:83e429c4-718e-11e9-9071-525400fdb384\"></reference></actor><status value=\"accepted\"></status></participant><participant><actor><reference value=\"#prac\"></reference></actor><status value=\"accepted\"></status></participant></Appointment></resource></entry><entry><fullUrl value=\"urn:uuid:83e429c4-718e-11e9-9071-525400fdb384\"></fullUrl><resource><Patient><id value=\"83e429c4-718e-11e9-9071-525400fdb384\"></id><identifier><system value=\"https://phfapi.digitalhealthplatform.net/fhir/chinumber\"></system><value value=\"0109560000\"></value></identifier><name><family value=\"Hscpportal\"></family><given value=\"Test Two\"></given></name><telecom><system value=\"email\"></system><value value=\"HSCPortalTest2@gmail.com\"></value><use value=\"home\"></use></telecom><birthDate value=\"1956-09-01\"></birthDate><careProvider><reference value=\"urn:uuid:83e4311c-718e-11e9-9071-525400fdb384\"></reference></careProvider></Patient></resource></entry><entry><fullUrl value=\"urn:uuid:83e4311c-718e-11e9-9071-525400fdb384\"></fullUrl><resource><Organization><id value=\"83e4311c-718e-11e9-9071-525400fdb384\"></id><identifier><system value=\"https://digitalhealthplatform.scot/fhir/GpPracticeCode\"></system><value value=\"54321\"></value></identifier><name value=\"Alba House\"></name></Organization></resource></entry></Bundle>";

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer";
    private static final String CONTENT_TYPE="Content-Type";
    private static final String FHIR_JSON="application/json+fhir";
    private static final String FHIR_XML="application/xml+fhir";

    private AccessTokenService accessTokenService;
    private HttpClientService httpClientService;
    private ExecutorService executor;

    private DHPTestClient dhpTestClient = new DHPTestClient();

    private int currentMessageType=1;
    private int currentMessageFormat=1;
    private String currentUUID;

    public DHPTestClientController() {
        accessTokenService = new AccessTokenService();
        httpClientService = new HttpClientService();

        executor = Executors.newSingleThreadExecutor();
    }

    @GetMapping("/dhptestclient")
    public String handleGetRequest(Model model) {
        dhpTestClient.setMessageType(currentMessageType);
        dhpTestClient.setMessageFormat(currentMessageFormat);

        dhpTestClient.setEndPoint(ServerProperty.getMessageEndpoint());
        model.addAttribute("dhptestclient", dhpTestClient);
        return "DHPTestClient";
    }

    @PostMapping("/dhptestclient")
    public String handlePostRequest(@ModelAttribute("dhptestclient") DHPTestClient dhpTestClient, Model model) {

        currentMessageType = dhpTestClient.getMessageType();
        currentMessageFormat = dhpTestClient.getMessageFormat();
        postRequest(dhpTestClient.getMessageType(), dhpTestClient.getMessageFormat());

        return "redirect:/dhptestclient";
    }

    private void postRequest(int messageType, int messageFormat) {
        String token;
        HttpPost httpPost;
        HttpResponse httpResponse;

        try {

            token = accessTokenService.request();

            httpPost = httpClientService.createHttpPost(ServerProperty.getMessageEndpoint(), createHeaders(token, messageFormat), new StringEntity(createBundleRequest(messageType, messageFormat)));

            displayRequest(httpPost);

            httpResponse = httpClientService.post(httpPost);

            displayResponse(httpResponse);

        } catch(Exception exception) {
            logger.error("Unable to process post request");
        }

    }

    private String createBundleRequest(int messageType, int messageFormat) throws Exception {
        Bundle bundle=null;
        String request=null;

        if(messageType == 1) {
            currentUUID = UUID.randomUUID().toString();
            bundle = BundleFactory.createDvaNotif(currentUUID);
        } else if(messageType == 2) {
            bundle = BundleFactory.createDvaNotifR_Response(currentUUID);
        }

        if(messageFormat == 1) {
            request = MessageParser.formatXML(bundle);
        } else if(messageFormat == 2) {
            request = MessageParser.formatJSON(bundle);
        }

        return request;
    }

    private void displayRequest(HttpPost httpPost) throws Exception {

        dhpTestClient.setRequestHeader(createHeaderRequest(httpPost));
        dhpTestClient.setRequestBody(createBody(httpPost.getEntity()));
    }

    private void displayResponse(HttpResponse httpResponse) throws Exception {

        dhpTestClient.setResponseHeader(createHeaderRequest(httpResponse));
        dhpTestClient.setResponseBody(createBody(httpResponse.getEntity()));
    }

    private String createHeaderRequest(HttpMessage httpMessage) {
        StringBuilder stringBuilder;

        stringBuilder = new StringBuilder();

        for(org.apache.http.Header postHeader : httpMessage.getAllHeaders()) {
            stringBuilder.append(postHeader.getName() + " " + postHeader.getValue() + "\n");
        }

        return stringBuilder.toString();
    }

    private String createBody(HttpEntity httpEntity) throws Exception {

        return EntityUtils.toString(httpEntity, "UTF-8");
    }

    private static List<Header> createHeaders(String token, int messageFormat) {
        List<Header> headers;

        headers = new ArrayList<>();

        if(messageFormat == 1) {
            headers.add(new Header(CONTENT_TYPE, FHIR_XML));
        } else if(messageFormat == 2) {
            headers.add(new Header(CONTENT_TYPE, FHIR_JSON));
        }

        headers.add(new Header(AUTHORIZATION, BEARER + " " + token));

        return headers;
    }
}
