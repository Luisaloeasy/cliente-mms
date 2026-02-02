package com.megasoft.merchant.mms.transporte;

import com.megasoft.merchant.mms.transporte.exception.IOMSTransportException;
import com.megasoft.merchant.mms.transporte.exception.UnknownHostMSSocketException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class MSSocket {
	
	private String servidor;
	private int puerto;
	
	
	public MSSocket(String servidorAConectar, int puerto){
		this.servidor = servidorAConectar;
		this.puerto = puerto;
	}
	
	public MSSocket(int puertoServicio){
		this.puerto = puertoServicio;
	}
	
	public Socket conectarServidor() throws UnknownHostMSSocketException, IOMSTransportException{
		try {
			Socket miCliente = new Socket(servidor, puerto);
			return miCliente;
		} catch (UnknownHostException e) {
			throw new UnknownHostMSSocketException(e);
		} catch (IOException e) {
			throw new IOMSTransportException(e);
		}
	}
	
	public ServerSocket crearServidor() throws IOMSTransportException{
		try {
			ServerSocket miServicio = new ServerSocket(puerto);
			return miServicio;
		} catch (IOException e) {
			throw new IOMSTransportException(e);
		}	
	}
	
	public BufferedReader crearStreamEntrada(Socket socket) throws IOMSTransportException{
	    try {
	    	BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    	return entrada;	    
	    } catch (IOException e) {
	    	throw new IOMSTransportException(e);
		}    
	}
	
	public PrintStream crearStreamSalida(Socket socket) throws IOMSTransportException{
	    try {
	    	PrintStream entrada = new PrintStream(socket.getOutputStream());
		    return entrada;
	    } catch (IOException e) {
	    	throw new IOMSTransportException(e);
		}	    
	}
	
	public void enviar(Socket socket,String contenido) throws IOMSTransportException{
		PrintStream salida = crearStreamSalida(socket);
		salida.println(contenido);
		salida.close();
		try {
			socket.close();
		} catch (IOException e) {
			throw new IOMSTransportException(e);
		}
	}
}
