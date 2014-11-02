/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package integrationTests.helper;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Svetoslav
 */
public class ITTasksCounter {
	private Map<Class, Integer> counts;
	
	public ITTasksCounter() {
		this.counts = new HashMap<>();
	}
	
	public synchronized void increment(Class clazz) {
		Integer count = 0;
		if(counts.containsKey(clazz)){
			count = this.counts.get(clazz);
		}
		
		this.counts.put(clazz, ++count);
	}
	
	public synchronized int getCount(Class clazz) {
		if(this.counts.containsKey(clazz)){
			return this.counts.get(clazz);
		}
		
		return 0;
	}
}
