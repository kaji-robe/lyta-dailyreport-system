package com.techacademy.controller;

import java.time.LocalDateTime;

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



    // 従業員更新画面を表示する
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


 // 従業員の更新処理
    @PostMapping(value = "/{code}/update")
    public String update(@PathVariable String code, @Validated @ModelAttribute("employee") Employee updatedEmployee, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // エラーがある場合
            return "employees/update";
        }

        // パスワードが空白でない場合のみ、エラーチェックを行う
        if (!"".equals(updatedEmployee.getPassword())) {
            ErrorKinds passwordCheckResult = employeeService.employeePasswordCheck(updatedEmployee);
            // パスワードチェックエラーがある場合
            if (ErrorKinds.CHECK_OK != passwordCheckResult) {
                model.addAttribute(ErrorMessage.getErrorName(passwordCheckResult), ErrorMessage.getErrorValue(passwordCheckResult));
                return "employees/update";
            }
        }

        Employee employee = employeeService.findByCode(code);

        //ユーザリスト一覧からの遷移なので不要だが一応。
        //if (employee == null) {
        //    // 従業員が見つからない場合の処理
        //    return "redirect:/employees";
        //}

        // パスワードが空白でない場合のみ更新
        if (!"".equals(updatedEmployee.getPassword())) {
            employee.setPassword(updatedEmployee.getPassword());
        }

        // 名前と権限の更新
        employee.setName(updatedEmployee.getName());
        employee.setRole(updatedEmployee.getRole());


        //更新日時の実装はサービス側が適切
        LocalDateTime now = LocalDateTime.now();
        employee.setUpdatedAt(now);

        // 更新処理
        employeeService.save(employee);

        return "redirect:/employees";
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