package com.megasoft.merchant.mms.transporte;

import com.megasoft.merchant.mms.transporte.exception.IOMSTransportException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author Christian De Sousa
 *
 */
public abstract class MSServidorSocketMultiHilo extends Thread{
	private int puerto;
	public boolean isActive;
	private MSSocket servidorSocket;
	private ServerSocket servidor;
	
	
	public MSServidorSocketMultiHilo(int puerto){
		super("ServidorSocketMultiHilo");
		this.puerto = puerto;
	}
	
	public void establishService() {
		try {
			servidorSocket = new MSSocket(puerto);
			servidor = servidorSocket.crearServidor();
			isActive = true;
		}catch (IOMSTransportException e1) {
			isActive = false;
		}
	}
	
	public void run() {
		if (!isActive){
			return;
		}
		
		try {
			while (true){
				activarServidor(servidor.accept(), puerto);
			}
		} catch (IOException e) {
			isActive = false;
		}
	}

	protected void finalize() {
        if (servidor != null) {
            try {
            	servidor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            servidor = null;
        }
    }
	
	/*
	 * ejemplo new ServidorSocketSimple(servidor.accept(), puerto).start();
	 * */
	protected abstract void activarServidor(Socket socket, int puerto);
}
