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
import com.techacademy.entity.Report;
import com.techacademy.entity.Employee;
import com.techacademy.service.EmployeeService;
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

        // 日報一覧画面
        @GetMapping
        public String list(Model model) {

            model.addAttribute("listSize", reportService.findAllReports().size());
            model.addAttribute("reportList", reportService.findAllReports());

            return "reports/list";
        }

        // 従業員詳細画面
        @GetMapping(value = "/{code}/")
        public String detail(@PathVariable String code, Model model) {

            model.addAttribute("report", reportService.findByCode(code));
            return "reports/detail";
        }

        // 従業員新規登録画面
        @GetMapping(value = "/add")
        public String create(@ModelAttribute Report report) {

            return "reports/new";
        }

        // 従業員新規登録処理
        //@PostMapping(value = "/add")
        //public String add(@Validated Report report, BindingResult res, Model model) {
            // パスワード空白チェック
            /*
             * エンティティ側の入力チェックでも実装は行えるが、更新の方でパスワードが空白でもチェックエラーを出さずに
             * 更新出来る仕様となっているため上記を考慮した場合に別でエラーメッセージを出す方法が簡単だと判断
             */
//            if ("".equals(report.getPassword())) {
            //                // パスワードが空白だった場合
            //  model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.BLANK_ERROR),
            //          ErrorMessage.getErrorValue(ErrorKinds.BLANK_ERROR));
            //
            //  return create(report);
            //
            //}

            // 入力チェック
            //if (res.hasErrors()) {
            //    return create(report);
            //}

            // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
            // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
            //try {
            //    ErrorKinds result = reportService.save(report);
            //
            //     if (ErrorMessage.contains(result)) {
            //        model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            //        return create(report);
            //    }
            //
            //} catch (DataIntegrityViolationException e) {
            //    model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
            //            ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            //    return create(report);
            // }
            //
            //return "redirect:/reports";
            //}


        // 従業員更新画面を表示する
        @GetMapping(value = "/{code}/update")
        public String update(@PathVariable String code, Model model) {
            Report report = reportService.findByCode(code);
            if (report == null) {
                // 従業員が見つからない場合の処理
               return "redirect:/reports";
            }
            model.addAttribute("report", report);
            return "reports/update";
        }


     // 従業員の更新処理
        @PostMapping(value = "/{code}/update")
        public String update(@PathVariable String code, @Validated @ModelAttribute("report") Report updatedReport, BindingResult result, Model model) {
            if (result.hasErrors()) {
                // エラーがある場合
                return "reports/update";
            }

            // 従業員の更新処理をサービスに
            ErrorKinds updateResult = reportService.updateReport(code, updatedReport);

            if (updateResult == ErrorKinds.NOT_FOUND_ERROR) {
                // 従業員が見つからない場合の処理
                return "redirect:/reports";
            } else if (updateResult != ErrorKinds.SUCCESS) {
                // 更新に失敗した場合の処理
                model.addAttribute(ErrorMessage.getErrorName(updateResult), ErrorMessage.getErrorValue(updateResult));
                return "reports/update";
            }

            return "redirect:/reports";
        }






        // 従業員削除処理
        @PostMapping(value = "/{code}/delete")
        public String delete(@PathVariable String code, @AuthenticationPrincipal UserDetail userDetail, Model model) {

            ErrorKinds result = reportService.delete(code, userDetail);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                model.addAttribute("report", reportService.findByCode(code));
                return detail(code, model);
            }

            return "redirect:/employees";
        }

    }



