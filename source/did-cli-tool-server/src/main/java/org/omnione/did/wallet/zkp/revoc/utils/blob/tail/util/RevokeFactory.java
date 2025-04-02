package org.omnione.did.wallet.zkp.revoc.utils.blob.tail.util;

import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.dao.TailsDao;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.db.TailsDB;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.service.TailStorageService;

// Revoke에 관련된 공유되는 객체를 보관하는 클래스
public class RevokeFactory {

    private static TailsDB db;
    private static TailsDao tailsDao;
    private static TailStorageService tailsFileService;

    public static TailsDao getTailsDao() {

        if (tailsDao == null) {
            tailsDao = new TailsDao();
        }
        return tailsDao;
    }

    public static TailsDB getTailsDB() {

        if (db == null) {
            db = new TailsDB();
        }
        return db;
    }

    public static TailStorageService getTailsFileService() {

        if (tailsFileService == null) {
            tailsFileService = new TailStorageService();
        }
        return tailsFileService;
    }

}
