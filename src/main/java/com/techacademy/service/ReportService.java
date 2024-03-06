package com.techacademy.service;

import java.time.LocalDate;
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
        // return reportRepository.findByEmployee(employee);
        return reportRepository.findByEmployeeAndDeleteFlgFalse(employee);
    }



    // ■■権限に応じた日報一覧を取得するメソッド
    public List<Report> getReportsForUser(Employee employee) {
        // 従業員の権限に応じて条件分岐
        if (employee.getRole() == Employee.Role.ADMIN) {
            // ADMIN権限を持つユーザーは全ての日報を表示
            return reportRepository.findByDeleteFlgFalse();
        } else {
            // GENERAL権限を持つユーザーは自分の日報のみ表示
            return reportRepository.findByEmployeeAndDeleteFlgFalse(employee);
        }
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
    public ErrorKinds save(Report report, Employee loginUser) {
        report.setEmployee(loginUser);

        Optional<Report> existingReport = reportRepository.findByEmployeeAndReportDate(report.getEmployee(),
                report.getReportDate());
        if (existingReport.isPresent()) {
            // 同じ日付で既に日報が存在する場合はエラーを返す
            return ErrorKinds.DUPLICATE_DATE_ERROR;
        }

        report.setDeleteFlg(false); // 論理削除フラグをfalseに設定
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }



// // 次の日報保存処理
//  @Transactional
//  public ErrorKinds save(Report report, Employee employee) {
//      // 日付の重複チェック
//      Optional<Report> existingReport = reportRepository.findByEmployeeAndReportDate(employee, report.getReportDate());
//      if (existingReport.isPresent()) {
//          // 既に同じ日付で日報が存在する場合
//          return ErrorKinds.DUPLICATE_DATE_ERROR;
//      }
//
//      // 重複がない場合、日報を保存
//      report.setEmployee(employee); // ログイン中の従業員を設定
//      report.setDeleteFlg(false); // 削除フラグをfalseに設定
//
//      LocalDateTime now = LocalDateTime.now();
//      report.setCreatedAt(now); // 作成日時を設定
//      report.setUpdatedAt(now); // 更新日時を設定
//
//      reportRepository.save(report); // 日報を保存
//
//      return ErrorKinds.SUCCESS; // 成功した場合
//  }
//



//    // ■■ 最初の日報保存
//    @Transactional
//    public ErrorKinds save(Report report) {
//
//        report.setDeleteFlg(false);
//
//        LocalDateTime now = LocalDateTime.now();
//        report.setCreatedAt(now);
//        report.setUpdatedAt(now);
//
//        reportRepository.save(report);
//
//        return ErrorKinds.SUCCESS;
//    }



    // ■■ 日報更新処理
    @Transactional
    public ErrorKinds updateReport(Integer id, Report updatedReport) {
        // 日報を検索
        Report report = findById(id);
        if (report == null) {
            return ErrorKinds.NOT_FOUND_ERROR;
        }

        // 名前と権限の更新
        //// employee.setName(updatedEmployee.getName());
        // employee.setRole(updatedEmployee.getRole());
        // パスワードが空白でない場合のみ更新
        // if (!"".equals(updatedEmployee.getPassword())) {
        // // パスワードチェック
        // ErrorKinds passwordCheckResult = employeePasswordCheck(updatedEmployee);
        // if (passwordCheckResult != ErrorKinds.CHECK_OK) {
        // return passwordCheckResult; // パスワードチェックエラー
        // }
        // employee.setPassword(updatedEmployee.getPassword());
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



//    //■■　合格時の日報の新規登録日付の重複チェック
//    public ErrorKinds checkDuplicateReportDate(Employee employee, LocalDate reportDate) {
//        Optional<Report> existingReport = reportRepository.findByEmployeeAndReportDate(employee, reportDate);
//        if (existingReport.isPresent()) {
//            // 既に同じ日に日報が存在する場合はエラーを返す
//            return ErrorKinds.DUPLICATE_DATE_ERROR;
//        }
//        return ErrorKinds.SUCCESS;
//    }



    // ■■ 新規の日報の新規登録日付の重複チェック
    public ErrorKinds checkDuplicateReportDate(Employee employee, LocalDate reportDate) {
        Optional<Report> existingReport = reportRepository.findByEmployeeAndReportDate(employee, reportDate);
        if (existingReport.isPresent()) {
            // 既に同じ日に日報が存在する場合はエラーを返す
            return ErrorKinds.DUPLICATE_DATE_ERROR;
        }
        return ErrorKinds.SUCCESS;
    }



    // ■■ 日報の更新時の日付重複チェック
    public ErrorKinds checkDuplicateReportDateForUpdate(Integer reportId, Employee employee, LocalDate reportDate) {
        Optional<Report> existingReport = reportRepository.findByEmployeeAndReportDate(employee, reportDate);
        if (existingReport.isPresent() && !existingReport.get().getId().equals(reportId)) {
            // 他の日報で同じ日付が使用されている場合はエラーを返す
            return ErrorKinds.DUPLICATE_DATE_ERROR;
        }
        return ErrorKinds.SUCCESS;
    }

}