package edu.buffalo.cse562.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import edu.buffalo.cse562.exception.fileException;
import edu.buffalo.cse562.logger.logManager;

public class fileReader {

	File file;
	List<String> contents;
	logManager lg;

	public fileReader(String fileName) {
		lg = new logManager();

		if (new File(fileName).exists()) {
			this.file = new File(fileName);
		} else {
			String basePath = new File("").getAbsolutePath();
			this.file = new File(basePath + File.separator + fileName);
		}
		// lg.logger.log(Level.INFO, basePath);
		lg.logger.log(Level.INFO, fileName);
	}

	public fileReader(File fileName) {
		this.file = fileName;
	}

	public List<String> readContents() {
		contents = new ArrayList<String>();
		try {
			String line = null;
			Boolean normalQuery = false;
			Boolean tchpQuery = false;
			String lineAppend = "";
			BufferedReader buf = new BufferedReader(new FileReader(file));
			while ((line = buf.readLine()) != null) {

				if (line.length() == 0 && line.isEmpty())
					continue;
				else if (line.startsWith("--"))
					continue;
				else {
					if (line.contains("--"))
						line = line.substring(0, line.indexOf("--"));
					if ((line.endsWith(")") || line.endsWith(";") || normalQuery)
							&& !tchpQuery) {
						contents.add(line);
						normalQuery = true;
						continue;
					}
					if (!line.trim().endsWith(";")) {
						tchpQuery = true;
						lineAppend += line + " ";
					} else {
						lineAppend += line + " ";
						contents.add(lineAppend);
						lineAppend = "";
					}

				}

			}

			buf.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contents;

	}

}
