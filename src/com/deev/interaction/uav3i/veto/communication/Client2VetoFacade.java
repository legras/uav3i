package com.deev.interaction.uav3i.veto.communication;

import com.deev.interaction.uav3i.ui.Manoeuver;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;

public abstract class Client2VetoFacade
{
  //-----------------------------------------------------------------------------
  /**
   * Communication d'une manoeuvre au responsable du vol pour affichage sur
   * l'interface Veto : <i>méthode utile uniquement dans ce cas</i>.
   * 
   * @param mnvrDTO objet DTO (transfert de données) représentant les données de
   *                la manoeuvre.
   */
  public abstract void communicateManoeuver(ManoeuverDTO mnvrDTO);
  //-----------------------------------------------------------------------------
  /**
   * Exécution de la manoeuvre sur Paparazzi dans le mode PAPARAZZI_REMOTE.
   * 
   * @param idMnvr id de la maneuvre dont on demande l'exécution.
   */
  public abstract void executeManoeuver(int idMnvr);
  //-----------------------------------------------------------------------------
  /**
   * Exécution de la manoeuvre sur Paparazzi dans le mode PAPARAZZI_DIRECT.
   * 
   * @param mnvrDTO objet DTO (transfert de données) représentant les données de
   *                la manoeuvre.
   */
  public abstract void executeManoeuver(Manoeuver mnvr);
  //-----------------------------------------------------------------------------
  /**
   * Demande d'effacement d'une manoeuvre sur l'interface Veto : <i>méthode utile
   * uniquement dans ce cas</i>.
   */
  public abstract void clearManoeuver();
  //-----------------------------------------------------------------------------
}
