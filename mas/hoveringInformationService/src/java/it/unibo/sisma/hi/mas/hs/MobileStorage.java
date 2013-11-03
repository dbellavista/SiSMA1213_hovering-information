package it.unibo.sisma.hi.mas.hs;

import java.util.HashMap;

public class MobileStorage {

	private double total_space;
	private double free_space;
	private HashMap<Object, Data> storage;

	public MobileStorage(double total_space) {
		super();
		this.total_space = total_space;
		this.free_space = total_space;
	}

	public synchronized double getTotal_space() {
		return total_space;
	}

	public synchronized double getFree_space() {
		return free_space;
	}
	
	public synchronized String getData(Object ID) {
		return storage.get(ID).getValue();
	}

	public synchronized boolean allocateData(Object ID, double size) {
		if(free_space < size) {
			return false;
		}
		free_space -= size;
		storage.put(ID, new Data(null, size));
		return true;
	}
	
	public synchronized void editData(Object ID, String newValue) {
		Data d = storage.get(ID);
		if(d != null) {
			d.setValue(newValue);
		}
	}
	
	public synchronized void freeData(Object ID) {
		Data d = storage.remove(ID);
		if(d != null) {
			free_space += d.getSize();
		}
	}
	
}
