package Reportes;

import Entidades.DetalleNomina;
import Entidades.Empleado;
import Entidades.Nomina;
import Excepciones.ReporteException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Genera el PDF de la colilla de pago de un empleado (Etapa 5): datos
 * personales, período, y desglose completo de salario y deducciones.
 */
public class ReporteEmpleadoPDF implements IGeneradorReporte<DatosColilla> {

    @Override
    public File generar(DatosColilla datos, String rutaDestino) throws ReporteException {
        Empleado empleado = datos.getEmpleado();
        Nomina nomina = datos.getNomina();

        Document documento = new Document(PageSize.LETTER, 40, 40, 50, 50);
        try (FileOutputStream salida = new FileOutputStream(rutaDestino)) {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font fuenteSubtitulo = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
            Font fuenteNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

            Paragraph titulo = new Paragraph("Colilla de Pago", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            documento.add(new Paragraph(" "));

            documento.add(new Paragraph("Empleado: " + empleado.getNombreCompleto(), fuenteSubtitulo));
            documento.add(new Paragraph("Cédula: " + empleado.getCedula(), fuenteNormal));
            documento.add(new Paragraph("Puesto: " + empleado.getPuesto(), fuenteNormal));
            documento.add(new Paragraph("Período: " + nomina.getPeriodo(), fuenteNormal));
            documento.add(new Paragraph("Fecha de generación: " + nomina.getFechaGeneracion(), fuenteNormal));
            documento.add(new Paragraph(" "));

            PdfPTable tablaDeducciones = new PdfPTable(2);
            tablaDeducciones.setWidthPercentage(100);
            agregarFilaEncabezado(tablaDeducciones, "Deducción", "Monto (₡)");
            for (DetalleNomina d : datos.getDetalle()) {
                if (d.getTipo() == DetalleNomina.TipoRubro.DEDUCCION_TRABAJADOR) {
                    agregarFila(tablaDeducciones, d.getConcepto(), formatoMonto(d.getMonto()));
                }
            }
            documento.add(tablaDeducciones);
            documento.add(new Paragraph(" "));

            PdfPTable tablaResumen = new PdfPTable(2);
            tablaResumen.setWidthPercentage(100);
            agregarFila(tablaResumen, "Salario bruto", formatoMonto(nomina.getSalarioBruto()));
            agregarFila(tablaResumen, "Total deducciones", formatoMonto(nomina.getTotalDeduccionesTrabajador()));
            agregarFila(tablaResumen, "Impuesto sobre la renta", formatoMonto(nomina.getImpuestoRenta()));
            agregarFila(tablaResumen, "Salario neto", formatoMonto(nomina.getSalarioNeto()));
            documento.add(tablaResumen);

            documento.close();
        } catch (DocumentException | IOException e) {
            throw new ReporteException("No se pudo generar la colilla de pago en " + rutaDestino, e);
        }

        return new File(rutaDestino);
    }

    private void agregarFilaEncabezado(PdfPTable tabla, String col1, String col2) {
        Font fuente = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        tabla.addCell(new PdfPCell(new Phrase(col1, fuente)));
        tabla.addCell(new PdfPCell(new Phrase(col2, fuente)));
    }

    private void agregarFila(PdfPTable tabla, String col1, String col2) {
        tabla.addCell(col1);
        tabla.addCell(col2);
    }

    private String formatoMonto(double monto) {
        return String.format("%,.2f", monto);
    }
}
