package nc.mairie.outils.distiller;

import java.io.File;
/**
 * Ins�rez la description du type ici.
 * Date de cr�ation : (08/04/2004 10:35:10)
 * @author: Administrator
 */
public class DossierDistiller implements java.io.Serializable {
	private File dossierOrg;
	private File dossierDest;

/**
 * Commentaire relatif au constructeur Dossiers.
 */
public DossierDistiller(String org, String dest) {
	super();
	dossierOrg = new File(org);
	dossierDest = new File(dest);
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (08/04/2004 11:16:49)
 * @return java.io.File
 */
public java.io.File getDossierDest() {
	return dossierDest;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (08/04/2004 11:16:49)
 * @return java.io.File
 */
public java.io.File getDossierOrg() {
	return dossierOrg;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (08/04/2004 11:16:49)
 * @param newDossierOrg java.io.File
 */
void setDossierOrg(java.io.File newDossierOrg) {
	dossierOrg = newDossierOrg;
}
}
