package gson.test;
public class Gold extends Member {
	private String goldData = "SomeGoldData";
	private String extraData = "Extra Gold Data";
	public Gold() {
		super(); 
		this.setType(2);
	}
	public Gold(String theName) { 
		super(theName); 
		this.setType(2); 
	}
	public boolean equals(Gold that) {
		Gold gold = (Gold) that;
		return (super.equals(that) && this.goldData.equals(gold.goldData)); 
	}
}
