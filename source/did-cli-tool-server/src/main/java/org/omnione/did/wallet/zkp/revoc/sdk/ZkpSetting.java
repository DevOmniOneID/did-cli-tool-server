package org.omnione.did.wallet.zkp.revoc.sdk;

import org.omnione.did.wallet.zkp.revoc.enums.IssuanceType;

/**
 * Copyright 2021 Raoncorp, Inc.
 * core_sdk
 * Class: ZkpSetting (singleton)
 * Created by djpark on 2021/03/18.
 *
 * Description:
 */
public class ZkpSetting {

    private static ZkpSetting instance;

    private boolean isEnabledUserInfoToServer;
    private boolean isSupportedRevocation;
    private IssuanceType issuanceType;
    private int maxCredNumber;
    private String tailsBasePath;
    private boolean isWalletCrypto;

    private ZkpSetting() {}

    public static synchronized ZkpSetting getInstance() {

        if (instance == null) {
            instance = new ZkpSetting();
            instance.setEnabledUserInfoToServer(true);
            instance.setSupportedRevocation(false);
//            instance.setWalletCrypto(true);
            instance.setIssuanceType(IssuanceType.ISSUANCE_ON_DEMAND);
            instance.setMaxCredNumber(1000);
            instance.setTailsBasePath("/src/tails");
        }
        return instance;
    }

    /**
     * cred value 를 누가 생성할것인가, true = server , false = prover
     * 기본값을 true로 server가 생성한다.
     */
    public void setEnabledUserInfoToServer(boolean isEnabled) {
        isEnabledUserInfoToServer = isEnabled;
    }

    public boolean isEnabledUserInfoToServer() {
        return isEnabledUserInfoToServer;
    }

    /**
     * 폐기 모드 타입 반환 (IssuanceType.java 참고)
     */
    public IssuanceType getIssuanceType() {
        return issuanceType;
    }

    /**
     * 폐기 모드 타입 설정 (IssuanceType.java 참고)
     */
    public void setIssuanceType(IssuanceType issuanceType) {
        this.issuanceType = issuanceType;
    }

    /**
     * 최대 발급 가능한 인증서 개수 반환
     */
    public int getMaxCredNumber() {
        return maxCredNumber;
    }

    /**
     * 최대 발급 가능한 인증서 개수 설정 (tails file 생성시 factor 개수가 됨)
     */
    public void setMaxCredNumber(int maxCredNumber) {
        this.maxCredNumber = maxCredNumber;
    }

    /**
     * tails file 경로 반환
     */
    public String getTailsBasePath() {
        return tailsBasePath;
    }

    /**
     * tails file 경로 설정 (System.getProperty("user.dir") + "tailsBasePath" 에 생성 )
     */
    public void setTailsBasePath(String tailsBasePath) {
        this.tailsBasePath = tailsBasePath;
    }

    /**
     * wallet 데이터 암호화 유무
     */
    public boolean isWalletCrypto() {
        return isWalletCrypto;
    }

    /**
     * wallet 데이터 암호화 설정
     */
    public void setWalletCrypto(boolean walletCrypto) {
        isWalletCrypto = walletCrypto;
    }

    /**
     * 비해지증명 사용유무 설정
     */
    public void setSupportedRevocation(boolean isSupportedRevocation) {
        this.isSupportedRevocation = isSupportedRevocation;
    }

    /**
     * 비해지증명 사용유무 반환
     */
    public boolean isSupportedRevocation() {
        return isSupportedRevocation;
    }
}

