/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.tests.enterprise.lifecycle;

import static org.jboss.arquillian.api.RunModeType.AS_CLIENT;
import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletResponse;

import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.Run;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.test.Utils;
import org.jboss.weld.tests.category.Integration;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Sections
 * 
 * 6.5. Lifecycle of stateful session beans 6.6. Lifecycle of stateless session
 * and singleton beans 6.11. Lifecycle of EJBs
 * 
 * Mostly overlapping with other tests...
 * 
 * @author Nicklas Karlsson
 * @author David Allen
 * 
 *         Spec version: Public Release Draft 2
 * 
 */
@Category(Integration.class)
@RunWith(Arquillian.class)
@Run(AS_CLIENT)
public class EnterpriseBeanLifecycleRemoteTest
{
   @Deployment
   public static Archive<?> deploy()
   {
      EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "test.ear");
      ear.addModule(ShrinkWrap.create(WebArchive.class, "test.war")
            .addClass(RemoteClient.class)
            .addWebResource(EmptyAsset.INSTANCE, "beans.xml")
      );
      ear.addModule(ShrinkWrap.create(BeanArchive.class, "test.jar")
            .addClasses(KleinStadt.class, Kassel.class, GrossStadt.class, FrankfurtAmMain.class, SchoeneStadt.class)
            .addClasses(Utils.class, Assert.class, Description.class, SelfDescribing.class, ComparisonFailure.class)
            .addManifestResource(EmptyAsset.INSTANCE, "beans.xml")
      );
      return ear;
   }

   @Test
   public void testDestroyRemovesSFSB() throws Exception
   {
      WebClient client = new WebClient();
      Page page = client.getPage(getPath("request1"));
      assertEquals(page.getWebResponse().getStatusCode(), HttpServletResponse.SC_OK);
      page = client.getPage(getPath("request2"));
      assertEquals(page.getWebResponse().getStatusCode(), HttpServletResponse.SC_OK);
   }
   
   protected String getPath(String viewId)
   {
      // TODO: this should be moved out and be handled by Arquillian
      return "http://localhost:8080/test/" + viewId;
   }

}