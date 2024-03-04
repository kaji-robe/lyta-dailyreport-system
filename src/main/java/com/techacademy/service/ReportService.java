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
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import com.techacademy.repository.EmployeeRepository;


import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository, PasswordEncoder passwordEncoder) {
        this.reportRepository = reportRepository;
    }



//   // ■■ 既存の日報一覧表示処理 分別なしですべて表示
//    public List<Report> findAll() {
//        return reportRepository.findAll();
//    }


    // ■■論理削除されていない全ての日報を取得
    public List<Report> findAll() {
        return reportRepository.findByDeleteFlgFalse();
    }

    // ■■特定の従業員に関連する日報を取得するメソッド
    public List<Report> findByEmployee(Employee employee) {
        //return reportRepository.findByEmployee(employee);
        return reportRepository.findByEmployeeAndDeleteFlgFalse(employee);
    }

    // ■■ 日報 1件を検索 ■■
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }



    // ■■ 日報保存
    @Transactional
    public ErrorKinds save(Report report) {

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);

        return ErrorKinds.SUCCESS;
    }



    //■■　日報更新処理
    @Transactional
    public ErrorKinds updateReport(Integer id, Report updatedReport) {
        // 日報を検索
        Report report = findById(id);
        if (report == null) {
            return ErrorKinds.NOT_FOUND_ERROR;
        }

        // 名前と権限の更新
        ////employee.setName(updatedEmployee.getName());
        //employee.setRole(updatedEmployee.getRole());
        // パスワードが空白でない場合のみ更新
        //if (!"".equals(updatedEmployee.getPassword())) {
        //    // パスワードチェック
        //    ErrorKinds passwordCheckResult = employeePasswordCheck(updatedEmployee);
        //    if (passwordCheckResult != ErrorKinds.CHECK_OK) {
        //        return passwordCheckResult; // パスワードチェックエラー
        //    }
        //    employee.setPassword(updatedEmployee.getPassword());
        // }


        // タイトル、内容、時間の更新
        report.setTitle(updatedReport.getTitle());
        report.setContent(updatedReport.getContent());
        report.setReportDate(updatedReport.getReportDate());
        report.setUpdatedAt(LocalDateTime.now());

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;

    }


    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer id, UserDetail userDetail) {
        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }




}