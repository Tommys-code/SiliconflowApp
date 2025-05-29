import com.google.gson.Gson
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class ExcelToJsonTask : DefaultTask() {

    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun convert() {
        val excelFile = inputFile.get().asFile
        val jsonFile = outputFile.get().asFile

        WorkbookFactory.create(excelFile).use { workbook ->
            val data = workbook.getSheetAt(0).let { sheet ->
                val headers = sheet.getRow(0).map { it.stringCellValue }
                val headerIndexMap =
                    headers.withIndex().associate { (index, value) -> value to index }

                (1 until sheet.physicalNumberOfRows).mapNotNull { rowNum ->
                    sheet.getRow(rowNum)?.let { row ->
                        headers.associateWith { header ->
                            val cell = row.getCell(headerIndexMap[header] ?: -1)
                            cell?.let { getCellValue(it) }
                        }
                    }
                }
            }
            jsonFile.writeText(Gson().toJson(data))
        }
        println("Excel 文件已生成！${jsonFile.absolutePath}")
    }

    private fun getCellValue(cell: Cell): Any? {
        return when (cell.cellType) {
            CellType.NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    cell.dateCellValue
                } else {
                    cell.numericCellValue
                }
            }

            CellType.BOOLEAN -> cell.booleanCellValue
            CellType.STRING -> cell.stringCellValue.trim().takeIf { it.isNotEmpty() }
            CellType.FORMULA -> {
                val evaluator = cell.sheet.workbook.creationHelper.createFormulaEvaluator()
                val value = evaluator.evaluate(cell)
                when (value.cellType) {
                    CellType.NUMERIC -> value.numberValue
                    CellType.BOOLEAN -> value.booleanValue
                    CellType.STRING -> value.stringValue?.trim()?.takeIf { it.isNotEmpty() }
                    else -> null
                }
            }

            CellType.BLANK -> null
            else -> cell.toString().trim().takeIf { it.isNotEmpty() }
        }
    }
}