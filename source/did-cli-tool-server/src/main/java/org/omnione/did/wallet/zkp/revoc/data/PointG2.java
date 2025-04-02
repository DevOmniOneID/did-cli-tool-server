package org.omnione.did.wallet.zkp.revoc.data;

import com.google.gson.annotations.Expose;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpEncodeMethod;
import org.omnione.did.wallet.zkp.revoc.utils.AMCLUtils;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.*;


public class PointG2 implements ZkpEncodeMethod {

    @Expose
    private ECP2 point;

    public PointG2() {

        BIG point_xa = new BIG(ROM.CURVE_Pxa);
        BIG point_xb = new BIG(ROM.CURVE_Pxb);
        BIG point_ya = new BIG(ROM.CURVE_Pya);
        BIG point_yb = new BIG(ROM.CURVE_Pyb);

        FP2 point_x = new FP2(point_xa, point_xb);
        FP2 point_y = new FP2(point_ya, point_yb);

        ECP2 gen_g2 = new ECP2(point_x, point_y);

        point = PAIR.G2mul(gen_g2, AMCLUtils.randModOrder(AMCLUtils.getRand()));
    }

    public ECP2 add(ECP2 value) {
        this.point.add(value);
        return this.point;
    }

    public void setEncodeString(String str) throws ZkpException {
        try {
            String[] result = str.split(" ");

    //        for (String s : result) {
    //            System.out.println(">>>>"+s);
    //        }

            point.getx().geta().XES = Integer.parseInt(result[0]);
            point.getx().geta().x.copy(BIG.fromString(result[1]));
            point.getx().getb().XES = Integer.parseInt(result[2]);
            point.getx().getb().x.copy(BIG.fromString(result[3]));
            point.gety().getb().XES = Integer.parseInt(result[4]);
            point.gety().geta().x.copy(BIG.fromString(result[5]));
            point.gety().getb().XES = Integer.parseInt(result[6]);
            point.gety().getb().x.copy(BIG.fromString(result[7]));
            point.getz().geta().XES = Integer.parseInt(result[8]);
            point.getz().geta().x.copy(BIG.fromString(result[9]));
            point.getz().getb().XES = Integer.parseInt(result[10]);
            point.getz().getb().x.copy(BIG.fromString(result[11]));
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_BIG_NUMBER_FROM_HEXA_STRING_FAIL);
        }
//        AMCLUtils.toHashString(g2, "g2");
//        return g2;
    }

    public String getEncodeString() throws ZkpException{
        try {
            StringBuilder string = new StringBuilder();
            string.append(point.getx().geta().XES).append(" ").append(point.getx().geta().x.toString()).append(" ")
              .append(point.getx().getb().XES).append(" ").append(point.getx().getb().x.toString()).append(" ")
              .append(point.gety().geta().XES).append(" ").append(point.gety().geta().x.toString()).append(" ")
              .append(point.gety().getb().XES).append(" ").append(point.gety().getb().x.toString()).append(" ")
              .append(point.getz().geta().XES).append(" ").append(point.getz().geta().x.toString()).append(" ")
              .append(point.getz().getb().XES).append(" ").append(point.getz().getb().x.toString());
            return string.toString();
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_APPEND_BIG_NUMBER_FOR_HASH_FAIL);
        }
    }

    public ECP2 getPoint() {
        return point;
    }

    public void setPoint(ECP2 point) {
        this.point = point;
    }

    public byte[] toBytes() throws ZkpException {
        try {
            byte[] v = new byte[CONFIG_BIG.MODBYTES * 4];
    //        Vector<Byte> vec = new Vector<>();
    //        for (int i = 0 ; i < CONFIG_BIG.MODBYTES * 16 ; i++) {
    //            vec.add((byte)0x00);
    //        }
            this.point.toBytes(v);
    //        System.out.println(AMCLUtils.byteArrayToInteger(v));
            return v;
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_BIG_NUMBER_TO_BYTE_FAIL);
        }
    }
}
