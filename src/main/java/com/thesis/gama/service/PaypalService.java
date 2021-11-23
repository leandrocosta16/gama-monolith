package com.thesis.gama.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.thesis.gama.dto.PaymentOrderSetDTO;
import com.thesis.gama.exceptions.AlreadyPayedException;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.model.Order;
import com.thesis.gama.model.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

@Service
public class PaypalService {

    private final APIContext apiContext;
    private final OrderService orderService;

    @Autowired
    public PaypalService(APIContext apiContext, OrderService orderService) {
        this.apiContext = apiContext;
        this.orderService = orderService;
    }

    public Payment preparePayment(PaymentOrderSetDTO paymentOrderSetDTO, String successURL, String cancelURL) throws NoDataFoundException, PayPalRESTException, AlreadyPayedException {
        Order order = orderService.getOrderById(paymentOrderSetDTO.getOrderID());
        if(order.getPaymentOrder() == null || order.getPaymentOrder().getState() != PaymentStatus.PAYED) {
            Double totalAmount = order.getTotalPrice() + order.getShipping().getCost();
            orderService.addPaymentToOrder(paymentOrderSetDTO);
            return createPayment(totalAmount, paymentOrderSetDTO.getCurrency(), paymentOrderSetDTO.getMethod(),
                    paymentOrderSetDTO.getIntent(), paymentOrderSetDTO.getDescription(), "http://localhost:8080" + cancelURL + "?orderID=" + order.getId(),
                    "http://localhost:8080" + successURL + "?orderID=" + order.getId());
        }
        else {
            throw new AlreadyPayedException("Order: " + order.getId() + " was already payed.");
        }
    }


    public Payment createPayment(
            Double total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl) throws PayPalRESTException{
        Amount amount = new Amount();
        amount.setCurrency(currency);
        total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
        amount.setTotal(String.format(Locale.US, "%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method.toString());

        Payment payment = new Payment();
        payment.setIntent(intent.toString());
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }


    public void updateOrder(int orderID) throws NoDataFoundException {
        orderService.paymentSuccessful(orderID);
    }


}