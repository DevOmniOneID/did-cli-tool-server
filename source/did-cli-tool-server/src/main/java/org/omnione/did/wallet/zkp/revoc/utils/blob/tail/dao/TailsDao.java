package org.omnione.did.wallet.zkp.revoc.utils.blob.tail.dao;

import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.db.TailsDB;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.dto.TailFileDto;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.util.RevokeFactory;

public class TailsDao {

    private TailsDB db;

    public TailsDao() {
        db = RevokeFactory.getTailsDB();
    }

    public String getPath() {
        return db.getDirPath();
    }

    public void tailFileDelete(String filePath) {
        db.deleteTailFile(filePath);
    }

    public TailFileDto getTailFileById(String id) throws ZkpException {
        return db.getTailFileById(id);
    }

    public String saveTailsFile(TailFileDto tailFile) throws ZkpException {
        return db.saveTailFile(tailFile);
    }

    public TailFileDto getTailFile(String id) throws ZkpException {
        return db.getTailFileById(id);
    }
}
