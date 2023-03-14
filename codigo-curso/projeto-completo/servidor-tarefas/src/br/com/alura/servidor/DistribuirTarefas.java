package br.com.alura.servidor;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DistribuirTarefas implements Runnable {

	private Socket socket;
	private ServidorTarefas servidor;
	private ExecutorService threadPool;
	private BlockingQueue<String> filaComandos;

	public DistribuirTarefas(ExecutorService threadPool, BlockingQueue<String> filaComandos, Socket socket, ServidorTarefas servidor) {
		this.threadPool = threadPool;
		this.filaComandos = filaComandos;
		this.socket = socket;
		this.servidor = servidor;
	}

	@Override
	public void run() {

		try {
			
			System.out.println("Distribuindo as tarefas para o cliente " + socket);

			Scanner entradaCliente = new Scanner(socket.getInputStream());

			PrintStream saidaCliente = new PrintStream(socket.getOutputStream());

			while (entradaCliente.hasNextLine()) {

				String comando = entradaCliente.nextLine();
				System.out.println("Comando recebido " + comando);

				switch (comando) {
					case "c1": {						
						saidaCliente.println("Confirmação do comando c1");
		                ComandoC1 c1 = new ComandoC1(saidaCliente);
		                this.threadPool.submit(c1);
						break;
					}
					case "c2": {
						saidaCliente.println("Confirmação do comando c2");

				        //criando os dois comando 
				        ComandoC2ChamaWS c2WS = new ComandoC2ChamaWS(saidaCliente);
				        ComandoC2AcessaBanco c2Banco = new ComandoC2AcessaBanco(saidaCliente);

				        //passando os comandos para o pool, resutlado é um Future
				        Future<String> futureWS = this.threadPool.submit(c2WS);
				        Future<String> futureBanco = this.threadPool.submit(c2Banco);

				        Callable<Void> juntaResultados = new JuntaResultadosFutureWSFutureBanco(futureWS, futureBanco, saidaCliente);
				        this.threadPool.submit(juntaResultados);
				        
						break;
					}
					case "c3" : {
					    this.filaComandos.put(comando); //lembrando, bloqueia se tiver cheia
					    saidaCliente.println("Comando c3 adicionado na fila");
					    break;
					}
					case "fim": {
						saidaCliente.println("Desligando o servidor");
						servidor.parar();
						return;
					}
					default: {
						saidaCliente.println("Comando não encontrado");
					}
				}

			}

			saidaCliente.close();
			entradaCliente.close();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
