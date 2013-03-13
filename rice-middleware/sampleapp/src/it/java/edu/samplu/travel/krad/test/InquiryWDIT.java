/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package edu.samplu.travel.krad.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * tests the inquiry feature in rice.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InquiryWDIT extends WebDriverLegacyITBase {
    public static final String TEST_URL = ITUtil.PORTAL
            + "?channelTitle=Travel%20Account%20Lookup&channelUrl="
            + ITUtil.getBaseUrlString()
            + "/kr-krad/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&returnLocation="
            + ITUtil.PORTAL_URL + "&hideReturnLink=true&showMaintenanceLinks=true";

    @Override
    public String getTestUrl() {
        return TEST_URL;
    }

    @Test
    public void testInquiry() throws Exception {
      super.testInquiry();

    }
}
