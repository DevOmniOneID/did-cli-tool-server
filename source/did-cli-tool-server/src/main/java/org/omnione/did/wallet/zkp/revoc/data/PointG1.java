package org.omnione.did.wallet.zkp.revoc.data;

import com.google.gson.annotations.Expose;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpEncodeMethod;
import org.omnione.did.wallet.zkp.revoc.utils.AMCLUtils;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.*;


public class PointG1 implements ZkpEncodeMethod {
    @Expose
    private ECP point;

    public PointG1() {

        BIG point_x = new BIG(ROM.CURVE_Gx);
        BIG point_y = new BIG(ROM.CURVE_Gy);
        ECP gen_g1 = new ECP(point_x, point_y);

        this.point = PAIR.G1mul(gen_g1, AMCLUtils.randModOrder(AMCLUtils.getRand()));
    }

    public byte[] toBytes() throws ZkpException {

        try {
            byte[] v = new byte[CONFIG_BIG.MODBYTES * 4];
            this.point.toBytes(v, false);
//        System.out.println("len: "+v.length+", data: "+AMCLUtils.byteArrayToInteger(v));
            return v;
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_BIG_NUMBER_TO_BYTE_FAIL);
        }
    }

    public ECP getPoint() {
        return point;
    }


    public void setPoint(ECP point) {
        this.point = point;
    }

    public String getEncodeString() throws ZkpException {
        try {
            StringBuilder string = new StringBuilder();
            string.append(point.getx().XES).append(" ").append(point.getx().x.toString()).append(" ")
                    .append(point.gety().XES).append(" ").append(point.gety().x.toString()).append(" ")
                    .append(point.getz().XES).append(" ").append(point.getz().x.toString());
            return string.toString();
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_APPEND_BIG_NUMBER_FOR_HASH_FAIL);
        }
    }

    public void setEncodeString(String str) throws ZkpException {
        try {
            String[] result = str.split(" ");

//        for (String s : result) {
//            System.out.println(">>>>"+s);
//        }
            point.getx().XES = Integer.parseInt(result[0]);
            point.getx().x.copy(BIG.fromString(result[1]));
            point.gety().XES = Integer.parseInt(result[2]);
            point.gety().x.copy(BIG.fromString(result[3]));
            point.getz().XES = Integer.parseInt(result[4]);
            point.getz().x.copy(BIG.fromString(result[5]));
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_BIG_NUMBER_FROM_HEXA_STRING_FAIL);
        }
//        AMCLUtils.toHashString(g1, "======= result: PointG1 ");
    }
}
