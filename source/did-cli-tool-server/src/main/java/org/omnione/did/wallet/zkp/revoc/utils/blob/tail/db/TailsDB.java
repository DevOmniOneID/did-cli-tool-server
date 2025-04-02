package org.omnione.did.wallet.zkp.revoc.utils.blob.tail.db;

import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpSetting;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.dto.TailFileDto;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.util.FileHelper;

public class TailsDB {

    private TailFileTable table;

    public TailsDB() {

        String dbDirPath = getDirPath();
//        System.out.println("dbDirPath :"+ dbDirPath);
        FileHelper.makeDir(dbDirPath);
        table = new TailFileTable(dbDirPath);
    }

    public String getDirPath() {
        return System.getProperty("user.dir")+ ZkpSetting.getInstance().getTailsBasePath();
    }

    public void deleteTailFile(String filePath) {
        table.delete(filePath);
    }

    public TailFileDto getTailFileById(String id) throws ZkpException {

        TailFileDto tailFile = getTailsFile(id);
        return tailFile;
    }

    public TailFileDto getTailsFile(String id) throws ZkpException {
        return table.getRow(id);
    }

    public String saveTailFile(TailFileDto tailFile) throws ZkpException {
        return table.saveRow(tailFile);
    }
}
