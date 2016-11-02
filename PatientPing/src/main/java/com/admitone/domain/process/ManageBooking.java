package com.admitone.domain.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.admitone.cache.DataLoad;
import com.admitone.domain.objects.BookingInfo;
import com.admitone.domain.objects.CustomerInfo;
import com.admitone.domain.objects.EventInfo;
import com.admitone.utils.BookingStatusEnum;
import com.admitone.utils.Constants;

/**
 * This class contains the business logic of handling ticketing events.
 * @author Nilesh Sawant
 *
 */
@Component
public class ManageBooking {
	
	@Autowired
	private DataLoad dataLoad;

	/**
	 * Updates the event details after the booking is created
	 * @param bookingInfo
	 * @return status of the booking
	 */
	public BookingStatusEnum bookEvent(BookingInfo bookingInfo){
		HashMap<Integer, EventInfo> events = (HashMap<Integer, EventInfo>)dataLoad.getCache().getUnchecked(Constants.EVENTS); 
		EventInfo eventInfo = events.get(bookingInfo.getEventId());
		if(eventInfo.getTicketsAvailable() - bookingInfo.getNo_of_tickets() > 0){
			events.put(bookingInfo.getEventId(), 
					new EventInfo(bookingInfo.getEventId(), eventInfo.getTicketsAvailable() - bookingInfo.getNo_of_tickets()));
			return BookingStatusEnum.SUCCESS;
		}else 
			return BookingStatusEnum.FULL;
	}
	
/**
 * Cancels the event tickets after the booking is created
 * @param userID
 * @param no_of_tickets
 * @param eventID
 * @param booking_id
 * @return status of the booking
 */
	public BookingStatusEnum cancelTickets(String userID, Integer no_of_tickets, Integer eventID, Long booking_id){
		HashMap<Integer, EventInfo> events = (HashMap<Integer, EventInfo>)dataLoad.getCache().getUnchecked(Constants.EVENTS); 
    	HashMap<Long, BookingInfo> bookings = (HashMap<Long, BookingInfo>)dataLoad.getCache().getUnchecked(Constants.BOOKINGS);
    	
    	BookingInfo bookingInfo = bookings.get(booking_id);
		EventInfo eventInfo = events.get(bookingInfo.getEventId());
		
		Integer newTicketsAvailable = no_of_tickets + eventInfo.getTicketsAvailable();
		eventInfo.setTicketsAvailable(newTicketsAvailable);
		events.put(eventID, eventInfo);
		
		Integer updatedBookingTickets = bookingInfo.getNo_of_tickets() - no_of_tickets;
		bookingInfo.setNo_of_tickets(updatedBookingTickets);
		bookings.put(booking_id, bookingInfo);
		
		return BookingStatusEnum.CANCELED;
	}
	
	public BookingStatusEnum exchangeTickets(String userID, Integer no_of_tickets, Integer eventID_from, Long booking_id, Integer eventID_to){
		HashMap<Integer, EventInfo> events = (HashMap<Integer, EventInfo>)dataLoad.getCache().getUnchecked(Constants.EVENTS); 
    	HashMap<Long, BookingInfo> bookings = (HashMap<Long, BookingInfo>)dataLoad.getCache().getUnchecked(Constants.BOOKINGS);
    	
    	BookingInfo bookingInfo = bookings.get(booking_id);
		if(bookingInfo.getNo_of_tickets()<no_of_tickets)
			return BookingStatusEnum.ERROR;
		
		Integer updatedBookingTickets = bookingInfo.getNo_of_tickets() - no_of_tickets;
		bookingInfo.setNo_of_tickets(updatedBookingTickets);
		bookings.put(booking_id, bookingInfo);
		
		EventInfo eventInfo_from = events.get(bookingInfo.getEventId());
		
		EventInfo eventInfo_to = events.get(eventID_to);
		
		Integer newTicketsAvailableInEventto = eventInfo_from.getTicketsAvailable() - no_of_tickets;
		if(newTicketsAvailableInEventto<0)
			return BookingStatusEnum.FULL;
		
		Integer newTicketsAvailableInEventFrom = eventInfo_from.getTicketsAvailable() + no_of_tickets;

		eventInfo_to.setTicketsAvailable(newTicketsAvailableInEventto);		
		eventInfo_from.setTicketsAvailable(newTicketsAvailableInEventFrom);
		events.put(eventID_from, eventInfo_from);
		events.put(eventID_to, eventInfo_to);
		

		
		return BookingStatusEnum.SUCCESS;
	}	
	
	

}
