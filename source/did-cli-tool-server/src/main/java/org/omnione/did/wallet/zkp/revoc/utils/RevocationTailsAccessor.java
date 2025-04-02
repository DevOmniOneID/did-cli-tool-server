package org.omnione.did.wallet.zkp.revoc.utils;

import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.CONFIG_BIG;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.ECP2;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.dto.TailFileDto;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.service.TailStorageService;

public class RevocationTailsAccessor {

    public static Tail accessTail(String tailHash, int tail_id) throws ZkpException {

        try {
            if (tail_id < 1) {
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_GET_TAIL_FROM_TAILS_FILE_FAIL);
            }

            TailStorageService tailStorageService = new TailStorageService();
            int tailSize = CONFIG_BIG.MODBYTES * 4;
            int offset = tailSize * tail_id + 2;
            TailFileDto tailFileDto = tailStorageService.getTailFile(tailHash);
            byte[] tailFileBytes = tailFileDto.getTailHash();

//            System.out.println("Tail tailSize :"+tailSize);
//            System.out.println("Tail offset :"+offset);
//            byte[] slice = Arrays.copyOfRange(tailFileBytes, offset, tailFileBytes.length);
            byte[] slice = new byte[tailSize];
            System.arraycopy(tailFileBytes, offset, slice, 0, tailSize);
            Tail tail = new Tail();
            tail.getTail().setPoint(ECP2.fromBytes(slice));
            return tail;
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_GET_TAIL_FROM_TAILS_FILE_FAIL);
        }
    }
}