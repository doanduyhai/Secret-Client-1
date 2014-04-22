package com.secret.client.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect
public class Contract {

    public Contract() {
    }

    public Contract(String numeroClient, String identifiant) {
        this.numeroClient = numeroClient;
        this.identifiant = identifiant;
    }

    public Contract(String numeroClient, String identifiant, String idMen) {
        this.numeroClient = numeroClient;
        this.identifiant = identifiant;
        this.idMen = idMen;
    }

    private String accompagnementKey;
    private String clientKey;

    private String idMen;
    private String siGestion;
    private String typeContratCredit;
    private String codeProduit;
    private String codeOffre;
    private String SocieteCommerciale;
    private String codeSousProduit;
    private String dateCreation;
    private String dateFinReelle;
    private String dateSituation;
    private String provenance;
    private String IDCAUT;
    private String IDT2;
    private String numeroClient;
    private String situationContratEvry;
    private String dateSaisieDossier;
    private String dateSituationContrat;
    private String montantCredit;
    private String nbImpayes;
    private String picImpayes;
    private String topDefaut;
    private String baleIIProbabiliteDefaut;
    private String baleIIEAD;
    private String baleIILGD;
    private String capitalAttribue;
    private String capitalRestantDu;
    private String dateDerniereUtilisation;
    private String datePremierLoyer;
    private String mensualiteReelle;
    private String montantExigible;
    private String montantImpayes;
    private String natureDeBien;
    private String nombreEcheancesGlobales;
    private String procedureSurveillancePermanente;
    private String procedureSurveillanceSemiPerm;
    private String procedureSurveillanceTemporaire;
    private String tauxPlanPrincipal;
    private String NbImpayes_M1;
    private String NbImpayes_M2;
    private String NbImpayes_M3;
    private String NbImpayes_M4;
    private String NbImpayes_M5;
    private String NbImpayes_M6;
    private String NbImpayes_M7;
    private String NbImpayes_M8;
    private String NbImpayes_M9;
    private String NbImpayes_M10;
    private String NbImpayes_M11;
    private String NbImpayes_M12;
    private String dateDernierImpaye;
    private String decisionFinaleOperatrice;
    private String typeCompteSIRoubaix;
    private String montant_credit;
    private String domaineDeGestionSIRoubaix;
    private String classeActivite;
    private String topPauseMensualite;
    private String cycleRelanceSIRoubaix;
    private String TypeProjetSIRoubaix;
    private String niveauRelance;
    private String motifBlocage;
    private String sousTypeCreditRevolvingSIRoubaix;
    private String capitalMaximumAutoriseMMCA;
    private String NotePaiement_M1;
    private String NotePaiement_M2;
    private String NotePaiement_M3;
    private String NotePaiement_M4;
    private String NotePaiement_M5;
    private String NotePaiement_M6;
    private String NotePaiement_M7;
    private String NotePaiement_M8;
    private String NotePaiement_M9;
    private String NotePaiement_M10;
    private String NotePaiement_M11;
    private String NotePaiement_M12;
    private String note_m_moins12;
    private String note_m_moins13;
    private String codeSocieteCommerciale;
    private String codeProduitCommercial;
    private String engagement;
    private String identifiant;
    private String identifiantCompte;
    private String topblocage;
    private String topCoSignataire;
    private String canalDeVente;
    private String topADE;
    private String topReportMensualite;
    private String codeStructureAPorteur;
    private String dateDernUtilHorsAchatEnseigne;
    private String dateEtatActivation;
    private String dateOuvertureCarte;
    private String typeCarte;
    private String dateTopageAbusif;
    private String etatActivation;
    private String montantDisponibleReserveProtege;
    private String montantDuDisponibleTotal;
    private String processusAugmentation;
    private String topActivation;
    private String topageAbusif;
    private String topPossessionCartePaiement;
    private String nbImpayesPartiels;
    private String compte;
    private String topAssurance;
    private String topSinistreAssurance;
    private String typeRemboursement;
    private String operationEnCoursDeReutil;
    private String bic;
    private String iban;
    private String intitule;
    private String rib;
    private String dateMiseAJourEmail;
    private String dateMiseAJourTelephoneFixe;
    private String situationSITDOS;
    private String bareme;
    private String DateEditionDernMistral;
    private String TopMauvaisFinop;
    private String VersionInadaptee;

    public String getAccompagnementKey() {
        return accompagnementKey;
    }

