package org.omnione.did.wallet.zkp.revoc.utils.blob.tail.util;

import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.dto.TailFileDto;

import java.io.*;

public class FileHelper {

    public static boolean isFileExists(String filePath) {
        File f = new File(filePath);
        if (f.isFile()) {
            return true;
        }

        return false;
    }

    public static void writeFileContents(String filePath, int data) {
        writeFileContents(filePath, data+ "");
    }

    public static String getFileContents(String filePath) {
        String rs = null;
        try {
            FileInputStream fileStream = null;
            fileStream = new FileInputStream(filePath);
            byte[] readBuffer = new byte[fileStream.available()];

            while (fileStream.read(readBuffer) != -1) {}
            rs = new String(readBuffer);
            fileStream.close();

        } catch (Exception e) {
            e.getStackTrace();
        }
        return rs;
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {

            if (file.delete()) {
                ZkpLogger.debug("succeed file{"+filePath +"} delete");
            }
            else {
                ZkpLogger.debug("failure file{"+filePath +"} delete");
            }
        }
        else {
            ZkpLogger.debug("not exist file{"+filePath +"} delete");
        }
    }

    public static void writeFileContents(String filePath, String contents) {
        BufferedOutputStream bs = null;

        try {
            bs = new BufferedOutputStream(new FileOutputStream(filePath));
            bs.write(contents.getBytes());

        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                bs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static TailFileDto readFile(String filePath) throws ZkpException{

//        System.out.println("filePath: "+filePath);
        File f = new File(filePath);

        int fileSize = (int)f.length();
//        System.out.println("tails size:" + fileSize);

        byte[] b = new byte[fileSize];

        FileInputStream fis;
        try {
            fis = new FileInputStream(filePath);
            int pos = 0;
            int size = 10;
            int temp;
            while((size = fis.read(b, pos, size)) > 0){
                pos += size;
                temp = b.length - pos;
                if(temp < 10){
                    size = temp;
                }
            }
            fis.close();
//            System.out.println("tails byte size:" + pos);
            TailFileDto obj = new TailFileDto(f.getName(), b);
            return obj;

        } catch (IndexOutOfBoundsException e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_IO, "index out of bounds exception");
        } catch (FileNotFoundException e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NOT_FOUND_TAILS_FILE, "file not found exception {"+filePath+"}");
        } catch (IOException e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_IO, "io exception");
        }
    }

    public static void writeFile(String filePath, byte[] data) throws ZkpException {

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(filePath, true);
            fos.write(data);
//            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NOT_FOUND_TAILS_FILE, "file not found exception {"+filePath+"}");
        } catch (IOException e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_IO, "io exception");
        }
    }

    public static void makeDir(String dirPath) {

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
    public static boolean isDirExists(String dirPath) {
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            return true;
        }

        return false;
    }
    public static void deleteDir(String dirPath){
        File dir = new File(dirPath);
        File[] dirFileList = dir.listFiles();
        for (int i = 0 ; i < dirFileList.length; i++){
            dirFileList[i].delete();
        }
        dir.delete();
    }

}
