package gson.test;
public abstract class Member {
	private String clsname = this.getClass().getName() ;
	private int type;
	private String name = "unknown";

	public Member() { }
	public Member(String theName) {this.name = theName;}
	public int getType() { return type; }
	public void setType(int type) { this.type = type; }
	public boolean equals(Object member) {
		Member that = (Member) member;
		return this.name.equals(that.name);
	}
}
