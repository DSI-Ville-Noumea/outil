package nc.mairie.outils.distiller;

import java.io.File;
/**
 * Insérez la description du type ici.
 * Date de création : (08/04/2004 10:35:10)
 * @author: Administrator
 */
public class DossierDistiller implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1576921731845181997L;
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
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 11:16:49)
 * @return java.io.File
 */
public java.io.File getDossierDest() {
	return dossierDest;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 11:16:49)
 * @return java.io.File
 */
public java.io.File getDossierOrg() {
	return dossierOrg;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 11:16:49)
 * @param newDossierOrg java.io.File
 */
void setDossierOrg(java.io.File newDossierOrg) {
	dossierOrg = newDossierOrg;
}
}
