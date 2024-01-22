import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.List;

public class TablePrintable implements Printable {
    private final List<String[]> table;

    public TablePrintable(List<String[]> table) {
        this.table = table;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        if (pageIndex >= table.size()) {
            return Printable.NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        String[] row = table.get(pageIndex);
        for (int i = 0; i < row.length; i++) {
            String cellValue = row[i];
            g2d.drawString(cellValue, i * 50, 20); // Ajuste conforme necessário
        }

        return Printable.PAGE_EXISTS;
    }
}
