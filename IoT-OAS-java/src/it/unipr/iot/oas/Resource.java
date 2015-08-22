package it.unipr.iot.oas;

/**
 * 
 *
 * @author	Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public class Resource {

	private String uuid;
	private String actions;

	public Resource() {
	}

	public Resource(String uuid, String actions) {
		this.uuid = uuid;
		this.actions = actions;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getActions() {
		return this.actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	public String toString() {
		return this.uuid + "\t" + this.actions;
	}
	
}
