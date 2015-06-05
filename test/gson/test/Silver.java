package gson.test;
public class Silver extends Member {
	private String silverData = "SomeSilverData";
	public Silver() { 
		super(); 
		this.setType(1); 
	}
	public Silver(String theName) {
		super(theName); 
		this.setType(1); 
	}
	public boolean equals(Object that) {
		Silver silver = (Silver)that;
		return (super.equals(that) && this.silverData.equals(silver.silverData)); 
	}
}
