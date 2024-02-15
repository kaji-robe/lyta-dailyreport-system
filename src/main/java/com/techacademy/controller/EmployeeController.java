package com.techacademy.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.techacademy.service.EmployeeService;
import com.techacademy.service.UserDetail;

@Controller

@RequestMapping("employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // 従業員一覧画面
    @GetMapping
    public String list(Model model) {

        model.addAttribute("listSize", employeeService.findAll().size());
        model.addAttribute("employeeList", employeeService.findAll());

        return "employees/list";
    }

    // 従業員詳細画面
    @GetMapping(value = "/{code}/")
    public String detail(@PathVariable String code, Model model) {

        model.addAttribute("employee", employeeService.findByCode(code));
        return "employees/detail";
    }

    // 従業員新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Employee employee) {

        return "employees/new";
    }

    // 従業員新規登録処理

    @PostMapping(value = "/add")
    public String add(@Validated Employee employee, BindingResult res, Model model) {

        // パスワード空白チェック
        /*
         * エンティティ側の入力チェックでも実装は行えるが、更新の方でパスワードが空白でもチェックエラーを出さずに
         * 更新出来る仕様となっているため上記を考慮した場合に別でエラーメッセージを出す方法が簡単だと判断
         */
        if ("".equals(employee.getPassword())) {
            // パスワードが空白だった場合
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.BLANK_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.BLANK_ERROR));

            return create(employee);

        }

        // 入力チェック
        if (res.hasErrors()) {
            return create(employee);
        }

        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = employeeService.save(employee);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(employee);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(employee);
        }

        return "redirect:/employees";
    }


    // 従業員更新画面を表示
    @GetMapping(value = "/{code}/update")
    public String update(@PathVariable String code, Model model) {
        Employee employee = employeeService.findByCode(code);
        if (employee == null) {
            // 従業員が見つからない場合の処理
            return "redirect:/employees";
        }
        model.addAttribute("employee", employee);
        return "employees/update";
    }

    // 従業員更新処理
    @PostMapping(value = "/{code}/update")
    public String update(@PathVariable String code, @Validated Employee employee, BindingResult res, Model model) {
        // 入力チェック
        if (res.hasErrors()) {
            return "employees/update";
        }

        // パスワードが変更される場合のみエラーチェックを行う
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            // パスワードのエラーチェック
            if (isHalfSizeCheckError(employee.getPassword())) {
                model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.HALFSIZE_ERROR), ErrorMessage.getErrorValue(ErrorKinds.HALFSIZE_ERROR));
                return "employees/update";
            }

            if (isOutOfRangePassword(employee.getPassword())) {
                model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.RANGECHECK_ERROR), ErrorMessage.getErrorValue(ErrorKinds.RANGECHECK_ERROR));
                return "employees/update";
            }
        }

        // 従業員情報の更新処理
        try {
            // 従業員情報を取得
            Employee existingEmployee = employeeService.findByCode(code);
            if (existingEmployee == null) {
                // 従業員が見つからない場合の処理
                // エラーメッセージを設定して適切な処理を行う
                return "redirect:/employees";
            }

            // フォームからのデータで既存の従業員情報を更新
            existingEmployee.setName(employee.getName());
            if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
                existingEmployee.setPassword(employee.getPassword()); // パスワードを更新する場合のみ
            }

            // 従業員情報を保存
            employeeService.save(existingEmployee);

        } catch (DataIntegrityViolationException e) {
            // 従業員番号重複エラーの場合
            //model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
            //        ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return "employees/update";
        }

        // 更新処理が成功した場合
        return "redirect:/employees";
    }

    // 更新画面用の従業員パスワードの半角英数字チェック処理
    private boolean isHalfSizeCheckError(String password) {
        // 半角英数字チェック
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher matcher = pattern.matcher(password);
        return !matcher.matches();
    }

    // 更新画面用の従業員パスワードの8文字～16文字チェック処理
    private boolean isOutOfRangePassword(String password) {
        // 桁数チェック
        int passwordLength = password.length();
        return passwordLength < 8 || 16 < passwordLength;
    }

    // 従業員削除処理
    @PostMapping(value = "/{code}/delete")
    public String delete(@PathVariable String code, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = employeeService.delete(code, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("employee", employeeService.findByCode(code));
            return detail(code, model);
        }

        return "redirect:/employees";
    }

}
