package com.secret.client.csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.secret.client.model.Client;
import com.secret.client.model.Contract;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

public class CsvDataLoaderOld {
    private static final char CSV_SEPARATOR = ';';

    public Map<String, Client> loadClients(String clientFile) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(clientFile), CSV_SEPARATOR);
        ColumnPositionMappingStrategy<Client> strat = new ColumnPositionMappingStrategy<Client>();
        strat.captureHeader(reader);
        strat.setType(Client.class);
        String[] columns = new String[] {
                "idMen",
                "autresRevenus",
                "allocationsDeLaPersonne",
                "autresCharges",
                "canalDeContact",
                "chargesHabitation",
                "codeCsp",
                "codePostal",
                "codeRegroupement",
                "codeValiditeEmail",
                "codeValiditeTelephoneFixe",
                "codeValiditeTelephonePortable",
                "conjoint",
                "creditExterne",
                "dateDebut",
                "dateDeCreation",
                "dateEmbauche",
                "dateEmmenagement",
                "dateFin",
                "dateMajHabitat",
                "dateMiseAJourTelephonePortable",
                "dateMajRevenus",
                "dateNaissance",
                "loyer",
                "motif",
                "nbEnfantsACharge",
                "numeroClient",
                "paysDeNationalite",
                "pensionRecus",
                "qualite",
                "regimeJuridique",
                "revenuPrincipal",
                "sinistreEnCours",
                "situationFamiliale",
                "situationLegale",
                "topMembreDuPersonnel",
                "topRefusabilite",
                "topValiditeAdresse",
                "typeHabitat",
                "typePieceIdentite",
                "typologieClientCredit",
                "dateDerniereUtilisationRevolving",
                "datePlusAncienneSouscriptionPB",
                "datePlusAncienneSouscriptionRev",
                "sofCapitalRestant_du_PB",
                "sofCapitalRestant_du_REV",
                "SOFMontant_Moyen_REV_12m",
                "SOFMax_Nombre_Impayes",
                "SOFTauxUtilisation",
                "topClientActifEvry",
                "topClientActifRBX",
                "ffMaxNotePaiement",
                "ffMinResultanteREV",
                "ffNbComptesAmortissables",
                "ffNbComptesDetenus",
                "ffNbComptesRenouvelables",
                "ffTauxDutilisationREV12m",
                "ffVariationDette12",
                "topDetentionKangourou",
                "topDetentionRevNaff",
                "dateMajAdresse",
                "paysEmployeur",
                "ffDateDernierAchat",
                "ffDateDernierImpaye",
                "type",
                "nomGrille",
                "note",
                "domaine",
                "refusabiliteBaleII",
                "refusabilite",
                "segment",
                "domiciliationsBancaires",
                "ficheFICP",
                "indicateurFauxClient",
                "regroupementClient",
                "Charges",
                "dateFinValiditePieceIdentite",
                "presenceHomonyme",
                "topRepresentantDesSARL",
                "codeIdentificationDeclinaison",
                "societesCommerciales",
                "nbContratClient",
                "dateFinContrat",
                "codePaysResidence",
                "montantPensionDu"
        };
        strat.setColumnMapping(columns);
        CsvToBean csv = new CsvToBean();
        final List<Client> clients = csv.parse(strat, reader);
        Map<String, Client> clientMap = new HashMap<String, Client>(5100);

        // Remove first line corresponding to the header
        clients.remove(0);

        for (Client client : clients) {
            clientMap.put(client.getNumeroClient(), client);
        }
        return clientMap;
    }

    public Map<String, List<Contract>> loadContrats(String contratFile) throws FileNotFoundException {
        CSVReader reader = new CSVReader(new FileReader(contratFile), CSV_SEPARATOR);
        ColumnPositionMappingStrategy<Contract> strat = new ColumnPositionMappingStrategy<Contract>();
        strat.setType(Contract.class);
        String[] columns = new String[] {
                "idMen",
                "siGestion",
                "typeContratCredit",
                "codeProduit",
                "codeOffre",
                "SocieteCommerciale",
                "codeSousProduit",
                "dateCreation",
                "dateFinReelle",
                "dateSituation",
                "provenance",
                "IDCAUT",
                "IDT2",
                "numeroClient",
                "situationContratEvry",
                "dateSaisieDossier",
                "dateSituationContrat",
                "montantCredit",
                "nbImpayes",
                "picImpayes",
                "topDefaut",
                "baleIIProbabiliteDefaut",
                "baleIIEAD",
                "baleIILGD",
                "capitalAttribue",
                "capitalRestantDu",
                "dateDerniereUtilisation",
                "datePremierLoyer",
                "mensualiteReelle",
                "montantExigible",
                "montantImpayes",
                "natureDeBien",
                "nombreEcheancesGlobales",
                "procedureSurveillancePermanente",
                "procedureSurveillanceSemiPerm",
                "procedureSurveillanceTemporaire",
                "tauxPlanPrincipal",
                "NbImpayes_M1",
                "NbImpayes_M2",
                "NbImpayes_M3",
                "NbImpayes_M4",
                "NbImpayes_M5",
                "NbImpayes_M6",
                "NbImpayes_M7",
                "NbImpayes_M8",
                "NbImpayes_M9",
                "NbImpayes_M10",
                "NbImpayes_M11",
                "NbImpayes_M12",
                "dateDernierImpaye",
                "decisionFinaleOperatrice",
                "typeCompteSIRoubaix",
                "montant_credit",
                "domaineDeGestionSIRoubaix",
                "classeActivite",
                "topPauseMensualite",
                "cycleRelanceSIRoubaix",
                "TypeProjetSIRoubaix",
                "niveauRelance",
                "motifBlocage",
                "sousTypeCreditRevolvingSIRoubaix",
                "capitalMaximumAutoriseMMCA",
                "NotePaiement_M1",
                "NotePaiement_M2",
                "NotePaiement_M3",
                "NotePaiement_M4",
                "NotePaiement_M5",
                "NotePaiement_M6",
                "NotePaiement_M7",
                "NotePaiement_M8",
                "NotePaiement_M9",
                "NotePaiement_M10",
                "NotePaiement_M11",
                "NotePaiement_M12",
                "note_m_moins12",
                "note_m_moins13",
                "codeSocieteCommerciale",
                "codeProduitCommercial",
                "engagement",
                "identifiant",
                "identifiantCompte",
                "topblocage",
                "topCoSignataire",
                "canalDeVente",
                "topADE",
                "topReportMensualite",
                "codeStructureAPorteur",
                "dateDernUtilHorsAchatEnseigne",
                "dateEtatActivation",
                "dateOuvertureCarte",
                "typeCarte",
                "dateTopageAbusif",
                "etatActivation",
                "montantDisponibleReserveProtege",
                "montantDuDisponibleTotal",
                "processusAugmentation",
                "topActivation",
                "topageAbusif",
                "topPossessionCartePaiement",
                "nbImpayesPartiels",
                "compte",
                "topAssurance",
                "topSinistreAssurance",
                "typeRemboursement",
                "operationEnCoursDeReutil",
                "bic",
                "iban",
                "intitule",
                "rib",
                "dateMiseAJourEmail",
                "dateMiseAJourTelephoneFixe",
                "situationSITDOS",
                "bareme",
                "DateEditionDernMistral",
                "TopMauvaisFinop",
                "VersionInadaptee"
        };
        strat.setColumnMapping(columns);
        CsvToBean csv = new CsvToBean();
        final List<Contract> contrats = csv.parse(strat, reader);
        Map<String, List<Contract>> clientContrats = new HashMap<String, List<Contract>>();

        // Remove first line corresponding to the header
        contrats.remove(0);

        for (Contract contrat : contrats) {
            final String numeroClient = contrat.getNumeroClient();
            if (!clientContrats.containsKey(numeroClient)) {
                clientContrats.put(numeroClient, new ArrayList<Contract>());
            }
            clientContrats.get(numeroClient).add(contrat);
        }
        return clientContrats;
    }
}
