package nc.mairie.metier;

import java.util.Hashtable;

import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
/**
 * Broker de l'Objet métier DistillerEtat
 */
public class DistillerEtatBroker extends nc.mairie.technique.BasicBroker {
/**
 * Constructeur DistillerEtatBroker.
 * @param aMetier aMetier 
 */
public DistillerEtatBroker(BasicMetier aMetier) {
	super(aMetier);
}
/**
 * @return src/main/java/nc.mairie.metier.DistillerEtatMetier
 */
protected BasicMetier definirMyMetier() {
	return new DistillerEtat() ;
}
/**
 * @return src/main/java/nc.mairie.metier.DistillerEtatMetier
 */
protected DistillerEtat getMyDistillerEtat() {
	return (DistillerEtat)getMyBasicMetier();
}
/**
 * Retourne le nom de la table.
 */
protected String definirNomTable() {
	return "MAIRIE.DISTILLER";
}
/**
 * Retourne le mappage de chaque colonne de la table.
 */
protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
	Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
	mappage.put("SERVEURIP", new BasicRecord("SERVEURIP", "CHAR", getMyDistillerEtat().getClass().getField("serveurip"), "String"));
	mappage.put("ETAT", new BasicRecord("ETAT", "CHAR", getMyDistillerEtat().getClass().getField("etat"), "String"));
	mappage.put("QUAND", new BasicRecord("QUAND", "INT", getMyDistillerEtat().getClass().getField("quand"), "String"));
	return mappage;
}
/**
 * Methode creerObjetMetierBroker qui retourne
 * true ou false
 * @param aTransaction aTransaction
 * @return boolean
 * @throws Exception Exception
 */
public boolean creerDistillerEtat(nc.mairie.technique.Transaction aTransaction)  throws Exception{
	return creer(aTransaction);
}
/**
 * Methode modifierObjetMetierBroker qui retourne
 * true ou false
 * @param aTransaction aTransaction
 * @return boolean
 * @throws java.lang.Exception Exception
 */
public boolean modifierDistillerEtat(nc.mairie.technique.Transaction aTransaction) throws java.lang.Exception {
	return modifier(aTransaction);
}
/**
 * Retourne un DistillerEtat.
 * @param aTransaction aTransaction
 * @param cle cle
 * @return DistillerEtat
 * @throws Exception Exception
 */
public DistillerEtat chercherDistillerEtat(nc.mairie.technique.Transaction aTransaction, String cle) throws Exception {
	return (DistillerEtat)executeSelect(aTransaction,"select * from "+getTable()+" where serveurip = '"+cle+"'");
}
/**
 * Retourne un DistillerEtat.
 * @param aTransaction aTransaction
 * @return DistillerEtat
 * @throws Exception Exception
 */
public DistillerEtat chercherLastDistillerEtat(nc.mairie.technique.Transaction aTransaction) throws Exception {
	return (DistillerEtat)executeSelect(aTransaction,"select * from "+getTable()+" where quand = (select max(quand) from "+getTable()+")");
}
}
