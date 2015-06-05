package gson.test;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Club {
	public static void main(String[] args) {
		// Setup a Club with 2 members
		Club myClub = new Club();
		myClub.addMember(new Silver("Jack"));
		myClub.addMember(new Gold("Jill"));
		myClub.addMember(new Silver("Mike"));
		
		// Get the GSON Object and register Type Adapter
    	GsonBuilder builder = new GsonBuilder();
    	builder.registerTypeAdapter(Member.class, new MemberDeserializer());
    	builder.registerTypeAdapter(Member.class, new MemberSerializer());
    	builder.setPrettyPrinting();
     	Gson gson = builder.create();
    	
    	// Serialize Club to JSON
    	String myJsonClub = gson.toJson(myClub); 
   	
    	// De-Serialize to Club
    	Club myNewClub = gson.fromJson(myJsonClub, Club.class);
		System.out.println(myClub.equals(myNewClub) ? "Cloned!" : "Failed");
		System.out.println(gson.toJson(myNewClub));
	}

	private String title = "MyClub";
	private ArrayList<Member> members = new ArrayList<Member>();

	public boolean equals(Object club) {
		Club that = (Club) club;
		if (!this.title.equals(that.title)) return false;
		for (int i=0; i<this.members.size(); i++) {
			Member member1 = this.getMember(i);
			Member member2 = that.getMember(i);
			if (! member1.equals(member2)) return false;
		}
		return true;
	}
	public void addMember(Member newMember) { members.add(newMember); }
	public Member getMember(int i) { return members.get(i); }
}
