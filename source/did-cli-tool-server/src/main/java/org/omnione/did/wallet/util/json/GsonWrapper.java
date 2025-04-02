package org.omnione.did.wallet.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.omnione.did.wallet.zkp.data.credential.SignatureCorrectnessProof;

import java.math.BigInteger;

public class GsonWrapper {
	public static GsonWrapper getGson() {
		return new GsonWrapper();
	}

	public static GsonWrapper getGsonPrettyPrinting() {
		return new GsonWrapper(true);
	}

	private Gson gson;

	public GsonWrapper() {
		super();
		gson = new GsonBuilder().disableHtmlEscaping().create();
	}

	public GsonWrapper(boolean prettyPrinting) {
		super();
		GsonBuilder builder = new GsonBuilder().disableHtmlEscaping();
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

	public static void main(String[] args) {

		SignatureCorrectnessProof signatureCorrectnessProof = new SignatureCorrectnessProof();

		signatureCorrectnessProof.setC(new BigInteger("1521591845901840120410481481048019824092184014098120498104810924801940"));
		signatureCorrectnessProof.setSe(new BigInteger("1531515151512516545654879897898707908909089080808098089089089089080"));

		String json = GsonWrapper.getGson().toJson(signatureCorrectnessProof);

		System.out.println(json);
		System.out.printf(GsonWrapper.getGson().fromJson(json, SignatureCorrectnessProof.class).getSe().toString());
		System.out.printf(GsonWrapper.getGson().fromJson(json, SignatureCorrectnessProof.class).getC().toString());
	}
}
