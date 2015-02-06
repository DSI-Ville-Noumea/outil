package nc.mairie.metier;

/**
 * Objet métier DistillerEtat
 */
public class DistillerEtat extends nc.mairie.technique.BasicMetier {
	public String serveurip;
	public String etat;
	public String quand;
/**
 * Constructeur DistillerEtat.
 */
public DistillerEtat() {
	super();
}
/**
 * Getter de l'attribut serveurip.
 * @return String
 */
public String getServeurip() {
	return serveurip;
}
/**
 * Setter de l'attribut serveurip.
 * @param newServeurip newServeurip 
 */
public void setServeurip(String newServeurip) { 
	serveurip = newServeurip;
}
/**
 * Getter de l'attribut etat.
 * @return String
 */
public String getEtat() {
	return etat;
}
/**
 * Setter de l'attribut etat.
 * @param  newEtat newEtat 
 */
public void setEtat(String newEtat) { 
	etat = newEtat;
}
/**
 * Getter de l'attribut quand.
 * @return String
 */
public String getQuand() {
	return quand;
}
/**
 * Setter de l'attribut quand.
 * @param newQuand newQuand 
 */
public void setQuand(String newQuand) { 
	quand = newQuand;
}
/**
 Methode à définir dans chaque objet Métier pour instancier un Broker 
*/
protected nc.mairie.technique.BasicBroker definirMyBroker() { 
	return new DistillerEtatBroker(this); 
}
/**
 Methode à définir dans chaque objet Métier pour instancier un Broker 
 * @return DistillerEtatBroker
*/
protected DistillerEtatBroker getMyDistillerEtatBroker() {
	return (DistillerEtatBroker)getMyBasicBroker();
}
/**
* Renvoie une chaîne correspondant à la valeur de cet objet.
* @return une représentation sous forme de chaîne du destinataire
*/
public String toString() {
	// Insérez ici le code pour finaliser le destinataire
	// Cette implémentation transmet le message au super. Vous pouvez remplacer ou compléter le message.
	return super.toString();
}
/**
 * Retourne un DistillerEtat.
 * @param aTransaction  aTransaction
 * @param code code 
 * @return DistillerEtat 
 * @throws Exception Exception 
 */
public static DistillerEtat chercherDistillerEtat(nc.mairie.technique.Transaction aTransaction, String code) throws Exception{
	DistillerEtat unDistillerEtat = new DistillerEtat();
	return unDistillerEtat.getMyDistillerEtatBroker().chercherDistillerEtat(aTransaction, code);
}
/**
 * Methode creerObjetMetier qui retourne
 * true ou false
 * @param aTransaction  aTransaction
 * @return boolean
 * @throws Exception Exception
 */
public boolean creerDistillerEtat(nc.mairie.technique.Transaction aTransaction )  throws Exception {
	//Creation du DistillerEtat
	return getMyDistillerEtatBroker().creerDistillerEtat(aTransaction);
}
/**
 * Methode modifierObjetMetier qui retourne
 * true ou false
 * @param aTransaction  aTransaction
 * @return boolean
 * @throws Exception Exception
 */
public boolean modifierDistillerEtat(nc.mairie.technique.Transaction aTransaction) throws Exception {
	//Modification du DistillerEtat
	return getMyDistillerEtatBroker().modifierDistillerEtat(aTransaction);
}
/**
 * Retourne un DistillerEtat.
 * @param aTransaction  aTransaction
 * @return DistillerEtat 
 * @throws Exception Exception 
 */
public static DistillerEtat chercherLastDistillerEtat(nc.mairie.technique.Transaction aTransaction) throws Exception{
	DistillerEtat unDistillerEtat = new DistillerEtat();
	return unDistillerEtat.getMyDistillerEtatBroker().chercherLastDistillerEtat(aTransaction);
}

}
