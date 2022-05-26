# VertxProtobufEventBusCodec
How to use Protobufs on Vertx Eventbus

I managed to get a Generic Protobuf MessageCodec working..

Feel free to use it if it works for you!

### How to Use
You simply register your Proto with the eventbus as the code below shows:

Notes:

1. If the eventbus is in LOCAL mode, then no serialization takes place and the same proto is used
2. If the eventbus is in CLUSTER mode, then the `encodeToWire` and `decodeFromWire` methods are used

```
// Create the codec using the default instance of the protobuf (SupMessage in my example)
ProtobufMessageCodec<SupMessage> protobufMessageCodec = new ProtobufMessageCodec<>(SupMessage.getDefaultInstance());

// register the codec
_vertx.eventBus().registerDefaultCodec(SupMessage.class, protobufMessageCodec);

// Send the proto on the eventbus
vertx.eventBus().publish("proto.response", mySupProtoInstance);

// Listen for protos on the eventbus
vertx.eventBus().<SupMessage>consumer("proto.response", message -> System.out.println("******** PROTO RESPONSE: ********" + message.body()));
```
