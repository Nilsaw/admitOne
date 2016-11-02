package com.admitone.controller;

import org.springframework.web.bind.annotation.RestController;

import com.admitone.cache.DataLoad;
import com.admitone.domain.objects.BookingInfo;
import com.admitone.domain.objects.CustomerInfo;
import com.admitone.domain.objects.EventInfo;
import com.admitone.domain.process.ManageBooking;
import com.admitone.utils.BookingStatusEnum;
import com.admitone.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Contains the rest endpoints to different processes.
 * @author Nilesh Sawant
 *
 */
@RestController
public class BookingController {
	
	public DataLoad dataLoad;
	private ManageBooking manageBooking;
	
	@Autowired
	public BookingController(DataLoad dataLoad, ManageBooking manageBooking){
		this.dataLoad = dataLoad;
		this.manageBooking = manageBooking;
	}
    
    /**
     * Use this endpoint while purchasing the tickets.
     * @param event_id
     * @param user_id
     * @param tickets
     * @return
     */
    @RequestMapping(value=Constants.PURCHASE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> purchase(@RequestParam(value = Constants.EVENT_ID_PARAM, required = true) Integer event_id,
    		@RequestParam(value = Constants.USER_ID_PARAM, required = true) String user_id,
    		@RequestParam(value = Constants.TICKETS_PARAM, required = true) Integer tickets) {
    	
		Map<String, CustomerInfo> customers = (HashMap<String, CustomerInfo>)dataLoad.getCache().getUnchecked("customers");
		Map<Integer, EventInfo> eventInfo = (HashMap<Integer, EventInfo>)dataLoad.getCache().getUnchecked("events");
    	HashMap<Long, BookingInfo> bookings = (HashMap<Long, BookingInfo>)dataLoad.getCache().getUnchecked("bookings");
  		
    	//Error handling
		if(!customers.containsKey(user_id))
	        return new ResponseEntity<String>(Constants.USER_ERROR_MSG, HttpStatus.BAD_REQUEST);
		if(!eventInfo.containsKey(event_id))
	        return new ResponseEntity<String>(Constants.EVENT_ERROR_MSG, HttpStatus.BAD_REQUEST);
		
		Long order_id = new Date().getTime();	
		BookingInfo bookingInfo = new BookingInfo(order_id, user_id, event_id, tickets);
    	if(manageBooking.bookEvent(bookingInfo) == BookingStatusEnum.SUCCESS){
    		bookings.put(order_id, bookingInfo);
	        return new ResponseEntity<String>(order_id.toString(), HttpStatus.OK);
    	}else 
	        return new ResponseEntity<String>(BookingStatusEnum.FULL.toString(), HttpStatus.CONFLICT);

    }
    
    @RequestMapping(value=Constants.CANCELLATION, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> cancelation(@RequestParam(value = Constants.EVENT_ID_PARAM, required = true) Integer event_id,
    		@RequestParam(value = Constants.USER_ID_PARAM, required = true) String user_id,
    		@RequestParam(value = Constants.BOOKING_ID_PARAM, required = true) Long booking_id,
    		@RequestParam(value = Constants.TICKETS_PARAM, required = true) Integer tickets) {
    	
		Map<String, CustomerInfo> customers = (HashMap<String, CustomerInfo>)dataLoad.getCache().getUnchecked("customers");
		Map<Integer, EventInfo> eventInfo = (HashMap<Integer, EventInfo>)dataLoad.getCache().getUnchecked("events");
    	HashMap<Long, BookingInfo> bookings = (HashMap<Long, BookingInfo>)dataLoad.getCache().getUnchecked("bookings");
  		
    	//Error handling
		if(!customers.containsKey(user_id))
	        return new ResponseEntity<String>(Constants.USER_ERROR_MSG, HttpStatus.BAD_REQUEST);
		if(!eventInfo.containsKey(event_id))
	        return new ResponseEntity<String>(Constants.EVENT_ERROR_MSG, HttpStatus.BAD_REQUEST);
		if(!bookings.containsKey(booking_id))
	        return new ResponseEntity<String>(Constants.BOOKING_ERROR_MSG, HttpStatus.BAD_REQUEST);

    	if(manageBooking.cancelTickets(user_id, tickets, event_id, booking_id) == BookingStatusEnum.CANCELED){
	        return new ResponseEntity<String>(BookingStatusEnum.SUCCESS.toString(), HttpStatus.OK);
    	}else 
	        return new ResponseEntity<String>(BookingStatusEnum.FULL.toString(), HttpStatus.CONFLICT);
    } 
    
    @RequestMapping(value=Constants.EXCHANGE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> exchange(@RequestParam(value = Constants.EVENT_ID_FROM_PARAM, required = true) Integer event_id_from,
    		@RequestParam(value = Constants.EVENT_ID_TO_PARAM, required = true) Integer event_id_to,
    		@RequestParam(value = Constants.BOOKING_ID_PARAM, required = true) Long booking_id,
    		@RequestParam(value = Constants.USER_ID_PARAM, required = true) String user_id,
    		@RequestParam(value = Constants.TICKETS_PARAM, required = true) Integer tickets) {
    	
		Map<String, CustomerInfo> customers = (HashMap<String, CustomerInfo>)dataLoad.getCache().getUnchecked("customers");
		Map<Integer, EventInfo> eventInfo = (HashMap<Integer, EventInfo>)dataLoad.getCache().getUnchecked("events");
    	HashMap<Long, BookingInfo> bookings = (HashMap<Long, BookingInfo>)dataLoad.getCache().getUnchecked("bookings");
  		
    	//Error handling
		if(!customers.containsKey(user_id))
	        return new ResponseEntity<String>(Constants.USER_ERROR_MSG, HttpStatus.BAD_REQUEST);
		if(!eventInfo.containsKey(event_id_from) || !eventInfo.containsKey(event_id_to))
	        return new ResponseEntity<String>(Constants.EVENT_ERROR_MSG, HttpStatus.BAD_REQUEST);
		if(!bookings.containsKey(booking_id))
	        return new ResponseEntity<String>(Constants.BOOKING_ERROR_MSG, HttpStatus.BAD_REQUEST);
		
    	if(manageBooking.exchangeTickets(user_id, tickets, event_id_from, booking_id, event_id_to) == BookingStatusEnum.SUCCESS){
    		//Create the new booking for the exchange.
    		Long new_booking_id = new Date().getTime();
    		BookingInfo newBooking = new BookingInfo(new_booking_id, user_id, event_id_to, tickets);
    		bookings.put(new_booking_id, newBooking);
	        return new ResponseEntity<String>(new_booking_id.toString(), HttpStatus.OK);
    	}else 
	        return new ResponseEntity<String>(BookingStatusEnum.FULL.toString(), HttpStatus.CONFLICT);
    
    } 
    
    /**
     * Get the list of the bookings done filtered by event ids
     * @param event_id_start
     * @param event_id_end
     * @return
     */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.BOOKING_DATA, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity bookingData(@RequestParam(value = Constants.EVENT_ID_START_PARAM, required = true) Integer event_id_start,
    		@RequestParam(value = Constants.EVENT_ID_END_PARAM, required = true) Integer event_id_end) {
  
    	HashMap<String, Object> bookingData = null; //Custom return object
    	List<HashMap<String,Object>> listOf_bookingData = new ArrayList<HashMap<String,Object>>(); 
    	HashMap<Long, BookingInfo> bookings = (HashMap<Long, BookingInfo>)dataLoad.getCache().getUnchecked("bookings");
		Map<String, CustomerInfo> customers = (HashMap<String, CustomerInfo>)dataLoad.getCache().getUnchecked("customers");


    	for(Long bkId: bookings.keySet()){
    		if(bookings.get(bkId).getEventId() >= event_id_start && bookings.get(bkId).getEventId() <= event_id_end){
    			bookingData = new HashMap<String, Object>();
    			bookingData.put(Constants.BK_KEY, bkId);
    			bookingData.put(Constants.BK_INF_KEY, bookings.get(bkId));
    			bookingData.put(Constants.CUST_KEY, customers.get(bookings.get(bkId).getCustomerId())); 
    			listOf_bookingData.add(bookingData);
    		}
    	}
        return new ResponseEntity<List<HashMap<String,Object>>>(listOf_bookingData, HttpStatus.OK);
    }

}