/*=================================================================
AmbieSense Context Software GPL Source Code.

Copyright (C) 2005 and onwards by AmbieSense Limited, 
 and/or its subsidiaries and co-founders. AmbieSense Limited
 is an R&D-focused company incorporated in Scotland.

This file is part of the AmbieSense Context Software GPL Source Code.

The AmbieSense Context Software GPL Source Code is free software: 
you can redistribute it and/or modify it under the terms of the 
GNU General Public License Version 3 as published by
the Free Software Foundation.

The AmbieSense Context Software Source Code is distributed in 
the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or 
FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with The AmbieSense Context Software Source Code. 
If not, see <http://www.gnu.org/licenses/>.

In addition, the The AmbieSense Context Software Source Code is also 
subject to certain additional terms. You should have received a copy 
of these additional terms immediately following the terms and 
conditions of the GNU General Public License which 
accompanied the The AmbieSense Context Software Source Code. 
If not, please request a copy in writing from id Software at the address below.

If you have questions concerning this license or the applicable 
additional terms, you may contact in writing: 
AmbieSense Limited, 7 Queens Gardens, Aberdeen, 
AB25 4YD, Scotland; or by email: 
kontactATambiesense.com (replace "AT" with "@").
============================================================ */

package com.ambiesense.context;

import java.io.File;
import java.net.*;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import com.ambiesense.persistent.UUIDGenerator;
import com.ambiesense.util.Attributes;
import com.ambiesense.util.Relevance;
import com.ambiesense.util.Templates;
import com.ambiesense.util.Contexts;
import com.ambiesense.util.Links;
import com.ambiesense.util.Strings;
import java.util.Vector;

public final class ContextSpace implements Serializable/*ContextMiddlewareEventListener*/{

	private static final long serialVersionUID = 1L;
	private static final ContextSpace CONTEXTSPACE = new ContextSpace(); 	
	private static ContextStorage contextDataBase;	
	private static Context context = null;
	private static Template template = null; 
	private static ContextAccess contextManager;	


	//private constructor
	private ContextSpace(){
		contextDataBase =  new ContextStorage();
	}

	public synchronized static ContextSpace getInstance(User validUser){
		try{
			User user = new User("Hans","");
			contextManager = ContextAccess.getInstance(user);
			//openConnection();
			if(contextManager.exists(validUser)){
				return CONTEXTSPACE; 
			}else{
				return null;
			}				
		} catch(Exception e){
			return null;
		}
	}
	/**
	 * Sets the template that new contexts will be instantiated from.
	 * It tries to add the template to the repository if it doesn't yet exist in it.
	 * Note that if the template is null, no changes will be done, and false will be returned.
	 * @param template the template to be used to create new contexts from
	 * @return returns true if successful, otherwise false
	 */
	public synchronized boolean setTemplate(Template template){
		if (template != null){
			ContextSpace.addTemplate(template);
			ContextSpace.template = template;
			return true;
		} else{
			return false;
		}
	}
	/**
	 * Gets the current template that new contexts will be instantiated from
	 * Note that if the template has not yet been set, this method will return null.
	 * in stead of a template.
	 */
	public static synchronized Template getTemplate(){
		return ContextSpace.template;
	}
	/**
	 * Adds a template to the context repository.
	 * The template can not be changed after storing.
	 * @param template the template that will be added
	 * @return returns true if it succeeded, otherwise false
	 */
	public static synchronized boolean addTemplate(Template template){	

		return contextDataBase.addTemplate(template);			
	}	
	/**
	 * Gets a template from the context repository.
	 * @param templateUUID the id of the template to be retrieved 
	 * @return returns a template it it exists, otherwise it returns null
	 */
	public static synchronized Template getTemplate(String templateUUID){
		return contextDataBase.getTemplate(templateUUID);
	}
	/**
	 * Gets all templates from the context repository.
	 * Note that if there are too many templates that are stored, one might get memory full
	 * @return returns all of the templates.
	 */
	public static synchronized Templates getTemplates(){
		return contextDataBase.getTemplates();
	}	

	/**
	 * Deletes a template from the context repository if it exists.
	 * Note that the template will not be deleted if there are any 
	 * contexts instantiated from this template residing in the context repository.
	 * All contexts decending from this template must first be deleted before this
	 * method can succeed with deleting the template.
	 * @param templateUUID the id of the template to be deleted 
	 * @return returns true if the deletion was a success, otherwise false
	 */
	public static synchronized boolean deleteTemplate(String templateUUID){
		return contextDataBase.deleteTemplate(templateUUID);
	}
	/**
	 * Sets the context that is currently in focus
	 * @param context the context to be currently in focus
	 * @return returns true if successful, otherwise false
	 */

	public static synchronized boolean setContext(Context context){
		if (context != null){
			ContextSpace.context = context;
			return true;
		} else{
			return false;
		}
	}
	/**
	 * Gets the context that is currently in focus
	 */
	public Context getContext(){
		return ContextSpace.context;
	}

