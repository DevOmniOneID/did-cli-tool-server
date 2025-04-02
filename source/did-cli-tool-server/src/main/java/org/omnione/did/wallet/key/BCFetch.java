package org.omnione.did.wallet.key;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.omnione.did.wallet.data.did.DIDs;
import org.omnione.did.wallet.data.iw.Assertion;
import org.omnione.did.wallet.data.iw.profile.Filter;
import org.omnione.did.wallet.data.iw.profile.VerifyInnerProfile;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.BCFetchInterface.*;
import org.omnione.did.wallet.util.GDPBase64;
import org.omnione.did.wallet.util.GDPLogger;
import org.omnione.did.wallet.util.http.HttpClient;
import org.omnione.did.wallet.util.http.HttpException;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BCFetch {

	public enum KEY_TYPE {
		SHA256("sha256"), NAME("name");

		private final String text;

		KEY_TYPE(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public enum INDEX_POSITION {
		PRIMARY("primary"), SECONDARY("secondary");

		private final String text;

		INDEX_POSITION(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public String CODE = "omnione.ent";
	public String SCOPE = "omnione.ent";
	public String TRUE = "true";

	public HttpClient httpClient;
	public String requestUrl;

	public BCFetch(String requestUrl) {
		httpClient = new HttpClient();
		this.requestUrl = requestUrl + "/v1/chain/get_table_rows";

	}

	private String hexIncrease(String strHex, int nIncValue) {
		BigInteger decimal = new BigInteger(strHex, 16);
		decimal = decimal.add(BigInteger.valueOf(nIncValue));

		// 입력받은 문자열 길이 만큼 패딩을 위한 값을 지정한다.
		String strPadOption = "%".concat(String.format("%ss", strHex.length()));

		String strPadded = String.format(strPadOption, decimal.toString(16)).replace(' ', '0');
		return strPadded;
	}

	/**
	 * BlockChain API 직접 호출하기 위한 파라미터 조립
	 *
	 * @param strTableName  table 명
	 * @param strIndexPos   'primary', 'secondary' 선택
	 * @param strKeyType    'sha256', 'name' 선택
	 * @param strLowerBound indexPos 가 'secondary' 인 경우 Hash
	 * @param strUpperBound lowerbound + 1
	 * @return
	 */
	private String getRequestJsonString(String strTableName, String strIndexPos, String strKeyType,
			String strLowerBound, String strUpperBound) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("code", CODE);
		jsonObject.addProperty("scope", SCOPE);
		jsonObject.addProperty("json", TRUE);

		jsonObject.addProperty("table", strTableName);
		jsonObject.addProperty("index_position", strIndexPos);
		jsonObject.addProperty("key_type", strKeyType);
		jsonObject.addProperty("lower_bound", strLowerBound);
		jsonObject.addProperty("upper_bound", strUpperBound);

		String jsonString = jsonObject.toString();
		return jsonString;
	}

	/**
	 * 블록체인에 등록된 DID 문서 가져오는 API
	 *
	 * @param did          조회할 did
	 * @param didsCallBack 결과값을 전달할 callback 리스너
	 * @return
	 */

	public void getDIDs(String did, DIDsCallBack didsCallBack) throws IWException {
		// did check
		if (did.equals("") || did == null) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_DID);
		}

		// callback null check
		if (didsCallBack == null) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_CALLBACK);
		}

		Sha256 hash = Sha256.from(did.getBytes());

		String lowerBound = hash.toString();
		String upperBound = hexIncrease(lowerBound, 1);

		String jsonString = getRequestJsonString("diddoc", INDEX_POSITION.SECONDARY.toString(),
				KEY_TYPE.SHA256.toString(), lowerBound, upperBound);
		try {
			String resultMsg = httpClient.send(requestUrl, jsonString);
			
			JsonObject jsonobject = new JsonObject();
			JsonParser jsonParser = new JsonParser();

			JsonElement element = jsonParser.parse(resultMsg);
			jsonobject = element.getAsJsonObject();
			JsonArray memberArray = (JsonArray) jsonobject.get("rows");

			if (memberArray.size() == 1) {
				JsonObject object = (JsonObject) memberArray.get(0);

				String did_document = object.getAsJsonObject("did_document").toString();

				DIDs dids = new DIDs();
				dids.fromJson(did_document);

				didsCallBack.success(dids);
				
			} else {
				if (jsonobject.get("message") != null && !jsonobject.get("message").equals("")) {
					didsCallBack.failure("[" + IWErrorCode.ERR_CODE_BCFETCH_NODE_SERVER_ERROR.getCode() + "] " + jsonobject.get("message").getAsString());
				} else { // rows가 빈 배열 일때 
					IWException e = new IWException(IWErrorCode.ERR_CODE_BCFETCH_DID_NOT_EXIST);
					didsCallBack.failure("[" + e.getErrorCode() + "] " + e.getErrorMsg());
				}
			}

		} catch (HttpException e) {
			didsCallBack.failure(e.getErrorMsg());
		}

	}

	/**
	 * 주어진 VC ID 로 해당 VC의 상태정보를 반환
	 *
	 * @param vcId             조회할 vcId
	 * @param vcStatusCallBack 결과값을 전달할 callback 리스너
	 * @return
	 */

	public void getVCStatus(String vcId, VCStatusCallBack vcStatusCallBack) throws IWException {

		if (vcId.equals("") || vcId == null) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_VC_ID);
		}

		if (vcStatusCallBack == null) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_CALLBACK);
		}

		Sha256 hash = Sha256.from(vcId.getBytes());

		String lowerBound = hash.toString();
		String upperBound = hexIncrease(lowerBound, 1);

		String jsonString = getRequestJsonString("vcstatus", INDEX_POSITION.SECONDARY.toString(),
				KEY_TYPE.SHA256.toString(), lowerBound, upperBound);
		try {
			String resultMsg = httpClient.send(requestUrl, jsonString);
			
			JsonObject jsonobject = new JsonObject();
			JsonParser jsonParser = new JsonParser();

			JsonElement element = jsonParser.parse(resultMsg);
			jsonobject = element.getAsJsonObject();
			JsonArray memberArray = (JsonArray) jsonobject.get("rows");

			if (memberArray.size() == 1) {
				JsonObject object = (JsonObject) memberArray.get(0);
	
				int intVCStatusNum = object.get("status_code").getAsInt();
				
				VCStatus vcStatus = VCStatus.fromValue(intVCStatusNum);

				vcStatusCallBack.success(vcStatus);
			} else {
				if (jsonobject.get("message") != null && !jsonobject.get("message").equals("")) {
					vcStatusCallBack.failure("[" + IWErrorCode.ERR_CODE_BCFETCH_NODE_SERVER_ERROR.getCode() + "] " + jsonobject.get("message").getAsString());
				} else {
					VCStatus vcStatus = VCStatus.fromValue(-1);
					vcStatusCallBack.success(vcStatus);
				}
			}

		} catch (HttpException e) {
			vcStatusCallBack.failure(e.getErrorMsg());
		}
	}

	/**
	 * - 주어진 SP DID 가 SP DID 문서로 등록된 여부를 반환하는 API
	 *
	 * @param spDid                        조회할 spDid
	 * @param didRegistrationCheckCallBack 결과값을 전달할 callback 리스너
	 * @return
	 */
	public void isRegisteredSPDIDs(String spDid, DIDRegistrationCheckCallBack didRegistrationCheckCallBack)
			throws IWException {
		// did check
		if (spDid.equals("") || spDid == null) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_DID);
		}

		if (didRegistrationCheckCallBack == null) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_CALLBACK);
		}

		Sha256 hash = Sha256.from(spDid.getBytes());

		String lowerBound = hash.toString();
		String upperBound = hexIncrease(lowerBound, 1);

		String jsonString = getRequestJsonString("sp", INDEX_POSITION.SECONDARY.toString(), KEY_TYPE.SHA256.toString(),
				lowerBound, upperBound);

		try {
			String resultMsg = httpClient.send(requestUrl, jsonString);

			JsonObject jsonobject = new JsonObject();
			JsonParser jsonParser = new JsonParser();

			JsonElement element = jsonParser.parse(resultMsg);
			jsonobject = element.getAsJsonObject();
			JsonArray memberArray = (JsonArray) jsonobject.get("rows");
			if (memberArray != null) {
				if (memberArray.size() == 1) {
					didRegistrationCheckCallBack.success(true);
				} else {
					didRegistrationCheckCallBack.success(false);
				}
			} else {
				String errorMessage = jsonobject.get("message").getAsString();
				if (errorMessage != null && !errorMessage.equals("")) {
					didRegistrationCheckCallBack.failure("[" + IWErrorCode.ERR_CODE_BCFETCH_NODE_SERVER_ERROR.getCode() + "] " + errorMessage);
				} else {
					didRegistrationCheckCallBack.failure("[" + IWErrorCode.ERR_CODE_BCFETCH_NODE_SERVER_ERROR.getCode() + "] " + 
							IWErrorCode.ERR_CODE_BCFETCH_NODE_SERVER_ERROR.getMsg());
				}
			}

		} catch (HttpException e) {
			didRegistrationCheckCallBack.failure(e.getErrorMsg());
		}

	}

	/**
	 * 주어진 DID로 해당 DID의 상태정보를 반환 “didrevoke” 테이블을 조회하고 리턴된 did 정보가 있으면 폐기 상태
	 * “didrevoke” 테이블을 조회하고 리턴된 did 정보가 없고 ‘1’번을 조회해 리턴된 did 정보가 있으면 발급 상태
	 * “didrevoke” 테이블을 조회하고 리턴된 did 정보가 없고 ‘1’번을 조회해 리턴된 did 정보도 없으면 미발급 상태
	 * 
	 * @param did               조회할 did
	 * @param didStatusCallBack 결과값을 전달할 callback 리스너
	 * @return
	 */
	public void getDIDStatus(String did, final DIDStatusCallBack didStatusCallBack) throws IWException {

		if (did.equals("") || did == null) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_DID);
		}

		if (didStatusCallBack == null) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_CALLBACK);
		}

		Sha256 hash = Sha256.from(did.getBytes());

		String lowerBound = hash.toString();
		String upperBound = hexIncrease(lowerBound, 1);

		String jsonString = getRequestJsonString("didrevoke", INDEX_POSITION.SECONDARY.toString(),
				KEY_TYPE.SHA256.toString(), lowerBound, upperBound);
		try {
			String resultMsg = httpClient.send(requestUrl, jsonString);

			JsonObject jsonobject = new JsonObject();
			JsonParser jsonParser = new JsonParser();

			JsonElement element = jsonParser.parse(resultMsg);
			jsonobject = element.getAsJsonObject();
			JsonArray memberArray = (JsonArray) jsonobject.get("rows");

			// 폐기상태
			if (memberArray.size() == 1) {
				DIDStatus didStatus = DIDStatus.fromValue(DIDStatus.DIDStatusRevoke.getValue());
				didStatusCallBack.success(didStatus);
			} else { // 폐기상태 X, 발급 / 미발급 상태
				getDIDs(did, new DIDsCallBack() {

					@Override
					public void success(DIDs dids) { // 발급 상태
						// TODO Auto-generated method stub
						DIDStatus didStatus = DIDStatus.fromValue(DIDStatus.DIDStatusIssued.getValue());
						didStatusCallBack.success(didStatus);
					}

					@Override
					public void failure(String errorMsg) { // 미발급 상태
						// TODO Auto-generated method stub
						DIDStatus didStatus = DIDStatus.fromValue(DIDStatus.DIDStatusNone.getValue());
						didStatusCallBack.success(didStatus);
					}

				});
			}

		} catch (HttpException e) {
			didStatusCallBack.failure(e.getErrorMsg());
		}

	}

	public void getProfileFilter(String serviceCode, FilterCallBack filterCallBack) throws IWException {

		if (filterCallBack == null) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_CALLBACK);
		}

		String jsonString = getRequestJsonString("spservice", INDEX_POSITION.PRIMARY.toString(),
				KEY_TYPE.NAME.toString(), serviceCode, serviceCode);

		try {
			String resultMsg = httpClient.send(requestUrl, jsonString);
			Gson gson = new Gson();
			JsonObject jsonRows = gson.fromJson(resultMsg, JsonObject.class);
			JsonElement jsonElement = jsonRows.get("rows");
			GDPLogger.print("element " + jsonElement.toString());

			Type listType = new TypeToken<List<ItemSpservice>>() {
			}.getType();
			List<ItemSpservice> list = gson.fromJson(jsonElement.getAsJsonArray(), listType);

			// 'rows' 배열 조회
			if (list != null && list.size() >= 1) {
				// 'rows' 배열 사이즈는 1로 간주
				ItemSpservice itemSpservice = list.get(0);
				// 'allowed_vc_type_codes' 배열 조회
				List<Assertion> assertionList = new ArrayList<Assertion>();
				if (itemSpservice.getAllowed_vctype_codes() != null
						&& itemSpservice.getAllowed_vctype_codes().size() > 0) {
					for (String strId : itemSpservice.getAllowed_vctype_codes()) {
						GDPLogger.print("strId " + strId);
						Assertion assertion = new Assertion();
						assertion.setId(strId);
						assertionList.add(assertion);
					}
				}

				// 'req_claims' 배열 조회
				List<String> requiredPrivacyList = itemSpservice.getReq_claims();

				// 'allowed_issuer_dids' 조회
				List<String> allowIssuers = itemSpservice.getAllowed_issuer_dids();

				// Debug 로그
				if (GDPLogger.FLAG) {
					for (Assertion assertion : assertionList) {
						GDPLogger.print("assertion " + assertion.getId());
					}
				}

				// Filter 설정
				Filter filter = new Filter();
				filter.setRequiredAssertionList(assertionList);
				filter.setRequiredPrivacyList(requiredPrivacyList);
				filter.setAllowIssuerList(allowIssuers);
				filter.setSpName(itemSpservice.getService_name());
				
				filterCallBack.success(filter);
			} else {
				filterCallBack.failure("["+IWErrorCode.ERR_CODE_BCFETCH_NO_REGISTERED_SERVICE_CODE.getCode()+"] " +
							IWErrorCode.ERR_CODE_BCFETCH_NO_REGISTERED_SERVICE_CODE.getMsg());

			}
		} catch (HttpException e) {
			filterCallBack.failure(e.getErrorMsg());
		}
	}

	/**
	 * QR 정보를 스캔하여 Profile 을 생성
	 * 
	 * @param strEncodedQR    입력받은 인코딩된 QR (QR 정보에는 spDID, serviceCode, nonce,
	 *                        callbackURL, encryptType 정보가 포함됨)
	 * @param profileCallBack
	 * @throws IWException
	 */
	public void getServiceProfile(String strEncodedQR, final ProfileCallBack profileCallBack) throws IWException {

		if (strEncodedQR.equals("") || strEncodedQR == null) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_QR_STRING);
		}

		if (profileCallBack == null) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_CALLBACK);
		}

		byte[] decodedQR = GDPBase64.decode(strEncodedQR, GDPBase64.URL_SAFE);
		if (decodedQR == null) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_QR_STRING);
		}

		// QR 파싱
		String strDecodedQR = new String(decodedQR);
		GDPLogger.print("strDecodedQR " + strDecodedQR);

		final ItemQR itemQR = new Gson().fromJson(strDecodedQR, ItemQR.class);
		final String strSPDID = itemQR.getSp_did();
		final String strServiceCode = itemQR.getService_code();
		GDPLogger.print("strSPDID " + strSPDID);
		GDPLogger.print("strServiceCode " + strServiceCode);

		// 'isRegisteredSPDID()' 호출하여 'SP_DID' 가 등록되어 있으면 Profile 생성을 한다.
		isRegisteredSPDIDs(strSPDID, new DIDRegistrationCheckCallBack() {
			@Override
			public void success(boolean bIsRegistered) throws IWException {
				// 'SP_DID' 가 등록되어 있으면 DID 요청을한다.

				if (bIsRegistered) {
					getDIDs(strSPDID, new DIDsCallBack() {
						@Override
						public void success(final DIDs dids) throws IWException {
							// DID 조회를 성공하면 profileFilter 를 조회한다.
							getProfileFilter(strServiceCode, new FilterCallBack() {
								@Override
								public void success(Filter filter) {
									// Profile 조합 후 리턴한다.
									VerifyInnerProfile verifyInnerProfile = makeVerifyInnerProfile(itemQR, dids,
											filter);
									profileCallBack.success(verifyInnerProfile);
								}

								// 'getProfileFilter()' 실패.
								@Override
								public void failure(String errorMsg) {
									profileCallBack.failure(errorMsg);

								}
							});
						}

						// 'getDIDs()' 실패
						@Override
						public void failure(String errorMsg) {
							profileCallBack.failure(errorMsg);
						}
					});
				} else {// not registered spdid
					IWException e = new IWException(IWErrorCode.ERR_CODE_BCFETCH_UNREGISTERED_SP_DID);
					profileCallBack.failure("[" + e.getErrorCode() + "] " + e.getErrorMsg());

				}
			}

			// 'isRegisteredSPDids()' 실패
			@Override
			public void failure(String errorMsg) {
				profileCallBack.failure(errorMsg);

			}
		});
	}

	/**
	 * Base64 인코딩되지 않은 QR 정보를 스캔하여 Profile 을 생성
	 *
	 * @param strJson    입력받은 JsonString 형태의 QR (QR 정보에는 spDID, serviceCode, nonce,
	 *                        callbackURL, encryptType 정보가 포함됨)
	 * @param profileCallBack
	 * @throws IWException
	 */
	public void getServiceProfileWithJson(String strJson, final ProfileCallBack profileCallBack) throws IWException {

		if (strJson.equals("") || strJson == null) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_QR_STRING);
		}

		// 입력받은 strJson 파라미터가 JSON 형태가 맞는지 확인.
		if (!isJSONValid(strJson)) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_JSON);
		}

		if (profileCallBack == null) {
			throw new IWException(IWErrorCode.ERR_CODE_BCFETCH_INVALID_CALLBACK);
		}

		// QR 파싱
		String strDecodedQR = new String(strJson);
		GDPLogger.print("strDecodedQR " + strDecodedQR);

		final ItemQR itemQR = new Gson().fromJson(strDecodedQR, ItemQR.class);
		final String strSPDID = itemQR.getSp_did();
		final String strServiceCode = itemQR.getService_code();
		GDPLogger.print("strSPDID " + strSPDID);
		GDPLogger.print("strServiceCode " + strServiceCode);

		// 'isRegisteredSPDID()' 호출하여 'SP_DID' 가 등록되어 있으면 Profile 생성을 한다.
		isRegisteredSPDIDs(strSPDID, new DIDRegistrationCheckCallBack() {
			@Override
			public void success(boolean bIsRegistered) throws IWException {
				// 'SP_DID' 가 등록되어 있으면 DID 요청을한다.

				if (bIsRegistered) {
					getDIDs(strSPDID, new DIDsCallBack() {
						@Override
						public void success(final DIDs dids) throws IWException {
							// DID 조회를 성공하면 profileFilter 를 조회한다.
							getProfileFilter(strServiceCode, new FilterCallBack() {
								@Override
								public void success(Filter filter) {
									// Profile 조합 후 리턴한다.
									VerifyInnerProfile verifyInnerProfile = makeVerifyInnerProfile(itemQR, dids,
											filter);
									profileCallBack.success(verifyInnerProfile);
								}

								// 'getProfileFilter()' 실패.
								@Override
								public void failure(String errorMsg) {
									profileCallBack.failure(errorMsg);

								}
							});
						}

						// 'getDIDs()' 실패
						@Override
						public void failure(String errorMsg) {
							profileCallBack.failure(errorMsg);
						}
					});
				} else {// not registered spdid
					IWException e = new IWException(IWErrorCode.ERR_CODE_BCFETCH_UNREGISTERED_SP_DID);
					profileCallBack.failure("[" + e.getErrorCode() + "] " + e.getErrorMsg());

				}
			}

			// 'isRegisteredSPDids()' 실패
			@Override
			public void failure(String errorMsg) {
				profileCallBack.failure(errorMsg);

			}
		});
	}

	/**
	 * Profile 생성
	 * 
	 * @param itemQR QR 스캔한 값을 decode 한 객체
	 * @param dids   'getDIDs()' 호출하여 리턴 받은 DIDs 객체
	 * @param filter 'getProfileFilter()' 호출하여 리턴 받은 Filter 객체
	 * @return
	 */
	private VerifyInnerProfile makeVerifyInnerProfile(ItemQR itemQR, DIDs dids, Filter filter) {
		VerifyInnerProfile verifyInnerProfile = new VerifyInnerProfile();
		verifyInnerProfile.setCallBackUrl(itemQR.getCallBackUrl());
		verifyInnerProfile.setEncryptType(itemQR.getEncryptType());
		verifyInnerProfile.setNonce(itemQR.getNonce());
		verifyInnerProfile.setPublicKey(dids.getPublicKey().get(0).getPublicKeyBase58());
		verifyInnerProfile.setFilter(filter);
		verifyInnerProfile.setType("VERIFY");
		verifyInnerProfile.setName(filter.getSpName());
		verifyInnerProfile.setSpName(filter.getSpName());
		GDPLogger.print("[makeProfile] " + verifyInnerProfile.toJson());

		return verifyInnerProfile;
	}

	/**
	 * 입력 받은 문자가 JSON 형태인지 판별
	 * @param strJson JSON 형태 검사할 문자열
	 * @return	true : JSON 형태, false : JSON 형태가 아
	 */
	private boolean isJSONValid(String strJson) {
		Gson gson = new Gson();
		try {
			gson.fromJson(strJson, Object.class);
			Object jsonObjType = gson.fromJson(strJson, Object.class).getClass();
			if(jsonObjType.equals(String.class)){
				return false;
			}
			return true;
		} catch (com.google.gson.JsonSyntaxException ex) {
			return false;
		}
	}
}
