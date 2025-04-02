package org.omnione.did.wallet.zkp.revoc.data;

import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpEncodeMethod;
import org.omnione.did.wallet.zkp.revoc.utils.AMCLUtils;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.BIG;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.CONFIG_BIG;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.ROM;

import java.util.Vector;

public class GroupOrderElement implements ZkpEncodeMethod {

    private BIG bn;

    public GroupOrderElement(){

        this.bn = AMCLUtils.randModOrder(AMCLUtils.getRand());
    }

    public BIG getBn() {
        return bn;
    }

    public void setBn(BIG bn) {
        this.bn = bn;
    }

    /**
     *  let mut sum = self.bn;
     *  sum.add(&r.bn);
     *  sum.rmod(&BIG::new_ints(&CURVE_ORDER));
     *  Ok(GroupOrderElement { bn: sum })
     * */
    public BIG add_mod(BIG r) {

        BIG sum = this.getBn();
        sum.add(r);
        sum.mod(new BIG(ROM.CURVE_Order));
        return sum;
    }

    /**
     * let mut bn = self.bn;
     * let mut vec = vec![0u8; Self::BYTES_REPR_SIZE];
     * bn.tobytes(&mut vec);
     * Ok(vec)
     * */
    public byte[] toBytes() {

        byte[] vec  = new byte[CONFIG_BIG.MODBYTES];
        this.getBn().toBytes(vec);
        return vec;
    }

    public static BIG from_bytes(byte[] b) throws ZkpException{

        try {
            Vector<Byte> vec = new Vector<Byte>();
            for (int i = 0; i < b.length; i++) {
                vec.add(b[i]);
            }
            int len = vec.size();
            if (len < CONFIG_BIG.MODBYTES) {
                int diff = CONFIG_BIG.MODBYTES - len;
                byte[] result = new byte[diff];
                for (int i = 0; i < diff; i++) {
                    result[i] = 0x00;
                }
                byte[] output = new byte[32];
                System.arraycopy(result, 0, output, 0, result.length);
                System.arraycopy(b, 0, output, diff, b.length);
                return BIG.fromBytes(output);
            }
            return BIG.fromBytes(b);
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_BIG_NUMBER_FROM_BYTE_FAIL);
        }

    }

    public void setEncodeString(String str) throws ZkpException {
        try {
            bn.copy(BIG.fromString(str));
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_BIG_NUMBER_FROM_HEXA_STRING_FAIL);
        }
//        AMCLUtils.toHashString(groupOrderElement, "GroupOrderElement");
//        return groupOrderElement;
    }

    public String getEncodeString() throws ZkpException{
        try {
            return getBn().toString();
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_APPEND_BIG_NUMBER_FOR_HASH_FAIL);
        }
    }
}
