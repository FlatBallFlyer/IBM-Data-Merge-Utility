package gson.test;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

public class MemberDeserializer implements JsonDeserializer<Member> {

	@Override
	public Member deserialize(JsonElement json, Type member, JsonDeserializationContext context) {
		int myType = json.getAsJsonObject().get("type").getAsInt();
		switch (myType) {
			case 1: return context.deserialize(json, Silver.class);
			case 2: return context.deserialize(json, Gold.class);
			default: return null;
		}
	}
}
