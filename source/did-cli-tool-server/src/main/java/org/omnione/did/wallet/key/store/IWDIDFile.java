package org.omnione.did.wallet.key.store;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omnione.did.wallet.data.did.DIDs;
import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;

public class IWDIDFile {
	private String path;
	
	private DIDs data;
		
	private final static String DID_DOCUMENT_FILE_EXTENSION_NAME	= ".did";
	
	public IWDIDFile(String pathWithName) throws IWException {

    	
	  	
		String ext = null;
        int i = pathWithName.lastIndexOf('.');

        if (i > 0 &&  i < pathWithName.length() - 1) {
            ext = pathWithName.substring(i).toLowerCase();
        }
        
        if(ext == null || !ext.contentEquals(DID_DOCUMENT_FILE_EXTENSION_NAME)) {
        	pathWithName = pathWithName + DID_DOCUMENT_FILE_EXTENSION_NAME;
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

    
    public void write(DIDs dids) {
        String dids_str = dids.toJson();
        try {
			write(dids_str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
        try {
			load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
    }

    private void write(String data) throws IOException {
    	File file = new File(path);
        
    	FileWriter fw = null;
        try {
        	fw = new FileWriter(file);
            fw.write(data);
//            fw.close();
        }
        finally{
        	
			fw.close();
			
        }
    }
    
    public DIDs getData() {
    	try {
			load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
    	return data;
    }
    
    public String getFilePath() {
    	return path;
    }
    
    private IWDIDFile load() throws IOException {
    	
    	BufferedReader br = null;
    	FileReader fr = null;
    	try {
    		// java.io
            File file = new File(path);
//            FileReader fr = new FileReader(file);
//            BufferedReader br = new BufferedReader(fr);
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            StringBuffer buf = new StringBuffer();            
            String line = "";
            while((line = br.readLine()) != null){
            	buf.append(line);
            }
                     
            data = new DIDs();
            data.fromJson(buf.toString());   
            
    		
    		// java.nio (기존)
            // - android 7.0 (API25) 미지원 
            // - android 8.0 (API26) 부터추가
//	    	byte[] bytes = Files.readAllBytes(Paths.get(path));
//	    	data = new DIDs();
//	    	data.fromJson(new String(bytes));    
    	} finally {
    		br.close();
            fr.close();   
    	}
    	return this;
    }
}
