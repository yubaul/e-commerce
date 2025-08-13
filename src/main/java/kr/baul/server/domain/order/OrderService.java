package kr.baul.server.domain.order;

import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.AccountReader;
import kr.baul.server.domain.order.orderinfo.OrderInfo;
import kr.baul.server.domain.order.orderinfo.OrderInfoMapper;
import kr.baul.server.domain.order.payment.PaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final AccountReader accountReader;
    private final ItemStockProcessor itemStockProcessor;
    private final OrderFactory orderFactory;
    private final PaymentProcessor paymentProcessor;
    private final OrderInfoMapper orderInfoMapper;

    @Transactional
    public OrderInfo.Order registerOrder(OrderCommand.RegisterOrder registerOrder){
        var userId = registerOrder.userId();
        var requestItems = registerOrder.items();

        itemStockProcessor.deduct(requestItems);
        Order order = orderFactory.store(userId, requestItems);

        Account account = accountReader.getAccount(userId);
        paymentProcessor.process(account, order);

        return orderInfoMapper.of(order);
    }

}
