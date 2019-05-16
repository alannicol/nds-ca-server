package org.nds.dhp.factory;

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import org.junit.Test;
import org.nds.dhp.util.MessageParser;

import static org.junit.Assert.assertNotNull;

public class BundleFactoryTest {

    @Test
    public void createDvaNotif() {
        Bundle bundle;

        bundle = BundleFactory.createDvaNotif();

        System.out.println(MessageParser.format(bundle));

        assertNotNull(bundle);
    }

    @Test
    public void createDvaNotifR_Response() throws Exception {
        Bundle bundle;

        bundle = BundleFactory.createDvaNotifR_Response();

        System.out.println(MessageParser.format(bundle));

        assertNotNull(bundle);
    }
}
