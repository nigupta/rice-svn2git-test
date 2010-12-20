/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.web.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.ui.container.View;
import org.kuali.rice.kns.ui.service.ViewService;
import org.kuali.rice.kns.web.spring.form.UITestForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the Test UI Page
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
public class UITestController {
	private ViewService viewService;

	@RequestMapping(value="/uitest", params="methodToCall=start")
	public ModelAndView start(@ModelAttribute("KualiForm") UITestForm uiTestForm,
			BindingResult result, HttpServletRequest request, HttpServletResponse response) {		
		
		String viewId = "testView1";
		View view = getViewService().getViewById(viewId);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("KualiForm", uiTestForm);
		modelAndView.addObject("View", view);
		
		modelAndView.setViewName("View");
		
        return modelAndView;
	}

	protected ViewService getViewService() {
		if (viewService == null) {
			viewService = KNSServiceLocator.getViewService();
		}
		
		return this.viewService;
	}

	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}
	
}
