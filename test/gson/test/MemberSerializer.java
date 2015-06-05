package gson.test;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MemberSerializer implements JsonSerializer<Member> {
	 
	public JsonElement serialize(Member src, Type member, JsonSerializationContext context) {
		switch (src.getType()) {
			case 1: return context.serialize((Silver)src);
			case 2: return context.serialize((Gold)src);
			default: return null;
		}
	}
}