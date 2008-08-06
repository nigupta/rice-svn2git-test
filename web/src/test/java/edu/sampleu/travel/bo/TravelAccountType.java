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
// Created on Oct 18, 2006

package edu.sampleu.travel.bo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;



@Entity
@Table(name="TRV_ACCT_TYPE")
public class TravelAccountType extends PersistableBusinessObjectBase {
    
    @Id
	@Column(name="acct_type")
	private String accountTypeCode;
    @Column(name="acct_type_name")
	private String name;
    

    public String getAccountTypeCode() {
		return accountTypeCode;
	}


	public void setAccountTypeCode(String accountTypeCode) {
		this.accountTypeCode = accountTypeCode;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getCodeAndDescription() {
		return accountTypeCode + " - " + name;
	}

	@Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap propMap = new LinkedHashMap();
        propMap.put("accountTypeCode", accountTypeCode);
        propMap.put("name", name);
        return propMap;
    }

 
}
