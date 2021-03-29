package com.soen390.team11.service;

import com.soen390.team11.constant.Type;
import com.soen390.team11.dto.OrderDto;
import com.soen390.team11.dto.OrderResponseDto;
import com.soen390.team11.entity.Orders;
import com.soen390.team11.entity.RawMaterial;
import com.soen390.team11.entity.Vendors;
import com.soen390.team11.repository.OrdersRepository;
import com.soen390.team11.repository.RawMaterialRepository;
import com.soen390.team11.repository.VendorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service Layer for Order
 */
@Service
public class OrdersService {

    OrdersRepository ordersRepository;
    VendorsRepository vendorsRepository;
    RawMaterialRepository rawMaterialRepository;

    public OrdersService(OrdersRepository ordersRepository, VendorsRepository vendorsRepository, RawMaterialRepository rawMaterialRepository) {
        this.ordersRepository = ordersRepository;
        this.vendorsRepository = vendorsRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    /**
     * Creates a new Order
     *
     * @param orderDto The Order details
     * @return The new Order's ID
     */
    public String createOrder(OrderDto orderDto)
    {
        Orders order = new Orders(orderDto.getVendorID(), orderDto.getSaleID() ,orderDto.getQuantity(), orderDto.getDateTime());
        Orders result = ordersRepository.save(order);
        return result.getOrderID();
    }

    /**
     * Gets an Order
     *
     * @param orderID The order's ID
     * @return The Order
     */
    public Optional<OrderDto> getOrderById(String orderID)
    {
        Optional<Orders> order = ordersRepository.findByOrderID(orderID);
        if (order.isPresent())
        {
            return Optional.of(new OrderDto(order.get().getVendorID(), order.get().getSaleID(),order.get().getQuantity(), order.get().getTime()));
        }
        else
        {
            return Optional.empty();
        }
    }

    /**
     * Gets all Orders
     *
     * @return List of all Orders
     */
    public List<OrderResponseDto> getAllOrders()
    {
        Iterable<Orders> orders = ordersRepository.findAll();
        List<OrderResponseDto> orderDtos = new ArrayList<>();
        Vendors vendor=null;
        RawMaterial rawMaterial=null;
        for (Orders order: orders) {
            rawMaterial=null;
            vendor=vendorsRepository.findByVendorID(order.getVendorID()).get();
            if(rawMaterialRepository.existsById(order.getSaleID())){
                rawMaterial=rawMaterialRepository.findByRawmaterialid(order.getSaleID()).get();
            }
            OffsetDateTime dateTime = OffsetDateTime.now();
            orderDtos.add(new OrderResponseDto(
                    vendor.getCompanyname(), vendor.getVendorID(),Type.RAW_MATERIAL.toString(),rawMaterial==null?order.getSaleID():rawMaterial.getName(), order.getSaleID(),
                    order.getQuantity(), order.getTime().isAfter(dateTime)?"Not Receive":"Receive")
            );
        }
        return orderDtos;
    }

    /**
     * Gets all orders before a certain time
     *
     * @return Iterable of orders before a time
     */
    public Iterable<Orders> getOrdersBeforeNow()
    {
        return ordersRepository.findAllByTimeBefore(OffsetDateTime.now());
    }
}
