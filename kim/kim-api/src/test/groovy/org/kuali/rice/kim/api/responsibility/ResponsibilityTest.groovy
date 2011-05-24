/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.api.responsibility

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import org.junit.Assert
import org.junit.Test
import org.kuali.rice.core.api.mo.common.Attributes
import org.kuali.rice.kim.api.common.attribute.KimAttribute
import org.kuali.rice.kim.api.type.KimType

class ResponsibilityTest {

	private static final String OBJECT_ID = UUID.randomUUID()
	private static final Long VERSION_NUMBER = new Long(1)
	private static final boolean ACTIVE = "true"
	
	private static final String ID = "50"
	private static final String NAMESPACE_CODE = "KUALI"
	private static final String NAME = "PermissionName"
	private static final String DESCRIPTION = "Some Permission Description"
	private static final String TEMPLATE_ID = "7317791873"

	private static final String ATTRIBUTES_1_ID = "1"
	private static final String ATTRIBUTES_1_PERMISSION_ID = "50"
	private static final String ATTRIBUTES_1_VALUE = "Some Attribute Value 1"
	private static final Long ATTRIBUTES_1_VER_NBR = new Long(1)
	private static final String ATTRIBUTES_1_OBJ_ID = UUID.randomUUID()
	
	private static final KimType KIM_TYPE_1
	private static final String KIM_TYPE_1_ID = "1"
	private static final String KIM_TYPE_1_OBJ_ID = UUID.randomUUID()
	static {
		KimType.Builder builder = KimType.Builder.create()
		builder.setId(KIM_TYPE_1_ID)
		builder.setNamespaceCode(NAMESPACE_CODE)
		builder.setActive(ACTIVE)
		builder.setVersionNumber(VERSION_NUMBER)
		builder.setObjectId(KIM_TYPE_1_OBJ_ID)
		KIM_TYPE_1 = builder.build()
	}
		
	private static final KimAttribute KIM_ATTRIBUTE_1
	private static final String KIM_ATTRIBUTE_1_ID = "1"
	private static final String KIM_ATTRIBUTE_1_COMPONENT_NAME = "the_component1"
	private static final String KIM_ATTRIBUTE_1_NAME = "the_attribute1"
	static {
		KimAttribute.Builder builder = KimAttribute.Builder.create(KIM_ATTRIBUTE_1_COMPONENT_NAME, KIM_ATTRIBUTE_1_NAME, NAMESPACE_CODE)
		builder.setId(KIM_ATTRIBUTE_1_ID)
		KIM_ATTRIBUTE_1 = builder.build()
	}
	
	private static final String XML = """
        <ns2:responsibility xmlns:ns2="http://rice.kuali.org/kim/v2_0" xmlns="http://rice.kuali.org/core/v2_0">
          <ns2:id>${ID}</ns2:id>
          <ns2:namespaceCode>${NAMESPACE_CODE}</ns2:namespaceCode>
          <ns2:name>${NAME}</ns2:name>
          <ns2:description>${DESCRIPTION}</ns2:description>
          <ns2:templateId>${TEMPLATE_ID}</ns2:templateId>
          <ns2:active>${ACTIVE}</ns2:active>
          <ns2:attributes>
            <item xmlns="" xmlns:ns3="http://rice.kuali.org/core/v2_0">
              <ns3:key>${KIM_ATTRIBUTE_1_NAME}</ns3:key>
              <ns3:value>${ATTRIBUTES_1_VALUE}</ns3:value>
            </item>
          </ns2:attributes>
          <ns2:versionNumber>${VERSION_NUMBER}</ns2:versionNumber>
          <ns2:objectId>${OBJECT_ID}</ns2:objectId>
        </ns2:responsibility>
		"""
	
    @Test
    void happy_path() {
        Responsibility.Builder.create(ID, NAMESPACE_CODE, NAME, TEMPLATE_ID)
    }

	@Test(expected = IllegalArgumentException.class)
	void test_Builder_fail_ver_num_null() {
		Responsibility.Builder.create(ID, NAMESPACE_CODE, NAME, TEMPLATE_ID).setVersionNumber(null);
	}

	@Test(expected = IllegalArgumentException.class)
	void test_Builder_fail_ver_num_less_than_1() {
		Responsibility.Builder.create(ID, NAMESPACE_CODE, NAME, TEMPLATE_ID).setVersionNumber(-1);
	}
	
	@Test
	void test_copy() {
		def o1b = Responsibility.Builder.create(ID, NAMESPACE_CODE, NAME, TEMPLATE_ID)
		o1b.description = DESCRIPTION

		def o1 = o1b.build()

		def o2 = Responsibility.Builder.create(o1).build()

		Assert.assertEquals(o1, o2)
	}
	
	@Test
	public void test_Xml_Marshal_Unmarshal() {
	  JAXBContext jc = JAXBContext.newInstance(Responsibility.class)
	  Marshaller marshaller = jc.createMarshaller()
	  StringWriter sw = new StringWriter()

	  Responsibility responsibility = this.create()
	  marshaller.marshal(responsibility,sw)
	  String xml = sw.toString()
      println(responsibility)
      println(xml)
	  Unmarshaller unmarshaller = jc.createUnmarshaller();
	  Object actual = unmarshaller.unmarshal(new StringReader(xml))
	  Object expected = unmarshaller.unmarshal(new StringReader(XML))
	  Assert.assertEquals(expected,actual)
	}
	
	private create() {
		Responsibility responsibility = Responsibility.Builder.create(new ResponsibilityContract() {
			String getId() {ResponsibilityTest.ID}
			String getNamespaceCode() {ResponsibilityTest.NAMESPACE_CODE}
			String getName() {ResponsibilityTest.NAME}
			String getDescription() {ResponsibilityTest.DESCRIPTION}
			String getTemplateId() {ResponsibilityTest.TEMPLATE_ID}
			Attributes getAttributes() { Attributes.fromStrings(ResponsibilityTest.KIM_ATTRIBUTE_1_NAME, ResponsibilityTest.ATTRIBUTES_1_VALUE)}
			boolean isActive() {ResponsibilityTest.ACTIVE.toBoolean()}
			Long getVersionNumber() {ResponsibilityTest.VERSION_NUMBER}
			String getObjectId() {ResponsibilityTest.OBJECT_ID}
		}).build()
		
		return responsibility
	}
}
