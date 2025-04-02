/*
 * Copyright (c) 2017-2018 PLACTAL.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.omnione.did.wallet.eoscommander.crypto.ec;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Arrays;
import java.util.regex.PatternSyntaxException;

import org.omnione.did.wallet.crypto.GDPCryptoHelperServer;
import org.omnione.did.wallet.eoscommander.crypto.digest.Ripemd160;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.eoscommander.crypto.util.Base58;
import org.omnione.did.wallet.eoscommander.crypto.util.BitUtils;
import org.omnione.did.wallet.eoscommander.crypto.util.HexUtils;
import org.omnione.did.wallet.eoscommander.util.RefValue;
import org.omnione.did.wallet.eoscommander.util.StringUtils;
import org.omnione.did.wallet.util.GDPLogger;
import org.omnione.did.wallet.zkp.revoc.utils.AMCLUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by swapnibble on 2018-02-02.
 */

public class EosEcUtil {

	public static final String PREFIX_K1 = "K1";

	public static final String PREFIX_R1 = "R1";

	// public static byte[] decodeEosCrypto(String base58Data, RefValue<CurveParam>
	// curveParamRef, RefValue<Long> checksumRef ){
	//
	// final byte[] retKeyData;
	//
	// final String typePrefix;
	// if ( base58Data.startsWith( EOS_PREFIX ) ) {
	//
	// if ( base58Data.startsWith( PREFIX_K1, EOS_PREFIX.length())) {
	// typePrefix = PREFIX_K1;
	// }
	// else
	// if ( base58Data.startsWith( PREFIX_R1, EOS_PREFIX.length())) {
	// typePrefix = PREFIX_R1;
	// }
	// else {
	// typePrefix = null;
	// }
	//
	// retKeyData = getBytesIfMatchedRipemd160( base58Data.substring( EOS_PREFIX.length()
	// ), typePrefix, checksumRef);
	// }
	// else{
	// typePrefix = null;
	// retKeyData = getBytesIfMatchedSha256( base58Data, checksumRef );
	// }
	//
	// if ( curveParamRef != null) {
	// curveParamRef.data = EcTools.getCurveParam( PREFIX_R1.equals( typePrefix ) ?
	// CurveParam.SECP256_R1 : CurveParam.SECP256_K1);
	// }
	//
	// return retKeyData;
	// }

	public static byte[] extractFromRipemd160(String base58Data) {
		byte[] data = Base58.decode(base58Data);
		if (data[0] == data.length) {
			return Arrays.copyOfRange(data, 2, data.length);
		}

		return null;
	}

	// public static byte[] getBytesIfMatchedRipemd160(String base58Data, String prefix,
	// RefValue<Long> checksumRef ){
	// byte[] prefixBytes = StringUtils.isEmpty(prefix) ? new byte[0] : prefix.getBytes();
	//
	// byte[] data= Base58.decode( base58Data.substring( prefixBytes.length));
	//
	// byte[] toHashData = new byte[data.length - 4 + prefixBytes.length];
	// System.arraycopy( data, 0, toHashData, 0, data.length - 4); // key data
	//
	// System.arraycopy( prefixBytes, 0, toHashData, data.length - 4, prefixBytes.length);
	//
	// Ripemd160 ripemd160 = Ripemd160.from( toHashData); //byte[] data, int startOffset,
	// int length
	// long checksumByCal = BitUtils.uint32ToLong( ripemd160.bytes(), 0);
	// long checksumFromData= BitUtils.uint32ToLong(data, data.length - 4 );
	// if ( checksumByCal != checksumFromData ) {
	// throw new IllegalArgumentException("Invalid format, checksum mismatch");
	// }
	//
	// if ( checksumRef != null ){
	// checksumRef.data = checksumFromData;
	// }
	//
	// return Arrays.copyOfRange(data, 0, data.length - 4);
	// }

	public static byte[] getBytesIfMatchedRipemd160(String base58Data, String prefix,
			RefValue<Long> checksumRef) {
		byte[] prefixBytes = StringUtils.isEmpty(prefix) ? new byte[0]
				: prefix.getBytes();

		byte[] data = Base58.decode(base58Data);

		byte[] toHashData = new byte[data.length - 4 + prefixBytes.length];
		System.arraycopy(data, 0, toHashData, 0, data.length - 4); // key data

		System.arraycopy(prefixBytes, 0, toHashData, data.length - 4, prefixBytes.length);

		Ripemd160 ripemd160 = Ripemd160.from(toHashData); // byte[] data, int startOffset,
															// int length

		long checksumByCal = BitUtils.uint32ToLong(ripemd160.bytes(), 0);
		long checksumFromData = BitUtils.uint32ToLong(data, data.length - 4);
		if (checksumByCal != checksumFromData) {
			throw new IllegalArgumentException("Invalid format, checksum mismatch");
		}

		if (checksumRef != null) {
			checksumRef.data = checksumFromData;
		}

		return Arrays.copyOfRange(data, 0, data.length - 4);
	}

