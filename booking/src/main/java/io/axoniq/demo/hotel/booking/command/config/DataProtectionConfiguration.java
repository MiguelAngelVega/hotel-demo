package io.axoniq.demo.hotel.booking.command.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import io.axoniq.dataprotection.api.FieldEncryptingSerializer;
import io.axoniq.dataprotection.cryptoengine.CryptoEngine;
import io.axoniq.dataprotection.cryptoengine.vault.VaultCryptoEngine;
import okhttp3.OkHttpClient;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataProtectionConfiguration {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Bean
    public CryptoEngine cryptoEngine(OkHttpClient okHttpClient) {
        return new VaultCryptoEngine(okHttpClient,
                                     "http://localhost:8200",
                                     "s.VGzifiHuUuZbQjFew7FMWE5F",
                                     "hotel-keys/");
    }

    @Bean("eventSerializer")
    public Serializer eventSerializer(CryptoEngine cryptoEngine, Serializer messageSerializer) {
        return new FieldEncryptingSerializer(cryptoEngine, messageSerializer);
    }

    @Bean("messageSerializer")
    public Serializer messageSerializer() {
        ObjectMapper objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .registerModule(new Jdk8Module())
                .registerModule(new KotlinModule());
        return JacksonSerializer
                .builder()
                .objectMapper(objectMapper)
                .build();
    }
}
