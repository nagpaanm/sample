package Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Database.DatabaseJDBCAccess;

@Controller
public class HomeController {
	
	private final String DATABASE_PATH = "jdbc:postgresql://localhost:5432/";
	private final String DATABASE_NAME = "mydb";
	private final String DATABASE_USERNAME = "postgres";
	private final String DATABASE_PASSWORD = "root";
	
	private DatabaseJDBCAccess jdbc = 
			new DatabaseJDBCAccess(
					DATABASE_PATH,
					DATABASE_NAME,
					DATABASE_USERNAME,
					DATABASE_PASSWORD);
	
	
	@GetMapping("/")
	public String home(Model model) {
		ArrayList<String> tables = jdbc.getTableNames();
		model.addAttribute("tables", tables);
		return "index.html";
	}
	
	@GetMapping("/retrieve")
	public String retrieve(Model model, @RequestParam String tables) {
		ArrayList<String> columns = jdbc.getColumnNames(tables);
		model.addAttribute("columns", columns);
		model.addAttribute("table", tables);
		return "retrieve.html";
	}
	
	@GetMapping("/fetch")
	public String fetch(Model model, 
						@RequestParam List<String> columns,
						@RequestParam String table) {
		ArrayList<String> columnsArray = new ArrayList<String>();
		for (String column : columns) {
			columnsArray.add(column);
		}
		ArrayList<ArrayList<String>> results = jdbc.getResults(table, 
													columnsArray, 
													"", 
													-1);
		model.addAttribute("results", results);
		model.addAttribute("columns", columnsArray);
		model.addAttribute("table", table);
		return "fetch.html";
	}
	
}
