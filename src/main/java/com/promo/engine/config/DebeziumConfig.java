package com.promo.engine.config;

import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.storage.FileOffsetBackingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DebeziumConfig {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private DebeziumEngine<ChangeEvent<String, String>> engine;

    @Bean
    public Configuration debeziumConfig() {
        return Configuration.create()
            .with("name", "promotion-engine-connector")
            .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
            .with("offset.storage", FileOffsetBackingStore.class.getName())
            .with("offset.storage.file.filename", "./offsets.dat")
            .with("offset.flush.interval.ms", "60000")
            .with("database.hostname", "db")
            .with("database.port", "5432")
            .with("database.user", "promouser")
            .with("database.password", "promopass")
            .with("database.dbname", "promodb")
            .with("database.server.name", "promotion-engine")
            .with("schema.include.list", "public")
            .with("table.include.list", "public.promotions,public.rules,public.actions")
            .with("plugin.name", "pgoutput")
            .build();
    }

    @PostConstruct
    private void start() {
        Configuration config = debeziumConfig();
        this.engine = DebeziumEngine.create(Json.class)
            .using(config.asProperties())
            .notifying(this::handleEvent)
            .build();

        executor.execute(engine);
    }

    private void handleEvent(ChangeEvent<String, String> event) {
        // Here you can implement your logic to handle the CDC events
        // For example, sending to Kafka, updating cache, etc.
        log.info("Received CDC event - Key: {}, Value: {}", event.key(), event.value());
    }

    @PreDestroy
    private void stop() {
        if (engine != null) {
            try {
                engine.close();
            } catch (IOException e) {
                log.error("Error closing Debezium engine", e);
            }
        }
    }
} 