/*******************************************************************************
 * Copyright (c) 2017, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jca.fat.derbyra;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;

import componenttest.annotation.Server;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.rules.repeater.JakartaEE10Action;
import componenttest.rules.repeater.JakartaEE9Action;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;

@RunWith(FATRunner.class)
public class LoginModuleInStandaloneResourceAdapterTest extends FATServletClient {

    private static final String APP = "derbyRAApp";
    private static final String WAR_NAME = "fvtweb";
    private static final String DerbyRAServlet = "fvtweb/DerbyRAServlet";
    private static final String DerbyRAAnnoServlet = "fvtweb/DerbyRAAnnoServlet";

    @Server("com.ibm.ws.jca.fat.derbyra.loginModuleInRA")
    public static LibertyServer server;

    @BeforeClass
    public static void setUp() throws Exception {

        WebArchive war = ShrinkWrap.create(WebArchive.class, WAR_NAME + ".war");
        war.addPackage("web");
        war.addAsWebInfResource(new File("test-applications/fvtweb/resources/WEB-INF/ibm-web-bnd.xml"));
        war.addAsWebInfResource(new File("test-applications/fvtweb/resources/WEB-INF/web.xml"));

        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, APP + ".ear");
        ear.addAsModule(war);
        ShrinkHelper.addDirectory(ear, "lib/LibertyFATTestFiles/derbyRAApp");
        ShrinkHelper.exportToServer(server, "apps", ear);

        ResourceAdapterArchive rar = ShrinkWrap.create(ResourceAdapterArchive.class, "DerbyLMRA.rar");
        rar.as(JavaArchive.class).addPackage("fat.derbyra.resourceadapter");
        rar.addAsManifestResource(new File("test-resourceadapters/fvt-resourceadapter/resources/META-INF/ra.xml"));
        rar.addAsManifestResource(new File("test-resourceadapters/fvt-resourceadapter/resources/META-INF/permissions.xml"));

        rar.addAsLibrary(new File("publish/shared/resources/derby/derby.jar"));

        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "loginModule.jar");
        jar.addPackage("com.ibm.ws.jca.fat.security.login");
        rar.addAsLibraries(jar);

        ShrinkHelper.exportToServer(server, "connectors", rar);

        server.addEnvVar("PERMISSION", (JakartaEE9Action.isActive()
                                        || JakartaEE10Action.isActive()) ? "jakarta.resource.spi.security.PasswordCredential" : "javax.resource.spi.security.PasswordCredential");
        server.addInstalledAppForValidation(APP);
        server.startServer();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.stopServer("SRVE9967W"); // The manifest class path derbyLocale_cs.jar can not be found in jar file wsjar:file:/C:/Users/IBM_ADMIN/Documents/workspace/build.image/wlp/usr/servers/com.ibm.ws.jca.fat.derbyra/connectors/DerbyRA.rar!/derby.jar or its parent.
                                        // This may just be because we don't care about including manifest files in our test buckets, if that's the case, we can ignore this.
    }

    @Test
    public void testJCADataSourceResourceRefLoginModuleInRAR() throws Exception {
        FATServletClient.runTest(server, DerbyRAServlet, "testJCADataSourceResourceRefSecurity");
    }

    @Test
    public void testCustomLoginModuleInRARCF() throws Exception {
        FATServletClient.runTest(server, DerbyRAAnnoServlet, "testCustomLoginModuleCF");
    }

}
