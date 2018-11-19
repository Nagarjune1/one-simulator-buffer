/* 
 * Copyright 2010-2012 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

/** 
 * Records the average buffer occupancy and its variance with format:
 * <p>
 * [Simulation time] [average buffer occupancy % [0..100] ] [variance]
 * </p>
 * 
 * <p>
 * The occupancy is calculated as an instantaneous snapshot every nth second
 * as defined by the <code>occupancyInterval</code> setting, not as an
 * average over time.
 * </p>
 * 
 * @author	teemuk
 */
import java.util.List;

import core.DTNHost;
import core.Settings;
import core.SimClock;
import core.UpdateListener;

public class BufferOccupancySpecificReport extends Report implements UpdateListener {

	/**
	 * Record occupancy every nth second -setting id ({@value}). 
	 * Defines the interval how often (seconds) a new snapshot of buffer
	 * occupancy is taken
	 */
	public static final String BUFFER_REPORT_INTERVAL = "occupancyInterval";
	/** Default value for the snapshot interval */
	public static final int DEFAULT_BUFFER_REPORT_INTERVAL = 5;
	
	private double lastRecord = Double.MIN_VALUE;
	private int interval;
	
	/**
	 * Creates a new BufferOccupancySpecificReport instance.
	 */
	public BufferOccupancySpecificReport() {
		super();
		
		Settings settings = getSettings();
		if (settings.contains(BUFFER_REPORT_INTERVAL)) {
			interval = settings.getInt(BUFFER_REPORT_INTERVAL);
		} else {
			interval = -1; /* not found; use default */
		}
		
		if (interval < 0) { /* not found or invalid value -> use default */
			interval = DEFAULT_BUFFER_REPORT_INTERVAL;
		}
	}
	
	public void updated(List<DTNHost> hosts) {
		if (SimClock.getTime() - lastRecord >= interval) {
			lastRecord = SimClock.getTime();
			
			printLine(hosts);
		}
	}
	
	/**
	 * Prints a snapshot of the buffer occupancy through all nodes
	 * @param hosts The list of hosts in the simulation
	 */
	private void printLine(List<DTNHost> hosts) {
		String output = format(SimClock.getTime()) + " ";

		for (DTNHost h : hosts) {
			double tmp = h.getBufferOccupancy();
			tmp = (tmp<=100.0)?(tmp):(100.0);
            output += (tmp + " ");
		}
		write(output);
	}

}
