/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */

package applications;

import java.util.Random;

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
	/** Content generation interval */
	public static final String CONTENT_INTERVAL = "interval";
	/** Size of the content message */
	public static final String CONTENT_SIZE = "contentSize";

    /** Application ID */
	public static final String APP_ID = "fi.tkk.netlab.GeneratorApplication";

    // Private vars
    private double	lastCreation = 0;
    private double	interval = 500;
    private boolean passive = false;
    private int		contentSize = 1;

    /** 
	 * Creates a new generator application with the given settings.
	 * 
	 * @param s	Settings to use for initializing the application.
	 */
    public GeneratorApplication(Settings s) {
        if (s.contains(CONTENT_PASSIVE)){
			this.passive = s.getBoolean(CONTENT_PASSIVE);
		}
		if (s.contains(CONTENT_INTERVAL)){
			this.interval = s.getDouble(CONTENT_INTERVAL);
		}
		if (s.contains(CONTENT_SIZE)) {
			this.contentSize = s.getInt(CONTENT_SIZE);
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
		this.interval = a.getInterval();
		this.passive = a.isPassive();
		this.contentSize = a.getContentSize();
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

    /** 
	 * Generate a request.
	 * 
	 * @param host to which the application instance is attached
	 */
	@Override
	public void update(DTNHost host) {
		if (this.passive) return;
		double curTime = SimClock.getTime();
		if (curTime - this.lastCreation >= this.interval) {
			// Time to send a new content
			Message m = new Message(host, null, getId(host), getContentSize());
			m.addProperty("type", "content");
			m.setAppID(APP_ID);
			host.createNewMessage(m);
			
			// Call listeners
			// super.sendEventToListeners("SentContent", null, host);
			
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
		return "G" + host.getAddress();
	}

}