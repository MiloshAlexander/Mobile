package by.it.milosh.service.service;

import by.it.milosh.model.PhoneNumber;
import by.it.milosh.model.Addon;
import by.it.milosh.model.Tariff;
import by.it.milosh.model.User;

import java.util.List;

public interface UserService extends BaseService<User> {

    User findUserByUsername(String username);

    void addTariffToUser(User user, Long tariff_id);

    void addTariffToUser(User user, Tariff tariff);

    void addNumberToUser(User user, PhoneNumber phoneNumber);

    Long numberOfUsers();

    void addAddonToUser(Long userId, Long addonId);

    List<Addon> getAddonsOfUser(Long userId);

    List<Addon> getAddonsNonUser(Long userId);

    void registrationUser(User user);

    boolean isAdmin(User user);

    User connect(User user, Tariff tariff);

    User setBalance(User user, Integer changeBalance);

}
