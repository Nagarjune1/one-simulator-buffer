/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.List;

import core.DTNHost;
import core.Message;
import core.MessageListener;

public class ReqResReport extends Report implements MessageListener {
	public static String HEADER="# time  ID  req_pack  path";
	private int created;
	private int delivered;
	private int matched;

	/**
	 * Constructor.
	 */
	public ReqResReport() {
		init();
	}
	
	@Override
	public void init() {
		super.init();
		created = 0;
		delivered = 0;
		write(HEADER);
	}

	/** 
	 * Returns the given messages hop path as a string
	 * @param m The message
	 * @return hop path as a string
	 */
	private String getPathString(Message m) {
		List<DTNHost> hops = m.getHops();
		String str = m.getFrom().toString();
		
		for (int i=1; i<hops.size(); i++) {
			str += "->" + hops.get(i); 
		}
		
		return str;
	}

	public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
		if (!isWarmupID(m.getId()) && m.isResponse()) {
			write(format(getSimTime()) + " " + m.getId() + " " +
				m.getRequest() + " " + getPathString(m));
		}
	}

	public void newMessage(Message m) {
		if (isWarmup()) {
			addWarmupID(m.getId());
		}
	}

	public void messageDeleted(Message m, DTNHost where, boolean dropped) {}
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {}
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {}

	@Override
	public void done() {
		super.done();
	}
}