	public static byte[] getBytesIfMatchedSha256(String base58Data,
			RefValue<Long> checksumRef) {
		byte[] data = Base58.decode(base58Data);

		// offset 0은 제외, 뒤의 4바이트 제외하고, private key 를 뽑자
		Sha256 checkOne = Sha256.from(data, 0, data.length - 4);
		Sha256 checkTwo = Sha256.from(checkOne.getBytes());
		if (checkTwo.equalsFromOffset(data, data.length - 4, 4)
				|| checkOne.equalsFromOffset(data, data.length - 4, 4)) {

			if (checksumRef != null) {
				checksumRef.data = BitUtils.uint32ToLong(data, data.length - 4);
			}

			return Arrays.copyOfRange(data, 1, data.length - 4);
		}

		throw new IllegalArgumentException("Invalid format, checksum mismatch");
	}

	// public static String encodeEosCrypto(byte[] data, CurveParam curveParam ) {
	// boolean isR1 = ( null != curveParam ) && curveParam.isType( CurveParam.SECP256_R1);
	//
	// byte[] toHashData = new byte[ data.length + (isR1 ? PREFIX_R1.length() : 0) ];
	// System.arraycopy( data, 0, toHashData, 0, data.length);
	// if ( isR1 ) {
	// System.arraycopy( PREFIX_R1.getBytes(), 0, toHashData, data.length,
	// PREFIX_R1.length());
	// }
	//
	// byte[] result = new byte[ data.length + 4 ];
	//
	// Ripemd160 ripemd160 = Ripemd160.from( toHashData); //byte[] data, int startOffset,
	// int length
	// byte[] checksumBytes = ripemd160.bytes();
	//
	// System.arraycopy( data, 0, result, 0, data.length); // copy source data
	// System.arraycopy( checksumBytes, 0, result, data.length, 4); // copy checksum data
	//
	// return EOS_PREFIX + ( isR1 ? PREFIX_R1 : "") + Base58.encode( result );
	// }

	public static String encodeEosCrypto(String prefix, CurveParam curveParam,
			byte[] data) {
		String typePart = "";
		if (curveParam != null) {
			if (curveParam.isType(CurveParam.SECP256_K1)) {
				typePart = PREFIX_K1;
			}
			else if (curveParam.isType(CurveParam.SECP256_R1)) {
				typePart = PREFIX_R1;
			}
		}

		byte[] toHashData = new byte[data.length + typePart.length()];
		System.arraycopy(data, 0, toHashData, 0, data.length);
		if (typePart.length() > 0) {
			System.arraycopy(typePart.getBytes(), 0, toHashData, data.length,
					typePart.length());
		}

		byte[] dataToEncodeBase58 = new byte[data.length + 4];

		Ripemd160 ripemd160 = Ripemd160.from(toHashData);
		byte[] checksumBytes = ripemd160.bytes();

		System.arraycopy(data, 0, dataToEncodeBase58, 0, data.length); // copy source data
		System.arraycopy(checksumBytes, 0, dataToEncodeBase58, data.length, 4); // copy
																				// checksum
																				// data

		
		String result;
		if (StringUtils.isEmpty(typePart)) {
			result = prefix;
		}
		else {
			result = prefix + EOS_CRYPTO_STR_SPLITTER + typePart
					+ EOS_CRYPTO_STR_SPLITTER;
		}
		return result + Base58.encode(dataToEncodeBase58);
	}

	private static final String EOS_CRYPTO_STR_SPLITTER = "_";

	public static String[] safeSplitEosCryptoString(String cryptoStr) {
		if (StringUtils.isEmpty(cryptoStr)) {
			return new String[] { cryptoStr };
		}

		try {
			return cryptoStr.split(EOS_CRYPTO_STR_SPLITTER);
		}
		catch (PatternSyntaxException e) {
//			e.printStackTrace();
			return new String[] { cryptoStr };
		}
	}

	public static String concatEosCryptoStr(String... strData) {

		String result = "";

		for (int i = 0; i < strData.length; i++) {
			result += strData[i]
					+ (i < strData.length - 1 ? EOS_CRYPTO_STR_SPLITTER : "");
		}
		return result;
	}

	public static CurveParam getCurveParamFrom(String curveType) {
		return EcTools.getCurveParam(PREFIX_R1.equals(curveType) ? CurveParam.SECP256_R1
				: CurveParam.SECP256_K1);
	}

