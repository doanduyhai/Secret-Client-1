package com.secret.client.csv;

import java.io.IOException;
import com.secret.client.model.Client;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;

public class ClientDataLoader extends CsvDataLoader<Client> {

    public ClientDataLoader(String inputFile) throws IOException {
        super(inputFile);
        // Consume header
        reader.readNext();
    }

    protected DataMapper<Client> configureMapping() throws IOException {
        ColumnPositionMappingStrategy<Client> strategy = new ColumnPositionMappingStrategy<Client>();
        strategy.captureHeader(reader);
        strategy.setType(Client.class);
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
        strategy.setColumnMapping(columns);
        DataMapper<Client> mapper = new DataMapper<Client>(strategy);
        return mapper;
    }


}
