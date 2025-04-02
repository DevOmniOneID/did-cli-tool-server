package org.omnione.did.wallet.zkp.revoc.utils.common;

public class TailsTuple<A, B> {

    private final A tailsLocation;
    private final B tailsHash;

    public TailsTuple(A a, B b) {
        this.tailsLocation = a;
        this.tailsHash = b;
    }

    public static <A, B> TailsTuple<A, B> of(final A a, final B b) {
        return new TailsTuple<A, B>(a, b);
    }

    public A getTailsLocation() {
        return tailsLocation;
    }

    public B getTailsHash() {
        return tailsHash;
    }
}
