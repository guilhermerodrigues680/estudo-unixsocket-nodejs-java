package com.example.unixsocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class UnixsocketApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(UnixsocketApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(UnixsocketApplication.class, args);
	}

	@Override
	public void run(String... args) /*throws Exception*/ {
		log.debug("Iniciando servidor...");
		File socketFile = new File("/tmp/app.world");
		AFUNIXServerSocket server = null;
		AFUNIXSocket clientSocket;

		while (true) {

			if (server == null) {
				try {
					server = AFUNIXServerSocket.newInstance();
					server.bind(new AFUNIXSocketAddress(socketFile));
					log.info("OK! Servidor iniciado");
				} catch (Exception exception) {
					log.error("Ocorreu um erro ao iniciar o servidor",  exception);
					log.info("Tentando iniciar novamente");

					try { TimeUnit.SECONDS.sleep(1); }
					catch (Exception e) { }

					continue;
				}
			}

			try {
				log.info("Aguardando um cliente");
				clientSocket = server.accept();
				log.info("Cliente conectado");
			} catch (Exception exception) {
				log.error("Ocorreu um erro ao receber estabelecer a conexão com cliente", exception);
				continue;
			}

			// Contem cliente
			try {
				long ultimoPing = 0L;
				long intervaloPing = 1L * 2000L;
				// Caso o cliente se desconecte uma excessao será lancada
				while (true) {

					if (clientSocket.getInputStream().available() > 0) {
						String msg = readEventMessage(clientSocket);
						log.info("Mensagem recebida: {}", msg);
						UnixSocketEvent event = new UnixSocketEvent("app.message", new UnixSocketData("server", "OK!"));
						String response = jsonWrite(event);
						log.info("Enviando resposta: {}", response);
						clientSocket.getOutputStream().write((response + "\f").getBytes());
					}

					long currentTimeMillis = System.currentTimeMillis();
					if (currentTimeMillis - ultimoPing > intervaloPing) {
						ultimoPing = currentTimeMillis;
						UnixSocketEvent event = new UnixSocketEvent("app.ping", new UnixSocketData("server", Instant.ofEpochMilli(currentTimeMillis).atZone(ZoneId.systemDefault()).toLocalDateTime().toString()));
						String response = jsonWrite(event);
						log.debug("Enviando ping {}", response);
						clientSocket.getOutputStream().write((response + "\f").getBytes());
					}

					TimeUnit.MILLISECONDS.sleep(100);
				}

			} catch (Exception exception) {
				log.error("Ocorreu um erro");
			} finally {
				try { clientSocket.close(); }
				catch (Exception exception) { }
			}


		}

	}

	private void sendEvent(AFUNIXSocket sock) throws IOException {
		//var g = new PrintWriter(sock.getOutputStream(), true);
		//g.printf("{\"type\":\"message\",\"data\":{\"foo\":\"olasa\"}}\f");

		//sock.getOutputStream().write("{\"type\":\"message\",\"data\":{\"foo\":\"olaassa\"}}\f".getBytes());
		sock.getOutputStream().write("{\"type\":\"app.message\",\"data\":{\"id\":\"world\",\"message\":\"s world!\"}}\f".getBytes());
		//String resposta = new BufferedReader(new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining(System.lineSeparator()));
	}

	private String readEventMessage(AFUNIXSocket sock) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[1024];
		sock.getInputStream().available();
		nRead = sock.getInputStream().read(data, 0, sock.getInputStream().available());
		buffer.write(data, 0, nRead);
		//while ((nRead = sock.getInputStream().read(data, 0, data.length)) != -1) {
		//	buffer.write(data, 0, nRead);
		//}

		buffer.flush();
		byte[] byteArray = buffer.toByteArray();

		String text = new String(byteArray, StandardCharsets.UTF_8);
		return text;
	}

	private void jsonRead() {
		ObjectMapper objectMapper = new ObjectMapper();

	}

	private String jsonWrite(UnixSocketEvent event) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(event);
		} catch (Exception exception) {
			log.info("ERRO");

		}

		return "";
	}

}
