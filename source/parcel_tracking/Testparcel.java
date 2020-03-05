package parcel_tracking;
public class Testparcel {
	String trackid;
	int orderer;

	public Testparcel(String tid, int oid) {
		this.trackid = tid;
		this.orderer = oid;
	}

	public String getTrackID() {
		return this.trackid;
	}

	public int getOrderer() {
		return this.orderer;
	}
}