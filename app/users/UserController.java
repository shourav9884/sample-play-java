package users;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import play.mvc.*;
import play.libs.concurrent.ClassLoaderExecutionContext;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import play.libs.Json;

@With(UserAction.class)
public class UserController extends Controller {
    private ClassLoaderExecutionContext ec;
    private UserResourceHandler handler;

    @Inject
    public UserController(ClassLoaderExecutionContext ec, UserResourceHandler handler) {
        this.ec = ec;
        this.handler = handler;
    }

    public CompletionStage<Result> list(Http.Request request) {
        return handler.find(request).thenApplyAsync(posts -> {
            final List<UserData> postList = posts.collect(Collectors.toList());
            return ok(Json.toJson(postList));
        }, ec.current());
    }

    public CompletionStage<Result> create(Http.Request request) {
        JsonNode json = request.body().asJson();
        final UserData resource = Json.fromJson(json, UserData.class);
        return handler.create(request, resource).thenApplyAsync(savedResource -> {
            return created(Json.toJson(savedResource));
        }, ec.current());
    }

    public CompletionStage<Result> export(Http.Request request) {
        return handler.find(request).thenApplyAsync(users -> {
            final List<UserData> userList = users.collect(Collectors.toList());
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Users");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Email");
            headerRow.createCell(2).setCellValue("Name");
            // Add more columns as needed

            // Fill data rows
            int rowNum = 1;
            for (UserData user : userList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getEmailAddress());
                row.createCell(2).setCellValue(user.getName());
                // Add more columns as needed
            }

            // Convert workbook to byte array
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                byte[] excelBytes = outputStream.toByteArray();
                return ok(excelBytes).as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        .withHeader("Content-Disposition", "attachment; filename=users.xlsx");
            } catch (IOException e) {
                e.printStackTrace();
                return internalServerError("Error exporting users to Excel");
            }
        }, ec.current());
    }
}
