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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Observable;
import java.util.Vector;


public final class ContextAccess extends Observable implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final ContextAccess CONTEXTACCESS = new ContextAccess(); 
	private static ContextAccessStorage contextAccess = new ContextAccessStorage();	

	/**
	 * Private constructor that suppresses the default public constructor to avoid multiple instances.
	 * We only want one instance of the context access running 
	 * therefore we have a private constructor
	 */
	private ContextAccess() {	
	}
	/**
	 * Method to get the instance of the ContextAccess.
	 * @param a user of the ContextAccess.
	 * @return returns the instance if the user is validated, otherwise false
	 * 
	 */
	public static synchronized ContextAccess getInstance(User owner) {	
		try{
			//if (connect()){
				if(contextAccess.isOwner(owner)||contextAccess.isDefaultUser(owner)){	
					return CONTEXTACCESS;
				}			
			//}
			return null;
		} catch (Exception e){
			return null;
		}
	}
	/**
	 * Changes the owner of the context access
	 * @param oldOwner the current owner of the context access
	 * @param newOwner the new owner of context access
	 * @throws IllegalArgumentException if something is wcontext accessameters (e.g. oldOwner is wrong, or newOwner already exist)
	 */	
	public static synchronized boolean changeOwner(User oldOwner, User newOwner) {
		return contextAccess.changeOwner(oldOwner, newOwner);
	}

	/**
	 * Gives a user access right to the context space
	 * @param user the user to set
	 * @return true if user is successfully added, false if not 
	 * @throws IllegalArgumentException if user name of user already exist
	 */		
	public static synchronized boolean addUser(User user){
		return contextAccess.addUser(user);	
	}

	/**
	 * Creates a user group to the context space. Users in the user group will have access rights to the context space.
	 * @param userGroupName the name of the user group
	 * @return true if usergroup is successfully created, false if not 
	 */
	public static synchronized boolean addUserGroup(UserGroup group){
		return contextAccess.addUserGroup(group);	
	}

	/**
	 * Adds a user to a user group. The user must already exist to be added to a usergroup. therefore make sure
	 * to store the user using the <code>registerUserToContextSpace(User user)</code> method before
	 * ading to a user group.
	 * @param userName the user to add
	 * @param userGroupName the user group where the user should be added
	 * @return true if user is successfully added, false if not 
	 * @throws IllegalArgumentException if user name of user already exist
	 */		
	public static synchronized boolean addUserToGroup(String userName, String userGroupName) throws IllegalArgumentException{
		return contextAccess.addUserToGroup(userGroupName, userName);
	}
	/**
	 * Removes a user's access rights to the context space
	 * @param user the user to remove
	 * @return true if user is successfully removed, false if not 
	 */			
	public static synchronized boolean deleteUser(User user){
		return contextAccess.deleteUser(user);	
	}

	/**
	 * Removes a usergroup from the context space.
	 * NOTE: The users in the usergroup are not deleted.  
	 * @param userGroupName the name of the usergroup to remove
	 * @return true if usergroup is removed, false if not
	 */
	public static synchronized boolean deleteUserGroup(String userGroupName){
		return contextAccess.deleteUserGroup(userGroupName);
	}


	/**
	 * Returns a user group (including users if any) from the context space.
	 * @param userGroupName the name of the usergroup tp retrieve 
	 * @return the usergroup if it exists
	 */
	public static synchronized UserGroup getUserGroupFromContextSpace(String userGroupName){
		return contextAccess.getUserGroup(userGroupName);	
	}

	/**
	 * Returns a user from the context space.
	 * @param userName the name of the user to retrieve 
	 * @return the user if it exists
	 */
	public static synchronized User getUser(String userName){
		return contextAccess.getUser(userName);	
	}


	/**
	 * Returns the context space owner 
	 */
	public static synchronized User getOwner(){
		return contextAccess.getOwner();	
	}
	/**
	 * Returns true if the user login already exists.
	 * @param user the user to check
	 * @return true if user exists, false if not 
	 */
	public synchronized boolean exists(User user){
		return contextAccess.exists(user);	
	}
	/**
	 * Returns true if the user group already exists.
	 * @param user the user to check
	 * @return true if user exists, false if not 
	 */
	public static synchronized boolean exists(UserGroup group){
		return contextAccess.exists(group);	
	}

	/**
	 * Method to close the Amber connection.
	 * @return returns true if it was successfully closed, otherwise false
	 */
	public static synchronized void disconnect(){	
		contextAccess.disconnect();
	}

	/**
	 * Use this method when you are starting to use the context software. 
	 * @return true if context space opens correctly, false if not 
	 */
	public static boolean connect(){	
		
		return contextAccess.connect();
	}

	/**
	 * Deletes all users from the context space 
	 */
	public static synchronized void deleteAllUsers(){	
		contextAccess.deleteAllUsers();
	}


	/**
	 * Check to see if context software is closed
	 * @return true if context software is closed, false if it is open 
	 */
	public static boolean isClosed(){
		return contextAccess.isClosed();
	}

	/*
	 * Private class that handles the context softwares database operations  
	 * 
	 */
	private static class ContextAccessStorage{

		private String dataBaseURL ;
		private Connection connection;

		protected ContextAccessStorage(){
			//try to open connection
			try{
				this.connect();
			}catch(Exception e){
				System.out.println("Could not open context access data base, because:");
				e.printStackTrace();
			}
		}

		protected synchronized boolean isClosed(){
			try{
				return connection.isClosed();
			}catch(SQLException e){
				return true;
			}
		}

		protected synchronized void createDataBaseTables(){
			try{
				Vector<String> queries = new Vector<String>();
				final String text       	= "VARCHAR(50)";
				final String integer    	= "INTEGER";

				String createContextSpaceUsersTable ="CREATE TABLE IF NOT EXISTS ContextSpaceUsers" +
				"(UniqueID "+integer+",UserID "+text+" PRIMARY KEY, Login "+text+", Password "+text+" )"; 

				String createUserAndUserGroupsTable ="CREATE TABLE IF NOT EXISTS UserAndUserGroups" +
				"(UserID "+text+" PRIMARY KEY, UserGroupID "+text+")"; 


				String createTableUserGroups = "CREATE TABLE IF NOT EXISTS UserGroups" +
				"(UniqueID "+integer+", UserGroupID "+text+" PRIMARY KEY, UserGroupName "+text+")";

				String createTableContextMiddlewareOwner = "CREATE TABLE IF NOT EXISTS ContextMiddlewareOwner" +
				"(OwnerID "+text+" PRIMARY KEY, Login "+text+", Password "+text+")";

				String createTableDefaultContextSpaceUser = "CREATE TABLE IF NOT EXISTS DefaultContextSpaceUser" +
				"(ContextSpaceUserID "+text+", ContextSpaceLogin "+text+", ContextSpacePassword "+text+")";

				queries.add(createContextSpaceUsersTable);
				queries.add(createUserAndUserGroupsTable);
				queries.add(createTableUserGroups);
				queries.add(createTableContextMiddlewareOwner);		
				queries.add(createTableDefaultContextSpaceUser);		

				fireUpdateQueries(queries);			
			} catch (Exception e){}

		}

		protected synchronized boolean deleteAllUsers(){
			try{
				if (connection != null){
					Statement statement = connection.createStatement();		
					statement.executeUpdate("DELETE FROM ContextSpaceUsers");
					statement.executeUpdate("DELETE FROM UserGroups");
					statement.executeUpdate("DELETE FROM UserAndUserGroups");
					connection.commit();
					return true;	
				} 		
			}catch(Exception e){
				return false;
			}
			return false;
		}

		protected boolean connect(){
			try{
				// Load the JDBC driver
				String driverName =    "org.h2.Driver";// H2 JDBC driver
				Class.forName(driverName); 
				// Create a connection to the database
				dataBaseURL = "jdbc:h2:~/contexts"; 
				System.out.println("CONNECTING TO CONTEXT ACCESS DATABASE...");
				connection = DriverManager.getConnection(dataBaseURL, "sa", "");
				createDataBaseTables();
				createDefaultOwner();
				createDefaultUser();
				System.out.println("SUCCESSFULLY CONNECTED TO CONTEXT ACCESS DATABASE");
				return true;
			} catch(Exception e){
				return false;
			}
		}


		protected synchronized void disconnect(){
			try{
				connection.close();

			}catch(Exception e){
				e.printStackTrace();
			}
		}

		protected synchronized boolean createDefaultOwner(){
			try{
				
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement .executeQuery("SELECT MAX(OwnerID) FROM ContextMiddlewareOwner");
				while(resultSet.next()){
					String userID = resultSet.getString(1);
					if(userID == null){
						statement.executeUpdate("INSERT INTO ContextMiddlewareOwner Values('1','Hans','')");
						return true;
					}
				}
				return false;
			}catch(Exception e){
				return false;
			}		
		}

		protected synchronized boolean createDefaultUser(){
			try{
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT MAX(ContextSpaceUserID) FROM DefaultContextSpaceUser");
				while(resultSet.next()){
					String userID = resultSet.getString(1);
					if(userID == null){
						statement.executeUpdate("INSERT INTO DefaultContextSpaceUser Values('1','F5ghjbd/883','kd6473i9jash')");
						return true;
					}
				}
				return false;
			}catch(Exception e){
				return true;
			}		
		}

		protected synchronized boolean isOwner(User user){
			try{
				String userLogin = user.getLogin();
				String userPassword = user.getPassword();
				String query = "SELECT OwnerID FROM ContextMiddlewareOwner WHERE Login='"+userLogin+"' AND Password='"+userPassword+"'";
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query);
				while(resultSet.next()){
					String contextID = resultSet.getString(1);
					if(contextID!=null){
						return true;		
					}	
				}
				return false;
			}catch(Exception e){
				return false;
			}
		}

		protected synchronized boolean isDefaultUser(User user){
			try{
				String userLogin = user.getLogin();
				String userPassword = user.getPassword();
				String defaultUserQuery ="SELECT ContextSpaceUserID FROM DefaultContextSpaceUser WHERE ContextSpaceLogin='"+userLogin+"' AND ContextSpacePassword='"+userPassword+"'";
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(defaultUserQuery);
				while(resultSet.next()){
					String contextID = resultSet.getString(1);
					if(contextID != null){
						return true;	
					}	
				}
				return false;
			}catch(Exception e){
				return false;
			}
		}

		protected synchronized boolean changeOwner(User oldOwner, User newOwner){
			try{
				String newUserName = newOwner.getLogin();
				String newUserPassword = newOwner.getPassword();
				if(isOwner(oldOwner) == true){ 
					if (!exists(newOwner)){
						Statement statement = connection.createStatement();
						statement.executeUpdate("UPDATE ContextMiddlewareOwner SET Login='"+newUserName+"', Password='"+newUserPassword+"' WHERE OwnerID='1'");
						return true;
					} else {
						return false; 
					}
				}
				return false; 
			}catch(Exception e){
				return false;
			}
		}

		/**
		 * @return true if adding succeeded, otherwise false
		 * 
		 */
		protected synchronized boolean addUser(User user) {
			try{
				String userName = user.getLogin();
				String password = user.getPassword();
				String newUserID="";
				int id =0;

				if(userName!="" || password!=""){
					if(!exists(user)){
						//find a new user id
						Statement statement = connection.createStatement();
						ResultSet resultSet = statement.executeQuery("SELECT MAX(UniqueID)  FROM ContextSpaceUsers");
						while(resultSet.next()){
							id = resultSet.getInt(1);
							if(id!=0){
								id++;
								newUserID = String.valueOf(id);
								statement.executeUpdate("INSERT INTO ContextSpaceUsers Values("+id+",'"+newUserID+"','"+userName+"','"+password+"')");	
								return true;
							}else{
								statement.executeUpdate("INSERT INTO ContextSpaceUsers Values("+1+",'1','"+userName+"','"+password+"')");	
								return true;
							}
						}
					}else{
						return false;
					}
				}else if (userName=="" || password!=""){
					// TODO Test if username is an email
					return false;
				}
			}catch(Exception e){
				return false;
			}				
			return false;
		}
		/**
		 * @return true if registration succeeds, false if not
		 */
		protected synchronized boolean addUserGroup(UserGroup group){
			try{
				String groupID="";
				int id = 0;
				if(!exists(group)){

					Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery("SELECT MAX(UniqueID) FROM UserGroups");
					while(resultSet.next()){
						id = resultSet.getInt(1);						 
					}
					if(id!=0){
						id++;
						groupID = String.valueOf(id);
						statement.executeUpdate("INSERT INTO UserGroups VALUES("+id+",'"+groupID+"','"+group+"')");	
						return true;
					}else{
						id=1;
						statement.executeUpdate("INSERT INTO UserGroups VALUES("+id+",'1','"+group+"')");	
						return true; 
					}
				}		
			}
			catch(Exception e){
				return false;
			}
			return false;
		}

		protected boolean exists(UserGroup group){
			//check if user group exists from before
			try{
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT UserGroupID FROM UserGroups WHERE UserGroupName='"+group.getName()+"'");
				while(resultSet.next()){
					String groupID = resultSet.getString(1);
					if(groupID != null){
						return true;			
					}
				}
				return false;
			}catch(Exception e){
				return false;
			}
		}

		public synchronized boolean deleteUser(User user){
			try{
				String userName = user.getLogin();
				String userID="";
				Statement statement = connection.createStatement();
				//get the user id
				ResultSet resultSet = statement.executeQuery("SELECT UserID FROM ContextSpaceUsers WHERE Login='"+userName+"'");
				while(resultSet.next()){
					userID = resultSet.getString(1);
				}

				statement.executeUpdate("DELETE FROM ContextSpaceUsers WHERE Login ='"+userName+"'");		
				statement.executeUpdate("DELETE FROM UserAndUserGroups WHERE UserID='"+userID+"'");
				return true;
			}catch(Exception e){
				return false;
			}
		}

		protected synchronized boolean deleteUserGroup(String userGroupName){
			try{			
				String userGroupID="";
				Statement statement = connection.createStatement();

				//get the user group id
				ResultSet resultSet = statement.executeQuery("SELECT UserGroupID FROM UserGroups WHERE UserGroupName='"+userGroupName+"'");
				while(resultSet.next()){
					userGroupID = resultSet.getString(1);
				}
				if(userGroupID!=null){
					statement.executeUpdate("DELETE FROM UserGroups WHERE UserGroupName ='"+userGroupName+"'");		
					statement.executeUpdate("DELETE FROM UserAndUserGroups WHERE UserGroupID='"+userGroupID+"'");	
				}
				return true;
			}catch(Exception e){
				return false;
			}
		}

		protected synchronized UserGroup getUserGroup(String userGroupName){
			UserGroup userGroup = new UserGroup(""); 
			try{			
				int userGroupID=0;
				String userName="";
				int userID =0;
				String userPassword="";
				boolean idAndNameSet=false;
				User user;
				Statement statement = connection.createStatement();

				ResultSet resultSet = statement.executeQuery("SELECT ug.UserGroupID, csu.UserID, csu.Login, csu.Password "+
						"FROM ContextSpaceUsers as csu, UserAndUserGroups as uaug, UserGroups as ug "+
						"WHERE ug.UserGroupName='"+userGroupName+"' AND ug.UserGroupID=uaug.UserGroupID AND csu.UserID = uaug.UserID");
				while(resultSet.next()){

					userGroupID = resultSet.getInt(1);
					userID = resultSet.getInt(2);
					userName = resultSet.getString(3);
					userPassword = resultSet.getString(4);

					if(!idAndNameSet && userGroupID!=0){
						userGroup = new UserGroup(userGroupName);
						userGroup.setID(userGroupID, this);
						idAndNameSet = true;
					}
					if(userID!=0 && userName!=null && userPassword!=null){//alle varaiblene har verdier 
						user = new User(userName, userPassword);
						user.setUserID(userID, this);
						userGroup.add(user);
					}				
				}
				return userGroup;
			}catch(Exception e){
				return userGroup;
			}
		}

		protected boolean addUserToGroup(String userGroupName, String login){
			//boolean addSuccess= false;
			try{
				String userID = "";
				String userGroupID = "";
				boolean userIsSet = false;
				//hent bruker id 

				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT UserID FROM ContextSpaceUsers WHERE Login='"+login+"'");

				while(resultSet.next()){
					userID = resultSet.getString(1);	

				}
				if(userID != null){
					//finn id til usergroup			
					resultSet = statement.executeQuery("SELECT UserGroupID FROM UserGroups WHERE UserGroupName='"+userGroupName+"'");

					while(resultSet.next()){			
						userGroupID = resultSet.getString(1); 				
					}
				}
				//sjekk om brukeren er lagt til denne gruppen fra f�r 
				if(userGroupID!=null){
					//legg til
					resultSet = statement.executeQuery("SELECT UserID FROM UserAndUserGroups WHERE UserGroupID ='"+userGroupID+"'");
					while(resultSet.next()){
						userIsSet = true;
					}					
				}		
				//legg til hvis testVar er null som betyr at denne gruppe id ikke eksiterer for dene bruker
				if(!userIsSet){
					statement.executeUpdate("INSERT INTO UserAndUserGroups VALUES('"+userID+"', '"+userGroupID+"')");			
					return true;
				}
			}catch(Exception e){
				return false; 
			}
			return false;
		}

		protected synchronized User getUser(String userName){
			User user = new User("","");
			try{
				int userID=0;
				String login = "";
				String password ="";

				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT * FROM ContextSpaceUsers WHERE Login='"+userName+"'");

				while(resultSet.next()){
					userID = resultSet.getInt(1);
					login = resultSet.getString(2);
					password = resultSet.getString(3);
					if(userID!=0 && login!=null && password!=null){
						user = new User(login, password);
						user.setUserID(userID, this);
					}
				}
				return user;
			}catch(Exception e){
				return user;
			}		
		}

		//returns null if user does not exist
		protected synchronized User getOwner(){
			User user = new User("","");	
			try{
				int userID=0;
				String login = "";
				String password ="";

				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT * FROM ContextMiddlewareOwner");

				while(resultSet.next()){
					userID = resultSet.getInt(1);
					login = resultSet.getString(2);
					password = resultSet.getString(3);
					if(userID!=0 && login!=null && password!=null){
						user = new User(login, password);
						user.setUserID(userID, this);
					}
				}	
				return user;
			}catch(Exception e){
				return user;
			}	
		}

		//returns true if a user name is already in use 
		protected boolean exists(User user){
			try{
				String userName = user.getLogin();
				Statement statement = connection.createStatement();
				//check the user table 
				ResultSet resultSet = statement.executeQuery("SELECT UserID FROM ContextSpaceUsers WHERE Login='"+userName+"'");
				while(resultSet.next()){
					String userID = resultSet.getString(1);
					if(userID != null){
						return true;
					}	
				}
				//check the owner table
				resultSet = statement.executeQuery("SELECT OwnerID FROM ContextMiddlewareOwner WHERE Login='"+userName+"'");
				while(resultSet.next()){
					String ownerID = resultSet.getString(1);
					if(ownerID != null){
						return true;
					}	
				}
			}catch(Exception e){
				return false;
			}
			return false;
		}

		protected boolean fireUpdateQueries(Vector<String> queryVector){
			try{
				connection.setAutoCommit(false);//sl� av autocommitt slik at vi kan gj�re en rollback dersom noe feiler under utf�relsen
				Statement statement=connection.createStatement();

				//s� lenge det er queryer i vector, fyr i vei 
				for(int i=0; i<queryVector.size(); i++){
					//System.out.println((String)queryVector.elementAt(i));
					statement.executeUpdate((String)queryVector.elementAt(i));
					System.out.println("Executed: "+ (String)queryVector.elementAt(i));
				}				
				//commit
				connection.commit();			
				//har vi kommet hit s� er allt vel fra databasens sitt synspunkt 

				connection.setAutoCommit(true);
				//this.disconnect();
				return true;				
			}catch(Exception e){
				try{
					connection.rollback();//gj�r en rollback dersom noe feiler, slik at ingen av operasjonene vi har planlagt blir utf�rt
					connection.setAutoCommit(true); //sl� p� autocommitt igjen
					return false;
				}catch(SQLException ex){
					//do nothing
					return false;
				}
			}
		}

	}//end private class 

}//end class


