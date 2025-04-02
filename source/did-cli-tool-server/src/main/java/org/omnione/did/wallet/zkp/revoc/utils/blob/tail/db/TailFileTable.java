package org.omnione.did.wallet.zkp.revoc.utils.blob.tail.db;


import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.dto.TailFileDto;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.util.FileHelper;

import java.io.File;

public class TailFileTable {

    private String tableDirPath;

    public TailFileTable(String dbDirPath) {

        this.tableDirPath = dbDirPath;
        FileHelper.makeDir(tableDirPath);
    }

    public String saveRow(TailFileDto data) throws ZkpException {
        String rowFilePath = getRowFilePath(data.getId());
        FileHelper.writeFile(rowFilePath, data.getTailHash());
        return data.getId();
    }

    private String getRowFilePath(String id) {
        return tableDirPath+"/"+id;
    }

    public TailFileDto getRow(String id) throws ZkpException {
        return FileHelper.readFile(getRowFilePath(id));
    }

    public void delete(String filePath) {
        FileHelper.deleteFile(filePath);
    };

    public int folderFileCount(File directory) {
        int length = 0;
        try {
            for (File file : directory.listFiles()) {
                if (file.isFile())
                    length++;  //폴더 내부 파일 갯수
                else
                    length += folderFileCount(file);
            }
        } catch (Exception e) {

        }
        return length;
    }
}