    public void setAccompagnementKey(String accompagnementKey) {
        this.accompagnementKey = accompagnementKey;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getIdMen() {
        return idMen;
    }

    public void setIdMen(String idMen) {
        this.idMen = idMen;
    }

    public String getSiGestion() {
        return siGestion;
    }

    public void setSiGestion(String siGestion) {
        this.siGestion = siGestion;
    }

    public String getTypeContratCredit() {
        return typeContratCredit;
    }

    public void setTypeContratCredit(String typeContratCredit) {
        this.typeContratCredit = typeContratCredit;
    }

    public String getCodeProduit() {
        return codeProduit;
    }

    public void setCodeProduit(String codeProduit) {
        this.codeProduit = codeProduit;
    }

    public String getCodeOffre() {
        return codeOffre;
    }

    public void setCodeOffre(String codeOffre) {
        this.codeOffre = codeOffre;
    }

    public String getSocieteCommerciale() {
        return SocieteCommerciale;
    }

    public void setSocieteCommerciale(String societeCommerciale) {
        SocieteCommerciale = societeCommerciale;
    }

    public String getCodeSousProduit() {
        return codeSousProduit;
    }

    public void setCodeSousProduit(String codeSousProduit) {
        this.codeSousProduit = codeSousProduit;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getDateFinReelle() {
        return dateFinReelle;
    }

    public void setDateFinReelle(String dateFinReelle) {
        this.dateFinReelle = dateFinReelle;
    }

    public String getDateSituation() {
        return dateSituation;
    }

    public void setDateSituation(String dateSituation) {
        this.dateSituation = dateSituation;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getIDCAUT() {
        return IDCAUT;
    }

    public void setIDCAUT(String IDCAUT) {
        this.IDCAUT = IDCAUT;
    }

    public String getIDT2() {
        return IDT2;
    }

    public void setIDT2(String IDT2) {
        this.IDT2 = IDT2;
    }

    public String getNumeroClient() {
        return numeroClient;
    }

    public void setNumeroClient(String numeroClient) {
        this.numeroClient = numeroClient;
    }

    public String getSituationContratEvry() {
        return situationContratEvry;
    }

    public void setSituationContratEvry(String situationContratEvry) {
        this.situationContratEvry = situationContratEvry;
    }

    public String getDateSaisieDossier() {
        return dateSaisieDossier;
    }

    public void setDateSaisieDossier(String dateSaisieDossier) {
        this.dateSaisieDossier = dateSaisieDossier;
    }

    public String getDateSituationContrat() {
        return dateSituationContrat;
    }

    public void setDateSituationContrat(String dateSituationContrat) {
        this.dateSituationContrat = dateSituationContrat;
    }

    public String getMontantCredit() {
        return montantCredit;
    }

    public void setMontantCredit(String montantCredit) {
        this.montantCredit = montantCredit;
    }

    public String getNbImpayes() {
        return nbImpayes;
    }

    public void setNbImpayes(String nbImpayes) {
        this.nbImpayes = nbImpayes;
    }

    public String getPicImpayes() {
        return picImpayes;
    }

    public void setPicImpayes(String picImpayes) {
        this.picImpayes = picImpayes;
    }

    public String getTopDefaut() {
        return topDefaut;
    }

    public void setTopDefaut(String topDefaut) {
        this.topDefaut = topDefaut;
    }

    public String getBaleIIProbabiliteDefaut() {
        return baleIIProbabiliteDefaut;
    }

    public void setBaleIIProbabiliteDefaut(String baleIIProbabiliteDefaut) {
        this.baleIIProbabiliteDefaut = baleIIProbabiliteDefaut;
    }

    public String getBaleIIEAD() {
        return baleIIEAD;
    }

    public void setBaleIIEAD(String baleIIEAD) {
        this.baleIIEAD = baleIIEAD;
    }

    public String getBaleIILGD() {
        return baleIILGD;
    }

    public void setBaleIILGD(String baleIILGD) {
        this.baleIILGD = baleIILGD;
    }

    public String getCapitalAttribue() {
        return capitalAttribue;
    }

    public void setCapitalAttribue(String capitalAttribue) {
        this.capitalAttribue = capitalAttribue;
    }

    public String getCapitalRestantDu() {
        return capitalRestantDu;
    }

    public void setCapitalRestantDu(String capitalRestantDu) {
        this.capitalRestantDu = capitalRestantDu;
    }

    public String getDateDerniereUtilisation() {
        return dateDerniereUtilisation;
    }

    public void setDateDerniereUtilisation(String dateDerniereUtilisation) {
        this.dateDerniereUtilisation = dateDerniereUtilisation;
    }

    public String getDatePremierLoyer() {
        return datePremierLoyer;
    }

    public void setDatePremierLoyer(String datePremierLoyer) {
        this.datePremierLoyer = datePremierLoyer;
    }

    public String getMensualiteReelle() {
        return mensualiteReelle;
    }

    public void setMensualiteReelle(String mensualiteReelle) {
        this.mensualiteReelle = mensualiteReelle;
    }

    public String getMontantExigible() {
        return montantExigible;
    }

    public void setMontantExigible(String montantExigible) {
        this.montantExigible = montantExigible;
    }

    public String getMontantImpayes() {
        return montantImpayes;
    }

    public void setMontantImpayes(String montantImpayes) {
        this.montantImpayes = montantImpayes;
    }

    public String getNatureDeBien() {
        return natureDeBien;
    }

    public void setNatureDeBien(String natureDeBien) {
        this.natureDeBien = natureDeBien;
    }

    public String getNombreEcheancesGlobales() {
        return nombreEcheancesGlobales;
    }

    public void setNombreEcheancesGlobales(String nombreEcheancesGlobales) {
        this.nombreEcheancesGlobales = nombreEcheancesGlobales;
    }

    public String getProcedureSurveillancePermanente() {
        return procedureSurveillancePermanente;
    }

    public void setProcedureSurveillancePermanente(String procedureSurveillancePermanente) {
        this.procedureSurveillancePermanente = procedureSurveillancePermanente;
    }

    public String getProcedureSurveillanceSemiPerm() {
        return procedureSurveillanceSemiPerm;
    }

    public void setProcedureSurveillanceSemiPerm(String procedureSurveillanceSemiPerm) {
        this.procedureSurveillanceSemiPerm = procedureSurveillanceSemiPerm;
    }

    public String getProcedureSurveillanceTemporaire() {
        return procedureSurveillanceTemporaire;
    }

    public void setProcedureSurveillanceTemporaire(String procedureSurveillanceTemporaire) {
        this.procedureSurveillanceTemporaire = procedureSurveillanceTemporaire;
    }

    public String getTauxPlanPrincipal() {
        return tauxPlanPrincipal;
    }

    public void setTauxPlanPrincipal(String tauxPlanPrincipal) {
        this.tauxPlanPrincipal = tauxPlanPrincipal;
    }

    public String getNbImpayes_M1() {
        return NbImpayes_M1;
    }

    public void setNbImpayes_M1(String nbImpayes_M1) {
        NbImpayes_M1 = nbImpayes_M1;
    }

    public String getNbImpayes_M2() {
        return NbImpayes_M2;
    }

    public void setNbImpayes_M2(String nbImpayes_M2) {
        NbImpayes_M2 = nbImpayes_M2;
    }

    public String getNbImpayes_M3() {
        return NbImpayes_M3;
    }

    public void setNbImpayes_M3(String nbImpayes_M3) {
        NbImpayes_M3 = nbImpayes_M3;
    }

    public String getNbImpayes_M4() {
        return NbImpayes_M4;
    }

    public void setNbImpayes_M4(String nbImpayes_M4) {
        NbImpayes_M4 = nbImpayes_M4;
    }

    public String getNbImpayes_M5() {
        return NbImpayes_M5;
    }

    public void setNbImpayes_M5(String nbImpayes_M5) {
        NbImpayes_M5 = nbImpayes_M5;
    }

    public String getNbImpayes_M6() {
        return NbImpayes_M6;
    }

    public void setNbImpayes_M6(String nbImpayes_M6) {
        NbImpayes_M6 = nbImpayes_M6;
    }

    public String getNbImpayes_M7() {
        return NbImpayes_M7;
    }

    public void setNbImpayes_M7(String nbImpayes_M7) {
        NbImpayes_M7 = nbImpayes_M7;
    }

    public String getNbImpayes_M8() {
        return NbImpayes_M8;
    }

    public void setNbImpayes_M8(String nbImpayes_M8) {
        NbImpayes_M8 = nbImpayes_M8;
    }

    public String getNbImpayes_M9() {
        return NbImpayes_M9;
    }

    public void setNbImpayes_M9(String nbImpayes_M9) {
        NbImpayes_M9 = nbImpayes_M9;
    }

    public String getNbImpayes_M10() {
        return NbImpayes_M10;
    }

    public void setNbImpayes_M10(String nbImpayes_M10) {
        NbImpayes_M10 = nbImpayes_M10;
    }

    public String getNbImpayes_M11() {
        return NbImpayes_M11;
    }

    public void setNbImpayes_M11(String nbImpayes_M11) {
        NbImpayes_M11 = nbImpayes_M11;
    }

    public String getNbImpayes_M12() {
        return NbImpayes_M12;
    }

    public void setNbImpayes_M12(String nbImpayes_M12) {
        NbImpayes_M12 = nbImpayes_M12;
    }

    public String getDateDernierImpaye() {
        return dateDernierImpaye;
    }

    public void setDateDernierImpaye(String dateDernierImpaye) {
        this.dateDernierImpaye = dateDernierImpaye;
    }

    public String getDecisionFinaleOperatrice() {
        return decisionFinaleOperatrice;
    }

    public void setDecisionFinaleOperatrice(String decisionFinaleOperatrice) {
        this.decisionFinaleOperatrice = decisionFinaleOperatrice;
    }

    public String getTypeCompteSIRoubaix() {
        return typeCompteSIRoubaix;
    }

    public void setTypeCompteSIRoubaix(String typeCompteSIRoubaix) {
        this.typeCompteSIRoubaix = typeCompteSIRoubaix;
    }

    public String getMontant_credit() {
        return montant_credit;
    }

    public void setMontant_credit(String montant_credit) {
        this.montant_credit = montant_credit;
    }

    public String getDomaineDeGestionSIRoubaix() {
        return domaineDeGestionSIRoubaix;
    }

    public void setDomaineDeGestionSIRoubaix(String domaineDeGestionSIRoubaix) {
        this.domaineDeGestionSIRoubaix = domaineDeGestionSIRoubaix;
    }

    public String getClasseActivite() {
        return classeActivite;
    }

    public void setClasseActivite(String classeActivite) {
        this.classeActivite = classeActivite;
    }

    public String getTopPauseMensualite() {
        return topPauseMensualite;
    }

    public void setTopPauseMensualite(String topPauseMensualite) {
        this.topPauseMensualite = topPauseMensualite;
    }

    public String getCycleRelanceSIRoubaix() {
        return cycleRelanceSIRoubaix;
    }

    public void setCycleRelanceSIRoubaix(String cycleRelanceSIRoubaix) {
        this.cycleRelanceSIRoubaix = cycleRelanceSIRoubaix;
    }

    public String getTypeProjetSIRoubaix() {
        return TypeProjetSIRoubaix;
    }

    public void setTypeProjetSIRoubaix(String typeProjetSIRoubaix) {
        TypeProjetSIRoubaix = typeProjetSIRoubaix;
    }

    public String getNiveauRelance() {
        return niveauRelance;
    }

    public void setNiveauRelance(String niveauRelance) {
        this.niveauRelance = niveauRelance;
    }

    public String getMotifBlocage() {
        return motifBlocage;
    }

    public void setMotifBlocage(String motifBlocage) {
        this.motifBlocage = motifBlocage;
    }

    public String getSousTypeCreditRevolvingSIRoubaix() {
        return sousTypeCreditRevolvingSIRoubaix;
    }

    public void setSousTypeCreditRevolvingSIRoubaix(String sousTypeCreditRevolvingSIRoubaix) {
        this.sousTypeCreditRevolvingSIRoubaix = sousTypeCreditRevolvingSIRoubaix;
    }

    public String getCapitalMaximumAutoriseMMCA() {
        return capitalMaximumAutoriseMMCA;
    }

    public void setCapitalMaximumAutoriseMMCA(String capitalMaximumAutoriseMMCA) {
        this.capitalMaximumAutoriseMMCA = capitalMaximumAutoriseMMCA;
    }

    public String getNotePaiement_M1() {
        return NotePaiement_M1;
    }

    public void setNotePaiement_M1(String notePaiement_M1) {
        NotePaiement_M1 = notePaiement_M1;
    }

    public String getNotePaiement_M2() {
        return NotePaiement_M2;
    }

    public void setNotePaiement_M2(String notePaiement_M2) {
        NotePaiement_M2 = notePaiement_M2;
    }

    public String getNotePaiement_M3() {
        return NotePaiement_M3;
    }

    public void setNotePaiement_M3(String notePaiement_M3) {
        NotePaiement_M3 = notePaiement_M3;
    }

    public String getNotePaiement_M4() {
        return NotePaiement_M4;
    }

    public void setNotePaiement_M4(String notePaiement_M4) {
        NotePaiement_M4 = notePaiement_M4;
    }

    public String getNotePaiement_M5() {
        return NotePaiement_M5;
    }

    public void setNotePaiement_M5(String notePaiement_M5) {
        NotePaiement_M5 = notePaiement_M5;
    }

    public String getNotePaiement_M6() {
        return NotePaiement_M6;
    }

    public void setNotePaiement_M6(String notePaiement_M6) {
        NotePaiement_M6 = notePaiement_M6;
    }

    public String getNotePaiement_M7() {
        return NotePaiement_M7;
    }

    public void setNotePaiement_M7(String notePaiement_M7) {
        NotePaiement_M7 = notePaiement_M7;
    }

    public String getNotePaiement_M8() {
        return NotePaiement_M8;
    }

    public void setNotePaiement_M8(String notePaiement_M8) {
        NotePaiement_M8 = notePaiement_M8;
    }

    public String getNotePaiement_M9() {
        return NotePaiement_M9;
    }

    public void setNotePaiement_M9(String notePaiement_M9) {
        NotePaiement_M9 = notePaiement_M9;
    }

    public String getNotePaiement_M10() {
        return NotePaiement_M10;
    }

    public void setNotePaiement_M10(String notePaiement_M10) {
        NotePaiement_M10 = notePaiement_M10;
    }

    public String getNotePaiement_M11() {
        return NotePaiement_M11;
    }

    public void setNotePaiement_M11(String notePaiement_M11) {
        NotePaiement_M11 = notePaiement_M11;
    }

    public String getNotePaiement_M12() {
        return NotePaiement_M12;
    }

    public void setNotePaiement_M12(String notePaiement_M12) {
        NotePaiement_M12 = notePaiement_M12;
    }

    public String getNote_m_moins13() {
        return note_m_moins13;
    }

    public void setNote_m_moins13(String note_m_moins13) {
        this.note_m_moins13 = note_m_moins13;
    }

    public String getNote_m_moins12() {
        return note_m_moins12;
    }

    public void setNote_m_moins12(String note_m_moins12) {
        this.note_m_moins12 = note_m_moins12;
    }

    public String getCodeSocieteCommerciale() {
        return codeSocieteCommerciale;
    }

    public void setCodeSocieteCommerciale(String codeSocieteCommerciale) {
        this.codeSocieteCommerciale = codeSocieteCommerciale;
    }

    public String getCodeProduitCommercial() {
        return codeProduitCommercial;
    }

    public void setCodeProduitCommercial(String codeProduitCommercial) {
        this.codeProduitCommercial = codeProduitCommercial;
    }

    public String getEngagement() {
        return engagement;
    }

    public void setEngagement(String engagement) {
        this.engagement = engagement;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getIdentifiantCompte() {
        return identifiantCompte;
    }

    public void setIdentifiantCompte(String identifiantCompte) {
        this.identifiantCompte = identifiantCompte;
    }

    public String getTopblocage() {
        return topblocage;
    }

    public void setTopblocage(String topblocage) {
        this.topblocage = topblocage;
    }

    public String getTopCoSignataire() {
        return topCoSignataire;
    }

    public void setTopCoSignataire(String topCoSignataire) {
        this.topCoSignataire = topCoSignataire;
    }

    public String getCanalDeVente() {
        return canalDeVente;
    }

    public void setCanalDeVente(String canalDeVente) {
        this.canalDeVente = canalDeVente;
    }

    public String getTopADE() {
        return topADE;
    }

    public void setTopADE(String topADE) {
        this.topADE = topADE;
    }

    public String getTopReportMensualite() {
        return topReportMensualite;
    }

    public void setTopReportMensualite(String topReportMensualite) {
        this.topReportMensualite = topReportMensualite;
    }

    public String getCodeStructureAPorteur() {
        return codeStructureAPorteur;
    }

    public void setCodeStructureAPorteur(String codeStructureAPorteur) {
        this.codeStructureAPorteur = codeStructureAPorteur;
    }

    public String getDateDernUtilHorsAchatEnseigne() {
        return dateDernUtilHorsAchatEnseigne;
    }

    public void setDateDernUtilHorsAchatEnseigne(String dateDernUtilHorsAchatEnseigne) {
        this.dateDernUtilHorsAchatEnseigne = dateDernUtilHorsAchatEnseigne;
    }

    public String getDateEtatActivation() {
        return dateEtatActivation;
    }

    public void setDateEtatActivation(String dateEtatActivation) {
        this.dateEtatActivation = dateEtatActivation;
    }

    public String getDateOuvertureCarte() {
        return dateOuvertureCarte;
    }

    public void setDateOuvertureCarte(String dateOuvertureCarte) {
        this.dateOuvertureCarte = dateOuvertureCarte;
    }

    public String getTypeCarte() {
        return typeCarte;
    }

    public void setTypeCarte(String typeCarte) {
        this.typeCarte = typeCarte;
    }

    public String getDateTopageAbusif() {
        return dateTopageAbusif;
    }

    public void setDateTopageAbusif(String dateTopageAbusif) {
        this.dateTopageAbusif = dateTopageAbusif;
    }

    public String getEtatActivation() {
        return etatActivation;
    }

    public void setEtatActivation(String etatActivation) {
        this.etatActivation = etatActivation;
    }

    public String getMontantDisponibleReserveProtege() {
        return montantDisponibleReserveProtege;
    }

    public void setMontantDisponibleReserveProtege(String montantDisponibleReserveProtege) {
        this.montantDisponibleReserveProtege = montantDisponibleReserveProtege;
    }

    public String getMontantDuDisponibleTotal() {
        return montantDuDisponibleTotal;
    }

    public void setMontantDuDisponibleTotal(String montantDuDisponibleTotal) {
        this.montantDuDisponibleTotal = montantDuDisponibleTotal;
    }

    public String getProcessusAugmentation() {
        return processusAugmentation;
    }

    public void setProcessusAugmentation(String processusAugmentation) {
        this.processusAugmentation = processusAugmentation;
    }

    public String getTopActivation() {
        return topActivation;
    }

    public void setTopActivation(String topActivation) {
        this.topActivation = topActivation;
    }

    public String getTopageAbusif() {
        return topageAbusif;
    }

    public void setTopageAbusif(String topageAbusif) {
        this.topageAbusif = topageAbusif;
    }

    public String getTopPossessionCartePaiement() {
        return topPossessionCartePaiement;
    }

    public void setTopPossessionCartePaiement(String topPossessionCartePaiement) {
        this.topPossessionCartePaiement = topPossessionCartePaiement;
    }

    public String getNbImpayesPartiels() {
        return nbImpayesPartiels;
    }

    public void setNbImpayesPartiels(String nbImpayesPartiels) {
        this.nbImpayesPartiels = nbImpayesPartiels;
    }

    public String getCompte() {
        return compte;
    }

    public void setCompte(String compte) {
        this.compte = compte;
    }

    public String getTopAssurance() {
        return topAssurance;
    }

    public void setTopAssurance(String topAssurance) {
        this.topAssurance = topAssurance;
    }

    public String getTopSinistreAssurance() {
        return topSinistreAssurance;
    }

    public void setTopSinistreAssurance(String topSinistreAssurance) {
        this.topSinistreAssurance = topSinistreAssurance;
    }

    public String getTypeRemboursement() {
        return typeRemboursement;
    }

    public void setTypeRemboursement(String typeRemboursement) {
        this.typeRemboursement = typeRemboursement;
    }

    public String getOperationEnCoursDeReutil() {
        return operationEnCoursDeReutil;
    }

    public void setOperationEnCoursDeReutil(String operationEnCoursDeReutil) {
        this.operationEnCoursDeReutil = operationEnCoursDeReutil;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getRib() {
        return rib;
    }

    public void setRib(String rib) {
        this.rib = rib;
    }

    public String getDateMiseAJourEmail() {
        return dateMiseAJourEmail;
    }

    public void setDateMiseAJourEmail(String dateMiseAJourEmail) {
        this.dateMiseAJourEmail = dateMiseAJourEmail;
    }

    public String getDateMiseAJourTelephoneFixe() {
        return dateMiseAJourTelephoneFixe;
    }

    public void setDateMiseAJourTelephoneFixe(String dateMiseAJourTelephoneFixe) {
        this.dateMiseAJourTelephoneFixe = dateMiseAJourTelephoneFixe;
    }

    public String getSituationSITDOS() {
        return situationSITDOS;
    }

    public void setSituationSITDOS(String situationSITDOS) {
        this.situationSITDOS = situationSITDOS;
    }

    public String getBareme() {
        return bareme;
    }

    public void setBareme(String bareme) {
        this.bareme = bareme;
    }

    public String getDateEditionDernMistral() {
        return DateEditionDernMistral;
    }

    public void setDateEditionDernMistral(String dateEditionDernMistral) {
        DateEditionDernMistral = dateEditionDernMistral;
    }

    public String getTopMauvaisFinop() {
        return TopMauvaisFinop;
    }

    public void setTopMauvaisFinop(String topMauvaisFinop) {
        TopMauvaisFinop = topMauvaisFinop;
    }

    public String getVersionInadaptee() {
        return VersionInadaptee;
    }

    public void setVersionInadaptee(String versionInadaptee) {
        VersionInadaptee = versionInadaptee;
    }

    public String toCsv() {
        return (idMen != null ? idMen : "") + ";" +
                (siGestion != null ? siGestion : "") + ";" +
                (typeContratCredit != null ? typeContratCredit : "") + ";" +
                (codeProduit != null ? codeProduit : "") + ";" +
                (codeOffre != null ? codeOffre : "") + ";" +
                (SocieteCommerciale != null ? SocieteCommerciale : "") + ";" +
                (codeSousProduit != null ? codeSousProduit : "") + ";" +
                (dateCreation != null ? dateCreation : "") + ";" +
                (dateFinReelle != null ? dateFinReelle : "") + ";" +
                (dateSituation != null ? dateSituation : "") + ";" +
                (provenance != null ? provenance : "") + ";" +
                (IDCAUT != null ? IDCAUT : "") + ";" +
                (IDT2 != null ? IDT2 : "") + ";" +
                (numeroClient != null ? numeroClient : "") + ";" +
                (situationContratEvry != null ? situationContratEvry : "") + ";" +
                (dateSaisieDossier != null ? dateSaisieDossier : "") + ";" +
                (dateSituationContrat != null ? dateSituationContrat : "") + ";" +
                (montantCredit != null ? montantCredit : "") + ";" +
                (nbImpayes != null ? nbImpayes : "") + ";" +
                (picImpayes != null ? picImpayes : "") + ";" +
                (topDefaut != null ? topDefaut : "") + ";" +
                (baleIIProbabiliteDefaut != null ? baleIIProbabiliteDefaut : "") + ";" +
                (baleIIEAD != null ? baleIIEAD : "") + ";" +
                (baleIILGD != null ? baleIILGD : "") + ";" +
                (capitalAttribue != null ? capitalAttribue : "") + ";" +
                (capitalRestantDu != null ? capitalRestantDu : "") + ";" +
                (dateDerniereUtilisation != null ? dateDerniereUtilisation : "") + ";" +
                (datePremierLoyer != null ? datePremierLoyer : "") + ";" +
                (mensualiteReelle != null ? mensualiteReelle : "") + ";" +
                (montantExigible != null ? montantExigible : "") + ";" +
                (montantImpayes != null ? montantImpayes : "") + ";" +
                (natureDeBien != null ? natureDeBien : "") + ";" +
                (nombreEcheancesGlobales != null ? nombreEcheancesGlobales : "") + ";" +
                (procedureSurveillancePermanente != null ? procedureSurveillancePermanente : "") + ";" +
                (procedureSurveillanceSemiPerm != null ? procedureSurveillanceSemiPerm : "") + ";" +
                (procedureSurveillanceTemporaire != null ? procedureSurveillanceTemporaire : "") + ";" +
                (tauxPlanPrincipal != null ? tauxPlanPrincipal : "") + ";" +
                (NbImpayes_M1 != null ? NbImpayes_M1 : "") + ";" +
                (NbImpayes_M2 != null ? NbImpayes_M2 : "") + ";" +
                (NbImpayes_M3 != null ? NbImpayes_M3 : "") + ";" +
                (NbImpayes_M4 != null ? NbImpayes_M4 : "") + ";" +
                (NbImpayes_M5 != null ? NbImpayes_M5 : "") + ";" +
                (NbImpayes_M6 != null ? NbImpayes_M6 : "") + ";" +
                (NbImpayes_M7 != null ? NbImpayes_M7 : "") + ";" +
                (NbImpayes_M8 != null ? NbImpayes_M8 : "") + ";" +
                (NbImpayes_M9 != null ? NbImpayes_M9 : "") + ";" +
                (NbImpayes_M10 != null ? NbImpayes_M10 : "") + ";" +
                (NbImpayes_M11 != null ? NbImpayes_M11 : "") + ";" +
                (NbImpayes_M12 != null ? NbImpayes_M12 : "") + ";" +
                (dateDernierImpaye != null ? dateDernierImpaye : "") + ";" +
                (decisionFinaleOperatrice != null ? decisionFinaleOperatrice : "") + ";" +
                (typeCompteSIRoubaix != null ? typeCompteSIRoubaix : "") + ";" +
                (montant_credit != null ? montant_credit : "") + ";" +
                (domaineDeGestionSIRoubaix != null ? domaineDeGestionSIRoubaix : "") + ";" +
                (classeActivite != null ? classeActivite : "") + ";" +
                (topPauseMensualite != null ? topPauseMensualite : "") + ";" +
                (cycleRelanceSIRoubaix != null ? cycleRelanceSIRoubaix : "") + ";" +
                (TypeProjetSIRoubaix != null ? TypeProjetSIRoubaix : "") + ";" +
                (niveauRelance != null ? niveauRelance : "") + ";" +
                (motifBlocage != null ? motifBlocage : "") + ";" +
                (sousTypeCreditRevolvingSIRoubaix != null ? sousTypeCreditRevolvingSIRoubaix : "") + ";" +
                (capitalMaximumAutoriseMMCA != null ? capitalMaximumAutoriseMMCA : "") + ";" +
                (NotePaiement_M1 != null ? NotePaiement_M1 : "") + ";" +
                (NotePaiement_M2 != null ? NotePaiement_M2 : "") + ";" +
                (NotePaiement_M3 != null ? NotePaiement_M3 : "") + ";" +
                (NotePaiement_M4 != null ? NotePaiement_M4 : "") + ";" +
                (NotePaiement_M5 != null ? NotePaiement_M5 : "") + ";" +
                (NotePaiement_M6 != null ? NotePaiement_M6 : "") + ";" +
                (NotePaiement_M7 != null ? NotePaiement_M7 : "") + ";" +
                (NotePaiement_M8 != null ? NotePaiement_M8 : "") + ";" +
                (NotePaiement_M9 != null ? NotePaiement_M9 : "") + ";" +
                (NotePaiement_M10 != null ? NotePaiement_M10 : "") + ";" +
                (NotePaiement_M11 != null ? NotePaiement_M11 : "") + ";" +
                (NotePaiement_M12 != null ? NotePaiement_M12 : "") + ";" +
                (note_m_moins12 != null ? note_m_moins12 : "") + ";" +
                (note_m_moins13 != null ? note_m_moins13 : "") + ";" +
                (codeSocieteCommerciale != null ? codeSocieteCommerciale : "") + ";" +
                (codeProduitCommercial != null ? codeProduitCommercial : "") + ";" +
                (engagement != null ? engagement : "") + ";" +
                (identifiant != null ? identifiant : "") + ";" +
                (identifiantCompte != null ? identifiantCompte : "") + ";" +
                (topblocage != null ? topblocage : "") + ";" +
                (topCoSignataire != null ? topCoSignataire : "") + ";" +
                (canalDeVente != null ? canalDeVente : "") + ";" +
                (topADE != null ? topADE : "") + ";" +
                (topReportMensualite != null ? topReportMensualite : "") + ";" +
                (codeStructureAPorteur != null ? codeStructureAPorteur : "") + ";" +
                (dateDernUtilHorsAchatEnseigne != null ? dateDernUtilHorsAchatEnseigne : "") + ";" +
                (dateEtatActivation != null ? dateEtatActivation : "") + ";" +
                (dateOuvertureCarte != null ? dateOuvertureCarte : "") + ";" +
                (typeCarte != null ? typeCarte : "") + ";" +
                (dateTopageAbusif != null ? dateTopageAbusif : "") + ";" +
                (etatActivation != null ? etatActivation : "") + ";" +
                (montantDisponibleReserveProtege != null ? montantDisponibleReserveProtege : "") + ";" +
                (montantDuDisponibleTotal != null ? montantDuDisponibleTotal : "") + ";" +
                (processusAugmentation != null ? processusAugmentation : "") + ";" +
                (topActivation != null ? topActivation : "") + ";" +
                (topageAbusif != null ? topageAbusif : "") + ";" +
                (topPossessionCartePaiement != null ? topPossessionCartePaiement : "") + ";" +
                (nbImpayesPartiels != null ? nbImpayesPartiels : "") + ";" +
                (compte != null ? compte : "") + ";" +
                (topAssurance != null ? topAssurance : "") + ";" +
                (topSinistreAssurance != null ? topSinistreAssurance : "") + ";" +
                (typeRemboursement != null ? typeRemboursement : "") + ";" +
                (operationEnCoursDeReutil != null ? operationEnCoursDeReutil : "") + ";" +
                (bic != null ? bic : "") + ";" +
                (iban != null ? iban : "") + ";" +
                (intitule != null ? intitule : "") + ";" +
                (rib != null ? rib : "") + ";" +
                (dateMiseAJourEmail != null ? dateMiseAJourEmail : "") + ";" +
                (dateMiseAJourTelephoneFixe != null ? dateMiseAJourTelephoneFixe : "") + ";" +
                (situationSITDOS != null ? situationSITDOS : "") + ";" +
                (bareme != null ? bareme : "") + ";" +
                (DateEditionDernMistral != null ? DateEditionDernMistral : "") + ";" +
                (TopMauvaisFinop != null ? TopMauvaisFinop : "") + ";" +
                (VersionInadaptee != null ? VersionInadaptee : "");
    }
}
