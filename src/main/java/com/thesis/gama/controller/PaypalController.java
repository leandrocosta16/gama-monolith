package com.thesis.gama.controller;

import com.thesis.gama.dto.PaymentOrderSetDTO;
import com.thesis.gama.exceptions.AlreadyPayedException;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.service.PaypalService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

@Controller
@Api(tags = "PaypalController")
public class PaypalController {

    @Autowired
    PaypalService service;

    private static final String SUCCESS_URL = "/pay/paypal/success";
    private static final String CANCEL_URL = "/pay/paypal/cancel";

    @PostMapping(value = "/pay/paypal")
    public String payment(@RequestBody PaymentOrderSetDTO paymentOrder) throws AlreadyPayedException {
        try {
            Payment payment = service.preparePayment(paymentOrder, SUCCESS_URL, CANCEL_URL);

            for(Links link:payment.getLinks()) {
                if(link.getRel().equals("approval_url")) {
                    return "redirect:"+link.getHref();
                }
            }

        } catch (PayPalRESTException | NoDataFoundException e) {

            e.printStackTrace();
        }
        return "didnt work out";
    }

    @GetMapping(value = CANCEL_URL)
    public String cancelPay() {
        System.out.println("canceled");
        return "cancel";
    }

    @GetMapping(value = SUCCESS_URL)
    public String successPay(@RequestParam("orderID") int orderID, @RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = service.executePayment(paymentId, payerId);
            System.out.println(payment.toJSON());
            System.out.println(orderID);
            service.updateOrder(orderID);
            //chamar order service que poe order como approved
            //no paymentOrder, atualiza o payment time e guarda-o
            //em microserviços eu publicaria aqui um evento de order payed no oayment service e o order services subscreveria
            if (payment.getState().equals("approved")) {
                return "redirect:https://www.google.com/"; //isto é para onde vai depois do site do paypal, ou seja quero por uma página do frontend, maybe orders
            }
        } catch (PayPalRESTException | NoDataFoundException e) {
            System.out.println(e.getMessage());
        }
        return "didnt work out"; //isto é para onde vai depois do site do paypal, ou seja quero por uma página do frontend
    }


}