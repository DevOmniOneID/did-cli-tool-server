package org.omnione.did.wallet.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;

public class DataSplitter {
	
	public void DataSplitter() {
		
	}

	/**
	 * key, VC 를 2개로 분할하여 리스트로 리턴 
	 * 분할 시, 첫번째 byte에 index를 설정 하여 순서를 표시함 
	 *
	 * @param source 	분할할 key / VC 
	 * @return
	 */
	public List<byte[]> split(byte[] source) throws IWException {
		List<byte[]> splittedByteList = new ArrayList<byte[]>();
		
		int size = source.length;
		
		if(size < 2) {
			throw new IWException(IWErrorCode.ERR_CODE_SPLITTER_INVALID_DATA_SIZE);
		}
		
		int halfSize = size / 2;
		int location = 0;
		
		for (int index = 0; index < 2; index++) {
			// index 0 ~ 1 
			byte[] orderByte = new byte[1];
			orderByte[0] = (byte)index;
			
			byte[] splittedByte = new byte[halfSize];
			System.arraycopy(source, location, splittedByte, 0, halfSize);
			
			location += halfSize;
			
			int bufferSize = halfSize + 1;
			if(index == 1) {
				bufferSize += size %2;
			}
			
			ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
			buffer.put(orderByte); // index 
			buffer.put(splittedByte); // 분할된 data 
			
			// source가 홀수 개 일때 -> 나머지 크기 만큼의 바이트를 추가로 할당 
			if(index == 1 && location != size) {
				int leftSize = size - location;
				byte[] leftByte = new byte[leftSize];
				System.arraycopy(source, location, leftByte, 0, leftSize);
				
				buffer.put(leftByte);
			}		
	
			byte[] tempByte = buffer.array();
			
			splittedByteList.add(tempByte);

			// 메모리 해제
			Arrays.fill(splittedByte, (byte) 0);
			buffer.clear();
		}

		// 메모리 해제
		Arrays.fill(source, (byte) 0);

		return splittedByteList;		
	}
	
	/**
	 * 2개로 분할된 data를 하나로 합쳐 byte[]로 리턴 
	 * 리스트 데이터 각각의 첫번째 byte를 통해 첫번째, 두번째 순서를 파악하여 이어 붙임
	 *
	 * @param source	 분할된 key/VC 
	 * @return
	 */
	public byte[] join(List<byte[]> source) throws IWException {
		if(source.size()!= 2) {
			throw new IWException(IWErrorCode.ERR_CODE_SPLITTER_INVALID_LIST_SIZE);
		}
	
		List<byte[]> splittedDataArray = new ArrayList<byte[]>();
		
		int checkIndex = 1;
		
		int size = 0;
		
		for(int i=0; i<source.size(); i++) {
			byte[] splitted = source.get(i);

			if(splitted.length<2) {
				splittedDataArray.clear();
			}		
						
			byte[] firstByte = new byte[1];
			System.arraycopy(splitted, 0, firstByte, 0, 1);
			int index = firstByte[0] & 0xff;
			checkIndex -= index;
			
			byte[] tempData = new byte[splitted.length-1];
			System.arraycopy(splitted, 1, tempData, 0, splitted.length-1);
			
			size += tempData.length;			
			if(index == 0) { // 첫번째 
				splittedDataArray.add(0, tempData);			
			} else { // 두번째 
				splittedDataArray.add(tempData);	
			}

			// 메모리 해제
			Arrays.fill(source.get(i), (byte) 0);
			Arrays.fill(splitted, (byte) 0);
		}
	
		if(checkIndex != 0) {
			throw new IWException(IWErrorCode.ERR_CODE_SPLITTER_DUPLICATED_INDEX);
		}
	
		ByteBuffer buffer = ByteBuffer.allocate(size);
		for(int i=0; i<2; i++) {			
			buffer.put(splittedDataArray.get(i));

			// 메모리 해제
			Arrays.fill(splittedDataArray.get(i), (byte) 0);
		}

		byte[] joinedData = buffer.array();

		// 메모리 해제
		buffer.clear();

		return joinedData;	
	}
	
	
}
