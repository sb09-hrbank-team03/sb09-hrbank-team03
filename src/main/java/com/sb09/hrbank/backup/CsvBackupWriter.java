package com.sb09.hrbank.backup;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Component;

@Component
public class CsvBackupWriter {

  public Path createCsv() throws IOException {

    String fileName = "backup-" + System.currentTimeMillis() + ".csv";

    Path path = Paths.get("backup", fileName);

    Files.createDirectories(path.getParent());

    return path;
  }

  public void writeHeader(BufferedWriter writer) throws IOException {
    writer.write("employeeNumber,name,email,department,title,status,hiredAt");
    writer.newLine();
  }

}
