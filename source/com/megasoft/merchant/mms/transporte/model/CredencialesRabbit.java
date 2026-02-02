package com.megasoft.merchant.mms.transporte.model;

public class CredencialesRabbit {

    private static String usuario;
    private static String contrasena;
    private static String id;
    private static String virtualHost;

    public static void setUsuario(String usuario) {
        CredencialesRabbit.usuario = usuario;
    }

    public static void setContrasena(String contrasena) {
        CredencialesRabbit.contrasena = contrasena;
    }

    public static void setId(String id) {
        CredencialesRabbit.id = id;
    }

    public static void setVirtualHost(String virtualHost) {
        CredencialesRabbit.virtualHost = virtualHost;
    }

    public static String getUsuario() {
        return CredencialesRabbit.usuario;
    }

    public static String getContrasena() {
        return CredencialesRabbit.contrasena;
    }

    public static String getId() {
        return CredencialesRabbit.id;
    }

    public static String getVirtualHost() {
        return CredencialesRabbit.virtualHost;
    }
}


