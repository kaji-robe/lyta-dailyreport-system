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

        // ■■ 日報　一覧画面の表示
        @GetMapping
        public String list(Model model) {
            model.addAttribute("listSize", reportService.findAll().size());
            model.addAttribute("reportList", reportService.findAll());
            return "reports/list";
        }


        // ■■ 日報　新規登録画面の表示
        @GetMapping(value = "/add")
        public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {
            model.addAttribute("loginUser", userDetail.getEmployee());
            return "reports/new";
        }



        // ■■日報 新規登録処理
        @PostMapping(value = "/add")
        //public String add(@Validated Report report, @AuthenticationPrincipal UserDetail userDetail, BindingResult res, Model model) {
        //各項目のエラーチェックは未実装なのでこれから。
        public String add(Report report, @AuthenticationPrincipal UserDetail userDetail, BindingResult res, Model model) {

            // 入力チェック
            if (res.hasErrors()) {
                return create(report, userDetail, model);
                }
            return "redirect:/reports";
            }



        // ■■日報詳細画面
        @GetMapping(value = "/{code}/")
        public String detail(@PathVariable Integer code, Model model) {

            model.addAttribute("report", reportService.findByCode(code));
            return "reports/detail";
        }



//        // 従業員更新画面を表示する
//        @GetMapping(value = "/{code}/update")
//        public String update(@PathVariable String code, Model model) {
//            Employee employee = employeeService.findByCode(code);
//            if (employee == null) {
//                // 従業員が見つからない場合の処理
//               return "redirect:/employees";
//            }
//            model.addAttribute("employee", employee);
//            return "employees/update";
//        }
//
//
//     // 従業員の更新処理
//        @PostMapping(value = "/{code}/update")
//        public String update(@PathVariable String code, @Validated @ModelAttribute("employee") Employee updatedEmployee, BindingResult result, Model model) {
//            if (result.hasErrors()) {
//                // エラーがある場合
//                return "employees/update";
//            }
//
//            // 従業員の更新処理をサービスに
//            ErrorKinds updateResult = employeeService.updateEmployee(code, updatedEmployee);
//
//            if (updateResult == ErrorKinds.NOT_FOUND_ERROR) {
//                // 従業員が見つからない場合の処理
//                return "redirect:/employees";
//            } else if (updateResult != ErrorKinds.SUCCESS) {
//                // 更新に失敗した場合の処理
//                model.addAttribute(ErrorMessage.getErrorName(updateResult), ErrorMessage.getErrorValue(updateResult));
//                return "employees/update";
//            }
//
//            return "redirect:/employees";
//        }
//
//
//
//
//
//
//        // 従業員削除処理
//        @PostMapping(value = "/{code}/delete")
//        public String delete(@PathVariable String code, @AuthenticationPrincipal UserDetail userDetail, Model model) {
//
//            ErrorKinds result = employeeService.delete(code, userDetail);
//
//            if (ErrorMessage.contains(result)) {
//                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
//                model.addAttribute("employee", employeeService.findByCode(code));
//                return detail(code, model);
//            }
//
//            return "redirect:/employees";
//        }
//
//    }






    }


