/* 
 * Copyright 2010-2012 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.List;

import core.DTNHost;
import core.Settings;
import core.SimClock;
import core.UpdateListener;

/**
 * Reports the amount of messages in the buffer
 */
public class MessageInBufferReport extends Report implements UpdateListener {
    /**
	 * Record message every nth second -setting id ({@value}). 
	 * Defines the interval how often (seconds) a new snapshot of
	 * message is taken
	 */
	public static final String CONTENT_REPORT_INTERVAL = "messageInterval";
	/** Default value for the snapshot interval */
	public static final int DEFAULT_CONTENT_REPORT_INTERVAL = 10;
	
	private double lastRecord = Double.MIN_VALUE;
	private int interval;
	
	/**
	 * Creates a new MessageInBufferReport instance.
	 */
	public MessageInBufferReport() {
		super();
		
		Settings settings = getSettings();
		if (settings.contains(CONTENT_REPORT_INTERVAL)) {
			interval = settings.getInt(CONTENT_REPORT_INTERVAL);
		} else {
			interval = -1; /* not found; use default */
		}
		
		if (interval < 0) { /* not found or invalid value -> use default */
			interval = DEFAULT_CONTENT_REPORT_INTERVAL;
		}
	}
	
	public void updated(List<DTNHost> hosts) {
		if (SimClock.getTime() - lastRecord >= interval) {
			lastRecord = SimClock.getTime();
			
			printLine(hosts);
		}
	}
	
	/**
	 * Prints a snapshot of the message content through all nodes
	 * @param hosts The list of hosts in the simulation
	 */
	private void printLine(List<DTNHost> hosts) {
		String output = format(SimClock.getTime()) + " ";

		for (DTNHost h : hosts) {
            output += (h.getNrofMessages() + " ");
		}
		write(output);
	}
}