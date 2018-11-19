/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package routing;

import java.util.List;
import java.util.ArrayList;

import core.Settings;
import core.Message;
import core.DTNHost;
import core.Connection;

import util.Tuple;

/**
 * request response router to handle requests and return message packet
 */
public class ReqResRouter extends ActiveRouter {
	/**
	 * Constructor. Creates a new request response router based on the settings in
	 * the given Settings object.
	 * @param s The settings object
	 */
	public ReqResRouter(Settings s) {
		super(s);
		//TODO: read&use request response router specific settings (if any)
	}
	
	/**
	 * Copy constructor.
	 * @param r The router prototype where setting values are copied from
	 */
	protected ReqResRouter(ReqResRouter r) {
		super(r);
		//TODO: copy request response settings here (if any)
	}

	protected String comparePackets() {
		ArrayList<String> requests = new ArrayList<String>();
		ArrayList<String> contents = new ArrayList<String>();

		String result = "";
		// print host and interest packets
		for (Message hm : this.getMessageCollection()) {
			String type = (String) hm.getProperty("type");
			// only show interest packets
			if (type != null && type.equals("request")) {
				requests.add(hm.getId());
			}
		}
		result += "Host:" + getHost() + "-" + requests;
		// print other hosts + contents
		for (Connection con : this.getConnections()) {
			DTNHost other = con.getOtherNode(getHost());
			ArrayList<Message> bufferToCheck = new ArrayList<Message>(other.getMessageCollection());
			for (Message om : bufferToCheck) {
				// only show content packages
				if (om.getProperty("type") == null) {
					contents.add(om.getId());
				}
			}
			result += "\nOther:" + other + "-" + contents;
		}

		// interest packet matches content request
		boolean found = false;
		result += "\nFound: ";
		for (String request : requests) {
			for (String content : contents) {
				if (request.substring(1).equals(content.substring(1))) {
					if (!found) found = true;
					result += content + ",";
				}
			}
		}
		// only return connections where content packages matches interest packets
		return found? result + "\n" : null;
	}
			
	@Override
	public void update() {
		super.update();
		if (isTransferring() || !canStartTransfer()) {
			return; // transferring, don't try other connections yet
		}

		/** compare interest packet with content **/
		String results = comparePackets();
		if (results != null) {
			System.out.println(results);
		}

		/** only transfer interest packets **/
		ArrayList<Message> messagesToTransfer = new ArrayList<Message>();
		for (Message m : this.getMessageCollection()) {
			if (m.getProperty("type") != null) {
				messagesToTransfer.add(m);
			}
		}
		this.tryMessagesToConnections(messagesToTransfer, this.getConnections());
	}
	
	@Override
	public ReqResRouter replicate() {
		return new ReqResRouter(this);
	}

}