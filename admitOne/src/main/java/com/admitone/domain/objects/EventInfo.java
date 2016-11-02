package com.admitone.domain.objects;

import java.util.Date;
/**
 * The eventId acts like the primary key. 
 * @author Nilesh Sawant
 *
 */
public class EventInfo {
	private Integer eventID;
	private Integer ticketsAvailable;
	private Date date; //Features that should be added in the future

	public EventInfo(Integer eventID, Integer ticketsAvailable){
		this.eventID = eventID;
		this.ticketsAvailable = ticketsAvailable;
	}
	
	public Integer getEventID() {
		return eventID;
	}

	public void setEventID(Integer eventID) {
		this.eventID = eventID;
	}

	public Integer getTicketsAvailable() {
		return ticketsAvailable;
	}

	public void setTicketsAvailable(Integer ticketsAvailable) {
		this.ticketsAvailable = ticketsAvailable;
	}
}
