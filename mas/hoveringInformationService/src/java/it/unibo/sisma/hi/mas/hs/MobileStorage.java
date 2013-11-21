package it.unibo.sisma.hi.mas.hs;

import java.util.HashMap;

public class MobileStorage {

	private double totalSpace;
	private double freeSpace;
	private HashMap<Object, Data> storage;

	public MobileStorage(double totalSpace) {
		super();
		this.totalSpace = totalSpace;
		this.freeSpace = totalSpace;
		this.storage = new HashMap<>();
	}

	public double getTotalSpace() {
		return totalSpace;
	}

	public double getFreeSpace() {
		return freeSpace;
	}
	
	public String getData(Object ID) {
		Data data = storage.get(ID);
		if(data == null) {
			return null;
		}
		return data.getValue();
	}
	
	public Object[] getAllData() {
		return storage.values().toArray(new Object[storage.size()]);
	}

	public boolean allocateData(Object ID, double size) {
		if(freeSpace < size) {
			return false;
		}
		freeSpace -= size;
		storage.put(ID, new Data("__no_data__", size));
		return true;
	}
	
	public void editData(Object ID, String newValue) {
		Data d = storage.get(ID);
		if(d != null) {
			d.setValue(newValue);
		}
	}
	
	public synchronized void freeData(Object ID) {
		Data d = storage.remove(ID);
		if(d != null) {
			freeSpace += d.getSize();
		}
	}
	
}
