package org.omnione.did.wallet.eoscommander.data.remote.model.types;

import org.omnione.did.wallet.eoscommander.crypto.ec.EcSignature;
import org.omnione.did.wallet.eoscommander.data.remote.model.types.EosType.InsufficientBytesException;
import org.omnione.did.wallet.eoscommander.data.remote.model.types.EosType.Reader;
import org.omnione.did.wallet.eoscommander.data.remote.model.types.EosType.Writer;

public class TypeSignature implements EosType.Packer, EosType.Unpacker {

	private EcSignature ecSignature;
	
	

	public TypeSignature() {
		super();
	}

	public TypeSignature(EcSignature ecSignature) {
		super();
		this.ecSignature = ecSignature;
	}

	@Override
	public void pack(Writer writer) {

		byte[] ecSignatureBytes = ecSignature.eosEncoding(true);

		writer.putVariableUInt(0);
		writer.putBytes(ecSignatureBytes);
	}

	@Override
	public void unpack(Reader reader) throws InsufficientBytesException {
		reader.getVariableUint();
		byte[] ecSignatureBytes = reader.getBytes(65);
		
		ecSignature = new EcSignature(ecSignatureBytes);
		
	}
	
	public String getSig() {
		return ecSignature.toString();
	}
	@Override
	public String toString() {
		return "TypeSignature [ecSignature=" + ecSignature + "]";
	}
	
	

}
