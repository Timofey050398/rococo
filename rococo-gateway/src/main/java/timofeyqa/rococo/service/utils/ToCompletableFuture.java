package timofeyqa.rococo.service.utils;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.experimental.UtilityClass;

import java.util.concurrent.CompletableFuture;

@UtilityClass
public class ToCompletableFuture {

    public static <T> CompletableFuture<T> toCf(ListenableFuture<T> lf) {
        CompletableFuture<T> cf = new CompletableFuture<>();
        Futures.addCallback(lf, new FutureCallback<>() {
            public void onSuccess(T result) { cf.complete(result); }
            public void onFailure(Throwable t) { cf.completeExceptionally(t); }
        }, MoreExecutors.directExecutor());
        return cf;
    }
}
