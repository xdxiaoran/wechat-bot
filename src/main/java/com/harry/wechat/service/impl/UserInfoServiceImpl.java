package com.harry.wechat.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.harry.wechat.dao.RechargeDao;
import com.harry.wechat.dao.UserInfoDao;
import com.harry.wechat.dto.BaseResponse;
import com.harry.wechat.dto.server.FriendDto;
import com.harry.wechat.dto.vo.GetUserDto;
import com.harry.wechat.dto.vo.PageDto;
import com.harry.wechat.dto.vo.RechargeDto;
import com.harry.wechat.dto.vo.RechargeInfoDto;
import com.harry.wechat.entity.Recharge;
import com.harry.wechat.entity.UserInfo;
import com.harry.wechat.service.UserInfoService;
import com.harry.wechat.util.InstructionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Harry
 * @date 2020/10/9
 * Time: 10:10
 * Desc: UserInfoServiceImpl
 */
@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private RechargeDao rechargeDao;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncUserInfo(List<FriendDto> accounts) {
        if (CollectionUtils.isNotEmpty(accounts)) {
            List<UserInfo> all = userInfoDao.findAll();
            List<UserInfo> forSave = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(all)) {
                Map<String, UserInfo> users = Maps.uniqueIndex(all, UserInfo::getWxid);
                accounts.forEach(account -> {
                    UserInfo userInfo = users.get(account.getWxid());
                    if (userInfo != null) {
                        if (!Objects.equals(userInfo.getNickName(), account.getName()) || !Objects.equals(userInfo.getRemarkName(), account.getRemark())) {
                            userInfo.setNickName(account.getName());
                            userInfo.setRemarkName(account.getRemark());
                            forSave.add(userInfo);
                        }
                    } else {
                        userInfo = UserInfo.of(account);
                        forSave.add(userInfo);
                    }
                });

            } else {
                // init
                accounts.forEach(account -> forSave.add(UserInfo.of(account)));
            }

            if (CollectionUtils.isNotEmpty(forSave)) {
                log.info("检测到联系人信息发生变化,同步联系人到数据库");
                userInfoDao.saveAll(forSave);
            }
        }
    }

    @Override
    public BaseResponse getUserList(GetUserDto dto, PageRequest pageRequest) {
        Page<UserInfo> users = userInfoDao.findAll((root, criteriaQuery, criteraBuilder) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtils.isNotBlank(dto.getName())) {
                predicates.add(criteraBuilder.or(criteraBuilder.like(root.get("nickName"), "%" + dto.getName() + "%"),
                        criteraBuilder.like(root.get("remarkName"), "%" + dto.getName() + "%")));
            }

            if (StringUtils.isNotBlank(dto.getMark())) {
                predicates.add(criteraBuilder.like(root.get("mark"), "%" + dto.getMark() + "%"));
            }
            if (dto.getSex() != null) {
                predicates.add(criteraBuilder.equal(root.get("mark"), dto.getSex()));
            }
            if (StringUtils.isNotBlank(dto.getStatus())) {
                predicates.add(criteraBuilder.like(root.get("status"), "%" + dto.getStatus() + "%"));
            }

            return criteraBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, pageRequest);
        return BaseResponse.OK(users);
    }

    @Override
    public UserInfo getUserByWxid(String wxid) {
        Optional<UserInfo> userInfoOptional = userInfoDao.findByWxid(wxid);

        return userInfoOptional.orElseGet(null);
    }

    @Override
    public Boolean recharge(RechargeDto dto) {
        return recharge(dto, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean recharge(RechargeDto dto, boolean notify) {
        Optional<UserInfo> userInfoOptional = userInfoDao.findById(dto.getUserId());
        if (userInfoOptional.isPresent()) {
            if (dto.getBalance().compareTo(BigDecimal.ZERO) == -1) {
                return false;
            }
            UserInfo userInfo = userInfoOptional.get();
            userInfo.setBalance(userInfo.getBalance().add(dto.getBalance()));

            Recharge recharge = Recharge.builder()
                    .balance(dto.getBalance())
                    .userId(userInfo.getId())
                    .type(dto.getType())
                    .build();

            userInfoDao.save(userInfo);
            rechargeDao.save(recharge);

            if (notify) {
                InstructionUtil.sendText(userInfo.getWxid(), "充值成功\n充值金额: " + dto.getBalance() + "\n账户余额: " + userInfo.getBalance());
            }

            return true;
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse save(UserInfo dto) {

        UserInfo userOld;
        if (dto.getId() == null || dto.getId() == 0) {
            userOld = new UserInfo();
        } else {
            userOld = userInfoDao.getOne(dto.getId());
        }

        BeanUtils.copyProperties(dto, userOld);

        userInfoDao.save(userOld);

        return BaseResponse.OK;
    }

    @Override
    public BaseResponse rechargeList(PageRequest pageRequest) {

        Page<Recharge> recharges = rechargeDao.findAll(pageRequest);

        List<Long> userIds = recharges.getContent().stream().map(Recharge::getUserId).collect(Collectors.toList());

        List<UserInfo> userInfos = userInfoDao.findByIdIn(userIds);

        ImmutableMap<Long, UserInfo> userMap = Maps.uniqueIndex(userInfos, UserInfo::getId);

        List<RechargeInfoDto> infoDtos = Lists.newArrayList();
        recharges.getContent().forEach(recharge -> {
            RechargeInfoDto rechargeInfoDto = new RechargeInfoDto();
            BeanUtils.copyProperties(recharge, rechargeInfoDto);
            UserInfo userInfo = userMap.get(rechargeInfoDto.getUserId());
            rechargeInfoDto.setNickName(userInfo.getNickName());
            rechargeInfoDto.setRemarkName(userInfo.getRemarkName());
            infoDtos.add(rechargeInfoDto);
        });

        PageDto<RechargeInfoDto> pageDto = new PageDto<>();
        pageDto.setContent(infoDtos);
        pageDto.setTotalElements(recharges.getTotalElements());

        return BaseResponse.OK(pageDto);
    }

    @Override
    @Transactional
    public BaseResponse markAsRentGroup(Long userId, Integer status) {

        Optional<UserInfo> userInfoOptional = userInfoDao.findById(userId);
        if (userInfoOptional.isPresent()) {
            UserInfo userInfo = userInfoOptional.get();
            userInfo.setIsRentGroup(status == 1);
            userInfoDao.save(userInfo);
            return BaseResponse.OK;
        } else {
            return BaseResponse.fail("查无此人");
        }
    }
}
