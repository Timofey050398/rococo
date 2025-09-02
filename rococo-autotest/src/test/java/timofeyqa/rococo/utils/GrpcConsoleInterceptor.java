package timofeyqa.rococo.utils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import timofeyqa.rococo.utils.LogUtils;

@SuppressWarnings("unchecked")
public class GrpcConsoleInterceptor implements ClientInterceptor {

    private static final JsonFormat.Printer printer = JsonFormat.printer();
    private static final Logger LOG = LoggerFactory.getLogger(GrpcConsoleInterceptor.class);
    private static final LogUtils LOG_UTILS = new LogUtils();

    @SuppressWarnings("rawtypes")
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall(
                channel.newCall(method,callOptions)
        ) {
            @Override
            public void sendMessage(Object message) {
                try {
                    LOG.debug("REQUEST: {}",LOG_UTILS.maskLongParams(printer.print((MessageOrBuilder) message)));
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
                super.sendMessage(message);
            }

            @Override
            public void start(Listener responseListener, Metadata headers) {
                ForwardingClientCallListener<Object> clientCallListener = new ForwardingClientCallListener<>() {
                    @Override
                    public void onMessage(Object message) {
                        try {
                            LOG.debug("RESPONSE: {}",LOG_UTILS.maskLongParams(printer.print((MessageOrBuilder) message)));
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e);
                        }
                        super.onMessage(message);
                    }

                    @Override
                    protected Listener<Object> delegate() {
                        return responseListener;
                    }
                };
                super.start(clientCallListener, headers);
            }
        };
    }
}
