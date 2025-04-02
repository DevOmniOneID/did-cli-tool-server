package org.omnione.did.wallet.zkp.data;

import java.util.ArrayList;
import java.util.Collection;

public class IWObjectList<T> {

    private ArrayList<T> elementData = new ArrayList<T>();

    public void add(T value) {
        elementData.add(value);
    }

    public T get(int idx) {
        return (T) elementData.get(idx);
    }

    public ArrayList<T> getAll() {
        return elementData;
    }

    public int size() {
        return elementData.size();
    }

    public boolean addAll(Collection<?> c) {
        return elementData.addAll((Collection<? extends T>) c);
    }

    public void remove(T credential) {
        elementData.remove(credential);
    }

    public void remove(int index) {
        elementData.remove(index);
    }

    public void removeAll() {
        elementData.clear();
    }

    public void clear() {
        elementData.clear();
    }
}

