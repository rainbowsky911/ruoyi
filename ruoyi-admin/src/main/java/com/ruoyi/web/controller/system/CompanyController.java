package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.HeFeiCompanyAdvice;
import com.ruoyi.web.PageResult;
import com.ruoyi.web.RequestParams;
import com.ruoyi.web.controller.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zdw
 * @Date: 2022/5/27 10:47
 * @Description:
 */

@RestController
@RequestMapping("/company")
public class CompanyController extends BaseController {


    @Autowired
    private CompanyService companyService;


    /**
     * 获取用户列表
     */

    @GetMapping("list")
    public PageResult search(RequestParams params) {
        return companyService.listCompany(params);
    }

    @GetMapping("suggestion")
    public List<String> getSuggestion(@RequestParam("key") String key) {
        return companyService.getSuggestion(key);
    }
}
