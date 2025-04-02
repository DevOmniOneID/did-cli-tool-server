package org.omnione.did.wallet.data;

import com.google.gson.JsonObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class GDPUserRegData {

	public String signBase64Url;

	public String publicKeyBase64Url;

	public String signSource;

	public static String KEY_NAME_SIGN = "signBase64Url";

	public static String KEY_NAME_PUBKEY = "publicKeyBase64Url";

	public static String KEY_NAME_SIGN_SOURCE = "signSource";

	public String toJson() {
		GsonWrapper gson = new GsonWrapper();
		JsonObject object = new JsonObject();
		object.addProperty(KEY_NAME_SIGN, signBase64Url);
		object.addProperty(KEY_NAME_PUBKEY, publicKeyBase64Url);
		object.addProperty(KEY_NAME_SIGN_SOURCE, signSource);

		String json = gson.toJson(object);

		return json;
	}

}
