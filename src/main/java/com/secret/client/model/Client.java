package com.secret.client.model;


import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect
public class Client {

    public Client() {
    }

    public Client(String numeroClient, String conjoint) {
        this.numeroClient = numeroClient;
        this.conjoint = conjoint;
    }

    public Client(String numeroClient, String conjoint, String idMen) {
        this.numeroClient = numeroClient;
        this.conjoint = conjoint;
        this.idMen = idMen;
    }

    private String idMen;
    private String autresRevenus;
    private String allocationsDeLaPersonne;
    private String autresCharges;
    private String canalDeContact;
    private String chargesHabitation;
    private String codeCsp;
    private String codePostal;
    private String codeRegroupement;
    private String codeValiditeEmail;
    private String codeValiditeTelephoneFixe;
    private String codeValiditeTelephonePortable;
    private String conjoint;
    private String creditExterne;
    private String dateDebut;
    private String dateDeCreation;
    private String dateEmbauche;
    private String dateEmmenagement;
    private String dateFin;
    private String dateMajHabitat;
    private String dateMiseAJourTelephonePortable;
    private String dateMajRevenus;
    private String dateNaissance;
    private String loyer;
    private String motif;
    private String nbEnfantsACharge;
    private String numeroClient;
    private String paysDeNationalite;
    private String pensionRecus;
    private String qualite;
    private String regimeJuridique;
    private String revenuPrincipal;
    private String sinistreEnCours;
    private String situationFamiliale;
    private String situationLegale;
    private String topMembreDuPersonnel;
    private String topRefusabilite;
    private String topValiditeAdresse;
    private String typeHabitat;
    private String typePieceIdentite;
    private String typologieClientCredit;
    private String dateDerniereUtilisationRevolving;
    private String datePlusAncienneSouscriptionPB;
    private String datePlusAncienneSouscriptionRev;
    private String sofCapitalRestant_du_PB;
    private String sofCapitalRestant_du_REV;
    private String SOFMontant_Moyen_REV_12m;
    private String SOFMax_Nombre_Impayes;
    private String SOFTauxUtilisation;
    private String topClientActifEvry;
    private String topClientActifRBX;
    private String ffMaxNotePaiement;
    private String ffMinResultanteREV;
    private String ffNbComptesAmortissables;
    private String ffNbComptesDetenus;
    private String ffNbComptesRenouvelables;
    private String ffTauxDutilisationREV12m;
    private String ffVariationDette12;
    private String topDetentionKangourou;
    private String topDetentionRevNaff;
    private String dateMajAdresse;
    private String paysEmployeur;
    private String ffDateDernierAchat;
    private String ffDateDernierImpaye;
    private String type;
    private String nomGrille;
    private String note;
    private String domaine;
    private String refusabiliteBaleII;
    private String refusabilite;
    private String segment;
    private String domiciliationsBancaires;
    private String ficheFICP;
    private String indicateurFauxClient;
    private String regroupementClient;
    private String Charges;
    private String dateFinValiditePieceIdentite;
    private String presenceHomonyme;
    private String topRepresentantDesSARL;
    private String codeIdentificationDeclinaison;
    private String societesCommerciales;
    private String nbContratClient;
    private String dateFinContrat;
    private String codePaysResidence;
    private String montantPensionDu;

    public String getIdMen() {
        return idMen;
    }

    public void setIdMen(String idMen) {
        this.idMen = idMen;
    }

    public String getAutresRevenus() {
        return autresRevenus;
    }

    public void setAutresRevenus(String autresRevenus) {
        this.autresRevenus = autresRevenus;
    }

    public String getAllocationsDeLaPersonne() {
        return allocationsDeLaPersonne;
    }

    public void setAllocationsDeLaPersonne(String allocationsDeLaPersonne) {
        this.allocationsDeLaPersonne = allocationsDeLaPersonne;
    }

    public String getAutresCharges() {
        return autresCharges;
    }

    public void setAutresCharges(String autresCharges) {
        this.autresCharges = autresCharges;
    }

    public String getCanalDeContact() {
        return canalDeContact;
    }

    public void setCanalDeContact(String canalDeContact) {
        this.canalDeContact = canalDeContact;
    }

    public String getChargesHabitation() {
        return chargesHabitation;
    }

    public void setChargesHabitation(String chargesHabitation) {
        this.chargesHabitation = chargesHabitation;
    }

