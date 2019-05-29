package org.nds.dhp.factory;

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import org.junit.Test;
import org.nds.dhp.util.MessageParser;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;

public class BundleFactoryTest {

    @Test
    public void createDvaNotif() {
        Bundle bundle;

        bundle = BundleFactory.createDvaNotif(UUID.randomUUID().toString());

        System.out.println(MessageParser.formatJSON(bundle));

        assertNotNull(bundle);
    }

    @Test
    public void createDvaNotifR_Response() throws Exception {
        Bundle bundle;

        bundle = BundleFactory.createDvaNotifR_Response(UUID.randomUUID().toString());

        System.out.println(MessageParser.formatJSON(bundle));

        assertNotNull(bundle);
    }
}
