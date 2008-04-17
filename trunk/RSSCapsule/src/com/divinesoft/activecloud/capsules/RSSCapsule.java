package com.divinesoft.activecloud.capsules;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.ubisoa.activecloud.capsules.NotificationCapsule;
import com.ubisoa.activecloud.exceptions.CapsuleInitException;
import com.ubisoa.activecloud.exceptions.ReceiveException;
import com.ubisoa.activecloud.exceptions.StartException;
import com.ubisoa.activecloud.exceptions.StopException;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * A Capsule to report data in RSS Format
 * @version 1.0
 * @author Cesar Olea
 * */
public class RSSCapsule extends NotificationCapsule{
	private static Logger log = Logger.getLogger(RSSCapsule.class);
	private String feedType;
	private String filename;
	private String encoding;
	private SyndFeed feed;
	private String feedTitle;
	private int maxEntries;
	private static final int defaultMaxEntries = 15;
	private String timestampFormat;
	private static final String defaultTimestampFormat = "yyyy-MM-dd 'at' HH:mm:ss";
	private String author;
	private static final String defaultAuthor = "RSSCapsule";

	@Override
	public void receive(Element payload) throws ReceiveException {
		//Get the current feed entries;
		List<SyndEntry> entries = feed.getEntries();
		//The total number of elements if we combine the current entries + the new ones
		//This should never be > than maxEntries
		int numberOfElements = entries.size() + payload.getChildren().size();
		//If it is greater, we need to delete the older entries to make room for the
		//new ones
		if(numberOfElements > maxEntries){
			int exceedingElements = numberOfElements - maxEntries;
			//Loop to delete the exceeding elements
			for(int i=0; i<exceedingElements; i++){
				//This removes from the last (oldest) element to the first one
				entries.remove(maxEntries - i);
			}
		}
		
		//Add the new elements
		entries.add(nodeToEntry(payload));
		feed.setEntries(entries);
	}
	
	private SyndEntry nodeToEntry(Element node){
		//Get each <entry>
		List<Element> entries = node.getChildren();
		Iterator<Element> iterator = entries.iterator();
		//The name of the new entry
		String entryName;
		Date publishedDate = new Date();
		
		//Try to format the timestamp according to what the user provided
		try{
			SimpleDateFormat timestamp = new SimpleDateFormat(timestampFormat);
			entryName = "Producer: "+node.getAttributeValue("class").substring(
					node.getAttributeValue("class").lastIndexOf('.')+1)+ " at "+
					timestamp.format(publishedDate);
		}catch(Exception ex){
			SimpleDateFormat timestamp = new SimpleDateFormat(
					RSSCapsule.defaultTimestampFormat);
			entryName = "Producer: "+node.getAttributeValue("class").substring(
					node.getAttributeValue("class").lastIndexOf('.')+1)+ " at "+
					timestamp.format(publishedDate);
		}
		
		SyndEntry newEntry = new SyndEntryImpl();
		newEntry.setTitle(entryName);
		newEntry.setPublishedDate(publishedDate);
		newEntry.setAuthor(author);
		
		SyndContent content = new SyndContentImpl();
		
		while(iterator.hasNext()){
			Element e = iterator.next();
			String c = "<p>Name: " +  e.getAttributeValue("name") +
				" Value: " + e.getValue() + "</p></br />";
			content.setValue(c);
		}
		
		return newEntry;
	}

	public void init(Element e) throws CapsuleInitException {
		final Element config = e.getChild("nc");
		feedType = config.getAttributeValue("feedtype");
		filename = config.getAttributeValue("filename");
		encoding = config.getAttributeValue("encoding");
		feedTitle = config.getAttributeValue("feedtitle");
		author = config.getAttributeValue("author");
		
		if(author == null){
			author = RSSCapsule.defaultAuthor;
		}
		
		try{
			maxEntries = Integer.parseInt(config.getAttributeValue("maxentries"));
		}catch(NumberFormatException ex){
			log.debug("Setting maxEntries to default value");
			maxEntries = RSSCapsule.defaultMaxEntries;
		}
		
		//See if the feed is already created and if not, create it
		final File feedFile = new File(filename);
		if(feedFile.isDirectory()){
			throw new CapsuleInitException("The filename for the RSS file is a directory",
					RSSCapsule.class.getName());
		}
		
		if(!feedFile.exists()){
			createNewFeed(feedFile);
			feed.setFeedType(feedType);
			feed.setEncoding(encoding);
			feed.setTitle(feedTitle);
		}else{
			feed = loadFeed();
		}
	}

	public void start() throws StartException {
		// TODO Auto-generated method stub
		
	}

	public void stop() throws StopException {
		// TODO Auto-generated method stub
		
	}
	
	private void createNewFeed(File feedFile){
		feed = new SyndFeedImpl();
	}
	
	private SyndFeed loadFeed(){
		final SyndFeedInput input = new SyndFeedInput();
		SyndFeed newFeed = null;
		try{
			final ClassLoader loader = this.getClass().getClassLoader();
			newFeed = input.build(new XmlReader(loader.getResource(filename)));	
		}catch(final FeedException fe){
			log.error(fe.getMessage());
		}catch(final IOException ioe){
			log.error(ioe.getMessage());
		}
		return newFeed;
	}

}
