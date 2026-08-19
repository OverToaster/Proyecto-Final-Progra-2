package Reportes;

import Entidades.Empleado;
import Entidades.Nomina;
import Excepciones.ReporteException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Genera el PDF de resumen de planilla para el patrono (Etapa 5): lista
 * de empleados de la corrida de nómina de un período y el total de
 * aportes patronales.
 */
public class ReportePatronoPDF implements IGeneradorReporte<List<DatosColilla>> {

    @Override
    public File generar(List<DatosColilla> datos, String rutaDestino) throws ReporteException {
        Document documento = new Document(PageSize.LETTER, 40, 40, 50, 50);
        try (FileOutputStream salida = new FileOutputStream(rutaDestino)) {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font fuenteNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

            String periodo = datos.isEmpty() ? "-" : datos.get(0).getNomina().getPeriodo();

            Paragraph titulo = new Paragraph("Resumen de Planilla - " + periodo, fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.addCell("Empleado");
            tabla.addCell("Salario bruto");
            tabla.addCell("Salario neto");
            tabla.addCell("Aportes patronales");

            double totalBruto = 0;
            double totalNeto = 0;
            double totalAportes = 0;

            for (DatosColilla dc : datos) {
                Empleado emp = dc.getEmpleado();
                Nomina n = dc.getNomina();
                tabla.addCell(emp.getNombreCompleto());
                tabla.addCell(formatoMonto(n.getSalarioBruto()));
                tabla.addCell(formatoMonto(n.getSalarioNeto()));
                tabla.addCell(formatoMonto(n.getTotalAportesPatronales()));

                totalBruto += n.getSalarioBruto();
                totalNeto += n.getSalarioNeto();
                totalAportes += n.getTotalAportesPatronales();
            }

            documento.add(tabla);
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Total salario bruto: " + formatoMonto(totalBruto), fuenteNormal));
            documento.add(new Paragraph("Total salario neto: " + formatoMonto(totalNeto), fuenteNormal));
            documento.add(new Paragraph("Total aportes patronales: " + formatoMonto(totalAportes), fuenteNormal));

            documento.close();
        } catch (DocumentException | IOException e) {
            throw new ReporteException("No se pudo generar el resumen de planilla en " + rutaDestino, e);
        }

        return new File(rutaDestino);
    }

    private String formatoMonto(double monto) {
        return String.format("%,.2f", monto);
    }
}
