package org.omnione.did.wallet.zkp.revoc.utils;

import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.PointG2;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.CONFIG_BIG;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.ECP2;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.dto.TailFileDto;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.service.TailStorageService;

import java.util.Iterator;
import java.util.TreeSet;

public class Witness {
    @JsonAdapter(ZkpSerializer.class)
    private PointG2 omega;

    public Witness(String tailHash, int revocIndex, int maxCredNum, boolean issuance_by_default,
               RevocationRegistryDelta revRegDelta) throws ZkpException {

        omega = new PointG2();
        omega.getPoint().inf();

        TreeSet<Integer> issued = new TreeSet<Integer>();

        if (issuance_by_default) {
            issued = new TreeSet<Integer>();
            for (int idx = 1 ; idx <= maxCredNum ; idx++) {
                if (!revRegDelta.getRevoked().contains(idx)) {
                    issued.add(idx);
                }
            }
        } else {    // demand
//            issued = revRegDelta.getIssued();
            Iterator itor = revRegDelta.getIssued().iterator();
            while (itor.hasNext()) {
                issued.add((Integer)(itor.next()));
            }
        }

        issued.remove(revocIndex);
//        ZkpLogger.debug("issued :"+issued);
        /**
         * 속도 개선을 위해 메모리로 로드해서 연산하도록 변경
         * */
//        for(Integer j: issued) {    // for java 6
//            int index = maxCredNum + 1 - j + revocIndex;
//            ZkpLogger.debug("j: "+j+", "+"index: "+index);
//            Tail tail = RevocationTailsAccessor.accessTail(tailHash, index);
//            omega.setPoint(omega.add(tail.getPoint()));
//        }
        TailStorageService tailStorageService = new TailStorageService();
        TailFileDto tailFileDto = tailStorageService.getTailFile(tailHash);
        byte[] tailFileBytes = tailFileDto.getTailHash();

        int tailSize = CONFIG_BIG.MODBYTES * 4; // 128
        byte[] slice = new byte[tailSize];

        for(Integer j: issued) {   // for jdk 1.6
            int index = maxCredNum + 1 - j + revocIndex;
            int offset = tailSize * index + 2;
//            ZkpLogger.debug("Tail j: "+j+", "+"index: "+index+", offset: "+offset);

//            byte[] slice = Arrays.copyOfRange(tailFileBytes, offset, tailFileBytes.length);
            // 속도 개선 (slice 싸이즈를 128byte 고정)
            try {
                System.arraycopy(tailFileBytes, offset, slice, 0, tailSize);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_GET_TAIL_FROM_TAILS_FILE_FAIL, "get tail from tails file fail");
            }
            omega.setPoint(omega.add(ECP2.fromBytes(slice)));
        }
    }

    public void update(String tailHash, int revIdx, int maxCredNum, RevocationRegistryDelta revRegDelta) throws ZkpException {

        PointG2 omega_denom = new PointG2();
        omega_denom.getPoint().inf();

//        Iterator revokedItor = revRegDelta.getRevoked().iterator();
        ZkpLogger.debug("revRegDelta.getRevoked() :"+revRegDelta.getRevoked());
//        while(revokedItor.hasNext()){
//            int j = (int)revokedItor.next();
        for(Integer j: revRegDelta.getRevoked()) {  // for jdk 1.6
            if (revIdx == j) continue;

            int index = maxCredNum + 1 - j + revIdx;
//            System.out.println("update j: "+j+", "+"index: "+index);
            Tail tail = RevocationTailsAccessor.accessTail(tailHash, index);
            omega_denom.setPoint(omega_denom.add(tail.getTail().getPoint()));
        }

        PointG2 omega_num = new PointG2();
        omega_num.getPoint().inf();

//        Iterator issuedItor = revRegDelta.getIssued().iterator();
//        ZkpLogger.debug("revRegDelta.getIssued() :" + revRegDelta.getIssued());
//        while(issuedItor.hasNext()) {
//            int j = (int)issuedItor.next();
        for(Integer j: revRegDelta.getIssued()) {  // for jdk 1.6
            if (revIdx == j) continue;

            int index = maxCredNum + 1 - j + revIdx;
//            ZkpLogger.debug("update j: "+j+", "+"index: "+index);
            Tail tail = RevocationTailsAccessor.accessTail(tailHash, index);
            omega_num.setPoint(omega_num.add(tail.getTail().getPoint()));
        }

        PointG2 new_omega = new PointG2();
        omega_num.getPoint().sub(omega_denom.getPoint());
        omega.add(omega_num.getPoint());
        new_omega.getPoint().copy(omega.getPoint());
        omega.getPoint().copy(new_omega.getPoint());
    }

    public PointG2 getOmega() {
        return this.omega;
    }

    public void setOmega(PointG2 omega) {
        this.omega = omega;
    }

    public String toJson() {
        GsonWrapper gson = new GsonWrapper();
        return gson.toJson(this);
    }
}
