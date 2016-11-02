package com.admitone.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.admitone.domain.objects.BookingInfo;
import com.admitone.domain.objects.CustomerInfo;
import com.admitone.domain.objects.EventInfo;
import com.admitone.domain.process.ManageBooking;
import com.admitone.utils.Constants;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Loads the mock data in the cache
 * I have used app cache to persist the data.
 * @author Nilesh Sawant
 *
 */
@Component
public class DataLoad {

    private static LoadingCache<String,Object> cache;
    
	@Autowired
	private ManageBooking manageBooking;
	
	public DataLoad(){
		init();
	}
	
	/**
	 * Loads the cache
	 */
	private void init(){
        cache = CacheBuilder.newBuilder()
                .refreshAfterWrite(15,TimeUnit.MINUTES)
                .build(new CacheLoader<String, Object>() {
                        @Override
                        public Object load(String dataKey) throws Exception {
                        	switch(dataKey){
	                        	case Constants.BOOKINGS:
		                        		return loadBookings();
	                        	case Constants.CUSTOMERS:
	                        			return loadCustomers();
	                        	case Constants.EVENTS :
	                        			return loadEvents();
	                        	default:
	                        			return null;
                        	}
                        }
                });		
	}
	
	/**
	 * Creates mock bookings
	 * @return mock data
	 */
	
	private Map<Long, BookingInfo> loadBookings(){
		Map<String, CustomerInfo> customers = (HashMap<String, CustomerInfo>)cache.getUnchecked(Constants.CUSTOMERS);
		Map<Integer, EventInfo> eventInfo = (HashMap<Integer, EventInfo>)cache.getUnchecked(Constants.EVENTS);
		Long baseId = new Date().getTime();
		BookingInfo firstBooking = new BookingInfo(baseId*1,"nsawant", 1, 10);
		BookingInfo secondBooking = new BookingInfo(baseId*2,"mspencer", 5, 77);
		BookingInfo thirdBooking = new BookingInfo(baseId*3, "matwall", 12, 4);
		BookingInfo fourthBooking = new BookingInfo(baseId*4, "bob213", 7, 44);
		BookingInfo fifthBooking = new BookingInfo(baseId*5, "bob213", 90, 106);
		BookingInfo sixthBooking = new BookingInfo(baseId*6, "jdoe", 33, 170);
		
		manageBooking.bookEvent(firstBooking);
		manageBooking.bookEvent(secondBooking);
		manageBooking.bookEvent(thirdBooking);
		manageBooking.bookEvent(fourthBooking);
		manageBooking.bookEvent(fifthBooking);
		manageBooking.bookEvent(sixthBooking);
		
		return new HashMap<Long, BookingInfo>(){{
				put(firstBooking.getBookingKey(), firstBooking);
				put(secondBooking.getBookingKey(), secondBooking);
				put(thirdBooking.getBookingKey(), thirdBooking);
				put(fourthBooking.getBookingKey(), fourthBooking);
				put(fifthBooking.getBookingKey(), fifthBooking);
				put(sixthBooking.getBookingKey(), sixthBooking);
			}};
	}
	
	/**
	 * Creates mock Events
	 * @return mock data
	 */
	private Map<Integer, EventInfo> loadEvents(){
		Map<Integer, EventInfo> eventInfo = new HashMap<Integer, EventInfo>();
		for(int i=0;i<100;i++)
			eventInfo.put(i, new EventInfo(i, 10000));
		return eventInfo;
	}
	
	/**
	 * Creates mock Customers which are static. Add customers here. No rest endpoint to do so. No time. :(
	 * @return mock data
	 */
	private Map<String, CustomerInfo> loadCustomers(){
		Map<String, CustomerInfo> customers = new HashMap<String, CustomerInfo>(){{
			put("nsawant",new CustomerInfo("nsawant", "Nilesh", "Sawant", "nilsaw101@gmail.com"));
			put("mspencer", new CustomerInfo("mspencer", "Mark", "Spencer", "mark.spencer@gmail.com"));
			put("matwall", new CustomerInfo("matwall", "Matthews", "Wallace", "wallace.matthews@gmail.com"));
			put("bob213", new CustomerInfo("bob213", "Bob", "Title", "bob.title@gmail.com"));
			put("jdoe", new CustomerInfo("jdoe", "Jon", "doe", "jon.doe@gmail.com"));
			put("swilliams", new CustomerInfo("swilliams", "serena", "williams", "swilliams@aol.com"));
			put("seth222", new CustomerInfo("seth222", "seth", "nickolos", "seth222@gmail.com"));
		}};
		return customers;
	}

	public static LoadingCache<String, Object> getCache() {
		return cache;
	}
}
