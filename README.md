# But du projet
Vous devrez réaliser deux applications en client/serveur destinées à gérer une base de données collaboratives sur les radars et autres petites tracasseries routières. 

# Le client
L'application client doit proposer une carte Google Maps sur laquelle l'utilisateur doit pouvoir placer à la souris les choses qu'il rencontre (radar fixe, radar mobile ou travaux; pour plus de clarté, nous appellerons ces choses des binious dans la suite du sujet). Il doit pouvoir transmettre ces informations au serveur. Réciproquement, il doit pouvoir envoyer une position au serveur et obtenir de celui-ci la liste de tous les binious connus dans un rayon de 20 kilomètres autour de la position fournie. L'utilisateur doit pouvoir également indiquer au serveur qu'il n'a pas vu un biniou, par exemple si des travaux se sont terminés. Naturellement, on veut que les binious décrits par le serveur soient joliment indiqués sur la carte avec des icônes. 

# Le serveur
En plus de répondre aux clients, le serveur devra proposer une interface de gestion des binious. Cela comprendra une carte permettant de voir l'intégralité des binious, ainsi qu'une vue sous forme de table montrant toutes les informations remontées par des clients. Ce sera au serveur de fusionner des binious, si plusieurs clients remontent la même information, et ce, en tenant compte des possibles légères différences sur les coordonnées. On veut également savoir par combien de clients une information a été remontée, pour avoir une idée de sa fiabilité. On devra pouvoir trier les informations de toutes les façons possibles, et bien sûr, l'utilisateur de l'application serveur doit pouvoir éditer/ajouter/supprimer à la main tous les binious. On veut également disposer d'une sauvegarde des données pour pouvoir les conserver d'un lancement de l'application serveur à l'autre. 

Les binious seront caractérisés par des coordonnées géographiques ainsi que par la date à laquelle l'information a été remontée. On veut pouvoir avoir un historique des remontées de binious, de façon à pouvoir par exemple avoir une vue globale de quand un radar mobile est présent ou non (le tout dans un but uniquement pédagogique, cela va de soi). A vous de produire une vue la plus ergonomique possible, quelle que soit la plage de temps sur laquelle s'étendent les binious (jours, semaines, mois, ...). 

# Briques fournies
Afin de vous soulager un peu, la partie réseau est déjà presque codée avec [des classes](http://igm.univ-mlv.fr/ens/IR/IR2/2011-2012/Interface_Graphique/src/bipbip.zip) que vous n'aurez plus qu'à adapter un peu. 

Si vous êtes des IR, pour toute la gestion des cartes, vous utiliserez l'API Google Maps. Vous aurez probablement besoin d'adapter légèrement le code de la partie réseau au système de coordonnées manipulé par cette API, où les X et Y ne seront peut-être pas de simple flottants. 

Si vous êtes des géomatiques, vous ferez ce boulot à la main en utilisant les données d'Open Street Map, soit en faisant le tracé des routes à partir des données vectorielles, soit en utilisant un serveur de tuiles (note: vous avez le droit de sauter de joie en hurlant "Youpi!! Pour une fois qu'on pense à nous!!"). 

# Travail demandé
Vous devez fournir deux applications Swing, une pour le client, une pour le serveur. Ces applications devront se conformer aux spécifications décrites précédemment, et être le plus ergonomique possible. 

SI ET SEULEMENT SI le travail demandé est accompli, vous êtes libres d'apporter toutes les fonctionnalités supplémentaires de votre choix, voire de redévelopper le client pour qu'il tourne sous Android avec un vrai GPS :) 

# Conditions de rendu
Vous travaillerez en binômes. En premier lieu, vous relirez avec profit la [charte des projets](http://www-igm.univ-mlv.fr/~paumier/charte_des_projets.html) pour éviter des bavures malheureuses. Vous rendrez ensuite une archive nommée login1-login2.zip contenant les choses suivantes: 
* un répertoire src contenant les sources du projet ainsi qu'un build.xml. Lorsqu'on lance ant, on doit obtenir deux exécutables nommés bipbipclient et bipbipserver. La cible clean doit fonctionner correctement. Les sources doivent être propres, en anglais et commentées. 
* un fichier doc.pdf contenant votre rapport qui devra décrire votre travail. En particulier, vous décrirez avec soin et en les justifiant tous vos choix d'architecture, en particulier sur le MVC. Si votre projet ne fonctionne pas complètement, vous devrez en décrire les bugs. 
* **En dehors des classes fournies pour la partie réseau et de l'API Google Maps, il est interdit d'utiliser du code externe: vous devrez tout coder vous-mêmes.**
* **Tout code commun à plusieurs projets vaudra zéro pour tous les projets concernés.**

Le projet est à rendre par mail à tous les enseignants (i.e. Sébastien Paumier, Michel Chilowitz et Sylvain Cherrier), au plus tard le **lundi 28 mai 2012 à 17h34**.
