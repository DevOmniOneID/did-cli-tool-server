package org.omnione.did.wallet.zkp.revoc.utils;

import org.omnione.did.wallet.zkp.revoc.data.PointG2;

public class Accumulator extends PointG2 {

    public Accumulator() {
        this.new_inf();
    }
    public void new_inf() {
        super.getPoint().inf();
    }
}
