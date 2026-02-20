package com.spring.boot.levelup.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.levelup.payment.dto.CreatePaymentRequest;
import com.spring.boot.levelup.payment.entity.payment.PaymentProvider;
import com.spring.boot.levelup.payment.service.OrderService;
import com.spring.boot.levelup.payment.service.PaymentService;
import com.spring.boot.levelup.payment.mappers.PaymentMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
public class PaymentControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private PaymentMapper paymentMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testInvalidOrderIdReturns400() throws Exception {
        Mockito.when(paymentService.createPayment(any(), any()))
                .thenThrow(new IllegalArgumentException("Order not found"));

        CreatePaymentRequest request = new CreatePaymentRequest(999L, PaymentProvider.STRIPE);

        mockMvc.perform(post("/api/payments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Order not found"))
                .andExpect(jsonPath("$.path").value("/api/payments/create"));
    }

    @Test
    void testPaidOrderReturns409() throws Exception {
        Mockito.when(paymentService.createPayment(any(), any()))
                .thenThrow(new IllegalStateException("Order cannot accept new payment"));

        CreatePaymentRequest request = new CreatePaymentRequest(1L, PaymentProvider.STRIPE);

        mockMvc.perform(post("/api/payments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("INVALID_STATE"))
                .andExpect(jsonPath("$.message").value("Order cannot accept new payment"))
                .andExpect(jsonPath("$.path").value("/api/payments/create"));
    }

    @Test
    void testValidationFailureReturns400() throws Exception {
        String invalidRequestJson = "{\"provider\":\"STRIPE\"}"; // Missing orderId

        mockMvc.perform(post("/api/payments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/payments/create"));
    }
}
