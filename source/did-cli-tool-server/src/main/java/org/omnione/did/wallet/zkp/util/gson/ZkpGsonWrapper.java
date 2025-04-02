package org.omnione.did.wallet.zkp.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class ZkpGsonWrapper {
	public static ZkpGsonWrapper getGson() {
		return new ZkpGsonWrapper();
	}

	public static ZkpGsonWrapper getGsonPrettyPrinting() {
		return new ZkpGsonWrapper(true);
	}

	private Gson gson;

	private ExclusionExposeAnnotation exclude = new ExclusionExposeAnnotation();

	public ZkpGsonWrapper() {
		super();
		gson = new GsonBuilder()
				.addSerializationExclusionStrategy(exclude)
				.addDeserializationExclusionStrategy(exclude)
				.disableHtmlEscaping()
				.create();
	}

	public ZkpGsonWrapper(boolean prettyPrinting) {
		super();
		GsonBuilder builder = new GsonBuilder()
				.disableHtmlEscaping()
				.addSerializationExclusionStrategy(exclude)
				.addDeserializationExclusionStrategy(exclude);
		if (prettyPrinting) {
			builder.setPrettyPrinting();
		}
		gson = builder.create();
	}

	public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
		return gson.fromJson(json, classOfT);
	}

	public String toJson(Object src) {
		return gson.toJson(src);
	}
}
