package org.omnione.did.wallet.eoscommander.data.remote.model.types;

import org.omnione.did.wallet.eoscommander.crypto.util.HexUtils;
import org.omnione.did.wallet.eoscommander.data.remote.model.types.EosType.InsufficientBytesException;
import org.omnione.did.wallet.eoscommander.data.remote.model.types.EosType.Reader;
import org.omnione.did.wallet.eoscommander.data.remote.model.types.EosType.Writer;

public class TypeChecksum256 implements EosType.Packer, EosType.Unpacker{

	private String checksum256;
	
	

	public TypeChecksum256() {
		super();
	}

	public TypeChecksum256(String checksum256) {
		super();
		this.checksum256 = checksum256;
	}

	public String getChecksum256() {
		return checksum256;
	}

	@Override
	public void pack(Writer writer) {
		byte[] chechsumBytes = HexUtils.toBytes(checksum256);

		writer.putBytes(chechsumBytes);
	}

	@Override
	public void unpack(Reader reader) throws InsufficientBytesException {
		
		byte[] chechsumBytes =  reader.getBytes(32);
		
		checksum256 = HexUtils.toHex(chechsumBytes);
	}

}
