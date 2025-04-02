package org.omnione.did.wallet.zkp.revoc.utils;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.PointG2;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.BIG;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.ECP2;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.PAIR;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.ROM;

public class Tail {

    @JsonAdapter(ZkpSerializer.class)
    @SerializedName("tail")
    private PointG2 t;

    public Tail() {
        t = new PointG2();
    }

    public ECP2 new_tail(int index, PointG2 g_dash, GroupOrderElement gamma) throws ZkpException {

        byte[] i_bytes = AMCLUtils.transform_i32_to_array_of_i8(index);
        BIG pow = GroupOrderElement.from_bytes(i_bytes);
        pow = gamma.getBn().powmod(pow, new BIG(ROM.CURVE_Order));
        t.setPoint(PAIR.G2mul(g_dash.getPoint(), pow));
        return t.getPoint();
    }

    public void setTail(PointG2 t) {
        this.t = t;
    }

    public PointG2 getTail() {
        return t;
    }
}
