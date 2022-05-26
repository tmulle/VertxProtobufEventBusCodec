import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBufUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import java.util.Objects;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

/**
 * Codec for encoding/decoding Protobufs on the EventBus
 *
 * @author tmulle
 * @param <T> Class that extends/implements MessageLite
 */
public class ProtobufMessageCodec<T extends MessageLite> implements MessageCodec<T, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtobufMessageCodec.class);
    private final T prototype;

    /**
     * Take in the instance we will parse
     *
     * @param prototype
     */
    public ProtobufMessageCodec(T prototype) {
        Objects.requireNonNull(prototype, "Prototype message must be supplied");
        this.prototype = prototype;
    }

    /**
     * Encode the protobuf to the wire
     * 
     * @param buffer Buffer to write the data to
     * @param msg Protobuf message to encode
     */
    @Override
    public void encodeToWire(Buffer buffer, T msg) {
        LOGGER.debug("Encoding protobuf to wire");
        if (msg instanceof MessageLite) {
            MessageLite mlight = (MessageLite) msg;
            buffer.appendBytes(mlight.toByteArray());
            return;
        }
        if (msg instanceof MessageLite.Builder) {
            MessageLite.Builder mlightBuilder = (MessageLite.Builder) msg;
            buffer.appendBytes(mlightBuilder.build().toByteArray());
        }
    }

    /**
     * Decode the protobuf from the wire
     *
     * @param pos starting position of the protodata in the message
     * @param buffer Payload
     * @return Decoded protobuf or null if error
     */
    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        LOGGER.debug("Decoding protobuf from wire at pos: {} and length: {} - {}",
                pos, buffer.length(), ByteBufUtil.prettyHexDump(buffer.getByteBuf()));

        T instance = null;

        try {

            // NOTE: Length is the length of the payload data 
            // NOTE: pos is the start of the payload data in the message
            byte[] bytes = buffer.getBytes(pos, buffer.length());
            instance = (T) prototype.getParserForType().parseFrom(bytes);
            
        } catch (InvalidProtocolBufferException ex) {
            LOGGER.error("Error decoding protobuf", ex);
        }

        return instance;
    }

    /**
     * Used to simply return the original message when EventBus
     * is being used in LOCAL mode
     * 
     * @param s T
     * @return T
     */
    @Override
    public T transform(T s) {
        return s;
    }

    @Override
    public String name() {
        return getClass().getName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }

}