	// public static EosCryptoProperty getEosCryptoProperty( String cryptoStr ) {
	// if ( StringUtils.isEmpty( cryptoStr)) {
	// return new EosCryptoProperty( cryptoStr );
	// }
	//
	// String[] splitted = null;
	// try {
	// splitted = cryptoStr.split(EOS_CRYPTO_STR_SPLITTER);
	//
	// if ( splitted == null || splitted.length <= 1) {
	// return new EosCryptoProperty( cryptoStr );
	// }
	//
	// return new EosCryptoProperty( splitted[0], null, splitted[1]);
	// }
	// }
	public static byte[] genSymmetricKey(EosPrivateKey myEosPrivateKey,EosPublicKey otherEosPublicKey) throws Exception {
		GDPCryptoHelperServer cryptoHelperServer = new GDPCryptoHelperServer();
		ECPrivateKey myEcPrivateKey = cryptoHelperServer.eosPriKeyToECPriKey(myEosPrivateKey.getBytes(), GDPCryptoHelperServer.CurveParamEnum.SECP256_K1);
		ECPublicKey otherEcPublicKey = cryptoHelperServer.getEcPublicKey(otherEosPublicKey.getBytes(), GDPCryptoHelperServer.CurveParamEnum.SECP256_K1);
		byte[] symmetricKey = cryptoHelperServer.getSharedSecret(myEcPrivateKey, otherEcPublicKey);

		return symmetricKey;
	}

	public static String encrypt(String key, String text) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec keySpec = new SecretKeySpec(key.substring(0, 16).getBytes(), "AES");
		IvParameterSpec ivParamSpec = new IvParameterSpec(key.substring(0, 16).getBytes());
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

		byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
		return Base58.encode(encrypted);
	}

	public static String decrypt(String key, String cipherText) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec keySpec = new SecretKeySpec(key.substring(0, 16).getBytes(), "AES");
		IvParameterSpec ivParamSpec = new IvParameterSpec(key.substring(0, 16).getBytes());
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

		byte[] decodedBytes = Base58.decode(cipherText);
		byte[] decrypted = cipher.doFinal(decodedBytes);
		return new String(decrypted, "UTF-8");
	}

	public static void main(String[] args) throws Exception {
		GDPLogger.FLAG = false;

		EosPrivateKey priKeyA = new EosPrivateKey("5Jv2kGgw1U8vYTmLQo12JsQV3tvquoBdKfWj1f7ACE4FMf2HBca");
		EosPublicKey pubKeyA = priKeyA.getPublicKey();
		System.out.println("A 개인키: "+priKeyA.toString());
		System.out.println("A 공개키: "+pubKeyA.toString());

//		EosPrivateKey priKeyB = new EosPrivateKey(CurveParam.SECP256_K1);
//		EosPublicKey pubKeyB = priKeyB.getPublicKey();
//		System.out.println("B 개인키: "+priKeyB.toString());
//		System.out.println("B 공개키: "+pubKeyB.toString());


		EosPrivateKey priKeyB = new EosPrivateKey(CurveParam.SECP256_K1);
		EosPublicKey pubKeyB = priKeyB.getPublicKey();
		System.out.println("B 개인키: "+priKeyB.toString());
		System.out.println("B 공개키: "+pubKeyB.toString());
		byte[] symmetricKey1 = genSymmetricKey(priKeyA, pubKeyB);

		String secretKey = Sha256.from(symmetricKey1).toString();
		String source = "{\"to\":\"nftusernum11\",\"quantity\":\"1 NFT\",\"uris\":[\"www.naver.com\"],\"name\":\"test\",\"memo\":\"memo\"}";
		System.out.println("원문: "+source);
		System.out.println("A 개인키와 B 공개키로 생성한 대칭키: "+ AMCLUtils.byteArrayToHex(symmetricKey1));
		String encrypt = encrypt(secretKey, source);

		// 암호화 된 데이터를 hash
		Sha256 digest = Sha256.from(source.getBytes());
		EcSignature ecSignature = EcDsa.sign(digest, priKeyA);

		System.out.println("======= B에게 m(암호화 데이터), hash(m), 서명 데이터 전송 =======");
		System.out.println("m: "+encrypt);
		System.out.println("hash(m): "+digest);
		System.out.println("서명값: "+ecSignature.toString());
		System.out.println("============================================================");
		System.out.println("\n");

		Sha256 sigSourceHash = Sha256.from(encrypt.getBytes());
		EosPublicKey recovered = EcDsa.recoverPubKey(sigSourceHash.getBytes(), ecSignature);

		if (!pubKeyA.equals(recovered)) {
			System.out.println("검증 실패");
			return;
		}

		System.out.println("============ 서명 값에서 송신자의 A 공개키가 복구되는지 확인 ============");
		System.out.println("A 공개키: "+pubKeyA.toString());
		System.out.println("hash(m): "+sigSourceHash);
		System.out.println("서명값: "+ecSignature.toString());
		System.out.println("===========================================================");

		System.out.println("검증 성공");

		byte[] symmetricKey2 = genSymmetricKey(priKeyB, pubKeyA);

		System.out.println("B 개인키와 A 공개키로 생성한 대칭키: "+ AMCLUtils.byteArrayToHex(symmetricKey2));
		String decrypt = decrypt(secretKey, encrypt);
		System.out.println("복호화: "+decrypt);
	}


}
