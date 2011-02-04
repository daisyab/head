package org.mifos.ui.core.controller;

import org.mifos.application.admin.servicefacade.AppliedUpgradesServiceFacade;
import org.mifos.db.upgrade.ChangeSetInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller

public class AppliedUpgradesController {

    @Autowired
    protected AppliedUpgradesServiceFacade appliedUpgradesServiceFacade;

    @RequestMapping("/appliedUpgrades.ftl")
    public String viewAppliedUpgrades(ModelMap model, HttpServletRequest httpServletRequest) {
        List<ChangeSetInfo> list = appliedUpgradesServiceFacade.getAppliedUpgrades();

        model.put("upgrades", list);

        return "appliedUpgrades";
    }

}
