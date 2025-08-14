package org.example.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.model.Etudiant;
import org.example.model.Montant;
import org.example.model.Payer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PDFService {
    private static final Logger logger = LoggerFactory.getLogger(PDFService.class);
    
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);
    
    public boolean genererRecu(Etudiant etudiant, List<Payer> paiements, Montant montantNiveau, 
                              Montant equipement, String cheminFichier) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(cheminFichier));
            document.open();
            
            // En-tête avec date
            Paragraph date = new Paragraph("Aujourd'hui le " + LocalDate.now().format(DATE_FORMATTER), NORMAL_FONT);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);
            document.add(new Paragraph("\n"));
            
            // Titre
            Paragraph titre = new Paragraph("REÇU DE PAIEMENT DE BOURSE", TITLE_FONT);
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);
            document.add(new Paragraph("\n"));
            
            // Informations étudiant
            document.add(new Paragraph("Matricule : " + etudiant.getMatricule(), NORMAL_FONT));
            document.add(new Paragraph(etudiant.getNom(), HEADER_FONT));
            document.add(new Paragraph("Né(e) le " + etudiant.getDatenais().format(DATE_FORMATTER), NORMAL_FONT));
            document.add(new Paragraph(etudiant.getSexe().equals("M") ? "Masculin" : "Féminin", NORMAL_FONT));
            
            String[] institutionParts = etudiant.getInstitution().split("/");
            String institution = institutionParts.length > 0 ? institutionParts[0].trim() : etudiant.getInstitution();
            String niveau = montantNiveau != null ? montantNiveau.getNiveau() : etudiant.getIdniv();
            
            document.add(new Paragraph("Institution : " + institution + " / Niveau : " + niveau, NORMAL_FONT));
            document.add(new Paragraph("\n"));
            
            // Tableau des paiements
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(60);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            
            // En-têtes du tableau
            PdfPCell cellMois = new PdfPCell(new Phrase("Mois", HEADER_FONT));
            cellMois.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellMois.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cellMois);
            
            PdfPCell cellMontant = new PdfPCell(new Phrase("Montant", HEADER_FONT));
            cellMontant.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellMontant.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cellMontant);
            
            int total = 0;
            
            // Ajout de l'équipement s'il existe
            if (equipement != null && equipement.getMontant() > 0) {
                table.addCell(new Phrase("Equipement", NORMAL_FONT));
                table.addCell(new Phrase(String.format("%,d", equipement.getMontant()), NORMAL_FONT));
                total += equipement.getMontant();
            }
            
            // Ajout des paiements mensuels
            if (montantNiveau != null) {
                for (Payer paiement : paiements) {
                    for (int i = 0; i < paiement.getNbrMois(); i++) {
                        String mois = getMoisFromDate(paiement.getDate().plusMonths(i));
                        table.addCell(new Phrase(mois, NORMAL_FONT));
                        table.addCell(new Phrase(String.format("%,d", montantNiveau.getMontant()), NORMAL_FONT));
                        total += montantNiveau.getMontant();
                    }
                }
            }
            
            // Ligne total
            PdfPCell cellTotalLabel = new PdfPCell(new Phrase("Total", HEADER_FONT));
            cellTotalLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTotalLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cellTotalLabel);
            
            PdfPCell cellTotalValue = new PdfPCell(new Phrase(String.format("%,d", total), HEADER_FONT));
            cellTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTotalValue.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cellTotalValue);
            
            document.add(table);
            document.add(new Paragraph("\n"));
            
            // Total final
            Paragraph totalPaye = new Paragraph("Total Payé : " + String.format("%,d", total) + " Ariary", HEADER_FONT);
            document.add(totalPaye);
            
            document.close();
            logger.info("Reçu PDF généré: {}", cheminFichier);
            return true;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la génération du PDF", e);
            return false;
        }
    }
    
    private String getMoisFromDate(java.time.LocalDateTime date) {
        String[] mois = {
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        };
        return mois[date.getMonthValue() - 1];
    }
}