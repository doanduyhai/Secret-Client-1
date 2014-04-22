package com.secret.client.csv;

import java.io.IOException;
import java.util.List;
import com.secret.client.model.Client;

public class ClientDataWriter extends CsvDataWriter {

    private static final String CLIENT_HEADER = "IDMEN;autresRevenus;allocationsDeLaPersonne;autresCharges;canalDeContact;chargesHabitation;codeCsp;codePostal;codeRegroupement;codeValiditeEmail;codeValiditeTelephoneFixe;codeValiditeTelephonePortable;conjoint;creditExterne;dateDebut;dateDeCreation;dateEmbauche;dateEmmenagement;dateFin;dateMajHabitat;dateMiseAJourTelephonePortable;dateMajRevenus;dateNaissance;loyer;motif;nbEnfantsACharge;numeroClient;paysDeNationalite;pensionRecus;qualite;regimeJuridique;revenuPrincipal;sinistreEnCours;situationFamiliale;situationLegale;topMembreDuPersonnel;topRefusabilite;topValiditeAdresse;typeHabitat;typePieceIdentite;typologieClientCredit;dateDerniereUtilisationRevolving;datePlusAncienneSouscriptionPB;datePlusAncienneSouscriptionRev;SOF_Capital_restant_du_PB;SOF_Capital_restant_du_REV;SOF_Montant_Moyen_REV_12m;SOF_Max_Nombre_Impayes;SOFTauxUtilisation;top_client_actif_evry;top_client_actif_RBX;ffMaxNotePaiement;ffMinResultanteREV;ffNbComptesAmortissables;ffNbComptesDetenus;ffNbComptesRenouvelables;ffTauxDutilisationREV12m;ffVariationDette12;topDetentionKangourou;topDetentionRevNaff;dateMajAdresse;paysEmployeur;ffDateDernierAchat;ffDateDernierImpaye;type;nomGrille;note;domaine;refusabiliteBaleII;refusabilite;segment;domiciliationsBancaires;ficheFICP;regroupementClient;indicateurFauxClient;Charges;dateFinValiditePieceIdentite;presenceHomonyme;topRepresentantDesSARL;codeIdentificationDeclinaison;societesCommerciales;nb_contrat_client;dateFinContrat;CodePaysResidence;MontantPensionDu";

    public ClientDataWriter(String outputFile) throws IOException {
        super(outputFile);
        writer.writeNext(CLIENT_HEADER.split(";"));
    }

    public void writeClients(List<Client> clients) {
        for (Client client : clients) {
            writer.writeNext(client.toCsv().split(";"));
        }
    }

    public void flush() throws IOException {
        writer.close();
    }
}
