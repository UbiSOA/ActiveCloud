package com.divinesoft.activecloud.capsules;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import ubisoa.activecloud.capsules.NotificationCapsule;
import ubisoa.activecloud.exceptions.CapsuleInitException;
import ubisoa.activecloud.exceptions.ReceiveException;
import ubisoa.activecloud.exceptions.StartException;
import ubisoa.activecloud.exceptions.StopException;

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
	
	@Override
	public void receive(final byte[] payload) throws ReceiveException {
		// TODO Auto-generated method stub
		log.debug("Not implemented");
	}

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
		
	}
	
	private SyndEntry nodeToEntry(Element node){
		SyndEntry newEntry = new SyndEntry();
	}

	@Override
	public void init(final Element e) throws CapsuleInitException {
		final Element config = e.getChild("nc");
		feedType = config.getAttributeValue("feedtype");
		filename = config.getAttributeValue("filename");
		encoding = config.getAttributeValue("encoding");
		feedTitle = config.getAttributeValue("feedtitle");
		
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

	@Override
	public void start() throws StartException {
		// TODO Auto-generated method stub
		
	}

	@Override
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
