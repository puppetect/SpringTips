package org.example;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ImageBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.*;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.ftp.dsl.Ftp;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.ReflectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Consumer;
import java.util.function.Function;

@SpringBootApplication
public class FilesIntegrationApplication {

	@Configuration
	public static class FtpConfig {
		@Bean
		DefaultFtpSessionFactory ftpSessionFactory(
				@Value("${ftp.host:192.168.0.101}") String host,
				@Value("${ftp.port:2121}") int port,
				@Value("${ftp.username:admin}") String username,
				@Value("${ftp.password:admin}") String password
		) {
			DefaultFtpSessionFactory ftpSessionFactory = new DefaultFtpSessionFactory();
			ftpSessionFactory.setHost(host);
			ftpSessionFactory.setPort(port);
			ftpSessionFactory.setUsername(username);
			ftpSessionFactory.setPassword(password);
			return ftpSessionFactory;
		}
	}

	private String ascii = "ascii";


	@Bean
	IntegrationFlow files(@Value("${input-directory:${HOME}/Desktop/in}") File in,
						  Environment environment) {

		GenericTransformer<File, Message<String>> fileStringGenericTransformer = (File file) -> {
			try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				 PrintStream printStream = new PrintStream(byteArrayOutputStream)) {
				ImageBanner imageBanner = new ImageBanner(new FileSystemResource(file));
				imageBanner.printBanner(environment, getClass(), printStream);
				return MessageBuilder.withPayload(new String(byteArrayOutputStream.toByteArray()))
						.setHeader(FileHeaders.FILENAME, file.getAbsoluteFile().getName())
						.build();
			} catch (IOException e) {
				ReflectionUtils.rethrowRuntimeException(e);
				e.printStackTrace();
			}
			return null;
		};

		return IntegrationFlows.from(
				Files.inboundAdapter(in).autoCreateDirectory(true).preventDuplicates(true).patternFilter("*.jpg"),
				e -> e.poller(Pollers.fixedDelay(1000)))
				.transform(File.class, fileStringGenericTransformer)
				.channel(this.asciiProcessor())

				.get();
	}

	@Bean
	IntegrationFlow ftp(
			DefaultFtpSessionFactory ftpSessionFactory) {
		return IntegrationFlows.from(this.asciiProcessor())
				.handle(Ftp.outboundAdapter(ftpSessionFactory)
						.remoteDirectory("uploads")
						.fileNameGenerator(message -> {
							Object o = message.getHeaders().get(FileHeaders.FILENAME);
							String fileName = String.class.cast(o);
							return fileName.split("\\.")[0] + ".txt";
						}))
				.get();
	}

	@Bean
	IntegrationFlow amqp(AmqpTemplate amqpTemplate) {
		return IntegrationFlows.from(this.asciiProcessor())
				.handle(Amqp.outboundAdapter(amqpTemplate).exchangeName(this.ascii).routingKey(this.ascii))
				.get();
	}

	@Bean
	MessageChannel asciiProcessor() {
		return MessageChannels.publishSubscribe().get();
	}

	public static void main(String[] args) {
		SpringApplication.run(FilesIntegrationApplication.class, args);
	}

}
