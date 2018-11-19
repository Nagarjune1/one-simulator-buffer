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

public class RequestApplication extends Application {
    /** Run in passive mode - don't generate request but respond */
	public static final String REQUEST_PASSIVE = "passive";
	/** Request generation interval */
	public static final String REQUEST_INTERVAL = "interval";
    /** Destination address range - inclusive lower, exclusive upper */
	public static final String REQUEST_DEST_RANGE = "destinationRange";
    /** Seed for the app's random number generator */
	public static final String REQUEST_SEED = "seed";
    /** Size of the request message */
	public static final String REQUEST_SIZE = "requestSize";

    /** Application ID */
	public static final String APP_ID = "fi.tkk.netlab.RequestApplication";

    // Private vars
    private boolean	lastRequest = false;
    private double	interval = 500;
    private boolean passive = false;
    private int		seed = 0;
	private int		destMin=0;
	private int		destMax=1;
    private int		requestSize = 1;
	private Random	rng;

    /** 
	 * Creates a new request application with the given settings.
	 * 
	 * @param s	Settings to use for initializing the application.
	 */
    public RequestApplication(Settings s) {
        if (s.contains(REQUEST_PASSIVE)){
			this.passive = s.getBoolean(REQUEST_PASSIVE);
		}
		if (s.contains(REQUEST_INTERVAL)){
			this.interval = s.getDouble(REQUEST_INTERVAL);
		}
        if (s.contains(REQUEST_SEED)){
			this.seed = s.getInt(REQUEST_SEED);
		}
		if (s.contains(REQUEST_SIZE)) {
			this.requestSize = s.getInt(REQUEST_SIZE);
		}
        if (s.contains(REQUEST_DEST_RANGE)){
			int[] destination = s.getCsvInts(REQUEST_DEST_RANGE,2);
			this.destMin = destination[0];
			this.destMax = destination[1];
		}

        rng = new Random(this.seed);
		super.setAppID(APP_ID);
	}

    /** 
	 * Copy-constructor
	 * 
	 * @param a
	 */
	public RequestApplication(RequestApplication a) {
		super(a);
		this.lastRequest = a.getLastRequest();
		this.interval = a.getInterval();
		this.passive = a.isPassive();
		this.destMax = a.getDestMax();
		this.destMin = a.getDestMin();
        this.seed = a.getSeed();
		this.requestSize = a.getRequestSize();
        this.rng = new Random(this.seed);
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

    /** 
	 * Draws a random host from the destination range
	 * @return host
	 */
	private DTNHost randomHost() {
		int destaddr = 0;
		if (destMax == destMin) {
			destaddr = destMin;
		}
		destaddr = destMin + rng.nextInt(destMax - destMin);
		World w = SimScenario.getInstance().getWorld();
		return w.getNodeByAddress(destaddr);
	}

    @Override
	public Application replicate() {
		return new RequestApplication(this);
	}

    /** 
	 * Generate a request.
	 * @param host to which the application instance is attached
	 */
    @Override
	public void update(DTNHost host) {
        if (this.passive) return;
		if (!this.lastRequest && host.getAddress() % 2 == 0) {
			// Time to send a new content
			// prevent too much flooding of interest packets, only create if host has even address
			Message m = new Message(host, randomHost(), getId(host), getRequestSize());
			m.addProperty("type", "request");
			m.setAppID(APP_ID);
			host.createNewMessage(m);
			
			// Call listeners
			super.sendEventToListeners("SentRequest", null, host);
			
			this.lastRequest = true;
		}
    }

    /**
	 * @return the lastRequest
	 */
	public boolean getLastRequest() {
		return lastRequest;
	}

	/**
	 * @param lastRequest the lastRequest to set
	 */
	public void setLastRequest(boolean lastRequest) {
		this.lastRequest = lastRequest;
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
	 * @return the seed
	 */
	public int getSeed() {
		return seed;
	}

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(int seed) {
		this.seed = seed;
	}

    /**
	 * @return the requestSize
	 */
	public int getRequestSize() {
		return requestSize;
	}

    /**
	 * @param requestSize the requestSize to set
	 */
	public void setRequestSize(int requestSize) {
		this.requestSize = requestSize;
	}

	/**
	 * @param host the host sending the request
	 * @return the contentSize
	 */
	// TODO need new way to name interest packet
	public String getId(DTNHost host) {
		return "I" + host.getAddress();
	}

}