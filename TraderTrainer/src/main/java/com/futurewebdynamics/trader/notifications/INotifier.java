package com.futurewebdynamics.trader.notifications;

/**
 * Created by Charlie on 02/11/2017.
 */
public interface INotifier {

    boolean SendNotification(String subject, String message);
}
