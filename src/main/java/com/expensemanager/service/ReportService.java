package com.expensemanager.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expensemanager.report.ReportGenerator;

@Service
public class ReportService {
	
	@Autowired
	ReportGenerator reportGenerator;
	
	public File getReport(String fromDate, String toDate){
		return reportGenerator.generateReport(fromDate, toDate);
	}

}
