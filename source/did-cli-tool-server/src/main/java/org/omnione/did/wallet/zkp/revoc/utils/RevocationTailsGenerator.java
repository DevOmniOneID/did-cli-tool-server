package org.omnione.did.wallet.zkp.revoc.utils;

import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.eoscommander.crypto.util.Base58;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.PointG2;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.dto.TailFileDto;
import org.omnione.did.wallet.zkp.revoc.utils.blob.tail.service.TailStorageService;
import org.omnione.did.wallet.zkp.revoc.utils.common.TailsTuple;

import java.io.File;
import java.util.Vector;

public class RevocationTailsGenerator {

    private int size;
    private int currentIndex;
    @JsonAdapter(ZkpSerializer.class)
    private PointG2 g_dash;
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement gamma;
    private Vector<Tail> tails = new Vector<Tail>();

    public RevocationTailsGenerator(int max_cred_num, GroupOrderElement gamma, PointG2 g_dash) {

        this.size = 2 * max_cred_num + 1; /* Unused 0th + valuable 1..L + unused (L+1)th + valuable (L+2)..(2L) */
        this.currentIndex = 0;
        this.g_dash = g_dash;
        this.gamma = gamma;
    }

    public Tail getTail(int index) {
        return tails.get(index);
    }

    public int count() {
        return this.size - this.currentIndex;
    }

    public boolean hasNext() {
        if (this.currentIndex >= this.size) {
            return false;
        }
        return true;
    }

    public Tail tryNext() throws ZkpException {
        if (this.currentIndex >= this.size) {
            return null;
        }

        Tail tail = new Tail();
        tail.new_tail(this.currentIndex, this.g_dash, this.gamma);

        tails.add(currentIndex, tail);
        this.currentIndex += 1;

        return tail;
    }

    public void showTails() {
        for (int i = 0 ; i < tails.size(); i++) {
            AMCLUtils.toHashString(tails.get(i), "tail "+i);
        }
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public PointG2 getG_dash() {
        return g_dash;
    }

    public void setG_dash(PointG2 g_dash) {
        this.g_dash = g_dash;
    }

    public GroupOrderElement getGamma() {
        return gamma;
    }

    public void setGamma(GroupOrderElement gamma) {
        this.gamma = gamma;
    }

    public TailsTuple<String, String> storeTailsFromGenerator() throws ZkpException {

        TailStorageService service = new TailStorageService();
        // TODO: blob_handle 임시 값
        String blob_handle = "1";

        byte[] verBytes = new byte[] {0x00, 0x02};
        service.append(blob_handle, verBytes);

        while(this.hasNext()) {
            Tail tail = this.tryNext();
            byte[] tailBytes = tail.getTail().toBytes();
//            System.out.println(":: "+AMCLUtils.byteArrayToHex(tailBytes));
            service.append(blob_handle, tailBytes);
        }

        TailFileDto tails = service.getTailFile(blob_handle);

        byte[] sha256hash = Sha256.from(tails.getTailHash()).getBytes();
        String tailsHash = Base58.encode(sha256hash);
        String tailsLocation =  service.getTailsLocation() +"/"+tailsHash;

        // 파일명 변경
        File file = new File(service.getTailsLocation()+"/"+blob_handle);
        File fileToMove = new File(tailsLocation);
        boolean isMoved = file.renameTo(fileToMove);
        ZkpLogger.info("====================================================================================================");
        ZkpLogger.info("tailsLocation "+tailsLocation);
        ZkpLogger.info("tailsHash "+tailsHash);
        ZkpLogger.info("====================================================================================================");
        return TailsTuple.of(tailsLocation, tailsHash);
    }
}
