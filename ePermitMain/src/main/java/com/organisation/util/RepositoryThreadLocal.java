package com.organisation.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RepositoryThreadLocal {

	private static final Logger log = LoggerFactory.getLogger(RepositoryThreadLocal.class);
	private static ThreadLocal<Map<String, Object>> repositoryThreadLocal = new ThreadLocal<>();
	
	public static void setValue(String key, Object valueObject) {
		log.info("Entering setValue()");
		Map<String, Object> threadMap = repositoryThreadLocal.get();
		if(threadMap != null) {
			threadMap.put(key, valueObject);
		} else {
			threadMap = new HashMap<String, Object>(5);
			threadMap.put(key, valueObject);
		}
		log.info("Exiting setValue() function");
		repositoryThreadLocal.set(threadMap);
	}
	
	
	public static Object getValue(String key) {
		log.info("Entering getValue()");
		Map<String , Object> threadMap = repositoryThreadLocal.get();
		if(threadMap == null) {
			throw new IllegalStateException("Current thread scope is not initiated");
		}
		Object value = threadMap.get(key);
		log.info("Exiting getValue()");
		return value;
	}
	
	public static void remove() {
		repositoryThreadLocal.remove();
	}
	
	public static void verbose() {
		log.info("Entering verbose()");
		Map<String,Object> threadMap = repositoryThreadLocal.get();
		Iterator<Map.Entry<String, Object>> threadMapIterator = threadMap.entrySet().iterator();
		while(threadMapIterator.hasNext()) {
			Map.Entry<String, Object> threadMapEntry = threadMapIterator.next();
			log.info("Key: " + threadMapEntry.getKey() + " Value: " + threadMapEntry.getValue());
		}
		log.info("Exiting verbose()");
		
	}
	
}




