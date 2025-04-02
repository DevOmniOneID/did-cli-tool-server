package org.omnione.did.wallet.zkp.revoc.data.dto;

import org.omnione.did.wallet.zkp.exceptions.ZkpException;

public interface ZkpEncodeMethod {

    String getEncodeString() throws ZkpException;
}
