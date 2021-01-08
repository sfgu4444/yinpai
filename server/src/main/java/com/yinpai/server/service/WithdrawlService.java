package com.yinpai.server.service;

import com.yinpai.server.domain.dto.LoginAdminInfoDto;
import com.yinpai.server.domain.entity.admin.Admin;
import com.yinpai.server.domain.entity.admin.Message;
import com.yinpai.server.domain.entity.admin.Withdrawal;
import com.yinpai.server.domain.repository.admin.AdminRepository;
import com.yinpai.server.domain.repository.admin.WithdrawalRepository;
import com.yinpai.server.exception.ProjectException;
import com.yinpai.server.thread.threadlocal.LoginAdminThreadLocal;
import com.yinpai.server.utils.CheckBankCard;
import com.yinpai.server.utils.ProjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @program server
 * @description: 提现
 * @author: liuzhenda
 * @create: 2021/01/06 14:06
 */
@Slf4j
@Service
public class WithdrawlService {

    private final WithdrawalRepository withdrawalRepository;

    private final MessageService messageService;

    private final AdminService adminService;

    private final AdminRepository adminRepository;

    @Autowired
    public WithdrawlService(WithdrawalRepository withdrawalRepository, MessageService messageService, AdminService adminService, AdminRepository adminRepository) {
        this.withdrawalRepository = withdrawalRepository;
        this.messageService = messageService;
        this.adminService = adminService;
        this.adminRepository = adminRepository;
    }

    public Page<Withdrawal> findFilterAll(Pageable pageable) {
        LoginAdminInfoDto adminInfoDto = LoginAdminThreadLocal.get();
        HashMap<String, String> conditionMap = new HashMap<>();
        conditionMap.put("id", "=");
        HttpServletRequest request = ProjectUtil.getRequest();
        String adminName = request.getParameter("adminName");
        log.info("【提现账号检索】: {}", adminName);
        Admin adminFind = null;
        if (StringUtils.isNotBlank(adminName)) {
            adminFind = adminRepository.findByAdminName(adminName);
        }

        Page<Withdrawal> recordRepositoryAll;
        Admin admin = null;
        if (adminInfoDto.isSuperAdmin()) {
            if (null == adminFind) {
                recordRepositoryAll = withdrawalRepository.findAll(ProjectUtil.getSpecification(conditionMap), pageable);
            } else {
                recordRepositoryAll = withdrawalRepository.findAllByAdminId(adminFind.getId(), pageable);
                admin = adminFind;
            }
        } else {
            recordRepositoryAll = withdrawalRepository.findAllByAdminId(adminInfoDto.getAdminId(), pageable);
            admin = adminService.findById(adminInfoDto.getAdminId());
        }

        for (Withdrawal withdrawal : recordRepositoryAll) {
            if (adminInfoDto.isSuperAdmin()) {
                Admin ad = adminService.findById(withdrawal.getAdminId());
                withdrawal.setAdminName(ad.getAdminName());
            } else {
                if (null != admin) {
                    withdrawal.setAdminName(admin.getAdminName());
                }
            }

        }
        return recordRepositoryAll;
    }


    public void delete(Integer id) {
        Withdrawal withdrawal = findByIdNotNull(id);
        if (withdrawal.getState() == 0) {
            throw new ProjectException("提现正在申请中，不可删除");
        }
        withdrawalRepository.delete(withdrawal);
    }

    public Withdrawal findByIdNotNull(Integer id) {
        return withdrawalRepository.findById(id).orElseThrow(() -> new ProjectException("无此ID"));
    }

    public Withdrawal add(Withdrawal withdrawal) {
        try {
            LoginAdminInfoDto adminInfoDto = LoginAdminThreadLocal.get();
            Admin admin = adminService.findById(adminInfoDto.getAdminId());
            if (withdrawal.getMoney() <= 0) {
                throw new ProjectException("金额输入不合法");
            }
            if (admin.getMoney() < withdrawal.getMoney()) {
                throw new ProjectException("提现金额，大于账户余额");
            }
            withdrawal.setBankAccount(admin.getBandkAccount());
            withdrawal.setState(0);
            withdrawal.setAdminId(adminInfoDto.getAdminId());
            withdrawalRepository.save(withdrawal);
            Integer money = admin.getMoney() - withdrawal.getMoney();
            admin.setMoney(money);
            adminRepository.save(admin);
            Message message = new Message();
            message.setSend(adminInfoDto.getAdminId());
            message.setSendName(admin.getAdminName());
            message.setReceive(1);
            Admin ads = adminService.findById(1);
            message.setReceiveName(ads.getAdminName());
            message.setMessage("【申请提现】金额 ：【 " + withdrawal.getMoney() + " 元】");
            messageService.add(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ProjectException(e.getMessage());
        }
        return withdrawal;
    }

    public void edit(Withdrawal withdrawal) {
        if (LoginAdminThreadLocal.get().isSuperAdmin()) {
            withdrawal.setState(1);
            withdrawalRepository.save(withdrawal);
            Message message = new Message();
            message.setSend(withdrawal.getAdminId());
            Admin ads = adminService.findById(1);
            Admin admin = adminService.findById(withdrawal.getAdminId());
            message.setSendName(ads.getAdminName());
            message.setReceiveName(admin.getAdminName());
            message.setReceive(withdrawal.getAdminId());
            message.setMessage("【提现成功】金额 ：【 " + withdrawal.getMoney() + " 元】");
            messageService.add(message);
        } else {
            throw new ProjectException("操作违法");
        }
    }

    public void bind(Admin admin) {
        Admin admins = adminService.findById(admin.getId());
        if (StringUtils.isBlank(admin.getNameBandk())) {
            throw new ProjectException("请输入姓名");
        }
        if (StringUtils.isBlank(admin.getBandk())) {
            throw new ProjectException("请输入开户行");
        }
        if (StringUtils.isBlank(admin.getBandkAccount().toString())) {
            throw new ProjectException("请输入银行卡号");
        }
        admins.setNameBandk(admin.getNameBandk());
        admins.setBandk(admin.getBandk());
        if (!CheckBankCard.checkBankCard(String.valueOf(admin.getBandkAccount()))) {
            throw new ProjectException("银行卡号有误");
        }
        admins.setBandkAccount(admin.getBandkAccount());
        adminRepository.save(admins);
    }
}
