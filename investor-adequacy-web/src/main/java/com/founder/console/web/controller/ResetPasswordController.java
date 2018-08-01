package com.founder.console.web.controller;

import com.founder.console.web.config.annotation.WebController;
import com.founder.console.web.utils.LoginUtils;
import com.founder.console.web.utils.PdfGeneratorUtil;
import com.founder.console.web.view.DownloadingView;
import com.founder.contract.business.ClientUserService;
import com.founder.dto.business.CheckedItem;
import com.founder.dto.sysadmin.ResetPasswordDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@WebController
@Slf4j
@RequestMapping("/resetPassword")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResetPasswordController {

    private String failureKeyAttribute = "error";

    private final ClientUserService clientUserService;

    private String resetPasswordPage = "resetPassword";

    private String mainPage = "redirect:/";


    private final  PdfGeneratorUtil pdfGeneratorUtil;

    @RequestMapping(method = RequestMethod.GET)
    public String index(HttpSession session) {

        return resetPasswordPage;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String resetPassword(HttpServletRequest req, ResetPasswordDto resetPasswordDto) {
        if (req.getAttribute(failureKeyAttribute) != null)
            return resetPasswordPage;

        clientUserService.changePasswordByLoginName(resetPasswordDto.getUserName(), resetPasswordDto.getPassword());

        return LoginUtils.loginDirectly(req, resetPasswordDto.getUserName(), resetPasswordDto.getPassword(), failureKeyAttribute, resetPasswordPage, mainPage);
    }

    @RequestMapping(
            value = "/test",
            method = RequestMethod.GET
    )
    public View download() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("gender", "男");
        map.put("birthday", "2000-01-02");
        map.put("nationality", "中华人民共和国");
        map.put("controlPerson", "刘德华");
        map.put("favoree", "刘德华");

        List<CheckedItem> occpupyCheckedItems = new ArrayList<>();
        occpupyCheckedItems.add(new CheckedItem("党政机关工作人员", false));
        occpupyCheckedItems.add(new CheckedItem("企事业单位职工", false));
        occpupyCheckedItems.add(new CheckedItem("农民", false));
        occpupyCheckedItems.add(new CheckedItem("个体工商户", false));
        occpupyCheckedItems.add(new CheckedItem("注册会计师", false));
        occpupyCheckedItems.add(new CheckedItem("律师", false));
        occpupyCheckedItems.add(new CheckedItem("学生", false));
        occpupyCheckedItems.add(new CheckedItem("金融机构从业人员", false));
        occpupyCheckedItems.add(new CheckedItem("金融机构高级管理人员", true));
        occpupyCheckedItems.add(new CheckedItem("无业", false));
        occpupyCheckedItems.add(new CheckedItem("其他，", false));
        map.put("occupyCheckItems", occpupyCheckedItems);
        //map.put("occupyOther", "樱桃小丸子");

        map.put("company", "方正证券");
        map.put("post", "首席架构师");

        List<CheckedItem> eductionCheckedItems = new ArrayList<>();
        eductionCheckedItems.add(new CheckedItem("博士", false));
        eductionCheckedItems.add(new CheckedItem("硕士", true));
        eductionCheckedItems.add(new CheckedItem("本科", false));
        eductionCheckedItems.add(new CheckedItem("大专", false));
        eductionCheckedItems.add(new CheckedItem("中专", false));
        eductionCheckedItems.add(new CheckedItem("高中", false));
        eductionCheckedItems.add(new CheckedItem("初中及以下", false));

        map.put("educationCheckItems", eductionCheckedItems);


        List<CheckedItem> badCredibilityCheckItems = new ArrayList<>();
        badCredibilityCheckItems.add(new CheckedItem("中国人民银行征信中心", false));
        badCredibilityCheckItems.add(new CheckedItem("最高人民法院失信被执行人名单", false));
        badCredibilityCheckItems.add(new CheckedItem("工商行政管理机构", false));
        badCredibilityCheckItems.add(new CheckedItem("税务管理机构", false));
        badCredibilityCheckItems.add(new CheckedItem("监管机构、自律组织", false));
        badCredibilityCheckItems.add(new CheckedItem("投资者在证券经营机构的失信记录", false));
        badCredibilityCheckItems.add(new CheckedItem("其他", false));

        map.put("badCredibilityCheckItems", badCredibilityCheckItems);

        List<CheckedItem> badCredibilityCheckOtherItems = new ArrayList<>();
        badCredibilityCheckOtherItems.add(new CheckedItem("无", false));
        badCredibilityCheckOtherItems.add(new CheckedItem("有", false));
        map.put("badCredibilityCheckOtherItems", badCredibilityCheckOtherItems);

        map.put("badCredibilityCheckOtherText", "美利坚合众国华尔街证监会");

        map.put("idType", "身份证");
        map.put("idNo", "610322198606212281");

        map.put("idPeriod", "20年");
        map.put("phoneNo", "010-12345678");
        map.put("mobileNo", "13875888888");
        map.put("addr", "湖南省长沙市天心区劳动西路恒力卡瑞尔大厦");

        map.put("zipCode", "410007");
        map.put("email", "yyi.mailer@gmail.com");


        String fileName = UUID.randomUUID().toString();
        byte[] buff = pdfGeneratorUtil.createPdf("investorBasicInformation", map, "G:\\" + fileName);
        return new DownloadingView("bbb测试中文aaa.pdf",
                "application/pdf", buff);

    }


}
