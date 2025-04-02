//package org.omnione.did.wallet.util;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//
//import com.raonsecure.license.RSLicenseChecker;
//import org.omnione.did.wallet.exception.IWErrorCode;
//import org.omnione.did.wallet.exception.IWException;
//import org.omnione.did.wallet.key.store.IWKeyFile;
//
//
//public final class OmniOneLicenseChcker {
//
//	private static int resultCode = -1000;
//	private static String[] options = {};
//	private static boolean useVault = false;
//	private static boolean useFabric = false;
//
//	private final static String MATCHED_LICENSE_FILE_NAME_MOBILE = "omnione_enterprise_mclient.rsl";
//	private final static String MATCHED_LICENSE_FILE_NAME_SERVER = "omnione_enterprise_server.rsl";
//	private final static String MATCHED_LICENSE_PRODUCT_NAME = "OmniOne Enterprise";
//	private final static String MATCHED_FEATURE_NAME_VAULT = "vault";
//	private final static String MATCHED_OPTION_NAME_FABRIC = "fabric";
//
//	static {
//		try {
//			Class<?> defultValue = Class.forName("com.raonsecure.omnione.core.OmniLicenseDefault");
//			Field field = defultValue.getField("CHECK_VALUE");
//			int value = field.getInt(null);
//			GDPLogger.debug("OmniLicenseDefault CHECK_VALUE:" + value);
//			resultCode = value;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//
//	public static String getRSLicenseSDKVersion(Object context) {
//		RSLicenseChecker rslChecker = null;
//
//		Method[] methods = RSLicenseChecker.class.getDeclaredMethods();
//		try {
//			for (Method method : methods) {
//				String name = method.getName();
//				if (name.equals("getInstance")) {
//					rslChecker = (RSLicenseChecker) method.invoke(null, context);
//					break;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//
//		return rslChecker.getSdkVersion();
//	}
//
//	public static String getRSLicenseSDKVersion() {
//		RSLicenseChecker rslChecker = new RSLicenseChecker();
//		return rslChecker.getSdkVersion();
//	}
//
////	public static String getOmniEntSDKVersion() {
////		return OmniBuildConfig.VERSION;
////	}
//
//
//
//	public static boolean check(Object context) throws IWException {
//		return check(null, context);
//	}
//
//
//	public static boolean check(String licenseFilePath) throws IWException {
//
//		return check(licenseFilePath, null);
//	}
//
//	public static boolean check(String licenseFilePath, Object context) throws IWException {
//		// 모바일 라이선스 체크
//		if (licenseFilePath == null) {
//			licenseFilePath = MATCHED_LICENSE_FILE_NAME_MOBILE;
//		} else { // 서버 라이선스 체크
//			if (licenseFilePath.indexOf(MATCHED_LICENSE_FILE_NAME_SERVER) == -1) {
//				resultCode = IWErrorCode.ERR_CODE_LICENSE_CHECKER_INVALID_LICENSE_FILE_NAME.getCode();
//				throw new IWException(IWErrorCode.ERR_CODE_LICENSE_CHECKER_INVALID_LICENSE_FILE_NAME);
//			}
//			//tykim: 서버쪽에서 사용할 경우 키 로드를 매번 하지 않도록함
//			IWKeyFile.ONETIME_LOAD = true;
//		}
//
//
//		if (resultCode != -1000) {
//			return 0 <= resultCode;
//		}
//
//		RSLicenseChecker rslChecker = null;
//
//		Method[] methods = RSLicenseChecker.class.getDeclaredMethods();
//		try {
//			for (Method method : methods) {
//				String name = method.getName();
//				if (name.equals("getInstance")) {
//					rslChecker = (RSLicenseChecker) method.invoke(null, context);
//					break;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//		if (rslChecker == null) {
//			rslChecker = new RSLicenseChecker();
//		}
//
//		resultCode = rslChecker.checkLicenseFile(licenseFilePath);
//		options = rslChecker.getSchema_Options().split(",");
//		for(int i=0;options.length>i;i++) {
//			if(options[i].toLowerCase().equals(MATCHED_FEATURE_NAME_VAULT)) {
//				useVault=true;
//				break;
//			}
//		}
//
//		/*
//		 * RSLicense 내의 Option 중 fabric 확인
//		 * */
//		if(options[0].equals(MATCHED_OPTION_NAME_FABRIC)){
//			useFabric=true;
//		}
//		else {
//			useFabric=false;
//			resultCode=IWErrorCode.ERR_CODE_LICENSE_CHECKER_INVALID_OPTION_NAME.getCode();
//			throw new IWException(IWErrorCode.ERR_CODE_LICENSE_CHECKER_INVALID_OPTION_NAME);
//		}
//
//		if (0 > resultCode) {
//			printLog();
//			throw new IWException(resultCode,"Please refer to the license error code ");
//		}
//		else {
//
//			if(!rslChecker.getProductName().equals(MATCHED_LICENSE_PRODUCT_NAME)){
//				resultCode = IWErrorCode.ERR_CODE_LICENSE_CHECKER_INVALID_PRODUCT_NAME.getCode();
//            	throw new IWException(IWErrorCode.ERR_CODE_LICENSE_CHECKER_INVALID_PRODUCT_NAME);
//
//			}
//
//		}
//
//
//		Object type = null;
//		try {
//			for (Method method : methods) {
//				String name = method.getName();
//				if (name.equals("doubleCheckLicense")) {
//					type = method.invoke(rslChecker);
//					break;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//
//		if (type instanceof Integer) {
//			Integer intResultCode = (Integer) type;
//			resultCode = intResultCode;
//		} else if (type instanceof Boolean) {
//			Boolean booleanResultCode = (Boolean) type;
//			if (!booleanResultCode) {
//				return false;
//			} else {
//				resultCode = 0;
//			}
//		}
//
//		if (0 > resultCode) {
//			printLog();
//			return false;
//		}
//		return true;
//	}
//
//	public static void check() throws IWException {
//		if (0 > resultCode) {
//			printLog();
//		}
//
//		if (resultCode == -1000) {
//			throw new IWException(IWErrorCode.ERR_CODE_NO_LICENSE_CHECK);
//		} else if (0 > resultCode) {
//			throw new IWException(resultCode,"Please refer to the license error code ");
//		}
//	}
//
//	public static int getResultCode() {
//		return resultCode;
//	}
//
//	public static String[] getOptions() {
//		return options;
//	}
//
//	public static boolean isUseVault() {
//		return useVault;
//	}
//
//	private static void printLog() {
//		System.out.println("OmniOne License Check Fail Result Code = " + resultCode);
//		if (!GDPLogger.FLAG) {
//			GDPLogger.FLAG = true;
//		}
//		GDPLogger.debug("OmniOne License Check Fail Result Code = " + resultCode);
//	}
//}
