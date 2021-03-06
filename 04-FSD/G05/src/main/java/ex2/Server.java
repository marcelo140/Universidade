package ex2;

import ex2.com.*;
import io.atomix.catalyst.concurrent.Futures;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    public static void main(String[] args) {
        Map<Integer, Object> objs = new HashMap<>();
        AtomicInteger id = new AtomicInteger(0);

        Store s = new Store();

        ThreadContext tc = new SingleThreadContext("tc-%d", new Serializer());
        Transport t = new NettyTransport();

        tc.serializer().register(StoreSearchReq.class);
        tc.serializer().register(StoreSearchRep.class);
        tc.serializer().register(StoreMakeCartReq.class);
        tc.serializer().register(StoreMakeCartRep.class);
        tc.serializer().register(CartAddReq.class);
        tc.serializer().register(CartAddRep.class);
        tc.serializer().register(CartBuyReq.class);
        tc.serializer().register(CartBuyRep.class);

        tc.execute(() -> {
            t.server().listen(new Address("127.0.0.1:6000"), conn -> {
                conn.handler(StoreSearchReq.class, req -> {
                    Book b = s.search(req.getTitle());

                    return Futures.completedFuture(new StoreSearchRep(b.getIsbn()));
                });
                conn.handler(StoreMakeCartReq.class, req -> {
                    Store.Cart c = s.newCart();
                    int cartId = id.getAndIncrement();

                    objs.put(cartId, c);

                    return Futures.completedFuture(new StoreMakeCartRep(cartId));
                });
                conn.handler(CartAddReq.class, req -> {
                    Store.Cart c = (Store.Cart) objs.get(req.getCartId());
                    Book b = s.getBook(req.getIsbn());

                    c.add(b);

                    return Futures.completedFuture(new CartAddRep());
                });
                conn.handler(CartBuyReq.class, req -> {
                    Store.Cart c = (Store.Cart) objs.get(req.getCartId());

                    boolean success = c.buy();

                    return Futures.completedFuture(new CartBuyRep(success));
                });
            });
        });
    }
}
