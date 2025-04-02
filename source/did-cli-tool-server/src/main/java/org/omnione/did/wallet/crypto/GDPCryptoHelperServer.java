package org.omnione.did.wallet.crypto;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;

/**
 * Server용 Crypto 함수 구현부
 * <pre>History:</b>
 *		Eliot, 2018.09.13 최초작성
 * </pre>
 *
 * @author Eliot
 * @version 1.0
 * @see None
 */

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.spongycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.ECPointUtil;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.jce.spec.ECNamedCurveSpec;
import org.spongycastle.jce.spec.ECParameterSpec;

import org.omnione.did.wallet.crypto.GDPCryptoHelperServer.CurveParamEnum;
import org.omnione.did.wallet.eoscommander.crypto.ec.CurveParam;
import org.omnione.did.wallet.eoscommander.crypto.ec.EcTools;
import org.omnione.did.wallet.eoscommander.crypto.ec.EosPrivateKey;
import org.omnione.did.wallet.eoscommander.crypto.ec.EosPublicKey;
import org.omnione.did.wallet.eoscommander.crypto.util.Base58;
import org.omnione.did.wallet.eoscommander.crypto.util.HexUtils;
import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.util.GDPLogger;

public class GDPCryptoHelperServer extends GDPCryptoHelperInterface {

	public enum CurveParamEnum {

		SECP256_R1("secp256r1", "R1", 0), SECP256_K1("secp256k1", "K1", 1);

		private String value;

		private String prefix;

		private int number;

		private CurveParamEnum(String value, String prefix, int number) {
			this.value = value;
			this.prefix = prefix;
			this.number = number;
		}

		public String getValue() {
			return value;
		}

		public String getPrefix() {
			return prefix;
		}

		public int getNumber() {
			return number;
		}

		public CurveParam getEosCommandCurveParam() {
			return EcTools.getCurveParam(this.number);
		}

	}

