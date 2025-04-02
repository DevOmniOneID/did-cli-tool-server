package org.omnione.did.wallet.zkp.revoc.utils.acml.BN254;

public class CONFIG_FIELD {
	public static final int NOT_SPECIAL=0;
	public static final int PSEUDO_MERSENNE=1;
	public static final int MONTGOMERY_FRIENDLY=2;
	public static final int GENERALISED_MERSENNE=3;

	public static final int MODBITS=254; /* Number of bits in Modulus */
	public static final int MOD8=3;  /* Modulus mod 8 */
	public static final int MODTYPE=NOT_SPECIAL;

	public static final int FEXCESS = (((int)1<<26)-1); // BASEBITS*NLEN-MODBITS or 2^30 max!
}
