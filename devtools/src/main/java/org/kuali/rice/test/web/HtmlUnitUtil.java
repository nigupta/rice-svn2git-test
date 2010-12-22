/*
 * Copyright 2007-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.test.web;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import org.kuali.rice.core.config.ConfigContext;

import java.net.URL;

public final class HtmlUnitUtil {

    public static final String BASE_URL = "http://localhost:" + getPort() + "/knstest";
    
	private HtmlUnitUtil() {
		throw new UnsupportedOperationException("do not call");
	}
    
    public static HtmlPage gotoPageAndLogin(WebClient webClient, String url) throws Exception {
        HtmlPage loginPage = (HtmlPage)webClient.getPage(new URL(url));
        HtmlForm htmlForm = (HtmlForm)loginPage.getForms().get(0);
        htmlForm.getInputByName("__login_user").setValueAttribute("quickstart");
        HtmlSubmitInput button = (HtmlSubmitInput)htmlForm.getInputByValue("Login");
        return (HtmlPage)button.click();
    }
    
    public static boolean pageContainsText(HtmlPage page, String text) {
        return page.asText().contains(text);
    }
    
    public static Integer getPort() {
	return new Integer(ConfigContext.getCurrentContextConfig().getProperty("kns.test.port"));
    }
}