	/**
	 * Adds a context to the context repository.
	 * @param context the context that will be added
	 * @return returns true if it succeeded, otherwise false
	 */

	public synchronized boolean addContext(Context context){		
		return contextDataBase.addContext(context);			
	}
	/**
	 * Deletes a context from the context repository if it exists.
	 * @param contextUUID the id of the context to be deleted 
	 * @return returns true if the deletion was a success, otherwise false
	 */
	public synchronized boolean deleteContext(String contextUUID){
		return contextDataBase.deleteContext(contextUUID);
	}
	/**
	 * Deletes all context from the context repository that were instantiated from the template.
	 * @param templateUUID the id of the context to be deleted 
	 * @return returns true if the deletion was a success, otherwise false
	 */
	public synchronized boolean deleteAllContexts(String templateUUID){
		return contextDataBase.deleteAllContexts(templateUUID);
	}
	/**
	 * Deletes all contexts from the context repository that exists.
	 * @return returns true if the deletion was a success, otherwise false
	 */
	public synchronized boolean deleteAllContexts(){
		return contextDataBase.deleteAllContexts();
	}
	/**
	 * Gets a context from the context repository if it exists.
	 * @param contextUUID the id of the context to be retrieved. 
	 * @return returns the context if the context exists, otherwise null
	 */
	public synchronized Context getContext(String contextUUID){
		return contextDataBase.getContext(contextUUID);	
	}

	/**
	 * Gets all contexts from the context repository.
	 * Note that if there are too many contexts that are stored, one might get memory full
	 * @return returns all of the contexts.
	 */
	public synchronized Contexts getContexts(){
		return contextDataBase.getContexts();
	}	
	/**
	 * Adds a link to a context in the context repository.
	 * @param link the link that will be added
	 * @return returns true if it succeeded, otherwise false
	 */
	public synchronized boolean addLink(Link link){		
		return contextDataBase.addLink(link);			
	}
	/**
	 * Updates a link in the context repository.
	 * @param link the link that will be updated
	 * @return returns true if it succeeded, otherwise false
	 */
	public synchronized boolean updateLink(Link link){		
		return contextDataBase.updateLink(link);			
	}

	/**
	 * Gets a link from the context repository by the linkUUID.
	 * @param linkUUID the linkUUID of the link.
	 * @return returns the link if it succeeded in finding it, otherwise null
	 */
	public synchronized Link getLink(String linkUUID){		
		return contextDataBase.getLink(linkUUID);			
	}
	/**
	 * Gets all links from the context repository for the given contextUUID.
	 * Note that if there are too many links stored for this context, one might get memory full
	 * @return returns all of the contexts.
	 */
	public synchronized Links getLinks(String contextUUID){
		return contextDataBase.getLinks(contextUUID);
	}
	/**
	 * Deletes a link from the context repository if it exists.
	 * @param linkUUID the id of the link to be deleted 
	 * @return returns true if the deletion was a success, otherwise false
	 */
	public synchronized boolean deleteLink(String linkUUID){
		return contextDataBase.deleteLink(linkUUID);
	}
	/**
	 * Deletes all links from the context repository associated with the given context.
	 * @param contextUUID the id of the context to delete the links from.
	 * @return returns true if the deletion of any links was a success, otherwise false
	 */
	public synchronized boolean deleteLinks(String contextUUID){
		return contextDataBase.deleteLinks(contextUUID);
	}
	/**
	 * Gets all links from the context repository.
	 * Note that if there are too many links that are stored, one might get memory full
	 * @return returns all of the links.
	 */
	public synchronized Links getLinks(){
		return contextDataBase.getLinks();
	}	

	/**
	 * Matches two contexts with each other, independent of their template(s).
	 * 
	 * Each attribute in the first context will be attempted matched with 
	 * a similar attribute in the second context. 
	 * 
	 * If the same attribute exist in both contexts, 
	 * then an equality match is tried on the two attribute values. 
	 * If the value is the same on the same corresponding attribute, then there is a match on that attribute.
	 * If zero of the attributes has the same value, then 0 is returned.
	 * Similarly if all attribute values match, and the attributes are the same,
	 * then 1 is returned. Anything between 0 and 0 means that one or more attributes 
	 * had the same values, but not all. If one swap the two contexts around, the
	 * method will still return the same number.
	 * @return returns a number between 0 and 1;
	 */
	public synchronized float equalityMatch(Context c1, Context c2){
		return contextDataBase.equalityMatch(c1, c2);
	}	

	public synchronized float distanceMatch(Context c1, Context c2){
		return contextDataBase.distanceMatch(c1, c2);
	}	


