package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

    @Controller
    @RequestMapping("reports")
    public class ReportController {

        private final ReportService reportService;

        @Autowired
        public ReportController(ReportService reportService) {
            this.reportService = reportService;
        }

        // 日報　一覧画面の表示
        @GetMapping
        public String list(Model model) {

            model.addAttribute("listSize", reportService.findAllReports().size());
            model.addAttribute("reportList", reportService.findAllReports());
            return "reports/list";
        }



        // 日報　新規登録画面の表示
        @GetMapping(value = "/add")
        public String create(@ModelAttribute Report report) {
            return "reports/new";
        }



        // 日報　新規登録の処理
        @PostMapping(value = "/add")
        public String add(@Validated Report report, BindingResult res, Model model) {
            // 入力チェック
            if (res.hasErrors()) {
                return create(report);
            }
            return null;
        }


        // 日報 更新画面を表示する
        //@GetMapping(value = "/{code}/update")
        //public String update(@PathVariable Integer code, Model model) {
        //    Report report = reportService.findByCode(code);
        //    if (report == null) {
        //        // 従業員が見つからない場合の処理
        //       return "redirect:/reports";
        //    }
        //    model.addAttribute("report", report);
        //    return "reports/update";
        //}




    }


