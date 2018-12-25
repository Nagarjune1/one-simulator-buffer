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
	/** Destination address range - inclusive lower, exclusive upper */
	public static final String REQUEST_DEST_RANGE = "destinationRange";
    /** Size of the request message */
	public static final String REQUEST_SIZE = "requestSize";

    /** Application ID */
	public static final String APP_ID = "fi.tkk.netlab.RequestApplication";

    // Private vars
    private double	lastRequest = 0;
	private double	spread = 5;
    private double	interval = 500;
	private int		destMin = 0;
	private int		destMax = 1;
    private boolean passive = false;
    private int		requestSize = 1;

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
		if (s.contains(REQUEST_SIZE)) {
			this.requestSize = s.getInt(REQUEST_SIZE);
		}
		if (s.contains(REQUEST_DEST_RANGE)){
			int[] destination = s.getCsvInts(REQUEST_DEST_RANGE,2);
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
	public RequestApplication(RequestApplication a) {
		super(a);
		this.lastRequest = a.getLastRequest();
		this.spread = a.getSpread();
		this.interval = a.getInterval();
		this.passive = a.isPassive();
		this.destMax = a.getDestMax();
		this.destMin = a.getDestMin();
		this.requestSize = a.getRequestSize();
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

	public int randomInteger() {
		int randomInt = 0;
		Random rng = new Random();
		if (destMax == destMin) randomInt = destMin;
		randomInt = destMin + rng.nextInt(destMax - destMin);

		return randomInt;
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

			// declare random destinations and target packets for interest packets
			DTNHost randDest = SimScenario.getInstance().getWorld().getNodeByAddress(randomInteger());
			m.setTo(randDest);
			m.addProperty("target", "M" + randomInteger());
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