package com.deev.interaction.uav3i.veto.communication;

import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;

import uk.me.jstott.jcoord.LatLng;

public abstract class PaparazziCommunication
{
  //-----------------------------------------------------------------------------
  /**
   * Mise à jour du rayon (en mètres ?) pour le vol circulaire.
   * @param radius rayon en mètres.
   */
  public abstract void setNavRadius(double radius);
  //-----------------------------------------------------------------------------
  /**
   * Déplacement d'un waypoint défini dans le plan de vol.
   * 
   * @param waypointName nom du waypoint (attribut <code>name</code> de l'élément <code>&lt;waypoint&gt:</code>).
   * @param coordinate nouvelles coordonnées en {@link LatLng} du point.
   */
  public abstract void moveWayPoint(String waypointName, LatLng coordinate);
  //-----------------------------------------------------------------------------
  /**
   * Demande d'exécution d'un template défini dans le plan de vol.
   * 
   * @param blockName nom du template de vol à exécuter.
   */
  public abstract void jumpToBlock(String blockName);
  //-----------------------------------------------------------------------------
  /**
   * Communication d'une manoeuvre au responsable du vol.
   * @param mnvrDTO objet DTO (transfert de données) représentant les données de
   *                la manoeuvre.
   * @return
   */
  public abstract void communicateManoeuver(ManoeuverDTO mnvrDTO);
  //-----------------------------------------------------------------------------
  public abstract void executeManoeuver(ManoeuverDTO mnvrDTO);
  //-----------------------------------------------------------------------------
  public abstract void clearManoeuver();
  //-----------------------------------------------------------------------------
}
