package org.omnione.did.wallet.data.iw.profile;

import org.omnione.did.wallet.key.data.AESType;

public enum EncryptTypeEnum {
	NONE(0), AES_128(1), AES_256(2);

	private int val;

	private EncryptTypeEnum(int val) {
		this.val = val;
	}

	public int getVal() {
		return val;
	}

	public static EncryptTypeEnum getEnum(int val) {
		for (EncryptTypeEnum b : EncryptTypeEnum.values()) {
			if (b.getVal() == val) {
				return b;
			}
		}
		return EncryptTypeEnum.NONE;
	}

	public static AESType getAESEnum(int val) {
		EncryptTypeEnum encryptTypeEnum = getEnum(val);
		if (encryptTypeEnum == AES_128) {
			return AESType.AES128;
		} else if (encryptTypeEnum == AES_256) {
			return AESType.AES256;
		}
		return null;
	}
}
