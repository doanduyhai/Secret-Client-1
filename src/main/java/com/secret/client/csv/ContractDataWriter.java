package com.secret.client.csv;

import java.io.IOException;
import java.util.List;
import com.secret.client.model.Contract;

public class ContractDataWriter extends CsvDataWriter {

    private static final String CONTRACT_HEADER = "IDMEN;siGestion;typeContratCredit;codeProduit;codeOffre;SocieteCommerciale;codeSousProduit;dateCreation;dateFinReelle;dateSituation;provenance;IDCAUT;IDT2;numeroClient;situationContratEvry;dateSaisieDossier;dateSituationContrat;montantCredit;nbImpayes;picImpayes;topDefaut;baleIIProbabiliteDefaut;baleIIEAD;baleIILGD;capitalAttribue;capitalRestantDu;dateDerniereUtilisation;datePremierLoyer;mensualiteReelle;montantExigible;montantImpayes;natureDeBien;nombreEcheancesGlobales;procedureSurveillancePermanente;procedureSurveillanceSemiPerm;procedureSurveillanceTemporaire;tauxPlanPrincipal;NbImpayes_M1;NbImpayes_M2;NbImpayes_M3;NbImpayes_M4;NbImpayes_M5;NbImpayes_M6;NbImpayes_M7;NbImpayes_M8;NbImpayes_M9;NbImpayes_M10;NbImpayes_M11;NbImpayes_M12;dateDernierImpaye;decisionFinaleOperatrice;typeCompteSIRoubaix;montant_credit;domaineDeGestionSIRoubaix;classeActivite;topPauseMensualite;cycleRelanceSIRoubaix;TypeProjetSIRoubaix;niveauRelance;motifBlocage;sousTypeCreditRevolvingSIRoubaix;capitalMaximumAutoriseMMCA;NotePaiement_M1;NotePaiement_M2;NotePaiement_M3;NotePaiement_M4;NotePaiement_M5;NotePaiement_M6;NotePaiement_M7;NotePaiement_M8;NotePaiement_M9;NotePaiement_M10;NotePaiement_M11;NotePaiement_M12;note_m_moins12;note_m_moins13;codeSocieteCommerciale;codeProduitCommercial;engagement;identifiant;identifiantCompte;topblocage;topCoSignataire;canalDeVente;topADE;topReportMensualite;codeStructureAPorteur;dateDernUtilHorsAchatEnseigne;dateEtatActivation;dateOuvertureCarte;typeCarte;dateTopageAbusif;etatActivation;montantDisponibleReserveProtege;montantDuDisponibleTotal;processusAugmentation;topActivation;topageAbusif;topPossessionCartePaiement;nbImpayesPartiels;compte;topAssurance;topSinistreAssurance;typeRemboursement;operationEnCoursDeReutil;bic;iban;intitule;rib;dateMiseAJourEmail;dateMiseAJourTelephoneFixe;situationSITDOS;bareme;TopMauvaisFinop;VersionInadaptee;cpnotep";

    public ContractDataWriter(String outputFile) throws IOException {
        super(outputFile);
        writer.writeNext(CONTRACT_HEADER.split(";"));
    }

    public void writeContracts(List<Contract> contracts) {
        for (Contract contract : contracts) {
            writer.writeNext(contract.toCsv().split(";"));
        }
    }

    public void writeContract(Contract contract) {
        writer.writeNext(contract.toCsv().split(";"));
    }

    public void flush() throws IOException {
        writer.close();
    }
}
