/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.uif.component;

import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean;
import org.kuali.rice.krad.datadictionary.validator.ErrorReport;
import org.kuali.rice.krad.datadictionary.validator.TracerToken;
import org.kuali.rice.krad.uif.modifier.ComponentModifier;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.Tooltip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Component defines basic properties and methods that all rendering element implement
 *
 * <p>
 * All classes of the UIF that are used as a rendering element implement the
 * component interface. All components within the framework have the
 * following structure:
 * <ul>
 * <li>Dictionary Configuration/Composition</li>
 * <li>Java Class (the Component implementation</li>
 * <li>>JSP Template Renderer</li>
 * </ul>
 * </p>
 * <p>
 * There are three basic types of components:
 * <ul>
 * <li>Container Components: {@code View}, {@code Group}</li>
 * <li>Field Components: {@code Field}</li>
 * <li>Widget Components: {@code Widget}</li>
 * </ul>
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.krad.uif.container.Container
 * @see org.kuali.rice.krad.uif.field.Field
 * @see org.kuali.rice.krad.uif.widget.Widget
 */
public interface Component extends UifDictionaryBean, Serializable, Ordered, ScriptEventSupport {

    /**
     * The unique id (within a given tree) for the component
     *
     * <p>
     * The id will be used by renderers to set the HTML element id. This gives a way to find various elements
     * for scripting. If the id is not given, a default will be generated by the framework
     * </p>
     *
     * @return String id
     */
    String getId();

    /**
     * Setter for the unique id (within a given tree) for the component
     *
     * @param id - string to set as the component id
     */
    void setId(String id);

    /**
     * Holds the id for the component that can be used to request new instances of that component from the
     * {@link org.kuali.rice.krad.uif.util.ComponentFactory}
     *
     * <p>
     * During component refreshes the component is reinitialized and the lifecycle is performed again to
     * reflect the component state based on the latest updates (data, other component state). Since the lifecycle
     * is only performed on the component, a new instance with configured initial state needs to be retrieved. Some
     * component instances, such as those that are nested or created in code, cannot be obtained from the spring
     * factory. For those the initial state is captured during the perform initialize phase and the factory id
     * generated for referencing retrieving that configuration during a refresh
     * </p>
     *
     * @return String bean id for component
     */
    String getBaseId();

    /**
     * Setter for the base id that backs the component instance
     *
     * @param baseId
     */
    void setBaseId(String baseId);

    /**
     * The name for the component type
     *
     * <p>
     * This is used within the rendering layer to pass the component instance into the template. The component instance
     * is exported under the name given by this method.
     * </p>
     *
     * @return String type name
     */
    String getComponentTypeName();

    /**
     * The path to the JSP file that should be called to render the component
     *
     * <p>
     * The path should be relative to the web root. An attribute will be available to the component to use under the
     * name given by the method {@code getComponentTypeName}. Based on the component type, additional attributes could
     * be available for use. See the component documentation for more information on such attributes.
     * </p>
     *
     * <p>
     * e.g. '/krad/WEB-INF/jsp/tiles/component.jsp'
     * </p>
     *
     * @return String representing the template path
     */
    String getTemplate();

    /**
     * Setter for the components template
     *
     * @param template
     */
    void setTemplate(String template);

    /**
     * The component title
     *
     * <p>
     * Depending on the component can be used in various ways. For example with a Container component the title is
     * used to set the header text. For components like controls other other components that render an HTML element it
     * is used to set the HTML title attribute.
     * </p>
     *
     * @return String title for component
     */
    String getTitle();

    /**
     * Setter for the component's title
     *
     * @param title
     */
    void setTitle(String title);

    /**
     * Initializes the component
     *
     * <p>
     * Where components can set defaults and setup other necessary state. The initialize method should only be called
     * once per component lifecycle and is invoked within the initialize phase of the view lifecylce.
     * </p>
     *
     * @param view - view instance in which the component belongs
     * @param model - object instance containing the view data
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#performInitialization(org.kuali.rice.krad.uif.view.View,
     *      Object)
     */
    void performInitialization(View view, Object model);

    /**
     * Called after the initialize phase to perform conditional logic based on the model data
     *
     * <p>
     * Where components can perform conditional logic such as dynamically generating new fields or setting field state
     * based on the given data
     * </p>
     *
     * @param view - view instance to which the component belongs
     * @param model - Top level object containing the data (could be the form or a
     * top level business object, dto)
     */
    void performApplyModel(View view, Object model, Component parent);

    /**
     * The last phase before the view is rendered
     *
     * <p>
     * Here final preparations can be made based on the updated view state.
     * </p>
     *
     * @param view - view instance that should be finalized for rendering
     * @param model - top level object containing the data
     * @param parent - parent component
     */
    void performFinalize(View view, Object model, Component parent);

    /**
     * List of components that are contained within the component and should be sent through
     * the lifecycle
     *
     * <p>
     * Used by {@code ViewHelperService} for the various lifecycle callbacks
     * </p>
     *
     * @return List<Component> child components
     */
    List<Component> getComponentsForLifecycle();

    /**
     * List of components that are maintained by the component as prototypes for creating other component instances
     *
     * <p>
     * Prototypes are held for configuring how a component should be created during the lifecycle. An example of this
     * are the fields in a collection group that are created for each collection record. They only participate in the
     * initialize phase.
     * </p>
     *
     * @return List<Component> child component prototypes
     */
    List<Component> getComponentPrototypes();

    /**
     * List of components that are contained within the List of {@code PropertyReplacer} in component
     *
     * <p>
     * Used to get all the nested components in the property replacers.
     * </p>
     *
     * @return List<Component> {@code PropertyReplacer} child components
     */
    List<Component> getPropertyReplacerComponents();

    /**
     * {@code ComponentModifier} instances that should be invoked to
     * initialize the component
     *
     * <p>
     * These provide dynamic initialization behavior for the component and are
     * configured through the components definition. Each initializer will get
     * invoked by the initialize method.
     * </p>
     *
     * @return List of component modifiers
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#performInitialization(org.kuali.rice.krad.uif.view.View,
     *      Object)
     */
    List<ComponentModifier> getComponentModifiers();

    /**
     * Setter for the components List of {@code ComponentModifier}
     * instances
     *
     * @param componentModifiers
     */
    void setComponentModifiers(List<ComponentModifier> componentModifiers);

    /**
     * Indicates whether the component should be rendered in the UI
     *
     * <p>
     * If set to false, the corresponding component template will not be invoked
     * (therefore nothing will be rendered to the UI).
     * </p>
     *
     * @return boolean true if the component should be rendered, false if it
     *         should not be
     */
    boolean isRender();

    /**
     * Setter for the components render indicator
     *
     * @param render
     */
    void setRender(boolean render);

    /**
     * Indicates whether the component should be hidden in the UI
     *
     * <p>
     * How the hidden data is maintained depends on the views persistence mode.
     * If the mode is request, the corresponding data will be rendered to the UI
     * but not visible. If the mode is session, the data will not be rendered to
     * the UI but maintained server side.
     * </p>
     *
     * <p>
     * For a {@code Container} component, the hidden setting will apply to
     * all contained components (making a section hidden makes all fields within
     * the section hidden)
     * </p>
     *
     * @return boolean true if the component should be hidden, false if it
     *         should be visible
     */
    boolean isHidden();

    /**
     * Setter for the hidden indicator
     *
     * @param hidden
     */
    void setHidden(boolean hidden);

    /**
     * Indicates whether the component can be edited
     *
     * <p>
     * When readOnly the controls and widgets of {@code Field} components
     * will not be rendered. If the Field has an underlying value it will be
     * displayed readOnly to the user.
     * </p>
     *
     * <p>
     * For a {@code Container} component, the readOnly setting will apply
     * to all contained components (making a section readOnly makes all fields
     * within the section readOnly).
     * </p>
     *
     * @return boolean true if the component should be readOnly, false if is
     *         allows editing
     */
    boolean isReadOnly();

    /**
     * Setter for the read only indicator
     *
     * @param readOnly
     */
    void setReadOnly(boolean readOnly);

    /**
     * Indicates whether the component is required
     *
     * <p>
     * At the general component level required means there is some action the
     * user needs to take within the component. For example, within a section it
     * might mean the fields within the section should be completed. At a field
     * level, it means the field should be completed. This provides the ability
     * for the renderers to indicate the required action.
     * </p>
     *
     * @return boolean true if the component is required, false if it is not
     *         required
     */
    Boolean getRequired();

    /**
     * Setter for the required indicator
     *
     * @param required
     */
    void setRequired(Boolean required);

    /**
     * CSS style string to be applied to the component
     *
     * <p>
     * Any style override or additions can be specified with this attribute.
     * This is used by the renderer to set the style attribute on the
     * corresponding element.
     * </p>
     *
     * <p>
     * e.g. 'color: #000000;text-decoration: underline;'
     * </p>
     *
     * @return String css style string
     */
    String getStyle();

    /**
     * Setter for the components style
     *
     * @param style
     */
    void setStyle(String style);

    /**
     * CSS style class(s) to be applied to the component
     *
     * <p>
     * Declares style classes for the component. Multiple classes are specified
     * with a space delimiter. This is used by the renderer to set the class
     * attribute on the corresponding element. The class(s) declared must be
     * available in the common style sheets or the style sheets specified for
     * the view
     * </p>
     *
     * @return List<String> css style classes to appear on the 'class' attribute
     */
    List<String> getCssClasses();

    /**
     * Setter for the components style classes
     *
     * @param styleClasses
     */
    void setCssClasses(List<String> styleClasses);

    /**
     * Adds a single style to the list of styles on this component
     *
     * @param styleClass
     */
    void addStyleClass(String styleClass);

    /**
     * Appends to the inline style set on a component
     *
     * @param itemStyle
     */
    void appendToStyle(String itemStyle);

    /**
     * Number of places the component should take up horizontally in the
     * container
     *
     * <p>
     * All components belong to a {@code Container} and are placed using a
     * {@code LayoutManager}. This property specifies how many places
     * horizontally the component should take up within the container. This is
     * only applicable for table based layout managers. Default is 1
     * </p>
     *
     * TODO: this should not be on component interface since it only applies if
     * the layout manager supports it, need some sort of layoutOptions map for
     * field level options that depend on the manager
     *
     * @return int number of columns to span
     */
    int getColSpan();

    /**
     * Setter for the components column span
     *
     * @param colSpan
     */
    void setColSpan(int colSpan);

    /**
     * Number of places the component should take up vertically in the container
     *
     * <p>
     * All components belong to a {@code Container} and are placed using a
     * {@code LayoutManager}. This property specifies how many places
     * vertically the component should take up within the container. This is
     * only applicable for table based layout managers. Default is 1
     * </p>
     *
     * TODO: this should not be on component interface since it only applies if
     * the layout manager supports it, need some sort of layoutOptions map for
     * field level options that depend on the manager
     *
     * @return int number of rows to span
     */
    int getRowSpan();

    /**
     * Setter for the component row span
     *
     * @param rowSpan
     */
    void setRowSpan(int rowSpan);

    /**
     * Context map for the component
     *
     * <p>
     * Any el statements configured for the components properties (e.g.
     * title="@{foo.property}") are evaluated using the el context map. This map
     * will get populated with default objects like the model, view, and request
     * from the {@code ViewHelperService}. Other components can push
     * further objects into the context so that they are available for use with
     * that component. For example, {@code Field} instances that are part
     * of a collection line as receive the current line instance
     * </p>
     *
     * <p>
     * Context map also provides objects to methods that are invoked for
     * {@code GeneratedField} instances
     * </p>
     *
     * <p>
     * The Map key gives the name of the variable that can be used within
     * expressions, and the Map value gives the object instance for which
     * expressions containing the variable should evaluate against
     * </p>
     *
     * <p>
     * NOTE: Calling getContext().putAll() will skip updating any configured property replacers for the
     * component. Instead you should call #pushAllToContext
     * </p>
     *
     * @return Map<String, Object> context
     */
    Map<String, Object> getContext();

    /**
     * Setter for the context Map
     *
     * @param context
     */
    void setContext(Map<String, Object> context);

    /**
     * Places the given object into the context Map for the component with the
     * given name
     *
     * <p>
     * Note this also will push context to property replacers configured on the component.
     * To place multiple objects in the context, you should use #pushAllToContext since that
     * will call this method for each and update property replacers. Using {@link #getContext().putAll()}
     * will bypass property replacers.
     * </p>
     *
     * @param objectName - name the object should be exposed under in the context map
     * @param object - object instance to place into context
     */
    void pushObjectToContext(String objectName, Object object);

    /**
     * Places each entry of the given Map into the context for the component
     *
     * <p>
     * Note this will call #pushObjectToContext for each entry which will update any configured property
     * replacers as well. This should be used in place of getContext().putAll()
     * </p>
     *
     * @param objects - Map<String, Object> objects to add to context, where the entry key will be the context key
     * and the entry value will be the context value
     */
    void pushAllToContext(Map<String, Object> objects);

    /**
     * gets a list of {@code PropertyReplacer} instances
     *
     * <p>They will be evaluated
     * during the view lifecycle to conditionally set properties on the
     * {@code Component} based on expression evaluations</p>
     *
     * @return List<PropertyReplacer> replacers to evaluate
     */
    List<PropertyReplacer> getPropertyReplacers();

    /**
     * Setter for the components property substitutions
     *
     * @param propertyReplacers
     */
    void setPropertyReplacers(List<PropertyReplacer> propertyReplacers);

    /**
     * The options that are passed through to the Component renderer
     *
     * <p>
     * The Map key is the option name, with the Map value as the option value. See
     * documentation on the particular widget render for available options.
     * </p>
     *
     * @return Map<String, String> options
     */
    Map<String, String> getTemplateOptions();

    /**
     * Setter for the template's options
     *
     * @param templateOptions
     */
    void setTemplateOptions(Map<String, String> templateOptions);

    /**
     * Builds a string from the underlying <code>Map</code> of template options that will export that options as a
     * JavaScript Map for use in js and jQuery plugins
     *
     * <p>
     * See documentation on the particular component render for available options.
     * </p>
     *
     * @return String options
     */
    String getTemplateOptionsJSString();

    /**
     * Setter for the template's options
     *
     * @param templateOptionsJSString
     */
    void setTemplateOptionsJSString(String templateOptionsJSString);

    /**
     * Order of a component within a List of other components
     *
     * <p>Lower numbers are placed higher up in the list, while higher numbers are placed
     * lower in the list</p>
     *
     * @return int ordering number
     * @see org.springframework.core.Ordered#getOrder()
     */
    int getOrder();

    /**
     * Setter for the component's order
     *
     * @param order
     */
    void setOrder(int order);

    /**
     * The Tooltip widget that renders a tooltip with additional information about the element on
     * specified trigger event
     *
     * @return Tooltip
     */
    Tooltip getToolTip();

    /**
     * Setter for the component tooltip widget instance
     *
     * @param toolTip
     */
    void setToolTip(Tooltip toolTip);

    /**
     * The name of the method that should be invoked for finalizing the component
     * configuration (full method name, without parameters or return type)
     *
     * <p>
     * Note the method can also be set with the finalizeMethodInvoker
     * targetMethod property. If the method is on the configured
     * {@code ViewHelperService}, only this property needs to be configured
     * </p>
     *
     * <p>
     * The model backing the view will be passed as the first argument method and then
     * the {@code Component} instance as the second argument. If any additional method
     * arguments are declared with the finalizeMethodAdditionalArguments, they will then
     * be passed in the order declared in the list
     * </p>
     *
     * <p>
     * If the component is selfRendered, the finalize method can return a string which
     * will be set as the component's renderOutput. The selfRendered indicator will also
     * be set to true on the component.
     * </p>
     *
     * @return String method name
     */
    String getFinalizeMethodToCall();

    /**
     * The List of Object instances that should be passed as arguments to the finalize method
     *
     * <p>
     * These arguments are passed to the finalize method after the standard model and component
     * arguments. They are passed in the order declared in the list
     * </p>
     *
     * @return List<Object> additional method arguments
     */
    List<Object> getFinalizeMethodAdditionalArguments();

    /**
     * {@code MethodInvokerConfig} instance for the method that should be invoked
     * for finalizing the component configuration
     *
     * <p>
     * MethodInvoker can be configured to specify the class or object the method
     * should be called on. For static method invocations, the targetClass
     * property can be configured. For object invocations, that targetObject
     * property can be configured
     * </p>
     *
     * <p>
     * If the component is selfRendered, the finalize method can return a string which
     * will be set as the component's renderOutput. The selfRendered indicator will also
     * be set to true on the component.
     * </p>
     *
     * @return MethodInvokerConfig instance
     */
    MethodInvokerConfig getFinalizeMethodInvoker();

    /**
     * Indicates whether the component contains its own render output (through
     * the renderOutput property)
     *
     * <p>
     * If self rendered is true, the corresponding template for the component
     * will not be invoked and the renderOutput String will be written to the
     * response as is.
     * </p>
     *
     * @return boolean true if component is self rendered, false if not (renders
     *         through template)
     */
    boolean isSelfRendered();

    /**
     * Setter for the self render indicator
     *
     * @param selfRendered
     */
    void setSelfRendered(boolean selfRendered);

    /**
     * Rendering output for the component that will be sent as part of the
     * response (can contain static text and HTML)
     *
     * @return String render output
     */
    String getRenderedHtmlOutput();

    /**
     * Setter for the component's render output
     *
     * @param renderOutput
     */
    void setRenderedHtmlOutput(String renderOutput);

    /**
     * Disables the storage of the component in session (when the framework determines it needs to be due to a
     * refresh condition)
     *
     * <p>
     * When the framework determines there is a condition on the component that requires it to keep around between
     * posts, it will store the component instance in session. This flag can be set to disable this behavior (which
     * would require custom application logic to support behavior such as refresh)
     * </p>
     *
     * @return boolean true if the component should not be stored in session, false if session storage is allowed
     */
    boolean isDisableSessionPersistence();

    /**
     * Setter for disabling storage of the component in session
     *
     * @param disableSessionPersistence
     */
    void setDisableSessionPersistence(boolean disableSessionPersistence);

    /**
     * Indicates whether the component should be stored with the session view regardless of configuration
     *
     * <p>
     * By default the framework nulls out any components that do not have a refresh condition or are needed for
     * collection processing. This can be a problem if custom application code is written to refresh a component
     * without setting the corresponding component flag. In this case this property can be set to true to force the
     * framework to keep the component in session. Defaults to false
     * </p>
     *
     * @return boolean true if the component should be stored in session, false if not
     */
    boolean isForceSessionPersistence();

    /**
     * Setter for the indicator to force persistence of the component in session
     *
     * @param persistInSession
     */
    void setForceSessionPersistence(boolean persistInSession);

    /**
     * Security object that indicates what authorization (permissions) exist for the component
     *
     * @return ComponentSecurity instance
     */
    ComponentSecurity getComponentSecurity();

    /**
     * Setter for the components security object
     *
     * @param componentSecurity
     */
    void setComponentSecurity(ComponentSecurity componentSecurity);

    /**
     * Spring EL expression, which when evaluates to true, makes this component visible
     *
     * @return the SpEl expression string
     */
    String getProgressiveRender();

    /**
     * Setter for progressiveRender Spring EL expression
     *
     * @param progressiveRender the progressiveRender to set
     */
    void setProgressiveRender(String progressiveRender);

    /**
     * Spring EL expression, which when evaluates to true, causes this component to be refreshed
     *
     * @return the SpEl expression string
     */
    String getConditionalRefresh();

    /**
     * Setter for conditionalRefresh Spring EL expression
     *
     * @param conditionalRefresh the conditionalRefresh to set
     */
    void setConditionalRefresh(String conditionalRefresh);

    /**
     * List of control names (ids) extracted from {@link #getProgressiveRender()}
     *
     * @return the list of control names
     */
    List<String> getProgressiveDisclosureControlNames();

    /**
     * A JavaScript expression constructed from {@link #getConditionalRefresh()}
     *
     * <p>The script can be executed on the client to determine if the original exp was satisfied before
     * interacting with the server.</p>
     *
     * @return the JS script
     */
    String getProgressiveDisclosureConditionJs();

    /**
     * A JavaScript expression constructed from {@link #getProgressiveRender()}
     *
     * <p>The script can be executed on the client to determine if the original exp was satisfied before
     * interacting with the server.</p>
     *
     * @return the JS script
     */
    String getConditionalRefreshConditionJs();

    /**
     * The list of control names (ids) extracted from {@link #getConditionalRefresh()}
     *
     * @return the list of control names
     */
    List<String> getConditionalRefreshControlNames();

    /**
     * Indicates whether the component will be stored on the client, but hidden, after the first retrieval
     *
     * <p>
     * The component will not be rendered hidden after first retrieval if this flag is set to true. The component will
     * then be fetched via an ajax call when it should be rendered.
     * </p>
     *
     * @return the progressiveRenderViaAJAX
     */
    boolean isProgressiveRenderViaAJAX();

    /**
     * Setter for the progressiveRenderViaAJAX flag
     *
     * @param progressiveRenderViaAJAX the progressiveRenderViaAJAX to set
     */
    void setProgressiveRenderViaAJAX(boolean progressiveRenderViaAJAX);

    /**
     * Determines whether the component will always be retrieved from the server and shown
     *
     * <p>
     * If true, when the progressiveRender condition is satisfied, the component
     * will always be retrieved from the server and shown(as opposed to being
     * stored on the client, but hidden, after the first retrieval as is the
     * case with the progressiveRenderViaAJAX option). <b>By default, this is
     * false, so components with progressive render capabilities will always be
     * already within the client html and toggled to be hidden or visible.</b> </p>
     *
     * @return the progressiveRenderAndRefresh
     */
    boolean isProgressiveRenderAndRefresh();

    /**
     * Setter for the progressiveRenderAndRefresh flag
     *
     * @param progressiveRenderAndRefresh the progressiveRenderAndRefresh to set
     */
    void setProgressiveRenderAndRefresh(boolean progressiveRenderAndRefresh);

    /**
     * Specifies a property by name that when its value changes will automatically perform
     * a refresh on this component
     *
     * <p>
     * This can be a comma
     * separated list of multiple properties that require this component to be
     * refreshed when any of them change. <Br>DO NOT use with progressiveRender
     * unless it is know that progressiveRender condition will always be
     * satisfied before one of these fields can be changed.
     * </p>
     *
     * @return List property names that should trigger a refresh when their values change
     */
    List<String> getRefreshWhenChangedPropertyNames();

    /**
     * Setter for the list of property names that trigger a refresh
     *
     * @param refreshWhenChangedPropertyNames
     */
    void setRefreshWhenChangedPropertyNames(List<String> refreshWhenChangedPropertyNames);

    /**
     *  Returns a list of componentIds which will be also be refreshed when this component is refreshed
     *  <p>
     *  This will be a comma separated list of componentIds that need to be refreshed when a refresh
     *  condition has been set on this component.
     *  </p>
     * @return List<String>
     */
    public List<String> getAdditionalComponentsToRefresh();

    /**
     * Setter for alsoRefreshComponents
     *
     * @param additionalComponentsToRefresh
     */
    public void setAdditionalComponentsToRefresh(List<String> additionalComponentsToRefresh);

    /**
     * Returns a string for representing the list of additional components to be refreshed as
     * a JavaScript value
     *
     * @return String representation of the list of componentIds for the components that need to be refreshed
     */
    public String getAdditionalComponentsToRefreshJs();

    /**
     * Indicates the component can be refreshed by an action
     *
     * <p>
     * This is set by the framework for configured ajax action buttons, should not be set in
     * configuration
     * </p>
     *
     * @return boolean true if the component is refreshed by an action, false if not
     */
    boolean isRefreshedByAction();

    /**
     * Setter for the refreshed by action indicator
     *
     * <p>
     * This is set by the framework for configured ajax action buttons, should not be set in
     * configuration
     * </p>
     *
     * @param refreshedByAction
     */
    void setRefreshedByAction(boolean refreshedByAction);

    /**
     * Indicates whether data contained within the component should be reset (set to default) when the
     * component is refreshed
     *
     * @return boolean true if data should be refreshed, false if data should remain as is
     */
    boolean isResetDataOnRefresh();

    /**
     * Setter for the reset data on refresh indicator
     *
     * @param resetDataOnRefresh
     */
    void setResetDataOnRefresh(boolean resetDataOnRefresh);

    /**
     *  Time in seconds after which the component is automatically refreshed
     *
     * @return  time in seconds
     */
    int getRefreshTimer();

    /**
     *  Setter for refreshTimer
     *
     * @param refreshTimer
     */
    void setRefreshTimer(int refreshTimer);

    /**
     * Add a data attribute to the dataAttributes map
     *
     * @param key
     * @param value
     */
    void addDataAttribute(String key, String value);

    Map<String, String> getDataAttributes();

    /**
     * The DataAttributes that will be written to the html and/or through script to be consumed by jQuery
     *
     * <p>The attributes that are complex objects (contain {}) they will be written through script.
     * The attributes that are simple (contain no objects) will be written directly to the html of the
     * component using standard data-.</p>
     * <p>Either way they can be access through .data() call in jQuery.</p>
     *
     * @param dataAttributes
     */
    void setDataAttributes(Map<String, String> dataAttributes);

    /**
     * A JavaScript script that will add data to this component by the element which matches its id
     *
     * <p>
     * This will return script for only the complex data elements (containing {})</p>
     *
     * @return jQuery data script for adding complex data attributes
     */
    String getComplexDataAttributesJs();

    /**
     * The string that can be put into a the tag of a component to add data attributes inline
     *
     * <p>
     * This does not include the complex attributes which contain {}</p>
     *
     * @return html string for data attributes for the simple attributes
     */
    String getSimpleDataAttributes();

    /**
     * A JavaScript script that will add data to this component by the element which matches its id
     *
     * <p>This will return script for all the complex data elements.
     * This method is useful for controls that are implemented as spring form tags</p>
     *
     * @return jQuery data script for adding all data attributes
     */
    String getAllDataAttributesJs();

    /**
     * Validates different requirements of component compiling a series of reports detailing information on errors
     * found in the component.  Used by the RiceDictionaryValidator.
     *
     * @param tracer Record of component's location
     * @return A list of ErrorReports detailing errors found within the component and referenced within it
     */
    ArrayList<ErrorReport> completeValidation(TracerToken tracer);
}
