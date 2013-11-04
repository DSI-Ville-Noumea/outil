package nc.mairie.outils.distiller;

import java.util.Enumeration;
import java.util.Hashtable;

import nc.mairie.technique.Services;
/**
 * Insérez la description du type ici.
 * Date de création : (08/04/2004 12:59:27)
 * @author: Administrator
 */
public class ImageDistillerBean {
	private String [] LB_DOSSIER = null;
	private String [] LB_DOSSIER_CLE = null;
	private String messageErreur = null;
	private String repOrg = null;
	private String repDest = null;
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 15:37:49)
 * @return java.lang.String
 */
public java.lang.String getEtat() {
	return ImageDistiller.getInstance().estArrete() ? "arrêté" : "démarré";
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 13:00:18)
 * @return java.lang.String[]
 */
public java.lang.String[] getLB_DOSSIER() throws Exception {

	Hashtable<String, DossierDistiller> h = ImageDistiller.getInstance().lireDossier();
	LB_DOSSIER = new String [h.size()];
	LB_DOSSIER_CLE = new String [h.size()];
	Enumeration<DossierDistiller> enume = h.elements();
	int i=0;
	while (enume.hasMoreElements()) {
		DossierDistiller d = (DossierDistiller)enume.nextElement();
		LB_DOSSIER[i] = Services.rpad(d.getDossierOrg().getAbsolutePath(), 25, " ") + Services.rpad(d.getDossierDest().getAbsolutePath(), 25, " ");
		LB_DOSSIER_CLE[i++] = d.getDossierOrg().getAbsolutePath();
	}
	return LB_DOSSIER;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 13:00:18)
 * @return java.lang.String[]
 */
public java.lang.String[] getLB_DOSSIER_CLE() throws Exception {
	return LB_DOSSIER_CLE;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 13:00:18)
 * @return java.lang.String[]
 */
public java.lang.String getLB_INDICE(int i) throws Exception {
	return ""+i;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 15:00:12)
 * @return java.lang.String
 */
public java.lang.String getMessageErreur() {
	if (messageErreur == null) {
		messageErreur = "";
	}
	return messageErreur;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 15:37:49)
 * @return java.lang.String
 */
public java.lang.String getRepDest() {
	return repDest;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 15:37:49)
 * @return java.lang.String
 */
public java.lang.String getRepOrg() {
	return repOrg;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 15:00:12)
 * @param newMessageErreur java.lang.String
 */
public void setMessageErreur(java.lang.String newMessageErreur) {
	messageErreur = newMessageErreur;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 15:37:49)
 * @param newRepDest java.lang.String
 */
public void setRepDest(java.lang.String newRepDest) {
	repDest = newRepDest;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 15:37:49)
 * @param newRepOrg java.lang.String
 */
public void setRepOrg(java.lang.String newRepOrg) {
	repOrg = newRepOrg;
}
}
