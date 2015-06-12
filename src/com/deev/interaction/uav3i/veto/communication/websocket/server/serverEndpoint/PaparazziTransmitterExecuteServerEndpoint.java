package com.deev.interaction.uav3i.veto.communication.websocket.server.serverEndpoint;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.UAV3iSettings.Mode;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO.ManoeuverRequestedStatus;
import com.deev.interaction.uav3i.veto.ui.Veto;

@ServerEndpoint(value = "/PaparazziTransmitterExecute")
public class PaparazziTransmitterExecuteServerEndpoint
{
  //-----------------------------------------------------------------------------
  @OnMessage
  public void receive(String idManoeuver)
  {
    System.out.println("####### PaparazziTransmitterExecuteServerEndpoint.receive("+idManoeuver+")");
//    int idMnvr = Integer.parseInt(idManoeuver)
//    // Comme les boutons ne sont pas 'Serializable' (et qu'on n'en a rien à
//    // faire côté table), on les ajoute sur la manoeuvre une fois qu'elle
//    // arrive sur le Veto. Comme cette manoeuvre (même si c'est la même) est une
//    // copie, on récupère celle précédemment transmise lors du 'communicateManoeuver'
//    // pour lui ajouter les boutons.
//    // TODO : il n'est pas utile de transmettre l'intance de la manoeuvre, seul son id est suffisant.
//    ManoeuverDTO mDTO = Veto.getSymbolMapVeto().getSharedManoeuver();  
//    if(mDTO != null)
//    {
//      if(mDTO.getId() == idMnvr)
//      {
//        if(UAV3iSettings.getMode() == Mode.VETO)
//        {
//          LoggerUtil.LOG.info("executeManoeuver("+mDTO+") asked");
//          mDTO.addButtons();
//          mDTO.setRequestedStatus(ManoeuverRequestedStatus.ASKED);
//        }
//        else if(UAV3iSettings.getMode() == Mode.VETO_AUTO)
//        {
//          LoggerUtil.LOG.info("executeManoeuver("+mDTO+") automaticaly accepted");
//          // On transmet à la table le résultat de l'évaluation de la manoeuvre
//          // par l'opérateur Paparazzi pour mise à jour de l'affichage.
//          instance.getUav3iTransmitter().resultAskExecution(idMnvr, true);
//          // On met à jour localement le statut de la manoeuvre pour mise
//          // à jour de l'affichage sur le Veto.
//          mDTO.setRequestedStatus(ManoeuverRequestedStatus.ACCEPTED);
//          // On lance l'exécution de la manoeuvre.
//          instance.startManoeuver(mDTO);
//        }
//      }
//    }
//    else
//      LoggerUtil.LOG.severe(("Exection of a manoeuver that is not shared : " + idMnvr));
//
  }
  //-----------------------------------------------------------------------------
}
