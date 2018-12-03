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
	/** Request host interval */
	public static final String REQUEST_SPREAD = "spread";
	/** Request generation interval */
	public static final String REQUEST_INTERVAL = "interval";
    /** Seed for the app's random number generator */
	public static final String REQUEST_SEED = "seed";
    /** Size of the request message */
	public static final String REQUEST_SIZE = "requestSize";

    /** Application ID */
	public static final String APP_ID = "fi.tkk.netlab.RequestApplication";

    // Private vars
    private double	lastRequest = 0;
	private double	spread = 5;
    private double	interval = 500;
    private boolean passive = false;
    private int		seed = 0;
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
		if (s.contains(REQUEST_SPREAD)){
			this.spread = s.getDouble(REQUEST_SPREAD);
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
		this.spread = a.getSpread();
		this.interval = a.getInterval();
		this.passive = a.isPassive();
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
		double curTime = SimClock.getTime();
		if (curTime - this.lastRequest >= this.interval && host.getAddress() % this.spread == 0) {
			Message m = new Message(host, null, getId(host), getRequestSize());
			m.addProperty("type", "request");
			m.setAppID(APP_ID);
			host.createNewMessage(m);
			
			// Call listeners
			super.sendEventToListeners("SentRequest", null, host);
			
			this.lastRequest = curTime;
		}
    }

    /**
	 * @return the lastRequest
	 */
	public double getLastRequest() {
		return lastRequest;
	}

	/**
	 * @param lastRequest the lastRequest to set
	 */
	public void setLastRequest(double lastRequest) {
		this.lastRequest = lastRequest;
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