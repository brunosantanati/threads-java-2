package br.com.alura.servidor;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class DistribuirTarefas implements Runnable {

	private Socket socket;

	public DistribuirTarefas(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
	
		try {
	
			System.out.println("Distribuindo as tarefas para o cliente "
					+ socket);
	
			Scanner entradaCliente = new Scanner(socket.getInputStream());
	
			PrintStream saidaCliente = new PrintStream(socket.getOutputStream());
	
			while (entradaCliente.hasNextLine()) {
				
				String comando = entradaCliente.nextLine();
				System.out.println("Comando recebido " + comando);
	
				switch (comando) {
					case "c1": {
						// confirmação do o cliente
						saidaCliente.println("Confirmação do comando c1");
						break;
					}
					case "c2": {
						saidaCliente.println("Confirmação do comando c2");
						break;
					}
					default: {
						saidaCliente.println("Comando não encontrado");
					}
				}
				
				System.out.println(comando);
			}
	
			saidaCliente.close();
			entradaCliente.close();
	
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	
	}

}
