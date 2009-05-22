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
package org.kuali.rice.kim.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.GroupDocumentMember;
import org.kuali.rice.kim.bo.ui.GroupDocumentQualifier;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.TypedArrayList;


/**
 * This is a description of what this class does - bhargavp don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementGroupDocument extends IdentityManagementTypeAttributeTransactionalDocument {
	private static final Logger LOG = Logger.getLogger(IdentityManagementGroupDocument.class);
	
	private static final long serialVersionUID = 1L;
	
	// principal data
	protected String groupId;
	protected String groupTypeId;
	protected String groupTypeName;
	protected String groupNamespace;
	protected String groupName;
	protected boolean active = true;

	protected boolean editing;

	private List<GroupDocumentMember> members = new TypedArrayList(GroupDocumentMember.class);
	private List<GroupDocumentQualifier> qualifiers = new TypedArrayList(GroupDocumentQualifier.class);

	public IdentityManagementGroupDocument() {
	}
	
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setRoleId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @param members the members to set
	 */
	public void addMember(GroupDocumentMember member) {
       	getMembers().add(member);
	}

	/**
	 * @return the kimType
	 */
	public KimTypeImpl getKimType() {
		if ( kimType == null || !StringUtils.equals(kimType.getKimTypeId(), getGroupTypeId() ) ) {
	        Map<String, String> criteria = new HashMap<String, String>();
	        criteria.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID, getGroupTypeId());
	        kimType = (KimTypeImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, criteria);
		}
		return kimType;
	}
	
	/**
	 * @param members the members to set
	 */
	public GroupDocumentMember getBlankMember() {
		GroupDocumentMember member = new GroupDocumentMember();
       	return member;
	}

	/**
	 * @see org.kuali.rice.kns.document.DocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
	 */
	@Override
	public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
		super.doRouteStatusChange(statusChangeEvent);
		if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
			KIMServiceLocator.getUiDocumentService().saveGroup(this);
		}
	}

	@Override
	public void prepareForSave(){
		String groupId;
		if(StringUtils.isBlank(getGroupId())){
			SequenceAccessorService sas = getSequenceAccessorService();
			Long nextSeq = sas.getNextAvailableSequenceNumber(
					"KRIM_GRP_ID_S", this.getClass());
			groupId = nextSeq.toString();
			setGroupId(groupId);
		} else{
			groupId = getGroupId();
		}
		if(getMembers()!=null){
			String groupMemberId;
			for(GroupDocumentMember member: getMembers()){
				member.setGroupId(groupId);
				if(StringUtils.isBlank(member.getGroupMemberId())){
					SequenceAccessorService sas = getSequenceAccessorService();
					Long nextSeq = sas.getNextAvailableSequenceNumber(
							"KRIM_GRP_MBR_ID_S", this.getClass());
					groupMemberId = nextSeq.toString();
					member.setGroupMemberId(groupMemberId);
				}
			}
		}
		int index = 0;
		// this needs to be checked - are all qualifiers present?
		for(String key : getDefinitions().keySet()) {
			if ( getQualifiers().size() > index ) {
				GroupDocumentQualifier qualifier = getQualifiers().get(index);
				qualifier.setKimAttrDefnId(getKimAttributeDefnId(getDefinitions().get(key)));
				qualifier.setKimTypId(getKimType().getKimTypeId());
				qualifier.setGroupId(groupId);
			}
			index++;
        }
	}

	public void initializeDocumentForNewGroup() {
		if(StringUtils.isBlank(this.groupId)){
			SequenceAccessorService sas = getSequenceAccessorService();
			Long nextSeq = sas.getNextAvailableSequenceNumber(
					KimConstants.SequenceNames.KRIM_GROUP_ID_S, this.getClass());
			this.groupId = nextSeq.toString();
		}
		if(StringUtils.isBlank(this.groupTypeId)) {
			this.groupTypeId = "1";
		}
	}
	
	public String getGroupId(){
//		if(StringUtils.isBlank(this.groupId)){
//			initializeDocumentForNewGroup();
//		}
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return this.groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the groupNamespace
	 */
	public String getGroupNamespace() {
		return this.groupNamespace;
	}

	/**
	 * @param groupNamespace the groupNamespace to set
	 */
	public void setGroupNamespace(String groupNamespace) {
		this.groupNamespace = groupNamespace;
	}

	/**
	 * @return the groupTypeId
	 */
	public String getGroupTypeId() {
		return this.groupTypeId;
	}

	/**
	 * @param groupTypeId the groupTypeId to set
	 */
	public void setGroupTypeId(String groupTypeId) {
		this.groupTypeId = groupTypeId;
	}

	/**
	 * @return the groupTypeName
	 */
	public String getGroupTypeName() {
		return this.groupTypeName;
	}

	/**
	 * @param groupTypeName the groupTypeName to set
	 */
	public void setGroupTypeName(String groupTypeName) {
		this.groupTypeName = groupTypeName;
	}

	/**
	 * @return the members
	 */
	public List<GroupDocumentMember> getMembers() {
		return this.members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(List<GroupDocumentMember> members) {
		this.members = members;
	}

	/**
	 * @return the qualifiers
	 */
	public List<GroupDocumentQualifier> getQualifiers() {
		return this.qualifiers;
	}

	/**
	 * @param qualifiers the qualifiers to set
	 */
	public void setQualifiers(List<GroupDocumentQualifier> qualifiers) {
		this.qualifiers = qualifiers;
	}

	public GroupDocumentQualifier getQualifier(String kimAttributeDefnId) {
		for(GroupDocumentQualifier qualifier: qualifiers){
			if(qualifier.getKimAttrDefnId().equals(kimAttributeDefnId))
				return qualifier;
		}
		return null;
	}

	public AttributeSet getQualifiersAsAttributeSet() {
		AttributeSet attributes = new AttributeSet(qualifiers.size());
		for(GroupDocumentQualifier qualifier: qualifiers){
			if ( qualifier.getKimAttribute() != null ) {
				attributes.put(qualifier.getKimAttribute().getAttributeName(), qualifier.getAttrVal());
			} else {
				LOG.warn( "Unknown attribute ID on group: " + qualifier.getKimAttrDefnId() + " / value=" + qualifier.getAttrVal());
				attributes.put("Unknown Attribute ID: " + qualifier.getKimAttrDefnId(), qualifier.getAttrVal());
			}
		}
		return attributes;
	}
	
	public void setDefinitions(AttributeDefinitionMap definitions) {
		super.setDefinitions(definitions);
		if(getQualifiers()==null || getQualifiers().size()<1){
			GroupDocumentQualifier qualifier;
			setQualifiers(new ArrayList<GroupDocumentQualifier>());
			for(String key : getDefinitions().keySet()) {
				qualifier = new GroupDocumentQualifier();
	        	qualifier.setKimAttrDefnId(getKimAttributeDefnId(getDefinitions().get(key)));
	        	getQualifiers().add(qualifier);
	        }
		}
	}

	/**
	 * @return the editing
	 */
	public boolean isEditing() {
		return this.editing;
	}

	/**
	 * @param editing the editing to set
	 */
	public void setEditing(boolean editing) {
		this.editing = editing;
	}

}