package com.example.unixsocket;

import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class UnixsocketApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(UnixsocketApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(UnixsocketApplication.class, args);
	}

	@Override
	public void run(String... args) /*throws Exception*/ {
		File socketFile = new File("/tmp/app.world");
		AFUNIXSocket sock = null;

		while (true) {

			if (sock == null || !sock.isConnected()) {
				try {
					sock = AFUNIXSocket.newInstance();
					sock.connect(new AFUNIXSocketAddress(socketFile));
				} catch (Exception exception) {
					log.error("Erro");
				}
			}

			try {

				log.info("OK!");

				while (sock.getInputStream().available() != 0) {
					String message = readEventMessage(sock);
					log.info("Messagem: {}", message);
				}
				TimeUnit.SECONDS.sleep(1);

				sendEvent(sock);
			} catch (Exception exception) {
				log.error("Ocorreu uma excecao");
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

}
