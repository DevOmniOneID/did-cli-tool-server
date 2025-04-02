package org.omnione.did.wallet.zkp.util.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigInteger;

public class BigIntegerSerializer implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {

        TypeAdapter defaultAdapter = gson.getAdapter(typeToken);
        return new Adapter(defaultAdapter);
    }

    private static class Adapter<T extends BigInteger> extends TypeAdapter<T> {
        private final TypeAdapter<T> defaultAdapter;

        Adapter(TypeAdapter<T> defaultAdapter) {
            this.defaultAdapter = defaultAdapter;
        }

        public void write(JsonWriter out, T value) throws IOException {
            out.value(value.toString());
        }

        public T read(JsonReader in) throws IOException {
            return defaultAdapter.read(in);
        }
    }
}
