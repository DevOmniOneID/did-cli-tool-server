package org.omnione.did.wallet.user;

/**
 * 서버 단독 테스트에 필요한 테스트 클라이언트
 * <pre>History:</b>
 *		Eliot, 2018.09.13 최초작성
 * </pre>
 *
 * @author Eliot
 * @version 1.0
 * @see None
 */

import java.security.KeyPair;
import java.security.PrivateKey;

import org.omnione.did.wallet.crypto.GDPCryptoConst;
import org.omnione.did.wallet.crypto.GDPCryptoHelperServer;
import org.omnione.did.wallet.data.GDPUserRegData;
import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.util.GDPBase64;
import org.omnione.did.wallet.util.GDPLogger;

public class SampleClient {

	public String makeRegJson() {
		GDPCryptoHelperServer cryptoHelperServer = new GDPCryptoHelperServer();

		KeyPair keyPair = cryptoHelperServer.generateKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();

		// USER scenario
		// 1. 사용자 등록 서명

		// 1.1 서명 원문 : 사용자 개인 정보 (주민번호,전화번)
		String signSource = "790910-xxxxxxx,010-1234-1234";

		// 1.2 사용자 전자서명
		byte[] signData = null;
		try {
			signData = cryptoHelperServer.sign(GDPCryptoConst.SIG_ALG_SHA256_ECDSA,
					signSource.getBytes(), privateKey);
		} catch (IWException e) {
//			e.printStackTrace();
		}
		
		String signBase64 = GDPBase64.encodeUrlString(signData);

		// 1.3 사용자 공개키
		byte[] publicKey = keyPair.getPublic().getEncoded();
		String publicKeyBase64 = GDPBase64.encodeUrlString(publicKey);

		GDPUserRegData userRegistrationData = new GDPUserRegData();
		userRegistrationData.signSource = signSource;
		userRegistrationData.publicKeyBase64Url = publicKeyBase64;
		userRegistrationData.signBase64Url = signBase64;

		String resultJson = userRegistrationData.toJson();
		GDPLogger.printPrettyJson("user dump", resultJson);

		return resultJson;
	}

}
