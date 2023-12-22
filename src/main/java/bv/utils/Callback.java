package bv.utils;

public interface Callback<V> {
    V run() throws Exception;
}