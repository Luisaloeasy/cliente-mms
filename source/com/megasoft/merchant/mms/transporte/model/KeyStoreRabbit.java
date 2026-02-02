package com.megasoft.merchant.mms.transporte.model;

public class KeyStoreRabbit {

    private static String id;
    private static String tipo;
    private static String algoritmo;
    private static String clave;
    private static String alias;
    private static String proveedor;

    public static void setTipo(String tipo) {
        KeyStoreRabbit.tipo = tipo;
    }

    public static void setAlgoritmo(String algoritmo) {
        KeyStoreRabbit.algoritmo = algoritmo;
    }

    public static void setClave(String clave) {
        KeyStoreRabbit.clave = clave;
    }

    public static void setId(String id) {
        KeyStoreRabbit.id = id;
    }

    public static String getAlias() {
        return KeyStoreRabbit.alias;
    }

    public static void setAlias(String alias) {
        KeyStoreRabbit.alias = alias;
    }

    public static String getProveedor() {
        return KeyStoreRabbit.proveedor;
    }

    public static String getId() {
        return KeyStoreRabbit.id;
    }

    public static String getTipo() {
        return KeyStoreRabbit.tipo;
    }

    public static String getAlgoritmo() {
        return KeyStoreRabbit.algoritmo;
    }

    public static String getClave() {
        return KeyStoreRabbit.clave;
    }

    public static void setProveedor(String proveedor) {
        KeyStoreRabbit.proveedor = proveedor;
    }
}
