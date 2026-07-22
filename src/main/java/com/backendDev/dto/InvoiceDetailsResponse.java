package com.backendDev.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Invoice details assembled from multiple existing tables — no dedicated
 * "invoice" table is used, everything here is read straight off:
 *   - tabletop_leo_orders           (order, amount, gst, admin_id, table no)
 *   - tabletop_leo_order_items      (item rows)
 *   - tabletop_leo_customer_sessions(customer email)
 *   - tabletop_leo_business_information (business name, for display)
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InvoiceDetailsResponse {

    private String invoiceNumber;   // order_number, e.g. TL-30511
    private String orderId;
    private String adminId;
    private String businessId;
    private String businessName;

    private String customerName;
    private String customerPhone;
    private String customerEmail;   // resolved from CustomerSession, may be null
    private String tableNumber;
    private String orderType;

    private BigDecimal subtotal;
    private BigDecimal gstAmount;
    private BigDecimal grandTotal;

    private String paymentStatus;
    private String paymentMethod;

    private LocalDateTime createdAt;

    private List<InvoiceItemDto> items;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class InvoiceItemDto {
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
    }
}
