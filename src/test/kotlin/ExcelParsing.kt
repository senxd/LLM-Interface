import net.prismclient.document.type.database.excel.Excel
import net.prismclient.util.localResource

fun main() {
    val spreadsheet = Excel("spr.xlsx".localResource)
}