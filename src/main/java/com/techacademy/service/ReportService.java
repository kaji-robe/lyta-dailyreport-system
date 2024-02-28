package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;

import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;

import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ReportService(ReportRepository reportRepository, PasswordEncoder passwordEncoder) {
        this.reportRepository = reportRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // ■■ 日報一覧表示処理 ■■
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // ■■ 日報1件を検索 ■■
    public Report findByCode(String code) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(code);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }





    //■■■ 更新処理 ■■■
    @Transactional
    public ErrorKinds updateReport(String code, Report updatedReport) {
        // 従業員を検索
        Report report = findByCode(code);
        if (report == null) {
            return ErrorKinds.NOT_FOUND_ERROR;
        }

        // 名前と権限の更新
        report.setName(updatedReport.getName());
        report.setRole(updatedReport.getRole());

        // パスワードが空白でない場合のみ更新
        if (!"".equals(updatedReport.getPassword())) {
            // パスワードチェック
            ErrorKinds passwordCheckResult = reportPasswordCheck(updatedReport);
            if (passwordCheckResult != ErrorKinds.CHECK_OK) {
                return passwordCheckResult; // パスワードチェックエラー
            }
            report.setPassword(updatedReport.getPassword());
        }

        // 更新日時の更新
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);

        // 更新処理
        reportRepository.save(report);

        return ErrorKinds.SUCCESS;
    }



    // ■■ 従業員削除 ■■
    @Transactional
    public ErrorKinds delete(String code, UserDetail userDetail) {

        // 自分を削除しようとした場合はエラーメッセージを表示
        if (code.equals(userDetail.getEmployee().getCode())) {
            return ErrorKinds.LOGINCHECK_ERROR;
        }
        Report report = findByCode(code);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }


    // ■■ 従業員パスワードチェック ■■
    public ErrorKinds reportPasswordCheck(Report report) {


        // 従業員パスワードの半角英数字チェック処理
        if (isHalfSizeCheckError(report)) {
            return ErrorKinds.HALFSIZE_ERROR;
        }

        // 従業員パスワードの8文字～16文字チェック処理
        if (isOutOfRangePassword(report)) {
            return ErrorKinds.RANGECHECK_ERROR;
        }

        // パスワードが空白の場合はエラーチェックしない
        //if ("".equals(employee.getPassword())) {
        //    return ErrorKinds.SUCCESS;
        //}

        report.setPassword(passwordEncoder.encode(report.getPassword()));

        return ErrorKinds.CHECK_OK;
    }

    // ■■ 従業員パスワードの半角英数字チェック処理 ■■
    private boolean isHalfSizeCheckError(Report report) {

        // 半角英数字チェック
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher matcher = pattern.matcher(report.getPassword());
        return !matcher.matches();
    }

    // ■■ 従業員パスワードの8文字～16文字チェック処理 ■■
    public boolean isOutOfRangePassword(Report report) {

        // 桁数チェック
        int passwordLength = report.getPassword().length();
        return passwordLength < 8 || 16 < passwordLength;
    }

}