package com.admitone.domain.objects;

import java.util.Date;
/**
 * Contains the booking info for each booking.
 * In a relational database the booking key is the primary key
 * @author Nilesh Sawant
 *
 */
public class BookingInfo {
	private Long bookingKey;
	private String customerId;
	private Integer eventId;
	private Integer no_of_tickets;
	private Date date;  //Features that should be added in the future
	
	public BookingInfo(Long bookingKey, String customerId, Integer eventId, Integer no_of_tickets){
		this.bookingKey = bookingKey;
		this.customerId = customerId;
		this.eventId = eventId;
		this.no_of_tickets = no_of_tickets;
	}
	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Integer getEventId() {
		return eventId;
	}
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
	public Integer getNo_of_tickets() {
		return no_of_tickets;
	}
	public void setNo_of_tickets(Integer no_of_tickets) {
		this.no_of_tickets = no_of_tickets;
	}

	public Long getBookingKey() {
		return bookingKey;
	}

	public void setBookingKey(Long bookingKey) {
		this.bookingKey = bookingKey;
	}
}
