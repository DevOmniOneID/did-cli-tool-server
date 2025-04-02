package org.omnione.did.wallet.data;

import com.google.gson.JsonObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class GDPIssuerRegData {

	public String signBase58;

	public String userPublicKeyBase64Url;

	public String issuerPublicKeyBase64Url;

	public static String KEY_NAME_SIGN = "signBase58";

	public static String KEY_NAME_USER_PUBKEY = "userPublicKeyBase64Url";

	public static String KEY_NAME_ISSUER_PUBKEY = "issuerPublicKeyBase64Url";

	public String toJson() {
		GsonWrapper gson = new GsonWrapper();
		JsonObject object = new JsonObject();
		object.addProperty(KEY_NAME_SIGN, signBase58);
		object.addProperty(KEY_NAME_USER_PUBKEY, userPublicKeyBase64Url);
		object.addProperty(KEY_NAME_ISSUER_PUBKEY, issuerPublicKeyBase64Url);

		String json = gson.toJson(object);

		return json;
	}

}
