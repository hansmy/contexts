
package com.ambiesense.application;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import com.ambiesense.context.Context;
import com.ambiesense.context.ContextAccess;
import com.ambiesense.context.ContextSpace;
import com.ambiesense.context.Template;
import com.ambiesense.context.Link;
import com.ambiesense.context.User;
import com.ambiesense.util.Links;
import com.ambiesense.util.Relevance;
import com.ambiesense.util.Templates;
import com.ambiesense.util.Contexts;

/**
 * @author juanmillan85
 * @version 0.01
 */
public class ContextDemoApp {

	private long timeBefore = 0;
	private long timeAfter = 0;
	private long timeUsed = 0;
	private Template _ct;
	private User myUser;

	//declare contextSpace
	private ContextSpace contextSpace;
	//declare context middleware
	private ContextAccess cmInstance;

	public static void main(String args[]) {
		new ContextDemoApp();
	}

	public ContextDemoApp() {
		try {
			//The user is created
			myUser= new User("Hans", "");
			System.out.println("Test start...");
			//Create connection with h2 database
			cmInstance = ContextAccess.getInstance(myUser);
			System.out.println("Here.");
			// Context space abstraction reflects  Context history, Current Context, and Future Context
			contextSpace = ContextSpace.getInstance(myUser);
			System.out.println("Start buildTemplate.");
			//Creates  an empty simple location template
			Template template = this.buildTemplate();
			System.out.println("TEMPLATE IS NULL: "+ (template == null));
			// Create a location context
			Context c1 = buildContext(template);
			//Creates  an empty simple interest template
			Template template2 = this.buildTemplate2();
			System.out.println("TEMPLATE2 IS NULL: "+ (template2 == null));
			// Create a interest context
			Context c2 = buildContext2(template2);
			System.out.println("C2 IS NULL: "+ (c2 == null));
			// Context Space adds the location and interest templates
			contextSpace.addTemplate(template);
			contextSpace.addTemplate(template2);

			Templates all = contextSpace.getTemplates();
			System.out.println("Showing all the Templates.");
			int n = all.size();
			for (int i = 0; i < n; i++){
				//printing each template
				Template t = (Template)all.elementAt(i);
				System.out.println(t);
			}
			System.out.println("Done getTemplates.");
			System.out.println("Start build contexts");
			//Creating context attributes dynamically
			Link l1 = new Link();
			l1.setContextUUID(c1.getId());
			l1.setHref("http://www.ambiesense.com");
			l1.setHreflang("English");
			l1.setMedia("for mobile");	
			l1.setRel("undefined");
			l1.setTarget("this");				
			l1.setType("text/html");
			l1.setLinkText("AmbieSense");
			l1.setTimestamp(459409405l);
			//Adding a new attribute for the location context
			c1.addLink( l1);
			
			//Creating context attributes dynamically
			Link l2 = new Link();
			l2.setContextUUID(c2.getId());
			l2.setHref("http://citeseerx.ist.psu.edu");
			l2.setHreflang("English");
			l2.setMedia("for tablet");	
			l2.setRel("undefined");
			l2.setTarget("this");				
			l2.setType("text/html");
			l2.setLinkText("Juanes");
			l2.setTimestamp(450353934499l);
			//Adding a new attribute for the interest context
			c2.addLink(l2);
			//Creating context attributes dynamically
			Link l3 = new Link();
			l3.setContextUUID(c2.getId());
			l3.setHref("http://www.youtube.com/watch?v=Y-4fo1mjvsA");
			l3.setHreflang("Spanish");
			l3.setType("text/html");
			l3.setLinkText("CiteSeerX");
			l3.setTimestamp(60968484499l);
			//Adding a new attribute for the interest context
			c2.addLink(l3);

			//contextSpace.addLink(l2);

			System.out.println("Done build contexts");
			System.out.println("Start add contexts");
			//Adding the location context to the context space
			System.out.println("Adding new context c1: " + contextSpace.addContext(c1));
			//Adding the interest context to the context space
			System.out.println("Adding new context c2: " + contextSpace.addContext(c2));
			System.out.println(c1);
			System.out.println(c2);
			System.out.println("Done add contexts");
			
			System.out.println("--All contexts----------");
			
			
			System.out.println("Context equality match = "+contextSpace.equalityMatch(c1, c2));
			System.out.println("Context distance match = "+contextSpace.distanceMatch(c1, c2));



			Object value5 = c1.getAttribute("Time");
			Object value6 = c2.getAttribute("Time");
			System.out.println("Relevance= "+ Relevance.relevance(value5, value6, (int)10));

			Object value7 = c1.getAttribute("Text");
			Object value8 = c2.getAttribute("Text");
			System.out.println("Relevance= "+ Relevance.relevance(value7, value8, (int)4));

			System.out.println("Reading all links as html...)");		
			System.out.println(contextSpace.getLinks().toHtml());
			System.out.println("Reading all links as string...)");		
			System.out.println(contextSpace.getLinks().toString());

			
			System.out.println("Printing out all contexts:");
			Contexts contexts = contextSpace.getContexts();

			System.out.println(contexts.toString());
			System.out.println("Printing all contexts");

			System.out.println("Test completed.)");		

			contextSpace.closeConnection();
			cmInstance.disconnect();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			if (contextSpace != null) contextSpace.closeConnection();
			if (cmInstance != null) cmInstance.disconnect();
			System.exit(0);
		}
	}
	
