package nc.mairie.outils.distiller;

import java.io.*;
import nc.mairie.technique.ConvertImageToPDF;
/**
 * Insérez la description du type ici.
 * Date de création : (08/04/2004 10:08:25)
 * @author: Administrator
 */
public class ImageDistiller extends Thread {
	private static  String nomFichier = "dossiers.dat";
	
	private int delay;
	private static ImageDistiller instance;
	private boolean estArrete = false;
	private boolean vivant = true;

	private java.util.Vector filesConvertError;
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:17:15)
 */
public ImageDistiller(int aDelay) {
	super();
	delay=aDelay*1000;
	if (instance == null) {
		instance=this;
	}
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:41:14)
 * @param ord java.lang.String
 * @param dest java.lang.String
 */
public void ajouteDossier(String ord, String dest) throws Exception {
	java.util.Hashtable list = lireDossier();

	DossierDistiller d = new DossierDistiller(ord, dest);
	list.put(d.getDossierOrg().getAbsolutePath(), d);

	ecrireDossier(list);
	
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:16:43)
 */
public void arreter() {
	estArrete=true;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:16:43)
 */
public void demarrer() {
	estArrete=false;
}
/**
 * Destroys this thread, without any cleanup. Any monitors it has 
 * locked remain locked. (This method is not implemented.)
 */
public void destroy() {
	vivant = false;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:41:14)
 * @param ord java.lang.String
 * @param dest java.lang.String
 */
private void ecrireDossier(java.util.Hashtable list) throws Exception{
	
	FileOutputStream fout = new FileOutputStream(nomFichier);
	ObjectOutputStream oos = new ObjectOutputStream(fout);
	oos.writeObject(list);
	oos.close();
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:41:14)
 * @param ord java.lang.String
 * @param dest java.lang.String
 */
public void enleveDossier(String ord) throws Exception {
	java.util.Hashtable list = lireDossier();

	list.remove(ord);

	ecrireDossier(list);
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:16:43)
 */
public boolean estArrete() {
	return estArrete;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (23/08/2004 11:19:09)
 * @return java.util.Vector
 */
private java.util.Vector getFilesConvertError() {
	if (filesConvertError == null) {
		filesConvertError = new java.util.Vector();
	}
	return filesConvertError;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:09:31)
 * @return nc.mairie.outils.ImageDistiller
 */
public static synchronized ImageDistiller getInstance() {
	if (instance == null) {
		//instance de 60 sec par défaut
		instance = new ImageDistiller(60);
	}
	return instance;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:41:14)
 * @param ord java.lang.String
 * @param dest java.lang.String
 */
public java.util.Hashtable lireDossier() throws Exception {
	FileInputStream fin = null;
	try {
		fin= new FileInputStream(nomFichier);
	} catch (Exception e) {
		return new java.util.Hashtable();
	}
	
	ObjectInputStream ois = new ObjectInputStream(fin);
	java.util.Hashtable result = (java.util.Hashtable) ois.readObject();
	ois.close();

	if (result == null)
		result = new java.util.Hashtable();
	
	return result;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:16:43)
 */
public void run() {
	while (vivant) {
		synchronized (this) {
			try {
				sleep(delay);
			} catch (Exception sleep) {
				sleep.printStackTrace();
			}
			if (! estArrete) {
				try {
					scanneDossiers();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	System.out.println("Sortie du Distiller");
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:23:08)
 */
private synchronized void scanneDossiers() throws Exception{

	//Rajout gestion fichiers en erreur de conversion
	java.util.Vector newFileConvertError = new java.util.Vector();

	//recup de la liste des dossiers
	java.util.Hashtable list = lireDossier();
	java.util.Enumeration enume = list.elements();
	while (enume.hasMoreElements()) {
		DossierDistiller d = (DossierDistiller)enume.nextElement();

		//Si dossier innaccessible
		if (! d.getDossierOrg().exists()) {
			System.out.println("ImageDistiller : le dossier d'origine "+d.getDossierOrg()+" est inexistant ou innaccessible");
			continue;
		}

		//Si dossier innaccessible
		if (! d.getDossierDest().exists()) {
			System.out.println("ImageDistiller : le dossier de destination "+d.getDossierDest()+" est inexistant ou innaccessible");
			continue;
		}
		
		File [] lesFile = d.getDossierOrg().listFiles();

		//init dyu compteur de temps
		long deb = System.currentTimeMillis();
		int nbFiles = 0;

		//parcours de chaque file
		for (int i = 0; i < lesFile.length; i++){

			//recup du fichier org
			File fichierOrg = lesFile[i];

			//Si c'est un dossier, on ignore
			if (fichierOrg.isDirectory()) {
				continue;
			}

			//Recup du nom de fichier
			String nom = fichierOrg.getName();
			int posPoint = nom.lastIndexOf(".") == -1 ? nom.length() : nom.lastIndexOf(".");

			//on vire l'extension
			nom= nom.substring(0,posPoint);
			
			//On crée le fichier dest
			File fichierDest = new File(d.getDossierDest().getPath()+"/"+nom+".pdf");

			//on convertit
			try {
				ConvertImageToPDF.convertir(fichierOrg, fichierDest);		
			} catch (Exception e) {
				newFileConvertError.add(fichierOrg.getAbsolutePath());
				//Si pas déjà en erreur alors on envoie le message dans la log
				if (! getFilesConvertError().contains(fichierOrg.getAbsolutePath())) {
					System.out.println("ImageDistiller : Impossible de convertir le fichier "+fichierOrg);
				}
				continue;
			}

			//on supprime la source
			try {
				fichierOrg.delete();
			} catch (Exception e) {
				System.out.println("ImageDistiller : Impossible de supprimer le fichier "+fichierOrg.getName());
				continue;
			}

			//incrémentation du nombre de fichiers traités
			nbFiles++;
		}

		//Si des fichiers ont été convertis, on affiche le total et le temps
		if (nbFiles !=0) {
			int diff = (int)(System.currentTimeMillis()-deb)/1000;
			int h = diff / 360;
			int m = (diff - h * 360) / 60;
			int s = diff - h * 360 - m * 60;
			System.out.println("ImageDistiller : Conversion de "+nbFiles+" fichiers dans le dossier "+d.getDossierOrg().getAbsolutePath()+" en "+
				(h>0 ? h+" heures " : "") +
				(h>0 || m > 0 ? m+" minutes ": "")+
				s+" secondes");
		}
	}

	//MAJ des fichiers en erreur de conversion
	setFilesConvertError(newFileConvertError);
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (23/08/2004 11:19:09)
 * @param newFilesConvertError java.util.Vector
 */
private void setFilesConvertError(java.util.Vector newFilesConvertError) {
	filesConvertError = newFilesConvertError;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:41:14)
 * @param ord java.lang.String
 * @param dest java.lang.String
 */
public void videDossier() throws Exception {
	java.util.Hashtable list = new java.util.Hashtable();

	ecrireDossier(list);
	
}
}
