package com.techacademy.controller;

import java.util.List;

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


    // ■■ ROLEによるフィルタ 日報一覧画面の表示
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {
        // ログインユーザーの従業員情報を取得
        Employee loggedInUser = userDetail.getEmployee();
        // ユーザーの権限に応じて表示する日報を取得
        List<Report> reportList = reportService.getReportsForUser(loggedInUser);
        // モデルに日報リストを追加
        model.addAttribute("reportList", reportList);
        model.addAttribute("listSize", reportList.size());
        return "reports/list";
    }



//        // ■■ 合格時の日報 一覧画面の表示
//        @GetMapping
//        public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {
//            // ログインユーザーの取得
//            Employee loggedInUser = userDetail.getEmployee();
//            List<Report> reportList;
//
//            // ADMIN権限を持つユーザーは全ての日報を表示、GENERALは自分の日報のみ表示
//            if (userDetail.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
//                reportList = reportService.findAll();
//            } else {
//                reportList = reportService.findByEmployee(loggedInUser);
//            }
//
//            model.addAttribute("listSize", reportList.size());
//            model.addAttribute("reportList", reportList);
//            return "reports/list";
//        }



//        // ■■ 初期の日報一覧画面の表示 全リストを表示
//        @GetMapping
//        public String list(Model model) {
//            model.addAttribute("listSize", reportService.findAll().size());
//            model.addAttribute("reportList", reportService.findAll());
//            return "reports/list";
//        }


    // ■■ 日報 新規登録画面の表示
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        model.addAttribute("loginUser", userDetail.getEmployee());

        return "reports/new";
    }


////         ■■最初の日報新規登録処理
//        @PostMapping(value = "/add")
//        public String add(@Validated @ModelAttribute("report") Report report, BindingResult Res, @AuthenticationPrincipal UserDetail userDetail, Model model) {
//
//            if (Res.hasErrors()) {
//                // バリデーションエラーがある場合、フォームを再表示
//                model.addAttribute("loginUser", userDetail.getEmployee());
//                return "reports/new";
//            }
//
//            // 以下の処理はバリデーションが成功した場合のみ実行される
//            Employee loginUser = userDetail.getEmployee();
//            report.setEmployee(loginUser);
//
//
//
//            ErrorKinds result = reportService.save(report, loginUser);
//            if (result != ErrorKinds.SUCCESS) {
//                model.addAttribute("errorMessage", ErrorMessage.getErrorValue(result));
//                return "reports/new";
//            }
//
//            return "redirect:/reports";
//        }



    // ■■ 日報新規登録処理 日付重複サービスメソッド呼び出し
    @PostMapping(value = "/add")
    public String add(@Validated @ModelAttribute("report") Report report, BindingResult Res,
            @AuthenticationPrincipal UserDetail userDetail, Model model) {

        if (Res.hasErrors()) {
            // バリデーションエラーがある場合、フォームを再表示
            model.addAttribute("loginUser", userDetail.getEmployee());
            return "reports/new";
        }

        // 以下の処理はバリデーションが成功した場合のみ実行される
        Employee loginUser = userDetail.getEmployee();
        report.setEmployee(loginUser);

        // 日報の登録日時の重複チェックを実行
        ErrorKinds duplicateCheckResult = reportService.checkDuplicateReportDate(loginUser, report.getReportDate());
        if (duplicateCheckResult == ErrorKinds.DUPLICATE_DATE_ERROR) {
            model.addAttribute("errorMessage", "既に登録されている日付でーーーす。");
            return "reports/new";
        }

        // 重複がない場合、日報を保存
        ErrorKinds result = reportService.save(report, loginUser);
        if (result != ErrorKinds.SUCCESS) {
            model.addAttribute("errorMessage", ErrorMessage.getErrorValue(result));
            return "reports/new";
        }

        return "redirect:/reports";
    }



    // ■■日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {

        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }



    // ■■日報更新画面を表示する
    @GetMapping(value = "/{id}/update")
    public String updateList(@PathVariable Integer id, Model model) {
        Report report = reportService.findById(id);
        if (report == null) {
            // 日報が見つからない場合の処理
            model.addAttribute("report", report);
            return "redirect:/reports";
        }
        model.addAttribute("report", report);
        return "reports/update";
    }



    // ■■日報の更新処理
    @PostMapping(value = "/{id}/update")
    public String updateReport(@PathVariable Integer id, @Validated @ModelAttribute("report") Report updatedReport,
            BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        if (res.hasErrors()) {
            // バリデーションエラーがある場合、フォームを再表示
            model.addAttribute("loginUser", userDetail.getEmployee());
            model.addAttribute("report", updatedReport); // 現在の入力値を保持
            return "reports/update";
        }

        // 日報の更新時の重複チェック
        ErrorKinds duplicateCheckResult = reportService.checkDuplicateReportDateForUpdate(id, userDetail.getEmployee(),
                updatedReport.getReportDate());
        if (duplicateCheckResult == ErrorKinds.DUPLICATE_DATE_ERROR) {
            model.addAttribute("errorMessage", "既に登録されている日付でーーす。");
            model.addAttribute("report", updatedReport);
            return "reports/update";
        }

        // 日報の更新処理をサービスに
        ErrorKinds updateResult = reportService.updateReport(id, updatedReport);
        if (updateResult != ErrorKinds.SUCCESS) {
            // 更新に失敗した場合の処理
            model.addAttribute("errorMessage", ErrorMessage.getErrorValue(updateResult));
            model.addAttribute("report", updatedReport); // エラー時も現在の入力値を保持
            return "reports/update";
        }

        // 更新が成功した場合、日報一覧ページにリダイレクト
        return "redirect:/reports";
    }



    // ■■日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = reportService.delete(id, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findById(id));
            return detail(id, model);
        }

        return "redirect:/reports";
    }

}
