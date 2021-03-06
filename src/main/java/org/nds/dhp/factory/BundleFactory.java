package org.nds.dhp.factory;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.*;
import ca.uhn.fhir.model.dstu2.valueset.*;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class BundleFactory {

    private static final String FULL_URL="urn:uuid:";
    private static final String DVA_NOTIF_RESPONSE_RESOURCE="/application.pdf";

    private static final String LINE_1="19 Patient Portal,Glasgow,,Unknown";
    private static final String LINE_2="Nk010aa";
    private static final String UNKNOWN="Unknown";
    private static final String POSTAL_CODE="NK010AA";

    public static Bundle createDvaNotif(String uuid) {
        Bundle bundle;
        Bundle.Entry organization;
        Bundle.Entry patient;
        Bundle.Entry appointment;
        Bundle.Entry messageHeader;

        bundle = createBundle();
        organization = createOrganization("99999", "Alba House");
        patient = createPatient(organization,"0109560000","HSCPortal","Testtwo","HSCPortalTest2@gmail.com");
        appointment = createAppointment(patient,"Dermatology Virtual","A Dermatology virtual appointment has been scheduled for you.");
        messageHeader = createMessageHeader(uuid,appointment, patient, organization, "DvaNotif", "NSS HUB", "DHP");

        bundle.addEntry(messageHeader);
        bundle.addEntry(appointment);
        bundle.addEntry(patient);
        bundle.addEntry(organization);

        return bundle;
    }

    public static Bundle createDvaNotifR_Response(String uuid) throws Exception {
        Bundle bundle;
        Bundle.Entry documentReference;
        Bundle.Entry messageHeader;

        bundle = createBundle();
        documentReference = createDocumentReference("Consultant Report", obtainPdfData(), "Consultant Report");
        messageHeader = createMessageHeader(uuid,documentReference, "DvaNotifR_Response", "Lenus", "DHP");

        bundle.addEntry(messageHeader);
        bundle.addEntry(documentReference);

        return bundle;
    }

    private static Bundle createBundle() {
        Bundle bundle;

        bundle = new Bundle();
        bundle.setId(UUID.randomUUID().toString());
        bundle.setType(BundleTypeEnum.MESSAGE);

        return bundle;
    }

    private static Bundle.Entry createPatient(Bundle.Entry organization, String patientIdentifier, String familyName, String givenName, String email) {
        Patient patient;

        patient = new Patient();
        patient.setId(new IdDt(UUID.randomUUID().toString()));
        patient.addIdentifier(createPatientIdentifier("https://phfapi.digitalhealthplatform.net/fhir/chinumber", patientIdentifier));
        patient.addName(createName(familyName,givenName));
        patient.addAddress(createAddress());
        patient.setTelecom(createEmail(email));
        patient.addCareProvider().setReference(organization.getFullUrl());

        return new Bundle.Entry().setFullUrl(createFullUrl(patient)).setResource(patient);
    }

    private static IdentifierDt createPatientIdentifier(String system, String value) {
        IdentifierDt identifierDt;

        identifierDt = new IdentifierDt();
        identifierDt.setSystem(system);
        identifierDt.setValue(value);

        return identifierDt;
    }

    private static HumanNameDt createName(String family, String given) {
        HumanNameDt humanNameDt;

        humanNameDt = new HumanNameDt();
        humanNameDt.addFamily(new StringDt(family));
        humanNameDt.addGiven(new StringDt(given));

        return humanNameDt;
    }

    private static AddressDt createAddress() {
        AddressDt addressDt;

        addressDt = new AddressDt();
        addressDt.setUse(AddressUseEnum.HOME);
        addressDt.setType(AddressTypeEnum.POSTAL);
        addressDt.addLine(LINE_1);
        addressDt.addLine(LINE_2);
        addressDt.setCity(UNKNOWN);
        addressDt.setPostalCode(POSTAL_CODE);

        return addressDt;
    }

    private static List<ContactPointDt> createEmail(String email) {
        List<ContactPointDt> contactPoints;
        ContactPointDt contactPointDt;

        contactPoints = new ArrayList<>();
        contactPointDt = new ContactPointDt();
        contactPointDt.setSystem(ContactPointSystemEnum.EMAIL);
        contactPointDt.setValue(email);
        contactPointDt.setUse(ContactPointUseEnum.HOME);

        contactPoints.add(contactPointDt);

        return contactPoints;
    }

    private static Bundle.Entry createAppointment(Bundle.Entry patient, String type, String description) {
        Appointment appointment;

        appointment = new Appointment();
        appointment.setId(new IdDt(UUID.randomUUID().toString()));
        appointment.setStatus(AppointmentStatusEnum.BOOKED);
        appointment.setType(createType(type));
        appointment.setDescription(description);
        appointment.setStart(new Date(), TemporalPrecisionEnum.SECOND);
        appointment.setEnd(DateUtils.addHours(new Date(), 1), TemporalPrecisionEnum.SECOND);
        appointment.addParticipant(createParticipant(patient));

        return new Bundle.Entry().setFullUrl(createFullUrl(appointment)).setResource(appointment);
    }

    private static CodeableConceptDt createType(String text) {
        CodeableConceptDt codeableConceptDt;

        codeableConceptDt = new CodeableConceptDt();
        codeableConceptDt.setText(text);

        return codeableConceptDt;
    }

    private static Appointment.Participant createParticipant(Bundle.Entry patient) {
        Appointment.Participant participant;

        participant = new Appointment.Participant();
        participant.setActor(new ResourceReferenceDt(patient.getFullUrl()));

        return participant;
    }

    private static Bundle.Entry createOrganization(String organisationId, String organisationName) {
        Organization organization;

        organization = new Organization();
        organization.setId(new IdDt(UUID.randomUUID().toString()));
        organization.addIdentifier(createOrganizationIdentifier("http://fhir.scot.nhs.uk/DSTU2/Id/national/organisationId", organisationId));
        organization.setName(organisationName);

        return new Bundle.Entry().setFullUrl(createFullUrl(organization)).setResource(organization);
    }

    private static IdentifierDt createOrganizationIdentifier(String system, String value) {
        IdentifierDt identifierDt;

        identifierDt = new IdentifierDt();
        identifierDt.setSystem(system);
        identifierDt.setValue(value);

        return identifierDt;
    }

    private static MessageHeader createMessageHeader(String uuid, String event, String source, String destination) {
        MessageHeader messageHeader;

        messageHeader = new MessageHeader();
        messageHeader.setId(uuid);
        messageHeader.setTimestamp(new Date(), TemporalPrecisionEnum.SECOND);
        messageHeader.setEvent(createEvent("https://digitalhealthplatform.scot/fhir/messagetypes", event));
        messageHeader.setSource(createSource(source));
        messageHeader.addDestination(createDestination(destination));

        return messageHeader;
    }

    private static Bundle.Entry createMessageHeader(String uuid, Bundle.Entry appointment, Bundle.Entry patient, Bundle.Entry organization, String event, String source, String
            destination) {
        MessageHeader messageHeader;

        messageHeader = createMessageHeader(uuid, event, source, destination);

        messageHeader.addData().setReference(appointment.getFullUrl());
        messageHeader.addData().setReference(patient.getFullUrl());
        messageHeader.addData().setReference(organization.getFullUrl());

        return new Bundle.Entry().setFullUrl(createFullUrl(messageHeader)).setResource(messageHeader);
    }

    private static Bundle.Entry createMessageHeader(String uuid, Bundle.Entry documentReference, String event, String source, String destination) {
        MessageHeader messageHeader;

        messageHeader = createMessageHeader(new IdDt(UUID.randomUUID().toString()).getValue(), event, source, destination);
        messageHeader.setResponse(createResponse(uuid));

        messageHeader.addData().setReference(documentReference.getFullUrl());

        return new Bundle.Entry().setFullUrl(createFullUrl(messageHeader)).setResource(messageHeader);
    }

    private static MessageHeader.Response createResponse(String uuid) {
        MessageHeader.Response response;

        response = new MessageHeader.Response();
        response.setIdentifier(uuid);
        response.setCode(ResponseTypeEnum.OK);

        return response;
    }

    private static CodingDt createEvent(String system, String code) {
        CodingDt codingDt;

        codingDt = new CodingDt();
        codingDt.setSystem(system);
        codingDt.setCode(code);

        return codingDt;
    }

    private static MessageHeader.Source createSource(String name) {
        MessageHeader.Source source;

        source = new MessageHeader.Source();
        source.setName(name);

        return source;
    }

    private static MessageHeader.Destination createDestination(String name) {
        MessageHeader.Destination destination;

        destination = new MessageHeader.Destination();
        destination.setName(name);

        return destination;
    }

    private static Bundle.Entry createDocumentReference(String description, byte[] data, String title) {
        DocumentReference documentReference;

        documentReference = new DocumentReference();
        documentReference.setId(new IdDt(UUID.randomUUID().toString()));
        documentReference.setCreated(new Date(), TemporalPrecisionEnum.SECOND);
        documentReference.setIndexed(new Date(), TemporalPrecisionEnum.SECOND);
        documentReference.setStatus(DocumentReferenceStatusEnum.CURRENT);
        documentReference.setDescription(description);
        documentReference.addContent(createContent(data, title));

        return new Bundle.Entry().setFullUrl(createFullUrl(documentReference)).setResource(documentReference);
    }

    private static DocumentReference.Content createContent(byte[] data, String title) {
        DocumentReference.Content content;

        content = new DocumentReference.Content();
        content.setAttachment(createAttachment(data, title));

        return content;
    }

    private static AttachmentDt createAttachment(byte[] data, String title) {
        AttachmentDt attachmentDt;

        attachmentDt = new AttachmentDt();
        attachmentDt.setContentType("application/pdf");
        attachmentDt.setData(data);
        attachmentDt.setTitle(title);

        return attachmentDt;
    }

    private static byte[] obtainPdfData() throws Exception {
        File file;
        byte[] data;

        file = new File(BundleFactory.class.getResource(DVA_NOTIF_RESPONSE_RESOURCE).toURI());
        data = Files.readAllBytes(file.toPath());

        return Base64.getEncoder().encode(data);
    }

    private static String createFullUrl(IResource resource) {
        return FULL_URL + resource.getId().getValue();
    }
}
