package app.thomas;

import app.thomas.manager.ApplicationManager;
import app.thomas.service.CreditService;
import app.thomas.service.ShippingService;
import app.thomas.service.model.Shipping;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GlobalTransactionalTest {

    @Autowired
    private ApplicationManager applicationManager;

    @Autowired
    private CreditService creditService;

    @MockitoBean
    private ShippingService shippingService;

    @Test
    public void globalTransactionalTest_OK() {
        var wallet = creditService.getWallet(1L);
        var shipping = new Shipping();
        shipping.setId(2L);
        when(shippingService.buyShipping(1L, new BigDecimal(4))).thenReturn(shipping);
        applicationManager.buyShipping(1L, new BigDecimal(4));
        var newWallet = creditService.getWallet(1L);
        assertEquals(new BigDecimal("4.00"), wallet.getBalance().subtract(newWallet.getBalance()));
    }

    @Test
    public void globalTransactionalTest_KO() {
        var wallet = creditService.getWallet(1L);
        var shipping = new Shipping();
        shipping.setId(2L);
        when(shippingService.buyShipping(1L, new BigDecimal(4))).thenThrow(new RuntimeException());

        try {
            applicationManager.buyShipping(1L, new BigDecimal(4));
        } catch (Exception e) {}

        var newWallet = creditService.getWallet(1L);
        assertEquals(newWallet.getBalance(), wallet.getBalance());
    }

}