	@Override
	public KeyPair generateKeyPair() {
		try {
			KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "SC");
			ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");

			g.initialize(ecSpec, SecureRandom.getInstance("SHA1PRNG"));
			return g.generateKeyPair();
		}
		catch (Exception var2) {
//			var2.printStackTrace();
			return null;
		}
	}

	public PrivateKey getPrivateKeyFromBytes(byte[] encodedPrivateKey) {

		KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance("ECDSA", "SC");
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
			keyFactory.generatePrivate(keySpec);
		}
		catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
		}
		catch (NoSuchProviderException e) {
//			e.printStackTrace();
		}
		catch (InvalidKeySpecException e) {
//			e.printStackTrace();
		}

		return null;
	}



	public ECPrivateKey eosPriKeyToECPriKey(byte[] eosPriKeyBytes, CurveParamEnum cureveParam) {
		try {
//			AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", GDPCryptoConst.Provider_SC);
//			parameters.init(new ECGenParameterSpec(cureveParam.getValue()));
//
//			java.security.spec.ECParameterSpec ecParameterSpec = parameters.getParameterSpec(java.security.spec.ECParameterSpec.class);
//			ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(new BigInteger(eosPriKeyBytes), ecParameterSpec);
//
//			ECPrivateKey privateKey = (ECPrivateKey) KeyFactory.getInstance("ECDSA", GDPCryptoConst.Provider_SC).generatePrivate(ecPrivateKeySpec);

			ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");

			GDPLogger.debug("eosPriKeyToECPriKey spec: " + spec.getName());
			org.spongycastle.jce.spec.ECPrivateKeySpec keySpec = new org.spongycastle.jce.spec.ECPrivateKeySpec(new BigInteger(1, eosPriKeyBytes), spec);

			GDPLogger.debug("eosPriKeyToECPriKey keySpec: " + keySpec.getClass().getCanonicalName());

			ECPrivateKey privateKey = (ECPrivateKey) KeyFactory.getInstance("ECDSA", GDPCryptoConst.Provider_SC).generatePrivate(keySpec);


			return privateKey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeySpecException ie) {
			ie.printStackTrace();
			return null;
		} catch (NoSuchProviderException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public ECPublicKey getEcPublicKey(byte[] pubKeyBytes, CurveParamEnum cureveParam) throws Exception {
//		AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", GDPCryptoConst.Provider_SC);
//		parameters.init(new ECGenParameterSpec(cureveParam.getValue()));
//
//		java.security.spec.ECParameterSpec ecParameterSpec = parameters.getParameterSpec(java.security.spec.ECParameterSpec.class);
//		ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec(cureveParam.getValue());
//
//		ECNamedCurveSpec params = new ECNamedCurveSpec(cureveParam.getValue(), spec.getCurve(), spec.getG(), spec.getN());
//		ECPoint point = ECPointUtil.decodePoint(params.getCurve(), pubKeyBytes);
//
//		ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(point, ecParameterSpec);
//
//		java.security.PublicKey ecPublicKey = (ECPublicKey) KeyFactory.getInstance("ECDSA", GDPCryptoConst.Provider_SC).generatePublic(ecPublicKeySpec);

		ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");

		ECNamedCurveSpec params = new ECNamedCurveSpec("secp256k1", spec.getCurve(), spec.getG(), spec.getN());
		ECPoint point = ECPointUtil.decodePoint(params.getCurve(), pubKeyBytes);
		PublicKey publicKey = KeyFactory.getInstance("ECDSA", GDPCryptoConst.Provider_SC).generatePublic(new java.security.spec.ECPublicKeySpec(point, params));



		return (ECPublicKey) publicKey;
	}

	public EosPrivateKey convertKeytoEosPrivateKey(ECPrivateKey privateKey,
												   CurveParamEnum cureveParam) {
		EosPrivateKey eosPrivateKey = new EosPrivateKey(cureveParam.getNumber(),
				privateKey.getS().toByteArray());
		return eosPrivateKey;
	}

	public EosPublicKey convertKeytoEosPublicKey(ECPublicKey publicKey,
												 CurveParamEnum cureveParam) {
		BCECPublicKey bcecPublicKey = null;
		if (publicKey instanceof BCECPublicKey) {
			bcecPublicKey = (BCECPublicKey) publicKey;
		}
		else {
			bcecPublicKey = new BCECPublicKey(publicKey, null);
		}

		EosPublicKey eosPublicKey = new EosPublicKey(
				bcecPublicKey.getQ().getEncoded(true),
				cureveParam.getEosCommandCurveParam());
		return eosPublicKey;
	}

	public byte[] getSharedSecret(ECPrivateKey ecPrivateKey, ECPublicKey otherPublicKey) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
		//org.spongycastle.crypto.agreement.ECDHBasicAgreement 코드 참조함 

		CipherParameters pubKey = ECUtil.generatePublicKeyParameter((PublicKey) otherPublicKey);
		ECPublicKeyParameters pub = (ECPublicKeyParameters) pubKey;
		ECPrivateKeyParameters privKey = (ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter((PrivateKey) ecPrivateKey);
		org.spongycastle.math.ec.ECPoint P = pub.getQ().multiply(privKey.getD()).normalize();
		if (P.isInfinity()) {
			throw new IllegalStateException("Infinity is not a valid agreement value for ECDH");
		}

		BigInteger bitInt = P.getAffineXCoord().toBigInteger();

		byte[] sharedSecret = getBytes(bitInt);
		return sharedSecret;
	}

	private byte[] getBytes(BigInteger bigint) {
		byte[] result = new byte[32];
		byte[] bytes = bigint.toByteArray();
		if (bytes.length <= result.length) {
			System.arraycopy(bytes, 0, result, result.length - bytes.length, bytes.length);
		} else {
			assert bytes.length == 33 && bytes[0] == 0;
			System.arraycopy(bytes, 1, result, 0, bytes.length - 1);
		}
		return result;
	}

	/**
	 * EOS 개인키를 SpongyCastle 에서 제공하는 개인키로 변경.
	 * @param eosPriKeyBytes	변경할 EOS 개인키
	 * @param cureveParam		CurveType (secp256k1, secp256r1)
	 * @return					SpongyCastle 에서 생성한 개인키
	 */
	public ECPrivateKey eosPriKeyToECPriKeyV2(byte[] eosPriKeyBytes, CurveParamEnum cureveParam) {
		try {
			AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", GDPCryptoConst.Provider_SC);
			parameters.init(new ECGenParameterSpec(cureveParam.getValue()));

			java.security.spec.ECParameterSpec ecParameterSpec = parameters.getParameterSpec(java.security.spec.ECParameterSpec.class);
			ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(new BigInteger(eosPriKeyBytes), ecParameterSpec);

			ECPrivateKey privateKey = (ECPrivateKey) KeyFactory.getInstance("ECDSA", GDPCryptoConst.Provider_SC).generatePrivate(ecPrivateKeySpec);

			return privateKey;
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (InvalidKeySpecException ie) {
			return null;
		} catch (NoSuchProviderException npe) {
			return null;
		} catch (InvalidParameterSpecException e) {
			return null;
		}
	}

	/**
	 * EOS 공개키를 SpongyCastle 에서 제공하는 공개키로 변경.
	 * @param pubKeyBytes		변경할 공개키
	 * @param cureveParam		CurveType (secp256k1, secp256r1)
	 * @return					SpongyCastle 에서 생성한 공개키
	 * @throws Exception
	 */
	public ECPublicKey getEcPublicKeyV2(byte[] pubKeyBytes, CurveParamEnum cureveParam) throws Exception {
		AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", GDPCryptoConst.Provider_SC);
		parameters.init(new ECGenParameterSpec(cureveParam.getValue()));

		java.security.spec.ECParameterSpec ecParameterSpec = parameters.getParameterSpec(java.security.spec.ECParameterSpec.class);
		ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec(cureveParam.getValue());

		ECNamedCurveSpec params = new ECNamedCurveSpec(cureveParam.getValue(), spec.getCurve(), spec.getG(), spec.getN());
		ECPoint point = ECPointUtil.decodePoint(params.getCurve(), pubKeyBytes);

		ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(point, ecParameterSpec);

		java.security.PublicKey ecPublicKey = (ECPublicKey) KeyFactory.getInstance("ECDSA", GDPCryptoConst.Provider_SC).generatePublic(ecPublicKeySpec);

		return (ECPublicKey) ecPublicKey;
	}

	public static void main(String[] args) {
		GDPLogger.FLAG = true;

		String pri = "7TBLHPwBk79YFMLBzokVq13jND5mNRZm4sVVsbC1SK32";

		byte[] decodedPrivateKey = Base58.decode(pri);
		EosPrivateKey eosPrivateKey = new EosPrivateKey(decodedPrivateKey);

		String pub = "28WXb89T732f6JDmQ7vqd3kG2CTtP9EdEUhj36m1BNmKx";

		System.out.println(eosPrivateKey.toString());


		GDPCryptoHelperServer cryptoHelperServer = new GDPCryptoHelperServer();
		ECPrivateKey ecPrivateKey = cryptoHelperServer.eosPriKeyToECPriKey(eosPrivateKey.getBytes(), CurveParamEnum.SECP256_K1);
		ECPublicKey otherPublicKey = null;
		try {
			byte[] decodedPublicKey = Base58.decode(pub);
			otherPublicKey = cryptoHelperServer.getEcPublicKey(decodedPublicKey, CurveParamEnum.SECP256_K1);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		try {
			cryptoHelperServer.getSharedSecret(ecPrivateKey, otherPublicKey);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
