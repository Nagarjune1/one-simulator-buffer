/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */

package applications;

import java.util.Random;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import core.Application;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;
import core.SimScenario;
import core.World;

public class GeneratorApplication extends Application {
	/** Run in passive mode - don't generate content but respond */
	public static final String CONTENT_PASSIVE = "passive";
	/** Content host interval */
	public static final String CONTENT_SPREAD = "spread";
	/** Content generation interval */
	public static final String CONTENT_INTERVAL = "interval";
	/** Destination address range - inclusive lower, exclusive upper */
	public static final String CONTENT_DEST_RANGE = "destinationRange";
	/** Size of the content message */
	public static final String CONTENT_TYPE = "contentType";
	/** Size of the content message */
	public static final String CONTENT_SIZE = "contentSize";

    /** Application ID */
	public static final String APP_ID = "fi.tkk.netlab.GeneratorApplication";

    // Private vars
    private double			lastCreation = 0;
	private double			spread = 5;
    private double			interval = 500;
    private boolean 		passive = false;
	private int				destMin = 0;
	private int				destMax = 1;
    private int				contentSize = 1;
	private List<String> 	contentType = new ArrayList();

    /** 
	 * Creates a new generator application with the given settings.
	 * 
	 * @param s	Settings to use for initializing the application.
	 */
    public GeneratorApplication(Settings s) {
        if (s.contains(CONTENT_PASSIVE)){
			this.passive = s.getBoolean(CONTENT_PASSIVE);
		}
		if (s.contains(CONTENT_SPREAD)){
			this.spread = s.getDouble(CONTENT_SPREAD);
		}
		if (s.contains(CONTENT_INTERVAL)){
			this.interval = s.getDouble(CONTENT_INTERVAL);
		}
		if (s.contains(CONTENT_TYPE)){
			this.contentType = Arrays.asList(s.getSetting(CONTENT_TYPE).split("\\|"));
		}
		if (s.contains(CONTENT_SIZE)) {
			this.contentSize = s.getInt(CONTENT_SIZE);
		}
		if (s.contains(CONTENT_DEST_RANGE)){
			int[] destination = s.getCsvInts(CONTENT_DEST_RANGE,2);
			this.destMin = destination[0];
			this.destMax = destination[1];
		}

		super.setAppID(APP_ID);
	}

    /** 
	 * Copy-constructor
	 * 
	 * @param a
	 */
	public GeneratorApplication(GeneratorApplication a) {
		super(a);
		this.lastCreation = a.getLastCreation();
		this.spread = a.getSpread();
		this.interval = a.getInterval();
		this.passive = a.isPassive();
		this.destMax = a.getDestMax();
		this.destMin = a.getDestMin();
		this.contentSize = a.getContentSize();
		this.contentType = a.getContentType();
	}

	/** 
	 * Handles an incoming message.
	 * 
	 * @param msg	message received by the router
	 * @param host	host to which the application instance is attached
	 */
	@Override
	public Message handle(Message msg, DTNHost host) {
		return msg;
	}

    @Override
	public Application replicate() {
		return new GeneratorApplication(this);
	}

	public int randomInteger() {
		int randomInt = 0;
		Random rng = new Random();
		if (destMax == destMin) randomInt = destMin;
		randomInt = destMin + rng.nextInt(destMax - destMin);

		return randomInt;
	}

    /** 
	 * Generate a request.
	 * 
	 * @param host to which the application instance is attached
	 */
	@Override
	public void update(DTNHost host) {
		if (this.passive) return;
		double curTime = SimClock.getTime();
		if (curTime - this.lastCreation >= this.interval && host.getAddress() % this.spread == 0) {
			Message m = new Message(host, null, getId(host), getContentSize());
			m.addProperty("type", "content");

			// declare random destinations and target packets for interest packets
			DTNHost randDest = SimScenario.getInstance().getWorld().getNodeByAddress(randomInteger());
			m.setTo(randDest);

			// send in all the possible content types to be randomized later
			int randomIndex = (int)(Math.random() * (this.contentType.size()));
			m.addProperty("contenttype", this.contentType.get(randomIndex));
			m.setAppID(APP_ID);
			host.createNewMessage(m);
			
			// Call listeners
			super.sendEventToListeners("SentContent", null, host);
			
			this.lastCreation = curTime;
		}
	}

	/**
	 * @return the lastCreation
	 */
	public double getLastCreation() {
		return lastCreation;
	}

	/**
	 * @param lastCreation the lastCreation to set
	 */
	public void setLastCreation(double lastCreation) {
		this.lastCreation = lastCreation;
	}

	/**
	 * @return the spread
	 */
	public double getSpread() {
		return spread;
	}

	/**
	 * @param spread the spread to set
	 */
	public void setSpread(double spread) {
		this.spread = spread;
	}

	/**
	 * @return the interval
	 */
	public double getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(double interval) {
		this.interval = interval;
	}

	/**
	 * @return the passive
	 */
	public boolean isPassive() {
		return passive;
	}

	/**
	 * @param passive the passive to set
	 */
	public void setPassive(boolean passive) {
		this.passive = passive;
	}

	/**
	 * @return the destMin
	 */
	public int getDestMin() {
		return destMin;
	}

	/**
	 * @param destMin the destMin to set
	 */
	public void setDestMin(int destMin) {
		this.destMin = destMin;
	}

	/**
	 * @return the destMax
	 */
	public int getDestMax() {
		return destMax;
	}

	/**
	 * @param destMax the destMax to set
	 */
	public void setDestMax(int destMax) {
		this.destMax = destMax;
	}

	/**
	 * @return the contentType
	 */
	public List<String> getContentType() {
		return contentType;
	}

    /**
	 * @return the contentSize
	 */
	public int getContentSize() {
		return contentSize;
	}

    /**
	 * @param contentSize the contentSize to set
	 */
	public void setContentSize(int contentSize) {
		this.contentSize = contentSize;
	}

	/**
	 * @param host the host generating the content
	 * @return the id
	 */
	public String getId(DTNHost host) {
		return "M" + host.getAddress();
	}

}