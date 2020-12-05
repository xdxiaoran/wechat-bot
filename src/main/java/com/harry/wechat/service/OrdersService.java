package com.harry.wechat.service;

import com.harry.wechat.dto.BaseResponse;
import com.harry.wechat.dto.vo.RentCancelDto;
import com.harry.wechat.dto.vo.RentDto;
import org.springframework.data.domain.PageRequest;

/**
 * @author Harry
 * @date 2020/10/12
 * Time: 15:38
 * Desc: OrdersService
 */
public interface OrdersService {

    BaseResponse rent(RentDto dto);

    BaseResponse getOrders(PageRequest pageRequest);

    BaseResponse cancelRent(RentCancelDto dto);

    BaseResponse totalAmount();

}
