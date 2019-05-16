package org.nds.dhp.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public class MessageParser {

    private static Logger logger = LoggerFactory.getLogger(MessageParser.class);

    private static FhirContext fhirContext = FhirContext.forDstu2();

    public static Bundle parseFile(String fileName) {
        Bundle bundle;

        bundle = parseFile(new File(fileName));

        return bundle;
    }

    public static Bundle parse(String data) {
        IParser parser;
        Bundle bundle;

        parser = fhirContext.newJsonParser();
        bundle = parser.parseResource(Bundle.class, data);

        return bundle;
    }

    private static Bundle parseFile(File file) {
        IParser parser;
        Reader reader;
        Bundle bundle=null;

        try {
            parser = fhirContext.newJsonParser();
            reader = new FileReader(file);
            bundle = parser.parseResource(Bundle.class, reader);
        } catch(Exception exception) {
            logger.error("Unable to parseFile file", exception);
        }

        return bundle;
    }

    public static String formatJSON(IBaseResource resource) {
        return fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(resource);
    }

    public static String formatXML(IBaseResource resource) {
        return fhirContext.newXmlParser().setPrettyPrint(true).encodeResourceToString(resource);
    }
}
