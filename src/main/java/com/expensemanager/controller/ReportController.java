package com.expensemanager.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.expensemanager.service.FileService;
import com.expensemanager.service.ReportService;

@Controller
public class ReportController {

	@Autowired
	ReportService reportService;
	
	@Autowired
	FileService fileService;

	@RequestMapping(value = "/report", method = RequestMethod.GET)
	public String showReportForm(ModelMap model) {
		return "report";
	}

	@RequestMapping(value = "/report", method = RequestMethod.POST)
	public ResponseEntity<byte[]> report(ModelMap model, @RequestParam String fromDate, @RequestParam String toDate) {
		byte[] contents = null;
		try {
			contents = Files.readAllBytes(reportService.getReport(fromDate, toDate).toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		// Here you have to set the actual filename of your pdf
		String filename = "report.pdf";
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
		return response;
	}

	@PostMapping("/upload")
	public ModelAndView upload(@RequestParam("file") MultipartFile file, HttpSession session) {
		String path = session.getServletContext().getRealPath("/")+"WEB-INF/files/";
		//String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		String filename = file.getOriginalFilename()+".csv";

		System.out.println(path + " " + filename);
		try {
			byte barr[] = file.getBytes();

			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(path + "/" + filename));
			bout.write(barr);
			bout.flush();
			bout.close();
			
			File csvFile = new File(path + "/" + filename);
			
			fileService.processCSVFile(csvFile);
			
			//csvFile.delete();

		} catch (Exception e) {
			System.out.println(e);
		}
		return new ModelAndView("report", "filename", path + "/" + filename);
	}

}
