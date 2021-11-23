package com.thesis.gama.service;


import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.thesis.gama.dto.PaymentOrderSetDTO;
import com.thesis.gama.dto.StripeResponseDTO;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {


    @Value("${stripe.secret.key}")
    String secretKey;
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;


    @Autowired
    OrderService orderService;

    public StripeResponseDTO paymentIntent(PaymentOrderSetDTO paymentIntentDto) throws StripeException, NoDataFoundException {
        Order order = orderService.getOrderById(paymentIntentDto.getOrderID());
        //int totalAmount = (int) (order.getTotalPrice()+order.getShipping().getCost());
        Stripe.apiKey = secretKey;

        PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                .setCurrency(paymentIntentDto.getCurrency())
                .setAmount((long) ((order.getTotalPrice()+order.getShipping().getCost()) * 100L))
                .setDescription(paymentIntentDto.getDescription())
                .putMetadata("orderID", String.valueOf(order.getId()))
                .build();

        RequestOptions options = RequestOptions
                .builder()
                .setIdempotencyKey(paymentIntentDto.getIdempotencyKey())
                .build();

        orderService.addPaymentToOrder(paymentIntentDto);

        PaymentIntent intent = PaymentIntent.create(createParams, options);

        return new StripeResponseDTO(intent.getClientSecret());
        /**
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");
        Map<String, Object> params = new HashMap<>();
        params.put("amount", 100);
        params.put("currency", paymentIntentDto.getCurrency());
        params.put("description", paymentIntentDto.getDescription());
        params.put("payment_method_types", paymentMethodTypes);

        //verificar state da order para nao pagar repetido?

        return PaymentIntent.create(params);

         **/
    }

    /**

    public PaymentIntent confirm(String id) throws StripeException {
        Stripe.apiKey = secretKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(id);
        Map<String, Object> params = new HashMap<>();
        params.put("payment_method", "pm_card_visa");
        paymentIntent.confirm(params);
        //make changes to order, but how do I get it
        return paymentIntent;
    }

    public PaymentIntent cancel(String id) throws StripeException {
        Stripe.apiKey = secretKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(id);
        paymentIntent.cancel();
        return paymentIntent;
    }

     **/



    public String handleStripeEvents(String payload, String sigHeader) throws NoDataFoundException {
        Stripe.apiKey = secretKey;

        if (sigHeader == null) {
            return "";
        }
        Event event;
        // Only verify the event if you have an endpoint secret defined.
        // Otherwise use the basic event deserialized with GSON.
        try {
            event = Webhook.constructEvent(
                    payload, sigHeader, endpointSecret
            );

        } catch (SignatureVerificationException e) {
            // Invalid signature
            System.out.println("⚠️  Webhook error while validating signature.");
            return "";
        }
        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
            // instructions on how to handle this case, or return an error here.
        }
        // Handle the event
        switch (event.getType()) {

            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                System.out.println("-----------");
                System.out.println(paymentIntent.getMetadata().get("orderID"));
                orderService.paymentSuccessful(Integer.parseInt(paymentIntent.getMetadata().get("orderID")));
                System.out.println("Payment for " + paymentIntent.getAmount() + " succeeded.");
                // Then define and call a method to handle the successful payment intent.
                //EVERTYHING IS WORKING
                //BUT NOW I DONT KNOW HOW TO REDIRECT THE USER TO OTHER PAGE TO PREVENT REPLICATED PAYMENTS

                // handlePaymentIntentSucceeded(paymentIntent);
                break;

            case "payment_method.attached":
                PaymentMethod paymentMethod = (PaymentMethod) stripeObject;
                // Then define and call a method to handle the successful attachment of a PaymentMethod.
                // handlePaymentMethodAttached(paymentMethod);
                break;

            default:
                System.out.println("Unhandled event type: " + event.getType());
                break;
        }
        return "";
    }



}