	private synchronized Hashtable computePercentage(double pathsSearchedFor, Hashtable searchResults){

		Double hashValue;
		double hashDouble;

		//hent en enumeration av n�klene i hashtabellen 
		Enumeration keyEnumeration = searchResults.keys();

		//for alle elementene i enumeration 
		while (keyEnumeration.hasMoreElements()) {
			String key = (String) keyEnumeration.nextElement();

			//for alle n�kler, hent verdien i hastabellen, og rekn ut prosent 
			hashValue = (Double) searchResults.get(key);

			hashDouble = hashValue.doubleValue();

			//compute the percentage
			hashDouble	= hashDouble/pathsSearchedFor;
			hashValue = new Double(hashDouble);

			//put the percentage in the hashtable 	
			searchResults.put(key, hashValue);
		}
		return searchResults;
	}

	public synchronized Strings searchForContexts(Attributes attributes){
		return new Strings();
	}

	/**
	 * <p>	  
	 * Finds all contexts with the same value, given a specified template.
	 * </p>
	 * </p>
	 * @param template - The template that the contexts must have been moulded from.
	 * @param value - the value that is being looked for.
	 * @return  Strings - a list of context uuid's that matched the search.
	 *  
	 */
	public synchronized Strings findContexts(Template template, Object value){
		return contextDataBase.findContexts(template, value);
	}
	/**
	 * <p>	  
	 * Finds all contexts with the same value, given a specified template.
	 * </p>
	 * </p>
	 * @param template - The template that the contexts must have been moulded from.
	 * @param attribute - the attribute that is being looked for.
	 * @param value - the value of the attribute that is being looked for.
	 * @return  Strings - a list of context uuid's that matched the search.
	 *  
	 */
	public synchronized Strings findContexts(Template template, String attribute, Object value){
		return contextDataBase.findContexts(template, attribute, value);
	}
	/*
	/**
	 * <p>
	 * Convenience method that uses the <code>ContextMatcher</code> to match two contexts based upon their structures and attribute values. 
	 * </p>
	 * <p>
	 * Matching is conducted in the following way:
	 * <ul>
	 * <li> First, contexts are validated towards the context template, see the <code>ContextEvaluator</code> documentation for details on how the
	 * 		validation is conducted and what it validates on. This ensures that the two contexts match on structure.
	 * <li> Second, the values are matched. If values does not match, a <code>ContextMatchingEvent</code> is generated.
	 * See the  <code>ContextMatchingEvent</code> for how to derive information of the event. 
	 *</ul>
	 * </p>
	 * <p>
	 * 	Note: The matching algorithm is fail-fast, meaning it fails if one of the contexts do not comply to the context template argument. 
	 * </p>
	 *  
	 * @param context1 A context to match
	 * @param context2 Another context to match
	 * @param contextTemplate - the contextTemplate the two contexts must be valid towards
	 * @param matchinglistener - registers a listener to the context matcher class. The listener is notified of <code>ContextMatchingEvent</code>'s 
	 * that occurs during matching
	 * 
	 *
	 * @see <code>ambiesense.util.ContextEvaluator</code>, <code>ambiesense.util.ContextMatcher</code>, <code>ambiesense.events.ContextMatchingEvent</code> 
	 */	

	/*
	public void match(Context context1, Context context2, Template contextTemplate, ContextMiddlewareEventListener matchingListener) {

		ContextMatcher contextMatcher = new ContextMatcher(matchingListener);
		contextMatcher.match(context1, context2, contextTemplate);

	}	
	 */


	/**
	 * Empties a context space. This metod will delete all contexts and context templates
	 * in the context space. 
	 * @return true if deletion succeeds, false if not
	 * 
	 */
	public synchronized boolean emptyContextSpace(){
		boolean success = false;
	
		success = contextDataBase.deleteAllContextInfo();
	
		return success;
	}

	/**
	 * Method that opens the connection to the context repository.
	 * @return returns true if a connection could be established, otherwise false 
	 */
	public synchronized boolean openConnection() {
		try{
			if (contextDataBase != null) contextDataBase.connect();
		}catch(Exception e){
			return false;
		}
		return true;
	}

	/**
	 * Method that closes the connection to the context repository. 
	 * @return returns true if the connection closed, otherwise false
	 * 
	 */
	public synchronized boolean closeConnection(){
		try{
			if (contextDataBase != null) contextDataBase.disconnect();
		}catch(Exception e){
			return false;
		}
		return true;
	}

	/**
	 * Check to see if context space is closed
	 * @return true if context space is closed, false if not 
	 */
	public synchronized boolean isClosed(){
		return contextDataBase.isClosed();
	}


	/**
	 * Class that connects to the database and utilises the connections and resultsets to do what it's told from 
	 * the ContextSpace class. This is the lowest level towards the actual database 
	 */

	private class ContextStorage{	
		private  Connection connection;
		private String databaseURL = "jdbc:h2:~/contextspace";
		private String userName = "Hans";
		private String password = "";

