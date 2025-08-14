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

import javax.swing.text.Document;
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

            // En-tête avec date selon le format du sujet
            Paragraph date = new Paragraph("Aujourd'hui le " + LocalDate.now().format(DATE_FORMATTER), NORMAL_FONT);
            date.setAlignment(Element.ALIGN_LEFT);
            document.add(date);
            document.add(new Paragraph("\n"));

            // Informations étudiant selon le format exact du sujet
            document.add(new Paragraph("Matricule : " + etudiant.getMatricule(), NORMAL_FONT));
            document.add(new Paragraph(etudiant.getNom(), HEADER_FONT));
            document.add(new Paragraph("Née le " + etudiant.getDatenais().format(DATE_FORMATTER), NORMAL_FONT));
            document.add(new Paragraph(etudiant.getSexe().equals("M") ? "Masculin" : "Féminin", NORMAL_FONT));

            // Institution et niveau selon le format du sujet (ENI / Niveau : L2)
            String niveau = montantNiveau != null ? montantNiveau.getNiveau() : etudiant.getIdniv();
            document.add(new Paragraph("Institution : " + etudiant.getInstitution() + " / Niveau : " + niveau, NORMAL_FONT));
            document.add(new Paragraph("\n"));

            // Tableau des paiements selon le format exact du sujet
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(60);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);

            // En-têtes du tableau
            PdfPCell cellMois = new PdfPCell(new Phrase("Mois", HEADER_FONT));
            cellMois.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellMois.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cellMois);

            PdfPCell cellMontant = new PdfPCell(new Phrase("Montant", HEADER_FONT));
            cellMontant.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellMontant.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cellMontant);

            int total = 0;

            // Ajout de l'équipement TOUJOURS en premier selon l'exemple du sujet
            if (equipement != null && equipement.getMontant() > 0) {
                PdfPCell equipCell = new PdfPCell(new Phrase("Equipement", NORMAL_FONT));
                equipCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(equipCell);

                PdfPCell equipMontantCell = new PdfPCell(new Phrase(String.format("%,d", equipement.getMontant()), NORMAL_FONT));
                equipMontantCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(equipMontantCell);

                total += equipement.getMontant();
            }

            // Ajout des paiements mensuels selon l'ordre du sujet (Février, Mars, Avril...)
            if (montantNiveau != null && !paiements.isEmpty()) {
                String[] moisNoms = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};

                // Pour chaque paiement, ajouter les mois concernés
                for (Payer paiement : paiements) {
                    int moisDebut = paiement.getDate().getMonthValue();

                    for (int i = 0; i < paiement.getNbrMois(); i++) {
                        int moisIndex = ((moisDebut - 1 + i) % 12);
                        String nomMois = moisNoms[moisIndex];

                        PdfPCell moisCell = new PdfPCell(new Phrase(nomMois, NORMAL_FONT));
                        moisCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(moisCell);

                        PdfPCell montantCell = new PdfPCell(new Phrase(String.format("%,d", montantNiveau.getMontant()), NORMAL_FONT));
                        montantCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(montantCell);

                        total += montantNiveau.getMontant();
                    }
                }
            }

            // Ligne total selon le format du sujet
            PdfPCell cellTotalLabel = new PdfPCell(new Phrase("Total", HEADER_FONT));
            cellTotalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellTotalLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cellTotalLabel);

            PdfPCell cellTotalValue = new PdfPCell(new Phrase(String.format("%,d", total), HEADER_FONT));
            cellTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTotalValue.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cellTotalValue);

            document.add(table);
            document.add(new Paragraph("\n"));

            // Total final selon le format exact du sujet
            Paragraph totalPaye = new Paragraph("Total Payé : " + String.format("%,d", total) + " Ariary", HEADER_FONT);
            totalPaye.setAlignment(Element.ALIGN_LEFT);
            document.add(totalPaye);

            document.close();
            logger.info("Reçu PDF généré: {}", cheminFichier);
            return true;

        } catch (Exception e) {
            logger.error("Erreur lors de la génération du PDF", e);
            return false;
        }
    }

    /**
     * Génère un reçu PDF pour un paiement spécifique avec exemple de données
     * Utile pour les tests et démonstrations
     */
    public boolean genererRecuExemple(String cheminFichier) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(cheminFichier));
            document.open();

            // Exemple exact du sujet
            document.add(new Paragraph("Aujourd'hui le 25 Avril 2024", NORMAL_FONT));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Matricule : 3432", NORMAL_FONT));
            document.add(new Paragraph("RAKOTO Bernard", HEADER_FONT));
            document.add(new Paragraph("Née le 23 Octobre 2000", NORMAL_FONT));
            document.add(new Paragraph("Masculin", NORMAL_FONT));
            document.add(new Paragraph("Institution : ENI / Niveau : L2", NORMAL_FONT));
            document.add(new Paragraph("\n"));

            // Tableau
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(60);

            // En-têtes
            table.addCell(new PdfPCell(new Phrase("Mois", HEADER_FONT)));
            table.addCell(new PdfPCell(new Phrase("Montant", HEADER_FONT)));

            // Données
            table.addCell(new Phrase("Equipement", NORMAL_FONT));
            table.addCell(new Phrase("110.000", NORMAL_FONT));
            table.addCell(new Phrase("Février", NORMAL_FONT));
            table.addCell(new Phrase("23.000", NORMAL_FONT));
            table.addCell(new Phrase("Mars", NORMAL_FONT));
            table.addCell(new Phrase("23.000", NORMAL_FONT));
            table.addCell(new Phrase("Avril", NORMAL_FONT));
            table.addCell(new Phrase("23.000", NORMAL_FONT));
            table.addCell(new PdfPCell(new Phrase("Total", HEADER_FONT)));
            table.addCell(new PdfPCell(new Phrase("179.000", HEADER_FONT)));

            document.add(table);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Total Payé : 179.000 Ariary", HEADER_FONT));

            document.close();
            logger.info("Reçu d'exemple généré: {}", cheminFichier);
            return true;

        } catch (Exception e) {
            logger.error("Erreur lors de la génération du reçu d'exemple", e);
            return false;
        }
    }
}