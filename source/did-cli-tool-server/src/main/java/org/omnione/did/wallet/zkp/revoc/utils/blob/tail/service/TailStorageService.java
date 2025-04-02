package org.omnione.did.wallet.zkp.revoc.utils.blob.tail.service;

import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.dao.TailsDao;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.dto.TailFileDto;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.util.RevokeFactory;

public class TailStorageService {

    private TailsDao tailsDao;

    public TailStorageService() {
        tailsDao = RevokeFactory.getTailsDao();
    }

    public String getTailsLocation() {
        return tailsDao.getPath();
    }

    public void tailFileDelete(String filePath) {
        tailsDao.tailFileDelete(filePath);
    }

    public String append(String id, byte[] hash) throws ZkpException {
        TailFileDto tailFile = new TailFileDto(id, hash);
        return tailsDao.saveTailsFile(tailFile);
    }

    public TailFileDto getTailFile(String id) throws ZkpException {
        return tailsDao.getTailFile(id);
    }
}
