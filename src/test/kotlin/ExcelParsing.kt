import net.prismclient.document.type.database.Excel
import net.prismclient.util.localResource

fun main() {
    val spreadsheet = Excel("spr.xlsx".localResource)

    spreadsheet.retrieveSheet { sheet ->
        for (i in 0 ..< sheet.getRow(0).lastCellNum) {
            println(sheet.getRow(0).getCell(i))
        }
    }
}