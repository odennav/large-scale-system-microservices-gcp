package com.ntw.oms.order.processor;

import com.ntw.oms.order.dao.OrderDao;
import com.ntw.oms.order.dao.OrderDaoFactory;
import com.ntw.oms.order.entity.InventoryReservation;
import com.ntw.oms.order.entity.Order;
import com.ntw.oms.order.entity.OrderLine;
import com.ntw.oms.order.entity.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Component
public class OrderPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OrderPostProcessor.class);

    @Autowired
    private OrderDaoFactory orderDaoFactory;

    private OrderDao orderDaoBean;

    @Value("${database.type}")
    private String orderDBType;

    @Autowired
    private InventoryClient inventoryClientBean;

    @PostConstruct
    public void postConstruct() throws Exception {
        this.orderDaoBean = orderDaoFactory.getOrderDao(orderDBType);
    }

    public OrderDao getOrderDaoBean() {
        return orderDaoBean;
    }

    public void setOrderDaoBean(OrderDao orderDaoBean) {
        this.orderDaoBean = orderDaoBean;
    }

    public void setInventoryClientBean(InventoryClient inventoryClientBean) {
        this.inventoryClientBean = inventoryClientBean;
    }

    public InventoryClient getInventoryClientBean() {
        return inventoryClientBean;
    }

    public boolean processOrder(Order order) {
        return reserveInventory(order);
    }

    private boolean reserveInventory(Order order) {
        List<OrderLine> orderLines = order.getOrderLines();
        InventoryReservation inventoryReservation = new InventoryReservation();
        for (OrderLine ol : orderLines) {
            inventoryReservation.addInvResLine(ol.getProductId(), ol.getQuantity());
        }
        try {
            if (!getInventoryClientBean().reserveInventory(inventoryReservation)) {
                logger.error("Unable to reserve inventory; context={}", inventoryReservation);
                return false;
            }
        } catch (IOException e) {
            logger.error("Unable to reserve inventory for {}; exception={}", inventoryReservation, e);
            return false;
        }
        // persist order
        order.setStatus(OrderStatus.CREATED);
        if (!getOrderDaoBean().saveOrder(order)) {
            logger.error("Unable to create order; context={}", order);
            // ToDo: Async Rollback inventory reservations
        }
        logger.debug("Created order; context={}", order);
        return true;
    }

}
