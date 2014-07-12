package com.deev.interaction.uav3i.veto.communication;

import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;

import uk.me.jstott.jcoord.LatLng;

public abstract class PaparazziCommunication
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
   * Exécution de la manoeuvre sur Paparazzi.
   * 
   * @param mnvrDTO objet DTO (transfert de données) représentant les données de
   *                la manoeuvre.
   */
  public abstract void executeManoeuver(ManoeuverDTO mnvrDTO);
  //-----------------------------------------------------------------------------
  /**
   * Demande d'effacement d'une manoeuvre sur l'interface Veto : <i>méthode utile
   * uniquement dans ce cas</i>.
   */
  public abstract void clearManoeuver();
  //-----------------------------------------------------------------------------
}