	private Template buildTemplate() {
		/*This is a possible template to model context location.*/
		Template template = new Template("content");
		
		template.addLongAttribute("content.context.spatio-temporal.location.longitude");
		template.addLongAttribute("content.context.spatio-temporal.location.lattitude");
		template.addStringAttribute("content.name");
		template.addLongAttribute("content.context.spatio-temporal.location.time");
		template.addUrlAttribute("content.resource");
		template.addStringAttribute("content.context.environment.postcode");
		template.setDistanceFilter("content.context.spatio-temporal.location.longitude", 0);
		template.setDistanceFilter("content.name", 3);
		template.setDistanceFilter("content.context.spatio-temporal.location.lattitude", 0);
		template.setDistanceFilter("content.resource", 0);
		template.setDistanceFilter("content.context.environment.postcode", 0);
		System.out.println(template.toString());
		return template;
	}

	private Context buildContext(Template template){
		System.out.println("Template: "+template);
		Context context = new Context(template);
		URL url=null;
		try {
			url = new URL("http://www.city.ac.uk");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.updateAttribute("content.context.spatio-temporal.location.longitude",(long)2);
		context.updateAttribute("content.context.spatio-temporal.location.lattitude",(long)342);
		context.updateAttribute("content.context.spatio-temporal.location.time", (long)123);
		context.updateAttribute("content.name", "City University");
		context.updateAttribute("content.resource",url );
		context.updateAttribute("content.context.environment.postcode","EC1V 0HB" );
		
		System.out.println("Context: "+context);
		return context;
	}
	
	private Template buildTemplate2() {

		Template template = new Template("user");
		template.addLongAttribute("user.context.spatio-temporal.location.longitude");
		template.addLongAttribute("user.context.spatio-temporal.location.lattitude");
		template.addLongAttribute("user.context.spatio-temporal.location.time");
		template.addStringAttribute("user.context.personal.profession");
		template.addBooleanAttribute("user.context.personal.shide");
		template.addBooleanAttribute("user.context.personal.geek");
		template.addBooleanAttribute("user.context.personal.bore");
		template.addUrlAttribute("user.resource");
		System.out.println(template.toString());
		return template;
	}
	private Context buildContext2(Template template){
		System.out.println("Template: "+template);
		URL url=null;
		try {
			url = new URL("http://www.city.ac.uk/informatics/school-organisation/department-of-information-science/faculty/dr-ayse-goker");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Context context = new Context(template);
		context.updateAttribute("user.context.spatio-temporal.location.longitude",(long)2);
		context.updateAttribute("user.context.spatio-temporal.location.lattitude",(long)21);
		context.updateAttribute("user.context.spatio-temporal.location.time",(long)22);
		context.updateAttribute("user.context.personal.profession","Computer Science");;
		context.updateAttribute("user.context.personal.shide", true);
		context.updateAttribute("user.context.personal.geek", true);
		context.updateAttribute("user.context.personal.bore", false );
		context.updateAttribute("user.resource",url );
		System.out.println("Context: "+context.toString());
		return context;
	}
	

}