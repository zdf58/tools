package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.MetaObject;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.resolver.ExcelWriterResolver;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * XLS处理器
 *
 * @author Gjing
 **/
class ExcelWriteXlsResolver implements ExcelWriterResolver, Closeable {
    private HSSFWorkbook workbook;
    private OutputStream outputStream;
    private ExcelHelper excelHelper;

    @Override
    public void write(List<?> data, Workbook workbook, String sheetName, List<Field> headFieldList, MetaObject metaObject, boolean changed, Excel excel) {
        this.workbook = (HSSFWorkbook) workbook;
        HSSFSheet sheet = this.workbook.getSheet(sheetName);
        if (sheet == null) {
            changed = true;
            sheet = this.workbook.createSheet(sheetName);
        }
        if (excelHelper == null) {
            this.excelHelper = new ExcelHelper(this.workbook);
        }
        int rowIndex = this.excelHelper.setBigTitle(headFieldList, metaObject, sheet);
        rowIndex = this.excelHelper.setHead(data, headFieldList, sheet, changed, rowIndex, metaObject, excel);
        if (data == null || data.isEmpty()) {
            return;
        }
        this.excelHelper.setValue(data, headFieldList, sheet, rowIndex);
    }

    @Override
    public void flush(HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.ms-excel");
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        try {
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            } else {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            }
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xls");
            this.outputStream = response.getOutputStream();
            this.workbook.write(this.outputStream);
        } catch (IOException e) {
            throw new ExcelResolverException("Excel cache data refresh failure");
        }
    }

    @Override
    public void close() throws IOException {
        if (this.outputStream != null) {
            this.outputStream.flush();
            this.outputStream.close();
        }
        if (this.workbook != null) {
            this.workbook.close();
        }
    }
}
