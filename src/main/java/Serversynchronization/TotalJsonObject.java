package Serversynchronization;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

public class TotalJsonObject {
	private JSONObject object = new JSONObject();

	public TotalJsonObject() {
	}

	public TotalJsonObject(String json) {
		jsonParser(json);
	}

	@SuppressWarnings("unchecked")
	public void addProperty(String key, Object value) {
		object.put(key, value);
	}

	public String toString() {
		return object.toJSONString();
	}

	public void removeValue(Object key) {
		object.remove(key);
	}

	public  static <T> T GsonConverter(String gson, Class<T> classOfT) {
		return new Gson().fromJson(gson, classOfT);
	}

	public static String GsonConverter(Object obj) {
		return new Gson().toJson(obj);
	}

	public void jsonParser(String str) {
		JSONParser parser = new JSONParser();
		try {
			object = (JSONObject) parser.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public <T> void addSimpleProperty(T object) {
		addProperty(T.get, value);
	}
	public Object get(String key) {
		return object.get(key);
	}

	public String getAsString() {
		return toString();
	}
	public Object getoOject(Class<?> typeof) {
		return TotalJsonObject.GsonConverter((String) this.get(typeof.getSimpleName()), typeof);
	}
	
}
