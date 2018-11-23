/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.List;
import java.util.Collection;

import core.DTNHost;
import core.Settings;
import core.Message;
import core.SimClock;
import core.UpdateListener;

public class ReqResReport extends Report implements UpdateListener {
	public static final int DEFAULT_REPORT_INTERVAL = 10;

	private double lastRecord = Double.MIN_VALUE;
	private int interval;

	/**
	 * Constructor.
	 */
	public ReqResReport() {
		super();
	}

	@Override
	protected void init() {
		super.init();
	}

	public void updated(List<DTNHost> hosts) {
		// TODO
	}
}