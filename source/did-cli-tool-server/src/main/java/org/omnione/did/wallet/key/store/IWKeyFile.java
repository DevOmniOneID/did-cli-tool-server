package org.omnione.did.wallet.key.store;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.data.IWKeyStoreData;

public class IWKeyFile {
	
	public static boolean ONETIME_LOAD = true;
	private String path;
    
//	private static IWKeyStoreData data;
	//tykim
	private IWKeyStoreData data;
	
	private final static String WALLET_FILE_EXTENSION_NAME	= ".wallet";
	
    public IWKeyFile(String pathWithName) {
    	
    	String ext = null;
        int i = pathWithName.lastIndexOf('.');

        if (i > 0 &&  i < pathWithName.length() - 1) {
            ext = pathWithName.substring(i).toLowerCase();
        }
        
        if(ext == null || !ext.contentEquals(WALLET_FILE_EXTENSION_NAME)) {
        	pathWithName = pathWithName + WALLET_FILE_EXTENSION_NAME;
        }
        
        path = pathWithName;
        

    }

    public boolean isExist() {    	
        File f = new File(path);
        return f.exists();
    }
    
    public void delete() {
        File f = new File(path);
        if (f.exists() ) {
        	f.delete();
        }
    }

    public void write(IWKeyStoreData data) throws IWException {
        String data_str = data.toJson();
        try {
			write(data_str);
		} catch (IOException e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILE_WRITE_FAIL, e);
		}
        try {
			load();
		} catch (IOException e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILE_LOAD_FAIL, e);
		}
    }
       
    public IWKeyStoreData getData() throws IWException {
    	try {
    		if(ONETIME_LOAD) {
    			//tykim: 서버쪽 매번 키파일을 로드할 경우 객체가 null이 될 수 있어 분기 처리함 (thread 관련)
    			if(data == null) {
	        		load();
    			}
        	}else {
    			load();
        	}
		} catch (IOException e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILE_LOAD_FAIL, e);
		}
    	
    	return data;
    }
    
  //modified by joshua
    public void clearData() {
    	if(data != null) {
    		data = null;
    	}
    }
    //modified ends
    
    public String getFilePath() {
    	return path;
    }    
    
    /**
     * (내부 함수) key file 내용 작성
     * @param data
     * @throws IOException 
     */
    private void write(String data) throws IOException {
    	FileWriter fw = null;
    	
        try {
            File file = new File(path);
//            FileWriter fw = new FileWriter(file);
            fw = new FileWriter(file);
            fw.write(data);
//            fw.close();
        }
        finally {
        	fw.close();
        }
//            catch (IOException e) {
//        	throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILE_WRITE_FAIL, e);
//        }
        
    }
    
    /**
     * (내부 함수) key file 내용 읽기
     * @param data
     */
    private void load() throws IOException {
    	
    	FileReader fr = null;
        BufferedReader br = null;
        
    	try {
    		// java.io
            File file = new File(path);
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            StringBuffer buf = new StringBuffer();            
            String line = "";
            while((line = br.readLine()) != null){
            	buf.append(line);
            }
                       
            data = new IWKeyStoreData();
            data.fromJson(buf.toString());   
            
            // java.nio (기존)
            // - android 7.0 (API25) 미지원 
            // - android 8.0 (API26) 부터추가
//	    	byte[] bytes = Files.readAllBytes(Paths.get(path));
//	    	data = new IWKeyStoreData();
//	    	data.fromJson(new String(bytes));    
    	}
    	finally {
    		br.close();
            fr.close(); 
    	}
//    	catch (IOException e) {
//    		throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILE_LOAD_FAIL, e);
//    	}
    }
    
}
