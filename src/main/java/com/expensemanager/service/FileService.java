package com.expensemanager.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expensemanager.dao.Expense;
import com.expensemanager.repository.ExpenseRepository;

@Service
public class FileService {

	@Autowired
	ExpenseRepository expenseRepository;
	
	public boolean processCSVFile(File file){
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        try {
        	List<Expense> expenses = new ArrayList<>();
            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] expenseString = line.split(cvsSplitBy);
                String amount = expenseString[0].replace("\"", "");
                String date = expenseString[3].replace("\"", "");
                Expense expense = new Expense();
                expense.setAmount(Double.parseDouble(amount));
                expense.setDate(new Timestamp(new SimpleDateFormat("dd/MM/yyyy").parse(date).getTime()));
                expense.setNarration(expenseString.length > 4 ? expenseString[4].replace("\"", "") : "");
                expenses.add(expense);
            }
            
            expenseRepository.saveAll(expenses);
            
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
}
