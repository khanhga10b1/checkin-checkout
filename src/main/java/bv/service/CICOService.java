package bv.service;

import bv.domain.ScheduleTask;

import java.util.List;

public interface CICOService {
    void autoCICO(List<ScheduleTask> tasks);

    boolean checkinCheckoutWithToken(String token);

    String preflight(String userName);

    String encodePassword(String secret, String password);

    void checkinCheckoutWithUser(String userName, String password);
}
