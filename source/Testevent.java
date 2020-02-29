public class Testevent {
	String parcel_id;
	int place_id;
	int id;
	String desc = "testing";

	public Testevent(int id, String parcel_id, int place_id) {
		this.id = id;
		this.parcel_id = parcel_id;
		this.place_id = place_id;
		this.desc = desc;
	}

	public String getParcelID() {
		return this.parcel_id;
	}

	public int getID() {
		return this.id;
	}

	public int getPlaceID() {
		return this.place_id;
	}

	public String getDescription() {
		return this.desc;
	}
}