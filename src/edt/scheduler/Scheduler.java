package edt.scheduler;

import edt.activity.Activity;
import edt.constraints.Constraint;
import edt.constraints.PrecedenceConstraint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Plannifie un emploi du temps en prenant en compte plusieurs contraintes de précedence
*/
public class Scheduler {

	/**
	* Compte le nombre de prédécesseurs de chaque activité selon les contraintes fournies en paramètre
	*
	* @param contraintes Liste des contraintes de précédence
	* @return Retourne une HashMap comprenant une activité et le nombre de prédécesseurs qu'elle a
	*/
	private HashMap<Activity, Integer> initNbPreds(List<PrecedenceConstraint> contraintes) {
		HashMap<Activity, Integer> nbrPredecesseurs = new HashMap<>();

		for(PrecedenceConstraint precedConstraint : contraintes) {
			// La première activité n'est pas dans la map, on l'ajoute
			if(!nbrPredecesseurs.containsKey(precedConstraint.getFirstActivity())) {
				nbrPredecesseurs.put(precedConstraint.getFirstActivity(), 0);
			}

			// La deuxième activité est déjà dans la liste, on augmente d'un son nombre de prédécesseurs
			if(nbrPredecesseurs.containsKey(precedConstraint.getSecondActivity())) {
				Integer valeur = nbrPredecesseurs.get(precedConstraint.getSecondActivity());
				nbrPredecesseurs.put(precedConstraint.getSecondActivity(), valeur+1);
			} else {
				// Sinon si l'activité n'y st pas, on l'ajoute en spécifiant qu'elle à 1 prédécesseur
				nbrPredecesseurs.put(precedConstraint.getSecondActivity(), 1);
			}
		}

		return nbrPredecesseurs;
	}


	/**
	* Plannifie une activité dans l'emploi du temps
	*
	* @param act L'activité à ajouter dans l'emploi du temps
	* @param heure L'heure à laquelle on la plannifie
	* @param contraintes Liste des contraintes de précédence
	* @param edt Emploi du temps où les activités sont ajoutées
	* @param nbrPredecesseurs HashMap comprenant une activité et le nombre de prédécesseurs qu'elle a
	*/
	private void scheduleActivity(Activity act, int heure, List<PrecedenceConstraint> contraintes, HashMap<Activity, Integer> edt, HashMap<Activity, Integer> nbrPredecesseurs) {
		// On ajoute l'activité sur lemploi du temps
		edt.put(act, heure);

		for(PrecedenceConstraint precedConstraint : contraintes) {
			// Si l'activité plannifé est prédécesseur dans la contrainte actuelle, on enlève 1 au nombre de prédecesseurs
			if(precedConstraint.getFirstActivity() == act) {
				Integer valeur = nbrPredecesseurs.get(precedConstraint.getSecondActivity());
				nbrPredecesseurs.put(precedConstraint.getSecondActivity(), valeur-1);
			}
		}

		// On enlève l'activité plannifié pour ne pas la replannifier à l'infini
		nbrPredecesseurs.remove(act);
	}


	/**
	* Génère l'emploi du temps en fonction des contraintes
	* @param contraintes Liste des contraintes de précédence
	* @return Retourne l'emploi du temps complet si il est possible, null sinon
	*/
	public HashMap<Activity, Integer> computeSchedule(List<PrecedenceConstraint> contraintes) {
		HashMap<Activity, Integer> nbrPredecesseurs = initNbPreds(contraintes);
		HashMap<Activity, Integer> edt = new HashMap<Activity, Integer>();
		int heure = 0;

		while(nbrPredecesseurs.size() > 0) {
			Activity actZero = null;

			for(Map.Entry<Activity, Integer> entry : nbrPredecesseurs.entrySet()) {
				// Si l'activité a zéro prédécesseur on la sélectionne
				if(entry.getValue() == 0) {
					actZero = entry.getKey();
					// Stop la boucle si il trouve une activité à 0 prédécesseur
					break;
				}
			}

			// Si on a aucune activité avec 0 prédécesseur, on arrête car aucun plan n'est possible
			if(actZero == null) {
				return null;
			}

			// Sinon on la plannifie
			scheduleActivity(actZero, heure, contraintes, edt, nbrPredecesseurs);
			heure += actZero.getDuree();
		}

		return edt;
	}
}