    public String getCodeCsp() {
        return codeCsp;
    }

    public void setCodeCsp(String codeCsp) {
        this.codeCsp = codeCsp;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getCodeRegroupement() {
        return codeRegroupement;
    }

    public void setCodeRegroupement(String codeRegroupement) {
        this.codeRegroupement = codeRegroupement;
    }

    public String getCodeValiditeEmail() {
        return codeValiditeEmail;
    }

    public void setCodeValiditeEmail(String codeValiditeEmail) {
        this.codeValiditeEmail = codeValiditeEmail;
    }

    public String getCodeValiditeTelephoneFixe() {
        return codeValiditeTelephoneFixe;
    }

    public void setCodeValiditeTelephoneFixe(String codeValiditeTelephoneFixe) {
        this.codeValiditeTelephoneFixe = codeValiditeTelephoneFixe;
    }

    public String getCodeValiditeTelephonePortable() {
        return codeValiditeTelephonePortable;
    }

    public void setCodeValiditeTelephonePortable(String codeValiditeTelephonePortable) {
        this.codeValiditeTelephonePortable = codeValiditeTelephonePortable;
    }

    public String getConjoint() {
        return conjoint;
    }

    public void setConjoint(String conjoint) {
        this.conjoint = conjoint;
    }

    public String getCreditExterne() {
        return creditExterne;
    }

    public void setCreditExterne(String creditExterne) {
        this.creditExterne = creditExterne;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateDeCreation() {
        return dateDeCreation;
    }

    public void setDateDeCreation(String dateDeCreation) {
        this.dateDeCreation = dateDeCreation;
    }

    public String getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(String dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public String getDateEmmenagement() {
        return dateEmmenagement;
    }

    public void setDateEmmenagement(String dateEmmenagement) {
        this.dateEmmenagement = dateEmmenagement;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public String getDateMajHabitat() {
        return dateMajHabitat;
    }

    public void setDateMajHabitat(String dateMajHabitat) {
        this.dateMajHabitat = dateMajHabitat;
    }

    public String getDateMiseAJourTelephonePortable() {
        return dateMiseAJourTelephonePortable;
    }

    public void setDateMiseAJourTelephonePortable(String dateMiseAJourTelephonePortable) {
        this.dateMiseAJourTelephonePortable = dateMiseAJourTelephonePortable;
    }

    public String getDateMajRevenus() {
        return dateMajRevenus;
    }

    public void setDateMajRevenus(String dateMajRevenus) {
        this.dateMajRevenus = dateMajRevenus;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getLoyer() {
        return loyer;
    }

    public void setLoyer(String loyer) {
        this.loyer = loyer;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getNbEnfantsACharge() {
        return nbEnfantsACharge;
    }

    public void setNbEnfantsACharge(String nbEnfantsACharge) {
        this.nbEnfantsACharge = nbEnfantsACharge;
    }

    public String getNumeroClient() {
        return numeroClient;
    }

    public void setNumeroClient(String numeroClient) {
        this.numeroClient = numeroClient;
    }

    public String getPaysDeNationalite() {
        return paysDeNationalite;
    }

    public void setPaysDeNationalite(String paysDeNationalite) {
        this.paysDeNationalite = paysDeNationalite;
    }

    public String getPensionRecus() {
        return pensionRecus;
    }

    public void setPensionRecus(String pensionRecus) {
        this.pensionRecus = pensionRecus;
    }

    public String getQualite() {
        return qualite;
    }

    public void setQualite(String qualite) {
        this.qualite = qualite;
    }

    public String getRegimeJuridique() {
        return regimeJuridique;
    }

    public void setRegimeJuridique(String regimeJuridique) {
        this.regimeJuridique = regimeJuridique;
    }

    public String getRevenuPrincipal() {
        return revenuPrincipal;
    }

    public void setRevenuPrincipal(String revenuPrincipal) {
        this.revenuPrincipal = revenuPrincipal;
    }

    public String getSinistreEnCours() {
        return sinistreEnCours;
    }

    public void setSinistreEnCours(String sinistreEnCours) {
        this.sinistreEnCours = sinistreEnCours;
    }

    public String getSituationFamiliale() {
        return situationFamiliale;
    }

    public void setSituationFamiliale(String situationFamiliale) {
        this.situationFamiliale = situationFamiliale;
    }

    public String getSituationLegale() {
        return situationLegale;
    }

    public void setSituationLegale(String situationLegale) {
        this.situationLegale = situationLegale;
    }

    public String getTopMembreDuPersonnel() {
        return topMembreDuPersonnel;
    }

    public void setTopMembreDuPersonnel(String topMembreDuPersonnel) {
        this.topMembreDuPersonnel = topMembreDuPersonnel;
    }

    public String getTopRefusabilite() {
        return topRefusabilite;
    }

    public void setTopRefusabilite(String topRefusabilite) {
        this.topRefusabilite = topRefusabilite;
    }

    public String getTopValiditeAdresse() {
        return topValiditeAdresse;
    }

    public void setTopValiditeAdresse(String topValiditeAdresse) {
        this.topValiditeAdresse = topValiditeAdresse;
    }

    public String getTypeHabitat() {
        return typeHabitat;
    }

    public void setTypeHabitat(String typeHabitat) {
        this.typeHabitat = typeHabitat;
    }

    public String getTypePieceIdentite() {
        return typePieceIdentite;
    }

    public void setTypePieceIdentite(String typePieceIdentite) {
        this.typePieceIdentite = typePieceIdentite;
    }

    public String getTypologieClientCredit() {
        return typologieClientCredit;
    }

    public void setTypologieClientCredit(String typologieClientCredit) {
        this.typologieClientCredit = typologieClientCredit;
    }

    public String getDateDerniereUtilisationRevolving() {
        return dateDerniereUtilisationRevolving;
    }

    public void setDateDerniereUtilisationRevolving(String dateDerniereUtilisationRevolving) {
        this.dateDerniereUtilisationRevolving = dateDerniereUtilisationRevolving;
    }

    public String getDatePlusAncienneSouscriptionPB() {
        return datePlusAncienneSouscriptionPB;
    }

    public void setDatePlusAncienneSouscriptionPB(String datePlusAncienneSouscriptionPB) {
        this.datePlusAncienneSouscriptionPB = datePlusAncienneSouscriptionPB;
    }

    public String getDatePlusAncienneSouscriptionRev() {
        return datePlusAncienneSouscriptionRev;
    }

    public void setDatePlusAncienneSouscriptionRev(String datePlusAncienneSouscriptionRev) {
        this.datePlusAncienneSouscriptionRev = datePlusAncienneSouscriptionRev;
    }

    public String getSofCapitalRestant_du_PB() {
        return sofCapitalRestant_du_PB;
    }

    public void setSofCapitalRestant_du_PB(String sofCapitalRestant_du_PB) {
        this.sofCapitalRestant_du_PB = sofCapitalRestant_du_PB;
    }

    public String getSofCapitalRestant_du_REV() {
        return sofCapitalRestant_du_REV;
    }

    public void setSofCapitalRestant_du_REV(String sofCapitalRestant_du_REV) {
        this.sofCapitalRestant_du_REV = sofCapitalRestant_du_REV;
    }

    public String getSOFMontant_Moyen_REV_12m() {
        return SOFMontant_Moyen_REV_12m;
    }

    public void setSOFMontant_Moyen_REV_12m(String SOFMontant_Moyen_REV_12m) {
        this.SOFMontant_Moyen_REV_12m = SOFMontant_Moyen_REV_12m;
    }

    public String getSOFMax_Nombre_Impayes() {
        return SOFMax_Nombre_Impayes;
    }

    public void setSOFMax_Nombre_Impayes(String SOFMax_Nombre_Impayes) {
        this.SOFMax_Nombre_Impayes = SOFMax_Nombre_Impayes;
    }

    public String getSOFTauxUtilisation() {
        return SOFTauxUtilisation;
    }

    public void setSOFTauxUtilisation(String SOFTauxUtilisation) {
        this.SOFTauxUtilisation = SOFTauxUtilisation;
    }

    public String getTopClientActifEvry() {
        return topClientActifEvry;
    }

    public void setTopClientActifEvry(String topClientActifEvry) {
        this.topClientActifEvry = topClientActifEvry;
    }

    public String getTopClientActifRBX() {
        return topClientActifRBX;
    }

    public void setTopClientActifRBX(String topClientActifRBX) {
        this.topClientActifRBX = topClientActifRBX;
    }

    public String getFfMaxNotePaiement() {
        return ffMaxNotePaiement;
    }

    public void setFfMaxNotePaiement(String ffMaxNotePaiement) {
        this.ffMaxNotePaiement = ffMaxNotePaiement;
    }

    public String getFfMinResultanteREV() {
        return ffMinResultanteREV;
    }

    public void setFfMinResultanteREV(String ffMinResultanteREV) {
        this.ffMinResultanteREV = ffMinResultanteREV;
    }

    public String getFfNbComptesAmortissables() {
        return ffNbComptesAmortissables;
    }

    public void setFfNbComptesAmortissables(String ffNbComptesAmortissables) {
        this.ffNbComptesAmortissables = ffNbComptesAmortissables;
    }

    public String getFfNbComptesDetenus() {
        return ffNbComptesDetenus;
    }

    public void setFfNbComptesDetenus(String ffNbComptesDetenus) {
        this.ffNbComptesDetenus = ffNbComptesDetenus;
    }

    public String getFfNbComptesRenouvelables() {
        return ffNbComptesRenouvelables;
    }

    public void setFfNbComptesRenouvelables(String ffNbComptesRenouvelables) {
        this.ffNbComptesRenouvelables = ffNbComptesRenouvelables;
    }

    public String getFfTauxDutilisationREV12m() {
        return ffTauxDutilisationREV12m;
    }

    public void setFfTauxDutilisationREV12m(String ffTauxDutilisationREV12m) {
        this.ffTauxDutilisationREV12m = ffTauxDutilisationREV12m;
    }

    public String getFfVariationDette12() {
        return ffVariationDette12;
    }

    public void setFfVariationDette12(String ffVariationDette12) {
        this.ffVariationDette12 = ffVariationDette12;
    }

    public String getTopDetentionKangourou() {
        return topDetentionKangourou;
    }

    public void setTopDetentionKangourou(String topDetentionKangourou) {
        this.topDetentionKangourou = topDetentionKangourou;
    }

    public String getTopDetentionRevNaff() {
        return topDetentionRevNaff;
    }

    public void setTopDetentionRevNaff(String topDetentionRevNaff) {
        this.topDetentionRevNaff = topDetentionRevNaff;
    }

    public String getDateMajAdresse() {
        return dateMajAdresse;
    }

    public void setDateMajAdresse(String dateMajAdresse) {
        this.dateMajAdresse = dateMajAdresse;
    }

    public String getPaysEmployeur() {
        return paysEmployeur;
    }

    public void setPaysEmployeur(String paysEmployeur) {
        this.paysEmployeur = paysEmployeur;
    }

    public String getFfDateDernierAchat() {
        return ffDateDernierAchat;
    }

    public void setFfDateDernierAchat(String ffDateDernierAchat) {
        this.ffDateDernierAchat = ffDateDernierAchat;
    }

    public String getFfDateDernierImpaye() {
        return ffDateDernierImpaye;
    }

    public void setFfDateDernierImpaye(String ffDateDernierImpaye) {
        this.ffDateDernierImpaye = ffDateDernierImpaye;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNomGrille() {
        return nomGrille;
    }

    public void setNomGrille(String nomGrille) {
        this.nomGrille = nomGrille;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public String getRefusabiliteBaleII() {
        return refusabiliteBaleII;
    }

    public void setRefusabiliteBaleII(String refusabiliteBaleII) {
        this.refusabiliteBaleII = refusabiliteBaleII;
    }

    public String getRefusabilite() {
        return refusabilite;
    }

    public void setRefusabilite(String refusabilite) {
        this.refusabilite = refusabilite;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getDomiciliationsBancaires() {
        return domiciliationsBancaires;
    }

    public void setDomiciliationsBancaires(String domiciliationsBancaires) {
        this.domiciliationsBancaires = domiciliationsBancaires;
    }

    public String getFicheFICP() {
        return ficheFICP;
    }

    public void setFicheFICP(String ficheFICP) {
        this.ficheFICP = ficheFICP;
    }

    public String getIndicateurFauxClient() {
        return indicateurFauxClient;
    }

    public void setIndicateurFauxClient(String indicateurFauxClient) {
        this.indicateurFauxClient = indicateurFauxClient;
    }

    public String getRegroupementClient() {
        return regroupementClient;
    }

    public void setRegroupementClient(String regroupementClient) {
        this.regroupementClient = regroupementClient;
    }

    public String getCharges() {
        return Charges;
    }

    public void setCharges(String charges) {
        Charges = charges;
    }

    public String getDateFinValiditePieceIdentite() {
        return dateFinValiditePieceIdentite;
    }

    public void setDateFinValiditePieceIdentite(String dateFinValiditePieceIdentite) {
        this.dateFinValiditePieceIdentite = dateFinValiditePieceIdentite;
    }

    public String getPresenceHomonyme() {
        return presenceHomonyme;
    }

    public void setPresenceHomonyme(String presenceHomonyme) {
        this.presenceHomonyme = presenceHomonyme;
    }

    public String getTopRepresentantDesSARL() {
        return topRepresentantDesSARL;
    }

    public void setTopRepresentantDesSARL(String topRepresentantDesSARL) {
        this.topRepresentantDesSARL = topRepresentantDesSARL;
    }

    public String getCodeIdentificationDeclinaison() {
        return codeIdentificationDeclinaison;
    }

    public void setCodeIdentificationDeclinaison(String codeIdentificationDeclinaison) {
        this.codeIdentificationDeclinaison = codeIdentificationDeclinaison;
    }

    public String getSocietesCommerciales() {
        return societesCommerciales;
    }

    public void setSocietesCommerciales(String societesCommerciales) {
        this.societesCommerciales = societesCommerciales;
    }

    public String getNbContratClient() {
        return nbContratClient;
    }

    public void setNbContratClient(String nbContratClient) {
        this.nbContratClient = nbContratClient;
    }

    public String getDateFinContrat() {
        return dateFinContrat;
    }

    public void setDateFinContrat(String dateFinContrat) {
        this.dateFinContrat = dateFinContrat;
    }

    public String getCodePaysResidence() {
        return codePaysResidence;
    }

    public void setCodePaysResidence(String codePaysResidence) {
        this.codePaysResidence = codePaysResidence;
    }

    public String getMontantPensionDu() {
        return montantPensionDu;
    }

    public void setMontantPensionDu(String montantPensionDu) {
        this.montantPensionDu = montantPensionDu;
    }

    public String toCsv() {
        return (idMen != null ? idMen : "") + ";" +
                (autresRevenus != null ? autresRevenus : "") + ";" +
                (allocationsDeLaPersonne != null ? allocationsDeLaPersonne : "") + ";" +
                (autresCharges != null ? autresCharges : "") + ";" +
                (canalDeContact != null ? canalDeContact : "") + ";" +
                (chargesHabitation != null ? chargesHabitation : "") + ";" +
                (codeCsp != null ? codeCsp : "") + ";" +
                (codePostal != null ? codePostal : "") + ";" +
                (codeRegroupement != null ? codeRegroupement : "") + ";" +
                (codeValiditeEmail != null ? codeValiditeEmail : "") + ";" +
                (codeValiditeTelephoneFixe != null ? codeValiditeTelephoneFixe : "") + ";" +
                (codeValiditeTelephonePortable != null ? codeValiditeTelephonePortable : "") + ";" +
                (conjoint != null ? conjoint : "") + ";" +
                (creditExterne != null ? creditExterne : "") + ";" +
                (dateDebut != null ? dateDebut : "") + ";" +
                (dateDeCreation != null ? dateDeCreation : "") + ";" +
                (dateEmbauche != null ? dateEmbauche : "") + ";" +
                (dateEmmenagement != null ? dateEmmenagement : "") + ";" +
                (dateFin != null ? dateFin : "") + ";" +
                (dateMajHabitat != null ? dateMajHabitat : "") + ";" +
                (dateMiseAJourTelephonePortable != null ? dateMiseAJourTelephonePortable : "") + ";" +
                (dateMajRevenus != null ? dateMajRevenus : "") + ";" +
                (dateNaissance != null ? dateNaissance : "") + ";" +
                (loyer != null ? loyer : "") + ";" +
                (motif != null ? motif : "") + ";" +
                (nbEnfantsACharge != null ? nbEnfantsACharge : "") + ";" +
                (numeroClient != null ? numeroClient : "") + ";" +
                (paysDeNationalite != null ? paysDeNationalite : "") + ";" +
                (pensionRecus != null ? pensionRecus : "") + ";" +
                (qualite != null ? qualite : "") + ";" +
                (regimeJuridique != null ? regimeJuridique : "") + ";" +
                (revenuPrincipal != null ? revenuPrincipal : "") + ";" +
                (sinistreEnCours != null ? sinistreEnCours : "") + ";" +
                (situationFamiliale != null ? situationFamiliale : "") + ";" +
                (situationLegale != null ? situationLegale : "") + ";" +
                (topMembreDuPersonnel != null ? topMembreDuPersonnel : "") + ";" +
                (topRefusabilite != null ? topRefusabilite : "") + ";" +
                (topValiditeAdresse != null ? topValiditeAdresse : "") + ";" +
                (typeHabitat != null ? typeHabitat : "") + ";" +
                (typePieceIdentite != null ? typePieceIdentite : "") + ";" +
                (typologieClientCredit != null ? typologieClientCredit : "") + ";" +
                (dateDerniereUtilisationRevolving != null ? dateDerniereUtilisationRevolving : "") + ";" +
                (datePlusAncienneSouscriptionPB != null ? datePlusAncienneSouscriptionPB : "") + ";" +
                (datePlusAncienneSouscriptionRev != null ? datePlusAncienneSouscriptionRev : "") + ";" +
                (sofCapitalRestant_du_PB != null ? sofCapitalRestant_du_PB : "") + ";" +
                (sofCapitalRestant_du_REV != null ? sofCapitalRestant_du_REV : "") + ";" +
                (SOFMontant_Moyen_REV_12m != null ? SOFMontant_Moyen_REV_12m : "") + ";" +
                (SOFMax_Nombre_Impayes != null ? SOFMax_Nombre_Impayes : "") + ";" +
                (SOFTauxUtilisation != null ? SOFTauxUtilisation : "") + ";" +
                (topClientActifEvry != null ? topClientActifEvry : "") + ";" +
                (topClientActifRBX != null ? topClientActifRBX : "") + ";" +
                (ffMaxNotePaiement != null ? ffMaxNotePaiement : "") + ";" +
                (ffMinResultanteREV != null ? ffMinResultanteREV : "") + ";" +
                (ffNbComptesAmortissables != null ? ffNbComptesAmortissables : "") + ";" +
                (ffNbComptesDetenus != null ? ffNbComptesDetenus : "") + ";" +
                (ffNbComptesRenouvelables != null ? ffNbComptesRenouvelables : "") + ";" +
                (ffTauxDutilisationREV12m != null ? ffTauxDutilisationREV12m : "") + ";" +
                (ffVariationDette12 != null ? ffVariationDette12 : "") + ";" +
                (topDetentionKangourou != null ? topDetentionKangourou : "") + ";" +
                (topDetentionRevNaff != null ? topDetentionRevNaff : "") + ";" +
                (dateMajAdresse != null ? dateMajAdresse : "") + ";" +
                (paysEmployeur != null ? paysEmployeur : "") + ";" +
                (ffDateDernierAchat != null ? ffDateDernierAchat : "") + ";" +
                (ffDateDernierImpaye != null ? ffDateDernierImpaye : "") + ";" +
                (type != null ? type : "") + ";" +
                (nomGrille != null ? nomGrille : "") + ";" +
                (note != null ? note : "") + ";" +
                (domaine != null ? domaine : "") + ";" +
                (refusabiliteBaleII != null ? refusabiliteBaleII : "") + ";" +
                (refusabilite != null ? refusabilite : "") + ";" +
                (segment != null ? segment : "") + ";" +
                (domiciliationsBancaires != null ? domiciliationsBancaires : "") + ";" +
                (ficheFICP != null ? ficheFICP : "") + ";" +
                (indicateurFauxClient != null ? indicateurFauxClient : "") + ";" +
                (regroupementClient != null ? regroupementClient : "") + ";" +
                (Charges != null ? Charges : "") + ";" +
                (dateFinValiditePieceIdentite != null ? dateFinValiditePieceIdentite : "") + ";" +
                (presenceHomonyme != null ? presenceHomonyme : "") + ";" +
                (topRepresentantDesSARL != null ? topRepresentantDesSARL : "") + ";" +
                (codeIdentificationDeclinaison != null ? codeIdentificationDeclinaison : "") + ";" +
                (societesCommerciales != null ? societesCommerciales : "") + ";" +
                (nbContratClient != null ? nbContratClient : "") + ";" +
                (dateFinContrat != null ? dateFinContrat : "") + ";" +
                (codePaysResidence != null ? codePaysResidence : "") + ";" +
                (montantPensionDu != null ? montantPensionDu : "");
    }
}