		private PreparedStatement selectLink;
		private PreparedStatement selectLinks;
		private PreparedStatement selectAllLinks;
		private PreparedStatement deleteLink;
		private PreparedStatement deleteLinks;
		private PreparedStatement selectTemplate;
		private PreparedStatement deleteTemplate;
		private PreparedStatement selectTemplates;
		private PreparedStatement selectContext;
		private PreparedStatement deleteContext;
		private PreparedStatement deleteAllContexts;
		private PreparedStatement deleteContextsWithTemplate;
		private PreparedStatement selectAllContexts;
		private PreparedStatement selectContextsWithTemplate;


		public ContextStorage() {
			//open connection to the database
			try{
				this.connect();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		public synchronized boolean isClosed(){
			try{
				return connection.isClosed();
			}catch(Exception e){
				return true;
			}
		}

		public synchronized boolean connect(){
			try {
				// Load the JDBC driver
				if (this.connection == null){
					connection = DriverManager.getConnection(databaseURL, userName, password);
					this.createDataBaseTables();
				} else{
					if (connection.isClosed()){
						connection = DriverManager.getConnection(databaseURL, userName, password);
						this.createDataBaseTables();
					}
				}
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		}

		public synchronized void disconnect(){
			try {
				if (connection != null) connection.close();
			} catch(Exception e){}
		}


		//**********************	
		//BUILD THE DATABASE
		//**********************

		//create an empty database 
		public synchronized boolean createDataBaseTables(){
			try{
				

				Vector<String> queries = new Vector<String>();

				String templatesTable = "CREATE TABLE IF NOT EXISTS Templates (Template VARCHAR , Attribute VARCHAR , Type LONG, MaxDistance DOUBLE, PRIMARY KEY (Template, Attribute))"; 				
				String contextsTable  = "CREATE TABLE IF NOT EXISTS Contexts (Template VARCHAR , ContextUUID VARCHAR , Attribute VARCHAR , Timestamp BIGINT, Type BIGINT, Text VARCHAR , Double DOUBLE, Long BIGINT, Boolean BOOLEAN, PRIMARY KEY(Template, ContextUUID, Attribute, Timestamp))";
				String linksTable     = "CREATE TABLE IF NOT EXISTS Links (ContextUUID VARCHAR, LinkUUID VARCHAR, Href VARCHAR, Hreflang VARCHAR, Media VARCHAR, Rel VARCHAR , Target VARCHAR , Type VARCHAR , LinkText VARCHAR, Timestamp LONG, PRIMARY KEY(ContextUUID, LinkUUID, Timestamp))";

				queries.add(contextsTable);													
				queries.add(linksTable);
				queries.add(templatesTable);										

				fireUpdateQueries(queries);


				selectLink 	    = connection.prepareStatement("SELECT ContextUUID, LinkUUID, Href, Hreflang, Media, Rel, Target, Type, LinkText, Timestamp FROM Links WHERE LinkUUID = ?");
				selectLinks 	= connection.prepareStatement("SELECT ContextUUID, LinkUUID, Href, Hreflang, Media, Rel, Target, Type, LinkText, Timestamp FROM Links WHERE ContextUUID = ?");
				selectAllLinks 	= connection.prepareStatement("SELECT ContextUUID, LinkUUID, Href, Hreflang, Media, Rel, Target, Type, LinkText, Timestamp FROM Links");
				deleteLink 	    = connection.prepareStatement("DELETE FROM Links WHERE LinkUUID = ?");
				deleteLinks     = connection.prepareStatement("DELETE FROM Links WHERE ContextUUID = ?");
				selectTemplate  = connection.prepareStatement("SELECT Template, Attribute, Type, MaxDistance FROM Templates WHERE Template = ?");
				
				deleteTemplate 	= connection.prepareStatement("DELETE FROM Templates WHERE Template = ?");
				selectTemplates = connection.prepareStatement("SELECT Template, Attribute, Type, MaxDistance FROM Templates");
				selectContext 	= connection.prepareStatement("SELECT Template, ContextUUID, Attribute, Timestamp, Type, Text, Double, Long, Boolean FROM Contexts WHERE ContextUUID = ?");
				selectContextsWithTemplate 	= connection.prepareStatement("SELECT Template, ContextUUID, Attribute, Timestamp, Type, Text, Double, Long, Boolean FROM Contexts WHERE Template = ?");
				deleteContextsWithTemplate 	= connection.prepareStatement("DELETE FROM Contexts WHERE Template = ?");				
				deleteContext 	= connection.prepareStatement("DELETE FROM Contexts WHERE ContextUUID = ?");
				deleteAllContexts = connection.prepareStatement("DELETE FROM Contexts");
				selectAllContexts 	= connection.prepareStatement("SELECT Template, ContextUUID, Attribute, Timestamp, Type, Text, Double, Long, Boolean FROM Contexts");
				return true;				
			} catch(Exception e){
				e.printStackTrace();
				return false;
			}

		}
		//*********************************
		//****DELETE CONTENT FROM ALL TABLES
		//**********************************
		public synchronized boolean deleteAllContextInfo(){
			try{
				if (this.connect() == true){
					Statement statement = connection.createStatement();
					statement.executeUpdate("DELETE FROM ContextID_Index");
					statement.executeUpdate("DELETE FROM ContextTemplateReference");
					statement.executeUpdate("DELETE FROM SubContexts");
					statement.executeUpdate("DELETE FROM Contexts");
					statement.executeUpdate("DELETE FROM ContextName");
					statement.executeUpdate("DELETE FROM Values");
					statement.executeUpdate("DELETE FROM TemplateID_Index");
					statement.executeUpdate("DELETE FROM SubContextTemplates");
					statement.executeUpdate("DELETE FROM ContextTemplateNames");
					statement.executeUpdate("DELETE FROM TemplateAttributes");
					statement.executeUpdate("DELETE FROM ValueSets");
					statement.executeUpdate("DELETE FROM Ranges");
					statement.executeUpdate("DELETE FROM Content");
					statement.executeUpdate("DELETE FROM Relations");
					statement.executeUpdate("DELETE FROM Links");
					connection.commit();
					this.disconnect();
					return true;
				}
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
			return false;
		}


		private synchronized boolean addTemplate(Template template){
			try{
				if (this.connect() == true){
					
					String templateName = template.getTemplateName();

					selectTemplate.setString(1, templateName);
					

					ResultSet resultSet = selectTemplate.executeQuery();

					if (resultSet.first() == false){
						//The template does not exist, so
						//insert one row per attribute
						Enumeration<String> e = template.getAttributes().keys();
						while (e.hasMoreElements()){
							String attributeName = e.nextElement();
							Value value = template.getAttributes().get(attributeName);
							long type = value.getType();
							Double maxDistance = template.getDistanceFilter(attributeName);
							String query = "INSERT INTO Templates Values('"+templateName+"','"+attributeName+"','"+type+"','"+maxDistance+"')";
							Statement statement = connection.createStatement();
							statement.executeUpdate(query); 								
						}
					} else{
						return false;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
			return true;
		}

		private synchronized boolean deleteTemplate(String template){
			try{
				selectContextsWithTemplate.setString(1, template);	
				ResultSet resultSet = selectContextsWithTemplate.executeQuery();
				if (resultSet.first() == false){
					deleteTemplate.setString(1, template);
					deleteTemplate.executeUpdate();
				} else{
					return false;
				}
			}catch(Exception e){
				return false;
			}
			return true;
		}

		private synchronized Template getTemplate(String template){
			if (template != null){
				Template t = new Template(template);
				try{
					selectTemplate.setString(1, template);
					ResultSet resultSet = selectTemplate.executeQuery();
					if (resultSet.first()){
						do {
							String name = resultSet.getString("Attribute");
							Long type = resultSet.getLong("Type");
							Double maxDistance = resultSet.getDouble("MaxDistance");
							t.addAttribute(name, type);
							t.setDistanceFilter(name, maxDistance);
						} while (resultSet.next());
					} 
					else{
						return t;
					}
				}catch(SQLException e){
					e.printStackTrace();
					return t;
				}
				return t;
			}
			return null;
		}

		private synchronized Templates getTemplates(){
			Templates templates = new Templates();
			Template template = new Template(UUIDGenerator.createId());
			try{
				ResultSet resultSet = selectTemplates.executeQuery();
				while (resultSet.next()){
					String id = resultSet.getString("Template");
					String name = resultSet.getString("Attribute");
					Long type = resultSet.getLong("Type");
					Double maxDistance = resultSet.getDouble("MaxDistance");

					if (template.getTemplateName().equalsIgnoreCase(id) == false){
						template = new Template(id);
						templates.addElement(template);
					}
					template.addAttribute(name, type);
					template.setDistanceFilter(name, maxDistance);
				} 
			}catch(Exception e){
				e.printStackTrace();
				return templates;
			}
			return templates;
		}

		private synchronized Context getContext(String contextUUID){
			if (contextUUID != null){
				Context context = new Context();
				try{
					selectContext.setString(1, contextUUID);
					ResultSet resultSet = selectContext.executeQuery();
					if (resultSet.first()){
						do {//221 608 235 akavemier
							context.setTemplateName(resultSet.getString("Template"));
							context.setId(resultSet.getString("ContextUUID"));
							String name = resultSet.getString("Attribute");
							long type = resultSet.getLong("Type");
							long timestamp = resultSet.getLong("Timestamp");
							if (type == Value.STRING){
								context.addAttribute(name, resultSet.getString("Text"), timestamp);
							}
							else if (type == Value.BOOLEAN){
								context.addAttribute(name, resultSet.getBoolean("Boolean"), timestamp);
							}
							else if (type == Value.DOUBLE){
								context.addAttribute(name, resultSet.getDouble("Double"), timestamp);
							}
							else if (type == Value.LONG){
								context.addAttribute(name, resultSet.getLong("Long"), timestamp);
							}
							else if (type == Value.URL){
								context.addAttribute(name, resultSet.getString("Text"), timestamp);
							}
							else if (type == Value.FILE){
								context.addAttribute(name, resultSet.getString("Text"), timestamp);
							}

						} while (resultSet.next());
					} 
					else{
						return null;
					}
				}catch(SQLException e){
					e.printStackTrace();
					return null;
				}
				return context;
			}
			return null;
		}

		private synchronized boolean addContext(Context context){
			try{
				String contextUUID = context.getId();
				selectContext.setString(1, contextUUID);
				ResultSet resultSet = selectContext.executeQuery();
				if (resultSet.first() == false){
					//The context does not exist, so
					//insert one row per attribute
					String template = context.getTemplate().getTemplateName();
					Enumeration<String> e = context.getAttributes().keys();


					while (e.hasMoreElements()){
						String attribute = e.nextElement();
						Value value = (Value) context.getAttributes().get(attribute);
						long type = value.getType();
						//TODO make while loop here
						Enumeration<Long> el = value.keys();
						while (el.hasMoreElements()){
							Long timestamp = el.nextElement();
							long longValue = Value.DEFAULT_LONG_VALUE;
							double doubleValue = Value.DEFAULT_DOUBLE_VALUE;
							String stringValue = Value.DEFAULT_STRING_VALUE;
							boolean booleanValue = Value.DEFAULT_BOOLEAN_VALUE;

							Object obj = value.get(timestamp);
							if (type == Value.STRING){
								stringValue = (String)obj;
							}
							else if (type == Value.DOUBLE){
								doubleValue = (Double)obj;		
							}
							else if (type == Value.BOOLEAN){
								booleanValue = (Boolean)obj;
							}
							else if (type == Value.LONG){
								longValue  = (Long)obj;	
							}
							else if (type == Value.URL){
								stringValue = ((URL)obj).toString();
							}
							else if (type == Value.FILE){
								stringValue  = ((File)obj).getAbsolutePath();
							}
							String query = "INSERT INTO Contexts Values('"+template+"','"+contextUUID+"','"+attribute+"','"+timestamp+"','"+type+"','"+stringValue+"','"+doubleValue+"','"+longValue+"','"+booleanValue+"')";
							Statement statement = connection.createStatement();
							statement.executeUpdate(query);
						}
						//						
						//long timestamp = value.getTimestamp();
					}
					Links links = context.getLinks();
					Enumeration<Link> e1 = links.elements();
					while (e1.hasMoreElements()){
						Link link = e1.nextElement();
						String linkUUID    = link.getLinkUUID();
						//To guarantee the contextUUID is the same as the context in both data base and runtime object
						link.setContextUUID(contextUUID);
						String href        = link.getHref();
						String hreflang    = link.getHreflang();
						String media       = link.getMedia();
						String rel         = link.getRel();
						String target      = link.getTarget();
						String type        = link.getType();
						String linkText    = link.getLinkText();
						long timestamp     = link.getTimestamp();
						// Links table columns: ContextUUID, LinkUUID, Href, Hreflang , Media , Rel , Target, Type, LinkText, Timestamp
						String query = "INSERT INTO Links Values('"+contextUUID+"','"+linkUUID+"','"+href+"','"+hreflang+"','"+media+"','"+rel+"','"+target+"','"+type+"','"+linkText+"','"+timestamp+"')";		
						Statement statement = connection.createStatement();
						statement.executeUpdate(query); 															
					}
				} else{
					return false;
				}
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
			return true;
		}

		private synchronized boolean deleteContext(String contextUUID){
			try{
				this.deleteLinks(contextUUID);
				deleteContext.setString(1, contextUUID);
				int rows = deleteContext.executeUpdate();
				if (rows == 0){
					return false;
				}
			}catch(Exception e){
				return false;
			}
			return true;
		}

		private synchronized boolean deleteAllContexts(){
			try{
				int rows = deleteAllContexts.executeUpdate();
			}catch(Exception e){
				return false;
			}
			return true;
		}

		private synchronized boolean deleteAllContexts(String templateUUID){
			try{
				deleteContextsWithTemplate.setString(1, templateUUID);
				int rows = deleteContextsWithTemplate.executeUpdate();
				return true;
			}catch(Exception e){
				return false;
			}
		}

		private synchronized Contexts getContexts(){
			Contexts contexts = new Contexts();
			Context context = new Context();
			try{
				ResultSet resultSet = selectAllContexts.executeQuery();
				while (resultSet.next()){
					String templateId = resultSet.getString("Template");
					String contextId = resultSet.getString("ContextUUID");
					String name = resultSet.getString("Attribute");
					long type = resultSet.getLong("Type");
					long timestamp = resultSet.getLong("Timestamp");

					if (context.getId().equalsIgnoreCase(contextId) == false){
						//Make new context and add it for return
						context = new Context();
						context.setId(contextId);
						context.setTemplateName(templateId);
						contexts.put(contextId, context);
						context.setLinks(this.getLinks(contextId));
					}
					// Add the attribute to the context
					if (type == Value.STRING){
						context.addAttribute(name, resultSet.getString("Text"), timestamp);
					}
					else if (type == Value.BOOLEAN){
						context.addAttribute(name, resultSet.getBoolean("Boolean"), timestamp);
					}
					else if (type == Value.DOUBLE){
						context.addAttribute(name, resultSet.getDouble("Double"), timestamp);
					}
					else if (type == Value.LONG){
						context.addAttribute(name, resultSet.getLong("Long"), timestamp);
					}
					else if (type == Value.URL){
						context.addAttribute(name, new java.net.URL(resultSet.getString("Text")), timestamp);
					}
					else if (type == Value.FILE){
						context.addAttribute(name, new File(resultSet.getString("Text")), timestamp);
					}
				} 
			}catch(Exception e){
				e.printStackTrace();
				return contexts;
			}
			return contexts;
		}

		private synchronized boolean addLink(Link link){
			try{
				String linkUUID = link.getLinkUUID();
				selectLink.setString(1, linkUUID);
				ResultSet resultSet = selectLink.executeQuery();

				if (resultSet.first() == false){
					//The link does not exist, so
					//insert the new link into the repository
					String contextUUID = link.getContextUUID();
					String href        = link.getHref();
					String hreflang    = link.getHreflang();
					String media       = link.getMedia();
					String rel         = link.getRel();
					String target      = link.getTarget();
					String type        = link.getType();
					String linkText    = link.getLinkText();
					long timestamp     = link.getTimestamp();
					// Links table columns: ContextUUID, LinkUUID, Href, Hreflang , Media , Rel , Target, Type, LinkText, Timestamp
					String query = "INSERT INTO Links Values('"+contextUUID+"','"+linkUUID+"','"+href+"','"+hreflang+"','"+media+"','"+rel+"','"+target+"','"+type+"','"+linkText+"','"+timestamp+"')";		
					Statement statement = connection.createStatement();
					statement.executeUpdate(query); 															
				} else{
					return false;
				}
			}catch(SQLException e){
				e.printStackTrace();
				return false;
			}
			return true;
		}

		private synchronized boolean updateLink(Link link){
			try{
				String linkUUID = link.getId();
				selectLink.setString(1, linkUUID);
				ResultSet resultSet = selectLink.executeQuery();

				if (resultSet.first() == true){
					//The link exists in the database, so
					//update the link in the repository
					String contextUUID = link.getContextUUID();
					String href        = link.getHref();
					String hreflang    = link.getHreflang();
					String media       = link.getMedia();
					String rel         = link.getRel();
					String target      = link.getTarget();
					String type        = link.getType();
					String linkText    = link.getLinkText();
					long timestamp     = link.getTimestamp();
					
					
					String query = "UPDATE Links SET ContextUUID='"+contextUUID+"', Href='"+href+"', Hreflang='"+hreflang+"', Media='"+media+"', Rel='"+rel+"', Target='"+target+"', Type='"+type+"', LinkText='"+linkText+"', Timestamp='"+timestamp+"' WHERE LinkUUID='"+linkUUID+"'";		

					
					Statement statement = connection.createStatement();
					statement.executeUpdate(query); 					
				} else{
					return false;
				}
			}catch(SQLException e){
				e.printStackTrace();
				return false;
			}
			return true;
		}

		private synchronized Link getLink(String linkUUID){
			if (linkUUID != null){
				Link link = new Link();
				try{
					selectLink.setString(1, linkUUID);
					ResultSet resultSet = selectLink.executeQuery();
					if (resultSet.first()){			
						link.setContextUUID(resultSet.getString("ContextUUID"));
						
						link.setLinkUUID(linkUUID);	
						
						link.setHref(resultSet.getString("Href"));
						link.setHreflang(resultSet.getString("Hreflang"));
						link.setMedia(resultSet.getString("Media"));	
						link.setRel(resultSet.getString("Rel"));
						link.setTarget(resultSet.getString("Target"));				
						link.setType(resultSet.getString("Type"));
						link.setLinkText(resultSet.getString("LinkText"));
						link.setTimestamp(resultSet.getLong("Timestamp"));
					} else{
						return null;
					}
				}catch(SQLException e){
					e.printStackTrace();
					return null;
				}
				return link;
			}
			return null;
		}
		private synchronized Links getLinks(String contextUUID){
			Links links = new Links();
			if (contextUUID != null){
				try{
					selectLinks.setString(1, contextUUID);
					ResultSet resultSet = selectLinks.executeQuery();
					while (resultSet.next()){
						Link link = new Link();
						
						link.setContextUUID(contextUUID);
						
						link.setLinkUUID(resultSet.getString("LinkUUID"));				
						link.setHref(resultSet.getString("Href"));
						link.setHreflang(resultSet.getString("Hreflang"));
						link.setMedia(resultSet.getString("Media"));	
						link.setRel(resultSet.getString("Rel"));
						link.setTarget(resultSet.getString("Target"));				
						link.setType(resultSet.getString("Type"));
						link.setLinkText(resultSet.getString("LinkText"));
						link.setTimestamp(resultSet.getLong("Timestamp"));
						
						links.put(link.getLinkUUID(), link);
					} 
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
			return links;
		}

		private synchronized boolean deleteLink(String linkUUID){
			try{
				deleteLink.setString(1, linkUUID);
				int rows = deleteLink.executeUpdate();
				if (rows == 0){
					return false;
				}
			}catch(Exception e){
				return false;
			}
			return true;
		}

		private synchronized boolean deleteLinks(String contextUUID){
			try{
				deleteLinks.setString(1, contextUUID);
				int rows = deleteLinks.executeUpdate();
				if (rows == 0){
					return false;
				}
			}catch(Exception e){
				return false;
			}
			return true;
		}

		private synchronized Links getLinks(){
			Links links = new Links();
			try{
				ResultSet resultSet = selectAllLinks.executeQuery();
				while (resultSet.next()){
					Link link = new Link();
					link.setContextUUID(resultSet.getString("ContextUUID"));
					link.setLinkUUID(resultSet.getString("LinkUUID"));				
					link.setHref(resultSet.getString("Href"));
					link.setHreflang(resultSet.getString("Hreflang"));
					link.setMedia(resultSet.getString("Media"));	
					link.setRel(resultSet.getString("Rel"));
					link.setTarget(resultSet.getString("Target"));				
					link.setType(resultSet.getString("Type"));
					link.setLinkText(resultSet.getString("LinkText"));
					link.setTimestamp(resultSet.getLong("Timestamp"));
					
					links.put(link.getLinkUUID(), link);
					
				} 
			}catch(SQLException e){
				e.printStackTrace();
				return links;
			}
			return links;
		}

		private synchronized float equalityMatch(Context source, Context target) {
			if (target.getAttributes() != null){
				if (source.equals(target)){
					return 1;
				}
				else{
					Attributes attributes = source.getAttributes();
					float n = Math.max((float)attributes.size(), target.getAttributes().size());
					float m = 0;
					Enumeration<String> e = attributes.keys();
					while (e.hasMoreElements()){
						String attribute = e.nextElement();
						Object v1 = source.getAttribute(attribute);
						Object v2 = target.getAttribute(attribute);
						if (v1 != null && v2 != null){
							if (v1.equals(v2))
								m++;
						}	
					}
					if (n == 0){
						return 0;
					}
					else{
						return (m / n);
					}
				}			
			}
			return 0;
		}

		public synchronized float distanceMatch(Context source, Context target ) {
			if (target.getAttributes() != null){
				if (source.equals(target)){
					return 1;
				}
				else{
					Template template = this.getTemplate(source.getTemplate().getTemplateName());
					Attributes attributes = source.getAttributes();
					float n = Math.max((float)attributes.size(), target.getAttributes().size());
					float m = 0;
					Enumeration<String> e = attributes.keys();
					while (e.hasMoreElements()){
						String attribute = e.nextElement();
						Object v1 = source.getAttribute(attribute);
						Object v2 = target.getAttribute(attribute);
						Double maxDistance = template.getDistanceFilter(attribute);
						if (v1 != null && v2 != null && maxDistance != null){
							m = m + Relevance.relevance(v1, v2, maxDistance.doubleValue());
						}	
					}
					if (n == 0){
						return 0;
					}
					else{
						return (m / n);
					}
				}			
			}
			return 0;
		}

		private synchronized boolean fireUpdateQueries(Vector<String> queryVector){
			try{
				connection.setAutoCommit(false);
				Statement statement=connection.createStatement();

				 
				for(int i=0; i<queryVector.size(); i++){
					statement.executeUpdate((String)queryVector.elementAt(i));
				}

				
				connection.commit();			
				 
				connection.setAutoCommit(true);
				return true;
			}catch(Exception e){
				try{
					connection.rollback();
					connection.setAutoCommit(true);
				}catch(Exception ex){
					
					return false;
				}
				return false;
			}
		}

		public synchronized Strings findContexts(Template template, Object value){
			return new Strings();
		}


		public synchronized Strings findContexts(Template template, String attribute, Object value){
			return new Strings();
		}

		public synchronized Contexts findContexts(Template template, Attributes attributes){
			return new Contexts();
			
		}

		public synchronized Contexts findContexts(Context context){
			return new Contexts();
			
		}
	}	
}
