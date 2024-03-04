package com.techacademy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    // 特定のEmployeeに関連するReportのリストを取得するメソッド
    List<Report> findByEmployee(Employee employee);
}