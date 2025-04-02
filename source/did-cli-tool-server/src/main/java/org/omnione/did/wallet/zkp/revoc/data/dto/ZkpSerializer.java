package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.Pair;
import org.omnione.did.wallet.zkp.revoc.data.PointG1;
import org.omnione.did.wallet.zkp.revoc.data.PointG2;
import java.io.IOException;

public class ZkpSerializer implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {

        TypeAdapter defaultAdapter = gson.getAdapter(typeToken);
        return new Adapter(defaultAdapter);
    }

    private static class Adapter<T extends ZkpEncodeMethod> extends TypeAdapter<T> {

        final TypeAdapter<T> defaultAdapter;

        Adapter(TypeAdapter<T> defaultAdapter) {
            this.defaultAdapter = defaultAdapter;
        }

        public void write(JsonWriter out, T value) throws IOException {
            try {
                out.value(value.getEncodeString());
            } catch (ZkpException e) {
                try {
                    throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_BIG_NUMBER_FROM_JSON_FAIL);
                } catch (ZkpException zkpException) {
                    zkpException.printStackTrace();
                }
            }
        }

        /**
         * djpark 20210610
         * AMCL MODEL 역 직렬화시 encodedString의 길이를 보고 Pair, PointG1, PointG2, GroupOrderElement를 판단
         * 더 좋은 방법이 있다면 변경해야 함
         * */
        public T read(JsonReader in) throws IOException {

            String encodedString = null;
            while (in.hasNext()) {
                try {
                    encodedString = in.nextString();
                } catch (Exception e) {
                    break;
                }
//                ZkpLogger.debug("Token Value >>>> " + encodedString);
            }
            try {
                String[] array = encodedString.split(" ");
    //            ZkpLogger.debug("encodedString split length: "+array.length);

                if (array.length == 12) {
                    PointG2 g2 = new PointG2();
                    g2.setEncodeString(encodedString);
                    return (T)g2;
                }
                else if (array.length == 6) {
                    PointG1 g1 = new PointG1();
                    g1.setEncodeString(encodedString);
                    return (T)g1;
                }
                else if (array.length == 1) {
                    GroupOrderElement groupOrderElement = new GroupOrderElement();
                    groupOrderElement.setEncodeString(encodedString);
                    return (T)groupOrderElement;
                }
                else {
                    Pair pair = new Pair();
                    pair.setEncodeString(encodedString);
                    return (T)pair;
                }
            } catch (Exception e) {
                try {
                    throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_BIG_NUMBER_FROM_JSON_FAIL);
                } catch (ZkpException zkpException) {
                    zkpException.printStackTrace();
                }
            }
            return null;
        }
    }
}

