package com.founder.console.web.config;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import org.objenesis.strategy.SerializingInstantiatorStrategy;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KyroSerializer implements RedisSerializer<Object> {
    Kryo kryo = new Kryo();

    public KyroSerializer() {
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new SerializingInstantiatorStrategy()));
        kryo.setRegistrationRequired(false);
    }

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return new byte[0];
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Output output = new Output(outputStream);
            kryo.writeClassAndObject(output, object);
            return output.toBytes();
        } catch (IOException e) {
            throw new SerializationException("Failed Serialization", e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try (Input input = new Input(bytes)) {
            return kryo.readClassAndObject(input);
        }
    }

}
